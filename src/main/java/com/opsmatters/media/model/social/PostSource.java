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
package com.opsmatters.media.model.social;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a social media post source.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class PostSource extends SocialPost
{
    private String siteId = "";
    private String name = "";
    private boolean shortenUrl = false;
    private Instant postedDate;
    private SourceStatus status;

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PostSource obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setSiteId(obj.getSiteId());
            setShortenUrl(obj.isShortenUrl());
            setPostedDate(obj.getPostedDate());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the source name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the post name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the post name.
     */
    public void setName(String name)
    {
        this.name = name;
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
     * Returns <CODE>true</CODE> if the post URL should be automatically shortened.
     */
    public boolean isShortenUrl()
    {
        return shortenUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the post URL should be automatically shortened.
     */
    public Boolean getShortenUrlObject()
    {
        return Boolean.valueOf(isShortenUrl());
    }

    /**
     * Set to <CODE>true</CODE> if the post URL should be automatically shortened.
     */
    public void setShortenUrl(boolean shortenUrl)
    {
        this.shortenUrl = shortenUrl;
    }

    /**
     * Set to <CODE>true</CODE> if the post URL should be automatically shortened.
     */
    public void setShortenUrlObject(Boolean shortenUrl)
    {
        setShortenUrl(shortenUrl != null && shortenUrl.booleanValue());
    }

    /**
     * Returns the date a post was last created from the post.
     */
    public Instant getPostedDate()
    {
        return postedDate;
    }

    /**
     * Returns the date a post was last created from the post.
     */
    public long getPostedDateMillis()
    {
        return getPostedDate() != null ? getPostedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date a post was last created from the post.
     */
    public LocalDateTime getPostedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getPostedDate());
    }

    /**
     * Returns the date a post was last created from the post.
     */
    public String getPostedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(postedDate, pattern);
    }

    /**
     * Returns the date a post was last created from the post.
     */
    public String getPostedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(postedDate, pattern, timezone);
    }

    /**
     * Returns the date a post was last created from the post.
     */
    public String getPostedDateAsString()
    {
        return getPostedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date a post was last created from the post.
     */
    public void setPostedDate(Instant postedDate)
    {
        this.postedDate = postedDate;
    }

    /**
     * Sets the date a post was last created from the post.
     */
    public void setPostedDateMillis(long millis)
    {
        if(millis > 0L)
            this.postedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date a post was last created from the post.
     */
    public void setPostedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setPostedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date a post was last created from the post.
     */
    public void setPostedDateAsString(String str) throws DateTimeParseException
    {
        setPostedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date a post was last created from the post.
     */
    public void setPostedDateUTC(LocalDateTime postedDate)
    {
        if(postedDate != null)
            setPostedDate(TimeUtils.toInstantUTC(postedDate));
    }

    /**
     * Returns the source status.
     */
    public SourceStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the source status.
     */
    public void setStatus(String status)
    {
        setStatus(SourceStatus.valueOf(status));
    }

    /**
     * Sets the source status.
     */
    public void setStatus(SourceStatus status)
    {
        this.status = status;
    }
}