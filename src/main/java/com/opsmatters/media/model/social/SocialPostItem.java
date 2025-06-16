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

import com.opsmatters.media.model.BaseEntityItem;

/**
 * Class representing a social media post list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class SocialPostItem<T extends SocialPost> extends BaseEntityItem<T>
{
    private T content;

    /**
     * Returns the content object.
     */
    public T get()
    {
        return content;
    }

    /**
     * Sets the content object.
     */
    protected void set(T content)
    {
        super.set(content);
        this.content = content;
    }

    /**
     * Returns the post type.
     */
    public SocialPostType getType()
    {
        return content.getType();
    }

    /**
     * Returns the post site.
     */
    public String getSiteId()
    {
        return content.getSiteId();
    }

    /**
     * Sets the post site.
     */
    public void setSiteId(String siteId)
    {
        content.setSiteId(siteId);
    }

    /**
     * Returns <CODE>true</CODE> if the site has been set.
     */
    public boolean hasSiteId()
    {
        return content.hasSiteId();
    }
}