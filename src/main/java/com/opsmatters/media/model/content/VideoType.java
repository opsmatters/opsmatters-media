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

package com.opsmatters.media.model.content;

/**
 * Represents a content video type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum VideoType
{
    BLOG("blog", "Blog"),
    CASE_STUDY("case study", "Case Study"),
    CONFERENCE("conference", "Conference"),
    DEMO("demo", "Demo"),
    EVENT("event", "Event"),
    HOW_TO("how to", "How to"),
    INTERVIEW("interview", "Interview"),
    MEETUP("meetup", "Meetup"),
    NEWS("news", "News"),
    OTHER("other", "Other"),
    PANEL("panel", "Panel"),
    PODCAST("podcast", "Podcast"),
    PRESENTATION("presentation", "Presentation"),
    SALES("sales", "Sales"),
    SEMINAR("seminar", "Seminar"),
    USER_GROUP("user group", "User Group"),
    WEBCAST("webcast", "Webcast"),
    WEBINAR("webinar", "Webinar");

    private String code;
    private String value;

    /**
     * Constructor that takes the type code and value.
     * @param code The code for the type
     * @param value The value for the type
     */
    VideoType(String code, String value)
    {
        this.code = code;
        this.value = value;
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
     * Returns the type for the given text.
     * @param text The text to search
     * @param deflt The default video type to return
     * @return The type for the given text
     */
    public static VideoType fromText(String text, VideoType deflt)
    {
        VideoType ret = deflt;
        text = text.toLowerCase();

        if(text.indexOf(WEBINAR.code()) != -1)
            ret = WEBINAR;
        else if(text.indexOf(WEBCAST.code()) != -1)
            ret = WEBCAST;
        else if(text.indexOf(PODCAST.code()) != -1)
            ret = PODCAST;
        else if(text.indexOf(CONFERENCE.code()) != -1)
            ret = CONFERENCE;
        else if(text.indexOf(MEETUP.code()) != -1)
            ret = MEETUP;
        else if(text.indexOf(SEMINAR.code()) != -1)
            ret = SEMINAR;
        else if(text.indexOf(USER_GROUP.code()) != -1)
            ret = USER_GROUP;
        else if(text.indexOf(PANEL.code()) != -1)
            ret = PANEL;
        else if(text.indexOf(INTERVIEW.code()) != -1)
            ret = INTERVIEW;
        else if(text.indexOf(HOW_TO.code()) != -1)
            ret = HOW_TO;
        else if(text.indexOf(DEMO.code()) != -1)
            ret = DEMO;

        return ret;
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