/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.model;

/**
 * Represents a post message format.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum MessageFormat
{
    RAW("Raw"),
    ENCODED("Encoded"),
    DECODED("Decoded");

    private String value;

    /**
     * Constructor that takes the format value.
     * @param value The value for the format
     */
    MessageFormat(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the format.
     * @return The value of the format.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the format for the given value.
     * @param value The format value
     * @return The format for the given value
     */
    public static MessageFormat fromValue(String value)
    {
        MessageFormat[] formats = values();
        for(MessageFormat format : formats)
        {
            if(format.value().equals(value))
                return format;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of formats.
     * @param value The format value
     * @return <CODE>true</CODE> if the given value is contained in the list of formats
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}