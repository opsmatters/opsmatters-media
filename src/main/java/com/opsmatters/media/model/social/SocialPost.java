/*
 * Copyright 2019 Gerald Curley
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

import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.model.OwnedEntity;

/**
 * Class representing a social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class SocialPost extends OwnedEntity
{
    public static final String HANDLE = "handle";
    public static final String HASHTAG = "hashtag";
    public static final String HASHTAGS = "hashtags";
    public static final String TITLE = "title";
    public static final String TITLE1 = "title1";
    public static final String TITLE2 = "title2";
    public static final String URL = "url";
    public static final String ORIGINAL_URL = "original-url";
    public static final String ORGANISATION = "organisation";
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_ID = "content-id";

    private String siteId = "";
    private String message = "";

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSiteId(obj.getSiteId());
            setMessage(obj.getMessage());
        }
    }

    /**
     * Returns the post type.
     */
    public abstract PostType getType();

    /**
     * Returns the site id.
     */
    public String getSiteId()
    {
        return siteId;
    }

    /**
     * Sets the site id.
     */
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }

    /**
     * Returns the post message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Returns the post message, with encoded emojis if required.
     */
    public String getMessage(MessageFormat format)
    {
        if(format == MessageFormat.ENCODED)
            return EmojiParser.parseToAliases(getMessage());
        else if(format == MessageFormat.DECODED)
            return EmojiParser.parseToUnicode(getMessage());
        else
            return getMessage();
    }

    /**
     * Sets the post message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns <CODE>true</CODE> if the post message has been set.
     */
    public boolean hasMessage()
    {
        return message != null && message.length() > 0;
    }
}