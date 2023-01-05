/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.crawler.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import com.opsmatters.media.model.content.crawler.field.FieldExclude;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;
import com.opsmatters.media.model.content.crawler.field.FilterResult;
import com.opsmatters.media.model.content.SummaryConfig;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.crawler.parser.ElementType.*;
import static com.opsmatters.media.crawler.parser.ElementDisplay.*;
import static com.opsmatters.media.model.content.crawler.field.FilterScope.*;
import static com.opsmatters.media.model.content.crawler.field.FilterResult.*;

/**
 * Class representing a parser for an article body.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BodyParser
{
    private static final Logger logger = Logger.getLogger(BodyParser.class.getName());

    private BodyElement previous = null;
    private List<BodyElement> elements = new ArrayList<BodyElement>();
    private List<FieldExclude> excludes;
    private List<FieldFilter> filters;
    private boolean converted = false;
    private boolean debug = false;

    /**
     * Constructor that takes a list of excludes, filters and debug flag.
     */
    public BodyParser(List<FieldExclude> excludes, List<FieldFilter> filters, boolean debug)
    {
        setExcludes(excludes);
        setFilters(filters);
        setDebug(debug);
    }

    /**
     * Constructor that takes a text string, filters and debug flag.
     */
    public BodyParser(String text, List<FieldFilter> filters, boolean debug)
    {
        this((List<FieldExclude>)null, filters, debug);
        if(text.indexOf("<p") != -1) // Contains markup
            parseHtml(text);
        else
            parseText(text);
    }

    /**
     * Constructor that takes a text string and debug flag.
     */
    public BodyParser(String text, boolean debug)
    {
        this(text, null, debug);
    }

    /**
     * Returns the list of exclude tags and classes.
     */
    public List<FieldExclude> getExcludes()
    {
        return excludes;
    }

    /**
     * Sets the list of exclude tags and classes.
     */
    public void setExcludes(List<FieldExclude> excludes)
    {
        this.excludes = excludes;
    }

    /**
     * Returns the list of filters for the parser.
     */
    public List<FieldFilter> getFilters()
    {
        return filters;
    }

    /**
     * Sets the list of filters for the parser.
     */
    public void setFilters(List<FieldFilter> filters)
    {
        this.filters = filters;
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Sets to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Returns <CODE>true</CODE> if the text was converted to HTML.
     */
    public boolean converted()
    {
        return converted;
    }

    /**
     * Parse the given HTML text and add to the list of body elements.
     */
    public void parseHtml(String html)
    {
        previous = null;

        Document doc = Jsoup.parse(html);
        doc.outputSettings().prettyPrint(false);
        parseNode(doc.getElementsByTag("body").get(0));

        if(debug)
        {
            logger.info(String.format("parseHtml: elements=%d", elements.size()));
            for(BodyElement element : elements)
            {
                logger.info(String.format("parseHtml: element: tag=%s type=%s text=%s strong=%b display=%s",
                    element.getTag(), element.getType(), element.getText(), element.isStrong(), element.getDisplay()));
            }
        }
    }

    /**
     * Parse the given node and add to the list of body elements.
     */
    public void parseHtml(Node node)
    {
        for(Node child : node.childNodes())
            parseNode(child);
    }

    /**
     * Parse the given node and add to the list of body elements.
     */
    private void parseNode(Node node)
    {
        String tag = node.nodeName();

        // Preprocess the node as wholeText() removes line breaks
        String oldHtml = null;
        if(node instanceof Element)
        {
            Element element = (Element)node;
            String html = element.html();
            if(html.indexOf("<br>") != -1)
            {
                element.html(html.replaceAll("<br>","\n"));
                oldHtml = html;
            }
        }

        // Apply the excludes to filter out particular nodes`
        if(FieldExclude.apply(getExcludes(), node))
            return;

        if(node.childNodeSize() == 0
            || (!tag.equals("body") && node.childNodeSize() == 1 && node.childNode(0) instanceof TextNode)
            || isLeafNode(tag))
        {
            boolean inline = true;
            String text = getText(node);
            if(text != null) // eg. a comment
                text = text.trim();
            else // eg. a comment
                return;

            if(debug)
                logger.info("parseNode:1: tag="+tag+" text="+text);

            // Add <br> to LF
            if(tag.equals("p"))
                text = text.replaceAll("\n(.+)","\n<br>$1");

            if(debug)
                logger.info("parseNode:2: tag="+tag+" text="+text);

            // If it's the first item or coming after a linefeed, it can't be inline
            if(elements.size() == 0 || text.startsWith("\n")
                || (previous != null && previous.getDisplay() == BLOCK))
            {
                inline = false;
            }

            String[] strings = text.split("\n", -1); // Include trailing empty strings

            // is this a strong paragraph?
            boolean strong = count(strings) == 1 && isStrong(node);

            for(int i = 0; i < strings.length; i++)
            {
                String string = strings[i];

                // Convert nbsp to space
                string = string.replaceAll("\u00A0"," ");

                // Replace "thin" spaces with normal space
                string = string.replaceAll("\\u2005|\\u2009|\\u202F", " ");

                // Collapse multiple spaces between end of sentence and punctuation
                string = string.replaceAll("(\\w+)[ ]+([\\.\\?!])","$1$2");

                // Collapse multiple spaces
                string = string.replaceAll("(\\S+)[ ]+(\\S+)","$1 $2");

                // Remove whitespace
                string = string.trim();

                strings[i] = string;

                if(debug)
                    logger.info("parseNode:3: i="+i+" tag="+tag+" i="+i+" string="+string+" strong="+strong);

                // Empty string is a linefeed
                if(string.length() == 0)
                {
                    // Is it at the end of a (non-empty) paragraph?
                    if(i == strings.length-1 && previous != null)
                        previous.setDisplay(BLOCK);
                    continue;
                }

                BodyElement element = new BodyElement(node.nodeName(), string, strong);

                // Start a new paragraph after a linefeed
                if(i > 0 || !inline)
                    element.setDisplay(INLINE_BLOCK);

                // If this is an <li> element, set the list type from the parent
                if(element.getType() == LIST && node.parentNode() != null)
                    element.setListType(node.parentNode().nodeName());

                if(debug)
                    logger.info("parseNode:4: i="+i+" tag="+tag+" text="+element.getText()+" type="+element.getType()
                        +" strong="+strong+" block="+element.isBlock()+" hasBR="+element.hasBR()+" previous="+previous);

                // If it's the first element or a block element
                if(previous != null && !element.isBlock())
                {
                    // Append the visible text to the previous element
                    previous.append(element);
                }
                else if(element.hasBR()                         // Treat <br> as a line break
                    && (i == 0
                        || (strings[i-1].length() > 0           // Treat \n\n<br> and \n<br>\n<br>
                            && !strings[i-1].equals("<br>")     //   as a paragraph instead
                            && element.getType() != TIMESTAMP)))
                {
                    previous.append("\n");
                    if(element.hasBR())
                        previous.appendBR();
                    previous.append(element);
                }
                else
                {
                    // The previous element was a title if it was a strong, block element
                    //   and doesn't end with a full stop (making it a sentence instead).
                    if(previous != null && previous.getType() == TEXT 
                        && previous.isStrong() && !previous.getText().endsWith(".")
                        && previous.isBlock() && element.isBlock())
                    {
                        previous.setType(TITLE);
                    }

                    // Add the new element
                    previous = element;
                    elements.add(element);
                }
            }
        }
        else // Recurse through the child nodes
        {
            // Restore the original html content before recursing
            if(oldHtml != null)
                ((Element)node).html(oldHtml);

            for(Node child : node.childNodes())
                parseNode(child);
        }
    }

    /**
     * Returns the number of non-empty strings in the given array.
     */
    private int count(String[] strings)
    {
        int ret = 0;
        if(strings != null)
        {
            for(String string : strings)
            {
                if(string != null && string.length() > 0)
                    ++ret;
            }
        }

        return ret;
    }

    /**
     * Parse the given plain text and add to the list of body elements.
     */
    public void parseText(String text)
    {
        parseHtml(textToHtml(text));
        converted = true;
    }

    /**
     * Returns <CODE>true</CODE> if the given node has a 'strong' tag.
     */
    private boolean isStrong(Node node)
    {
        String str = node.toString();
        if(str.indexOf("<p") != -1)
            str = str.replaceAll("<p(?:.*?)>(.+)</p>", "$1");
        str = str.replaceAll("<br>", "");
        str = str.trim();
        return str.startsWith("<strong>") || str.startsWith("<b>");
    }

    /**
     * Returns <CODE>true</CODE> if the given node is a leaf and should not be traversed further.
     */
    private boolean isLeafNode(String tag)
    {
        return tag.equals("p") || tag.startsWith("h")
            || tag.equals("blockquote") || tag.equals("pre")
            || tag.equals("li") || tag.equals("table")
            || tag.equals("figure") || tag.equals("iframe");
    }

    /**
     * Returns the text of the given node.
     */
    private String getText(Node node)
    {
        if(node instanceof Element)
            return ((Element)node).wholeText();
        else if(node instanceof TextNode)
            return ((TextNode)node).getWholeText();
        return null;
    }

    /**
     * Returns the list of parsed elements.
     */
    public List<BodyElement> getElements()
    {
        return elements;
    }

    /**
     * Returns the number of parsed elements.
     */
    public int numElements()
    {
        return elements.size();
    }

    /**
     * Returns the list of body elements formatted as an article body.
     */
    public String formatBody()
    {
        StringBuilder ret = new StringBuilder();

        // Traverse the elements
        String listType = null;
        for(BodyElement element : elements)
        {
            String tag = element.getTag();
            String text = element.getText();

            // Apply the filters to skip elements or truncate the text
            FilterResult result = FieldFilter.apply(getFilters(), text, BODY);
            if(result == SKIP)
                continue;
            else if(result == STOP)
                break;

            if(ret.length() > 0 && text.length() > 0)
                ret.append("\n");

            // Replace URLs with hyperlinks
            text = StringUtils.replaceUrls(text);

            // Process the various element types
            if(element.getType() == LIST)
            {
                // Start a list
                if(listType == null && element.getListType() != null)
                {
                    listType = element.getListType();
                    ret.append(String.format("<p><%s>", listType));
                    if(ret.length() > 0)
                        ret.append("\n");
                }

                // Add a list item
                ret.append(String.format("<%s>%s", tag, text));
            }
            else
            {
                // Close a list
                if(listType != null)
                {
                    ret.append(String.format("</%s></p>\n", listType));
                    listType = null;
                }

                if(element.isStrong()) // 'Strong' element
                {
                    ret.append(String.format("<strong>%s</strong>", text));
                }
                else if(element.getType() == TITLE) // Title element
                {
                    ret.append(String.format("<%s>%s</%s>", tag, text, tag));
                }
                else // Text
                {
                    ret.append(String.format("<p>%s</p>", text));
                }
            }
        }

        // Close the list if the last element was a listitem
        if(listType != null)
            ret.append(String.format("\n</%s></p>", listType));

        return ret.toString();
    }

    /**
     * Returns the list of body elements formatted as an article summary.
     */
    public String formatSummary(int minLength, int maxLength)
    {
        StringBuilder ret = new StringBuilder();

        // Traverse the elements
        Boolean header = null;
        for(BodyElement element : elements)
        {
            if(element.getType() == TITLE)
            {
                if(header == null)
                    header = true;
            }
            else if(element.getType() == TEXT)
            {
                header = false;
            }

            if((header != null && header == true && element.getType() == TITLE)
                || element.getType() == QUOTE
                || element.getType() == PRE
                || element.getType() == LIST
                || element.getType() == TABLE
                || element.getType() == FIGURE
                || element.getType() == IFRAME
                || element.getType() == TIMESTAMP)
            {
                continue;
            }

            String text = element.getText();
            text = text.replaceAll("\n|<br>",""); //Remove linefeeds and breaks
            text = text.replaceAll("\\[.+\\]",""); // Remove references and asides
            text = text.replaceAll("_{2,}",""); // Remove underscores as separators

            // Apply the filters to skip elements or truncate the text
            FilterResult result = FieldFilter.apply(getFilters(), text, SUMMARY);
            if(result == SKIP)
                continue;
            else if(result == STOP)
                break;

            // Filter out text containing a URL
            if(StringUtils.extractUrl(text) != null)
            {
                text = getNonUrlText(text);
                if(text == null)
                    continue;
            }

            // Exit if we've hit a title
            if(element.getType() == TITLE)
            {
                if(debug)
                    logger.info(String.format("formatSummary:1: break1: ret=%s text.length=%d ret.length=%d",
                        ret, text.length(), ret.length()));
                break;
            }

//GERALD
//System.out.println("formatSummary:1: text="+text+" line="+text.indexOf("_"));
if(text.startsWith("#")  // hashtags
    || text.startsWith("~") // tildes
    || text.startsWith("_") // underscores
    || text.startsWith("=") // equals
    || text.indexOf("\u25ac") != -1) // "bar" character in title
{
//System.out.println("formatSummary:2: text="+text);
    continue;
}
            // Exit if the addition would take us over the maximum
            if(ret.length() > 0 && (ret.length()+text.length()) > maxLength)
            {
                if(debug)
                    logger.info(String.format("formatSummary:2: break2: ret=%s text.length=%d ret.length=%d",
                        ret, text.length(), ret.length()));
                break;
            }

            if(debug)
                logger.info(String.format("formatSummary:3: ret=%s text.length=%d ret.length=%d",
                    ret, text.length(), ret.length()));

            if(ret.length() > 0 && text.length() > 0)
                ret.append(" ");
            ret.append(text);

            if(debug)
                logger.info(String.format("formatSummary:4: ret=%s text.length=%d ret.length=%d",
                    ret, text.length(), ret.length()));

            // Exit if the addition has taken us over the minimum
            if(ret.length() > minLength)
            {
                if(debug)
                    logger.info(String.format("formatSummary:5: break3: ret=%s text.length=%d ret.length=%d",
                        ret, text.length(), ret.length()));
                break;
            }
        }

        // Summary should end with full stop if it ends with a semi-colon
        if(ret.length() > 0 && ret.charAt(ret.length()-1) == ':')
        {
            ret.setCharAt(ret.length()-1, '.');

            if(debug)
                logger.info(String.format("formatSummary:6: fixed ':': ret=%s ret.length=%d",
                    ret, ret.length()));
        }

        return ret.toString();
    }

    /**
     * Returns the non-URL text extracted from the given text.
     */
    private String getNonUrlText(String text)
    {
        StringBuilder ret = null;

        String text1 = null;
        String text2 = null;

        String[] strings = text.split("\\. "); // split on sentence end
        for(String string : strings)
        {
            if(StringUtils.extractUrl(string) == null)
            {
                if(ret == null)
                    ret = new StringBuilder();
                if(ret.length() > 0)
                    ret.append(" ");
                ret.append(string).append(".");
            }
        }

        return ret != null ? ret.toString() : null;
    }

    /**
     * Returns the list of body elements formatted as an article summary.
     */
    public String formatSummary(SummaryConfig config)
    {
        return formatSummary(config.getMinLength(), config.getMaxLength());
    }

    /**
     * Returns the given text formatted as HTML.
     * @param text The text to be formatted
     * @return The formatted text
     */
    public String textToHtml(String text)
    {
        String ret = text;

        if(ret != null && ret.length() > 0 && !ret.equals(StringUtils.EMPTY))
        {
            // Wrap in a <p> tag
            ret = String.format("<p>%s</p>", ret);

            // Turn rows of dashes or stars into paragraphs
            ret = ret.replaceAll("\n(-|\\*)+\n", "\n\n");

            // Turn "<br>-", "<br>--", "<br>*", "<br>**", "<br>•, <br>▶" into <li> tags for <ul> list
            ret = ret.replaceAll("(\n)+( )*(-|\\*|\\u2022|\\u25cf|\\u25b6|\\u25aa\\ufe0f)+( )*", "<li> ");

            // Turn "<br>1.", "<br>2.", "<br>3.", etc into <oli> tags to indicate an <ol> list
            ret = ret.replaceAll("(\n)+[ ]*\\d{1,2}\\.[ ]*", "<oli> ");

            // Turn multiple line breaks into <p> tags
            ret = ret.replaceAll("\n\\s*\n", "</p>\n<p>");

            // Remove empty <li> tags
            ret = ret.replaceAll("\n<li>\\s*\n", "\n");

            // Process bulleted lists <ul> and <ol>
            Pattern p = Pattern.compile("<p>(.*?)((?:<li>.*?)+)(\n<br>.*)*</p>(.*)", Pattern.DOTALL);
            if(ret.indexOf("<li>") != -1)  // unordered list
            {
                ret = formatList(p, "ul", ret);
            }
            else if(ret.indexOf("<oli>") != -1)  // ordered list
            {
                ret = ret.replace("<oli>", "<li>");
                ret = formatList(p, "ol", ret);
            }
        }

        return ret;
    }

    /**
     * Returns the given string formatted as a HTML list.
     * @param p The pattern to use for matching elements
     * @param listTag Either "ul" or "ol"
     * @param str The string to be formatted
     * @return The formatted list
     */
    private String formatList(Pattern p, String listTag, String str)
    {
        String ret = str;

        if(str != null && str.length() > 0)
        {
            Matcher m = p.matcher(str);
            if(m.find())
            {
                String prefix = m.group(1).trim();  // The text before the list
                String items = m.group(2).trim();   // The <li> items
                String suffix1 = m.group(3);        // The text after the <li> items, but inside the <p>
                if(suffix1 == null)
                    suffix1 = "";
                suffix1 = suffix1.trim();
                if(suffix1.length() > 0)
                    suffix1 = String.format("\n<p>%s</p>", suffix1.trim());
                String suffix2 = m.group(4);        // The text after the <p>
                if(suffix2 == null)
                    suffix2 = "";
                suffix2 = suffix2.trim();
                if(suffix2.length() > 0)
                {
                    suffix2 = formatList(p, listTag, suffix2.trim()).trim();
                    if(!suffix2.startsWith("<p>"))
                        suffix2 = String.format("<p>%s</p>", suffix2);
                    if(!suffix2.startsWith("\n"))
                        suffix2 = String.format("\n%s", suffix2);
                }

                // Wrap the list in <ul> tags
                ret = String.format("<p>%s\n<%s>\n%s\n</%s></p>%s%s", 
                    prefix, listTag, items, listTag, suffix1, suffix2);
            }
        }

        return ret;
    }
}