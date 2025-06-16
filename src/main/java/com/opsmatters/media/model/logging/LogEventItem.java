/*
 * Copyright 2024 Gerald Curley
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

import com.opsmatters.media.model.BaseEntityItem;

/**
 * Class representing a log event list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class LogEventItem<T extends LogEvent> extends BaseEntityItem<T>
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
    public LogEventType getType()
    {
        return content.getType();
    }

    /**
     * Sets the event type.
     */
    public void setType(LogEventType type)
    {
        content.setType(type);
    }

    /**
     * Sets the event type.
     */
    public void setType(String type)
    {
        content.setType(type);
    }

    /**
     * Returns the event category.
     */
    public LogEventCategory getCategory()
    {
        return content.getCategory();
    }

    /**
     * Sets the event category.
     */
    public void setCategory(LogEventCategory category)
    {
        content.setCategory(category);
    }

    /**
     * Sets the event category.
     */
    public void setCategory(String category)
    {
        content.setCategory(category);
    }

    /**
     * Returns the event level.
     */
    public LogEventLevel getLevel()
    {
        return content.getLevel();
    }

    /**
     * Sets the event level.
     */
    public void setLevel(LogEventLevel level)
    {
        content.setLevel(level);
    }

    /**
     * Sets the event level.
     */
    public void setLevel(String level)
    {
        content.setLevel(level);
    }
}