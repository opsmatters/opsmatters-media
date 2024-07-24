/*
 * Copyright 2023 Gerald Curley
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

package com.opsmatters.media.model.logging;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the category of a log event.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum LogEventCategory
{
    TEASER("Teaser", "teaser"),
    ARTICLE("Article", "article"),
    MANAGER("Manager", "manager");

    private String value;
    private String tag;

    /**
     * Constructor that takes the category value.
     * @param value The value for the category
     * @param tag The tag for the category
     */
    LogEventCategory(String value, String tag)
    {
        this.value = value;
        this.tag = tag;
    }

    /**
     * Returns the value of the category.
     * @return The value of the category.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the category.
     * @return The value of the category.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the tag of the category.
     * @return The tag of the category.
     */
    public String tag()
    {
        return tag;
    }

    /**
     * Returns the category for the given value.
     * @param value The category value
     * @return The category for the given value
     */
    public static LogEventCategory fromValue(String value)
    {
        LogEventCategory[] categories = values();
        for(LogEventCategory category : categories)
        {
            if(category.value().equals(value))
                return category;
        }

        return null;
    }

    /**
     * LoReturns <CODE>true</CODE> if the given value is contained in the list of categories.
     * @param value The category value
     * @return <CODE>true</CODE> if the given value is contained in the list of categories
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns a list of the event categories.
     */
    public static List<LogEventCategory> toList()
    {
        List<LogEventCategory> ret = new ArrayList<LogEventCategory>();

        ret.add(TEASER);
        ret.add(ARTICLE);
        ret.add(MANAGER);

        return ret;
    }

    /**
     * Returns a list of the event category values.
     */
    public static List<String> toList(boolean blank)
    {
        List<String> ret = new ArrayList<String>();

        if(blank)
            ret.add("");
        for(LogEventCategory category : LogEventCategory.values())
            ret.add(category.value());

        return ret;
    }
}