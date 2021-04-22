/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.model.monitor;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the status of a content monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum MonitorStatus
{
    NEW("New", "glyphicon-unchecked", "", -1),
    WAITING("Waiting", "glyphicon-hourglass", "", 1),
    EXECUTING("Executing", "glyphicon-cog", "status-warn", 1),
    CHANGED("Changed", "glyphicon-adjust", "status-warn", 0),
    REVIEW("Review", "glyphicon-eye-open", "status-warn", 0),
    RESUMING("Resuming", "glyphicon-hourglass", "status-warn", 1),
    RETRYING("Retrying", "glyphicon-alert", "status-warn", 1),
    ERROR("Error", "glyphicon-alert", "status-error", 1),
    DISABLED("Disabled", "glyphicon-ban-circle", "status-error", -1),
    PENDING("Pending", "", "", 0), // Pseudo status
    RUNNING("Running", "", "", 1), // Pseudo status
    STOPPED("Stopped", "", "", -1), // Pseudo status
    ALL("All", "", "", 0); // Pseudo status

    private String value;
    private String icon;
    private String css;
    private int state;

    /**
     * Constructor that takes the status value.
     * @param value The value for the status
     * @param icon The glyphicon for the status
     * @param css The css class for the status
     * @param state The state for the status
     */
    MonitorStatus(String value, String icon, String css, int state)
    {
        this.value = value;
        this.icon = icon;
        this.css = css;
        this.state = state;
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
     * Returns <CODE>true<CODE> if the monitor state is PENDING.
     * @return <CODE>true<CODE> if the monitor state is PENDING.
     */
    public boolean isPending()
    {
        return state == 0;
    }

    /**
     * Returns <CODE>true<CODE> if the monitor state is RUNNING.
     * @return <CODE>true<CODE> if the monitor state is RUNNING.
     */
    public boolean isRunning()
    {
        return state > 0;
    }

    /**
     * Returns <CODE>true<CODE> if the monitor state is STOPPED.
     * @return <CODE>true<CODE> if the monitor state is STOPPED.
     */
    public boolean isStopped()
    {
        return state < 0;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static MonitorStatus fromValue(String value)
    {
        MonitorStatus[] types = values();
        for(MonitorStatus type : types)
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
     * Returns a list of the monitor statuses.
     */
    public static List<MonitorStatus> toList()
    {
        List<MonitorStatus> ret = new ArrayList<MonitorStatus>();

        ret.add(NEW);
        ret.add(WAITING);
        ret.add(EXECUTING);
        ret.add(CHANGED);
        ret.add(REVIEW);
        ret.add(RESUMING);
        ret.add(RETRYING);
        ret.add(ERROR);
        ret.add(DISABLED);

        return ret;
    }
}