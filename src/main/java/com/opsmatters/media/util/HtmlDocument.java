/*
 * Copyright 2024 Gerald Curley
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

package com.opsmatters.media.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.opsmatters.media.file.FileFormat;

/**
 * A set of utility methods to perform miscellaneous tasks related to HTML links.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class HtmlDocument
{

    private static final String START_ATTR = " start=";
    private static final String STYLE_ATTR = " style=";

    public static final String POST_FULL_CONTENT_CLASS = "post-full-content";
    private static final String IMAGE_WRAPPER_CLASS = "image-wrapper";
    private static final String IMAGE_LEGEND_CLASS = "image-legend";
    private static final String IMAGE_STYLE = "margin:15px 0;";

    private static final String NBSP_CHAR = "\u00a0";
    private static final String NBSP_ENTITY = "&nbsp;";
    private static final String LF_ENTITY = "&#013;";

    private static final String BEFORE_CHARS = " ('\"“"+NBSP_CHAR+LF_ENTITY;
    private static final String AFTER_CHARS = " !:;,.'\"”?)"+NBSP_CHAR;
    private static final String BEFORE_ENTITIES = "&ldquo;"+NBSP_ENTITY;
    private static final String AFTER_ENTITIES = "&rdquo;"+NBSP_ENTITY;

    private static final String HTTP_PROTOCOL = "http:";
    private static final String HTTPS_PROTOCOL = "https:";

    private static final String LINE_BREAKS = "(<br[ /]*>){2,}";
    private static final String NEW_PARAGRAPH = "</p>\n<p>";
    private static final String OPEN_SPAN = "<span.*?>";
    private static final String CLOSE_SPAN = "</span>";

    private static Pattern DATA_ATTR_PATTERN = Pattern.compile(" data-[-\\w]+=", Pattern.DOTALL);
    private static Pattern DIR_ATTR_PATTERN = Pattern.compile(" dir=\"[lr]t[rl]\"", Pattern.DOTALL);
    private static Pattern ANCHOR_TEXT_PATTERN = Pattern.compile("(.*?)(<a.*?>.*?</a>)(.*)", Pattern.DOTALL);
    private static Pattern ANCHOR_LEADING_TEXT_PATTERN = Pattern.compile("(.*?)<a(.*?)>(.*?)</a>", Pattern.DOTALL);
    private static Pattern BEFORE_TEXT_PATTERN = Pattern.compile(".*(&\\w+;)$", Pattern.DOTALL);
    private static Pattern AFTER_TEXT_PATTERN = Pattern.compile("^(&\\w+;).*", Pattern.DOTALL);
    private static Pattern ANCHOR_ATTR_PATTERN = Pattern.compile("<a(.*?)>", Pattern.DOTALL);
    private static Pattern ANCHOR_ATTR_CONTENT_PATTERN = Pattern.compile("<a(.*?)>(.+?)</a>", Pattern.DOTALL);
    private static Pattern HREF_PATTERN = Pattern.compile(" href=\"(.+?)\"", Pattern.DOTALL);
    private static Pattern SPAN_PATTERN = Pattern.compile(OPEN_SPAN, Pattern.DOTALL);
    private static Pattern DIV_CONTENT_PATTERN = Pattern.compile("<div>(.+?)</div>", Pattern.DOTALL);
    private static Pattern SECTION_CONTENT_PATTERN = Pattern.compile("<section>(.+?)</section>", Pattern.DOTALL);
    private static Pattern H1_CONTENT_PATTERN = Pattern.compile("<h1.*?>(.+?)</h1>", Pattern.DOTALL);
    private static Pattern OL_ATTR_CONTENT_PATTERN = Pattern.compile("<ol(.*?)>(.*?)</ol>", Pattern.DOTALL);
    private static Pattern LI_PATTERN = Pattern.compile("<li>", Pattern.DOTALL);
    private static Pattern SOURCE_PATTERN = Pattern.compile("^\\w*[ ]?source:", Pattern.DOTALL); // eg. Image Source
    private static Pattern FRAGMENTED_UL_LIST_PATTERN = Pattern.compile("</li>[\r\n]*</ul>[\r\n]*<ul.*?>[\r\n]*<li>", Pattern.DOTALL);
    private static Pattern FRAGMENTED_OL_LIST_PATTERN = Pattern.compile("</li>[\r\n]*</ol>[\r\n]*<ol.*?>[\r\n]*<li>", Pattern.DOTALL);
    private static Pattern LINE_BREAKS_PATTERN = Pattern.compile(LINE_BREAKS, Pattern.DOTALL);
    private static Pattern FIGCAPTION_PATTERN = Pattern.compile("<figcaption(.*?)>(.+?)</figcaption>", Pattern.DOTALL);

    private String doc = "";
    private String[] tags;

    /**
     * Constructor that takes a HTML document.
     */
    private HtmlDocument(String doc)
    {
        this.doc = doc;
    }    

    /**
     * Returns the HTML document.
     * @return The HTML document
     */
    public String get()
    {
        return doc;
    }

    /**
     * Sets the tags that will used to search the HTML document.
     * @param tags The array of tags used to search the document
     */
    public void setTags(String[] tags)
    {
        this.tags = tags;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains an unnecessary attribute.
     * @return <CODE>true</CODE> if the HTML document contains an unnecessary attribute.
     */
    public boolean hasUnnecessaryAttribute()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = hasUnnecessaryAttribute(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains an unnecessary attribute.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document contains an unnecessary attribute.
     */
    private boolean hasUnnecessaryAttribute(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>", tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String attr = m.group(1);

            ret = DATA_ATTR_PATTERN.matcher(attr).find();
            if(!ret)
                ret = DIR_ATTR_PATTERN.matcher(attr).find();
        }

        return ret;
    }

    /**
     * Removes unnecessary attributes from the HTML document.
     */
    public void removeUnnecessaryAttributes()
    {
        if(tags != null)
        {
            for(String tag : tags)
                removeUnnecessaryAttributes(tag);
        }
    }

    /**
     * Removes unnecessary attributes from the HTML document.
     * @param tag The tag to look for
     */
    private void removeUnnecessaryAttributes(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>", tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);

            boolean changed = false;

            // Remove data- attributes, with and without values
            if(DATA_ATTR_PATTERN.matcher(attr).find())
            {
                attr = attr.replaceAll(" data-[-\\w]+=\".*?\"", "");
                attr = attr.replaceAll(" data-[-\\w]+", "");
                changed = true;
            }

            // Remove dir attribute
            if(DIR_ATTR_PATTERN.matcher(attr).find())
            {
                attr = attr.replaceAll(" dir=\"[lr]t[rl]\"", "");
                changed = true;
            }

            if(changed)
            {
                doc = doc.replace(whole, String.format("<%s%s>", tag, attr));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains unnecessary spacing.
     * @return <CODE>true</CODE> if the HTML document contains unnecessary spacing.
     */
    public boolean hasUnnecessarySpacing()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = hasUnnecessarySpacing(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains unnecessary spacing.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document contains unnecessary spacing.
     */
    private boolean hasUnnecessarySpacing(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String attr = m.group(1);
            String content = m.group(2);

            ret = content.startsWith(" ") || content.endsWith(" ")
                || content.startsWith(NBSP_CHAR) || content.endsWith(NBSP_CHAR)
                || content.startsWith(NBSP_ENTITY) || content.endsWith(NBSP_ENTITY);
        }

        return ret;
    }

    /**
     * Removes unnecessary spacing from the HTML document.
     */
    public void removeUnnecessarySpacing()
    {
        if(tags != null)
        {
            for(String tag : tags)
                removeUnnecessarySpacing(tag);
        }
    }

    /**
     * Removes unnecessary spacing from the HTML document.
     * @param tag The tag to look for
     */
    private void removeUnnecessarySpacing(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);


            boolean changed = false;

            while(content.startsWith(NBSP_CHAR))
            {
                content = content.substring(NBSP_CHAR.length());
                changed = true;
            }

            while(content.endsWith(NBSP_CHAR))
            {
                content = content.substring(0, content.length()-NBSP_CHAR.length());
                changed = true;
            }

            while(content.startsWith(NBSP_ENTITY))
            {
                content = content.substring(NBSP_ENTITY.length());
                changed = true;
            }

            while(content.endsWith(NBSP_ENTITY))
            {
                content = content.substring(0, content.length()-NBSP_ENTITY.length());
                changed = true;
            }

            if(content.startsWith(" ") || content.endsWith(" "))
            {
                content = content.trim();
                changed = true;
            }

            if(changed)
            {
                doc = doc.replace(whole, String.format("<%s%s>%s</%s>", tag, attr, content, tag));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains links with bad external spacing.
     * @return <CODE>true</CODE> if the HTML document contains links with bad external spacing.
     */
    public boolean hasBadExternalSpacingLink()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = hasBadExternalSpacingLink(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains links with bad external spacing.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document contains links with bad external spacing.
     */
    private boolean hasBadExternalSpacingLink(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s.*?>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String content = m.group(1);

            ret = hasBadExternalSpacingLinkFromContent(content);
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the content contains links with bad external spacing.
     * @param content The content to search
     * @return <CODE>true</CODE> if the content contains links with bad external spacing.
     */
    private boolean hasBadExternalSpacingLinkFromContent(String content)
    {
        boolean ret = false;

        if(content != null && content.length() > 0)
        {
            Matcher textMatcher = ANCHOR_TEXT_PATTERN.matcher(content);

            while(textMatcher.find() && !ret)
            {
                String before = textMatcher.group(1);
                String after = textMatcher.group(3);

                ret = needSpaceBefore(removeMarkup(before));
                if(!ret)
                {
                    ret = needSpaceAfter(removeMarkup(after));
                }

                if(!ret)
                {
                    // Search the rest of the string in case there are more anchors
                    ret = hasBadExternalSpacingLinkFromContent(after);
                }
            }
        }

        return ret;
    }

    /**
     * Fix the links with bad external spacing in the HTML document.
     */
    public void fixBadExternalSpacingLinks()
    {
        if(tags != null)
        {
            for(String tag : tags)
                fixBadExternalSpacingLinks(tag);
        }
    }

    /**
     * Fix the links with bad external spacing in the HTML document.
     * @param tag The tag to look for
     */
    private void fixBadExternalSpacingLinks(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String content = m.group(2);

            fixBadExternalSpacingLinksFromContent(content);
        }
    }

    /**
     * Fix the links with bad external spacing in the content.
     * @param content The content to search
     */
    private void fixBadExternalSpacingLinksFromContent(String content)
    {
        if(content != null && content.length() > 0)
        {
            Matcher textMatcher = ANCHOR_TEXT_PATTERN.matcher(content);

            while(textMatcher.find())
            {
                String before = textMatcher.group(1);
                String link = textMatcher.group(2);
                String after = textMatcher.group(3);

                boolean addBefore = needSpaceBefore(removeMarkup(before));
                boolean addAfter = needSpaceAfter(removeMarkup(after));

                if(addBefore || addAfter)
                {
                    String newContent = String.format("%s%s%s%s%s",
                        before,
                        addBefore ? " " : "",
                        link,
                        addAfter ? " " : "",
                        after);

                    doc = doc.replace(String.format(">%s<", content), String.format(">%s<", newContent));
                }

                fixBadExternalSpacingLinksFromContent(after);
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the given string before a link requires a space to be added.
     * @param before The string to check
     * @return <CODE>true</CODE> if the given string before a link requires a space to be added
     */
    private boolean needSpaceBefore(String before)
    {
        boolean ret = false;

        if(before.length() > 0)
        {
            char beforeChar = before.charAt(before.length()-1);

            Matcher beforeMatcher = BEFORE_TEXT_PATTERN.matcher(before);
            String beforeEntity = beforeMatcher.find() ? beforeMatcher.group(1) : "";

            if(beforeEntity.length() > 0)
                ret = BEFORE_ENTITIES.indexOf(beforeEntity) == -1; // try to match the entity first
            else
                ret = BEFORE_CHARS.indexOf(beforeChar) == -1;
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given string after a link requires a space to be prepended.
     * @param after The string to check
     * @return <CODE>true</CODE> if the given string after a link requires a space to be prepended
     */
    private boolean needSpaceAfter(String after)
    {
        boolean ret = false;

        if(after.length() > 0)
        {
            char afterChar = after.charAt(0);

            // Allow for plurals
            if(afterChar == 's' && after.length() > 1)
                afterChar = after.charAt(1);

            Matcher afterMatcher = AFTER_TEXT_PATTERN.matcher(after);
            String afterEntity = afterMatcher.find() ? afterMatcher.group(1) : "";

            if(afterEntity.length() > 0)
                ret = AFTER_ENTITIES.indexOf(afterEntity) == -1; // try to match the entity first
            else
                ret = AFTER_CHARS.indexOf(afterChar) == -1;
        }

        return ret;
    }

    /**
     * Returns the given string with all markup removed.
     * @return The given string with all markup removed
     */
    private static String removeMarkup(String str)
    {
        String ret = str;

        ret = ret.replaceAll("<br[ ]*/>", LF_ENTITY);
        ret = ret.replaceAll("<\\w+.*?>", "");
        ret = ret.replaceAll("</\\w+>", "");

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a link with anchor text with bad spacing.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains a link with anchor text with bad spacing.
     */
    public static boolean hasBadAnchorTextLink(String doc)
    {
        boolean ret = false;

        Matcher m = ANCHOR_ATTR_CONTENT_PATTERN.matcher(doc);
        while (m.find() && !ret)
        {
            String anchor = removeMarkup(m.group(2));

            if(anchor.startsWith(" ") || anchor.endsWith(" ")
                || anchor.startsWith(NBSP_CHAR) || anchor.endsWith(NBSP_CHAR)
                || anchor.startsWith(NBSP_ENTITY) || anchor.endsWith(NBSP_ENTITY))
            {
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Fix links with anchor text with bad spacing in the HTML document.
     */
    public void fixBadAnchorTextLinks()
    {
        Matcher m = ANCHOR_ATTR_CONTENT_PATTERN.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String anchor = m.group(2);


            if(anchor.startsWith(NBSP_CHAR) || anchor.endsWith(NBSP_CHAR)
                || anchor.startsWith(NBSP_ENTITY) || anchor.endsWith(NBSP_ENTITY))
            {
                anchor = replaceNbsps(anchor);
            }

            if(anchor.startsWith(" ") || anchor.endsWith(" "))
            {
                doc = doc.replace(whole, String.format("<a%s>%s</a>", attr, anchor.trim()));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a link using "http:".
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains a link using "http:"
     */
    public static boolean hasBadProtocolLink(String doc)
    {
        boolean ret = false;

        Matcher m = ANCHOR_ATTR_PATTERN.matcher(doc);

        while(m.find())
        {
            String attr = m.group(1);

            if(ret = attr.indexOf(HTTP_PROTOCOL) != -1)
            {
                break;
            }
        }

        return ret;
    }

    /**
     * Replaces links using "http:" in the HTML document with "https:".
     */
    public void fixBadProtocolLinks()
    {
        Matcher m = ANCHOR_ATTR_PATTERN.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);

            if(attr.indexOf(HTTP_PROTOCOL) != -1)
            {
                // Replace http:// in title and href in attributes
                doc = doc.replace(whole, String.format("<a%s>",
                    attr.replace(HTTP_PROTOCOL, HTTPS_PROTOCOL)));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains an "&amp;nbsp;" character.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains an "&amp;nbsp;" character.
     */
    public static boolean hasNbsp(String doc)
    {
        return doc.indexOf(NBSP_CHAR) != -1 || doc.indexOf(NBSP_ENTITY) != -1;
    }

    /**
     * Replaces "&amp;nbsp;" characters in the HTML document with normal spaces.
     */
    private void replaceNbsps()
    {
        doc = replaceNbsps(doc);
    }

    /**
     * Replaces "&amp;nbsp;" characters in the given string with normal spaces.
     */
    private String replaceNbsps(String str)
    {
        // Replace nbsp with space
        str = str.replaceAll(NBSP_CHAR, " ");
        str = str.replaceAll(NBSP_ENTITY, " ");

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a "&lt;span&gt;" tag.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains a "&lt;span&gt;" tag.
     */
    public static boolean hasSpan(String doc)
    {
        return SPAN_PATTERN.matcher(doc).find();
    }

    /**
     * Removes "&lt;span&gt;" tags in the HTML document.
     */
    private void removeSpans()
    {
        doc = doc.replaceAll(OPEN_SPAN, "");
        doc = doc.replace(CLOSE_SPAN, "");
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a "&lt;div&gt;" tag.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains a "&lt;div&gt;" tag.
     */
    public static boolean hasDiv(String doc)
    {
        return DIV_CONTENT_PATTERN.matcher(doc).find();
    }

    /**
     * Replaces "&lt;div&gt;" tags in the HTML document with &lt;p&gt; tags.
     */
    private void replaceDivs()
    {
        // Replace <div> with <p>
        doc = DIV_CONTENT_PATTERN.matcher(doc).replaceAll("<p>$1</p>");
    }

    /**
     * Removes "&lt;div&gt;" tags in the HTML document.
     */
    private void removeDivs()
    {
        doc = doc.replaceAll("^<div>\\s*", ""); // <div> at start of doc
        doc = doc.replaceAll("\\s*<div>", "");
        doc = doc.replaceAll("\\s*</div>", "");
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a "&lt;section&gt;" tag.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains a "&lt;section&gt;" tag.
     */
    public static boolean hasSection(String doc)
    {
        return SECTION_CONTENT_PATTERN.matcher(doc).find();
    }

    /**
     * Removes "&lt;section&gt;" tags in the HTML document.
     */
    private void removeSections()
    {
        doc = doc.replaceAll("^<section>\\s*", ""); // <section> at start of doc
        doc = doc.replaceAll("\\s*<section>", "");
        doc = doc.replaceAll("\\s*</section>", "");
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a list that is not contiguous.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains a list that is not contiguous.
     */
    public static boolean hasFragmentedList(String doc)
    {
        return FRAGMENTED_UL_LIST_PATTERN.matcher(doc).find()
            || FRAGMENTED_OL_LIST_PATTERN.matcher(doc).find();
    }

    /**
     * Collapses fragmented list in the HTML document.
     */
    private void fixFragmentedLists()
    {
        // Remove list open and close tags in the middle of another list
        doc = doc.replaceAll("(</li>[\r\n]*)</ul>[\r\n]*<ul.*?>[\r\n]*(<li>)", "$1$2");
        doc = doc.replaceAll("(</li>[\r\n]*)</ol>[\r\n]*<ol.*?>[\r\n]*(<li>)", "$1$2");
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains an ordered list with bad numbering.
     * @param doc The HTML document to search
     * @return <CODE>true</CODE> if the HTML document contains an ordered list with bad numbering
     */
    public static boolean hasBadOrderedList(String doc)
    {
        boolean ret = false;

        Matcher m = OL_ATTR_CONTENT_PATTERN.matcher(doc);

        boolean found = false;
        while(m.find())
        {
            String attr = m.group(1);
            String content = m.group(2);

            // Look for two successive <ol><li>...</li></ol> without a "start" attribute
            if(attr.indexOf(START_ATTR) == -1)
            {
                int count = getLiCount(content);
                if(count == 1)
                {
                    if(found)
                    {
                        ret = true;
                        break;
                    }
                    else
                    {
                        found = true;
                    }
                }
                else
                {
                    found = false;
                }
            }
            else
            {
                found = false;
            }
        }

        return ret;
    }

    /**
     * Returns the number of &lt;li&gt; occurrences in the given string.
     * @param str The string to search
     * @return The number of &lt;li&gt; occurrences in the given string
     */
    public static int getLiCount(String str)
    {
        int ret = 0;

        Matcher m = LI_PATTERN.matcher(str);
        while(m.find()) 
            ++ret;

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains extra line breaks.
     * @return <CODE>true</CODE> if the HTML document contains extra line breaks.
     */
    public boolean hasExtraLineBreaks()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = hasExtraLineBreaks(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains extra line breaks.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document contains extra line breaks.
     */
    private boolean hasExtraLineBreaks(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String content = m.group(2);

            ret = LINE_BREAKS_PATTERN.matcher(content).find();
        }

        return ret;
    }

    /**
     * Replaces extra line breaks in the HTML document with a new paragraph.
     */
    private void replaceExtraLineBreaks()
    {
        if(tags != null)
        {
            for(String tag : tags)
                replaceExtraLineBreaks(tag);
        }
    }

    /**
     * Replaces extra line breaks in the HTML document with a new paragraph.
     * @param tag The tag to look for
     */
    private void replaceExtraLineBreaks(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

            if(LINE_BREAKS_PATTERN.matcher(content).find())
            {
                // Replace multiple line breaks with new paragraph
                content = content.replaceAll(LINE_BREAKS, NEW_PARAGRAPH);
                doc = doc.replace(whole, String.format("<%s%s>%s</%s>", tag, attr, content, tag));
            }
        }
    }

    /**
     * Removes extra line breaks from the HTML document.
     */
    private void removeExtraLineBreaks()
    {
        if(tags != null)
        {
            for(String tag : tags)
                removeExtraLineBreaks(tag);
        }
    }

    /**
     * Removes extra line breaks from the HTML document.
     * @param tag The tag to look for
     */
    private void removeExtraLineBreaks(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

            if(LINE_BREAKS_PATTERN.matcher(content).find())
            {
                // Remove multiple line breaks
                content = content.replaceAll(LINE_BREAKS, "");
                doc = doc.replace(whole, String.format("<%s%s>%s</%s>", tag, attr, content, tag));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document needs the image-wrapper class.
     * @return <CODE>true</CODE> if the HTML document needs the image-wrapper class.
     */
    public boolean needsImageWrapperClass()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = needsImageWrapperClass(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document needs the image-wrapper class.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document needs the image-wrapper class.
     */
    private boolean needsImageWrapperClass(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String attr = m.group(1);
            String content = m.group(2);

            if(attr.indexOf(POST_FULL_CONTENT_CLASS) != -1)
                continue;

            if(content.indexOf("<img") != -1)
            {
                if(attr.indexOf(IMAGE_WRAPPER_CLASS) == -1)
                {
                    ret = true;
                }
            }
        }

        return ret;
    }

    /**
     * Adds the image wrapper class to images in the given string.
     */
    public void addImageWrapperClass()
    {
        if(tags != null)
        {
            for(String tag : tags)
                addImageWrapperClass(tag);
        }
    }

    /**
     * Adds the image wrapper class to images in the given string.
     * @param tag The tag to look for
     */
    private void addImageWrapperClass(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

            if(attr.indexOf(POST_FULL_CONTENT_CLASS) != -1)
                continue;

            if(content.indexOf("<img") != -1)
            {
                if(attr.indexOf(IMAGE_WRAPPER_CLASS) == -1)
                {
                    attr = String.format("%s class=\"%s\"", attr, IMAGE_WRAPPER_CLASS);
                    if(attr.indexOf(STYLE_ATTR) == -1)
                        attr = String.format("%s style=\"%s\"", attr, IMAGE_STYLE);
                    doc = doc.replace(whole, String.format("<%s%s>%s</%s>", tag, attr, content, tag));
                }
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document needs the image-legend class.
     * @return <CODE>true</CODE> if the HTML document needs the image-legend class.
     */
    public boolean needsImageLegendClass()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = needsImageLegendClass(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document needs the image-legend class.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document needs the image-legend class.
     */
    private boolean needsImageLegendClass(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String content = m.group(2);

            Matcher captionMatcher = FIGCAPTION_PATTERN.matcher(content);

            if(captionMatcher.find())
            {
                String attr = captionMatcher.group(1);
                if(attr.indexOf(IMAGE_LEGEND_CLASS) == -1)
                {
                    ret = true;
                }
            }
        }

        return ret;
    }

    /**
     * Adds the image legend class to image captions in the given string.
     */
    public void addImageLegendClass()
    {
        if(tags != null)
        {
            for(String tag : tags)
                addImageLegendClass(tag);
        }
    }

    /**
     * Adds the image legend class to image captions in the given string.
     * @param tag The tag to look for
     */
    private void addImageLegendClass(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            Matcher captionMatcher = FIGCAPTION_PATTERN.matcher(m.group(2));

            if(captionMatcher.find())
            {
                String whole = captionMatcher.group(0);
                String attr = captionMatcher.group(1);
                String content = captionMatcher.group(2);
                if(attr.indexOf(IMAGE_LEGEND_CLASS) == -1)
                {
                    attr = String.format("%s class=\"%s\"", attr, IMAGE_LEGEND_CLASS);
                    doc = doc.replace(whole, String.format("<figcaption%s>%s</figcaption>", attr, content));
                }
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains a &lt;h1&gt; tag.
     * @param doc The string to search
     * @return <CODE>true</CODE> if the HTML document contains a &lt;h1&gt; tag.
     */
    public static boolean hasH1(String doc)
    {
        return H1_CONTENT_PATTERN.matcher(doc).find();
    }

    /**
     * Reduces the main headings by one level.
     */
    private void reduceHeadings()
    {
        doc = doc.replaceAll("<h5([ >])", "<h6$1");
        doc = doc.replaceAll("</h5>", "</h6>");

        doc = doc.replaceAll("<h4([ >])", "<h5$1");
        doc = doc.replaceAll("</h4>", "</h5>");

        doc = doc.replaceAll("<h3([ >])", "<h4$1");
        doc = doc.replaceAll("</h3>", "</h4>");

        doc = doc.replaceAll("<h2([ >])", "<h3$1");
        doc = doc.replaceAll("</h2>", "</h3>");

        doc = doc.replaceAll("<h1([ >])", "<h2$1");
        doc = doc.replaceAll("</h1>", "</h2>");
    }

    /**
     * Returns the list of duplicate link messages for the HTML document.
     * @param doc The HTML document to search
     * @return The list of duplicate link messages for the HTML document
     */
    public static List<String> getDuplicateLinkMessages(String doc)
    {
        List<String> ret = new ArrayList<String>();

        Matcher m = ANCHOR_ATTR_CONTENT_PATTERN.matcher(doc);
        Map<String,String> links = new HashMap<String,String>();

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

            String href = attr.replaceAll(" href=\"(.+)\"", "$1");
            String value = links.get(href);

            if(value != null && value.equals(content)) // duplicate link
                ret.add(String.format("duplicate link: %s", StringUtils.normalise(whole)));

            links.put(href, content);
        }

        return ret;
    }

    /**
     * Returns the list of bad link messages for the HTML document.
     * @return The list of bad link messages for the HTML document
     */
    public static List<String> getBadLinkMessages(String doc)
    {
        List<String> ret = new ArrayList<String>();

        Matcher m = ANCHOR_ATTR_CONTENT_PATTERN.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

            if(attr.indexOf(HTTP_PROTOCOL) != -1)
                ret.add(String.format("link with bad protocol: %s", StringUtils.normalise(whole)));
            else if(content.startsWith(" ") || content.endsWith(" ") || content.startsWith(NBSP_ENTITY) || content.endsWith(NBSP_ENTITY))
                ret.add(String.format("link with anchor text with bad spacing: %s", StringUtils.normalise(whole)));
            else if(content.startsWith(",") || content.endsWith(","))
                ret.add(String.format("link with anchor text with comma: %s", StringUtils.normalise(whole)));
        }

        return ret;
    }

    /**
     * Returns the list of messages for links with bad external spacing in the HTML document.
     * @return The list of messages for links with bad external spacing in the HTML document
     */
    public List<String> getBadExternalSpacingLinkMessages()
    {
        List<String> ret = new ArrayList<String>();

        if(tags != null)
        {
            for(String tag : tags)
                addBadExternalSpacingLinkMessages(tag, ret);
        }

        return ret;
    }

    /**
     * Returns the list of messages for links with bad external spacing in the HTML document.
     * @param tag The tag to look for
     * @param messages The list of messages to add to
     */
    private void addBadExternalSpacingLinkMessages(String tag, List<String> messages)
    {
        Pattern pattern = Pattern.compile(String.format("<%s.*?>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String content = m.group(1);

            addBadExternalSpacingLinkMessagesFromContent(content, messages);
        }
    }

    /**
     * Returns the list of messages for links with bad external spacing in the content.
     * @param content The content to search
     * @param messages The list of messages to add to
     */
    private void addBadExternalSpacingLinkMessagesFromContent(String content, List<String> messages)
    {
        if(content != null && content.length() > 0)
        {
            Matcher textMatcher = ANCHOR_TEXT_PATTERN.matcher(content);

            while(textMatcher.find())
            {
                String before = textMatcher.group(1);
                String link = textMatcher.group(2);
                String after = textMatcher.group(3);

                if(needSpaceBefore(removeMarkup(before)) || needSpaceAfter(removeMarkup(after)))
                {
                    messages.add(String.format("link with bad external spacing: %s", StringUtils.normalise(link)));
                }

                // Search the rest of the string in case there are more anchors
                addBadExternalSpacingLinkMessagesFromContent(after, messages);
            }
        }
    }

    /**
     * Returns the list of messages for split links in the HTML document.
     * @return The list of messages for split links in the HTML document
     */
    public List<String> getSplitLinkMessages()
    {
        List<String> ret = new ArrayList<String>();

        if(tags != null)
        {
            for(String tag : tags)
                addSplitLinkMessages(tag, ret);
        }

        return ret;
    }

    /**
     * Returns the list of messages for split links in the HTML document.
     * @param tag The tag to look for
     * @param messages The list of messages to add to
     */
    private void addSplitLinkMessages(String tag, List<String> messages)
    {
        Pattern pattern = Pattern.compile(String.format("<%s.*?>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String content = m.group(1);

            int idx = 0;
            String lastHref = null;
            String lastBefore = null;
            String lastWhole = null;

            Matcher textMatcher = ANCHOR_LEADING_TEXT_PATTERN.matcher(content);

            while(textMatcher.find())
            {
                String whole = textMatcher.group(0);
                String before = textMatcher.group(1);
                String attr = textMatcher.group(2);

                String href = attr.replaceAll(" href=\"(.+)\"", "$1");

                // Look for the same link repeated with no intervening text
                if(lastHref != null
                    && href.equals(lastHref)
                    && before.trim().length() == 0)
                {
                    // If this is the second part of the split link, add a message for the first part too
                    if(idx == 1)
                        messages.add(String.format("split link: %s", StringUtils.normalise(lastWhole.trim())));

                    messages.add(String.format("split link: %s", StringUtils.normalise(whole.trim())));
                }

                ++idx;
                lastHref = href;
                lastBefore = before;
                lastWhole = whole;
            }
        }
    }

    /**
     * Returns the list of image source messages in the HTML document
     * @return The list of image source messages in the HTML document
     */
    public List<String> getImageSourceMessages()
    {
        List<String> ret = new ArrayList<String>();

        if(tags != null)
        {
            for(String tag : tags)
                addImageSourceMessages(tag, ret);
        }

        return ret;
    }

    /**
     * Returns the list of image source messages for the HTML document.
     * @param tag The tag to look for
     * @param messages The list of messages to add to
     */
    private void addImageSourceMessages(String tag, List<String> messages)
    {
        Pattern pattern = Pattern.compile(String.format("<%s>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String content = m.group(1).trim();

            // If the content contains a link, remove it
            Matcher anchorMatcher = ANCHOR_ATTR_CONTENT_PATTERN.matcher(content);
            if(anchorMatcher.find())
            {
                String link = anchorMatcher.group(0);
                String anchor = anchorMatcher.group(2).trim();

                if(anchor.length() > 0)
                    content = content.replace(link, anchor);
            }

            content = removeMarkup(content);

            if(FileFormat.containsImage(content.toLowerCase()))
            {
                Matcher sourceMatcher = SOURCE_PATTERN.matcher(content.toLowerCase());
                if(sourceMatcher.find())
                    messages.add(String.format("image source: %s", StringUtils.normalise(whole)));
                else if(content.startsWith("https://") && content.indexOf("<") == -1) // No markup, just text
                    messages.add(String.format("image source: %s", StringUtils.normalise(whole)));
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains an image source.
     * @return <CODE>true</CODE> if the HTML document contains an image source.
     */
    public boolean hasImageSource()
    {
        boolean ret = false;

        if(tags != null)
        {
            for(String tag : tags)
            {
                if(ret = hasImageSource(tag))
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the HTML document contains an image source.
     * @param tag The tag to look for in the document
     * @return <CODE>true</CODE> if the HTML document contains an image source.
     */
    private boolean hasImageSource(String tag)
    {
        boolean ret = false;

        Pattern pattern = Pattern.compile(String.format("<%s>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String whole = m.group(0);
            String content = m.group(1).trim();

            // If the content contains a link, remove it
            Matcher anchorMatcher = ANCHOR_ATTR_CONTENT_PATTERN.matcher(content);
            if(anchorMatcher.find())
            {
                String link = anchorMatcher.group(0);
                String anchor = anchorMatcher.group(2).trim();

                if(anchor.length() > 0)
                    content = content.replace(link, anchor);
            }

            content = removeMarkup(content);

            if(FileFormat.containsImage(content.toLowerCase()))
            {
                Matcher sourceMatcher = SOURCE_PATTERN.matcher(content.toLowerCase());
                if(sourceMatcher.find())
                {
                    ret = true;
                }
                else if(!ret && content.startsWith("https://") && content.indexOf("<") == -1) // No markup, just text
                {
                    ret = true;
                }
            }
        }

        return ret;
    }

    /**
     * Removes image sources from the HTML document.
     */
    public void removeImageSources()
    {
        if(tags != null)
        {
            for(String tag : tags)
                removeImageSources(tag);
        }
    }

    /**
     * Removes image sources from the HTML document.
     * @param tag The tag to look for
     */
    private void removeImageSources(String tag)
    {
        Pattern pattern = Pattern.compile(String.format("\\s*<%s>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String content = m.group(1).trim();

            // If the content contains a link, remove it
            Matcher anchorMatcher = ANCHOR_ATTR_CONTENT_PATTERN.matcher(content);
            if(anchorMatcher.find())
            {
                String link = anchorMatcher.group(0);
                String anchor = anchorMatcher.group(2).trim();

                if(anchor.length() > 0)
                    content = content.replace(link, anchor);
            }

            content = removeMarkup(content);

            Matcher sourceMatcher = SOURCE_PATTERN.matcher(content.toLowerCase());
            boolean found = false;
            if(sourceMatcher.find())
            {
                found = true;
            }

            // Remove dir attribute
            if(content.startsWith("https://") && content.indexOf("<") == -1) // No markup, just text
            {
                found = true;
            }

            if(found)
            {
                doc = doc.replace(whole, "");
            }
        }
    }

    /**
     * Returns a builder for the HTML document.
     * @param doc The HTML document
     * @return The builder instance.
     */
    public static Builder builder(String doc)
    {
        return new Builder(doc);
    }

    /**
     * Builder to make HTML document construction easier.
     */
    public static class Builder
    {
        private HtmlDocument ret = null;

        /**
         * Constructor that takes a HTML document.
         * @param doc The HTML document
         */
        public Builder(String doc)
        {
            ret = new HtmlDocument(doc);
        }

        /**
         * Sets the tags that will be used to search the HTML document.
         * @return This object
         */
        public Builder withTags(String... tags)
        {
            ret.setTags(tags);
            return this;
        }

        /**
         * Removes unnecessary attributes from the HTML document.
         * @return This object
         */
        public Builder removeUnnecessaryAttributes()
        {
            ret.removeUnnecessaryAttributes();
            return this;
        }

        /**
         * Removes unnecessary spacing from the HTML document.
         * @return This object
         */
        public Builder removeUnnecessarySpacing()
        {
            ret.removeUnnecessarySpacing();
            return this;
        }

        /**
         * Fix the links with bad external spacing in the HTML document.
         * @return This object
         */
        public Builder fixBadExternalSpacingLinks()
        {
            ret.fixBadExternalSpacingLinks();
            return this;
        }

        /**
         * Fix the links with anchor text with bad spacing in the HTML document.
         * @return This object
         */
        public Builder fixBadAnchorTextLinks()
        {
            ret.fixBadAnchorTextLinks();
            return this;
        }

        /**
         * Fixes links using "http:" in the HTML document with "https:".
         * @return This object
         */
        public Builder fixBadProtocolLinks()
        {
            ret.fixBadProtocolLinks();
            return this;
        }

        /**
         * Replaces "&amp;nbsp;" characters in the HTML document with normal spaces.
         * @return This object
         */
        public Builder replaceNbsps()
        {
            ret.replaceNbsps();
            return this;
        }

        /**
         * Removes "&lt;span&gt;" tags in the HTML document.
         * @return This object
         */
        public Builder removeSpans()
        {
            ret.removeSpans();
            return this;
        }

        /**
         * Replaces "&lt;div&gt;" tags in the HTML document with &lt;p&gt; tags.
         * @return This object
         */
        public Builder replaceDivs()
        {
            ret.replaceDivs();
            return this;
        }

        /**
         * Removes "&lt;div&gt;" tags in the HTML document.
         * @return This object
         */
        public Builder removeDivs()
        {
            ret.removeDivs();
            return this;
        }

        /**
         * Removes "&lt;section&gt;" tags in the HTML document.
         * @return This object
         */
        public Builder removeSections()
        {
            ret.removeSections();
            return this;
        }

        /**
         * Collapses fragmented list in the HTML document.
         * @return This object
         */
        public Builder fixFragmentedLists()
        {
            ret.fixFragmentedLists();
            return this;
        }

        /**
         * Replaces extra line breaks in the HTML document with a new paragraph.
         * @return This object
         */
        public Builder replaceExtraLineBreaks()
        {
            ret.replaceExtraLineBreaks();
            return this;
        }

        /**
         * Removes extra line breaks from the HTML document.
         * @return This object
         */
        public Builder removeExtraLineBreaks()
        {
            ret.removeExtraLineBreaks();
            return this;
        }

        /**
         * Adds the image wrapper class to images in the given string.
         * @return This object
         */
        public Builder addImageWrapperClass()
        {
            ret.addImageWrapperClass();
            return this;
        }

        /**
         * Adds the image legend class to image captions in the given string.
         * @return This object
         */
        public Builder addImageLegendClass()
        {
            ret.addImageLegendClass();
            return this;
        }

        /**
         * Removes image sources from the HTML document.
         * @return This object
         */
        public Builder removeImageSources()
        {
            ret.removeImageSources();
            return this;
        }

        /**
         * Reduces the main headings by one level.
         * @return This object
         */
        public Builder reduceHeadings()
        {
            ret.reduceHeadings();
            return this;
        }

        /**
         * Returns the processed HTML document instance
         * @return The HTML document
         */
        public HtmlDocument build()
        {
            return ret;
        }

        /**
         * Returns the processed HTML document
         * @return The HTML document
         */
        public String get()
        {
            return build().get();
        }
    }
}