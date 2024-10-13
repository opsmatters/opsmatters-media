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
 * Class representing a content monitor review.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentReview extends ContentEvent
{
    public static final String NOTES = "notes";

    private Instant reviewDate;
    private ReviewReason reason;
    private String notes = "";
    private ReviewStatus status;
    private int sessionId = 0;

    /**
     * Default constructor.
     */
    public ContentReview()
    {
    }

    /**
     * Constructor that takes a monitor.
     */
    public ContentReview(ContentMonitor monitor)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(monitor.getCode());
        setReason(ReviewReason.UNDEFINED);
        setStatus(ReviewStatus.NEW);
        setMonitorId(monitor.getId());
    }

    /**
     * Copy constructor.
     */
    public ContentReview(ContentReview obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentReview obj)
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
        return EventType.REVIEW;
    }

    /**
     * Returns the review review date.
     */
    public Instant getReviewDate()
    {
        return reviewDate;
    }

    /**
     * Returns the review review date.
     */
    public long getReviewDateMillis()
    {
        return getReviewDate() != null ? getReviewDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the review review date.
     */
    public LocalDateTime getReviewDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getReviewDate());
    }

    /**
     * Returns the review review date.
     */
    public String getReviewDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(reviewDate, pattern);
    }

    /**
     * Returns the review review date.
     */
    public String getReviewDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(reviewDate, pattern, timezone);
    }

    /**
     * Returns the review review date.
     */
    public String getReviewDateAsString()
    {
        return getReviewDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the review review date.
     */
    public void setReviewDate(Instant reviewDate)
    {
        this.reviewDate = reviewDate;
    }

    /**
     * Sets the review review date.
     */
    public void setReviewDateMillis(long millis)
    {
        if(millis > 0L)
            this.reviewDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the review review date.
     */
    public void setReviewDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setReviewDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the review review date.
     */
    public void setReviewDateAsString(String str) throws DateTimeParseException
    {
        setReviewDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the review review date.
     */
    public void setReviewDateUTC(LocalDateTime reviewDate)
    {
        if(reviewDate != null)
            setReviewDate(TimeUtils.toInstantUTC(reviewDate));
    }

    /**
     * Returns the review reason.
     */
    public ReviewReason getReason()
    {
        return reason;
    }

    /**
     * Sets the review reason.
     */
    public void setReason(String reason)
    {
        setReason(ReviewReason.valueOf(reason));
    }

    /**
     * Sets the review reason.
     */
    public void setReason(ReviewReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the review notes.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the review notes.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Returns the review status.
     */
    public ReviewStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the review status.
     */
    public void setStatus(String status)
    {
        setStatus(ReviewStatus.valueOf(status));
    }

    /**
     * Sets the review status.
     */
    public void setStatus(ReviewStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the review status by the given user.
     */
    public void setStatus(ReviewStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns <CODE>true</CODE> if this review is NEW.
     */
    public boolean isNew()
    {
        return getStatus() == ReviewStatus.NEW;
    }

    /**
     * Returns <CODE>true</CODE> if this review is WAITING.
     */
    public boolean isWaiting()
    {
        return getStatus() == ReviewStatus.WAITING;
    }

    /**
     * Returns <CODE>true</CODE> if this review has been SKIPPED.
     */
    public boolean isSkipped()
    {
        return getStatus() == ReviewStatus.SKIPPED;
    }

    /**
     * Returns <CODE>true</CODE> if this review has been CLOSED.
     */
    public boolean isClosed()
    {
        return getStatus() == ReviewStatus.CLOSED;
    }

    /**
     * Returns the review session id.
     */
    public int getSessionId()
    {
        return sessionId;
    }

    /**
     * Sets the review session id.
     */
    public void setSessionId(int sessionId)
    {
        this.sessionId = sessionId;
    }
}