/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.config.content.generator;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents an event page config for the config generator.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum EventPage
{
    WEBINARS("Webinars", "webinars"),
    ZOOM("Zoom", "zoom"),
    BRIGHTTALK("BrightTALK", "brighttalk"),
    GOTOWEBINAR("GoToWebinar", "gotowebinar"),
    ON24("on24", "on24"),
    LIVESTORM("Livestorm", "livestorm"); 

    private String value;
    private String tag;

    /**
     * Constructor that takes the page value.
     * @param value The value for the page
     * @param tag The tag for the page
     */
    EventPage(String value, String tag)
    {
        this.value = value;
        this.tag = tag;
    }

    /**
     * Returns the value of the page.
     * @return The value of the page.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the tag of the page.
     * @return The tag of the page.
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
    public static EventPage fromValue(String value)
    {
        EventPage[] types = values();
        for(EventPage type : types)
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
     * Returns a list of the pages.
     */
    public static List<EventPage> toList()
    {
        List<EventPage> ret = new ArrayList<EventPage>();

        ret.add(WEBINARS);
        ret.add(ZOOM);
        ret.add(BRIGHTTALK);
        ret.add(GOTOWEBINAR);
        ret.add(ON24);
        ret.add(LIVESTORM);

        return ret;
    }
}