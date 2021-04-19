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

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the status of a task.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum TaskStatus
{
    NEW("New", "glyphicon-unchecked", ""),
    ACTIVE("Active", "glyphicon-ok-circle", "status-success"),
    PENDING("Pending", "glyphicon-check", "status-warn"),
    EXECUTING("Executing", "glyphicon-cog", "status-warn"),
    ERROR("Error", "glyphicon-alert", "status-error"),
    COMPLETED("Completed", "glyphicon-ok-circle", "status-success"),
    DISABLED("Disabled", "glyphicon-remove-circle", "status-error"),
    ALL("All", "", ""); // Pseudo status

    private String value;
    private String icon;
    private String css;

    /**
     * Constructor that takes the status value.
     * @param value The value for the status
     * @param icon The glyphicon for the status
     * @param css The css class for the status
     */
    TaskStatus(String value, String icon, String css)
    {
        this.value = value;
        this.icon = icon;
        this.css = css;
    }

    /**
     * Returns the value of the status.
     * @return The value of the status.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the status.
     * @return The value of the status.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the glyphicon of the status.
     * @return The glyphicon of the status.
     */
    public String icon()
    {
        return icon;
    }

    /**
     * Returns the css class of the status.
     * @return The css class of the status.
     */
    public String css()
    {
        return css;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static TaskStatus fromValue(String value)
    {
        TaskStatus[] types = values();
        for(TaskStatus type : types)
        {
            if(type.value().equals(value))
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of types.
     * @param value The type value
     * @return <CODE>true</CODE> if the given value is contained in the list of types
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }

    /**
     * Returns a list of the task statuses.
     */
    public static List<TaskStatus> toList()
    {
        List<TaskStatus> ret = new ArrayList<TaskStatus>();

        ret.add(NEW);
        ret.add(ACTIVE);
        ret.add(PENDING);
        ret.add(EXECUTING);
        ret.add(ERROR);
        ret.add(COMPLETED);
        ret.add(DISABLED);

        return ret;
    }
}