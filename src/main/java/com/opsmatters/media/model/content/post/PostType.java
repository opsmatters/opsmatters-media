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

package com.opsmatters.media.model.content.post;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a post type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum PostType
{
    BLOG("Blog"),
    PRESS_RELEASE("Press Release");

    private String value;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    PostType(String value)
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
    public static PostType fromValue(String value)
    {
        PostType[] types = values();
        for(PostType type : types)
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
     * Guesses the post type from the given tags.
     */
    public static PostType guess(List<String> tagsList)
    {
        PostType ret = null;
        PostType[] types = values();
        for(PostType type : types)
        {
            if(tagsList.contains(type.value()))
            {
                ret = type;
                break;
            }
        }

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
        for(PostType type : PostType.values())
            ret.add(type.value());

        return ret;
    }
}