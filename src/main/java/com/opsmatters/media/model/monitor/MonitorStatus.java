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

/**
 * Represents the status of a content monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum MonitorStatus
{
    NEW("New", -1),
    WAITING("Waiting", 1),
    EXECUTING("Executing", 1),
    CHANGED("Changed", 0),
    REVIEW("Review", 0),
    RESUMING("Resuming", 1),
    RETRYING("Retrying", 1),
    ERROR("Error", 1),
    DISABLED("Disabled", -1),
    PENDING("Pending", 0), // Pseudo status
    RUNNING("Running", 1), // Pseudo status
    STOPPED("Stopped", -1), // Pseudo status
    ALL("All", 0); // Pseudo status

    private String value;
    private int state;

    /**
     * Constructor that takes the status value.
     * @param value The value for the status
     * @param state The state for the status
     */
    MonitorStatus(String value, int state)
    {
        this.value = value;
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
}