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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A set of utility methods to perform miscellaneous tasks related to formatting.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FormatUtils
{
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
    static public String getFormattedSummary(String description)
    {
        String ret = description;

        if(ret != null)
        {
            // Extract the contents of the 1st paragraph
            Matcher m = Pattern.compile("<p>(.*?)</p>", Pattern.DOTALL).matcher(ret);
            if(m.find())
            {
                ret = m.group(1);
            }

            // If the summary contains a break
            int pos = ret.indexOf("<br>");
            if(pos != -1)
            {
                String str = ret.substring(0, pos).trim();
                String rest = ret.substring(pos+("<br>".length()));
                if(rest.indexOf("http") != -1) // Throw away the rest if it contains a link
                    ret = str;
                else
                    ret = ret.replaceAll("<br>", ""); // Otherwise just remove breaks
            }

            // Remove linefeeds
            ret = ret.replaceAll("[ \t]*(\r\n|\n)+[ \t]*", " ");

            // Remove <ul>/<ol> tags and first <li>
            ret = ret.replaceAll("<(u|o)l><li>|</(u|o)l>", "");

            // Turn subsequent <li> tags into comma to "flatten" the list items
            ret = ret.replaceAll("<li>", ",");
        }

        return ret;
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

            // Turn linefeeds into <br> tags
            ret = ret.replaceAll("\r\n?|\n", "<br>");

            // Turn rows of dashes or stars into paragraphs
            ret = ret.replaceAll("<br>(-|\\*)+<br>", "<br><br>");

            // Turn "<br>-", "<br>--", "<br>*", "<br>**", "<br>•" into <li> tags for <ul> list
            ret = ret.replaceAll("(<br>)+( )*(-|\\*|\\u2022)+( )*", "<li> ");

            // Add a linefeed to <li> tags to improve readability
            ret = ret.replaceAll("<li>", "\n<li>");

            // Turn "<br>1.", "<br>2.", "<br>3.", etc into <oli> tags to indicate an <ol> list
            ret = ret.replaceAll("(<br>)+( )*(\\d)+(\\.)*( )*", "<oli> ");

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
            ret = ret.replaceAll(" ", "-");

            // Replace escaped spaces with dashes
            ret = ret.replaceAll("%20", "-");

            // Replace escaped brackets with brackets
            ret = ret.replaceAll("%28", "(");
            ret = ret.replaceAll("%29", ")");

            // Remove escaped hashes
            ret = ret.replaceAll("%23", "");

            // Remove quotes etc
            ret = ret.replaceAll("'|‘|’|‚|‛|“|”|„|′|″", "");
            ret = ret.replaceAll("–|—|―|‗|‾	", "-");
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
            // Replace any special characters in the URL
            url = url.replaceAll(" ", "%20");

            if(ret.length() == 0 && url.startsWith("//"))
                ret.append("https:");

            // Remove query parameters
            if(removeParameters)
                url = url.replaceAll("(.*)[\\?#](.*)", "$1");

            ret = ret.append(url);
        }

        return ret.toString();
    }
}