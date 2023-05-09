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

import java.io.Serializable;

/**
 * Defines a log entry.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogEntry implements Serializable
{
    private LogLevel level;
    private LogCategory category;
    private String text = "";

    /**
     * Constructor that takes a level, category and text.
     * @param level The level of the entry
     * @param category The category of the entry
     * @param text The text of the entry
     */
    public LogEntry(LogLevel level, LogCategory category, String text)
    {
        setLevel(level);
        setCategory(category);
        setText(text);
    }

    /**
     * Returns the text of the entry.
     * @return The text of the entry
     */
    public String toString()
    {
        return getText();
    }

    /**
     * Returns the level of the entry.
     * @return The level of the entry
     */
    public LogLevel getLevel()
    {
        return level;
    }

    /**
     * Sets the level of the entry.
     * @param level The level of the entry
     */
    public void setLevel(LogLevel level)
    {
        this.level = level;
    }

    /**
     * Returns the category of the entry.
     * @return The category of the entry
     */
    public LogCategory getCategory()
    {
        return category;
    }

    /**
     * Sets the category of the entry.
     * @param category The category of the entry
     */
    public void setCategory(LogCategory category)
    {
        this.category = category;
    }

    /**
     * Returns the text of the entry.
     * @return The text of the entry
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text of the entry.
     * @param text The text of the entry
     */
    public void setText(String text)
    {
        this.text = text;
    }
}