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
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.social.SocialPostProperty.*;

/**
 * Class representing a saved social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class SavedPost extends PropertyPost
{
    public static final String DEFAULT = "New Post";

    private String title = "";
    private boolean shortenUrl = false;
    private Instant postedDate;
    private SavedPostStatus status;

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SavedPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTitle(obj.getTitle());
            setShortenUrl(obj.isShortenUrl());
            setPostedDate(obj.getPostedDate());
            setStatus(obj.getStatus());
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
     * Returns the post title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the post content type.
     */
    public ContentType getContentType()
    {
        return null;
    }

    /**
     * Returns the post hashtags.
     */
    public String getHashtags()
    {
        return getProperties().get(HASHTAGS);
    }

    /**
     * Sets the post hashtags.
     */
    public void setHashtags(String hashtags)
    {
        getProperties().put(HASHTAGS, hashtags);
    }

    /**
     * Returns <CODE>true</CODE> if the post hashtags have been set.
     */
    public boolean hasHashtags()
    {
        return getHashtags() != null && getHashtags().length() > 0;
    }

    /**
     * Returns the post URL.
     */
    public String getUrl()
    {
        return getProperties().get(URL);
    }

    /**
     * Sets the post URL.
     */
    public void setUrl(String url)
    {
        getProperties().put(URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the post URL has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
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
     * Returns the post status.
     */
    public SavedPostStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the post status.
     */
    public void setStatus(String status)
    {
        setStatus(SavedPostStatus.valueOf(status));
    }

    /**
     * Sets the post status.
     */
    public void setStatus(SavedPostStatus status)
    {
        this.status = status;
    }
}