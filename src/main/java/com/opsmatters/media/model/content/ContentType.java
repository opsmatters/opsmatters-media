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

/**
 * Represents a content type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContentType
{
    ORGANISATION("organisations", "Organisations"),
    VIDEO("videos", "Videos"),
    ROUNDUP("roundups", "Roundups"),
    POST("posts", "Posts"),
    EVENT("events", "Events"),
    WHITE_PAPER("white-papers", "White Papers"),
    EBOOK("ebooks", "EBooks"),
    PROJECT("projects", "Projects"),
    TOOL("tools", "Tools");

    private String value;
    private String title;

    /**
     * Constructor that takes the type value and title.
     * @param value The value for the type
     * @param title The title for the type
     */
    ContentType(String value, String title)
    {
        this.value = value;
        this.title = title;
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
     * Returns the title of the type.
     * @return The title of the type.
     */
    public String title()
    {
        return title;
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
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}