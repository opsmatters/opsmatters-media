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

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a social media post draft.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class DraftPost extends SocialPost
{
    private static final String ENABLED = ".enabled";

    private String templateId = "";
    private Map<String,String> properties = new LinkedHashMap<String,String>();
    private DraftStatus status;
    private Instant scheduledDate;

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DraftPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTemplateId(obj.getTemplateId());
            setProperties(obj.getProperties());
            setStatus(obj.getStatus());
            setScheduledDate(obj.getScheduledDate());
        }
    }

    /**
     * Returns the post title.
     */
    public abstract String getTitle();

    /**
     * Returns the attributes as a JSON object.
     */
    public abstract JSONObject getAttributes();

    /**
     * Initialise the attributes using a JSON object.
     */
    public abstract void setAttributes(JSONObject obj);

    /**
     * Returns the post template id.
     */
    public String getTemplateId()
    {
        return templateId;
    }

    /**
     * Sets the post template id.
     */
    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    /**
     * Returns <CODE>true</CODE> if the post template id has been set.
     */
    public boolean hasTemplateId()
    {
        return templateId != null && templateId.length() > 0;
    }

    /**
     * Returns the post properties.
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * Returns the post properties as a JSON object.
     */
    public JSONObject getPropertiesAsJson()
    {
        return new JSONObject(getProperties());
    }

    /**
     * Sets the post properties.
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Sets the post properties from a JSON object.
     */
    public void setProperties(JSONObject obj)
    {
        getProperties().clear();
        Iterator<String> keys = obj.keys();
        while(keys.hasNext())
        {
            String key = keys.next();
            getProperties().put(key, obj.getString(key));
        }
    }

    /**
     * Returns <CODE>true</CODE> if the given post property has been set.
     */
    public boolean hasProperty(String key)
    {
        return getProperties().containsKey(key);
    }

    /**
     * Returns <CODE>true</CODE> if the enabled property for given channel has been set.
     */
    public boolean hasEnabled(SocialChannel channel)
    {
        return hasProperty(channel.getName()+ENABLED);
    }

    /**
     * Returns the value of the given post property.
     */
    public String getProperty(String key)
    {
        return getProperties().get(key);
    }

    /**
     * Returns <CODE>true</CODE> if the given channel is enabled.
     */
    public boolean isEnabled(SocialChannel channel)
    {
        return Boolean.parseBoolean(getProperty(channel.getName()+ENABLED));
    }

    /**
     * Sets the value of the given post property.
     */
    public void setProperty(String key, String value)
    {
        getProperties().put(key, value);
    }

    /**
     * Set to <CODE>true</CODE> if the given channel is enabled.
     */
    public void setEnabled(SocialChannel channel, boolean enabled)
    {
        setProperty(channel.getName()+ENABLED, Boolean.toString(enabled));
    }

    /**
     * Returns the post hashtags.
     */
    public String getHashtags()
    {
        return getProperties().get(PostTemplate.HASHTAGS);
    }

    /**
     * Sets the post hashtags.
     */
    public void setHashtags(String hashtags)
    {
        getProperties().put(PostTemplate.HASHTAGS, hashtags);
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
        return getProperties().get(PostTemplate.URL);
    }

    /**
     * Sets the post URL.
     */
    public void setUrl(String url)
    {
        getProperties().put(PostTemplate.URL, url);
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
    public boolean hasShortenedUrl()
    {
        return hasUrl() && getUrl().indexOf("bit.ly") != -1;
    }

    /**
     * Returns the post original URL.
     */
    public String getOriginalUrl()
    {
        return getProperties().get(PostTemplate.ORIGINAL_URL);
    }

    /**
     * Sets the post original URL.
     */
    public void setOriginalUrl(String url)
    {
        getProperties().put(PostTemplate.ORIGINAL_URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the post original URL has been set.
     */
    public boolean hasOriginalUrl()
    {
        return getOriginalUrl() != null && getOriginalUrl().length() > 0;
    }

    /**
     * Returns the draft status.
     */
    public DraftStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the draft status.
     */
    public void setStatus(String status)
    {
        setStatus(DraftStatus.valueOf(status));
    }

    /**
     * Sets the draft status.
     */
    public void setStatus(DraftStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is SUBMITTED.
     */
    public boolean isSubmitted()
    {
        return status == DraftStatus.SUBMITTED;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is PROCESSED.
     */
    public boolean isProcessed()
    {
        return status == DraftStatus.PROCESSED;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is ERROR.
     */
    public boolean isError()
    {
        return status == DraftStatus.ERROR;
    }

    /**
     * Returns <CODE>true</CODE> if the draft status is SKIPPED.
     */
    public boolean isSkipped()
    {
        return status == DraftStatus.SKIPPED;
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