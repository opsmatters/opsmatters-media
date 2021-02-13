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
package com.opsmatters.media.model.feed;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a drupal feeds item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedsItem implements java.io.Serializable
{
    private int feedId = -1;
    private String guid = "";
    private int entityId = -1;
    private Instant importedDate;
    private String bundle = "";
    private String path = "";
    private String alias = "";

    /**
     * Default constructor.
     */
    public FeedsItem()
    {
    }

    /**
     * Copy constructor.
     */
    public FeedsItem(FeedsItem obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FeedsItem obj)
    {
        if(obj != null)
        {
            setFeedId(obj.getFeedId());
            setGuid(obj.getGuid());
            setEntityId(obj.getEntityId());
            setImportedDate(obj.getImportedDate());
            setBundle(obj.getBundle());
            setPath(obj.getPath());
            setAlias(obj.getAlias());
        }
    }

    /**
     * Returns the id.
     */
    public String toString()
    {
        return getGuid();
    }

    /**
     * Returns the feed id.
     */
    public int getFeedId()
    {
        return feedId;
    }

    /**
     * Sets the feed id.
     */
    public void setFeedId(int feedId)
    {
        this.feedId = feedId;
    }

    /**
     * Returns the feeds item GUID.
     */
    public String getGuid()
    {
        return guid;
    }

    /**
     * Sets the feeds item GUID.
     */
    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    /**
     * Returns the entity id.
     */
    public int getEntityId()
    {
        return entityId;
    }

    /**
     * Sets the entity id.
     */
    public void setEntityId(int entityId)
    {
        this.entityId = entityId;
    }

    /**
     * Returns the date the feeds item was imported.
     */
    public Instant getImportedDate()
    {
        return importedDate;
    }

    /**
     * Returns the date the feeds item was imported.
     */
    public long getImportedDateMillis()
    {
        return getImportedDate() != null ? getImportedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the feeds item was imported.
     */
    public String getImportedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(importedDate, pattern);
    }

    /**
     * Returns the date the feeds item was imported.
     */
    public String getImportedDateAsString()
    {
        return getImportedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the feeds item was imported.
     */
    public void setImportedDate(Instant importedDate)
    {
        this.importedDate = importedDate;
    }

    /**
     * Sets the date the feeds item was imported.
     */
    public void setImportedDateMillis(long millis)
    {
        if(millis > 0L)
            this.importedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the feeds item was imported.
     */
    public void setImportedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setImportedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the feeds item was imported.
     */
    public void setImportedDateAsString(String str) throws DateTimeParseException
    {
        setImportedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the feeds item bundle.
     */
    public String getBundle()
    {
        return bundle;
    }

    /**
     * Sets the feeds item bundle.
     */
    public void setBundle(String bundle)
    {
        this.bundle = bundle;
    }

    /**
     * Returns the feeds item path.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the feeds item path.
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Returns the feeds item alias.
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * Sets the feeds item alias.
     */
    public void setAlias(String alias)
    {
        this.alias = alias;
    }
}