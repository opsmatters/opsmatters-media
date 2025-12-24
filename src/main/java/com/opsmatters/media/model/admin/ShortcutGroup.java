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

package com.opsmatters.media.model.admin;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the group of a shortcut.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ShortcutGroup
{
    GENERAL("General", "general"),
    CONTENT("Content", "content"),
    ALL("All", ""); // Pseudo status

    private String value;
    private String tag;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     * @param value The tag for the type
     */
    ShortcutGroup(String value, String tag)
    {
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
    public static ShortcutGroup fromValue(String value)
    {
        ShortcutGroup[] types = values();
        for(ShortcutGroup type : types)
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
     * Returns a list of the shortcut groups.
     */
    public static List<ShortcutGroup> toList()
    {
        List<ShortcutGroup> ret = new ArrayList<ShortcutGroup>();

        ret.add(GENERAL);
        ret.add(CONTENT);

        return ret;
    }
}