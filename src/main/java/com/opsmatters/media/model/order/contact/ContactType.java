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

package com.opsmatters.media.model.order.contact;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the contact type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum ContactType
{
    INDIVIDUAL("Individual"),
    COMPANY("Company"),
    PLATFORM("Platform"),
    ALL("All"); // Pseudo status

    private String value;

    /**
     * Constructor that takes the type value.
     * @param value The value for the type
     */
    ContactType(String value)
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
     * Returns the value of the type.
     * @return The value of the type.
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
    public static ContactType fromValue(String value)
    {
        ContactType[] types = values();
        for(ContactType type : types)
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
     * Returns a list of the contact types.
     */
    public static List<ContactType> toList()
    {
        List<ContactType> ret = new ArrayList<ContactType>();

        ret.add(INDIVIDUAL);
        ret.add(COMPANY);
        ret.add(PLATFORM);
 
        return ret;
    }
}