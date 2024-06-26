/*
 * Copyright 2024 Gerald Curley
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
 * Represents a property of a log error.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum LogErrorProperty
{
    MESSAGE("message"),
    LOCATION("location"),
    EXCEPTION("exception"),
    STACKTRACE("stacktrace");

    private String value;

    /**
     * Constructor that takes the property value.
     * @param value The value for the property
     */
    LogErrorProperty(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the property.
     * @return The value of the property.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the property.
     * @return The value of the property.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the property for the given value.
     * @param value The property value
     * @return The property for the given value
     */
    public static LogErrorProperty fromValue(String value)
    {
        LogErrorProperty[] properties = values();
        for(LogErrorProperty property : properties)
        {
            if(property.value().equals(value))
                return property;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of properties.
     * @param value The property value
     * @return <CODE>true</CODE> if the given value is contained in the list of properties
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}