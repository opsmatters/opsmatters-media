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
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.format.DateTimeParseException;
import java.time.Month;
import java.time.format.TextStyle;
import org.apache.commons.logging.LogFactory;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.ScriptException;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.WordUtils;
import com.google.common.net.UrlEscapers;
import com.opsmatters.media.config.content.WebPageConfiguration;
import com.opsmatters.media.config.content.LoadingConfiguration;
import com.opsmatters.media.config.content.MoreLinkConfiguration;
import com.opsmatters.media.model.app.TraceObject;
import com.opsmatters.media.model.content.ContentSummary;
import com.opsmatters.media.model.content.ContentField;
import com.opsmatters.media.model.content.ContentFields;
import com.opsmatters.media.model.content.ContentFieldMatch;
import com.opsmatters.media.model.content.ContentFieldCase;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a crawler for content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentCrawler<T extends ContentSummary>
{
    private static final Logger logger = Logger.getLogger(ContentCrawler.class.getName());

    public static final String ROOT = "<root>";
    public static final String CURRENT_DAY = "current-day";
    public static final String CURRENT_MONTH = "current-month";
    public static final String CURRENT_MONTH_NAME = "current-month-name";
    public static final String CURRENT_YEAR = "current-year";
    public static final String URL_CONTEXT = "url-context";
    public static final String IMAGE_NAME = "image-name";
    public static final String IMAGE_EXT = "image-ext";

    private WebClient client;
    private String name = "";
    private boolean initialised = false;
    private boolean debug = false;
    private TraceObject traceObject = TraceObject.NONE;
    private int maxResults = 0;
    private WebPageConfiguration config;
    private List<T> content = new ArrayList<T>();
    protected HtmlPage page;

    protected Map<String, Object> properties = new HashMap<String, Object>();

    static
    {
        // Turn off CSS and JavaScript error logging
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    /**
     * Constructor that takes a name.
     */
    public ContentCrawler(WebPageConfiguration config)
    {
        setName(config.getName());
        this.config = config;

    }

    /**
     * Returns the htmlunit client with javascript enabled or disabled.
     */
    private WebClient getClient(boolean javascript)
    {
        if(client == null)
        {
            client = new WebClient();
            client.setJavaScriptTimeout(30000);
            client.getOptions().setTimeout(60000);
            client.getOptions().setRedirectEnabled(true);
            client.getOptions().setThrowExceptionOnFailingStatusCode(false);
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setCssEnabled(false);
            client.getOptions().setPrintContentOnFailingStatusCode(false);
            client.getCookieManager().setCookiesEnabled(false);
            client.getOptions().setUseInsecureSSL(true);

            // Change the window size for pages that execute javascript when the user scrolls down
            client.getCurrentWindow().setInnerHeight(60000);

            // Process all javascript requests synchonously
            client.setAjaxController(new NicelyResynchronizingAjaxController());
        }

        // Enable/disable javascript to improve page loading performance
        if(client.getOptions().isJavaScriptEnabled() != javascript)
            client.getOptions().setJavaScriptEnabled(javascript);

        // Initialise the format properties
        initFormatProperties();

        return client;
    }

    /**
     * Close the client and release resources.
     */
    public void close()
    {
        try
        {
            if(client != null)
                client.close();
        }
        catch(ScriptException e)
        {
            logger.warning(StringUtils.serialize(e));
        }
    }

    /**
     * Returns the htmlunit client with javascript disabled.
     */
    private WebClient getClient()
    {
        return getClient(false);
    }

    /**
     * Returns the name of the crawler.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the crawler.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the crawler.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
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
            if(obj instanceof HtmlPage)
            {
                ret = traceObject.isPages();
                if(debug())
                    logger.info("Trace pages: obj="+obj.getClass().getName()+" ret="+ret);
            }
            else if(obj instanceof DomNode)
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
     * Returns the teaser page loading configuration.
     */
    public LoadingConfiguration getTeaserLoading()
    {
        return config.getTeaserLoading();
    }

    /**
     * Returns <CODE>true</CODE> if javascript is enabled for the teasers.
     */
    public boolean isTeaserJavaScriptEnabled()
    {
        return config.getTeaserLoading() != null ? config.getTeaserLoading().isJavaScriptEnabled() : false;
    }

    /**
     * Returns <CODE>true</CODE> if javascript is enabled for the content items.
     */
    public boolean isContentJavaScriptEnabled()
    {
        return config.getContentLoading() != null ? config.getContentLoading().isJavaScriptEnabled() : false;
    }

    /**
     * Returns the teaser selections of the crawler.
     */
    public List<ContentFields> getTeaserFields()
    {
        return config.getTeaserFields();
    }

    /**
     * Returns the content page loading configuration.
     */
    public LoadingConfiguration getContentLoading()
    {
        return config.getContentLoading();
    }

    /**
     * Returns the content selection of the crawler.
     */
    public ContentFields getContentFields()
    {
        return config.getContentFields();
    }

    /**
     * Returns the maximum results of the crawler.
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * Sets the maximum results of the crawler.
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    /**
     * Returns the content items selected by the crawler.
     */
    public List<T> getContent()
    {
        return content;
    }

    /**
     * Adds a selected content item to the crawler.
     */
    public void addContent(T content)
    {
        if(maxResults == 0 || this.content.size() < maxResults)
            this.content.add(content);
    }

    /**
     * Returns <CODE>true</CODE> if parameters should be removed from the URL.
     */
    public boolean removeParameters()
    {
        return getContentLoading() != null ? getContentLoading().removeParameters() : true;
    }

    /**
     * Sets the default properties to be used in field formats.
     * <P>
     * Properties supported:
     * <ul>
     * <li> <strong>current-day</strong>: the current day of the month.
     * <li> <strong>current-month</strong>: the current month of the year.
     * <li> <strong>current-month-name</strong>: the name of the current month of the year.
     * <li> <strong>current-year</strong>: the current 4-digit year.
     * </ul>
     */
    private void initFormatProperties()
    {
        properties.clear();

        Calendar calendar = Calendar.getInstance();
        Month month = Month.of(calendar.get(Calendar.MONTH)+1); 
        properties.put(CURRENT_DAY, calendar.get(Calendar.DAY_OF_MONTH));
        properties.put(CURRENT_MONTH, calendar.get(Calendar.MONTH));
        properties.put(CURRENT_MONTH_NAME, month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        properties.put(CURRENT_YEAR, calendar.get(Calendar.YEAR));
    }

    /**
     * Adds the given property to be used in field formats.
     */
    protected void setFormatProperty(String name, String value)
    {
        properties.put(name, value);
    }

    /**
     * Returns the propert substitutor to be used in field formats.
     */
    protected StringSubstitutor getFormatSubstitutor()
    {
        return new StringSubstitutor(properties);
    }

    /**
     * Returns the given page with javascript enabled/disabled.
     */
    protected HtmlPage getPage(String url, boolean javascript) throws IOException
    {
        HtmlPage page = getClient(javascript).getPage(url);
        if(debug())
            logger.info("Loading page: "+page.getTitleText());
        return page;
    }

    /**
     * Returns the given page with javascript disabled.
     */
    protected HtmlPage getPage(String url) throws IOException
    {
        return getPage(url, false);
    }

    /**
     * Connect the client to the page indicated by the url.
     * <p>
     * Also optionally clicks a Load More button and waits for the loading delay if configured.
     */
    protected void connect() throws IOException
    {
        long now = System.currentTimeMillis();
        page = getPage(getUrl(), isTeaserJavaScriptEnabled());

        // Click a "Load More" button if configured
        if(getMoreLink() != null)
        {
            int count = getMoreLink().getCount();
            for(int i = 0; i < count; i++)
            {
                if(debug())
                    logger.info("More link click "+(i+1)+" of "+count);
                clickMoreLink(page, getMoreLink());
            }
        }

        // Wait for the javascript to load for the teasers
        loadPage(page, getTeaserLoading());

        // Trace to see the teaser page
        if(trace(page))
            logger.info("teaser-page="+page.asXml());

        if(debug())
            logger.info("Loaded page in: "+(System.currentTimeMillis()-now)+"ms");
        initialised = true;
    }

    /**
     * Click a Load More button after the initial page load.
     */
    protected void clickMoreLink(HtmlPage page, MoreLinkConfiguration moreLink) throws IOException
    {
        String selector = moreLink.getSelector();
        if(selector.length() > 0)
        {
            if(debug())
                logger.info("Looking for More link: "+selector);
            DomElement link = page.querySelector(selector);
            if(link != null)
            {
                if(debug())
                    logger.info("Found More link: "+selector);
                page = link.click();

                long wait = moreLink.getWait();
                if(wait > 0L)
                {
                    if(debug())
                        logger.info("Waiting after More link click: "+wait);
                    getClient(true).waitForBackgroundJavaScript(wait);
                    if(debug())
                        logger.info("Finished waiting after More link click");
                }
            }
            else
            {
                logger.warning("More link not found: "+selector);
            }
        }
    }

    protected void loadPage(HtmlPage page, LoadingConfiguration loading)
    {
        if(page == null || loading == null)
            return;

        // Wait for the configured interval before parsing the page
        boolean javascript = loading.isJavaScriptEnabled();
        String selector = loading.getSelector();
        long interval = loading.getInterval();
        long max = loading.getMaxWait();
        if(selector.length() > 0 && interval > 0L)
        {
            long tm = 0L;
            while(tm <= max)
            {
                DomElement element = page.querySelector(selector);
                if(element != null)
                {
                    if(debug())
                        logger.info("Found selector: "+selector);
                    break;
                }
                else
                {
                    if(debug())
                        logger.info("Selector not found, waiting for background javascript: "+interval);
                    getClient(javascript).waitForBackgroundJavaScript(interval);
                    if(debug())
                        logger.info("Finished waiting for background javascript");
                    tm += interval;
                }
            }
        }

        long wait = loading.getWait();
        if(wait > 0L)
        {
            if(debug())
                logger.info("Waiting for background javascript: "+wait);
            getClient(javascript).waitForBackgroundJavaScript(wait);
            if(debug())
                logger.info("Finished waiting for background javascript");
        }
    }

    /**
     * Create a content summary from a selected node.
     */
    public abstract T getContentSummary(DomNode result, ContentFields fields) throws DateTimeParseException;

    /**
     * Process all the configured teaser fields.
     */
    public void processTeaserFields() throws IOException, DateTimeParseException
    {
        if(getUrl().length() == 0)
            throw new IllegalArgumentException("Root empty for teasers");

        if(!initialised)
            connect();

        // Process selections
        Map<String,String> map = new HashMap<String,String>();
        for(ContentFields fields : getTeaserFields())
        {
            DomNodeList<DomNode> results = page.querySelectorAll(fields.getRoot());
            if(debug())
                logger.info("Found "+results.size()+" teasers for teasers: "+fields.getRoot());
            for(DomNode result : results)
            {
                // Trace to see the teaser root node
                if(trace(result))
                    logger.info("teaser-node="+result.asXml());

                T content = getContentSummary(result, fields);
                if(content.isValid() && !map.containsKey(content.getUniqueId()))
                {
                    addContent(content);
                    map.put(content.getUniqueId(), content.getUniqueId());
                }
            }
        }

        if(debug())
            logger.info("Found "+getContent().size()+" items");
    }

    /**
     * Apply the configured regular expression to the given field value.
     */
    public String getValue(ContentField field, String value)
    {
        String ret = value;

        if(field.getTextCase() != ContentFieldCase.NONE)
        {
            if(debug())
                logger.info(String.format("Changing case for field %s: ret=[%s], case=[%s]", 
                        field.getName(), ret, field.getTextCase()));

            if(field.getTextCase() == ContentFieldCase.LOWER)
                ret = ret.toLowerCase();
            else if(field.getTextCase() == ContentFieldCase.UPPER)
                ret = ret.toUpperCase();
            else if(field.getTextCase() == ContentFieldCase.CAPITALIZE)
                ret = WordUtils.capitalizeFully(ret);

            if(debug())
                logger.info(String.format("Changed case for field %s: ret=[%s], case=[%s]", 
                        field.getName(), ret, field.getTextCase()));
        }

        Pattern pattern = field.getExprPattern();
        if(pattern != null)
        {
            if(debug())
                logger.info(String.format("Evaluating field %s: pattern=[%s] value=[%s] match=[%s]", 
                    field.getName(), pattern.pattern(), ret, field.getMatch()));

            Matcher m = pattern.matcher(ret);

            if(m.find())
            {
                // Get the field format (can including properties)
                String format = "$1";
                if(field.hasFormat())
                {
                    format = getFormatSubstitutor().replace(field.getFormat());
                }

                // Make the necessary field replacements
                if(field.getMatch() == ContentFieldMatch.ALL)
                    ret = m.replaceAll(format);
                else
                    ret = m.replaceFirst(format);

                if(debug())
                    logger.info(String.format("Match found for field %s: ret=[%s] match=[%s]", 
                        field.getName(), ret, field.getMatch()));
            }
            else
            {
                logger.warning(String.format("No match found for field %s: value=[%s]", 
                    field.getName(), ret));
            }
        }

        return ret;
    }

    /**
     * Returns a list of the metatags for the given attribute name and value.
     */
    protected List<HtmlMeta> getMetatags(HtmlPage page, String name, String value)
    {
        List<HtmlMeta> ret = new ArrayList<HtmlMeta>();
        List<DomElement> tags = getChildElementsRecursive(page.getDocumentElement(), HtmlMeta.class);

        for(DomElement tag : tags)
        {
            String attr = tag.getAttribute(name);
            if(attr != null && attr.equals(value))
            {
                ret.add((HtmlMeta)tag);
                if(debug())
                    logger.info("Found metatag "+name+" "+value+": "+tag.getAttribute("content"));
            }
        }

        return ret;
    }

    /**
     * Traverse the children of startElement and its children looking for instances of the given class.
     * @param startElement the parent element
     * @param clazz the class to search for
     * @return {@code null} if no child found
     */
    private List<DomElement> getChildElementsRecursive(DomElement startElement, Class<?> clazz)
    {
        if(startElement == null)
            return null;

        List<DomElement> ret = new ArrayList<DomElement>();
        for(DomElement element : startElement.getChildElements())
        {
            if(clazz.isInstance(element))
                ret.add(element);

            traverseChildElementsRecursive(element, clazz, ret);
        }

        return ret;
    }

    /**
     * Traverse the children of startElement and its children looking for instances of the given class.
     * @param startElement the parent element
     * @param clazz the class to search for
     * @param list the list of elements matching the given class
     */
    private void traverseChildElementsRecursive(DomElement startElement, Class<?> clazz, List<DomElement> list)
    {
        if(startElement == null)
            return;

        for(DomElement element : startElement.getChildElements())
        {
            if(clazz.isInstance(element))
                list.add(element);

            traverseChildElementsRecursive(element, clazz, list);
        }
    }

    /**
     * Returns the content of the first "property" metatag for the given attribute value.
     */
    protected String getPropertyMetatag(HtmlPage page, String value)
    {
        List<HtmlMeta> tags = getMetatags(page, "property", value);
        if(tags.size() == 0)
            tags = getMetatags(page, "name", value); // Sometimes og tags called "name" instead of "property"
        if(tags.size() > 0)
            return tags.get(0).getContentAttribute();
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the content validator is found.
     */
    protected void validateContent(T content, String validator, DomNode root, String type)
    {
        DomElement element = root.querySelector(validator);
        content.setValid(element != null);
        if(element != null)
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
    protected String getElements(ContentField field, DomNode root, HtmlPage page, String type,
        boolean multiple, String separator)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for elements for "+type+" field: "+field.getName());

            DomNodeList<DomNode> nodes = root.querySelectorAll(field.getSelector());
            if(nodes.size() > 0)
            {
                int i = 0; 
                StringBuilder str = new StringBuilder();
                for(DomNode node : nodes)
                {
                    String value = null;
                    DomElement element = (DomElement)node;
                    if(field.getAttribute() != null && field.getAttribute().length() > 0)
                        value = element.getAttribute(field.getAttribute());
                    else
                        value = element.asText();
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

            String tag = getPropertyMetatag(page, field.getSelector());
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
    protected String getElement(ContentField field, DomNode root, HtmlPage page, String type)
    {
        return getElements(field, root, page, type, false, null);
    }

    /**
     * Process an anchor field.
     */
    protected String getAnchor(ContentField field, DomNode root, HtmlPage page, String type, boolean removeParameters)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for anchor for "+type+" field: "+field.getName());

            HtmlAnchor anchor = null;
            HtmlDivision div = null;
            if(field.getSelector().equals(ROOT)) // The anchor is the root node itself
            {
                if(root instanceof HtmlAnchor)
                    anchor = (HtmlAnchor)root;
                else if(root instanceof HtmlDivision)
                    div = (HtmlDivision)root;
            }
            else
            {
                HtmlElement element = (HtmlElement)root.querySelector(field.getSelector());
                if(element instanceof HtmlAnchor)
                    anchor = (HtmlAnchor)element;
                else if(element instanceof HtmlDivision)
                    div = (HtmlDivision)element;
            }

            if(anchor != null)
            {
                ret = FormatUtils.getFormattedUrl(getBasePath(), anchor.getHrefAttribute(), removeParameters);

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

            String tag = getPropertyMetatag(page, field.getSelector());
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
     * Coalesces the given paragraphs into a single string up to the maximum length.
     */
    protected String getFormattedSummary(DomNodeList<DomNode> paragraphs, int maxLength)
    {
        StringBuilder ret = new StringBuilder();

        for(DomNode paragraph : paragraphs)
        {
            String text = paragraph.asText().trim();
            if(text.length() > 0)
                text = String.format("<p>%s</p>", text);
            if(ret.length() > 0 && (ret.length()+text.length()) > maxLength)
                break;

            if(ret.length() > 0)
                ret.append(" ");
            ret.append(FormatUtils.getFormattedSummary(text));
        }

        return ret.toString();
    }

    /**
     * Process the body field to produce a summary.
     */
    protected String getBodySummary(ContentField field, DomNode root, HtmlPage page, String type, int maxLength)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for body summary for "+type+" field: "+field.getName());

            String body = getFormattedSummary(root.querySelectorAll(field.getSelector()), maxLength);
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

            String tag = getPropertyMetatag(page, field.getSelector());
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
    protected String getFormattedParagraphs(DomNodeList<DomNode> paragraphs, Pattern stopExprPattern)
    {
        StringBuilder ret = new StringBuilder();

        for(DomNode paragraph : paragraphs)
        {
            String text = paragraph.asText().trim();
            String tag = paragraph.getNodeName();

            if(text.length() == 0)
                continue;

            // Stop if the text matches the stop expression
            if(stopExprPattern != null && stopExprPattern.matcher(text).matches())
                break;

            if(ret.length() > 0)
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
    protected String getBody(ContentField field, DomNode root, HtmlPage page, String type)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for body for "+type+" field: "+field.getName());

            String body = getFormattedParagraphs(root.querySelectorAll(field.getSelector()),
                field.getStopExprPattern());
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

            String tag = getPropertyMetatag(page, field.getSelector());
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
    protected String getImageSrc(ContentField field, DomNode root, HtmlPage page, String type)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for image for "+type+" field: "+field.getName());

            HtmlImage image = root.querySelector(field.getSelector());
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
                    ret = getValue(field, image.getSrcAttribute());

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

            String tag = getPropertyMetatag(page, field.getSelector());
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
    protected String getStyle(ContentField field, DomNode root, String type)
    {
        String ret = null;

        if(field.getSource().isPage())
        {
            if(debug())
                logger.info("Looking for style for "+type+" field: "+field.getName());

            DomElement element = root.querySelector(field.getSelector());
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