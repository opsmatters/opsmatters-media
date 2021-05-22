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

package com.opsmatters.media.model.content;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the reason for an organisation being archived.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ArchiveReason
{
    NONE("None"),
    REJECTED("Rejected"),
    DISAPPROVED("Disapproved"),
    DISCONTINUED("Discontinued"),
    DEFUNCT("Defunct"),
    MIGRATED("Migrated"),
    ALL("All"); // Pseudo status

    private String value;

    /**
     * Constructor that takes the reason value.
     * @param value The value for the reason
     */
    ArchiveReason(String value)
    {
        this.value = value;
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
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static ArchiveReason fromValue(String value)
    {
        ArchiveReason[] types = values();
        for(ArchiveReason type : types)
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
     * Returns a list of the archive reasons.
     */
    public static List<ArchiveReason> toList()
    {
        List<ArchiveReason> ret = new ArrayList<ArchiveReason>();

        ret.add(NONE);
        ret.add(REJECTED);
        ret.add(DISAPPROVED);
        ret.add(DISCONTINUED);
        ret.add(DEFUNCT);
        ret.add(MIGRATED);

        return ret;
    }
}