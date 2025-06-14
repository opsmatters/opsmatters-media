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
import com.opsmatters.media.model.ManagedEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a feed import.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedImport extends ManagedEntity
{
    private String feedId = "";
    private long executionTime = -1L;
    private int createdCount = -1;
    private int updatedCount = -1;
    private int failedCount = -1;
    private String errorMessage = "";

    /**
     * Default constructor.
     */
    public FeedImport()
    {
    }

    /**
     * Constructor that takes a feed.
     */
    public FeedImport(Feed feed)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setFeedId(feed.getId());
    }

    /**
     * Copy constructor.
     */
    public FeedImport(FeedImport obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FeedImport obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setFeedId(obj.getFeedId());
            setExecutionTime(obj.getExecutionTime());
            setCreatedCount(obj.getCreatedCount());
            setUpdatedCount(obj.getUpdatedCount());
            setFailedCount(obj.getFailedCount());
            setErrorMessage(obj.getErrorMessage());
        }
    }

    /**
     * Returns the feed id.
     */
    public String getFeedId()
    {
        return feedId;
    }

    /**
     * Sets the feed id.
     */
    public void setFeedId(String feedId)
    {
        this.feedId = feedId;
    }

    /**
     * Returns the time taken for the feed to execute.
     */
    public long getExecutionTime()
    {
        return executionTime;
    }

    /**
     * Sets the time taken for the feed to execute.
     */
    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    /**
     * Returns the feed created item count.
     */
    public int getCreatedCount()
    {
        return createdCount;
    }

    /**
     * Sets the feed created item count.
     */
    public void setCreatedCount(int createdCount)
    {
        this.createdCount = createdCount;
    }

    /**
     * Returns the feed updated item count.
     */
    public int getUpdatedCount()
    {
        return updatedCount;
    }

    /**
     * Sets the feed updated item count.
     */
    public void setUpdatedCount(int updatedCount)
    {
        this.updatedCount = updatedCount;
    }

    /**
     * Returns the feed failed item count.
     */
    public int getFailedCount()
    {
        return failedCount;
    }

    /**
     * Sets the feed failed item count.
     */
    public void setFailedCount(int failedCount)
    {
        this.failedCount = failedCount;
    }

    /**
     * Returns the monitor error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the monitor error message.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor error message has been set.
     */
    public boolean hasErrorMessage()
    {
        return errorMessage != null && errorMessage.length() > 0;
    }
}