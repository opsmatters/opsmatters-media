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
import org.json.JSONObject;
import com.opsmatters.media.model.content.FieldName;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.social.SocialPostProperty.*;
import static com.opsmatters.media.model.social.DraftPostStatus.*;

/**
 * Class representing a social media post draft.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class DraftPost extends PropertyPost
{
    private String sourceId = "";
    private DraftPostStatus status;
    private Instant scheduledDate;

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DraftPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSourceId(obj.getSourceId());
            setStatus(obj.getStatus());
            setScheduledDate(obj.getScheduledDate());
        }
    }

    /**
     * Returns the post title.
     */
    public abstract String getTitle();

    /**
     * Sets the post title.
     */
    public abstract void setTitle(String title);

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        if(getScheduledDate() != null)
            ret.put(FieldName.SCHEDULED_DATE.value(), getScheduledDateMillis());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        long scheduledDateMillis = obj.optLong(FieldName.SCHEDULED_DATE.value());
        if(scheduledDateMillis > 0L)
            setScheduledDateMillis(scheduledDateMillis);
    }

    /**
     * Returns the post source id.
     */
    public String getSourceId()
    {
        return sourceId;
    }

    /**
     * Sets the post source id.
     */
    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    /**
     * Returns <CODE>true</CODE> if the post source id has been set.
     */
    public boolean hasSourceId()
    {
        return sourceId != null && sourceId.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the enabled property for given channel has been set.
     */
    public boolean hasEnabled(SocialChannel channel)
    {
        return getProperties().containsEnabled(channel);
    }

    /**
     * Returns <CODE>true</CODE> if the given channel is enabled.
     */
    public boolean isEnabled(SocialChannel channel)
    {
        return getProperties().isEnabled(channel);
    }

    /**
     * Set to <CODE>true</CODE> if the given channel is enabled.
     */
    public void setEnabled(SocialChannel channel, boolean enabled)
    {
        getProperties().setEnabled(channel, enabled);
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
     * Returns <CODE>true</CODE> if the post URL has been set and it has been shortened.
     */
    public boolean hasShortenedUrl(String shortDomain)
    {
        return hasUrl() && getUrl().indexOf(shortDomain) != -1;
    }

    /**
     * Returns the post original URL.
     */
    public String getOriginalUrl()
    {
        return getProperties().get(ORIGINAL_URL);
    }

    /**
     * Sets the post original URL.
     */
    public void setOriginalUrl(String url)
    {
        getProperties().put(ORIGINAL_URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the post original URL has been set.
     */
    public boolean hasOriginalUrl()
    {
        return getOriginalUrl() != null && getOriginalUrl().length() > 0;
    }

    /**
     * Returns the post status.
     */
    public DraftPostStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the post status.
     */
    public void setStatus(String status)
    {
        setStatus(DraftPostStatus.valueOf(status));
    }

    /**
     * Sets the post status.
     */
    public void setStatus(DraftPostStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is NEW.
     */
    public boolean isNew()
    {
        return status == NEW;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is SUBMITTED.
     */
    public boolean isSubmitted()
    {
        return status == SUBMITTED;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is PROCESSED.
     */
    public boolean isProcessed()
    {
        return status == PROCESSED;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is ERROR.
     */
    public boolean isError()
    {
        return status == ERROR;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is SKIPPED.
     */
    public boolean isSkipped()
    {
        return status == SKIPPED;
    }

    /**
     * Returns the date the item was scheduled.
     */
    public Instant getScheduledDate()
    {
        return scheduledDate;
    }

    /**
     * Returns the date the item was scheduled.
     */
    public long getScheduledDateMillis()
    {
        return getScheduledDate() != null ? getScheduledDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the item was scheduled.
     */
    public LocalDateTime getScheduledDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getScheduledDate());
    }

    /**
     * Returns the date the item was scheduled.
     */
    public String getScheduledDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(scheduledDate, pattern);
    }

    /**
     * Returns the date the item was scheduled.
     */
    public String getScheduledDateAsString()
    {
        return getScheduledDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDate(Instant scheduledDate)
    {
        this.scheduledDate = scheduledDate;
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateMillis(long millis)
    {
        if(millis > 0L)
            this.scheduledDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setScheduledDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateAsString(String str) throws DateTimeParseException
    {
        setScheduledDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateUTC(LocalDateTime scheduledDate)
    {
        if(scheduledDate != null)
            setScheduledDate(TimeUtils.toInstantUTC(scheduledDate));
    }
}