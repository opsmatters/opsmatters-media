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
package com.opsmatters.media.model.content;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a content item summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentSummary implements java.io.Serializable
{
    private String uuid = "";
    private String title = "";
    private Instant publishedDate;
    private String summary = "";
    private boolean valid = true;

    /**
     * Default constructor.
     */
    public ContentSummary()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentSummary(ContentSummary obj)
    {
        if(obj != null)
        {
            setUuid(obj.getUuid());
            setTitle(obj.getTitle());
            setPublishedDate(obj.getPublishedDate());
            setSummary(obj.getSummary());
        }
    }

    /**
     * Returns the title.
     */
    public String toString()
    {
        return getTitle();
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
        return uuid;
    }

    /**
     * Sets the content uuid.
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Returns <CODE>true</CODE> if the content item is valid.
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Set to <CODE>true</CODE> if the content item is valid.
     */
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    /**
     * Returns the content title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the content title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the date the content was published.
     */
    public Instant getPublishedDate()
    {
        return publishedDate;
    }

    /**
     * Returns the date the content was published.
     */
    public long getPublishedDateMillis()
    {
        return getPublishedDate() != null ? getPublishedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(publishedDate, pattern);
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString()
    {
        return getPublishedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDate(Instant publishedDate)
    {
        this.publishedDate = publishedDate;
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateMillis(long millis)
    {
        if(millis > 0L)
            this.publishedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setPublishedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateAsString(String str) throws DateTimeParseException
    {
        setPublishedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the content summary.
     */
    public String getSummary()
    {
        return summary;
    }

    /**
     * Sets the content summary.
     */
    public void setSummary(String summary)
    {
        this.summary = summary;
    }
}