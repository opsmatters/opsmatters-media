/*
 * Copyright 2025 Gerald Curley
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
 * Represents a metatag for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum Metatag
{
    TITLE("title"),
    DESCRIPTION("description"),
    OG_TITLE("og_title"),
    OG_DESCRIPTION("og_description"),
    CANONICAL_URL("canonical_url");

    private String value;

    /**
     * Constructor that takes the metatag value.
     * @param value The value for the metatag
     */
    Metatag(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the metatag.
     * @return The value of the metatag.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the metatag.
     * @return The value of the metatag.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the metatag for the given value.
     * @param value The metatag value
     * @return The metatag for the given value
     */
    public static Metatag fromValue(String value)
    {
        Metatag[] metatags = values();
        for(Metatag metatag : metatags)
        {
            if(metatag.value().equals(value))
                return metatag;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of metatags.
     * @param value The metatag value
     * @return <CODE>true</CODE> if the given value is contained in the list of metatags
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}