/*
 * Copyright 2022 Gerald Curley
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

package com.opsmatters.media.model.content.util;

/**
 * Represents the type of an image.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ImageType
{
    THUMBNAIL("Thumbnail", "/logos", false),
    LOGO("Logo", "/logos", false),
    BANNER("Banner", "/images", false),
    POST("Post", "/images", true);

    private String value;
    private String path;
    private boolean multiple;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     * @param path The path for the type
     * @param multiple <CODE>true</CODE> if there can be multiple images per organisation
     */
    ImageType(String value, String path, boolean multiple)
    {
        this.value = value;
        this.path = path;
        this.multiple = multiple;
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
     * Returns the path of the type.
     * @return The path of the type.
     */
    public String path()
    {
        return path;
    }

    /**
     * Returns <CODE>true</CODE> if there can be multiple images per organisation.
     * @return <CODE>true</CODE> if there can be multiple images per organisation
     */
    public boolean multiple()
    {
        return multiple;
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