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

/**
 * A set of utility methods to perform miscellaneous tasks related to HTML links.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class HtmlDocument
{

    private static final String START_ATTR = " start=";
    private static final String STYLE_ATTR = " style=";

    private static final String IMAGE_WRAPPER_CLASS = "image-wrapper";
    private static final String IMAGE_LEGEND_CLASS = "image-legend";
    private static final String IMAGE_STYLE = "margin:15px 0;";

    private static final String NBSP_CHAR = "\u00a0";
    private static final String NBSP_ENTITY = "&nbsp;";

    private static final String BEFORE_CHARS = " ("+NBSP_CHAR;
    private static final String AFTER_CHARS = " !:;,.?)"+NBSP_CHAR;
    private static final String BEFORE_ENTITIES = "&ldquo;"+NBSP_ENTITY;
    private static final String AFTER_ENTITIES = "&rdquo;"+NBSP_ENTITY;

    private static final String HTTP_PROTOCOL = "http:";
    private static final String HTTPS_PROTOCOL = "https:";

    private static final String LINE_BREAKS = "(<br[ /]*>){2,}";
    private static final String NEW_PARAGRAPH = "</p>\n<p>";

    private static Pattern DATA_ATTR_PATTERN = Pattern.compile(" data-[-\\w]+=", Pattern.DOTALL);
    private static Pattern DIR_ATTR_PATTERN = Pattern.compile(" dir=\"[lr]t[rl]\"", Pattern.DOTALL);
    private static Pattern ANCHOR_TEXT_PATTERN = Pattern.compile("(.*?)(<a.*?>.*?</a>)(.*)", Pattern.DOTALL);
    private static Pattern BEFORE_TEXT_PATTERN = Pattern.compile(".*(&\\w+;)$", Pattern.DOTALL);
    private static Pattern AFTER_TEXT_PATTERN = Pattern.compile("^(&\\w+;).*", Pattern.DOTALL);
    private static Pattern ANCHOR_ATTR_PATTERN = Pattern.compile("<a(.*?)>", Pattern.DOTALL);
    private static Pattern ANCHOR_ATTR_CONTENT_PATTERN = Pattern.compile("<a(.*?)>(.+?)</a>", Pattern.DOTALL);
    private static Pattern HREF_PATTERN = Pattern.compile(" href=\"(.+?)\"", Pattern.DOTALL);
    private static Pattern SPAN_CONTENT_PATTERN = Pattern.compile("<span.*?>(.*?)</span>", Pattern.DOTALL);
    private static Pattern DIV_CONTENT_PATTERN = Pattern.compile("<div>(.+?)</div>", Pattern.DOTALL);
    private static Pattern H1_CONTENT_PATTERN = Pattern.compile("<h1.*?>(.+?)</h1>", Pattern.DOTALL);
    private static Pattern OL_ATTR_CONTENT_PATTERN = Pattern.compile("<ol(.*?)>(.*?)</ol>", Pattern.DOTALL);
    private static Pattern LI_PATTERN = Pattern.compile("<li>", Pattern.DOTALL);
    private static Pattern SOURCE_PATTERN = Pattern.compile("^\\w*[ ]?source:", Pattern.DOTALL); // eg. Image Source
    private static Pattern FRAGMENTED_UL_LIST_PATTERN = Pattern.compile("</li>[\r\n]*</ul>[\r\n]*<ul>[\r\n]*<li>", Pattern.DOTALL);
    private static Pattern FRAGMENTED_OL_LIST_PATTERN = Pattern.compile("</li>[\r\n]*</ol>[\r\n]*<ol>[\r\n]*<li>", Pattern.DOTALL);
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

//GERALD
//System.out.println("HtmlDocument.hasUnnecessaryAttribute:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>", tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String attr = m.group(1);

            ret = DATA_ATTR_PATTERN.matcher(attr).find();
//GERALD
//System.out.println("HtmlDocument.hasUnnecessaryAttribute:2: tag="+tag+" ret="+ret);
            if(!ret)
                ret = DIR_ATTR_PATTERN.matcher(attr).find();
//GERALD
//System.out.println("HtmlDocument.hasUnnecessaryAttribute:3: tag="+tag+" ret="+ret);
        }

//GERALD
//System.out.println("HtmlDocument.hasUnnecessaryAttribute:4: tag="+tag+" ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.removeUnnecessaryAttributes:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>", tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);

//GERALD
//System.out.println("HtmlDocument.removeUnnecessaryAttributes:2: tag="+tag+" attr=["+attr+"] whole="+whole);

            boolean changed = false;

            // Remove data- attributes, with and without values
            if(DATA_ATTR_PATTERN.matcher(attr).find())
            {
                attr = attr.replaceAll(" data-[-\\w]+=\".*?\"", "");
                attr = attr.replaceAll(" data-[-\\w]+", "");
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessaryAttributes:3: tag="+tag+" attr=["+attr+"] changed="+changed);
            }

            // Remove dir attribute
            if(DIR_ATTR_PATTERN.matcher(attr).find())
            {
                attr = attr.replaceAll(" dir=\"[lr]t[rl]\"", "");
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessaryAttributes:4: tag="+tag+" attr=["+attr+"] changed="+changed);
            }

            if(changed)
            {
                doc = doc.replaceFirst(Pattern.quote(whole), String.format("<%s%s>", tag, attr));
//GERALD
//System.out.println("HtmlDocument.removeUnnecessaryAttributes:5: tag="+tag+" attr=["+attr
//  +" newWhole="+String.format("<%s%s>", tag, attr));
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

//GERALD
//System.out.println("HtmlDocument.hasUnnecessarySpacing:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String attr = m.group(1);
            String content = m.group(2);

            ret = content.startsWith(" ") || content.endsWith(" ")
                || content.startsWith(NBSP_CHAR) || content.endsWith(NBSP_CHAR)
                || content.startsWith(NBSP_ENTITY) || content.endsWith(NBSP_ENTITY);
//GERALD
//System.out.println("HtmlDocument.hasUnnecessarySpacing:2: tag="+tag+" content="+content+" ret="+ret);
        }

//GERALD
//System.out.println("HtmlDocument.hasUnnecessarySpacing:3: tag="+tag+" ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:2: tag="+tag+" content=["+content+"] whole="+whole);

            boolean changed = false;

            while(content.startsWith(NBSP_CHAR))
            {
                content = content.substring(NBSP_CHAR.length());
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:3: tag="+tag+" content=["+content+"]");
            }

            while(content.endsWith(NBSP_CHAR))
            {
                content = content.substring(0, content.length()-NBSP_CHAR.length());
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:4: tag="+tag+" content=["+content+"]");
            }

            while(content.startsWith(NBSP_ENTITY))
            {
                content = content.substring(NBSP_ENTITY.length());
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:5: tag="+tag+" content=["+content+"]");
            }

            while(content.endsWith(NBSP_ENTITY))
            {
                content = content.substring(0, content.length()-NBSP_ENTITY.length());
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:6: tag="+tag+" content=["+content+"]");
            }

            if(content.startsWith(" ") || content.endsWith(" "))
            {
                content = content.trim();
                changed = true;
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:7: tag="+tag+" content=["+content+"]");
            }

            if(changed)
            {
                doc = doc.replaceFirst(Pattern.quote(whole), String.format("<%s%s>%s</%s>", tag, attr, content, tag));
//GERALD
//System.out.println("HtmlDocument.removeUnnecessarySpacing:8: tag="+tag+" content=["+content
//  +" newWhole="+String.format("<%s%s>%s</%s>", tag, attr, content, tag));
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

//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLink:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s.*?>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String content = m.group(1);

            ret = hasBadExternalSpacingLinkFromContent(content);
        }

//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLink:2: tag="+tag+" ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:1: content="+content);
            Matcher textMatcher = ANCHOR_TEXT_PATTERN.matcher(content);

            while(textMatcher.find() && !ret)
            {
                String before = textMatcher.group(1);
                String after = textMatcher.group(3);

                ret = needSpaceBefore(removeMarkup(before));
//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:2: before=["+removeMarkup(before)+"] ret="+ret);
                if(!ret)
                {
                    ret = needSpaceAfter(removeMarkup(after));
//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:3: after=["+removeMarkup(after)+"] ret="+ret);
                }
//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:4: ret="+ret);

                if(!ret)
                {
                    // Search the rest of the string in case there are more anchors
                    ret = hasBadExternalSpacingLinkFromContent(after);
//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:5: ret="+ret);
                }
//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:6: ret="+ret);
            }
        }

//GERALD
//System.out.println("HtmlDocument.hasBadExternalSpacingLinkFromContent:7: ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.fixBadExternalSpacingLinks:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.fixBadExternalSpacingLinks:2: tag="+tag+" content=["+content+"]");
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
//GERALD
//System.out.println("HtmlDocument.fixBadExternalSpacingLinksFromContent:1: content="+content);
            Matcher textMatcher = ANCHOR_TEXT_PATTERN.matcher(content);

            while(textMatcher.find())
            {
                String before = textMatcher.group(1);
                String link = textMatcher.group(2);
                String after = textMatcher.group(3);

//GERALD
//System.out.println("HtmlDocument.fixBadExternalSpacingLinksFromContent:2: before=["+before+"] after=["+after+"] link="+link);
                boolean addBefore = needSpaceBefore(removeMarkup(before));
                boolean addAfter = needSpaceAfter(removeMarkup(after));

//GERALD
//System.out.println("HtmlDocument.fixBadExternalSpacingLinksFromContent:3: testBefore=["+removeMarkup(before)+"] testAfter=["+removeMarkup(after)+"] addBefore="+addBefore+" addAfter="+addAfter);

                if(addBefore || addAfter)
                {
                    String newContent = String.format("%s%s%s%s%s",
                        before,
                        addBefore ? " " : "",
                        link,
                        addAfter ? " " : "",
                        after);

                    doc = doc.replaceFirst(Pattern.quote(String.format(">%s<", content)), String.format(">%s<", newContent));
//GERALD
//System.out.println("HtmlDocument.fixBadExternalSpacingLinksFromContent:4: before=["+before+"] after=["+after+"] addBefore="+addBefore+" addAfter="+addAfter
//  +" newContent="+newContent+" newWhole="+String.format(">%s<", newContent));
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

//GERALD
//System.out.println("HtmlDocument.needSpaceBefore:1: before=["+before+"]");
        if(before.length() > 0)
        {
            char beforeChar = before.charAt(before.length()-1);

            Matcher beforeMatcher = BEFORE_TEXT_PATTERN.matcher(before);
            String beforeEntity = beforeMatcher.find() ? beforeMatcher.group(1) : "";

//GERALD
//System.out.println("HtmlDocument.needSpaceBefore:2: beforeChar=["+beforeChar+"] beforeEntity=["+beforeEntity+"]");
//System.out.println("HtmlDocument.needSpaceBefore:2a: BEFORE_ENTITIES="+BEFORE_ENTITIES.indexOf(beforeEntity));
//System.out.println("HtmlDocument.needSpaceBefore:2b: BEFORE_CHARS="+BEFORE_CHARS.indexOf(beforeChar));
            if(beforeEntity.length() > 0)
                ret = BEFORE_ENTITIES.indexOf(beforeEntity) == -1; // try to match the entity first
            else
                ret = BEFORE_CHARS.indexOf(beforeChar) == -1;

//GERALD
//System.out.println("HtmlDocument.needSpaceBefore:3: before=["+before+"] ret="+ret);
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

//GERALD
//System.out.println("HtmlDocument.needSpaceAfter:1: after=["+after+"]");
        if(after.length() > 0)
        {
            char afterChar = after.charAt(0);

            Matcher afterMatcher = AFTER_TEXT_PATTERN.matcher(after);
            String afterEntity = afterMatcher.find() ? afterMatcher.group(1) : "";

//GERALD
//System.out.println("HtmlDocument.needSpaceAfter:2: afterChar=["+afterChar+"] afterEntity=["+afterEntity+"]");
            if(afterEntity.length() > 0)
                ret = AFTER_ENTITIES.indexOf(afterEntity) == -1; // try to match the entity first
            else
                ret = AFTER_CHARS.indexOf(afterChar) == -1;

//GERALD
//System.out.println("HtmlDocument.needSpaceAfter:3: after=["+after+"] ret="+ret);
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

//GERALD
//System.out.println("HtmlDocument.hasBadAnchorTextLink:1: anchor=["+anchor+"]");
            if(anchor.startsWith(" ") || anchor.endsWith(" ")
                || anchor.startsWith(NBSP_ENTITY) || anchor.endsWith(NBSP_ENTITY))
            {
                ret = true;
            }
//GERALD
//System.out.println("HtmlDocument.hasBadAnchorTextLink:2: anchor=["+anchor+"] ret="+ret);
        }

//GERALD
//System.out.println("HtmlDocument.hasBadAnchorTextLink:3: ret="+ret);
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

//GERALD
//System.out.println("HtmlDocument.fixBadAnchorTextLinks:1: anchor=["+anchor+"] attr=["+attr+"] whole="+whole);

            if(anchor.startsWith(NBSP_CHAR) || anchor.endsWith(NBSP_CHAR)
                || anchor.startsWith(NBSP_ENTITY) || anchor.endsWith(NBSP_ENTITY))
            {
                anchor = replaceNbsps(anchor);
//GERALD
//System.out.println("HtmlDocument.fixBadAnchorTextLinks:2: anchor=["+anchor+"] attr=["+attr+"] whole="+whole);
            }

//GERALD
//System.out.println("HtmlDocument.fixBadAnchorTextLinks:3: anchor=["+anchor+"] attr=["+attr+"] whole="+whole);
            if(anchor.startsWith(" ") || anchor.endsWith(" "))
            {
                doc = doc.replaceFirst(Pattern.quote(whole), String.format("<a%s>%s</a>", attr, anchor.trim()));
//GERALD
//System.out.println("HtmlDocument.fixBadAnchorTextLinks:4: whole="+Pattern.quote(whole)
//  +" newWhole="+String.format("<a%s>%s</a>", attr, anchor.trim()));
            }
//GERALD
//System.out.println("HtmlDocument.fixBadAnchorTextLinks:5: anchor=["+anchor+"] attr=["+attr+"] whole="+whole);
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

//GERALD
//System.out.println("HtmlDocument.hasBadProtocolLink:1: attr="+attr);
            if(ret = attr.indexOf(HTTP_PROTOCOL) != -1)
            {
//GERALD
//System.out.println("HtmlDocument.hasBadProtocolLink:2: attr="+attr+" ret="+ret);
                break;
            }
//GERALD
//System.out.println("HtmlDocument.hasBadProtocolLink:3: attr="+attr+" ret="+ret);
        }

        return ret;
    }

    /**
     * Replaces links using "http:" in the HTML document with "https:".
     */
    public void fixBadProtocolLinks()
    {
//GERALD
//System.out.println("HtmlDocument.fixBadProtocolLinks:1: ");
        Matcher m = ANCHOR_ATTR_PATTERN.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);

            if(attr.indexOf(HTTP_PROTOCOL) != -1)
            {
//GERALD
//System.out.println("HtmlDocument.fixBadProtocolLinks:2: attr="+attr
//  +" newAttr="+attr.replaceFirst(HTTP_PROTOCOL, HTTPS_PROTOCOL));

                // Replace http:// in title and href in attributes
                doc = doc.replaceFirst(Pattern.quote(whole), String.format("<a%s>",
                    attr.replaceAll(HTTP_PROTOCOL, HTTPS_PROTOCOL)));
//GERALD
//System.out.println("HtmlDocument.fixBadProtocolLinks:3: whole="+Pattern.quote(whole)
//  +" newWhole="+String.format("<a%s>", attr.replaceFirst(HTTP_PROTOCOL, HTTPS_PROTOCOL)));
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
        return SPAN_CONTENT_PATTERN.matcher(doc).find();
    }

    /**
     * Removes "&lt;span&gt;" tags in the HTML document.
     */
    private void removeSpans()
    {
        Matcher m = SPAN_CONTENT_PATTERN.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String content = m.group(1);
            doc = doc.replaceFirst(whole, content);
        }
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
        doc = doc.replaceAll("(</li>[\r\n]*)</ul>[\r\n]*<ul>[\r\n]*(<li>)", "$1$2");
        doc = doc.replaceAll("(</li>[\r\n]*)</ol>[\r\n]*<ol>[\r\n]*(<li>)", "$1$2");
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

