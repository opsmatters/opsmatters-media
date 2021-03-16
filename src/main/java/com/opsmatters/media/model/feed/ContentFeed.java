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

import com.opsmatters.media.model.site.Site;
import com.opsmatters.media.model.site.EnvironmentName;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content feed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFeed extends Feed
{
    private String siteId = "";
    private EnvironmentName environment;
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
    public ContentFeed(FeedsFeed feed, Site site, EnvironmentName environment, ContentType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(feed.getCreatedDate());
        setExecutedDate(feed.getImportedDate());
        setName(feed.getTitle());
        setExternalId(feed.getId());
        setSiteId(site.getId());
        setEnvironment(environment);
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
            setSiteId(obj.getSiteId());
            setEnvironment(obj.getEnvironment());
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
     * Returns the feed environment.
     */
    public EnvironmentName getEnvironment()
    {
        return environment;
    }

    /**
     * Sets the feed environment.
     */
    public void setEnvironment(String environment)
    {
        setEnvironment(EnvironmentName.valueOf(environment));
    }

    /**
     * Sets the feed environment.
     */
    public void setEnvironment(EnvironmentName environment)
    {
        this.environment = environment;
    }
}