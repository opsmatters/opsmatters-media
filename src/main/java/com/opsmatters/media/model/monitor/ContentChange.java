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
import org.json.JSONObject;
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TextDiff;

/**
 * Class representing a content monitor change.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChange extends OwnedItem
{
    private String code = "";
    private String title = "";
    private ChangeStatus status;
    private String snapshotBefore = "";
    private String snapshotAfter = "";
    private String monitorId = "";
    private long executionTime = -1L;
    private int difference = 0;

    /**
     * Default constructor.
     */
    public ContentChange()
    {
    }

    /**
     * Constructor that takes a monitor.
     */
    public ContentChange(ContentMonitor monitor, JSONObject snapshot)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(monitor.getCode());
        setStatus(ChangeStatus.NEW);
        setSnapshotBefore(monitor.getSnapshot());
        setSnapshotAfter(snapshot);
        setMonitorId(monitor.getId());
        setExecutionTime(monitor.getExecutionTime());
        setDifference(TextDiff.getDifferencePercent(getSnapshotBefore(), getSnapshotAfter()));
    }

    /**
     * Copy constructor.
     */
    public ContentChange(ContentChange obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentChange obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setTitle(obj.getTitle());
            setStatus(obj.getStatus());
            setSnapshotBefore(obj.getSnapshotBefore());
            setSnapshotAfter(obj.getSnapshotAfter());
            setMonitorId(obj.getMonitorId());
            setExecutionTime(obj.getExecutionTime());
            setDifference(obj.getDifference());
        }
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
     * Returns the monitor organisation title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the monitor organisation title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation title has been set.
     */
    public boolean hasTitle()
    {
        return title != null && title.length() > 0;
    }

    /**
     * Returns the change status.
     */
    public ChangeStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the change status.
     */
    public void setStatus(String status)
    {
        setStatus(ChangeStatus.valueOf(status));
    }

    /**
     * Sets the change status.
     */
    public void setStatus(ChangeStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the change status by the given user.
     */
    public void setStatus(ChangeStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns the monitor snapshot before the change.
     */
    public String getSnapshotBefore()
    {
        return snapshotBefore;
    }

    /**
     * Returns the monitor snapshot before the change.
     */
    public JSONObject getSnapshotBeforeAsJson()
    {
        return new JSONObject(snapshotBefore);
    }

    /**
     * Returns the monitor snapshot before the change with pretty print.
     */
    public String getPrettySnapshotBefore()
    {
        return getSnapshotBeforeAsJson().toString(2);
    }

    /**
     * Sets the monitor snapshot before the change.
     */
    public void setSnapshotBefore(String snapshotBefore)
    {
        this.snapshotBefore = snapshotBefore;
    }

    /**
     * Sets the monitor snapshot before the change.
     */
    public void setSnapshotBefore(JSONObject snapshotBefore)
    {
        setSnapshotBefore(snapshotBefore.toString());
    }

    /**
     * Sets the monitor snapshot before the change with pretty print.
     */
    public void setPrettySnapshotBefore(String snapshotBefore)
    {
        setSnapshotBefore(new JSONObject(snapshotBefore));
    }

    /**
     * Returns the monitor snapshot after the change.
     */
    public String getSnapshotAfter()
    {
        return snapshotAfter;
    }

    /**
     * Returns the monitor snapshot after the change.
     */
    public JSONObject getSnapshotAfterAsJson()
    {
        return new JSONObject(snapshotAfter);
    }

    /**
     * Returns the monitor snapshot after the change with pretty print.
     */
    public String getPrettySnapshotAfter()
    {
        return getSnapshotAfterAsJson().toString(2);
    }

    /**
     * Sets the monitor snapshot after the change.
     */
    public void setSnapshotAfter(String snapshotAfter)
    {
        this.snapshotAfter = snapshotAfter;
    }

    /**
     * Sets the monitor snapshot after the change.
     */
    public void setSnapshotAfter(JSONObject snapshotAfter)
    {
        setSnapshotAfter(snapshotAfter.toString());
    }

    /**
     * Sets the monitor snapshot after the change with pretty print.
     */
    public void setPrettySnapshotAfter(String snapshotAfter)
    {
        setSnapshotAfter(new JSONObject(snapshotAfter));
    }

    /**
     * Returns the monitor id.
     */
    public String getMonitorId()
    {
        return monitorId;
    }

    /**
     * Sets the monitor id.
     */
    public void setMonitorId(String monitorId)
    {
        this.monitorId = monitorId;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor id id has been set.
     */
    public boolean hasMonitorId()
    {
        return monitorId != null && monitorId.length() > 0;
    }

    /**
     * Returns the time taken for the monitor execution.
     */
    public long getExecutionTime()
    {
        return executionTime;
    }

    /**
     * Sets the time taken for the monitor execution.
     */
    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    /**
     * Returns the % difference between the snapshots.
     */
    public int getDifference()
    {
        return difference;
    }

    /**
     * Sets the % difference between the snapshots.
     */
    public void setDifference(int difference)
    {
        this.difference = difference;
    }
}