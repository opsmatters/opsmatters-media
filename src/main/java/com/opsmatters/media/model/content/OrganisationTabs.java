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

package com.opsmatters.media.model.content;

/**
 * Represents a set of organisation tabs.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum OrganisationTabs
{
    NONE(-1),
    ALL(0),
    N(1),
    V(2),
    NV(10),
    NE(20),
    NP(30),
    NVP(40),
    NVE(50),
    NPE(60);

    private int value;

    /**
     * Constructor that takes the tabs value.
     * @param value The value for the tabs
     */
    OrganisationTabs(int value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the tabs.
     * @return The value of the tabs.
     */
    public int value()
    {
        return value;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static OrganisationTabs fromValue(int value)
    {
        OrganisationTabs[] types = values();
        for(OrganisationTabs type : types)
        {
            if(type.value() == value)
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