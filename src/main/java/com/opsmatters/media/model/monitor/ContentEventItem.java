/*
 * Copyright 2023 Gerald Curley
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

import com.opsmatters.media.model.BaseEntityItem;

/**
 * Class representing a content event post list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentEventItem<T extends ContentEvent> extends BaseEntityItem<T>
{
    private T content;

    /**
     * Returns the content object.
     */
    public T get()
    {
        return content;
    }

    /**
     * Returns the content object.
     */
    protected void set(T content)
    {
        super.set(content);
        this.content = content;
    }

    /**
     * Returns the event type.
     */
    public EventType getType()
    {
        return content.getType();
    }

    /**
     * Returns the post organisation.
     */
    public String getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the post organisation.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return content.getOrganisation();
    }

    /**
     * Sets the organisation name.
     */
    public void setOrganisation(String organisation)
    {
        content.setOrganisation(organisation);
    }

    /**
     * Returns the monitor id.
     */
    public String getMonitorId()
    {
        return content.getMonitorId();
    }

    /**
     * Sets the monitor id.
     */
    public void setMonitorId(String monitorId)
    {
        content.setMonitorId(monitorId);
    }

    /**
     * Returns <CODE>true</CODE> if the monitor id has been set.
     */
    public boolean hasMonitorId()
    {
        return content.hasMonitorId();
    }
}