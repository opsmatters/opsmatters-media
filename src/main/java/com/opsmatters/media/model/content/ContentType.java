/*
 * Copyright 2018 Gerald Curley
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

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a content type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContentType
{
    ORGANISATION("ORG", "Organisation", "organisations", "glyphicon-th-list"),
    VIDEO("VD", "Video", "videos", "glyphicon-film"),
    ROUNDUP("RP", "Roundup", "roundups", "glyphicon-file"),
    POST("PO", "Post", "posts", "glyphicon-file"),
    EVENT("EV", "Event", "events", "glyphicon-calendar"),
    PUBLICATION("PB", "Publication", "publications", "glyphicon-book"),
    PROJECT("PR", "Project", "projects", "glyphicon-tasks"),
    TOOL("TL", "Tool", "tools", "glyphicon-wrench"),
    JOB("JB", "Job", "jobs", "glyphicon-briefcase"),
    ARTICLE("", "Article", "", "glyphicon-file");

    private String code;
    private String value;
    private String tag;
    private String title;
    private String icon;

    /**
     * Constructor that takes the type code, value and tag.
     * @param code The code for the type
     * @param value The value for the type
     * @param tag The tag for the type
     * @param icon The glyphicon for the type
     */
    ContentType(String code, String value, String tag, String icon)
    {
        this.code = code;
        this.value = value;
        this.tag = tag;
        this.title = value+"s";
        this.icon = icon;
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
     * Returns the title of the type.
     * @return The title of the type.
     */
    public String title()
    {
        return title;
    }

    /**
     * Returns the glyphicon of the type.
     * @return The glyphicon of the type.
     */
    public String icon()
    {
        return icon;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has social posts.
     */
    public boolean hasSocialPosts()
    {
        return this == VIDEO
            || this == ROUNDUP
            || this == POST
            || this == EVENT
            || this == PUBLICATION
            || this == TOOL;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has features.
     */
    public boolean isFeaturesType()
    {
        return this == PROJECT
            || this == TOOL;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has tags.
     */
    public boolean isTagsType()
    {
        return this == VIDEO
            || this == ROUNDUP
            || this == PUBLICATION
            || this == POST;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has technologies.
     */
    public boolean isTechnologiesType()
    {
        return this == JOB;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has tracking.
     */
    public boolean isTrackingType()
    {
        return this == VIDEO
            || this == ROUNDUP
            || this == EVENT
            || this == PUBLICATION
            || this == TOOL;
    }

    /**
     * Returns <CODE>true</CODE> if this content type is in the newsletter.
     */
    public boolean isNewsletterType()
    {
        return this == VIDEO
            || this == ROUNDUP
            || this == POST;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static ContentType fromValue(String value)
    {
        ContentType[] types = values();
        for(ContentType type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns the type for the given tag.
     * @param value The type tag
     * @return The type for the given tag
     */
    public static ContentType fromTag(String tag)
    {
        ContentType[] types = values();
        for(ContentType type : types)
        {
            if(type.tag().equals(tag))
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
     * Returns a list of the types.
     */
    public static List<ContentType> toList()
    {
        List<ContentType> ret = new ArrayList<ContentType>();

        ret.add(VIDEO);
        ret.add(ROUNDUP);
        ret.add(POST);
        ret.add(EVENT);
        ret.add(PUBLICATION);
        ret.add(PROJECT);
        ret.add(TOOL);
        ret.add(JOB);

        return ret;
    }

    /**
     * Returns a list of the type values.
     */
    public static List<String> toList(String blank)
    {
        List<String> ret = new ArrayList<String>();

        if(blank != null)
            ret.add(blank);
        for(ContentType type : ContentType.values())
            ret.add(type.value());

        return ret;
    }
}