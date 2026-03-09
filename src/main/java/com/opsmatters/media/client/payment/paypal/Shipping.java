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
 * Represents the shipping object for a PayPal invoice.
 */
public class Shipping extends JSONObject
{
    private static final String AMOUNT = "amount";
    private static final String TAX = "tax";

    private Amount amount;
    private Tax tax;

    /**
     * Default constructor.
     */
    public Shipping() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Shipping(JSONObject obj) 
    {
        if(obj.has(AMOUNT))
            setAmount(new Amount(obj.getJSONObject(AMOUNT)));
        if(obj.has(TAX))
            setTax(new Tax(obj.getJSONObject(TAX)));
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
     * Returns the "tax" object.
     */
    public Tax getTax()
    {
        if(tax == null)
            setTax(new Tax());
        return tax;
    }

    /**
     * Sets the "tax" object for the invoice.
     */
    public void setTax(Tax tax)
    {
        this.tax = tax;
        put(TAX, tax);
    }
}