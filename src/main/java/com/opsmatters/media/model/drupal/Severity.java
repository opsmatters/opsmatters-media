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

package com.opsmatters.media.model.drupal;

/**
 * Represents the severity of a log message.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum Severity
{
    NONE(-1, "None"),
    EMERGENCY(0, "Emergency"),
    ALERT(1, "Alert"),
    CRITICAL(2, "Critical"),
    ERROR(3, "Error"),
    WARNING(4, "Warning"),
    NOTICE(5, "Notice"),
    INFO(6, "Info"),
    DEBUG(7, "Debug");

    private int level;
    private String value;

    /**
     * Constructor that takes the severity level and value.
     * @param level The level for the severity
     * @param value The value for the severity
     */
    Severity(int level, String value)
    {
        this.level = level;
        this.value = value;
    }

    /**
     * Returns the value of the severity.
     * @return The value of the severity.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the level of the severity.
     * @return The level of the severity.
     */
    public int level()
    {
        return level;
    }

    /**
     * Returns the value of the severity.
     * @return The value of the severity.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the severity for the given value.
     * @param value The severity value
     * @return The severity for the given value
     */
    public static Severity fromValue(String value)
    {
        Severity[] severities = values();
        for(Severity severity : severities)
        {
            if(severity.value().equals(value))
                return severity;
        }

        return null;
    }

    /**
     * Returns the severity for the given level.
     * @param level The severity level
     * @return The severity for the given level
     */
    public static Severity fromLevel(short level)
    {
        Severity[] severities = values();
        for(Severity severity : severities)
        {
            if(severity.level() == level)
                return severity;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of severities.
     * @param value The severity value
     * @return <CODE>true</CODE> if the given value is contained in the list of severities
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}