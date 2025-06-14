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

package com.opsmatters.media.model.logging;

import java.time.Instant;
import com.opsmatters.media.model.ManagedEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Defines a log event.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogEvent extends ManagedEntity
{
    private LogEventType type;
    private LogEventCategory category;
    private LogEventLevel level;
    private String message = "";

    /**
     * Default constructor.
     */
    public LogEvent()
    {
    }

    /**
     * Copy constructor.
     */
    public LogEvent(LogEvent obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a type, category, level and message.
     * @param type The type of the event
     * @param category The category of the event
     * @param level The level of the event
     * @param message The message of the event
     */
    public LogEvent(LogEventType type, LogEventCategory category, LogEventLevel level, String message)
    {
        init();
        setType(type);
        setCategory(category);
        setLevel(level);
        setMessage(message);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(LogEvent obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setType(obj.getType());
            setCategory(obj.getCategory());
            setLevel(obj.getLevel());
            setMessage(obj.getMessage());
        }
    }

    /**
     * Initialise the event.
     */
    protected void init()
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
    }

    /**
     * Returns the message of the event.
     * @return The message of the event
     */
    public String toString()
    {
        return getMessage();
    }

    /**
     * Returns the type of the event.
     * @return The type of the event
     */
    public LogEventType getType()
    {
        return type;
    }

    /**
     * Sets the type of the event.
     * @param type The type of the event
     */
    public void setType(String type)
    {
        setType(LogEventType.valueOf(type));
    }

    /**
     * Sets the type of the event.
     * @param type The type of the event
     */
    public void setType(LogEventType type)
    {
        this.type = type;
    }

    /**
     * Returns the category of the event.
     * @return The category of the event
     */
    public LogEventCategory getCategory()
    {
        return category;
    }

    /**
     * Sets the category of the event.
     * @param category The category of the event
     */
    public void setCategory(String category)
    {
        setCategory(LogEventCategory.valueOf(category));
    }

    /**
     * Sets the category of the event.
     * @param category The category of the event
     */
    public void setCategory(LogEventCategory category)
    {
        this.category = category;
    }

    /**
     * Returns the level of the event.
     * @return The level of the event
     */
    public LogEventLevel getLevel()
    {
        return level;
    }

    /**
     * Sets the level of the event.
     * @param level The level of the event
     */
    public void setLevel(String level)
    {
        setLevel(LogEventLevel.valueOf(level));
    }

    /**
     * Sets the level of the event.
     * @param level The level of the event
     */
    public void setLevel(LogEventLevel level)
    {
        this.level = level;
    }

    /**
     * Returns the message of the event.
     * @return The message of the event
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message of the event.
     * @param message The message of the event
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns <CODE>true</CODE> if the message of the event has been set.
     * @return <CODE>true</CODE> if the message of the event has been set
     */
    public boolean hasMessage()
    {
        return message != null && message.length() > 0;
    }
}