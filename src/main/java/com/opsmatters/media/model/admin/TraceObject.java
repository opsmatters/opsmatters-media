/*
 * Copyright 2018 Gerald Curley
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
 * Represents an object that can be traced.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum TraceObject
{
    NONE("none", "None"),
    NODES("nodes", "Nodes"),
    PAGES("pages", "Pages"); 

    private String code;
    private String value;

    /**
     * Constructor that takes the code and name.
     * @param code The code for the trace
     * @param value The value of the trace
     */
    TraceObject(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the trace.
     * @return The value of the trace.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the trace.
     * @return The code of the trace.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the trace.
     * @return The value of the trace.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns <CODE>true</code> if this is the NONE trace object.
     * @return <CODE>true</code> if this is the NONE trace object.
     */
    public boolean isNone()
    {
        return this == NONE;
    }

    /**
     * Returns <CODE>true</code> if this is the NODES trace object.
     * @return <CODE>true</code> if this is the NODES trace object.
     */
    public boolean isNodes()
    {
        return this == NODES;
    }

    /**
     * Returns <CODE>true</code> if this is the PAGES trace object.
     * @return <CODE>true</code> if this is the PAGES trace object.
     */
    public boolean isPages()
    {
        return this == PAGES;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static TraceObject fromCode(String code)
    {
        TraceObject[] types = values();
        for(TraceObject type : types)
        {
            if(type.code().equals(code))
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
     * Returns a list of the trace objects.
     */
    public static List<TraceObject> toList()
    {
        List<TraceObject> ret = new ArrayList<TraceObject>();

        ret.add(NONE);
        ret.add(NODES);
        ret.add(PAGES);

        return ret;
    }
}