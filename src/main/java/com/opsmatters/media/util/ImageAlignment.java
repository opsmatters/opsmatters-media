/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the image alignments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ImageAlignment
{
    LEFT("Left"),
    CENTER("Center"),
    RIGHT("Right");

    private String value;

    /**
     * Constructor that takes the value.
     * @param value The value for the environment
     */
    ImageAlignment(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the alignment.
     * @return The value of the alignment.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the alignment.
     * @return The value of the alignment.
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
    public static ImageAlignment fromValue(String value)
    {
        ImageAlignment[] types = values();
        for(ImageAlignment type : types)
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

    /**
     * Returns a list of the alignments.
     */
    public static List<ImageAlignment> toList()
    {
        List<ImageAlignment> ret = new ArrayList<ImageAlignment>();

        ret.add(CENTER);
        ret.add(LEFT);
        ret.add(RIGHT);

        return ret;
    }
}