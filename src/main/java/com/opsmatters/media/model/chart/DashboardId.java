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

package com.opsmatters.media.model.chart;

/**
 * Represents the standard dashboards.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum DashboardId
{
    DIRECTOR_STATUS("DIR-ST", "Director Status"),
    DIRECTOR_SUMMARY("DIR-SM", "Director Summary"),
    PUBLISHER_STATUS("PUB-ST", "Publisher Status"),
    PUBLISHER_SUMMARY("PUB-SM", "Publisher Summary"),
    ORDER_STATUS("ORD-ST", "Order Status");

    private String code;
    private String value;

    /**
     * Constructor that takes the dashboard code and value.
     * @param code The code for the dashboard
     * @param value The value for the dashboard
     */
    DashboardId(String code, String value)
    {
        this.code = code;
        this.value = value;
    }

    /**
     * Returns the value of the dashboard.
     * @return The value of the dashboard.
     */
    public String toString()
    {
        return value();
    }

    /**
     * Returns the code of the dashboard.
     * @return The code of the dashboard.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the value of the dashboard.
     * @return The value of the dashboard.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the dashboard name for the given value.
     * @param value The dashboard value
     * @return The dashboard name for the given value
     */
    public static DashboardId fromValue(String value)
    {
        DashboardId[] types = values();
        for(DashboardId type : types)
        {
            if(type.value().equals(value))
                return type;
        }

        return null;
    }

    /**
     * Returns the dashboard for the given code.
     * @param code The dashboard code
     * @return The dashboard for the given code
     */
    public static DashboardId fromCode(String code)
    {
        DashboardId[] types = values();
        for(DashboardId type : types)
        {
            if(type.code().equals(code))
                return type;
        }

        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given value is contained in the list of dashboard names.
     * @param value The dashboard value
     * @return <CODE>true</CODE> if the given value is contained in the list of dashboard names
     */
    public static boolean contains(String value)
    {
        return valueOf(value) != null;
    }
}