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

import static com.opsmatters.media.model.logging.ErrorLevel.*;

/**
 * Defines a set of log entries.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Log
{
    private List<LogEntry> entries = new ArrayList<LogEntry>();

    /**
     * Default constructor.
     */
    public Log()
    {
    }

    /**
     * Clears the entries.
     */
    public void clear()
    {
        entries.clear();
    }

    /**
     * Appends an entry.
     */
    public void add(LogEntry entry)
    {
        entries.add(entry);
    }

    /**
     * Appends an entry.
     */
    public void add(ErrorLevel level, ErrorCategory category, String text)
    {
        add(new LogEntry(level, category, text));
    }

    /**
     * Appends an DEBUG entry.
     */
    public void debug(ErrorCategory category, String text)
    {
        add(DEBUG, category, text);
    }

    /**
     * Appends an INFO entry.
     */
    public void info(ErrorCategory category, String text)
    {
        add(INFO, category, text);
    }

    /**
     * Appends an WARN entry.
     */
    public void warn(ErrorCategory category, String text)
    {
        add(WARN, category, text);
    }

    /**
     * Appends an ERROR entry.
     */
    public void error(ErrorCategory category, String text)
    {
        add(ERROR, category, text);
    }

    /**
     * Returns the list of entries for the given level and above.
     */
    public List<LogEntry> list(ErrorLevel level)
    {
        List<LogEntry> ret = new ArrayList<LogEntry>();
        for(LogEntry entry : entries)
        {
            if(level != null && entry.getLevel().precedence() < level.precedence())
                continue;

            ret.add(entry);
        }

        return ret;
    }

    /**
     * Returns the list of entries.
     */
    public List<LogEntry> list()
    {
        return list(null);
    }

    /**
     * Returns the count of entries.
     */
    public int size()
    {
        return entries.size();
    }
}