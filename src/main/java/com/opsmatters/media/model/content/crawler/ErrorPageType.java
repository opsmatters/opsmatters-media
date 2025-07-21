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

package com.opsmatters.media.model.content.crawler;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the type of an error page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ErrorPageType
{
    UNKNOWN("Unknown"),
    CLOUDFLARE("Cloudflare"),
    ERROR_403("403 Error"),
    ERROR_404("404 Error"),
    ERROR_406("406 Error");

    private String value;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    ErrorPageType(String value)
    {
        this.value = value;
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
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns a list of the error page types.
     */
    public static List<ErrorPageType> toList()
    {
        List<ErrorPageType> ret = new ArrayList<ErrorPageType>();

        ret.add(UNKNOWN);
        ret.add(CLOUDFLARE);
        ret.add(ERROR_403);
        ret.add(ERROR_404);
        ret.add(ERROR_406);

        return ret;
    }
}