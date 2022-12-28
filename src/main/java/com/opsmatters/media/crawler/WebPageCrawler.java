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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.opsmatters.media.cache.content.Teasers;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.SummaryConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.MoreLink;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.content.crawler.field.FieldSelector;
import com.opsmatters.media.model.content.crawler.field.FieldExclude;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.crawler.parser.ElementType;
import com.opsmatters.media.model.admin.TraceObject;
import com.opsmatters.media.model.content.ContentTeaser;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a crawler for content items from a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class WebPageCrawler<T extends ContentTeaser, D extends ContentTeaser> extends ContentCrawler<T,D>
{
    private static final Logger logger = Logger.getLogger(WebPageCrawler.class.getName());

    public static final String ANCHOR = "a";
    public static final String DIV = "div";
    public static final String SECTION = "section";
    public static final String ROOT = "<root>";

    private CrawlerBrowser browser;
    private WebDriver driver;
    private TraceObject traceObject = TraceObject.NONE;
    private ContentConfig config;
    private CrawlerWebPage page;
    private String imagePrefix = "";
    private String lastUrl;

    static
    {
        // Turn off CSS and JavaScript error logging
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    /**
     * Constructor that takes a code and page.
     */
    public WebPageCrawler(ContentConfig config, CrawlerWebPage page)
    {
        super(page);
        this.config = config;
        this.page = page;

        // Create the web driver
        WebDriverPool.setDebug(debug());
        if(driver == null)
            driver = WebDriverPool.getDriver(page.getBrowser(), page.isHeadless());
        this.browser = page.getBrowser();
    }

    /**
     * Close the crawler and release resources.
     */
    public void close()
    {
        WebDriverPool.releaseDriver(driver);
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
     * Returns the current page source.
     */
    protected String getPageSource(String root)
    {
        String ret = "";

        try
        {
            ret = driver.findElement(By.tagName(root)).getAttribute("outerHTML");
        }
        catch(RuntimeException e)
        {
            logger.severe("Unable to get page source for "+root);
        }

        return ret;

    }

    /**
     * Returns the current page source.
     */
    protected String getPageSource()
    {
        return getPageSource("html");
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
    public CrawlerWebPage getPage()
    {
        return page;
    }

    /**
     * Returns the image prefix for the crawler.
     */
    public String getImagePrefix()
    {
        return imagePrefix;
    }

    /**
     * Sets the image prefix for the crawler.
     */
    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }

    /**
     * Returns the base path of the crawler.
     */
    public String getBasePath()
    {
        String ret = page.getBasePath();
        if(ret == null || ret.length() == 0)
        {
            ret = page.getUrl(0);
            int pos = ret.indexOf("//");
            if(pos != -1)
            {
                pos = ret.indexOf("/", pos+2); // find first slash after http
                if(pos != -1)
                {
                    ret = ret.substring(0, pos);
                }
            }
        }
        return ret;
    }

    /**
     * Returns the title of the crawled page.
     */
    @Override
    public String getTitle()
    {
        return driver.getTitle();
    }

    /**
     * Loads the given page.
     */
    protected void loadPage(String url, ContentLoading loading) throws IOException
    {
        if(loading != null)
        {
            if(loading.isAntiCache())
                url = FormatUtils.addAntiCacheParameter(url);
            if(loading.hasTrailingSlash())
                url += "/";
        }

        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        logger.info("Loading page: "+url);
        driver.get(url);
        logger.info("Loaded page: "+driver.getTitle());
    }

    /**
     * Load the teaser page indicated by the url.
     * <p>
     * Also optionally clicks a Load More button and waits for the loading delay if configured.
     */
    private void loadTeaserPage(String url) throws IOException
    {
        long now = System.currentTimeMillis();
        ContentLoading loading = page.getTeasers().getLoading();

        configureImplicitWait(loading);
        loadPage(url, loading);
        configureExplicitWait(loading);

        // Click a "Load More" button if configured
        if(page.hasMoreLink())
        {
            int count = page.getMoreLink().getCount();
            for(int i = 0; i < count; i++)
            {
                if(debug())
                    logger.info("More link click "+(i+1)+" of "+count);
                clickMoreLink(page.getMoreLink());
            }
        }

        // Trace to see the teaser page
        if(trace(getDriver()))
            logger.info("teaser-page="+getPageSource());

        // Scroll the page if configured
        configureMovement(loading);

        // Wait for the page to load
        configureSleep(loading);

        if(debug())
            logger.info("Loaded page in: "+(System.currentTimeMillis()-now)+"ms");

        lastUrl = url;
    }

    /**
     * Load the article page indicated by the url.
     */
    protected void loadArticlePage(String url) throws IOException
    {
        long now = System.currentTimeMillis();
        ContentLoading loading = getPage().getArticles().getLoading();

        configureImplicitWait(loading);
        loadPage(url, loading);
        configureExplicitWait(loading);

        // Scroll the page if configured
        configureMovement(loading);

        // Wait for the page to load
        configureSleep(loading);
    }

    /**
     * Click a Load More button after the initial page load.
     */
    protected void clickMoreLink(MoreLink moreLink) throws IOException
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

    protected void configureImplicitWait(ContentLoading loading)
    {
        if(loading == null)
            return;

        long wait = loading.getWait();
        if(debug())
            logger.info("Set implicit wait: "+wait);
        driver.manage().timeouts().implicitlyWait(wait, TimeUnit.MILLISECONDS);
    }

    protected void configureExplicitWait(ContentLoading loading)
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

    protected void configureSleep(ContentLoading loading)
    {
        if(loading == null)
            return;

        long sleep = loading.getSleep();

        if(sleep > 0L)
        {
            if(debug())
                logger.info("Set sleep: "+sleep);
            try
            {
                Thread.sleep(sleep);
            }
            catch(InterruptedException e)
            {
            }
        }
    }

    protected void configureMovement(ContentLoading loading)
    {
        if(loading == null)
            return;

        long scrollX = loading.getScrollX();
        long scrollY = loading.getScrollY();
        if(debug())
            logger.info(String.format("Set scroll: x=%d, y=%d", scrollX, scrollY));
        if(scrollX != 0 || scrollY != 0)
        {
            JavascriptExecutor js = (JavascriptExecutor)getDriver();
            js.executeScript(String.format("window.scrollBy(%d,%d)", scrollX, scrollY), "");
        }

        String moveTo = loading.getMoveTo();
        if(debug())
            logger.info(String.format("Set move to: %s", moveTo));
        if(moveTo != null && moveTo.length() > 0)
        {
            try
            {
                WebElement element = getDriver().findElement(By.cssSelector(moveTo));
                if(element != null)
                {
                    JavascriptExecutor js = (JavascriptExecutor)getDriver();
                    js.executeScript(String.format("var $div = document.querySelector('%s'); if ($div) { $div.scrollIntoView(true); }", moveTo), "");
                    Thread.sleep(500);
                    Actions actions = new Actions(getDriver());
                    actions.moveToElement​(element).perform();
                }
                else
                {
                    logger.severe("Unable to find move to element: "+moveTo);
                }
            }
            catch(WebDriverException | InterruptedException e)
            {
            }
        }
    }

    /**
     * Create a teaser from the given element.
     */
    protected abstract T getTeaser(Element result, Fields fields) throws DateTimeParseException;

    /**
     * Process the configured teasers.
     */
    @Override
    public int processTeasers(boolean cache) throws IOException, DateTimeParseException
    {
        int ret = 0;
        ContentLoading loading = page.getTeasers().getLoading();

        Map<String,String> map = new HashMap<String,String>();
        for(String url : page.getUrls())
        {
            if(url.length() == 0)
                throw new IllegalArgumentException("Root empty for teasers");

            // Try to get the teasers from the cache
            List<ContentTeaser> teasers = Teasers.get(config.getCode(), url);
            if(teasers != null)
            {
                for(ContentTeaser teaser : teasers)
                    addTeaser((T)teaser);
                ret += numTeasers();
                if(debug())
                    logger.info("Retrieved "+numTeasers()+" teasers from cache");
            }
            else
            {
                if(lastUrl == null || !lastUrl.equals(url))
                    loadTeaserPage(url);

                Document doc = Jsoup.parse(getPageSource("body"));
                doc.outputSettings().prettyPrint(false);

                // Process the teaser selections
                int count = 0;
                for(Fields fields : page.getTeasers().getFields())
                {
                    Elements results = doc.select(fields.getRoot());
                    if(debug())
                        logger.info("Found "+results.size()+" teasers for selector: "+fields.getRoot());
                    ret += results.size();

                    for(Element result : results)
                    {
                        // Trace to see the teaser root node
                        if(trace(result))
                            logger.info("teaser-node="+result.html());

                        T teaser = getTeaser(result, fields);
                        if(teaser.isValid() && !map.containsKey(teaser.getUniqueId()))
                        {
                            // Check that the teaser matches the configured keywords
                            if(loading != null && loading.hasKeywords() && !teaser.matches(loading.getKeywordList()))
                            {
                                logger.info(String.format("Skipping article as it does not match keywords: %s (%s)",
                                    teaser.getTitle(), loading.getKeywords()));
                                continue;
                            }

                            addTeaser(teaser);
                            map.put(teaser.getUniqueId(), teaser.getUniqueId());
                            ++count;
                        }

                        if(count >= getMaxResults())
                            break;
                    }
                }

                if(cache)
                    Teasers.set(url, getTeasers(), config);

                if(debug())
                    logger.info("Found "+numTeasers()+" teasers");
            }
        }

        return ret;
    }

    /**
     * Returns a list of the metatags for the given attribute name and value.
     */
    protected List<Element> getMetatags(String name, String value)
    {
        List<Element> ret = new ArrayList<Element>();
        Document doc = Jsoup.parse(getPageSource());
        doc.outputSettings().prettyPrint(false);
        List<Element> tags = doc.getElementsByTag("meta");

        for(Element tag : tags)
        {
            String attr = tag.attr(name);
            if(attr != null && attr.equals(value))
            {
                ret.add(tag);
                if(debug())
                    logger.info("Found metatag "+name+" "+value+": "+tag.attr("content"));
            }
        }

        return ret;
    }

    protected String getPropertyMetatag(String value)
    {
        List<Element> tags = getMetatags("property", value);
        if(tags.size() == 0)
            tags = getMetatags("name", value); // Sometimes og tags called "name" instead of "property"
        if(tags.size() > 0)
            return tags.get(0).attr("content");
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the content validator is found.
     */
    protected void validateContent(T content, Field field, Element root, String type)
    {
        boolean valid = false;

        for(FieldSelector selector : field.getSelectors())
        {
            try
            {
                // Try each selector
                Elements nodes = root.select(selector.getExpr());
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
    protected String getElements(Field field, Element root, String type)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for elements for "+type+" field: "+field.getName());

        Elements nodes = null;
        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                // Try each selector
                nodes = root.select(selector.getExpr());
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
            else if(selector.getSource().isMeta())
            {
                String tag = getPropertyMetatag(selector.getExpr());
                if(tag != null)
                {
                    ret = getValue(field, tag);
                    if(ret.length() > 0)
                    {
                        if(debug())
                            logger.info("Found element metatag for "+type+" field "+field.getName()+": "+ret);
                        break;
                    }
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
    private String select(FieldSelector selector, Elements nodes)
    {
        int i = 0; 
        StringBuilder str = new StringBuilder();
        for(Element node : nodes)
        {
            String value = null;
            if(selector.getAttribute() != null && selector.getAttribute().length() > 0)
            {
                value = node.attr(selector.getAttribute());
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
    protected String getText(Element element)
    {
        String ret = "";

        if(browser == CrawlerBrowser.CHROME || browser == CrawlerBrowser.FIREFOX)
        {
            ret = element.html();
            if(ret.indexOf("<") != -1) // Remove any markup
                ret = ret.replaceAll("<.+?>","").trim();
        }
        else // HtmlUnit
        {
            ret = element.text().trim();
        }

        return ret;
    }

    /**
     * Process an anchor field.
     */
    protected String getAnchor(Field field, Element root, String type, boolean removeParameters)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for anchor for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                Element anchor = null;
                Element div = null;
                Element section = null;

                if(selector.getExpr().equals(ROOT)) // The anchor is the root node itself
                {
                    if(root.tagName().equals(ANCHOR))
                        anchor = root;
                    else if(root.tagName().equals(DIV))
                        div = root;
                }
                else
                {
                    Element element = root.select(selector.getExpr()).first();
                    if(element != null)
                    {
                        if(element.tagName().equals(ANCHOR))
                            anchor = element;
                        else if(element.tagName().equals(DIV))
                            div = element;
                        else if(element.tagName().equals(SECTION))
                            section = element;
                    }
                }

                String attribute = selector.hasAttribute() ? selector.getAttribute() : "href";

                if(anchor != null)
                {
                    String value = anchor.attr(attribute);
                    if(value != null)
                        value = value.trim();
                    ret = FormatUtils.getFormattedUrl(getBasePath(), value, removeParameters);
                    if(debug())
                        logger.info("Found anchor for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
                else if(div != null) // Sometimes the link is a div with a href attribute
                {
                    String value = div.attr(attribute);
                    if(value != null)
                        value = value.trim();
                    ret = FormatUtils.getFormattedUrl(getBasePath(), value, removeParameters);
                    if(debug())
                        logger.info("Found anchor div for "+type+" field "+field.getName()+": "+ret);
                    break;
                }
                else if(section != null) // Sometimes the link is a section with a custom attribute
                {
                    String value = section.attr(attribute);
                    if(value != null)
                        value = value.trim();
                    ret = FormatUtils.getFormattedUrl(getBasePath(), value, removeParameters);
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
        List<FieldExclude> excludes, List<FieldFilter> filters, Element root, SummaryConfig config, boolean debug)
    {
        return getFormattedSummary(selector, multiple, excludes, filters,
            root, config.getMinLength(), config.getMaxLength(), debug);
    }

    /**
     * Coalesces the given paragraphs into a single string by accumulating paragraphs up to a heading or maximum length.
     */
    protected String getFormattedSummary(String selector, boolean multiple,
        List<FieldExclude> excludes, List<FieldFilter> filters, Element root, int minLength, int maxLength, boolean debug)
    {
        ElementType lastType = null;
        List<Element> elements;
        if(selector.equals(ROOT)) // The parent is the root node itself
        {
            elements = new ArrayList<Element>();
            elements.add(root);
        }
        else
        {
            elements = root.select(selector);
        }

        if(debug)
            logger.info(String.format("1: getFormattedSummary: elements=%d minLength=%d maxLength=%d",
                elements.size(), minLength, maxLength));

        BodyParser parser = new BodyParser(excludes, filters, debug);
        for(Element element : elements)
        {
            parser.parseHtml(element);

            if(!multiple && parser.numElements() > 0)
                break;
        }

        String ret = parser.formatSummary(minLength, maxLength);
        if(debug)
            logger.info(String.format("2: getFormattedSummary: ret=%s ret.length=%d",
                ret, ret.length()));

        return ret;
    }

    /**
     * Process the body field to produce a summary.
     */
    protected String getBodySummary(Field field, Element root, String type,
        SummaryConfig summary, boolean debug)
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
    protected String getFormattedBody(String selector, Element root,
        List<FieldExclude> excludes, List<FieldFilter> filters, boolean debug)
    {
        ElementType lastType = null;
        List<Element> elements;
        if(selector.equals(ROOT)) // The parent is the root node itself
        {
            elements = new ArrayList<Element>();
            elements.add(root);
        }
        else
        {
            elements = root.select(selector);
        }

        if(debug)
            logger.info(String.format("1: getFormattedBody: elements=%d", elements.size()));

        BodyParser parser = new BodyParser(excludes, filters, debug);
        for(Element element : elements)
            parser.parseHtml(element.html());

        String ret = parser.formatBody();
        if(debug)
            logger.info(String.format("2: getFormattedBody: ret=%s ret.length=%d",
                ret, ret.length()));

        return ret;
    }

    /**
     * Process the body field.
     */
    protected String getBody(Field field, Element root, String type, boolean debug)
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
    protected String getImageSrc(Field field, Element root, String type)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for image for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            if(selector.getSource().isPage())
            {
                Element image = root.select(selector.getExpr()).first();
                if(image != null)
                {
                    if(selector.getAttribute() != null && selector.getAttribute().length() > 0)
                    {
                        ret = getValue(field, image.attr(selector.getAttribute()));
                        if(debug())
                            logger.info("Found image "+selector.getAttribute()+" for "+type+" field "+field.getName()+": "+ret);
                        break;
                    }
                    else if(image.hasAttr("srcset"))
                    {
                        String srcset = getValue(field, image.attr("srcset"));
                        String[] items = srcset.split(", ");

                        // Process each srcset item to extract the url and size
                        Map<String,String> map = new HashMap<String,String>();
                        List<String> list = new ArrayList<String>();
                        for(String item : items)
                        {
                            item = item.trim();
                            String size = null;
                            String url = item;

                            int pos = item.indexOf(" "); // separator
                            if(pos != -1)
                            {
                                size = item.substring(pos+1).trim();
                                url = item.substring(0, pos);
                            }

                            list.add(url);
                            if(size != null)
                                map.put(size, url);
                        }

                        // Try to look up the item by its size
                        if(selector.hasSize())
                            ret = map.get(selector.getSize());

                        // Default to first url if size not found
                        if(ret == null)
                            ret = list.get(0);

                        if(debug())
                            logger.info("Found image srcset for "+type+" field "+field.getName()
                                +" size="+selector.getSize()+": "+ret);
                        break;
                    }
                    else if(image.hasAttr("src"))
                    {
                        ret = getValue(field, image.attr("src"));
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
                    if(ret.length() > 0)
                    {
                        if(debug())
                            logger.info("Found image metatag for "+type+" field "+field.getName()+": "+ret);
                        break;
                    }
                }
            }
        }

        if(ret != null)
        {
            // Remove any relative paths
            ret = ret.replaceAll("\\.\\./", "");
        }
        else
        {
            logger.warning("Image not found for "+type+" field: "+field.getName());
        }

        return ret;
    }

    /**
     * Returns the style attribute of an element.
     */
    protected String getStyle(Field field, Element root, String type)
    {
        String ret = null;

        if(debug())
            logger.info("Looking for style for "+type+" field: "+field.getName());

        for(FieldSelector selector : field.getSelectors())
        {
            Element element = root.select(selector.getExpr()).first();
            if(element != null)
            {
                ret = getValue(field, element.attr("style"));
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