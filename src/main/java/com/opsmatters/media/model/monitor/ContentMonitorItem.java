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

import java.time.Instant;
import com.opsmatters.media.model.BaseEntityItem;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing a content monitor list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitorItem extends BaseEntityItem<ContentMonitor>
{
    private ContentMonitor content = new ContentMonitor();

    /**
     * Default constructor.
     */
    public ContentMonitorItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ContentMonitorItem(ContentMonitorItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a monitor.
     */
    public ContentMonitorItem(ContentMonitor obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentMonitorItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(ContentMonitor obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public ContentMonitor get()
    {
        return content;
    }

    /**
     * Returns the monitor organisation.
     */
    public String getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the monitor organisation.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Returns the monitor organisation name.
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
     * Returns the monitor name.
     */
    public String getName()
    {
        return content.getName();
    }

    /**
     * Sets the monitor name.
     */
    public void setName(String name)
    {
        content.setName(name);
    }

    /**
     * Returns the monitor content type.
     */
    public ContentType getContentType()
    {
        return content.getContentType();
    }

    /**
     * Sets the monitor content type.
     */
    public void setContentType(String contentType)
    {
        content.setContentType(contentType);
    }

    /**
     * Sets the monitor content type.
     */
    public void setContentType(ContentType contentType)
    {
        content.setContentType(contentType);
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public Instant getExecutedDate()
    {
        return content.getExecutedDate();
    }

    /**
     * Returns the date the monitor was last executed.
     */
    public long getExecutedDateMillis()
    {
        return content.getExecutedDateMillis();
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDate(Instant executedDate)
    {
        content.setExecutedDate(executedDate);
    }

    /**
     * Sets the date the monitor was last executed.
     */
    public void setExecutedDateMillis(long millis)
    {
        content.setExecutedDateMillis(millis);
    }

    /**
     * Returns the monitor status.
     */
    public MonitorStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the monitor status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the monitor status.
     */
    public void setStatus(MonitorStatus status)
    {
        content.setStatus(status);
    }

    /**
     * Returns <CODE>true</CODE> if this monitor has content alerts.
     */
    public boolean hasAlerts()
    {
        return content.hasAlerts();
    }

    /**
     * Set to <CODE>true</CODE> if this monitor has content alerts.
     */
    public void setAlerts(boolean alerts)
    {
        content.setAlerts(alerts);
    }
}