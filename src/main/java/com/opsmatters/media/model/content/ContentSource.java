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
 * Represents a content source.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContentSource
{
    CHANNEL("channel", "Channel"),
    PAGE("page", "Page"),
    STORE("store", "Store"); 

    private String code;
    private String value;

    /**
     * Constructor that takes the code and name.
     * @param code The code for the source
     * @param value The name of the source
     */
    ContentSource(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the source.
     * @return The value of the source.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the source.
     * @return The code of the source.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the source.
     * @return The value of the source.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns <CODE>true</code> if this is the STORE source.
     * @return <CODE>true</code> if this is the STORE source.
     */
    public boolean isStore()
    {
        return this == STORE;
    }

    /**
     * Returns <CODE>true</code> if this is the CHANNEL source.
     * @return <CODE>true</code> if this is the CHANNEL source.
     */
    public boolean isChannel()
    {
        return this == CHANNEL;
    }

    /**
     * Returns <CODE>true</code> if this is the PAGE source.
     * @return <CODE>true</code> if this is the PAGE source.
     */
    public boolean isPage()
    {
        return this == PAGE;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static ContentSource fromCode(String code)
    {
        ContentSource[] types = values();
        for(ContentSource type : types)
        {
            if(type.code().equals(code))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given code is contained in the list of types.
     * @param code The type code
     * @return <CODE>true</CODE> if the given code is contained in the list of types
     */
    public static boolean contains(String code)
    {
        return valueOf(code) != null;
    }
}