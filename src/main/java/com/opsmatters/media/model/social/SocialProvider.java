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

package com.opsmatters.media.model.social;

/**
 * Represents a social media provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum SocialProvider
{
    TWITTER("twitter", "Twitter"),
    FACEBOOK("facebook", "Facebook"),
    LINKEDIN("linkedin", "LinkedIn");

    private String code;
    private String displayName;

    /**
     * Constructor that takes the channel information.
     * @param code The code for the provider
     * @param displayName The display name for the provider
     */
    SocialProvider(String code, String displayName)
    {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * Returns the code of the provider.
     * @return The code of the provider.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the channel URL template.
     * @return The channel URL template.
     */
    public String displayName()
    {
        return displayName;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static SocialProvider fromCode(String code)
    {
        SocialProvider[] types = values();
        for(SocialProvider type : types)
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
        return fromCode(code) != null;
    }
}