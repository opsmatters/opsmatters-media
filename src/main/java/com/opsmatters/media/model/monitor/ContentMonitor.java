/*
 * Copyright 2020 Gerald Curley
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
import com.opsmatters.media.config.content.ContentConfiguration;
import com.opsmatters.media.config.monitor.MonitorConfiguration;
import com.opsmatters.media.model.BaseItem;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.admin.Email;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitor extends BaseItem
{
    public static final String CONTENT_TYPE = "content-type";
    public static final String URL = "url";
    public static final String INTERVAL = "interval";
    public static final String DIFFERENCE = "difference";
    public static final String SORT = "sort";
    public static final String EXECUTION_TIME = "execution-time";
    public static final String ERROR_MESSAGE = "error-message";
    public static final String RETRY = "retry";

    private String code = "";
    private String name = "";
    private ContentType contentType;
    private Instant executedDate;
    private long executionTime = -1L;
    private MonitorStatus status;
    private String url = "";
    private String snapshot = "";
    private String changeId = "";
    private int interval = -1;
    private int difference = 0;
    private ContentSort sort;
    private boolean active = false;
    private String errorMessage = "";
    private int retry = 0;

    /**
     * Default constructor.
     */
    public ContentMonitor()
    {
    }

    /**
     * Constructor that takes a content configuration and a monitor configuration.
     */
    public ContentMonitor(ContentConfiguration content, MonitorConfiguration config)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(content.getCode());
        setContentType(content.getType());
        setName(config.getName());
        setStatus(MonitorStatus.NEW);
        setSnapshot(new JSONObject());
        setInterval(config.getInterval());
        setMinDifference(config.getMinDifference());
        setSort(config.getSort());
        setActive(config.isActive());
    }

    /**
     * Copy constructor.
     */
    public ContentMonitor(ContentMonitor obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentMonitor obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setContentType(obj.getContentType());
            setStatus(obj.getStatus());
            setExecutedDate(obj.getExecutedDate());
            setExecutionTime(obj.getExecutionTime());
            setUrl(obj.getUrl());
            setSnapshot(obj.getSnapshot());
            setChangeId(obj.getChangeId());
            setInterval(obj.getInterval());
            setMinDifference(obj.getMinDifference());
            setSort(obj.getSort());
            setActive(obj.isActive());
            setErrorMessage(obj.getErrorMessage());
            setRetry(obj.getRetry());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(CONTENT_TYPE, getContentType().name());
        ret.putOpt(URL, getUrl());
        ret.putOpt(INTERVAL, getInterval());
        ret.putOpt(DIFFERENCE, getMinDifference());
        ret.putOpt(SORT, getSort().name());
        ret.putOpt(EXECUTION_TIME, getExecutionTime());
        ret.putOpt(ERROR_MESSAGE, getErrorMessage());
        ret.putOpt(RETRY, getRetry());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setContentType(obj.optString(CONTENT_TYPE));
        setUrl(obj.optString(URL));
        setInterval(obj.optInt(INTERVAL));
        setMinDifference(obj.optInt(DIFFERENCE));
        setSort(obj.optString(SORT));
        setExecutionTime(obj.optLong(EXECUTION_TIME));
        setErrorMessage(obj.optString(ERROR_MESSAGE));
        setRetry(obj.optInt(RETRY));
    }

    /**
     * Returns the monitor GUID.
     */
    public String getGuid()
    {
        return getGuid(getContentType(), code, name);
    }

    /**
     * Returns the monitor GUID.
     */
    public static String getGuid(ContentType type, String code, String name)
    {
        return String.format("%s-%s-%s", type.code(), code, name);
    }

    /**
     * Returns the monitor organisation.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the monitor organisation.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the monitor name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the monitor name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the monitor content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the monitor content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(ContentType.valueOf(contentType));
    }

    /**
     * Sets the monitor content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns the monitor status.
     */
    public MonitorStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the monitor status.
     */
    public void setStatus(String status)
    {
        setStatus(MonitorStatus.valueOf(status));
    }

    /**
     * Sets the monitor status.
     */
    public void setStatus(MonitorStatus status)
    {
        this.status = status;
    }

    /**
     * Set the monitor status to PENDING.
     */
    public void setPending(ContentChange change)
    {
        if(getStatus() != MonitorStatus.PENDING)
        {
            setStatus(MonitorStatus.PENDING);
            setUpdatedDate(Instant.now());
            setChangeId(change.getId());
        }
    }

    /**
     * Clear the monitor status after PENDING.
     */
    public void clearPending()
    {
        if(getStatus() == MonitorStatus.PENDING)
        {
            setStatus(MonitorStatus.RESUMING);
            setUpdatedDate(Instant.now());
            setChangeId("");
        }
    }

    /**
     * Resets the monitor.
     */
    public void clear()
    {
        setStatus(MonitorStatus.RESUMING);
        setUpdatedDate(Instant.now());
        setExecutedDate(null);
        setChangeId("");
        setErrorMessage("");
    }

    /**
     * Returns <CODE>true</CODE> if the monitor status is EXECUTING.
     */
    public boolean isExecuting()
    {
        return status == MonitorStatus.EXECUTING;
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public Instant getExecutedDate()
    {
        return executedDate;
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public long getExecutedDateMillis()
    {
        return getExecutedDate() != null ? getExecutedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public LocalDateTime getExecutedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getExecutedDate());
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public String getExecutedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(executedDate, pattern);
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public String getExecutedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(executedDate, pattern, timezone);
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public String getExecutedDateAsString()
    {
        return getExecutedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDate(Instant executedDate)
    {
        this.executedDate = executedDate;
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateMillis(long millis)
    {
        if(millis > 0L)
            this.executedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the monitor item was last executed.
     */
    public void setExecutedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setExecutedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateAsString(String str) throws DateTimeParseException
    {
        setExecutedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateUTC(LocalDateTime executedDate)
    {
        if(executedDate != null)
            setExecutedDate(TimeUtils.toInstantUTC(executedDate));
    }

    /**
     * Returns the time taken for the last monitor execution.
     */
    public long getExecutionTime()
    {
        return executionTime;
    }

    /**
     * Sets the time taken for the last monitor execution.
     */
    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    /**
     * Returns the monitor url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the monitor url.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor url has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.length() > 0;
    }

    /**
     * Returns the last monitor snapshot.
     */
    public String getSnapshot()
    {
        return snapshot;
    }

    /**
     * Returns the last monitor snapshot.
     */
    public JSONObject getSnapshotAsJson()
    {
        return new JSONObject(snapshot);
    }

    /**
     * Returns the last monitor snapshot with pretty print.
     */
    public String getPrettySnapshot()
    {
        return getSnapshotAsJson().toString(2);
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setSnapshot(String snapshot)
    {
        this.snapshot = snapshot;
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setSnapshot(JSONObject snapshot)
    {
        setSnapshot(snapshot.toString());
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setPrettySnapshot(String snapshot)
    {
        setSnapshot(new JSONObject(snapshot));
    }

    /**
     * Returns the change id.
     */
    public String getChangeId()
    {
        return changeId;
    }

    /**
     * Sets the change id.
     */
    public void setChangeId(String changeId)
    {
        this.changeId = changeId;
    }

    /**
     * Returns <CODE>true</CODE> if the change id id has been set.
     */
    public boolean hasChangeId()
    {
        return changeId != null && changeId.length() > 0;
    }

    /**
     * Returns the interval between monitor checks (in minutes).
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between monitor checks (in minutes).
     */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the minimum % difference between monitor checks.
     */
    public int getMinDifference()
    {
        return difference;
    }

    /**
     * Sets the minimum % difference between monitor checks.
     */
    public void setMinDifference(int difference)
    {
        this.difference = difference;
    }

    /**
     * Returns the monitor content sort.
     */
    public ContentSort getSort()
    {
        return sort;
    }

    /**
     * Sets the monitor content sort.
     */
    public void setSort(String sort)
    {
        setSort(sort != null && sort.length() > 0 ? ContentSort.valueOf(sort) : ContentSort.NONE);
    }

    /**
     * Sets the monitor content sort.
     */
    public void setSort(ContentSort sort)
    {
        this.sort = sort;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor is enabled.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Set to <CODE>true</CODE> if the monitor is enabled.
     */
    public void setActive(boolean active)
    {
        this.active = active;
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

    /**
     * Returns the number of retries since the last error.
     */
    public int getRetry()
    {
        return retry;
    }

    /**
     * Sets the number of retries since the last error.
     */
    public void setRetry(int retry)
    {
        this.retry = retry;
    }

    /**
     * Returns the email for a monitor status change.
     */
    public Email getStatusEmail()
    {
        String subject = String.format("Monitor %s: %s",
            getStatus().name(), getGuid());
        String body = String.format("The monitor %s was %s at %s.",
            getGuid(), getStatus().name(), getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT));
        return new Email(subject, body);
    }
}