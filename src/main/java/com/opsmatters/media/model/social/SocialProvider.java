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
    TWITTER("twitter",
        "Twitter",
        "https://twitter.com",
        "/%s",
        "/hashtag/%s",
        "images/twitter-thumb.png",
        280),
    FACEBOOK("facebook",
        "Facebook",
        "https://www.facebook.com",
        "/%s",
        "/hashtag/%s",
        "images/facebook-thumb.png",
        2000),
    LINKEDIN("linkedin",
        "LinkedIn",
        "https://www.linkedin.com",
        "/company/%s",
        "/feed/hashtag/?keywords=%%23%s",
        "images/linkedin-thumb.png",
        1300);

    private String code;
    private String value;
    private String url;
    private String handleUrl;
    private String hashtagUrl;
    private String thumbnail;
    private int maxLength;

    /**
     * Constructor that takes the channel information.
     * @param code The code for the provider
     * @param value The display name for the provider
     * @param url The url for the provider
     * @param handleUrl The handle url for the provider
     * @param hashtagUrl The hashtag url for the provider
     * @param thumbnail The thumbnail image for the provider
     * @param maxLength The maximum message length for the provider
     */
    SocialProvider(String code, String value, String url, String handleUrl, String hashtagUrl, String thumbnail, int maxLength)
    {
        this.code = code;
        this.value = value;
        this.url = url;
        this.handleUrl = url+handleUrl;
        this.hashtagUrl = url+hashtagUrl;
        this.thumbnail = thumbnail;
        this.maxLength = maxLength;
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
     * Returns the provider display name.
     * @return The provider display name.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the provider URL.
     * @return The provider URL.
     */
    public String url()
    {
        return url;
    }

    /**
     * Returns the provider handle URL.
     * @return The provider handle URL.
     */
    public String handleUrl()
    {
        return handleUrl;
    }

    /**
     * Returns the provider hashtag URL.
     * @return The provider hashtag URL.
     */
    public String hashtagUrl()
    {
        return hashtagUrl;
    }

    /**
     * Returns the thumbnail image name.
     * @return The thumbnail image name.
     */
    public String thumbnail()
    {
        return thumbnail;
    }

    /**
     * Returns the maximum message length.
     * @return The maximum message length.
     */
    public int maxLength()
    {
        return maxLength;
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