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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a social media item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialItem implements java.io.Serializable
{
    private String id = "";
    private Instant createdDate;
    private Instant updatedDate;
    private String createdBy = "";

    /**
     * Default constructor.
     */
    public SocialItem()
    {
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialItem obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setCreatedDate(obj.getCreatedDate());
            setUpdatedDate(obj.getUpdatedDate());
            setCreatedBy(obj.getCreatedBy());
        }
    }

    /**
     * Returns the id.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the item id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the item id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns <CODE>true</CODE> if the item id has been set.
     */
    public boolean hasId()
    {
        return id != null && id.length() > 0;
    }

    /**
     * Returns the date the item was created.
     */
    public Instant getCreatedDate()
    {
        return createdDate;
    }

    /**
     * Returns the date the item was created.
     */
    public long getCreatedDateMillis()
    {
        return getCreatedDate() != null ? getCreatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the item was created.
     */
    public LocalDateTime getCreatedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getCreatedDate());
    }

    /**
     * Returns the date the item was created.
     */
    public String getCreatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(createdDate, pattern);
    }

    /**
     * Returns the date the item was created.
     */
    public String getCreatedDateAsString()
    {
        return getCreatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was created.
     */
    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    /**
     * Sets the date the item was created.
     */
    public void setCreatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.createdDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the item was created.
     */
    public void setCreatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setCreatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the item was created.
     */
    public void setCreatedDateAsString(String str) throws DateTimeParseException
    {
        setCreatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was created.
     */
    public void setCreatedDateUTC(LocalDateTime createdDate)
    {
        if(createdDate != null)
            setCreatedDate(TimeUtils.toInstantUTC(createdDate));
    }

    /**
     * Returns the date the item status was last updated.
     */
    public Instant getUpdatedDate()
    {
        return updatedDate;
    }

    /**
     * Returns the date the item was last updated.
     */
    public long getUpdatedDateMillis()
    {
        return getUpdatedDate() != null ? getUpdatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the item was last updated.
     */
    public LocalDateTime getUpdatedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getUpdatedDate());
    }

    /**
     * Returns the date the item was last updated.
     */
    public String getUpdatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(updatedDate, pattern);
    }

    /**
     * Returns the date the item was last updated.
     */
    public String getUpdatedDateAsString()
    {
        return getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was last updated.
     */
    public void setUpdatedDate(Instant updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    /**
     * Sets the date the item was last updated.
     */
    public void setUpdatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.updatedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the item was last updated.
     */
    public void setUpdatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setUpdatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the item was last updated.
     */
    public void setUpdatedDateAsString(String str) throws DateTimeParseException
    {
        setUpdatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was last updated.
     */
    public void setUpdatedDateUTC(LocalDateTime updatedDate)
    {
        if(updatedDate != null)
            setUpdatedDate(TimeUtils.toInstantUTC(updatedDate));
    }

    /**
     * Returns the date the item status was created or last updated.
     */
    public Instant getDate()
    {
        return updatedDate != null ? updatedDate : createdDate;
    }

    /**
     * Returns the date the item was created or last updated.
     */
    public long getDateMillis()
    {
        return getDate() != null ? getDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the item was created or last updated.
     */
    public LocalDateTime getDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getDate());
    }

    /**
     * Returns the date the item was created or last updated.
     */
    public String getDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getDate(), pattern);
    }

    /**
     * Returns the date the item was created or last updated.
     */
    public String getDateAsString()
    {
        return getDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the item creator.
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the item creator.
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
}