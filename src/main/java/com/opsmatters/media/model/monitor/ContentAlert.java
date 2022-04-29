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
package com.opsmatters.media.model.monitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor alert.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentAlert extends ContentEvent
{
    private Instant effectiveDate;
    private AlertReason reason;
    private AlertStatus status;
    private String notes = "";
    private boolean change = false;

    /**
     * Default constructor.
     */
    public ContentAlert()
    {
    }

    /**
     * Constructor that takes a monitor.
     */
    public ContentAlert(ContentMonitor monitor, AlertReason reason)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setEffectiveDate(monitor.getUpdatedDate());
        setCode(monitor.getCode());
        setReason(reason);
        setStatus(AlertStatus.NEW);
        setMonitorId(monitor.getId());
    }

    /**
     * Copy constructor.
     */
    public ContentAlert(ContentAlert obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentAlert obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setEffectiveDate(obj.getEffectiveDate());
            setReason(obj.getReason());
            setStatus(obj.getStatus());
            setNotes(obj.getNotes());
            setChange(obj.hasChange());
        }
    }

    /**
     * Returns the type of the event.
     */
    @Override
    public EventType getType()
    {
        return EventType.ALERT;
    }

    /**
     * Returns the alert effective date.
     */
    public Instant getEffectiveDate()
    {
        return effectiveDate;
    }

    /**
     * Returns the alert effective date.
     */
    public long getEffectiveDateMillis()
    {
        return getEffectiveDate() != null ? getEffectiveDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the alert effective date.
     */
    public LocalDateTime getEffectiveDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getEffectiveDate());
    }

    /**
     * Returns the alert effective date.
     */
    public String getEffectiveDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(effectiveDate, pattern);
    }

    /**
     * Returns the alert effective date.
     */
    public String getEffectiveDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(effectiveDate, pattern, timezone);
    }

    /**
     * Returns the alert effective date.
     */
    public String getEffectiveDateAsString()
    {
        return getEffectiveDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the alert effective date.
     */
    public void setEffectiveDate(Instant effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }

    /**
     * Sets the alert effective date.
     */
    public void setEffectiveDateMillis(long millis)
    {
        if(millis > 0L)
            this.effectiveDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the alert effective date.
     */
    public void setEffectiveDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setEffectiveDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the alert effective date.
     */
    public void setEffectiveDateAsString(String str) throws DateTimeParseException
    {
        setEffectiveDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the alert effective date.
     */
    public void setEffectiveDateUTC(LocalDateTime effectiveDate)
    {
        if(effectiveDate != null)
            setEffectiveDate(TimeUtils.toInstantUTC(effectiveDate));
    }

    /**
     * Returns the alert reason.
     */
    public AlertReason getReason()
    {
        return reason;
    }

    /**
     * Sets the alert reason.
     */
    public void setReason(String reason)
    {
        setReason(AlertReason.valueOf(reason));
    }

    /**
     * Sets the alert reason.
     */
    public void setReason(AlertReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the alert status.
     */
    public AlertStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the alert status.
     */
    public void setStatus(String status)
    {
        setStatus(AlertStatus.valueOf(status));
    }

    /**
     * Sets the alert status.
     */
    public void setStatus(AlertStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the alert status by the given user.
     */
    public void setStatus(AlertStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns the alert notes.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the alert notes.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Returns <CODE>true</CODE> if the alert notes has been set.
     */
    public boolean hasNotes()
    {
        return notes != null && notes.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this alert should raise a change.
     */
    public boolean hasChange()
    {
        return change;
    }

    /**
     * Returns <CODE>true</CODE> if this alert should raise a change.
     */
    public Boolean getChangeObject()
    {
        return Boolean.valueOf(hasChange());
    }

    /**
     * Set to <CODE>true</CODE> if this alert should raise a change.
     */
    public void setChange(boolean change)
    {
        this.change = change;
    }

    /**
     * Set to <CODE>true</CODE> if this alert should raise a change.
     */
    public void setChangeObject(Boolean change)
    {
        setChange(change != null && change.booleanValue());
    }
}