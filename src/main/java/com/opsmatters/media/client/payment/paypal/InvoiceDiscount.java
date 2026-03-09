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
 * Represents the invoice discount object for a PayPal invoice.
 */
public class InvoiceDiscount extends JSONObject
{
    private static final String PERCENT = "percent";

    /**
     * Default constructor.
     */
    public InvoiceDiscount() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public InvoiceDiscount(JSONObject obj) 
    {
        if(obj.has(PERCENT))
            setPercent(obj.optString(PERCENT));
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