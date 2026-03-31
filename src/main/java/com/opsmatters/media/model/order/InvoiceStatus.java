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

import static com.opsmatters.media.model.order.PaymentMethod.*;

/**
 * Represents the status of an invoice.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum InvoiceStatus
{
    NONE("None", "glyphicon-unchecked", ""),
    NEW("New", "glyphicon-unchecked", ""),
    DRAFT("Draft", "glyphicon-edit", ""),
    SENT("Sent", "glyphicon-send", "status-warn"),
    PENDING("Pending", "glyphicon-hourglass", "status-warn"),
    PAID("Paid", "glyphicon-ok-circle", "status-success"),
    MARKED_AS_PAID("Marked As Paid", "glyphicon-ok-sign", "status-success"),
    SCHEDULED("Scheduled", "glyphicon-hourglass", "status-warn"),
    CANCELLED("Cancelled", "glyphicon-trash", "status-error"),
    REFUNDED("Refunded", "glyphicon-exclamation-sign", "status-info"),
    DECLINED("Declined", "glyphicon-trash", "status-error"),
    DISMISSED("Dismissed", "glyphicon-trash", "status-error"),
    UNPAID("Unpaid", "glyphicon-trash", "status-error"),
    ERROR("Error", "glyphicon-exclamation-sign", "status-error"),
    UNKNOWN("Unknown", "glyphicon-question-sign", "status-error"),
    ALL("All", "", ""); // Pseudo status

    private String value;
    private String icon;
    private String css;

    /**
     * Constructor that takes the status value, icon and css.
     * @param value The value for the status
     * @param icon The glyphicon for the status
     * @param css The css class for the status
     */
    InvoiceStatus(String value, String icon, String css)
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
    public static InvoiceStatus fromValue(String value)
    {
        InvoiceStatus[] types = values();
        for(InvoiceStatus type : types)
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
     * Returns a list of the invoice statuses for the given payment method.
     */
    public static List<InvoiceStatus> toList(PaymentMethod method)
    {
        List<InvoiceStatus> ret = new ArrayList<InvoiceStatus>();

        ret.add(NEW);

        for(InvoiceStatus status : method.statuses())
        {
            if(!ret.contains(status))
                ret.add(status);
        }

        return ret;
    }
}