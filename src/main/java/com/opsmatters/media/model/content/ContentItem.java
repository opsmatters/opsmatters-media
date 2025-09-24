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
package com.opsmatters.media.model.content;

import java.util.List;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Class representing a content list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentItem<T extends Content> implements java.io.Serializable
{
    private T content;

    /**
     * Copies the attributes of the given object.
     */
    public abstract void copyAttributes(T obj);

    /**
     * Returns the content object.
     */
    public T get()
    {
        return content;
    }

    /**
     * Returns the content object.
     */
    protected void set(T content)
    {
        this.content = content;
    }

    /**
     * Returns the title.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the content type.
     */
    public ContentType getType()
    {
        return content.getType();
    }

    /**
     * Returns the content uuid.
     */
    public String getUniqueId()
    {
        return getUuid();
    }

    /**
     * Returns the content uuid.
     */
    public String getUuid()
    {
        return content.getUuid();
    }

    /**
     * Sets the content uuid.
     */
    public void setUuid(String uuid)
    {
        content.setUuid(uuid);
    }

    /**
     * Returns <CODE>true</CODE> if the content uuid has been set.
     */
    public boolean hasUuid()
    {
        return getUuid() != null && getUuid().length() > 0;
    }

    /**
     * Returns the site id.
     */
    public String getSiteId()
    {
        return content.getSiteId();
    }

    /**
     * Sets the site id.
     */
    public void setSiteId(String siteId)
    {
        content.setSiteId(siteId);
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Returns the content ID.
     */
    public int getId()
    {
        return content.getId();
    }

    /**
     * Sets the content ID.
     */
    public void setId(int id)
    {
        content.setId(id);
    }

    /**
     * Returns the content title.
     */
    public String getTitle()
    {
        return content.getTitle();
    }

    /**
     * Sets the content title.
     */
    public void setTitle(String title)
    {
        content.setTitle(title);
    }

    /**
     * Returns the date the content was published.
     */
    public Instant getPublishedDate()
    {
        return content.getPublishedDate();
    }

    /**
     * Returns the date the content was published.
     */
    public long getPublishedDateMillis()
    {
        return content.getPublishedDateMillis();
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDate(Instant publishedDate)
    {
        content.setPublishedDate(publishedDate);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateMillis(long millis)
    {
        content.setPublishedDateMillis(millis);
    }

    /**
     * Returns <CODE>true</CODE> if this content should be published.
     */
    public boolean isPublished()
    {
        return content.isPublished();
    }

    /**
     * Set to <CODE>true</CODE> if this content should be published.
     */
    public void setPublished(boolean published)
    {
        content.setPublished(published);
    }

    /**
     * Returns the content status.
     */
    public ContentStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the content status.
     */
    public void setStatus(String status)
    {
        setStatus(ContentStatus.valueOf(status));
    }

    /**
     * Sets the content status.
     */
    public void setStatus(ContentStatus status)
    {
        content.setStatus(status);
    }

    /**
     * Returns the other site ids.
     */
    public String getOtherSites()
    {
        return content.getOtherSites();
    }

    /**
     * Sets the other site ids.
     */
    public void setOtherSites(String otherSites)
    {
        content.setOtherSites(otherSites);
    }

    /**
     * Sets the other site ids.
     */
    public void setOtherSites(List<? extends Content> items)
    {
        content.setOtherSites(items);
    }

    /**
     * Returns <CODE>true</CODE> if the other site ids have been set.
     */
    public boolean hasOtherSites()
    {
        return content.hasOtherSites();
    }
}