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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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
import com.opsmatters.media.config.content.FieldSelector;
import com.opsmatters.media.config.content.FieldExclude;
import com.opsmatters.media.config.content.FieldFilter;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.crawler.parser.ElementType;
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

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36";

    public static final String ANCHOR = "a";
    public static final String DIV = "div";
    public static final String SECTION = "section";

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
                options.addArguments("--user-agent="+USER_AGENT);
                options.addArguments("--no-proxy-server");
                options.addArguments("--proxy-server='direct://'");
                options.addArguments("--proxy-bypass-list=*");
                options.setHeadless(true);

                driver = new ChromeDriver(options);
                this.browser = browser;
            }
            else if(browser == CrawlerBrowser.FIREFOX)
            {
                FirefoxOptions options = new FirefoxOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--user-agent="+USER_AGENT);
                options.addArguments("--no-proxy-server");
                options.addArguments("--proxy-server='direct://'");
                options.addArguments("--proxy-bypass-list=*");
                options.setHeadless(true);
                driver = new FirefoxDriver(options);
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
        driver = null;
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
        if(loading != null)
        {
            if(loading.isAntiCache())
                url = FormatUtils.addAntiCacheParameter(url);
            if(loading.hasTrailingSlash())
                url += "/";
        }

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
                logger.info("Found "+results.size()+" teasers for selector: "+fields.getRoot());
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
    protected void validateContent(T content, ContentField field, WebElement root, String type)
    {
        boolean valid = false;

        for(FieldSelector selector : field.getSelectors())
        {
            try
            {
                // Try each selector
                List<WebElement> nodes = root.findElements(By.cssSelector(selector.getExpr()));
                if(nodes != null && nodes.size() > 0)
                {
                    if(field.hasExtractors())
                    {
                        String result = getValue(field, select(selector, nodes), null);
                        valid = result != null && result.length() > 0;
                    }
                    else
                    {
                        valid = true;
                    }

                    if(valid)
                        break;
                }
            }
            catch(StaleElementReferenceException e)
            {
            }
        }

        content.setValid(valid);
        if(content.isValid())
        {
            if(debug())
                logger.info("Validation successful for "+type+": "+field.getSelector(0).getExpr());
        }
        else
        {
            if(debug())
                logger.warning("Validation failed for "+type+", skipping: "+field.getSelector(0).getExpr());
        }
    }

    /**
     * Process an element field returning multiple results.
     */
    protected String getElements(ContentField field, WebElement root, String type)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for elements for "+type+" field: "+field.getName());

        List<WebElement> nodes = null;
        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                try
                {
                    // Try each selector
                    nodes = root.findElements(By.cssSelector(selector.getExpr()));
                    if(nodes != null && nodes.size() > 0)
                    {
                        ret = getValue(field, select(selector, nodes));
                        if(ret.length() > 0)
                        {
                            if(debug())
                                logger.info("Found elements for "+type+" field "+field.getName()+": "+ret);
                            break;
                        }
                    }
                }
                catch(StaleElementReferenceException e)
                {
                }
            }
            else if(selector.getSource().isMeta())
            {
                String tag = getPropertyMetatag(selector.getExpr());
                if(tag != null)
                {
                    ret = getValue(field, tag);
                    if(debug())
                        logger.info("Found element metatag for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
        }

        if(ret == null)
        {
            logger.warning("Elements not found for "+type+" field: "+field.getName());
        }

        return ret;
    }

    /**
     *  Select the value from the list of elements.
     */
    private String select(FieldSelector selector, List<WebElement> nodes)
    {
        int i = 0; 
        StringBuilder str = new StringBuilder();
        for(WebElement node : nodes)
        {
            String value = null;
            if(selector.getAttribute() != null && selector.getAttribute().length() > 0)
            {
                value = node.getAttribute(selector.getAttribute());
            }
            else
            {
                // Get the node text
                value = getText(node);
            }

            if(value.length() > 0)
            {
                if(i > 0 && selector.getSeparator() != null && selector.getSeparator().length() > 0)
                    str.append(selector.getSeparator());
                str.append(value);
            }

            if(!selector.isMultiple())
                break;
            ++i;
        }

        return str.toString().trim();
    }

    /**
     * Extract the text from the given element.
     */
    protected String getText(WebElement element)
    {
        String ret = "";

        if(browser == CrawlerBrowser.CHROME || browser == CrawlerBrowser.FIREFOX)
        {
            ret = element.getAttribute("innerHTML");
            if(ret.indexOf("<") != -1) // Remove any markup
                ret = ret.replaceAll("<.+?>","").trim();
        }
        else // HtmlUnit
        {
            ret = element.getText().trim();
        }

        return ret;
    }

    /**
     * Process an anchor field.
     */
    protected String getAnchor(ContentField field, WebElement root, String type, boolean removeParameters)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for anchor for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                WebElement anchor = null;
                WebElement div = null;
                WebElement section = null;

                if(selector.getExpr().equals(ROOT)) // The anchor is the root node itself
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
                        element = root.findElement(By.cssSelector(selector.getExpr()));
                        if(element.getTagName().equals(ANCHOR))
                            anchor = element;
                        else if(element.getTagName().equals(DIV))
                            div = element;
                        else if(element.getTagName().equals(SECTION))
                            section = element;
                    }
                    catch(NoSuchElementException e)
                    {
                    }
                }

                String attribute = selector.hasAttribute() ? selector.getAttribute() : "href";

                if(anchor != null)
                {
                    ret = FormatUtils.getFormattedUrl(getBasePath(), anchor.getAttribute(attribute), removeParameters);
                    if(debug())
                        logger.info("Found anchor for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
                else if(div != null) // Sometimes the link is a div with a href attribute
                {
                    ret = FormatUtils.getFormattedUrl(getBasePath(), div.getAttribute(attribute), removeParameters);
                    if(debug())
                        logger.info("Found anchor div for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
                else if(section != null) // Sometimes the link is a section with a custom attribute
                {
                    ret = FormatUtils.getFormattedUrl(getBasePath(), section.getAttribute(attribute), removeParameters);
                    if(debug())
                        logger.info("Found anchor section for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
            else if(selector.getSource().isMeta())
            {
                String tag = getPropertyMetatag(selector.getExpr());
                if(tag != null)
                {
                    ret = getValue(field, tag);
                    if(debug())
                        logger.info("Found anchor metatag for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
        }

        if(ret == null)
        {
            logger.warning("Anchor not found for "+type+" field: "+field.getName());
        }

        return ret;
    }

    /**
     * Coalesces the given paragraphs into a single string by accumulating paragraphs up to a heading or maximum length.
     */
    protected String getFormattedSummary(String selector, boolean multiple,
        List<FieldExclude> excludes, List<FieldFilter> filters, WebElement root, SummaryConfiguration config, boolean debug)
    {
        return getFormattedSummary(selector, multiple, excludes, filters,
            root, config.getMinLength(), config.getMaxLength(), debug);
    }

    /**
     * Coalesces the given paragraphs into a single string by accumulating paragraphs up to a heading or maximum length.
     */
    protected String getFormattedSummary(String selector, boolean multiple,
        List<FieldExclude> excludes, List<FieldFilter> filters, WebElement root, int minLength, int maxLength, boolean debug)
    {
        ElementType lastType = null;
        List<WebElement> elements;
        if(selector.equals(ROOT)) // The parent is the root node itself
        {
            elements = new ArrayList<WebElement>();
            elements.add(root);
        }
        else
        {
            elements = root.findElements(By.cssSelector(selector));
        }

        if(debug)
            logger.info(String.format("1: getFormattedSummary: elements=%d minLength=%d maxLength=%d",
                elements.size(), minLength, maxLength));

        BodyParser parser = new BodyParser(excludes, filters, debug);
        for(WebElement element : elements)
            parser.parseHtml(element.getAttribute("innerHTML"));

        String ret = parser.formatSummary(minLength, maxLength, multiple);
        if(debug)
            logger.info(String.format("2: getFormattedSummary: ret=%s ret.length=%d",
                ret, ret.length()));

        return ret;
    }

    /**
     * Process the body field to produce a summary.
     */
    protected String getBodySummary(ContentField field, WebElement root, String type,
        SummaryConfiguration summary, boolean debug)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for body summary for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                String body = getFormattedSummary(selector.getExpr(), selector.isMultiple(),
                    selector.getExcludes(), field.getFilters(), root, summary, debug);
                if(body.length() > 0)
                {
                    // Apply any extractors to the selection
                    body = getValue(field, body);
                    ret = String.format("<p>%s</p>", body.trim());
                    if(debug())
                        logger.info("Found body summary for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
            else if(selector.getSource().isMeta())
            {
                String tag = getPropertyMetatag(selector.getExpr());
                if(tag != null)
                {
                    ret = String.format("<p>%s</p>", getValue(field, tag));
                    if(debug())
                        logger.info("Found body summary metatag for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
        }

        if(ret == null)
        {
            logger.warning("Body summary not found for "+type+" field: "+field.getName());
        }

        return ret;
    }

    /**
     * Coalesces the given paragraphs into a single string, keeping any markup.
     */
    protected String getFormattedBody(String selector, WebElement root,
        List<FieldExclude> excludes, List<FieldFilter> filters, boolean debug)
    {
        ElementType lastType = null;
        List<WebElement> elements;
        if(selector.equals(ROOT)) // The parent is the root node itself
        {
            elements = new ArrayList<WebElement>();
            elements.add(root);
        }
        else
        {
            elements = root.findElements(By.cssSelector(selector));
        }

        if(debug)
            logger.info(String.format("1: getFormattedBody: elements=%d", elements.size()));

        BodyParser parser = new BodyParser(excludes, filters, debug);
        for(WebElement element : elements)
            parser.parseHtml(element.getAttribute("innerHTML"));

        String ret = parser.formatBody();
        if(debug)
            logger.info(String.format("2: getFormattedBody: ret=%s ret.length=%d",
                ret, ret.length()));

        return ret;
    }

    /**
     * Process the body field.
     */
    protected String getBody(ContentField field, WebElement root, String type, boolean debug)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for body for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                String body = getFormattedBody(selector.getExpr(), root,
                    selector.getExcludes(), field.getFilters(), debug);
                if(body.length() > 0)
                {
                    ret = body;
                    if(debug())
                        logger.info("Found body for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
            else if(selector.getSource().isMeta())
            {
                String tag = getPropertyMetatag(selector.getExpr());
                if(tag != null)
                {
                    ret = getValue(field, tag);
                    if(debug())
                        logger.info("Found body metatag for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
        }

        if(ret == null)
        {
            logger.warning("Body not found for "+type+" field: "+field.getName());
        }

        return ret;
    }

    /**
     * Process an image field.
     */
    protected String getImageSrc(ContentField field, WebElement root, String type)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for image for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                WebElement image = null;
                try
                {
                    image = root.findElement(By.cssSelector(selector.getExpr()));
                }
                catch(NoSuchElementException e)
                {
                }

                if(image != null)
                {
                    if(selector.getAttribute() != null && selector.getAttribute().length() > 0)
                    {
                        ret = getValue(field, image.getAttribute(selector.getAttribute()));
                        if(debug())
                            logger.info("Found image "+selector.getAttribute()+" for "+type+" field "+field.getName()+": "+ret);
                        break;
                    }
                    else
                    {
                        ret = getValue(field, image.getAttribute("src"));
                        if(debug())
                            logger.info("Found image src for "+type+" field "+field.getName()+": "+ret);
                        break;
                    }
                }
            }
            else if(selector.getSource().isMeta())
            {
                String tag = getPropertyMetatag(selector.getExpr());
                if(tag != null)
                {
                    ret = getValue(field, tag);
                    if(debug())
                        logger.info("Found image metatag for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
            }
        }

        if(ret == null)
        {
            logger.warning("Image not found for "+type+" field: "+field.getName());
        }

        return ret;
    }

    /**
     * Returns the style attribute of an element.
     */
    protected String getStyle(ContentField field, WebElement root, String type)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for style for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            WebElement element = null;
            try
            {
                element = root.findElement(By.cssSelector(selector.getExpr()));
            }
            catch(NoSuchElementException e)
            {
            }

            if(element != null)
            {
                ret = getValue(field, element.getAttribute("style"));
                if(debug())
                    logger.info("Found style for "+type+" field "+field.getName()+": "+ret);
                break;
            }
        }

        if(ret == null)
        {
            logger.warning("Style not found for "+type+" field: "+field.getName());
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