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

package com.opsmatters.media.model.provider;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the id of a video provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum VideoProviderId
{
    YOUTUBE("YTB"),
    VIMEO("VIM"),
    WISTIA("WST");

    private String code;

    /**
     * Constructor that takes the type code.
     * @param code The code for the type
     */
    VideoProviderId(String code)
    {
        this.code = code;
    }

    /**
     * Returns the code of the type.
     * @return The code of the type.
     */
    public String toString()
    {
        return code();
    }

    /**
     * Returns the code of the type.
     * @return The code of the type.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static VideoProviderId fromCode(String code)
    {
        VideoProviderId[] types = values();
        for(VideoProviderId type : types)
        {
            if(type.code().equals(code))
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
     * Returns a list of the providers.
     */
    public static List<VideoProviderId> toList()
    {
        List<VideoProviderId> ret = new ArrayList<VideoProviderId>();

        ret.add(YOUTUBE);
        ret.add(VIMEO);
        ret.add(WISTIA);

        return ret;
    }
}