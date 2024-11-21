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

import java.util.List;
import java.util.logging.Logger;

/**
 * A set of utility methods to perform miscellaneous tasks related to HTML documents.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class HtmlUtils
{
    private static final Logger logger = Logger.getLogger(HtmlUtils.class.getName());

    public static final String CONTENT_WRAPPER_CLASS = HtmlDocument.POST_FULL_CONTENT_CLASS;

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private HtmlUtils()
    {
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
     * Returns <CODE>true</CODE> if the given string contains an unnecessary attribute.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains an unnecessary attribute.
     */
    public static boolean hasUnnecessaryAttribute(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "div", "h1", "h2", "h3", "h4", "h5")
            .build()
            .hasUnnecessaryAttribute();
    }

    /**
     * Removes unnecessary attributes from the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeUnnecessaryAttributes(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "div", "h1", "h2", "h3", "h4", "h5")
            .removeUnnecessaryAttributes()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains unnecessary spacing.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains unnecessary spacing.
     */
    public static boolean hasUnnecessarySpacing(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "div", "h1", "h2", "h3", "h4", "h5")
            .build()
            .hasUnnecessarySpacing();
    }

    /**
     * Removes unnecessary spacing from the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeUnnecessarySpacing(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "div", "h1", "h2", "h3", "h4", "h5")
            .removeUnnecessarySpacing()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a list that is not contiguous.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a list that is not contiguous.
     */
    public static boolean hasFragmentedList(String str)
    {
        return HtmlDocument.hasFragmentedList(str);
    }

    /**
     * Collapses fragmented list in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String fixFragmentedLists(String str)
    {
        return HtmlDocument.builder(str)
            .fixFragmentedLists()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains an ordered list with bad numbering.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains an ordered list with bad numbering
     */
    public static boolean hasBadOrderedList(String str)
    {
        return HtmlDocument.hasBadOrderedList(str);
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains extra line breaks.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains extra line breaks.
     */
    public static boolean hasExtraLineBreaks(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "div", "li")
            .build()
            .hasExtraLineBreaks();
    }

    /**
     * Replaces extra line breaks in the given string with a new paragraph.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String fixExtraLineBreaks(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("li")
            .removeExtraLineBreaks()
            .withTags("p", "div")
            .replaceExtraLineBreaks()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string needs the image-wrapper class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string needs the image-wrapper class.
     */
    public static boolean needsImageWrapperClass(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("figure", "div", "p")
            .build()
            .needsImageWrapperClass();
    }

    /**
     * Adds the image wrapper class to images in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String addImageWrapperClass(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("figure", "div", "p")
            .addImageWrapperClass()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string needs the image-legend class.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string needs the image-legend class.
     */
    public static boolean needsImageLegendClass(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("figure")
            .build()
            .needsImageLegendClass();
    }

    /**
     * Adds the image legend class to image captions in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String addImageLegendClass(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("figure")
            .addImageLegendClass()
            .get();
    }

    /**
     * Returns the list of messages for links with bad external spacing for the given string.
     * @param str The string to search
     * @return The list of messages for links with bad external spacing for the given string
     */
    public static List<String> getBadExternalSpacingLinks(String str)
    {
        List<String> messages = HtmlDocument.builder(str)
            .withTags("p")
            .build()
            .getBadExternalSpacingLinkMessages();

        for(String message : messages)
            logger.warning("Found "+message);

        return messages;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains links with bad external spacing.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains links with bad external spacing.
     */
    public static boolean hasBadExternalSpacingLink(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "li")
            .build()
            .hasBadExternalSpacingLink();
    }

    /**
     * Fix the links with bad external spacing in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String fixBadExternalSpacingLinks(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "li")
            .fixBadExternalSpacingLinks()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a link with anchor text with bad spacing.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a link with anchor text with bad spacing.
     */
    public static boolean hasBadAnchorTextLink(String str)
    {
        return HtmlDocument.hasBadAnchorTextLink(str);
    }

    /**
     * Fix links with anchor text with bad spacing in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String fixBadAnchorTextLinks(String str)
    {
        return HtmlDocument.builder(str)
            .fixBadAnchorTextLinks()
            .get();
    }

    /**
     * Returns the list of messages for split links for the given string.
     * @param str The string to search
     * @return The list of messages for split links for the given string
     */
    public static List<String> getSplitLinks(String str)
    {
        List<String> messages = HtmlDocument.builder(str)
            .withTags("p")
            .build()
            .getSplitLinkMessages();

        for(String message : messages)
            logger.severe("Found "+message);

        return messages;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains an "&amp;nbsp;" character.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains an "&amp;nbsp;" character.
     */
    public static boolean hasNbsp(String str)
    {
        return HtmlDocument.hasNbsp(str);
    }

    /**
     * Replaces "&amp;nbsp;" characters in the given string with normal spaces.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String replaceNbsps(String str)
    {
        return HtmlDocument.builder(str)
            .replaceNbsps()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contain a "&lt;div&gt;" tag.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contain a "&lt;div&gt;" tag.
     */
    public static boolean hasDiv(String str)
    {
        return HtmlDocument.hasDiv(str);
    }

    /**
     * Replaces "&lt;div&gt;" tags in the given string with &lt;p&gt; tags.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String replaceDivs(String str)
    {
        return HtmlDocument.builder(str)
            .replaceDivs()
            .get();
    }

    /**
     * Removes "&lt;div&gt;" tags in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeDivs(String str)
    {
        return HtmlDocument.builder(str)
            .removeDivs()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contain a "&lt;section&gt;" tag.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contain a "&lt;section&gt;" tag.
     */
    public static boolean hasSection(String str)
    {
        return HtmlDocument.hasSection(str);
    }

    /**
     * Removes "&lt;section&gt;" tags in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeSections(String str)
    {
        return HtmlDocument.builder(str)
            .removeSections()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a "&lt;span&gt;" tag.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a "&lt;span&gt;" tag.
     */
    public static boolean hasSpan(String str)
    {
        return HtmlDocument.hasSpan(str);
    }

    /**
     * Removes "&lt;span&gt;" tags in the given string.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeSpans(String str)
    {
        return HtmlDocument.builder(str)
            .removeSpans()
            .get();
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a &lt;h1&gt; tag.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a &lt;h1&gt; tag.
     */
    public static boolean needsReduceHeadings(String str)
    {
        return HtmlDocument.hasH1(str);
    }

    /**
     * Reduces the main headings by one level.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String reduceHeadings(String str)
    {
        return HtmlDocument.builder(str)
            .reduceHeadings()
            .get();
    }

    /**
     * Returns the list of image source messages for the given string.
     * @param str The string to search
     * @return the list of image source messages for the given string
     */
    public static List<String> getImageSources(String str)
    {
        List<String> messages = HtmlDocument.builder(str)
            .withTags("p", "h2")
            .build()
            .getImageSourceMessages();

        for(String message : messages)
            logger.warning("Found "+message);

        return messages;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains an image source.
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains an image source.
     */
    public static boolean hasImageSource(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "h2")
            .build()
            .hasImageSource();
    }

    /**
     * Replaces extra line breaks in the given string contains an image source.
     * @param str The string to amend
     * @return The amended string.
     */
    public static String removeImageSources(String str)
    {
        return HtmlDocument.builder(str)
            .withTags("p", "h2")
            .removeImageSources()
            .get();
    }

    /**
     * Returns the list of duplicate link messages for the given string.
     * @param str The string to search
     * @return the list of duplicate link messages for the given string
     */
    public static List<String> getDuplicateLinks(String str)
    {
        List<String> messages = HtmlDocument.getDuplicateLinkMessages(str);

        for(String message : messages)
            logger.warning("Found "+message);

        return messages;
    }

    /**
     * Returns the list of bad link messages for the given string.
     * @param str The string to search
     * @return the list of bad link messages for the given string
     */
    public static List<String> getBadLinks(String str)
    {
        List<String> messages = HtmlDocument.getBadLinkMessages(str);

        for(String message : messages)
            logger.warning("Found "+message);

        return messages;
    }

    /**
     * Returns <CODE>true</CODE> if the given string contains a link using "http:".
     * @param str The string to search
     * @return <CODE>true</CODE> if the given string contains a link using "http:"
     */
    public static boolean hasBadProtocolLink(String str)
    {
        return HtmlDocument.hasBadProtocolLink(str);
    }

    /**
     * Fixes links using "http:" in the given string with "https:".
     * @param str The string to amend
     * @return The amended string.
     */
    public static String fixBadProtocolLinks(String str)
    {
        return HtmlDocument.builder(str)
            .fixBadProtocolLinks()
            .get();
    }
}