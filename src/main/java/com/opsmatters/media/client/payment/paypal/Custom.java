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
 * Represents the custom object for a PayPal invoice.
 */
public class Custom extends JSONObject
{
    private static final String LABEL = "label";
    private static final String AMOUNT = "amount";

    private Amount amount;

    /**
     * Default constructor.
     */
    public Custom() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Custom(JSONObject obj) 
    {
        if(obj.has(LABEL))
            setLabel(obj.optString(LABEL));

        if(obj.has(AMOUNT))
            setAmount(new Amount(obj.getJSONObject(AMOUNT)));
    }

    /**
     * Returns the "amount" object.
     */
    public Amount getAmount()
    {
        if(amount == null)
            setAmount(new Amount());
        return amount;
    }

    /**
     * Sets the "amount" object for the invoice.
     */
    public void setAmount(Amount amount)
    {
        this.amount = amount;
        put(AMOUNT, amount);
    }

    /**
     * Returns the label.
     */
    public String getLabel() 
    {
        return optString(LABEL);
    }

    /**
     * Sets the label.
     */
    public void setLabel(String label) 
    {
        put(LABEL, label);
    }
}