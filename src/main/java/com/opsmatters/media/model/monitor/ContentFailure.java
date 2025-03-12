/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.model.monitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor failure.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFailure extends ContentEvent
{
    public static final String NOTES = "notes";

    private Instant reviewDate;
    private FailureReason reason;
    private String notes = "";
    private FailureStatus status;
    private int sessionId = 0;

    /**
     * Default constructor.
     */
    public ContentFailure()
    {
    }

    /**
     * Constructor that takes a monitor.
     */
    public ContentFailure(ContentMonitor monitor)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(monitor.getCode());
        setReason(FailureReason.UNDEFINED);
        setStatus(FailureStatus.NEW);
        setMonitorId(monitor.getId());
    }

    /**
     * Copy constructor.
     */
    public ContentFailure(ContentFailure obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentFailure obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setReviewDate(obj.getReviewDate());
            setReason(obj.getReason());
            setNotes(obj.getNotes());
            setStatus(obj.getStatus());
            setSessionId(obj.getSessionId());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(NOTES, getNotes());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setNotes(obj.optString(NOTES));
    }

    /**
     * Returns the type of the event.
     */
    @Override
    public EventType getType()
    {
        return EventType.FAILURE;
    }

    /**
     * Returns the failure review date.
     */
    public Instant getReviewDate()
    {
        return reviewDate;
    }

    /**
     * Returns the failure review date.
     */
    public long getReviewDateMillis()
    {
        return getReviewDate() != null ? getReviewDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the failure review date.
     */
    public LocalDateTime getReviewDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getReviewDate());
    }

    /**
     * Returns the failure review date.
     */
    public String getReviewDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(reviewDate, pattern);
    }

    /**
     * Returns the failure review date.
     */
    public String getReviewDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(reviewDate, pattern, timezone);
    }

    /**
     * Returns the failure review date.
     */
    public String getReviewDateAsString()
    {
        return getReviewDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the failure review date.
     */
    public void setReviewDate(Instant reviewDate)
    {
        this.reviewDate = reviewDate;
    }

    /**
     * Sets the failure review date.
     */
    public void setReviewDateMillis(long millis)
    {
        if(millis > 0L)
            this.reviewDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the failure review date.
     */
    public void setReviewDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setReviewDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the failure review date.
     */
    public void setReviewDateAsString(String str) throws DateTimeParseException
    {
        setReviewDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the failure review date.
     */
    public void setReviewDateUTC(LocalDateTime reviewDate)
    {
        if(reviewDate != null)
            setReviewDate(TimeUtils.toInstantUTC(reviewDate));
    }

    /**
     * Returns the failure reason.
     */
    public FailureReason getReason()
    {
        return reason;
    }

    /**
     * Sets the failure reason.
     */
    public void setReason(String reason)
    {
        setReason(FailureReason.valueOf(reason));
    }

    /**
     * Sets the failure reason.
     */
    public void setReason(FailureReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the failure notes.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the failure notes.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Returns the failure status.
     */
    public FailureStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the failure status.
     */
    public void setStatus(String status)
    {
        setStatus(FailureStatus.valueOf(status));
    }

    /**
     * Sets the failure status.
     */
    public void setStatus(FailureStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the failure status by the given user.
     */
    public void setStatus(FailureStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns <CODE>true</CODE> if the status of this failure is NEW.
     */
    public boolean isNew()
    {
        return getStatus() == FailureStatus.NEW;
    }

    /**
     * Returns <CODE>true</CODE> if this failure has been RESOLVED.
     */
    public boolean isResolved()
    {
        return getStatus() == FailureStatus.RESOLVED;
    }

    /**
     * Returns the failure session id.
     */
    public int getSessionId()
    {
        return sessionId;
    }

    /**
     * Sets the failure session id.
     */
    public void setSessionId(int sessionId)
    {
        this.sessionId = sessionId;
    }
}