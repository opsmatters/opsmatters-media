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

package com.opsmatters.media.model.content.crawler.field;

/**
 * Represents the URL protocol of a content field.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum FieldProtocol
{
    ALL("all"),
    SECURE("secure"),
    INSECURE("insecure"); 

    private String value;

    /**
     * Constructor that takes the protocol value.
     * @param value The value for the protocol
     */
    FieldProtocol(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the protocol.
     * @return The value of the protocol.
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
    public static FieldProtocol fromValue(String value)
    {
        FieldProtocol[] types = values();
        for(FieldProtocol type : types)
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