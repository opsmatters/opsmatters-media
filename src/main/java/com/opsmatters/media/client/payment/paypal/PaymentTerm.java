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
 * Represents the payment term object for a PayPal invoice.
 */
public class PaymentTerm extends JSONObject
{
    private static final String TERM_TYPE = "term_type";
    private static final String DUE_DATE = "due_date";

    /**
     * Default constructor.
     */
    public PaymentTerm() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PaymentTerm(JSONObject obj) 
    {
        if(obj.has(TERM_TYPE))
            setTermType(obj.optString(TERM_TYPE));
        if(obj.has(DUE_DATE))
            setDueDate(obj.optString(DUE_DATE));
    }

    /**
     * Returns the term type.
     */
    public String getTermType() 
    {
        return optString(TERM_TYPE);
    }

    /**
     * Sets the term type.
     */
    public void setTermType(String termType) 
    {
        put(TERM_TYPE, termType);
    }

    /**
     * Returns the due date.
     */
    public String getDueDate() 
    {
        return optString(DUE_DATE);
    }

    /**
     * Sets the due date.
     */
    public void setDueDate(String dueDate) 
    {
        put(DUE_DATE, dueDate);
    }
}