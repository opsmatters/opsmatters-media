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

import java.util.List;
import java.util.ArrayList;

import static com.opsmatters.media.model.logging.LogEventLevel.*;

/**
 * Defines a set of log events.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Log
{
    private LogEventType type;   
    private List<LogEvent> events = new ArrayList<LogEvent>();

    /**
     * Default constructor.
     */
    public Log(LogEventType type)
    {
        setType(type);
    }

    /**
     * Returns the type of the log.
     * @return The type of the log
     */
    public LogEventType getType()
    {
        return type;
    }

    /**
     * Sets the type of the log.
     * @param type The type of the log
     */
    public void setType(LogEventType type)
    {
        this.type = type;
    }

    /**
     * Clears the events.
     */
    public void clear()
    {
        events.clear();
    }

    /**
     * Appends an event.
     */
    public void add(LogEvent event)
    {
        events.add(event);
    }

    /**
     * Appends a list of events.
     */
    public void addAll(List<LogEvent> events)
    {
        this.events.addAll(events);
    }

    /**
     * Appends an event.
     */
    public void add(LogEventCategory category, LogEventLevel level, String message)
    {
        add(new LogEvent(type, category, level, message));
    }

    /**
     * Appends a DEBUG event.
     */
    public void debug(LogEventCategory category, String message)
    {
        add(category, DEBUG, message);
    }

    /**
     * Appends an INFO event.
     */
    public void info(LogEventCategory category, String message)
    {
        add(category, INFO, message);
    }

    /**
     * Appends a WARN event.
     */
    public void warn(LogEventCategory category, String message)
    {
        add(category, WARN, message);
    }

    /**
     * Appends an ERROR event.
     */
    public void error(LogEventCategory category, String message)
    {
        add(category, ERROR, message);
    }

    /**
     * Returns an ERROR builder.
     */
    public LogError.Builder error(ErrorCode code, LogEventCategory category)
    {
        return LogError.builder(code, type, category, ERROR);
    }

    /**
     * Returns a WARN builder.
     */
    public LogError.Builder warn(ErrorCode code, LogEventCategory category)
    {
        return LogError.builder(code, type, category, WARN);
    }

    /**
     * Closes the error builder and adds it to the log.
     */
    public void add(LogError.Builder builder)
    {
        add(builder.build());
    }

    /**
     * Returns the list of events for the given level and above.
     */
    public List<LogEvent> getEvents(LogEventLevel level)
    {
        List<LogEvent> ret = new ArrayList<LogEvent>();
        for(LogEvent event : events)
        {
            if(level != null && event.getLevel().precedence() < level.precedence())
                continue;

            ret.add(event);
        }

        return ret;
    }

    /**
     * Returns the list of events.
     */
    public List<LogEvent> getEvents()
    {
        return getEvents(null);
    }

    /**
     * Returns the count of events.
     */
    public int size()
    {
        return events.size();
    }
}