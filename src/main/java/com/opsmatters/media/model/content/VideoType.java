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

package com.opsmatters.media.model.content;

import com.opsmatters.media.config.admin.AppParameters;

import static com.opsmatters.media.model.admin.AppParameterType.*;
import static com.opsmatters.media.model.admin.AppParameterName.*;

/**
 * Represents a video type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum VideoType
{
    CONFERENCE("Conference"),
    DEMO("Demo"),
    HOW_TO("How to"),
    INTERVIEW("Interview"),
    MEETUP("Meetup"),
    PANEL("Panel"),
    PODCAST("Podcast"),
    SEMINAR("Seminar"),
    USER_GROUP("User Group"),
    WEBINAR("Webinar"),
    WEBCAST("Webcast");

    private String value;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    VideoType(String value)
    {
        this.value = value;
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
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static VideoType fromValue(String value)
    {
        VideoType[] types = values();
        for(VideoType type : types)
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
     * Guesses the video type from the given description and duration.
     */
    public static VideoType guess(String text, long duration)
    {
        text = text.toLowerCase();
        VideoType ret = test(text, WEBINAR);
        if(ret == null)
            ret = test(text, WEBCAST);
        if(ret == null)
            ret = test(text, PODCAST);
        if(ret == null)
            ret = test(text, CONFERENCE);
        if(ret == null)
            ret = test(text, MEETUP);
        if(ret == null)
            ret = test(text, SEMINAR);
        if(ret == null)
            ret = test(text, USER_GROUP);
        if(ret == null)
            ret = test(text, PANEL);
        if(ret == null)
            ret = test(text, INTERVIEW);
        if(ret == null)
            ret = test(text, HOW_TO);
        if(ret == null)
            ret = test(text, DEMO);

        long min = AppParameters.get(UI, MIN_WEBINAR_DURATION).getValueAsLong();
        if((ret == null || ret == DEMO || ret == HOW_TO) && duration > min)
            ret = WEBINAR;

        return ret != null ? ret : DEMO;
    }

    /**
     * Returns the type for the given text.
     */
    private static VideoType test(String text, VideoType type)
    {
        if(text.indexOf(type.value().toLowerCase()) != -1)
            return type;
        return null;
    }
}