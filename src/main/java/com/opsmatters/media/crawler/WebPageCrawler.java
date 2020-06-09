/*
 * Copyright 2019 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opsmatters.media.crawler;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeParseException;
import org.apache.commons.logging.LogFactory;
import com.google.common.net.UrlEscapers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.gargoylesoftware.htmlunit.WebClient;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.LoadingConfiguration;
import com.opsmatters.media.config.content.MoreLinkConfiguration;
import com.opsmatters.media.config.content.SummaryConfiguration;
import com.opsmatters.media.config.content.ContentField;
import com.opsmatters.media.config.content.ContentFields;
import com.opsmatters.media.model.admin.TraceObject;
import com.opsmatters.media.model.content.ContentSummary;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a crawler for content items from a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class WebPageCrawler<T extends ContentSummary> extends FieldsCrawler<T>
{
    private static final Logger logger = Logger.getLogger(WebPageCrawler.class.getName());

    public static final String ANCHOR = "a";
    public static final String DIV = "div";

    public static final String ROOT = "<root>";
    public static final String URL_CONTEXT = "url-context";
    public static final String IMAGE_NAME = "image-name";
    public static final String IMAGE_EXT = "image-ext";

    private CrawlerBrowser browser;
    private WebDriver driver;
    private TraceObject traceObject = TraceObject.NONE;
    private WebPageConfiguration config;

    static
    {
        // Turn off CSS and JavaScript error logging
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    /**
     * Constructor that takes a name.
     */
    public WebPageCrawler(WebPageConfiguration config)
    {
        super(config);
        this.config = config;

        // Create the web driver
        createDriver(config.getBrowser());
    }


    protected void createDriver(CrawlerBrowser browser)
    {
        if(driver == null)
        {
            if(browser == CrawlerBrowser.CHROME)
            {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--no-sandbox");
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); 
                options.addArguments("--disable-extensions");
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                options.addArguments("--window-size=1920,1080");
                options.setHeadless(true);

                driver = new ChromeDriver(options);
                this.browser = browser;
            }
            else // Defaults to HtmlUnit
            {
                driver = new HtmlUnitDriver()
                {
                    @Override
                    protected WebClient modifyWebClient(WebClient webClient)
                    {
                        final WebClient client = super.modifyWebClient(webClient);

                        client.setJavaScriptTimeout(30000);
                        client.getOptions().setTimeout(60000);
                        client.getOptions().setRedirectEnabled(true);
                        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
                        client.getOptions().setThrowExceptionOnScriptError(false);
                        client.getOptions().setCssEnabled(false);
                        client.getOptions().setPrintContentOnFailingStatusCode(false);
                        client.getCookieManager().setCookiesEnabled(false);
                        client.getOptions().setUseInsecureSSL(true);

                        return client;
                    }
                };

                this.browser = CrawlerBrowser.HTMLUNIT;
            }
        }
    }

    /**
     * Close the crawler and release resources.
     */
    public void close()
    {
        if(driver != null)
            driver.quit();
    }

    /**
     * Returns the selenium web driver.
     */
    protected WebDriver getDriver()
    {
        return driver;
    }

    /**
     * Returns <CODE>true</CODE> if trace is enabled for the given object.
     */
    public boolean trace(Object obj)
    {
        boolean ret = false;

        if(!traceObject.isNone())
        {
            if(debug())
                logger.info("Trace enabled: traceObject="+traceObject+" obj="+obj.getClass().getName());
            if(obj instanceof WebDriver)
            {
                ret = traceObject.isPages();
                if(debug())
                    logger.info("Trace pages: obj="+obj.getClass().getName()+" ret="+ret);
            }
            else if(obj instanceof WebElement)
            {
                ret = traceObject.isNodes();
                if(debug())
                    logger.info("Trace nodes: obj="+obj.getClass().getName()+" ret="+ret);
            }
        }
        else
        {
            if(debug())
                logger.info("Trace disabled: traceObject="+traceObject);
        }

        return ret;
    }

    /**
     * Set to <CODE>true</CODE> if trace is enabled for the given object.
     */
    public void setTraceObject(TraceObject traceObject)
    {
        this.traceObject = traceObject;
    }

    /**
     * Returns the web page configuration of the crawler.
     */
    public WebPageConfiguration getPageConfig()
    {
        return config;
    }

    /**
     * Returns the url of the crawler.
     */
    public String getUrl()
    {
        return config.getUrl();
    }

    /**
     * Returns the base path of the crawler.
     */
    public String getBasePath()
    {
        return config.getBasePath();
    }

    /**
     * Returns the link to get more items.
     */
    public MoreLinkConfiguration getMoreLink()
    {
        return config.getMoreLink();
    }

    /**
     * Loads the given page.
     */
    protected void loadPage(String url, LoadingConfiguration loading) throws IOException
    {
        if(loading != null && loading.isAntiCache())
            url = FormatUtils.addAntiCacheParameter(url);
        if(debug())
            logger.info("Loading page: "+url);
        driver.get(url);
        if(debug())
            logger.info("Loaded page: "+driver.getTitle());
    }

    /**
     * Connect the client to the page indicated by the url.
     * <p>
     * Also optionally clicks a Load More button and waits for the loading delay if configured.
     */
    protected void connect() throws IOException
    {
        long now = System.currentTimeMillis();
        configureImplicitWait(getTeaserLoading());
        loadPage(getUrl(), getTeaserLoading());
        configureExplicitWait(getTeaserLoading());

        // Click a "Load More" button if configured
        if(getMoreLink() != null)
        {
            int count = getMoreLink().getCount();
            for(int i = 0; i < count; i++)
            {
                if(debug())
                    logger.info("More link click "+(i+1)+" of "+count);
                clickMoreLink(getMoreLink());
            }
        }

        // Trace to see the teaser page
        if(trace(getDriver()))
            logger.info("teaser-page="+getDriver().getPageSource());

        if(debug())
            logger.info("Loaded page in: "+(System.currentTimeMillis()-now)+"ms");
        initialised = true;
    }

    /**
     * Click a Load More button after the initial page load.
     */
    protected void clickMoreLink(MoreLinkConfiguration moreLink) throws IOException
    {
        String selector = moreLink.getSelector();
        if(selector.length() > 0)
        {
            if(debug())
                logger.info("Looking for More link: "+selector);
            List<WebElement> links = driver.findElements(By.cssSelector(selector));
            if(links.size() > 0)
            {
                if(debug())
                    logger.info("Found More link: "+selector);

                long max = moreLink.getMaxWait();
                long interval = moreLink.getInterval();
                if(max > 0L)
                {
                    if(debug())
                        logger.info("Set explicit wait before More link click: "+max);
                    WebDriverWait waiter = new WebDriverWait(driver, max, interval); 
                    WebElement element = waiter.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    if(element != null)
                        element.click();
                }
                else
                {
                    links.get(0).click();
                }
            }
            else
            {
                logger.warning("More link not found: "+selector);
            }
        }
    }

    protected void configureImplicitWait(LoadingConfiguration loading)
    {
        if(loading == null)
            return;

        long wait = loading.getWait();

        if(wait > 0L)
        {
            if(debug())
                logger.info("Set implicit wait: "+wait);
            driver.manage().timeouts().implicitlyWait(wait, TimeUnit.MILLISECONDS);
        }
    }

    protected void configureExplicitWait(LoadingConfiguration loading)
    {
        if(loading == null || browser == CrawlerBrowser.HTMLUNIT) // Don't use JS with HtmlUnit
            return;

        String selector = loading.getSelector();
        long interval = loading.getInterval();
        long max = loading.getMaxWait();

        if(selector.length() > 0 && interval > 0L)
        {
            if(debug())
                logger.info(String.format("Set explicit wait: max-wait=%d interval=%d selector=%s", max, interval, selector));
            WebDriverWait waiter = new WebDriverWait(driver, max, interval); 
            waiter.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
        }
    }

    /**
     * Create a content summary from a selected node.
     */
    public abstract T getContentSummary(WebElement result, ContentFields fields) throws DateTimeParseException;

    /**
     * Process all the configured teaser fields.
     */
    public int processTeaserFields(LoadingConfiguration loading) throws IOException, DateTimeParseException
    {
        int ret = 0;
        if(getUrl().length() == 0)
            throw new IllegalArgumentException("Root empty for teasers");

        if(!initialised)
            connect();

        // Process selections
        Map<String,String> map = new HashMap<String,String>();
        for(ContentFields fields : getTeaserFields())
        {
            List<WebElement> results = driver.findElements(By.cssSelector(fields.getRoot()));
            if(debug())
                logger.info("Found "+results.size()+" teasers for teasers: "+fields.getRoot());
            ret += results.size();

            for(WebElement result : results)
            {
                // Trace to see the teaser root node
                if(trace(result))
                    logger.info("teaser-node="+result.getAttribute("innerHTML"));

                T content = getContentSummary(result, fields);
                if(content.isValid() && !map.containsKey(content.getUniqueId()))
                {
                    // Check that the teaser matches the configured keywords
                    if(loading != null && loading.hasKeywords() && !content.matches(loading.getKeywordList()))
                    {
                        logger.info(String.format("Skipping article as it does not match keywords: %s (%s)",
                            content.getTitle(), loading.getKeywords()));
                        continue;
                    }

                    addContent(content);
                    map.put(content.getUniqueId(), content.getUniqueId());
                }

                if(numContentItems() >= getMaxResults())
                    break;
            }
        }

        if(debug())
            logger.info("Found "+numContentItems()+" items");
        return ret;
    }

    /**
     * Returns a list of the metatags for the given attribute name and value.
     */
    protected List<WebElement> getMetatags(String name, String value)
    {
        List<WebElement> ret = new ArrayList<WebElement>();
        List<WebElement> tags = driver.findElements(By.tagName("meta"));

        for(WebElement tag : tags)
        {
            String attr = tag.getAttribute(name);
            if(attr != null && attr.equals(value))
            {
                ret.add(tag);
                if(debug())
                    logger.info("Found metatag "+name+" "+value+": "+tag.getAttribute("content"));
            }
        }

        return ret;
    }

    protected String getPropertyMetatag(String value)
    {
        List<WebElement> tags = getMetatags("property", value);
        if(tags.size() == 0)
            tags = getMetatags("name", value); // Sometimes og tags called "name" instead of "property"
        if(tags.size() > 0)
            return tags.get(0).getAttribute("content");
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the content validator is found.
     */
    protected void validateContent(T content, String validator, WebElement root, String type)
    {
        List<WebElement> elements = root.findElements(By.cssSelector(validator));
        content.setValid(elements.size() > 0);
        if(elements.size() > 0)
        {
            if(debug())
                logger.info("Validation successful for "+type+": "+validator);
        }
        else
        {
            logger.warning("Validation failed for "+type+", skipping: "+validator);
        }
    }

    /**
     * Process an element field returning multiple results.
     */
    protected String getElements(ContentField field, WebElement root, String type,
        boolean multiple, String separator)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for elements for "+type+" field: "+field.getName());

            List<WebElement> nodes = null;
            try
            {
                nodes = root.findElements(By.cssSelector(field.getSelector()));
            }
            catch(StaleElementReferenceException e)
            {
            }

            if(nodes != null && nodes.size() > 0)
            {
                int i = 0; 
                StringBuilder str = new StringBuilder();
                for(WebElement node : nodes)
                {
                    String value = null;
                    if(field.getAttribute() != null && field.getAttribute().length() > 0)
                    {
                        value = node.getAttribute(field.getAttribute());
                    }
                    else
                    {
                        // Get the node text
                        if(browser == CrawlerBrowser.CHROME)
                        {
                            value = node.getAttribute("innerHTML");
                            if(value.indexOf("<") != -1) // Remove any markup
                                value = value.replaceAll("<.+?>","").trim();
                        }
                        else // HtmlUnit
                        {
                            value = node.getText();
                        }
                    }

                    if(value.length() > 0)
                    {
                        if(i > 0 && separator != null && separator.length() > 0)
                            str.append(separator);
                        str.append(value);
                    }

                    if(!multiple)
                        break;
                    ++i;
                }

                ret = getValue(field, str.toString());

                if(debug())
                    logger.info("Found elements for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Elements not found for "+type+" field: "+field.getName());
            }
        }
        else if(field.getSource().isMetatag())
        {
            if(debug())
                logger.info("Looking for element metatag for "+type+" field: "+field.getName());

            String tag = getPropertyMetatag(field.getSelector());
            if(tag != null)
            {
                ret = getValue(field, tag);

                if(debug())
                    logger.info("Found element metatag for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Element metatag not found for "+type+" field: "+field.getName());
            }
        }

        return ret;
    }

    /**
     * Process an element field returning a single result.
     */
    protected String getElement(ContentField field, WebElement root, String type)
    {
        return getElements(field, root, type, false, null);
    }

    /**
     * Process an anchor field.
     */
    protected String getAnchor(ContentField field, WebElement root, String type, boolean removeParameters)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for anchor for "+type+" field: "+field.getName());

            WebElement anchor = null;
            WebElement div = null;
            if(field.getSelector().equals(ROOT)) // The anchor is the root node itself
            {
                if(root.getTagName().equals(ANCHOR))
                    anchor = root;
                else if(root.getTagName().equals(DIV))
                    div = root;
            }
            else
            {
                WebElement element = null;
                try
                {
                    element = root.findElement(By.cssSelector(field.getSelector()));
                    if(element.getTagName().equals(ANCHOR))
                        anchor = element;
                    else if(element.getTagName().equals(DIV))
                        div = element;
                }
                catch(NoSuchElementException e)
                {
                }
            }

            if(anchor != null)
            {
                ret = FormatUtils.getFormattedUrl(getBasePath(), anchor.getAttribute("href"), removeParameters);

                if(debug())
                    logger.info("Found anchor for "+type+" field "+field.getName()+": "+ret);
            }
            else if(div != null) // Sometimes the link is a div with a href attribute
            {
                ret = FormatUtils.getFormattedUrl(getBasePath(), div.getAttribute("href"), removeParameters);

                if(debug())
                    logger.info("Found anchor div for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Anchor not found for "+type+" field: "+field.getName());
            }
        }
        else if(field.getSource().isMetatag())
        {
            if(debug())
                logger.info("Looking for anchor metatag for "+type+" field: "+field.getName());

            String tag = getPropertyMetatag(field.getSelector());
            if(tag != null)
            {
                ret = FormatUtils.getFormattedUrl(getBasePath(), tag, removeParameters);

                if(debug())
                    logger.info("Found anchor metatag for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Anchor metatag not found for "+type+" field: "+field.getName());
            }
        }

        return ret;
    }

    /**
     * Coalesces the given paragraphs into a single string by accumulating paragraphs up to a heading or maximum length.
     */
    protected String getFormattedSummary(String selector, WebElement root, SummaryConfiguration config, boolean debug)
    {
        return getFormattedSummary(selector, root, config.getMinLength(), config.getMaxLength(), config.getMinParagraph(), debug);
    }

    /**
     * Coalesces the given paragraphs into a single string by accumulating paragraphs up to a heading or maximum length.
     */
    protected String getFormattedSummary(String selector, WebElement root, 
        int minLength, int maxLength, int minParagraph, boolean debug)
    {
        StringBuilder ret = new StringBuilder();
        String carryover = null;

        String tag = null;
        List<WebElement> elements = root.findElements(By.cssSelector(selector));

        if(debug)
            logger.info(String.format("1: getFormattedSummary: elements=%d minParagraph=%d minLength=%d maxLength=%d",
                elements.size(), minParagraph, minLength, maxLength));

        for(WebElement element : elements)
        {
            if(ret.length() == 0 && element.getTagName().startsWith("h")) // Exclude headings
            {
                if(debug)
                    logger.info(String.format("1a: getFormattedSummary: heading: tag=%s element.tag=%s ret.length=%d",
                        tag, element.getTagName(), ret.length()));
                continue;
            }
            else if(tag == null)
            {
                tag = element.getTagName();
                if(debug)
                    logger.info(String.format("1b: getFormattedSummary: set: tag=%s element.tag=%s ret.length=%d",
                        tag, element.getTagName(), ret.length()));
            }
            else if(!element.getTagName().equals(tag)) // Tag has changed
            {
                if(debug)
                    logger.info(String.format("1c: getFormattedSummary: set: tag=%s element.tag=%s ret.length=%d",
                        tag, element.getTagName(), ret.length()));
                break;
            }

            String text = element.getText().trim();

            // Remove linefeeds
            text = text.replaceAll("[ \t]*(\r\n|\n)+[ \t]*", " ");
            text = text.trim();

            if(debug)
                logger.info(String.format("2: getFormattedSummary: tag=%s text=%s text.length=%d ret.length=%d",
                    element.getTagName(), text, text.length(), ret.length()));

            // Carry over very short paragraphs and add to the next
            if(carryover != null)
            {
                StringBuilder str = new StringBuilder(carryover);
                if(str.length() > 1) // Allow for stylised first character
                    str.append(" ");
                str.append(text);     
                text = str.toString();

                if(debug)
                    logger.info(String.format("3: getFormattedSummary: carryover=%s text=%s length=%d",
                        carryover, text, text.length()));
            }

            if(text.length() > 0 && text.length() < minParagraph) // Too short so just carry it forward
            {
                carryover = text;

                if(debug)
                    logger.info(String.format("4: getFormattedSummary: carryover=%s text=%s length=%d",
                        carryover, text, text.length()));
            }
            else
            {
                // Exit if the addition would take us over the maximum
                if(ret.length() > 0 && (ret.length()+text.length()) > maxLength)
                {
                    if(debug)
                        logger.info(String.format("5: getFormattedSummary: break1: ret=%s text.length=%d ret.length=%d",
                            ret, text.length(), ret.length()));
                    break;
                }

                if(debug)
                    logger.info(String.format("6: getFormattedSummary: ret=%s text.length=%d ret.length=%d",
                        ret, text.length(), ret.length()));

                if(ret.length() > 0 && text.length() > 0)
                    ret.append(" ");
                ret.append(text);
                carryover = null;

                if(debug)
                    logger.info(String.format("7: getFormattedSummary: ret=%s text.length=%d ret.length=%d",
                        ret, text.length(), ret.length()));

                // Exit if the addition has taken us over the minimum
                if(ret.length() > minLength)
                {
                    if(debug)
                        logger.info(String.format("8: getFormattedSummary: break2: ret=%s text.length=%d ret.length=%d",
                            ret, text.length(), ret.length()));
                    break;
                }
            }
        }

        // Handle descriptions less than the min paragraph length
        if(carryover != null
            && (ret.length() == 0 || (ret.length()+carryover.length()) <= maxLength))
        {
            if(ret.length() > 0 && carryover.length() > 0)
                ret.append(" ");
            ret.append(carryover);
        }

        // Summary should end with full stop if it ends with a semi-colon
        if(ret.length() > 0 && ret.charAt(ret.length()-1) == ':')
        {
            ret.setCharAt(ret.length()-1, '.');

            if(debug)
                logger.info(String.format("9: getFormattedSummary: fixed ':': ret=%s ret.length=%d",
                    ret, ret.length()));
        }

        if(debug)
            logger.info(String.format("10: getFormattedSummary: ret=%s ret.length=%d",
                ret, ret.length()));

        return ret.toString();
    }

    /**
     * Process the body field to produce a summary.
     */
    protected String getBodySummary(ContentField field, WebElement root, String type,
        SummaryConfiguration summary, boolean debug)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for body summary for "+type+" field: "+field.getName());

            String body = getFormattedSummary(field.getSelector(), root, summary, debug);
            if(body.length() == 0 && field.hasSelector2())
                body = getFormattedSummary(field.getSelector2(), root, summary, debug);

            if(body.length() > 0)
            {
                ret = String.format("<p>%s</p>", body);

                if(debug())
                    logger.info("Found body summary for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Body summary not found for "+type+" field: "+field.getName());
            }
        }
        else if(field.getSource().isMetatag())
        {
            if(debug())
                logger.info("Looking for body summary metatag for "+type+" field: "+field.getName());

            String tag = getPropertyMetatag(field.getSelector());
            if(tag != null)
            {
                ret = String.format("<p>%s</p>", getValue(field, tag));

                if(debug())
                    logger.info("Found body summary metatag for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Body summary metatag not found for "+type+" field: "+field.getName());
            }
        }

        return ret;
    }

    /**
     * Coalesces the given paragraphs into a single string, keeping any markup.
     */
    protected String getFormattedParagraphs(String selector, WebElement root, Pattern stopExprPattern)
    {
        StringBuilder ret = new StringBuilder();

        List<WebElement> elements = root.findElements(By.cssSelector(selector));

        for(WebElement element : elements)
        {
            String text = element.getText().trim();
            String tag = element.getTagName();

            if(text.length() == 0)
                continue;

            // Stop if the text matches the stop expression
            if(stopExprPattern != null && stopExprPattern.matcher(text).matches())
                break;

            if(ret.length() > 0 && text.length() > 0)
                ret.append("\n\n");

            if(tag.equals("ul") || tag.equals("ol"))
            {
                String[] tokens = text.split("\r\n|\n");

                int i = 0;
                for(String token : tokens)
                {
                    if(i > 0)
                        ret.append("\n");
                    if(tag.equals("ol"))  // "1." indicates <li> for <ol>
                        ret.append(Integer.toString(i+1)).append(". ");
                    else if(tag.equals("ul")) // "* " indicates <li> for <ul>
                        ret.append("* "); // "*" indicates <li> for <ul>
                    ret.append(token);
                    ++i;
                }
            }
            else // p / div
            {
                ret.append(text);
            }
        }

        return ret.toString();
    }

    /**
     * Process the body field.
     */
    protected String getBody(ContentField field, WebElement root, String type)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for body for "+type+" field: "+field.getName());

            String body = getFormattedParagraphs(field.getSelector(), root, field.getStopExprPattern());
            if(body.length() == 0 && field.hasSelector2())
                body = getFormattedParagraphs(field.getSelector2(), root, field.getStopExprPattern());

            if(body.length() > 0)
            {
                ret = body;

                if(debug())
                    logger.info("Found body for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Body not found for "+type+" field: "+field.getName());
            }
        }
        else if(field.getSource().isMetatag())
        {
            if(debug())
                logger.info("Looking for body metatag for "+type+" field: "+field.getName());

            String tag = getPropertyMetatag(field.getSelector());
            if(tag != null)
            {
                ret = getValue(field, tag);

                if(debug())
                    logger.info("Found body metatag for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Body metatag not found for "+type+" field: "+field.getName());
            }
        }

        return ret;
    }

    /**
     * Process an image field.
     */
    protected String getImageSrc(ContentField field, WebElement root, String type)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for image for "+type+" field: "+field.getName());

            WebElement image = null;
            try
            {
                image = root.findElement(By.cssSelector(field.getSelector()));
            }
            catch(NoSuchElementException e)
            {
            }

            if(image != null)
            {
                if(field.getAttribute() != null && field.getAttribute().length() > 0)
                {
                    ret = getValue(field, image.getAttribute(field.getAttribute()));

                    if(debug())
                        logger.info("Found image "+field.getAttribute()+" for "+type+" field "+field.getName()+": "+ret);
                }
                else
                {
                    ret = getValue(field, image.getAttribute("src"));

                    if(debug())
                        logger.info("Found image src for "+type+" field "+field.getName()+": "+ret);
                }
            }
            else
            {
                logger.warning("Image not found for "+type+" field: "+field.getName());
            }
        }
        else if(field.getSource().isMetatag())
        {
            if(debug())
                logger.info("Looking for image metatag for "+type+" field: "+field.getName());

            String tag = getPropertyMetatag(field.getSelector());
            if(tag != null)
            {
                ret = getValue(field, tag);

                if(debug())
                    logger.info("Found image metatag for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Image metatag not found for "+type+" field: "+field.getName());
            }
        }

        return ret;
    }

    /**
     * Returns the style attribute of an element.
     */
    protected String getStyle(ContentField field, WebElement root, String type)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for style for "+type+" field: "+field.getName());

            WebElement element = null;
            try
            {
                element = root.findElement(By.cssSelector(field.getSelector()));
            }
            catch(NoSuchElementException e)
            {
            }

            if(element != null)
            {
                ret = getValue(field, element.getAttribute("style"));

                if(debug())
                    logger.info("Found style for "+type+" field "+field.getName()+": "+ret);
            }
            else
            {
                logger.warning("Style not found for "+type+" field: "+field.getName());
            }
        }

        return ret;
    }

    /**
     * Returns the background image from a style attribute.
     */
    protected String getBackgroundImage(String style)
    {
        String ret = null;
        Matcher m = Pattern.compile("(?:.*)background-image:(.+?)(?:;|\\z)(?:.*)").matcher(style);

        if(m.find())
        {
            String image = m.group(1);

            if(debug())
                logger.info(String.format("Background image found for style: %s", image));

            if(image != null && image.length() > 0)
            {
                Matcher m2 = Pattern.compile("(?:.*)url\\((.+)\\)(?:.*)").matcher(style);
                if(m2.find())
                {
                    ret = m2.group(1);

                    // Remove quotes
                    ret = ret.replaceAll("[\"']", "");

                    if(debug())
                        logger.info(String.format("Url found for background image: %s", ret));
                }
                else
                {
                    logger.warning(String.format("No url found for background image: %s", image));
                }
            }
        }
        else
        {
            logger.warning(String.format("No background image found for style: %s", style));
        }

        return ret;
    }

    /**
     * URL encodes the given URL.
     */
    protected String encodeUrl(String url)
    {
        String ret = url;

        if(url != null && url.indexOf("%") == -1) // already escaped
        {
            ret = UrlEscapers.urlFragmentEscaper().escape(url);
            ret = ret.replaceAll("%23", "#"); // unescape # as its used for parameters
        }

        return ret;
    }
}