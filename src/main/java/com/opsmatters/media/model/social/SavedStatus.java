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

package com.opsmatters.media.model.social;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the status of a saved social post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum SavedStatus
{
    NEW("New", "glyphicon-unchecked", "", -1),
    ACTIVE("Active", "glyphicon-ok-circle", "status-success", 1),
    DISABLED("Disabled", "glyphicon-ban-circle", "status-error", 0),
    ARCHIVED("Archived", "glyphicon-trash", "status-error", 0),
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
    SavedStatus(String value, String icon, String css, int state)
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
     * Returns <CODE>true<CODE> if the organisation state is ACTIVE.
     * @return <CODE>true<CODE> if the organisation state is ACTIVE.
     */
    public boolean isActive()
    {
        return state > 0;
    }

    /**
     * Returns <CODE>true<CODE> if the organisation state is INACTIVE.
     * @return <CODE>true<CODE> if the organisation state is INACTIVE.
     */
    public boolean isInactive()
    {
        return state == 0;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static SavedStatus fromValue(String value)
    {
        SavedStatus[] types = values();
        for(SavedStatus type : types)
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
     * Returns a list of the post statuses.
     */
    public static List<SavedStatus> toList()
    {
        List<SavedStatus> ret = new ArrayList<SavedStatus>();

        ret.add(NEW);
        ret.add(ACTIVE);
        ret.add(DISABLED);
        ret.add(ARCHIVED);

        return ret;
    }
}