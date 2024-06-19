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

package com.opsmatters.media.model.logging;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the type of a log event.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum EventType
{
    UI("UI", "UI", "ui"),
    CRAWLER("CR", "Crawler", "crawler"),
    MONITOR("MN", "Monitor", "monitor");

    private String code;
    private String value;
    private String tag;

    /**
     * Constructor that takes the type code and value.
     * @param code The code for the type
     * @param value The value for the type
     * @param tag The tag for the type
     */
    EventType(String code, String value, String tag)
    {
        this.code = code;
        this.value = value;
        this.tag = tag;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the type.
     * @return The code of the type.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the tag of the type.
     * @return The tag of the type.
     */
    public String tag()
    {
        return tag;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static EventType fromValue(String value)
    {
        EventType[] types = values();
        for(EventType type : types)
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

    /**
     * Returns a list of the event types.
     */
    public static List<EventType> toList()
    {
        List<EventType> ret = new ArrayList<EventType>();

        ret.add(UI);
        ret.add(CRAWLER);
        ret.add(MONITOR);

        return ret;
    }

    /**
     * Returns a list of the event type values.
     */
    public static List<String> toList(boolean blank)
    {
        List<String> ret = new ArrayList<String>();

        if(blank)
            ret.add("");
        for(EventType type : EventType.values())
        {
            if(type.code().length() > 0)
                ret.add(type.value());
        }

        return ret;
    }
}