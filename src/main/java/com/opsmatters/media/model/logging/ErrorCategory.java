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
 * Represents the category of a log error.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ErrorCategory
{
    TEASER("teaser"),
    ARTICLE("article");

    private String value;

    /**
     * Constructor that takes the category value.
     * @param value The value for the category
     */
    ErrorCategory(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the category.
     * @return The value of the category.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the category.
     * @return The value of the category.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the category for the given value.
     * @param value The category value
     * @return The category for the given value
     */
    public static ErrorCategory fromValue(String value)
    {
        ErrorCategory[] categories = values();
        for(ErrorCategory category : categories)
        {
            if(category.value().equals(value))
                return category;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of categories.
     * @param value The category value
     * @return <CODE>true</CODE> if the given value is contained in the list of categories
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}