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

package com.opsmatters.media.model.monitor;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the reason for a content monitor failure.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum FailureReason
{
    UNDEFINED("Undefined"),
    INTERMITTENT("Intermittent"),
    ACCESS_DENIED("Access Denied"),
    VERIFICATION("Verification"),
    DEFECTIVE("Defective"),
    HANGING("Hanging"),
    ALL("All"); // Pseudo status

    private String value;

    /**
     * Constructor that takes the reason value.
     * @param value The value for the reason
     */
    FailureReason(String value)
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
    public static FailureReason fromValue(String value)
    {
        FailureReason[] types = values();
        for(FailureReason type : types)
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
     * Returns a list of the failure reasons.
     */
    public static List<FailureReason> toList()
    {
        List<FailureReason> ret = new ArrayList<FailureReason>();

        ret.add(UNDEFINED);
        ret.add(INTERMITTENT);
        ret.add(ACCESS_DENIED);
        ret.add(VERIFICATION);
        ret.add(DEFECTIVE);
        ret.add(HANGING);

        return ret;
    }
}