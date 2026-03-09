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
 * Represents the unit amount object for a PayPal invoice.
 */
public class UnitAmount extends JSONObject
{
    private static final String CURRENCY_CODE = "currency_code";
    private static final String VALUE = "value";

    /**
     * Default constructor.
     */
    public UnitAmount() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public UnitAmount(JSONObject obj) 
    {
        if(obj.has(CURRENCY_CODE))
            setCurrencyCode(obj.optString(CURRENCY_CODE));
        if(obj.has(VALUE))
            setValue(obj.optString(VALUE));
    }

    /**
     * Returns the currency code.
     */
    public String getCurrencyCode() 
    {
        return optString(CURRENCY_CODE);
    }

    /**
     * Sets the currency code.
     */
    public void setCurrencyCode(String currencyCode) 
    {
        put(CURRENCY_CODE, currencyCode);
    }

    /**
     * Returns the value.
     */
    public String getValue() 
    {
        return optString(VALUE);
    }

    /**
     * Sets the value.
     */
    public void setValue(String value) 
    {
        put(VALUE, value);
    }
}