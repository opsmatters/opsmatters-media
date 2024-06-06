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

package com.opsmatters.media.model.logging;

/**
 * Represents the level of a log error.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ErrorLevel
{
    ERROR("Error", 2, "glyphicon-exclamation-sign", "level-error"),
    WARN("Warn", 1, "glyphicon-warning-sign", "level-warn"),
    INFO("Info", 0, "glyphicon-info-sign", "level-info"),
    DEBUG("Debug", -1, "glyphicon-info-sign", "level-debug");

    private String value;
    private int precedence;
    private String icon;
    private String css;

    /**
     * Constructor that takes the level value, precedence, icon and css.
     * @param value The value for the level
     * @param precedence The precedence for the level
     * @param icon The glyphicon for the level
     * @param css The css class for the level
     */
    ErrorLevel(String value, int precedence, String icon, String css)
    {
        this.value = value;
        this.precedence = precedence;
        this.icon = icon;
        this.css = css;
    }

    /**
     * Returns the value of the level.
     * @return The value of the level.
     */
    public String toString()
    {
        return value();
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
     * Returns the precedence of the level.
     * @return The precedence of the level.
     */
    public int precedence()
    {
        return precedence;
    }

    /**
     * Returns the glyphicon of the level.
     * @return The glyphicon of the level.
     */
    public String icon()
    {
        return icon;
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
    public static ErrorLevel fromValue(String value)
    {
        ErrorLevel[] levels = values();
        for(ErrorLevel level : levels)
        {
            if(level.value().equals(value))
                return level;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of levels.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of levels
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}