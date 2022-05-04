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
import org.json.JSONObject;
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
    public static final String TITLE = "title";
    public static final String ERROR_MESSAGE = "error-message";

    private Instant startDate;
    private AlertReason reason;
    private AlertStatus status;
    private String title = "";
    private String errorMessage = "";

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
        setStartDate(monitor.getUpdatedDate());
        setCode(monitor.getCode());
        setReason(reason);
        setStatus(AlertStatus.NEW);
        setMonitorId(monitor.getId());
        setTitle(monitor.getTitle());
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
            setStartDate(obj.getStartDate());
            setReason(obj.getReason());
            setStatus(obj.getStatus());
            setTitle(obj.getTitle());
            setErrorMessage(obj.getErrorMessage());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(TITLE, getTitle());
        ret.putOpt(ERROR_MESSAGE, getErrorMessage());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setTitle(obj.optString(TITLE));
        setErrorMessage(obj.optString(ERROR_MESSAGE));
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
     * Returns the alert start date.
     */
    public Instant getStartDate()
    {
        return startDate;
    }

    /**
     * Returns the alert start date.
     */
    public long getStartDateMillis()
    {
        return getStartDate() != null ? getStartDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the alert start date.
     */
    public LocalDateTime getStartDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getStartDate());
    }

    /**
     * Returns the alert start date.
     */
    public String getStartDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(startDate, pattern);
    }

    /**
     * Returns the alert start date.
     */
    public String getStartDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(startDate, pattern, timezone);
    }

    /**
     * Returns the alert start date.
     */
    public String getStartDateAsString()
    {
        return getStartDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the alert start date.
     */
    public void setStartDate(Instant startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Sets the alert start date.
     */
    public void setStartDateMillis(long millis)
    {
        if(millis > 0L)
            this.startDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the alert start date.
     */
    public void setStartDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setStartDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the alert start date.
     */
    public void setStartDateAsString(String str) throws DateTimeParseException
    {
        setStartDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the alert start date.
     */
    public void setStartDateUTC(LocalDateTime startDate)
    {
        if(startDate != null)
            setStartDate(TimeUtils.toInstantUTC(startDate));
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
     * Returns the alert crawled page title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the alert crawled page title.
     */
    public void setTitle(String title)
    {
        this.title = title;
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