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
            ret = ret.replaceAll(" |%20|%2[Ff]", "-");

            // Remove "thin" spaces
            ret = ret.replaceAll("\\u200a", "");

            // Replace escaped plus, ampersand with plus
            ret = ret.replaceAll("%2[Bb]|%26", "+");

            // Replace some escaped chars with original char
            ret = ret.replaceAll("%24", "\\$");
            ret = ret.replaceAll("%28", "(");
            ret = ret.replaceAll("%29", ")");
            ret = ret.replaceAll("%5[Bb]", "[");
            ret = ret.replaceAll("%5[Dd]", "]");

            // Remove other special characters
            ret = ret.replaceAll("%22|%23|%25|%27|%2[Cc]|%3[Bb]|%3[Ff]|%7[Cc]", "");

            // Remove "combining" accent characters
            ret = ret.replaceAll("[\\u0300-\\u036F]", "");
            ret = ret.replaceAll("%C[CD3]%[89ab][0-9a-f]", "");

            // Remove "cyrillic" characters
            ret = ret.replaceAll("[\\u0400-\\u0467]", "");
            ret = ret.replaceAll("%D[01]%[89ab][0-9a-f]", "");

            // Remove quotes, dashes etc
            ret = ret.replaceAll("'|‘|’|‚|‛|“|”|„|′|″|®|™", "");
            ret = ret.replaceAll("‐|‑|‒|–|—|―|‖|‗|‾	", "-");
            ret = ret.replaceAll("…", "-");
            ret = ret.replaceAll("-+", "-");

            // Remove escape sequences
            ret = ret.replaceAll("%E2%80%[0-9a-fA-F]{2}", ""); // %E2%80%xx
            ret = ret.replaceAll("%F0%9F%[0-9a-fA-F]{2}%[0-9a-fA-F]{2}", ""); // %F0%9F%xx%xx - emojis
            ret = ret.replaceAll("%C2%AE", ""); //®

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

            // Remove any other file extensions included in the filename
            //   as drupal rejects these as a security issue
            if(ret.lastIndexOf(".") != -1)
            {
                ret = ret.replaceAll("(.+)\\.(?:phar|php|pl|py|cgi|asp|js)(\\..+)", "$1$2");
            }
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