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
package com.opsmatters.media.model.social;

import com.opsmatters.media.model.DeliveryStatus;

/**
 * Class representing a social media post list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChannelPostItem extends SocialPostItem<ChannelPost>
{
    private ChannelPost content = new ChannelPost();

    /**
     * Default constructor.
     */
    public ChannelPostItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ChannelPostItem(ChannelPostItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a post.
     */
    public ChannelPostItem(ChannelPost obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChannelPostItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(ChannelPost obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public ChannelPost get()
    {
        return content;
    }

    /**
     * Returns the post organisation.
     */
    public String getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the post organisation.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Returns <CODE>true</CODE> if the post organisation has been set.
     */
    public boolean hasCode()
    {
        return content.hasCode();
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return content.getOrganisation();
    }

    /**
     * Sets the organisation name.
     */
    public void setOrganisation(String organisation)
    {
        content.setOrganisation(organisation);
    }

    /**
     * Returns the post type.
     */
    public SocialPostType getType()
    {
        return content.getType();
    }

    /**
     * Sets the post type.
     */
    public void setType(String type)
    {
        content.setType(type);
    }

    /**
     * Sets the post type.
     */
    public void setType(SocialPostType type)
    {
        content.setType(type);
    }

    /**
     * Returns the social channel code.
     */
    public String getChannel()
    {
        return content.getChannel();
    }

    /**
     * Sets the social channel code.
     */
    public void setChannel(String channel)
    {
        content.setChannel(channel);
    }

    /**
     * Returns the post title.
     */
    public String getTitle()
    {
        return content.getTitle();
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        content.setTitle(title);
    }

    /**
     * Returns the post status.
     */
    public DeliveryStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the post status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the post status.
     */
    public void setStatus(DeliveryStatus status)
    {
        content.setStatus(status);
    }
}