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

//GERALD: check
import java.time.Instant;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.model.content.Organisation;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing a social media update.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialUpdate implements java.io.Serializable
{
    private String id = "";
    private Instant createdDate;
//GERALD: needed?
    private Instant updatedDate;
    private String organisation = "";
    private ContentType contentType;
    private UpdateStatus status;
    private String createdBy = "";

    static public enum UpdateStatus
    {
        NEW,
        PENDING,
        PROCESSED,
        SKIPPED;
    };

    /**
     * Default constructor.
     */
    public SocialUpdate()
    {
    }

    /**
     * Constructor that takes an organisation and content type.
     */
    public SocialUpdate(Organisation organisation, ContentType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setOrganisation(organisation.getCode());
        setContentType(type);
        setStatus(UpdateStatus.PENDING);
    }

    /**
     * Copy constructor.
     */
    public SocialUpdate(SocialUpdate obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setCreatedDate(obj.getCreatedDate());
            setUpdatedDate(obj.getUpdatedDate());
            setOrganisation(obj.getOrganisation());
            setContentType(obj.getContentType());
            setStatus(obj.getStatus());
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
     * Returns the update id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the update id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the date the update was created.
     */
    public Instant getCreatedDate()
    {
        return createdDate;
    }

    /**
     * Returns the date the update was created.
     */
    public long getCreatedDateMillis()
    {
        return getCreatedDate() != null ? getCreatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the update was created.
     */
    public String getCreatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(createdDate, pattern);
    }

    /**
     * Returns the date the update was created.
     */
    public String getCreatedDateAsString()
    {
        return getCreatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the update was created.
     */
    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    /**
     * Sets the date the update was created.
     */
    public void setCreatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.createdDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the update was created.
     */
    public void setCreatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setCreatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the update was created.
     */
    public void setCreatedDateAsString(String str) throws DateTimeParseException
    {
        setCreatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the date the update status was last updated.
     */
    public Instant getUpdatedDate()
    {
        return updatedDate;
    }

    /**
     * Returns the date the update was last updated.
     */
    public long getUpdatedDateMillis()
    {
        return getUpdatedDate() != null ? getUpdatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the update status was last updated.
     */
    public String getUpdatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(updatedDate, pattern);
    }

    /**
     * Returns the date the update status was last updated.
     */
    public String getUpdatedDateAsString()
    {
        return getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the update status was last updated.
     */
    public void setUpdatedDate(Instant updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    /**
     * Sets the date the update status was last updated.
     */
    public void setUpdatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.updatedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the update status was last updated.
     */
    public void setUpdatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setUpdatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the update status was last updated.
     */
    public void setUpdatedDateAsString(String str) throws DateTimeParseException
    {
        setUpdatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the update organisation.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the update organisation.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns the update content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the update content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(ContentType.valueOf(contentType));
    }

    /**
     * Sets the update content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns the update status.
     */
    public UpdateStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the update status.
     */
    public void setStatus(String status)
    {
        setStatus(UpdateStatus.valueOf(status));
    }

    /**
     * Sets the update status.
     */
    public void setStatus(UpdateStatus status)
    {
        this.status = status;
    }

    /**
     * Returns the update creator.
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the update creator.
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
}