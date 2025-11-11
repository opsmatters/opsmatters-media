/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model.drupal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a log message in the dblog.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogMessage extends BaseEntity
{
    private long wid = -1L;
    private int uid = -1;
    private String type = "";
    private String message = "";
    private Severity severity = Severity.NONE;
    private String link = "";
    private String location = "";
    private String referer = "";
    private String hostname = "";

    /**
     * Default constructor.
     */
    public LogMessage()
    {
    }

    /**
     * Copy constructor.
     */
    public LogMessage(LogMessage obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(LogMessage obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setWid(obj.getWid());
            setUid(obj.getUid());
            setType(obj.getType());
            setMessage(obj.getMessage());
            setSeverity(obj.getSeverity());
            setLink(obj.getLink());
            setLocation(obj.getLocation());
            setReferer(obj.getReferer());
            setHostname(obj.getHostname());
        }
    }

    /**
     * Returns the message id.
     */
    public long getWid()
    {
        return wid;
    }

    /**
     * Sets the message id.
     */
    public void setWid(long wid)
    {
        this.wid = wid;
    }

    /**
     * Returns the message uid.
     */
    public int getUid()
    {
        return uid;
    }

    /**
     * Sets the message uid.
     */
    public void setUid(int uid)
    {
        this.uid = uid;
    }

    /**
     * Returns the message type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the message type.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns the severity of the message.
     */
    public Severity getSeverity()
    {
        return severity;
    }

    /**
     * Sets the severity of the message.
     */
    public void setSeverity(short severity)
    {
        setSeverity(Severity.fromLevel(severity));
    }

    /**
     * Sets the severity of the message.
     */
    public void setSeverity(Severity severity)
    {
        this.severity = severity;
    }

    /**
     * Returns <CODE>true</CODE> if this message is an error or warning.
     */
    public boolean isError()
    {
        return getSeverity().level() <= Severity.WARNING.level();
    }

    /**
     * Returns the message link.
     */
    public String getLink()
    {
        return link;
    }

    /**
     * Sets the message link.
     */
    public void setLink(String link)
    {
        this.link = link;
    }

    /**
     * Returns the message location.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets the message location.
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Returns the message referer.
     */
    public String getReferer()
    {
        return referer;
    }

    /**
     * Sets the message referer.
     */
    public void setReferer(String referer)
    {
        this.referer = referer;
    }

    /**
     * Returns the message hostname.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the message hostname.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }
}