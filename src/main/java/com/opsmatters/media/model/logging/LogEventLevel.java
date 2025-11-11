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

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.drupal.Severity;

/**
 * Represents the level of a log event.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum LogEventLevel
{
    ERROR("Error", "error", 2, "glyphicon-exclamation-sign", "level-error"),
    WARN("Warn", "warning", 1, "glyphicon-exclamation-sign", "level-warn"),
    INFO("Info", "information", 0, "glyphicon-info-sign", "level-info"),
    DEBUG("Debug", "debug", -1, "glyphicon-info-sign", "level-debug");

    private String value;
    private String tag;
    private int precedence;
    private String icon;
    private String css;

    /**
     * Constructor that takes the level value, tag, precedence, icon and css.
     * @param value The value for the level
     * @param tag The tag for the level
     * @param precedence The precedence for the level
     * @param icon The glyphicon for the level
     * @param css The css class for the level
     */
    LogEventLevel(String value, String tag, int precedence, String icon, String css)
    {
        this.value = value;
        this.tag = tag;
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
     * Returns the tag of the level.
     * @return The tag of the level.
     */
    public String tag()
    {
        return tag;
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
     * Returns the level for the given severity.
     * @param severity The severity
     * @return The level for the given severity
     */
    public static LogEventLevel from(Severity severity)
    {
        LogEventLevel ret = INFO;
        if(severity == Severity.ERROR)
            ret = ERROR;
        else if(severity == Severity.WARNING)
            ret = WARN;

        return ret;
    }

    /**
     * Returns the level for the given value.
     * @param value The level value
     * @return The level for the given value
     */
    public static LogEventLevel fromValue(String value)
    {
        LogEventLevel[] levels = values();
        for(LogEventLevel level : levels)
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

    /**
     * Returns a list of the event levels.
     */
    public static List<LogEventLevel> toList()
    {
        List<LogEventLevel> ret = new ArrayList<LogEventLevel>();

        ret.add(ERROR);
        ret.add(WARN);
        ret.add(INFO);
        ret.add(DEBUG);

        return ret;
    }
}