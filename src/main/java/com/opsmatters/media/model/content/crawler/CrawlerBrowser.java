/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.model.content.crawler;

/**
 * Represents a content crawler browser.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum CrawlerBrowser
{
    HTMLUNIT("htmlunit"),
    CHROME("chrome"),
    UNDETECTED_CHROME("undetected-chrome"),
    FIREFOX("firefox");

    private String value;

    /**
     * Constructor that takes the browser value.
     * @param value The value for the browser
     */
    CrawlerBrowser(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the browser
     * @return The value of the browser.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static CrawlerBrowser fromValue(String value)
    {
        CrawlerBrowser[] types = values();
        for(CrawlerBrowser type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}