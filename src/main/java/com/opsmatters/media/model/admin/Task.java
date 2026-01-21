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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a task to be executed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Task extends BaseEntity
{
    private Instant executedDate;
    private String name = "";
    private int itemCount = -1;
    private TaskType type;
    private int interval = -1;
    private ChronoUnit intervalUnit = ChronoUnit.DAYS;
    private boolean enabled = false;
    private TaskStatus status = TaskStatus.NEW;

    /**
     * Default constructor.
     */
    public Task()
    {
    }

    /**
     * Copy constructor.
     */
    public Task(Task obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Task obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setType(obj.getType());
            setExecutedDate(obj.getExecutedDate());
            setItemCount(obj.getItemCount());
            setInterval(obj.getInterval());
            setIntervalUnit(obj.getIntervalUnit());
            setEnabled(obj.isEnabled());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the name of the task.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the task.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the type of the task.
     */
    public TaskType getType()
    {
        return type;
    }

    /**
     * Sets the type of the task.
     */
    public void setType(TaskType type)
    {
        this.type = type;
    }

    /**
     * Sets the type of the task.
     */
    public void setType(String type)
    {
        setType(TaskType.valueOf(type));
    }

    /**
     * Returns <CODE>true</CODE> if the task is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this task is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the task is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this task is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }

    /**
     * Returns the task interval.
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * Sets the task interval.
     */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the task interval unit.
     */
    public ChronoUnit getIntervalUnit()
    {
        return intervalUnit;
    }

    /**
     * Sets the task interval unit.
     */
    public void setIntervalUnit(ChronoUnit intervalUnit)
    {
        this.intervalUnit = intervalUnit;
    }

    /**
     * Sets the task interval unit.
     */
    public void setIntervalUnit(String intervalUnit)
    {
        setIntervalUnit(ChronoUnit.valueOf(intervalUnit));
    }

    /**
     * Returns the date the task was last executed.
     */
    public Instant getExecutedDate()
    {
        return executedDate;
    }

    /**
     * Returns the date the task was last executed.
     */
    public long getExecutedDateMillis()
    {
        return getExecutedDate() != null ? getExecutedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the task was last executed.
     */
    public LocalDateTime getExecutedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getExecutedDate());
    }

    /**
     * Returns the date the task was last executed.
     */
    public String getExecutedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(executedDate, pattern);
    }

    /**
     * Returns the date the task was last executed.
     */
    public String getExecutedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(executedDate, pattern, timezone);
    }

    /**
     * Returns the date the task was last executed.
     */
    public String getExecutedDateAsString()
    {
        return getExecutedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the task was last executed.
     */
    public void setExecutedDate(Instant executedDate)
    {
        this.executedDate = executedDate;
    }

    /**
     * Sets the date the task was last executed.
     */
    public void setExecutedDateMillis(long millis)
    {
        if(millis > 0L)
            this.executedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the task was last executed.
     */
    public void setExecutedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setExecutedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the task was last executed.
     */
    public void setExecutedDateAsString(String str) throws DateTimeParseException
    {
        setExecutedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the task was last executed.
     */
    public void setExecutedDateUTC(LocalDateTime executedDate)
    {
        if(executedDate != null)
            setExecutedDate(TimeUtils.toInstantUTC(executedDate));
    }

    /**
     * Returns <CODE>true</CODE> if the task is ready to execute.
     */
    public boolean isReady()
    {
        boolean ret = true;
        if(getExecutedDate() != null)
        {
            if(getInterval() > 0)
                ret = Instant.now().isAfter(getExecutedDate().plus(getInterval(), getIntervalUnit()));
            else
                ret = false;
        }

        return ret;
    }

    /**
     * Returns the item count.
     */
    public int getItemCount()
    {
        return itemCount;
    }

    /**
     * Sets the item count.
     */
    public void setItemCount(int itemCount)
    {
        this.itemCount = itemCount;
    }

    /**
     * Returns the task status.
     */
    public TaskStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the task status.
     */
    public void setStatus(TaskStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the task status.
     */
    public void setStatus(String status)
    {
        setStatus(TaskStatus.valueOf(status));
    }

    /**
     * Restarts the task eg. after hanging.
     */
    public void restart()
    {
        setStatus(TaskStatus.NEW);
        setUpdatedDate(Instant.now());
        setExecutedDate(null);
    }

    /**
     * Returns <CODE>true</CODE> if the task is not currently processing.
     */
    public boolean isIdle()
    {
        return getStatus().idle();
    }
}