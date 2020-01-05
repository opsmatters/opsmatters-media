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
 * Represents the case of a content field.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContentFieldCase
{
    NONE("none"),
    LOWER("lower"),
    UPPER("upper"),
    CAPITALIZE("capitalize"); 

    private String value;

    /**
     * Constructor that takes the case value.
     * @param value The value for the case
     */
    ContentFieldCase(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the case.
     * @return The value of the case.
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
    public static ContentFieldCase fromValue(String value)
    {
        ContentFieldCase[] types = values();
        for(ContentFieldCase type : types)
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