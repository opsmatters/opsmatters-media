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

package com.opsmatters.media.util;

import java.text.DecimalFormat;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.opsmatters.media.config.content.SummaryConfiguration;

/**
 * A set of utility methods to perform miscellaneous tasks related to formatting.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FormatUtils
{
    private static final Logger logger = Logger.getLogger(FormatUtils.class.getName());

    /**
     * Private constructor as this class shouldn't be instantiated.
     */
    private FormatUtils()
    {
    }    

    /**
     * Returns the given bytes number formatted as KBytes, MBytes or GBytes as appropriate.
     * @param bytes The bytes to be converted
     * @return The given bytes number formatted as KBytes, MBytes or GBytes as appropriate
     */
    static public String getFormattedBytes(long bytes)
    {
        return getFormattedBytes(bytes, "Bytes", "0.0#");
    }

    /**
     * Returns the given bytes number formatted as KBytes, MBytes or GBytes as appropriate.
     * @param bytes The bytes to be converted
     * @param units The units to be displayed with the converted bytes
     * @return The given bytes number formatted as KBytes, MBytes or GBytes as appropriate
     */
    static public String getFormattedBytes(long bytes, String units)
    {
        return getFormattedBytes(bytes, units, "0.0#");
    }

    /**
     * Returns the given bytes number formatted as KBytes, MBytes or GBytes as appropriate.
     * @param bytes The bytes to be converted
     * @param units The units to be displayed with the converted bytes
     * @param format The format to use to display the bytes
     * @return The given bytes number formatted as KBytes, MBytes or GBytes as appropriate
     */
    static public String getFormattedBytes(long bytes, String units, String format)
    {
        double num = bytes;
        String[] prefix = {"", "K", "M", "G", "T"};
        int count = 0;

        while(num >= 1024.0)
        {
            num = num/1024.0;
            ++count;
        }

        DecimalFormat f = new DecimalFormat(format);
        return f.format(num)+" "+prefix[count]+units;
    }

    /**
     * Returns the given fractional percentage formatted as "0.0#%".
     * @param t The percentage to be formatted
     * @return The given fractional percentage formatted as "0.0#%"
     */
    static public String getFormattedPercentage(double t)
    {
        DecimalFormat f = new DecimalFormat("0.0#");
        return f.format(t)+"%";
    }

    /**
     * Returns the given HTML content description formatted as a plain text summary.
     * @param description The description to be formatted
     * @return The formatted summary
     */
    static public String getFormattedSummary(String description, SummaryConfiguration config)
    {
        if(config != null)
            return getFormattedSummary(description, config.getMinLength(), config.getMaxLength(),
                config.getMinParagraph(), false);
        return null;
    }

    /**
     * Returns the given HTML content description formatted as a plain text summary.
     * @param description The description to be formatted
     * @return The formatted summary
     */
    static public String getFormattedSummary(String description, int minLength, int maxLength,
        int minParagraph, boolean debug)
    {
        StringBuilder ret = new StringBuilder();
        String carryover = null;

        if(description != null)
        {
            // Extract the contents of the 1st paragraph
            Matcher m = Pattern.compile("<p>(.*?)<\\/p>", Pattern.DOTALL).matcher(description);

            // Matches text starting with a number eg. "1." or "01:23"
            Pattern p = Pattern.compile("\\d+[\\:\\.].+", Pattern.DOTALL);

            if(debug)
                logger.info(String.format("1: getFormattedSummary: description=%s minLength=%d maxLength=%d minParagraph=%d",
                    description, minLength, maxLength, minParagraph));

            while(m.find())
            {
                String text = m.group(1);

                if(debug)
                    logger.info(String.format("2: getFormattedSummary: text=%s carryover=%s", text, carryover));

                // Exclude text that starts with numbers
                if(p.matcher(text).matches())
                  continue;

                if(debug)
                    logger.info(String.format("2a: getFormattedSummary: text=%s carryover=%s", text, carryover));

                // If the summary contains one or more breaks
                StringBuilder buff = new StringBuilder();
                String[] segments = text.split("<br>");
                for(String segment : segments)
                {
                    segment = segment.trim();

                    // If the segment contains a link
                    if(segment.indexOf("http") != -1 || segment.indexOf("www.") != -1)
                    {
                        // If the segment contains a full stop
                        int pos = segment.lastIndexOf(". ");
                        if(pos != -1)
                        {
                            String str = segment.substring(0, pos+1).trim();
                            String rest = segment.substring(pos+2);

                            if(buff.length() > 0 && buff.charAt(buff.length()-1) != ' ')
                                buff.append(" ");

                            // Throw away the text if it contains a link
                            if(str.indexOf("http") != -1 || str.indexOf("www.") != -1)
                                buff.append(rest);
                            else if(rest.indexOf("http") != -1 || rest.indexOf("www.") != -1)
                                buff.append(str);
                        }
                    }
                    else if(segment.endsWith(":")) // Usually indicates a list follows
                    {
                        int pos = segment.lastIndexOf(". ");
                        if(pos != -1)
                        {
                            segment = segment.substring(0, pos+1).trim();
                            if(buff.length() > 0 && buff.charAt(buff.length()-1) != ' ')
                                buff.append(" ");
                            buff.append(segment);
                        }
                    }
                    else if(!segment.startsWith("#") && !segment.startsWith("=")) // Ignore hashtags or delimiter
                    {
                        if(buff.length() > 0 && buff.charAt(buff.length()-1) != ' ')
                            buff.append(" ");
                        buff.append(segment);
                    }
                }

                text = buff.toString();

                // If the summary contains a list
                int pos = text.indexOf("<ul>");
                if(pos == -1)
                    pos = text.indexOf("<ol>");
                if(pos != -1)
                {
                    text = text.substring(0, pos).trim();
                    pos = text.lastIndexOf(". ");
                    if(pos != -1)
                        text = text.substring(0, pos+1).trim();
                }

                // Remove linefeeds
                text = text.replaceAll("[ \t]*(\r\n|\n)+[ \t]*", " ");
                text = text.trim();

                if(debug)
                    logger.info(String.format("3: getFormattedSummary: text=%s", text));

                if(text.indexOf("http") != -1 || text.indexOf("www.") != -1)
                {
                    // Skip paragraph if it contains a link
                    continue;
                }
                else if(p.matcher(text).matches()) // Ignore text that starts with numbers
                {
                    // Skip paragraph if it starts with numbers
                    continue;
                }
                else if(text.startsWith("#") || text.startsWith("=")) // Ignore Hashtags or delimiter
                {
                    // Skip paragraph if it starts with hashtags
                    continue;
                }

                if(debug)
                    logger.info(String.format("4: getFormattedSummary: text=%s", text));

                // Carry over very short paragraphs and add to the next
                if(carryover != null)
                {
                    StringBuilder str = new StringBuilder(carryover);
                    str.append(" ");
                    str.append(text);
                    text = str.toString();
                }

                if(debug)
                    logger.info(String.format("5: getFormattedSummary: text=%s text.length=%d carryover=%s",
                        text, text.length(), carryover));

                if(text.length() > 0 && text.length() < minParagraph) // Too short so just carry it forward
                {
                    carryover = text;

                    if(debug)
                        logger.info(String.format("6: getFormattedSummary: text=%s text.length=%d carryover=%s",
                            text, text.length(), carryover));
                }
                else
                {
                    // Exit if the addition would take us over the maximum
                    if(ret.length() > 0 && (ret.length()+text.length()) > maxLength)
                    {
                        if(debug)
                            logger.info(String.format("7: getFormattedSummary: break1: text=%s text.length=%d ret=%s ret.length=%d",
                                text, text.length(), ret, ret.length()));
                        break;
                    }

                    if(ret.length() > 0 && text.length() > 0)
                        ret.append(" ");
                    ret.append(text);
                    carryover = null;

                    // Exit if the addition has taken us over the minimum
                    if(ret.length() > minLength)
                    {
                        if(debug)
                            logger.info(String.format("8: getFormattedSummary: break2: ret=%s ret.length=%d",
                                ret, ret.length()));
                        break;
                    }
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

            if(debug)
                logger.info(String.format("9: getFormattedSummary: ret=%s ret.length=%d carryover=%s carryover.length=%d",
                    ret, ret.length(), carryover, carryover.length()));
        }

        // Summary should end with full stop if it ends with a semi-colon
        if(ret.length() > 0 && ret.charAt(ret.length()-1) == ':')
        {
            ret.setCharAt(ret.length()-1, '.');
        }

        if(debug)
            logger.info(String.format("10: getFormattedSummary: ret=%s ret.length=%d", ret, ret.length()));

        return ret.toString();
    }

    /**
     * Returns the given content description formatted as HTML.
     * @param description The description to be formatted
     * @return The formatted description
     */
    static public String getFormattedDescription(String description)
    {
        String ret = description;

        if(ret != null && ret.length() > 0 && !ret.equals(StringUtils.EMPTY))
        {
            // Wrap in a <p> tag
            ret = String.format("<p>%s</p>", ret);

            // Replace special characters
            ret = ret.replaceAll("→", "->");
            ret = ret.replaceAll("►", ">");

            // Turn linefeeds into <br> tags
            ret = ret.replaceAll("\r\n?|\n", "<br>");

            // Turn rows of dashes or stars into paragraphs
            ret = ret.replaceAll("<br>(-|\\*)+<br>", "<br><br>");

            // Turn "<br>-", "<br>--", "<br>*", "<br>**", "<br>•, <br>▶" into <li> tags for <ul> list
            ret = ret.replaceAll("(<br>)+( )*(-|\\*|\\u2022|\\u25cf|\\u25b6|\\u25aa\\ufe0f)+( )*", "<li> ");

            // Add a linefeed to <li> tags to improve readability
            ret = ret.replaceAll("<li>", "\n<li>");

            // Turn "<br>1.", "<br>2.", "<br>3.", etc into <oli> tags to indicate an <ol> list
            ret = ret.replaceAll("(<br>)+[ ]*\\d+[\\. ]+", "<oli> ");

            // Add a linefeed to <oli> tags to improve readability
            ret = ret.replaceAll("<oli>", "\n<oli>");

            // Remove blank paragraphs
            ret = ret.replaceAll("<br>[ ]+<br>", "<br>");

            // Turn multiple line breaks into <p> tags
            ret = ret.replaceAll("<br>\\s*<br>", "</p>\n<p>");

            // Add a linefeed to <br> tags to improve readability
            ret = ret.replaceAll("<br>", "\n<br>");

            // Remove empty <li> tags
            ret = ret.replaceAll("\n<li>\\s*\n", "\n");

            // Process bulleted lists <ul> and <ol>
            Pattern p = Pattern.compile("<p>(.*?)((?:<li>.*?)+)(\n<br>.*)*</p>(.*)", Pattern.DOTALL);
            if(ret.indexOf("<li>") != -1)  // unordered list
            {
                ret = getFormattedList(p, "ul", ret);
            }
            else if(ret.indexOf("<oli>") != -1)  // ordered list
            {
                ret = ret.replace("<oli>", "<li>");
                ret = getFormattedList(p, "ol", ret);
            }

            // Remove <br> if directly following a <p>
            ret = ret.replaceAll("<p>\\s*<br>", "<p>");

            // Remove empty paragraphs
            ret = ret.replaceAll("\n<p>\\s*</p>", "");

            // Replace URLs with hyperlinks
            ret = StringUtils.replaceUrls(ret);
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
    static private String getFormattedList(Pattern p, String listTag, String str)
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
                    suffix2 = getFormattedList(p, listTag, suffix2.trim()).trim();
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

    /**
     * Returns the given image filename formatted for a roundup post.
     * @param image The image name to be formatted
     * @return The formatted image
     */
    static public String getFormattedImageFilename(String image)
    {
        String ret = image;

        if(ret != null)
        {
            // Replace spaces with dashes
            ret = ret.replaceAll(" |%20", "-");

            // Replace escaped plus, ampersand with plus
            ret = ret.replaceAll("%2B|%26", "+");

            // Replace some escaped chars with original char
            ret = ret.replaceAll("%24", "\\$");
            ret = ret.replaceAll("%28", "(");
            ret = ret.replaceAll("%29", ")");
            ret = ret.replaceAll("%5B", "[");
            ret = ret.replaceAll("%5D", "]");

            // Remove other special characters
            ret = ret.replaceAll("%23|%25|%27|%2C|%3B|%3F|%7C|%CC%81|%CC%83", "");

            // Remove quotes, dashes etc
            ret = ret.replaceAll("'|‘|’|‚|‛|“|”|„|′|″", "");
            ret = ret.replaceAll("‐|‑|‒|–|—|―|‖|‗|‾	", "-");
            ret = ret.replaceAll("…", "-");
            ret = ret.replaceAll("-+", "-");

            // Remove escape sequences
            ret = ret.replaceAll("%E2%80%[0-9a-fA-F]{2}", ""); // %E2%80%xx
            ret = ret.replaceAll("%F0%9F%[0-9a-fA-F]{2}%[0-9a-fA-F]{2}", ""); // %F0%9F%xx%xx - emojis

            // Remove illegal characters
            ret = ret.replaceAll("\\*", "");

            // Remove image sizes
            ret = ret.replaceAll("[-_\\.]\\d{2,4}x\\d{2,4}", "");

            // Remove @?x multipler
            ret = ret.replaceAll("@\\dx", "");
            ret = ret.replaceAll("%40\\dx", "");

            // Remove ?00x multipler
            ret = ret.replaceAll("-\\d00x", "");

            // Remove #protocol after extension
            ret = ret.replaceAll("(.+)\\.(\\w+)#(.+)", "$1\\.$2");

            // Remove query parameters
            ret = ret.replaceAll("(.*)\\?(.*)", "$1");
        }

        return ret;
    }

    /**
     * Returns the given url parts formatted using a base and relative path.
     */
    static public String getFormattedUrl(String basePath, String url, boolean removeParameters)
    {
        StringBuilder ret = new StringBuilder();

        if(url != null)
        {
            // Replace any special characters in the URL
            url = url.replaceAll(" ", "%20");

            // Remove any leading "../" from the URL
            if(url.startsWith("../"))
                url = url.substring(2);

            // Remove query parameters
            if(removeParameters)
                url = url.replaceAll("(.*)[\\?#](.*)", "$1");

            // Remove trailing slash to normalise URLs
            if(url.length() > 3 && url.endsWith("/"))
                url = url.substring(0, url.length()-1);
        }

        if(basePath != null && basePath.length() > 0 && FileUtils.isRelativePath(url))
        {
            if(basePath.endsWith("/"))
                basePath = basePath.substring(0, basePath.length()-1);
            ret.append(basePath);
            if(!url.startsWith("/"))
                ret.append("/");
        }

        if(url != null)
        {
            if(ret.length() == 0 && url.startsWith("//"))
                ret.append("https:");
            ret = ret.append(url);
        }

        return ret.toString();
    }

    /**
     * Returns the given string formatted as a url using a base and relative path.
     */
    static public String generateUrl(String basePath, String str)
    {
        String url = str;

        // Look for an illegal character to truncate on
        int pos = -1;
        for(int i = 0; i < url.length() && pos == -1; i++)
        {
            char c = url.charAt(i);
            if(c == ':' || c == ',')
                pos = i;
        }

        if(pos != -1)
            url = url.substring(0, pos);

        url = url.toLowerCase().replaceAll("[ ‘'’]", "-");
        return getFormattedUrl(basePath, url, false);
    }

    /**
     * Appends a timestamp parameter to the given URL to prevent caching.
     */
    static public String addAntiCacheParameter(String url)
    {
        return new StringBuilder(url)
            .append(url.indexOf("?") == -1 ? "?" : "&")
            .append("ts=").append(System.currentTimeMillis())
            .toString();
    }
}