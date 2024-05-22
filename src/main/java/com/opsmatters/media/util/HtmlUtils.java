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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

/**
 * A set of utility methods to perform miscellaneous tasks related to HTML documents.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class HtmlUtils
{
    private static final Logger logger = Logger.getLogger(HtmlUtils.class.getName());

    public static final String DATA_ATTR = " data-";
    public static final String DIR_ATTR = " dir=\"ltr\"";
    public static final String NBSP = "\u00a0";
    public static final String NBSP_ENTITY = "&nbsp;";
    public static final String TEXT_CHARS = "\\w\\u2010-\\u2017";
    public static final String IMAGE_SOURCE = "image source:";
    public static final String CONTENT_WRAPPER_CLASS = "post-full-content";
    public static final String IMAGE_WRAPPER_CLASS = "image-wrapper";
    public static final String IMAGE_LEGEND_CLASS = "image-legend";

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private HtmlUtils()
    {
    }    

    /**
     * Returns <CODE>true</CODE> if the given string contains an attribute prefixed by "data-".
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains attributes prefixed by "data-".
     */
    private static boolean hasDataAttribute(String str)
    {
        Pattern dataAttribute = Pattern.compile(String.format("%s[-\\w]+=", DATA_ATTR), Pattern.DOTALL);
        return dataAttribute.matcher(str).find();
    }

    /**
     * Removes attributes prefixed by "data-" from the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    private static String removeDataAttributes(String str)
    {
        // Remove data- attributes, with and without values
        str = str.replaceAll(String.format("%s[-\\w]+=\"[-\\w\\[\\]{}:,./\"\\u25cf]*\"", DATA_ATTR), "");
        // Removed as it also replaces valid text such as "data-driven"
        // str = str.replaceAll(String.format("%s[-\\w]+", DATA_ATTR), "");

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a dir attribute.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a dir attribute.
     */
    private static boolean hasDirAttribute(String str)
    {
        return str.indexOf(DIR_ATTR) != -1;
    }

    /**
     * Removes dir attributes from the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    private static String removeDirAttributes(String str)
    {
        return str.replaceAll(DIR_ATTR, "");
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains unnecessary attributes.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains unnecessary attributes.
     */
    public static boolean hasAttributes(String str)
    {
        return hasDataAttribute(str) || hasDirAttribute(str);
    }

    /**
     * Removes unnecessary attributes from the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeAttributes(String str)
    {
        str = removeDataAttributes(str);
        str = removeDirAttributes(str);

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a list that is not contiguous.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a list that is not contiguous.
     */
    public static boolean hasFragmentedList(String str)
    {
        Pattern ul = Pattern.compile("</li>[\r\n]*</ul>[\r\n]*<ul>[\r\n]*<li>", Pattern.DOTALL);
        Pattern ol = Pattern.compile("</li>[\r\n]*</ol>[\r\n]*<ol>[\r\n]*<li>", Pattern.DOTALL);
        return ul.matcher(str).find() || ol.matcher(str).find();
    }

    /**
     * Collapses fragmented list in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String collapseLists(String str)
    {
        // Remove list open and close tags in the middle of another list
        str = str.replaceAll("(</li>[\r\n]*)</ul>[\r\n]*<ul>[\r\n]*(<li>)", "$1$2");
        str = str.replaceAll("(</li>[\r\n]*)</ol>[\r\n]*<ol>[\r\n]*(<li>)", "$1$2");

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains extra line breaks.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains extra line breaks.
     */
    public static boolean hasExtraLineBreaks(String str)
    {
        Pattern pattern = Pattern.compile("<br[ /]*><br[ /]*>", Pattern.DOTALL);
        return pattern.matcher(str).find();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains links not surrounded by spaces.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains links not surrounded by spaces.
     */
    public static boolean needsLinkSpacing(String str)
    {
        Pattern before = Pattern.compile(String.format("[>:;,.%s]+<a.*?>", TEXT_CHARS), Pattern.DOTALL);
        Pattern after = Pattern.compile(String.format("</a>[<%s]+", TEXT_CHARS), Pattern.DOTALL);
        return before.matcher(str).find() || after.matcher(str).find();
    }

    /**
     * Add spacing around links in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String addLinkSpacing(String str)
    {
        Pattern before = Pattern.compile(String.format("([>:;,.%s]+)(<a.*?>)", TEXT_CHARS), Pattern.DOTALL);
        str = before.matcher(str).replaceAll("$1 $2");

        Pattern after = Pattern.compile(String.format("(</a>)([<%s]+)", TEXT_CHARS), Pattern.DOTALL);
        str = after.matcher(str).replaceAll("$1 $2");

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains "&amp;nbsp;" characters.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains "&amp;nbsp;" characters.
     */
    public static boolean hasNbsp(String str)
    {
        return str.indexOf(NBSP) != -1
            || str.indexOf(NBSP_ENTITY) != -1;
    }

    /**
     * Replaces "&amp;nbsp;" characters in the given string with normal spaces.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String replaceNbsp(String str)
    {
        // Replace nbsp with space
        str = str.replaceAll(NBSP, " ");
        str = str.replaceAll(NBSP_ENTITY, " ");

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a &lt;h1&gt; tag.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a &lt;h1&gt; tag.
     */
    public static boolean needsReduceHeadings(String str)
    {
        return str.indexOf("<h1") != -1;
    }

    /**
     * Reduces the main headings by one level.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String reduceHeadings(String str)
    {
        str = str.replaceAll("<h5([ >])", "<h6$1");
        str = str.replaceAll("</h5>", "</h6>");

        str = str.replaceAll("<h4([ >])", "<h5$1");
        str = str.replaceAll("</h4>", "</h5>");

        str = str.replaceAll("<h3([ >])", "<h4$1");
        str = str.replaceAll("</h3>", "</h4>");

        str = str.replaceAll("<h2([ >])", "<h3$1");
        str = str.replaceAll("</h2>", "</h3>");

        str = str.replaceAll("<h1([ >])", "<h2$1");
        str = str.replaceAll("</h1>", "</h2>");

        return str;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains the post-full-content class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains the post-full-content class.
     */
    public static boolean hasContentWrapperClass(String str)
    {
        return str.indexOf(CONTENT_WRAPPER_CLASS) != -1;
    }

    /**
     * Returns <CODE>true</CODE> if the given string needs the post-full-content class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string needs the post-full-content class.
     */
    public static boolean needsContentWrapperClass(String str)
    {
        return str.indexOf("<table") != -1
            || str.indexOf("<pre") != -1
            || str.indexOf("<blockquote") != -1;
    }

    /**
     * Adds a div wrapper element with the post-full-content class.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String addContentWrapperClass(String str)
    {
        return String.format("<div class=\"%s\">\n%s\n</div>",
            CONTENT_WRAPPER_CLASS, str);
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains an image URL as text.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains an image URL as text.
     */
    public static boolean hasImageSource(String str)
    {
        boolean ret = false;
        Pattern pattern = Pattern.compile("<p>(.+?)</p>", Pattern.DOTALL);
        Matcher m = pattern.matcher(str);
        while(m.find() && !ret)
        {
            String contents = m.group(1).toLowerCase().trim();
            if(contents.indexOf(IMAGE_SOURCE) != -1)
            {
                ret = true;
            }
            else if(contents.startsWith("https://") && contents.indexOf("<") == -1) // No markup, just text
            {
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains malformed links.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains malformed links.
     */
    public static boolean hasMalformedLinks(String str)
    {
        boolean ret = false;
        Pattern linkPattern = Pattern.compile("<a(.+?)>(.+?)</a>", Pattern.DOTALL);
        Matcher linkMatcher = linkPattern.matcher(str);

        while(linkMatcher.find() && !ret)
        {
            String attr = linkMatcher.group(1);
            String anchor = linkMatcher.group(2);

            Pattern hrefPattern = Pattern.compile(" href=\"(.+?)\"", Pattern.DOTALL);
            Matcher hrefMatcher = hrefPattern.matcher(attr);
            String href = hrefMatcher.find() ? hrefMatcher.group(1) : "";

            if(!href.startsWith("https://") && !href.startsWith("mailto:")) // Malformed URL
            {
                ret = true;
                logger.warning(String.format("Found malformed link with bad protocol: href=%s, anchor=%s",
                    href, anchor));
            }
            else if(anchor.startsWith(" ") || anchor.endsWith(" "))
            {
                ret = true;
                logger.warning(String.format("Found malformed link with spaces: href=%s, anchor=%s",
                    href, anchor));
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains duplicate links.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains duplicate links.
     */
    public static boolean hasDuplicateLinks(String str)
    {
        boolean ret = false;
        Pattern pattern = Pattern.compile("<a(.+?)>(.+?)</a>", Pattern.DOTALL);
        Matcher m = pattern.matcher(str);
        Map<String,String> links = new HashMap<String,String>();

        while(m.find() && !ret)
        {
            String attributes = m.group(1);
            String anchor = m.group(2);

            String href = attributes.replaceAll(" href=\"(.+)\"", "$1");
            if(links.containsKey(href)) // duplicate link
            {
                ret = true;
                logger.warning(String.format("Found duplicate link: href=%s, anchor=%s",
                    href, anchor));
            }

            links.put(href, anchor);
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains the image-wrapper class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains the image-wrapper class.
     */
    public static boolean hasImageWrapperClass(String str)
    {
        return str.indexOf(IMAGE_WRAPPER_CLASS) != -1;
    }

    /**
     * Returns <CODE>true</CODE> if the given string needs the image-wrapper class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string needs the image-wrapper class.
     */
    public static boolean needsImageWrapperClass(String str)
    {
        return str.indexOf("<img") != -1;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains the image-legend class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains the image-legend class.
     */
    public static boolean hasImageLegendClass(String str)
    {
        return str.indexOf(IMAGE_LEGEND_CLASS) != -1;
    }

    /**
     * Returns <CODE>true</CODE> if the given string needs the image-legend class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string needs the image-legend class.
     */
    public static boolean needsImageLegendClass(String str)
    {
        return str.indexOf("<figcaption") != -1;
    }
}