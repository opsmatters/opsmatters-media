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
    public SocialPostType getType()
    {
        return null;
    }

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
     * Returns <CODE>true</CODE> if the site has been set.
     */
    public boolean hasSiteId()
    {
        return siteId != null && siteId.length() > 0;
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

    /**
     * Returns <CODE>true</CODE> if the post message contains a reference to the given property.
     */
    public boolean hasReference(SocialPostProperty property)
    {
        return hasMessage() && getMessage().indexOf(String.format("${%s}", property.value())) != -1;
    }
}