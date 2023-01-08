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
package com.opsmatters.media.model.admin;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a notification.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Notification extends BaseEntity
{
    private String code = "";
    private String summary = "";
    private NotificationLevel level = NotificationLevel.NONE;
    private NotificationType type = NotificationType.NONE;
    private NotificationStatus status = NotificationStatus.NEW;
    private int expiry = -1;

    /**
     * Default constructor.
     */
    public Notification()
    {
    }

    /**
     * Constructor that takes a type, code and summary.
     */
    public Notification(NotificationType type, String code, String summary)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(code);
        setSummary(summary);
        setType(type);
        setStatus(NotificationStatus.NEW);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Notification obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setSummary(obj.getSummary());
            setLevel(obj.getLevel());
            setType(obj.getType());
            setStatus(obj.getStatus());
            setExpiry(obj.getExpiry());
        }
    }

    /**
     * Returns the summary.
     */
    public String toString()
    {
        return getSummary();
    }

    /**
     * Returns the notification code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the notification code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the notification code has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the notification summary.
     */
    public String getSummary()
    {
        return summary;
    }

    /**
     * Sets the notification summary.
     */
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    /**
     * Returns <CODE>true</CODE> if the notification summary has been set.
     */
    public boolean hasSummary()
    {
        return summary != null && summary.length() > 0;
    }

    /**
     * Returns the notification's level.
     */
    public NotificationLevel getLevel()
    {
        return level;
    }

    /**
     * Sets the notification's level.
     */
    public void setLevel(NotificationLevel level)
    {
        this.level = level;
    }

    /**
     * Sets the notification's level.
     */
    public void setLevel(String level)
    {
        setLevel(NotificationLevel.valueOf(level));
    }

    /**
     * Returns the notification's type.
     */
    public NotificationType getType()
    {
        return type;
    }

    /**
     * Sets the notification's type.
     */
    public void setType(NotificationType type)
    {
        this.type = type;
    }

    /**
     * Sets the notification's type.
     */
    public void setType(String type)
    {
        setType(NotificationType.valueOf(type));
    }

    /**
     * Returns the notification's status.
     */
    public NotificationStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the notification's status.
     */
    public void setStatus(NotificationStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the notification's status.
     */
    public void setStatus(String status)
    {
        setStatus(NotificationStatus.valueOf(status));
    }

    /**
     * Returns the expiry (in minutes) of the notification.
     */
    public int getExpiry()
    {
        return expiry;
    }

    /**
     * Sets the expiry (in minutes) of the notification.
     */
    public void setExpiry(int expiry)
    {
        this.expiry = expiry;
    }
}