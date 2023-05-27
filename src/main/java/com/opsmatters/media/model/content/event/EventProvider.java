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

package com.opsmatters.media.model.content.event;

/**
 * Represents an event provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum EventProvider
{
    ZOOM("zoom", "Zoom", "zoom.us"),
    BRIGHTTALK("brighttalk", "BrightTALK", "brighttalk.com"),
    GOTOWEBINAR("gotowebinar", "GoToWebinar", "gotowebinar.com"),
    ON24("on24", "on24", "on24.com"),
    LIVESTORM("livestorm", "Livestorm", "livestorm.co"),
    HOPIN("hopin", "Hopin", "hopin.com");

    private String code;
    private String value;
    private String domain;

    /**
     * Constructor that takes the provider value.
     * @param code The code for the provider
     * @param value The value for the provider
     * @param domain The domain for the provider
     */
    EventProvider(String code, String value, String domain)
    {
        this.code = code;
        this.value = value;
        this.domain = domain;
    }

    /**
     * Returns the code of the provider.
     * @return The code of the provider.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the provider.
     * @return The value of the provider.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the domain of the provider.
     * @return The domain of the provider.
     */
    public String domain()
    {
        return domain;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static EventProvider fromCode(String code)
    {
        EventProvider[] types = values();
        for(EventProvider type : types)
        {
            if(type.code().equals(code))
                return type;
        }

        return null;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static EventProvider fromValue(String value)
    {
        EventProvider[] types = values();
        for(EventProvider type : types)
        {
            if(type.value().equals(value))
                return type;
        }

        return null;
    }

    /**
     * Returns the type for the given url.
     * @param url The type url
     * @return The type for the given url
     */
    public static EventProvider fromUrl(String url)
    {
        EventProvider[] types = values();
        for(EventProvider type : types)
        {
            if(url.indexOf(type.domain()) != -1)
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