//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:1: attr="+attr+" content="+content+" found="+found);
            // Look for two successive <ol><li>...</li></ol> without a "start" attribute
            if(attr.indexOf(START_ATTR) == -1)
            {
                int count = getLiCount(content);
//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:2: attr="+attr+" content="+content+" count="+count+" found="+found);
                if(count == 1)
                {
                    if(found)
                    {
                        ret = true;
//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:3: attr="+attr+" content="+content+" count="+count+" found="+found+" ret="+ret);
                        break;
                    }
                    else
                    {
                        found = true;
//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:4: attr="+attr+" content="+content+" count="+count+" found="+found+" ret="+ret);
                    }
                }
                else
                {
                    found = false;
//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:5: attr="+attr+" content="+content+" count="+count+" found="+found+" ret="+ret);
                }
            }
            else
            {
                found = false;
//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:6: attr="+attr+" content="+content+" found="+found+" ret="+ret);
            }
        }

//GERALD
//System.out.println("HtmlDocument.hasBadOrderedList:7: ret="+ret);
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

//GERALD
//System.out.println("HtmlDocument.hasExtraLineBreaks:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.hasExtraLineBreaks:2: tag="+tag+" content=["+content+"]");
            ret = LINE_BREAKS_PATTERN.matcher(content).find();
