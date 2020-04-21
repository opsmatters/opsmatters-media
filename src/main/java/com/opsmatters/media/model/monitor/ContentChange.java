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

import org.json.JSONObject;

/**
 * Class representing a content monitor change.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChange extends MonitorItem
{
    private String code = "";
    private ChangeStatus status;
    private JSONObject snapshot;
    private String monitorId = "";

    /**
     * Default constructor.
     */
    public ContentChange()
    {
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
     * Returns <CODE>true</CODE> if the change status is PENDING.
     */
    public boolean isPending()
    {
        return status == ChangeStatus.PENDING;
    }

    /**
     * Returns the last monitor snapshot.
     */
    public JSONObject getSnapshot()
    {
        return snapshot;
    }

    /**
     * Sets the last monitor snapshot.
     */
    public void setSnapshot(JSONObject snapshot)
    {
        this.snapshot = snapshot;
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
     * Returns <CODE>true</CODE> if the monitorId id has been set.
     */
    public boolean hasMonitorId()
    {
        return monitorId != null && monitorId.length() > 0;
    }
}