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
package com.opsmatters.media.model.feed;

import com.opsmatters.media.model.SiteEnv;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content feed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFeed extends Feed
{
    private SiteEnv env;
    private ContentType contentType;

    /**
     * Default constructor.
     */
    public ContentFeed()
    {
    }

    /**
     * Constructor that takes a drupal feed and environment.
     */
    public ContentFeed(FeedsFeed feed, SiteEnv env, ContentType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(feed.getCreatedDate());
        setExecutedDate(feed.getImportedDate());
        setName(feed.getTitle());
        setExternalId(feed.getId());
        setEnv(env);
        setContentType(type);
        setStatus(FeedStatus.NEW);
        setItemCount(feed.getItemCount());
    }

    /**
     * Copy constructor.
     */
    public ContentFeed(ContentFeed obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentFeed obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setEnv(obj.getEnv());
            setContentType(obj.getContentType());
        }
    }

    /**
     * Returns the feed content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the feed content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(ContentType.valueOf(contentType));
    }

    /**
     * Sets the feed content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns the feed env.
     */
    public SiteEnv getEnv()
    {
        return env;
    }

    /**
     * Sets the feed env.
     */
    public void setEnv(String env)
    {
        setEnv(SiteEnv.valueOf(env));
    }

    /**
     * Sets the feed env.
     */
    public void setEnv(SiteEnv env)
    {
        this.env = env;
    }
}