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

package com.opsmatters.media.client.payment.payoneer;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.order.InvoiceStatus;

/**
 * Represents the status of a Payoneer payment.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum PayoneerPaymentStatus
{
    PENDING(9, InvoiceStatus.SENT),
    MANUAL_REVIEW(1, InvoiceStatus.PENDING),
    APPROVED(6, InvoiceStatus.PENDING),
    PROCESSING(2, InvoiceStatus.PENDING),
    DEPOSITED(4, InvoiceStatus.PAID),
    MARKED_AS_PAID(5, InvoiceStatus.MARKED_AS_PAID),
    CANCELLED(7, InvoiceStatus.CANCELLED),
    DECLINED(3, InvoiceStatus.DECLINED),
    DISMISSED(10, InvoiceStatus.DISMISSED),
    REVERSED(11, InvoiceStatus.REFUNDED),
    ERROR(8, InvoiceStatus.ERROR);

    private int value;
    private InvoiceStatus status;

    /**
     * Constructor that takes the status value and invoice status.
     * @param value The value for the status
     * @param status The corresponding invoice status for the status
     */
    PayoneerPaymentStatus(int value, InvoiceStatus status)
    {
        this.value = value;
        this.status = status;
    }

    /**
     * Returns the value of the status.
     * @return The value of the status.
     */
    public int value()
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
    public static PayoneerPaymentStatus fromValue(int value)
    {
        PayoneerPaymentStatus[] types = values();
        for(PayoneerPaymentStatus type : types)
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

    /**
     * Returns a list of invoice statuses for Payoneer.
     */
    public static List<InvoiceStatus> toList()
    {
        List<InvoiceStatus> ret = new ArrayList<InvoiceStatus>();
        for(PayoneerPaymentStatus status : values())
        {
            if(status.status() != InvoiceStatus.UNKNOWN)
                ret.add(status.status());
        }

        return ret;
    }
}