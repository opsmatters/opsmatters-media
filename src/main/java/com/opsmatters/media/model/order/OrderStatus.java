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

package com.opsmatters.media.model.order;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the status of an order.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum OrderStatus
{
    NEW("New", "glyphicon-unchecked", ""),
    APPROVED("Approved", "glyphicon-ok-circle", "status-info"),
    PENDING("Pending", "glyphicon-log-in", "status-warn"),
    COMPLETED("Completed", "glyphicon-ok-circle", "status-success"),
    CANCELLED("Cancelled", "glyphicon-trash", "status-error"),
    ARCHIVED("Archived", "glyphicon-trash", "status-error"),
    ALL("All", "", ""); // Pseudo status

    private String value;
    private String icon;
    private String css;

    /**
     * Constructor that takes the status value.
     * @param value The value for the status
     * @param icon The glyphicon for the status
     * @param css The css class for the status
     */
    OrderStatus(String value, String icon, String css)
    {
        this.value = value;
        this.icon = icon;
        this.css = css;
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
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static OrderStatus fromValue(String value)
    {
        OrderStatus[] types = values();
        for(OrderStatus type : types)
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
     * Returns a list of the order statuses.
     */
    public static List<OrderStatus> toList()
    {
        List<OrderStatus> ret = new ArrayList<OrderStatus>();

        ret.add(NEW);
        ret.add(PENDING);
        ret.add(COMPLETED);
        ret.add(CANCELLED);
        ret.add(ARCHIVED);

        return ret;
    }
}