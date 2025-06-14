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
import com.opsmatters.media.model.ManagedEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing the execution results of a task.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaskExecution extends ManagedEntity
{
    private String taskId = "";
    private long executionTime = -1L;
    private int updatedCount = -1;
    private int deletedCount = -1;
    private String errorMessage = "";

    /**
     * Default constructor.
     */
    public TaskExecution()
    {
    }

    /**
     * Constructor that takes a task.
     */
    public TaskExecution(Task task)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setTaskId(task.getId());
    }

    /**
     * Copy constructor.
     */
    public TaskExecution(TaskExecution obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(TaskExecution obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTaskId(obj.getTaskId());
            setExecutionTime(obj.getExecutionTime());
            setUpdatedCount(obj.getUpdatedCount());
            setDeletedCount(obj.getDeletedCount());
            setErrorMessage(obj.getErrorMessage());
        }
    }

    /**
     * Returns the task id.
     */
    public String getTaskId()
    {
        return taskId;
    }

    /**
     * Sets the task id.
     */
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    /**
     * Returns the time taken for the task to execute.
     */
    public long getExecutionTime()
    {
        return executionTime;
    }

    /**
     * Sets the time taken for the task to execute.
     */
    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    /**
     * Returns the updated count.
     */
    public int getUpdatedCount()
    {
        return updatedCount;
    }

    /**
     * Sets the updated count.
     */
    public void setUpdatedCount(int updatedCount)
    {
        this.updatedCount = updatedCount;
    }

    /**
     * Returns the deleted count.
     */
    public int getDeletedCount()
    {
        return deletedCount;
    }

    /**
     * Sets the deleted count.
     */
    public void setDeletedCount(int deletedCount)
    {
        this.deletedCount = deletedCount;
    }

    /**
     * Returns the task error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the task error message.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns <CODE>true</CODE> if the task error message has been set.
     */
    public boolean hasErrorMessage()
    {
        return errorMessage != null && errorMessage.length() > 0;
    }
}