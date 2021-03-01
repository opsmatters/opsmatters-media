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
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.SnapshotDiff;

/**
 * Class representing a content monitor change.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChange extends ContentEvent
{
    private ChangeStatus status;
    private String snapshotBefore = "";
    private String snapshotAfter = "";
    private long executionTime = -1L;
    private int difference = 0;

    /**
     * Default constructor.
     */
    public ContentChange()
    {
    }

    /**
     * Constructor that takes a monitor and a snapshot.
     */
    public ContentChange(ContentMonitor monitor, ContentSnapshot snapshot)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(monitor.getCode());
        setStatus(ChangeStatus.NEW);
        setSnapshotBefore(monitor.getSnapshot());
        setSnapshotAfter(snapshot);
        setMonitorId(monitor.getId());
        setExecutionTime(monitor.getExecutionTime());
        setDifference(SnapshotDiff.getDifferencePercent(getSnapshotBefore(), getSnapshotAfter()));
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
            setStatus(obj.getStatus());
            setSnapshotBefore(obj.getSnapshotBefore());
            setSnapshotAfter(obj.getSnapshotAfter());
            setExecutionTime(obj.getExecutionTime());
            setDifference(obj.getDifference());
        }
    }

    /**
     * Returns the type of the event.
     */
    @Override
    public EventType getType()
    {
        return EventType.CHANGE;
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
     * Sets the monitor snapshot before the change.
     */
    public void setSnapshotBefore(String snapshotBefore)
    {
        this.snapshotBefore = snapshotBefore;
    }

    /**
     * Returns the monitor snapshot after the change.
     */
    public String getSnapshotAfter()
    {
        return snapshotAfter;
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
    public void setSnapshotAfter(ContentSnapshot snapshotAfter)
    {
        setSnapshotAfter(snapshotAfter.toString());
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