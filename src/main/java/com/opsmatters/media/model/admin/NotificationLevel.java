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

package com.opsmatters.media.model.admin;

/**
 * Represents a notification level.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum NotificationLevel
{
    ERROR("Error", "notification-error"),
    WARN("Warning", "notification-warn"),
    SUCCESS("Success", "notification-success"),
    INFO("Information", "notification-info"),
    NONE("None", "");

    private String value;
    private String css;

    /**
     * Constructor that takes the level value and prefix.
     * @param value The value for the level
     * @param css The css class for the level
     */
    NotificationLevel(String value, String css)
    {
        this.value = value;
        this.css = css;
    }

    /**
     * Returns the value of the level.
     * @return The value of the level.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the css class of the level.
     * @return The css class of the level.
     */
    public String css()
    {
        return css;
    }

    /**
     * Returns the level for the given value.
     * @param value The level value
     * @return The level for the given value
     */
    public static NotificationLevel fromValue(String value)
    {
        NotificationLevel[] types = values();
        for(NotificationLevel type : types)
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