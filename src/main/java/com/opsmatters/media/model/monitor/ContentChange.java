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
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor change.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChange extends MonitorItem
{
    private String code = "";
    private ChangeStatus status;
    private String snapshot = "";
    private String monitorId = "";

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
        setStatus(ChangeStatus.PENDING);
        setSnapshot(snapshot);
        setMonitorId(monitor.getId());
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
            setStatus(obj.getStatus());
            setSnapshot(obj.getSnapshot());
            setMonitorId(obj.getMonitorId());
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
     * Returns <CODE>true</CODE> if the change status is PENDING.
     */
    public boolean isPending()
    {
        return status == ChangeStatus.PENDING;
    }

    /**
     * Returns the monitor snapshot with the change.
     */
    public String getSnapshot()
    {
        return snapshot;
    }

    /**
     * Returns the monitor snapshot with the change.
     */
    public JSONObject getSnapshotAsJson()
    {
        return new JSONObject(snapshot);
    }

    /**
     * Sets the monitor snapshot with the change.
     */
    public void setSnapshot(String snapshot)
    {
        this.snapshot = snapshot;
    }

    /**
     * Sets the monitor snapshot with the change.
     */
    public void setSnapshot(JSONObject snapshot)
    {
        setSnapshot(snapshot.toString());
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
}