//GERALD
//System.out.println("HtmlDocument.hasExtraLineBreaks:3: tag="+tag+" ret="+ret);
        }

//GERALD
//System.out.println("HtmlDocument.hasExtraLineBreaks:4: tag="+tag+" ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.replaceExtraLineBreaks:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.replaceExtraLineBreaks:2: tag="+tag+" content=["+content+"] whole="+whole);
            if(LINE_BREAKS_PATTERN.matcher(content).find())
            {
                // Replace multiple line breaks with new paragraph
                content = content.replaceAll(LINE_BREAKS, NEW_PARAGRAPH);
//GERALD
//System.out.println("HtmlDocument.replaceExtraLineBreaks:3: tag="+tag+" content=["+content+"]");
                doc = doc.replaceFirst(Pattern.quote(whole), String.format("<%s%s>%s</%s>", tag, attr, content, tag));
//GERALD
//System.out.println("HtmlDocument.replaceExtraLineBreaks:4: tag="+tag+" content="+content
//  +" newWhole="+String.format("<%s%s>%s</%s>", tag, attr, content, tag));
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
//GERALD
//System.out.println("HtmlDocument.removeExtraLineBreaks:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.removeExtraLineBreaks:2: tag="+tag+" content=["+content+"] whole="+whole);
            if(LINE_BREAKS_PATTERN.matcher(content).find())
            {
                // Remove multiple line breaks
                content = content.replaceAll(LINE_BREAKS, "");
//GERALD
//System.out.println("HtmlDocument.removeExtraLineBreaks:3: tag="+tag+" content=["+content+"]");
                doc = doc.replaceFirst(Pattern.quote(whole), String.format("<%s%s>%s</%s>", tag, attr, content, tag));
//GERALD
//System.out.println("HtmlDocument.removeExtraLineBreaks:4: tag="+tag+" content="+content
//  +" newWhole="+String.format("<%s%s>%s</%s>", tag, attr, content, tag));
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

//GERALD
//System.out.println("HtmlDocument.needsImageWrapperClass:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String attr = m.group(1);
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.needsImageWrapperClass:2: tag="+tag+" content="+content+" attr="+attr);
            if(content.indexOf("<img") != -1)
            {
//GERALD
//System.out.println("HtmlDocument.needsImageWrapperClass:3: tag="+tag+" content="+content+" attr="+attr);
                if(attr.indexOf(IMAGE_WRAPPER_CLASS) == -1)
                {
                    ret = true;
//GERALD
//System.out.println("HtmlDocument.needsImageWrapperClass:4: tag="+tag+" content="+content+" attr="+attr+" ret="+ret);
                }
            }
        }

//GERALD
//System.out.println("HtmlDocument.needsImageWrapperClass:4: tag="+tag+" ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.addImageWrapperClass:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String whole = m.group(0);
            String attr = m.group(1);
            String content = m.group(2);

//GERALD
//System.out.println("HtmlDocument.addImageWrapperClass:2: tag="+tag+" content="+content+" attr=["+attr+"] whole="+whole);
            if(content.indexOf("<img") != -1)
            {
//GERALD
//System.out.println("HtmlDocument.addImageWrapperClass:3: tag="+tag+" content="+content+" attr=["+attr+"] whole="+whole);
                if(attr.indexOf(IMAGE_WRAPPER_CLASS) == -1)
                {
                    attr = String.format("%s class=\"%s\"", attr, IMAGE_WRAPPER_CLASS);
//GERALD
//System.out.println("HtmlDocument.addImageWrapperClass:4: tag="+tag+" content="+content+" attr=["+attr+"]");
                    if(attr.indexOf(STYLE_ATTR) == -1)
                        attr = String.format("%s style=\"%s\"", attr, IMAGE_STYLE);
//GERALD
//System.out.println("HtmlDocument.addImageWrapperClass:5: tag="+tag+" content="+content+" attr=["+attr+"]");

                    doc = doc.replaceFirst(Pattern.quote(whole),
                        String.format("<%s%s>%s</%s>", tag, attr, content, tag));
//GERALD
//System.out.println("HtmlDocument.addImageWrapperClass:6: tag="+tag+" content="+content+"] attr="+attr
//  +" newWhole="+String.format("<%s%s>%s</%s>", tag, attr, content, tag));
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

//GERALD
//System.out.println("HtmlDocument.needsImageLegendClass:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s(.*?)>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find() && !ret)
        {
            String content = m.group(2);

            Matcher captionMatcher = FIGCAPTION_PATTERN.matcher(content);

//GERALD
//System.out.println("HtmlDocument.needsImageLegendClass:2: tag="+tag+" content="+content);
            if(captionMatcher.find())
            {
                String attr = captionMatcher.group(1);
//GERALD
//System.out.println("HtmlDocument.needsImageLegendClass:3: tag="+tag+" content="+content+" attr="+attr);
                if(attr.indexOf(IMAGE_LEGEND_CLASS) == -1)
                {
                    ret = true;
//GERALD
//System.out.println("HtmlDocument.needsImageLegendClass:4: tag="+tag+" content="+content+" attr="+attr+" ret="+ret);
                }
            }
        }

//GERALD
//System.out.println("HtmlDocument.needsImageLegendClass:4: tag="+tag+" ret="+ret);
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
//GERALD
//System.out.println("HtmlDocument.addImageLegendClass:1: tag="+tag);
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
//GERALD
//System.out.println("HtmlDocument.addImageLegendClass:2: tag="+tag+" content="+content+" attr=["+attr+"] whole="+whole);
                if(attr.indexOf(IMAGE_LEGEND_CLASS) == -1)
                {
                    attr = String.format("%s class=\"%s\"", attr, IMAGE_LEGEND_CLASS);
//GERALD
//System.out.println("HtmlDocument.addImageLegendClass:3: tag="+tag+" content="+content+" attr=["+attr+"]");
                    doc = doc.replaceFirst(Pattern.quote(whole),
                        String.format("<figcaption%s>%s</figcaption>", attr, content));
//GERALD
//System.out.println("HtmlDocument.addImageLegendClass:4: tag="+tag+" content="+content+"] attr="+attr
//  +" newWhole="+String.format("<%s%s>%s</%s>", tag, attr, content, tag));
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
//GERALD
//System.out.println("HtmlDocument.addBadExternalSpacingLinkMessages:1: tag="+tag);
        Pattern pattern = Pattern.compile(String.format("<%s.*?>(.+?)</%s>", tag, tag), Pattern.DOTALL);
        Matcher m = pattern.matcher(doc);

        while(m.find())
        {
            String content = m.group(1);

//GERALD
//System.out.println("HtmlDocument.addBadExternalSpacingLinkMessages:2: tag="+tag+" content="+content);
            addBadExternalSpacingLinkMessagesFromContent(content, messages);
        }

//GERALD
//System.out.println("HtmlDocument.addBadExternalSpacingLinkMessages:3: tag="+tag+" messages="+messages);
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

//GERALD
//System.out.println("HtmlDocument.addBadExternalSpacingLinkMessagesFromContent:1: before=["+removeMarkup(before)+"] after=["+removeMarkup(after)+"] needSpaceBefore="+needSpaceBefore(before)+" needSpaceAfter="+needSpaceAfter(after));
                if(needSpaceBefore(removeMarkup(before)) || needSpaceAfter(removeMarkup(after)))
                {
                    messages.add(String.format("link with bad external spacing: %s", StringUtils.normalise(link)));
//GERALD
//System.out.println("HtmlDocument.addBadExternalSpacingLinkMessagesFromContent:2: before=["+before+"] after=["+after+"] needSpaceBefore="+needSpaceBefore(before)+" needSpaceAfter="+needSpaceAfter(after)+" messages="+messages);
                }

                // Search the rest of the string in case there are more anchors
                addBadExternalSpacingLinkMessagesFromContent(after, messages);
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
            String content = m.group(1);

            Matcher sourceMatcher = SOURCE_PATTERN.matcher(content.toLowerCase().trim());

            if(sourceMatcher.find())
                messages.add(String.format("image source: %s", StringUtils.normalise(whole)));
            else if(content.startsWith("https://") && content.indexOf("<") == -1) // No markup, just text
                messages.add(String.format("image source: %s", StringUtils.normalise(whole)));
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