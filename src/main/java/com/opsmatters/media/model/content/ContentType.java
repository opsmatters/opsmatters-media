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
    ARTICLE("", "Article"); // pseudo type

    private String code;
    private String value;
    private String tag;
    private String title;
    private String icon;

    /**
     * Constructor that takes the type code, value, tag, icon and social flag.
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
     * Constructor that takes the type code and value.
     * @param code The code for the type
     * @param value The value for the type
     */
    ContentType(String code, String value)
    {
        this.code = code;
        this.value = value;
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
     * Returns <CODE>true</CODE> if this content type is an article.
     */
    public boolean isArticleType()
    {
        return this == VIDEO
            || this == ROUNDUP
            || this == POST;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has social posts.
     */
    public boolean hasSocial()
    {
        return this == ORGANISATION
            || this == ROUNDUP
            || this == POST;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has social templates.
     */
    public boolean hasSocialTemplates()
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
            || this == POST
            || this == PUBLICATION;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has tracking.
     */
    public boolean isTrackingType()
    {
        return this == ROUNDUP
            || this == EVENT
            || this == PUBLICATION
            || this == TOOL;
    }

    /**
     * Returns <CODE>true</CODE> if this content type can be promoted.
     */
    public boolean isPromoteType()
    {
        return this == VIDEO
            || this == ROUNDUP
            || this == POST
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
     * Returns <CODE>true</CODE> if this content type can be featured.
     */
    public boolean isFeaturedType()
    {
        return this == ROUNDUP
            || this == POST;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has a crawled image filename.
     */
    public boolean isImageRejectType()
    {
        return this == ROUNDUP
            || this == PUBLICATION;
    }

    /**
     * Returns <CODE>true</CODE> if this content type has a crawled summary.
     */
    public boolean isSummaryRejectType()
    {
        return this == ROUNDUP
            || this == VIDEO
            || this == EVENT
            || this == PUBLICATION;
    }

    /**
     * Returns the summary minimum length for the type.
     */
    public int summaryMin()
    {
        if(this == ROUNDUP)
            return 550;
        return 400;
    }

    /**
     * Returns the summary maximum length for the type.
     */
    public int summaryMax()
    {
        if(this == ROUNDUP)
            return 750;
        return 600;
    }

    /**
     * Returns the summary error length for the type.
     */
    public int summaryError()
    {
        if(isArticleType())
            return 750;
        return 600;
    }

    /**
     * Returns the summary warning length for the type.
     */
    public int summaryWarn()
    {
        if(isArticleType())
            return 640;
        return 500;
    }

    /**
     * Returns the content source for the type.
     */
    public ContentSource source()
    {
        if(this == ROUNDUP)
            return ContentSource.PAGE;
        else if(this == VIDEO)
            return ContentSource.CHANNEL;
        return ContentSource.STORE;
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

        return ret;
    }

    /**
     * Returns a list of the type values.
     */
    public static List<String> toList(boolean blank)
    {
        List<String> ret = new ArrayList<String>();

        if(blank)
            ret.add("");
        for(ContentType type : ContentType.values())
        {
            if(type.code().length() > 0)
                ret.add(type.value());
        }

        return ret;
    }
}