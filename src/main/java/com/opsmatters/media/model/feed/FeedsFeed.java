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
import com.opsmatters.media.model.BaseItem;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a drupal feed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedsFeed extends BaseItem
{
    private String title = "";
    private String type = "";
    private int fid = -1;
    private Instant importedDate;
    private long itemCount = -1L;

    /**
     * Default constructor.
     */
    public FeedsFeed()
    {
    }

    /**
     * Copy constructor.
     */
    public FeedsFeed(FeedsFeed obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FeedsFeed obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTitle(obj.getTitle());
            setType(obj.getType());
            setFid(obj.getFid());
            setImportedDate(obj.getImportedDate());
            setItemCount(obj.getItemCount());
        }
    }

    /**
     * Returns the feed title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the feed title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the feed type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the feed type.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the feed id.
     */
    public int getFid()
    {
        return fid;
    }

    /**
     * Sets the feed id.
     */
    public void setFid(int fid)
    {
        this.fid = fid;
    }

    /**
     * Returns the date the feed was last imported.
     */
    public Instant getImportedDate()
    {
        return importedDate;
    }

    /**
     * Returns the date the feed was last imported.
     */
    public long getImportedDateMillis()
    {
        return getImportedDate() != null ? getImportedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the feed was last imported.
     */
    public LocalDateTime getImportedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getImportedDate());
    }

    /**
     * Returns the date the feed was last imported.
     */
    public String getImportedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(importedDate, pattern);
    }

    /**
     * Returns the date the feed was last imported.
     */
    public String getImportedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(importedDate, pattern, timezone);
    }

    /**
     * Returns the date the feed was last imported.
     */
    public String getImportedDateAsString()
    {
        return getImportedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the feed was last imported.
     */
    public void setImportedDate(Instant importedDate)
    {
        this.importedDate = importedDate;
    }

    /**
     * Sets the date the feed was last imported.
     */
    public void setImportedDateMillis(long millis)
    {
        if(millis > 0L)
            this.importedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the feed item was last imported.
     */
    public void setImportedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setImportedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the feed was last imported.
     */
    public void setImportedDateAsString(String str) throws DateTimeParseException
    {
        setImportedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the feed was last imported.
     */
    public void setImportedDateUTC(LocalDateTime importedDate)
    {
        if(importedDate != null)
            setImportedDate(TimeUtils.toInstantUTC(importedDate));
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