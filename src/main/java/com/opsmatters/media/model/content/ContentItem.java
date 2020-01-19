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

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.ContentConfiguration;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentItem implements java.io.Serializable
{
    private ContentSummary details;
    private boolean detailsSet = false;

    private String uuid = "";
    private int id = -1;
    private String code = "";
    private boolean published = false;
    private String createdBy = "";
    private boolean deployed = false;

    /**
     * Default constructor.
     */
    public ContentItem()
    {
    }

    /**
     * Initialise the content item.
     */
    public void init()
    {
        if(!hasUniqueId())
            setUuid();
    }

    /**
     * Resets the content UUID and ID.
     */
    public void reinit()
    {
        setUuid(null);
        setUuid();
        clearId();
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentItem obj)
    {
        setUuid(new String(obj.getUuid() != null ? obj.getUuid() : ""));
        setCode(new String(obj.getCode() != null ? obj.getCode() : ""));
        setId(obj.getId());
        setPublished(obj.isPublished());
        setCreatedBy(new String(obj.getCreatedBy() != null ? obj.getCreatedBy() : ""));
        setDeployed(obj.isDeployed());
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        setCode(obj.optString(Fields.CODE));
        setId(obj.optInt(Fields.ID));
        setUuid(obj.optString(Fields.UUID));
        setPublishedDateMillis(obj.optLong(Fields.PUBLISHED_DATE));
        setTitle(obj.optString(Fields.TITLE));
        setSummary(obj.optString(Fields.SUMMARY));
        setPublished(obj.optBoolean(Fields.PUBLISHED, false));
        setCreatedBy(obj.optString(Fields.CREATED_BY));
        setDeployed(obj.optBoolean(Fields.DEPLOYED));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(Fields.CODE, getCode());
        ret.put(Fields.ID, getId());
        ret.put(Fields.UUID, getUuid());
        ret.put(Fields.PUBLISHED_DATE, getPublishedDateMillis());
        ret.putOpt(Fields.TITLE, getTitle());
        if(getSummary() != null && getSummary().length() > 0)
            ret.putOpt(Fields.SUMMARY, getSummary());
        ret.put(Fields.PUBLISHED, isPublished());
        ret.put(Fields.CREATED_BY, getCreatedBy());
        ret.put(Fields.DEPLOYED, isDeployed());

        return ret;
    }

    /**
     * Returns the set of output fields from the content item.
     */
    public Fields toFields()
    {
        Fields ret = new Fields();

        ret.put(Fields.ID, Integer.toString(getId()));
        ret.put(Fields.UUID, getUuid());
        ret.put(Fields.PUBLISHED_DATE, getPublishedDateAsString());
        ret.put(Fields.TITLE, getTitle());
        ret.put(Fields.SUMMARY, getSummary());
        ret.put(Fields.PUBLISHED, isPublished() ? "1" : "0");
        ret.put(Fields.CREATED_BY, getCreatedBy());

        return ret;
    }

    /**
     * Copies any external attributes of the given object.
     * <p>
     * Implemented by super-class.
     */
    public void copyExternalAttributes(ContentItem obj)
    {
    }

    /**
     * Use the given configuration to set defaults for the content item.
     */
    public void init(ContentConfiguration config)
    {
        if(config.hasField(Fields.CODE))
            setCode(config.getField(Fields.CODE));

        String published = config.getField(Fields.PUBLISHED);
        setPublished(published == null || published.equals("0") ? false : true);

        setCreatedBy(System.getProperty("om-config.ui.username", "anonymous"));
    }

    /**
     * Returns the content UUID.
     */
    public String toString()
    {
        return getUuid();
    }

    /**
     * Returns the content type.
     */
    public abstract ContentType getType();

    /**
     * Sets the details from a summary.
     */
    public void setContentSummary(ContentSummary obj)
    {
        setPublishedDate(obj.getPublishedDate());
        setTitle(new String(obj.getTitle()));
        setSummary(new String(obj.getSummary()));
    }

    /**
     * Returns <CODE>true</CODE> if the details have been set.
     */
    public boolean hasContentDetails()
    {
        return detailsSet;
    }

    /**
     * Set to <CODE>true</CODE> if the details have been set.
     */
    public void setContentDetails(boolean detailsSet)
    {
        this.detailsSet = detailsSet;
    }

    /**
     * Returns the content details.
     */
    public ContentSummary getContentDetails()
    {
        return details;
    }

    /**
     * Set the content details.
     */
    protected void setContentDetails(ContentSummary details)
    {
        this.details = details;
    }

    /**
     * Returns the content UUID.
     */
    public String getUniqueId()
    {
        return getUuid();
    }

    /**
     * Returns <CODE>true</CODE> if the unique ID has been set.
     */
    public boolean hasUniqueId()
    {
        return getUniqueId() != null && getUniqueId().length() > 0;
    }

    /**
     * Returns the content UUID.
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * Sets the content UUID.
     */
    public void setUuid()
    {
        setUuid(StringUtils.getUUID(null));
    }

    /**
     * Sets the content UUID.
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Returns the content ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the content ID.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Clears the content ID.
     */
    public void clearId()
    {
        setId(-1);
    }

    /**
     * Returns the content GUID.
     */
    public String getGuid()
    {
        return String.format("%s-%s-%05d", getType().code(), code, id);
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the content title.
     */
    public String getTitle()
    {
        return details.getTitle();
    }

    /**
     * Sets the content title.
     */
    public void setTitle(String title)
    {
        details.setTitle(title != null ? title : "");
    }

    /**
     * Returns the date the content was published.
     */
    public Instant getPublishedDate()
    {
        return details.getPublishedDate();
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
    public LocalDateTime getPublishedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getPublishedDate());
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getPublishedDate(), pattern);
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
        details.setPublishedDate(publishedDate);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateMillis(long millis)
    {
        if(millis > 0L)
            setPublishedDate(Instant.ofEpochMilli(millis));
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
     * Sets the date the content was published.
     */
    public void setPublishedDateUTC(LocalDateTime publishedDate)
    {
        if(publishedDate != null)
            setPublishedDate(TimeUtils.toInstantUTC(publishedDate));
    }

    /**
     * Returns the content summary.
     */
    public String getSummary()
    {
        return details.getSummary();
    }

    /**
     * Sets the content summary.
     */
    public void setSummary(String summary)
    {
        details.setSummary(summary != null ? summary : "");
    }

    /**
     * Returns <CODE>true</CODE> if this content should be published.
     */
    public boolean isPublished()
    {
        return published;
    }

    /**
     * Returns <CODE>true</CODE> if this content should be published.
     */
    public Boolean getPublishedObject()
    {
        return new Boolean(isPublished());
    }

    /**
     * Set to <CODE>true</CODE> if this content should be published.
     */
    public void setPublished(boolean published)
    {
        this.published = published;
    }

    /**
     * Set to <CODE>true</CODE> if this content should be published.
     */
    public void setPublishedObject(Boolean published)
    {
        setPublished(published != null && published.booleanValue());
    }

    /**
     * Returns the user that created the content.
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the user that created the content.
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * Returns <CODE>true</CODE> if this content has been deployed.
     */
    public boolean isDeployed()
    {
        return deployed;
    }

    /**
     * Set to <CODE>true</CODE> if this content has been deployed.
     */
    public void setDeployed(boolean deployed)
    {
        this.deployed = deployed;
    }

    /**
     * Returns <CODE>true</CODE> if this content has metatags to set.
     */
    public boolean hasMetatags()
    {
        return false;
    }

    /**
     * Add the metatags for this content to the given JSON object.
     */
    protected void addMetatags(JSONObject obj)
    {
    }

    /**
     * Returns the metatags for this content in JSON format.
     */
    public String getMetatags()
    {
        String ret = "";

        if(hasMetatags())
        {
            JSONObject obj = new JSONObject();
            addMetatags(obj);
            ret = obj.toString();
        }

        return ret;
    }
}