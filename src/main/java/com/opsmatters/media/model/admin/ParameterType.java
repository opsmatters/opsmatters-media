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

package com.opsmatters.media.model.admin;

/**
 * Represents the type of an application parameter.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ParameterType
{
    SYSTEM("system"),
    UI("ui"),
    MONITOR_MANAGER("monitor-manager"),
    SOCIAL_MANAGER("social-manager"),
    NOTIFICATION_MANAGER("notification-manager"),
    FEED_MANAGER("feed-manager"),
    TASK_MANAGER("task-manager"),
    THREAD_MANAGER("thread-manager"),
    SYNC_MANAGER("sync-manager");

    private String value;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    ParameterType(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the type.
     * @return The value of the type.
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
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static ParameterType fromValue(String value)
    {
        ParameterType[] types = values();
        for(ParameterType type : types)
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