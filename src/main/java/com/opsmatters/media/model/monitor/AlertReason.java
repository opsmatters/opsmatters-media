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

package com.opsmatters.media.model.monitor;

/**
 * Represents the reason for a content monitor alert.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum AlertReason
{
    INACTIVITY("No Activity", false),
    SUSPENDED("Suspended", true),
    UNREACHABLE("Unreachable", true),
    MANUAL("Manual", false),
    ALL("All", false); // Pseudo status

    private String value;
    private boolean notification = false;

    /**
     * Constructor that takes the reason value.
     * @param value The value for the reason
     * @param notify <CODE>true</CODE> if this reason requires a notification
     */
    AlertReason(String value, boolean notification)
    {
        this.value = value;
        this.notification = notification;
    }

    /**
     * Returns the value of the reason.
     * @return The value of the reason.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the value of the reason.
     * @return The value of the reason.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns <CODE>true</CODE> if this reason requires a notification.
     * @return <CODE>true</CODE> if this reason requires a notification.
     */
    public boolean notification()
    {
        return notification;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static AlertReason fromValue(String value)
    {
        AlertReason[] types = values();
        for(AlertReason type : types)
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