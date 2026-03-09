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

package com.opsmatters.media.client.payment.paypal;

import org.json.JSONObject;

/**
 * Represents the discount object for a PayPal invoice.
 */
public class Discount extends JSONObject
{
    private static final String PERCENT = "percent";
    private static final String INVOICE_DISCOUNT = "invoice_discount";

    private InvoiceDiscount invoiceDiscount;

    /**
     * Default constructor.
     */
    public Discount() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Discount(JSONObject obj) 
    {
        if(obj.has(PERCENT))
            setPercent(obj.optString(PERCENT));

        if(obj.has(INVOICE_DISCOUNT))
            setInvoiceDiscount(new InvoiceDiscount(obj.getJSONObject(INVOICE_DISCOUNT)));
    }

    /**
     * Returns the "invoice_discount" object.
     */
    public InvoiceDiscount getInvoiceDiscount()
    {
        if(invoiceDiscount == null)
            setInvoiceDiscount(new InvoiceDiscount());
        return invoiceDiscount;
    }

    /**
     * Sets the "invoice_discount" object for the invoice.
     */
    public void setInvoiceDiscount(InvoiceDiscount invoiceDiscount)
    {
        this.invoiceDiscount = invoiceDiscount;
        put(INVOICE_DISCOUNT, invoiceDiscount);
    }

    /**
     * Returns the percent.
     */
    public String getPercent() 
    {
        return optString(PERCENT);
    }

    /**
     * Sets the percent.
     */
    public void setPercent(String percent) 
    {
        put(PERCENT, percent);
    }
}