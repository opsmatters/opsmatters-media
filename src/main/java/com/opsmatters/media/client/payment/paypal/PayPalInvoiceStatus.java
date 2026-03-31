/*
 * Copyright 2026 Gerald Curley
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

package com.opsmatters.media.client.payment.paypal;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.order.InvoiceStatus;

/**
 * Represents the status of a PayPal invoice.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum PayPalInvoiceStatus
{
    DRAFT("DRAFT", InvoiceStatus.DRAFT),
    SENT("SENT", InvoiceStatus.SENT),
    PAYMENT_PENDING("PAYMENT_PENDING", InvoiceStatus.PENDING),
    PAID("PAID", InvoiceStatus.PAID),
    PARTIALLY_PAID("PARTIALLY_PAID", InvoiceStatus.UNKNOWN), // Not supported
    MARKED_AS_PAID("MARKED_AS_PAID", InvoiceStatus.MARKED_AS_PAID),
    SCHEDULED("SCHEDULED", InvoiceStatus.SCHEDULED),
    CANCELLED("CANCELLED", InvoiceStatus.CANCELLED),
    REFUNDED("REFUNDED", InvoiceStatus.REFUNDED),
    PARTIALLY_REFUNDED("PARTIALLY_REFUNDED", InvoiceStatus.UNKNOWN), // Not supported
    MARKED_AS_REFUNDED("MARKED_AS_REFUNDED", InvoiceStatus.UNKNOWN), // Not supported
    UNPAID("UNPAID", InvoiceStatus.UNPAID);

    private String value;
    private InvoiceStatus status;

    /**
     * Constructor that takes the status value and invoice status.
     * @param value The value for the status
     * @param status The corresponding invoice status for the status
     */
    PayPalInvoiceStatus(String value, InvoiceStatus status)
    {
        this.value = value;
        this.status = status;
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
     * Returns the invoice status for the status.
     * @return The invoice status for the status.
     */
    public InvoiceStatus status()
    {
        return status;
    }

    /**
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static PayPalInvoiceStatus fromValue(String value)
    {
        PayPalInvoiceStatus[] types = values();
        for(PayPalInvoiceStatus type : types)
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
     * Returns a list of invoice statuses for PayPal.
     */
    public static List<InvoiceStatus> toList()
    {
        List<InvoiceStatus> ret = new ArrayList<InvoiceStatus>();
        for(PayPalInvoiceStatus status : values())
        {
            if(status.status() != InvoiceStatus.UNKNOWN)
                ret.add(status.status());
        }

        return ret;
    }
}