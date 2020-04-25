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
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitor extends MonitorItem
{
    public static final String CONTENT_TYPE = "content-type";
    public static final String URL = "url";
    public static final String INTERVAL = "interval";
    public static final String EXECUTION_TIME = "execution-time";

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
    private boolean active = false;

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
            setActive(obj.isActive());
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
        ret.putOpt(EXECUTION_TIME, getExecutionTime());

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
        setExecutionTime(obj.optLong(EXECUTION_TIME));
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
     * Resolved the monitor status from PENDING.
     */
    public void clearPending()
    {
        if(getStatus() == MonitorStatus.PENDING)
        {
            setChangeId("");
            setStatus(MonitorStatus.WAITING);
            setUpdatedDate(Instant.now());
        }
    }

    /**
     * Returns <CODE>true</CODE> if the monitor status is RUNNING.
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
}