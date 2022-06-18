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

import com.opsmatters.media.config.organisation.Organisations;
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.model.organisation.Organisation;

/**
 * Class representing a content monitor event.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentEvent extends OwnedItem
{
    private String code = "";
    private String organisation = "";
    private String monitorId = "";

    /**
     * Default constructor.
     */
    public ContentEvent()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentEvent(ContentEvent obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentEvent obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation());
            setMonitorId(obj.getMonitorId());
        }
    }

    /**
     * Returns the type of the event.
     */
    public abstract EventType getType();

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

        Organisation organisation = Organisations.get(code);
        setOrganisation(organisation != null ? organisation.getName() : "");
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the monitor organisation name.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the monitor organisation name.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
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
     * Returns <CODE>true</CODE> if the monitor id has been set.
     */
    public boolean hasMonitorId()
    {
        return monitorId != null && monitorId.length() > 0;
    }
}