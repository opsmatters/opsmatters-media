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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a feed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Feed extends BaseEntity
{
    private String name = "";
    private String externalId = "";
    private Instant executedDate;
    private FeedStatus status;
    private long itemCount = -1L;

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentFeed obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setExternalId(obj.getExternalId());
            setStatus(obj.getStatus());
            setExecutedDate(obj.getExecutedDate());
            setItemCount(obj.getItemCount());
        }
    }

    /**
     * Returns the feed name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the feed name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the feed external id.
     */
    public String getExternalId()
    {
        return externalId;
    }

    /**
     * Sets the feed external id.
     */
    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    /**
     * Returns the feed status.
     */
    public FeedStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the feed status.
     */
    public void setStatus(String status)
    {
        setStatus(FeedStatus.valueOf(status));
    }

    /**
     * Sets the feed status.
     */
    public void setStatus(FeedStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if the feed is not currently processing.
     */
    public boolean isIdle()
    {
        return getStatus().idle();
    }

    /**
     * Returns <CODE>true</CODE> if the feed is currently processing.
     */
    public boolean isProcessing()
    {
        return getStatus().processing();
    }

    /**
     * Returns <CODE>true</CODE> if the feed has an error.
     */
    public boolean hasError()
    {
        return getStatus() == FeedStatus.ERROR;
    }

    /**
     * Returns <CODE>true</CODE> if the feed is ready for processing.
     */
    public boolean canProcess()
    {
        return getStatus() == FeedStatus.WAITING
            || getStatus() == FeedStatus.ERROR;
    }

    /**
     * Set the feed status to WAITING.
     */
    public void setWaiting()
    {
        setStatus(FeedStatus.WAITING);
        setUpdatedDate(Instant.now());
    }

    /**
     * Set the feed status to PENDING.
     */
    public void setPending()
    {
        setStatus(FeedStatus.PENDING);
        setUpdatedDate(Instant.now());
    }

    /**
     * Returns the date the feed was last executed.
     */
    public Instant getExecutedDate()
    {
        return executedDate;
    }

    /**
     * Returns the date the feed was last executed.
     */
    public long getExecutedDateMillis()
    {
        return getExecutedDate() != null ? getExecutedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the feed was last executed.
     */
    public LocalDateTime getExecutedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getExecutedDate());
    }

    /**
     * Returns the date the feed was last executed.
     */
    public String getExecutedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(executedDate, pattern);
    }

    /**
     * Returns the date the feed was last executed.
     */
    public String getExecutedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(executedDate, pattern, timezone);
    }

    /**
     * Returns the date the feed was last executed.
     */
    public String getExecutedDateAsString()
    {
        return getExecutedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the feed was last executed.
     */
    public void setExecutedDate(Instant executedDate)
    {
        this.executedDate = executedDate;
    }

    /**
     * Sets the date the feed was last executed.
     */
    public void setExecutedDateMillis(long millis)
    {
        if(millis > 0L)
            this.executedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the feed item was last executed.
     */
    public void setExecutedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setExecutedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the feed was last executed.
     */
    public void setExecutedDateAsString(String str) throws DateTimeParseException
    {
        setExecutedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the feed was last executed.
     */
    public void setExecutedDateUTC(LocalDateTime executedDate)
    {
        if(executedDate != null)
            setExecutedDate(TimeUtils.toInstantUTC(executedDate));
    }

    /**
     * Returns the feed item count.
     */
    public long getItemCount()
    {
        return itemCount;
    }

    /**
     * Sets the feed item count.
     */
    public void setItemCount(long itemCount)
    {
        this.itemCount = itemCount;
    }
}