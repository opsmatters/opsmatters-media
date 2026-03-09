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
 * Represents the phone object for a PayPal invoice.
 */
public class Phone extends JSONObject
{
    private static final String COUNTRY_CODE = "country_code";
    private static final String NATIONAL_NUMBER = "national_number";
    private static final String PHONE_TYPE = "phone_type";

    /**
     * Default constructor.
     */
    public Phone() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Phone(JSONObject obj) 
    {
        if(obj.has(COUNTRY_CODE))
            setCountryCode(obj.optString(COUNTRY_CODE));
        if(obj.has(NATIONAL_NUMBER))
            setNationalNumber(obj.optString(NATIONAL_NUMBER));
        if(obj.has(PHONE_TYPE))
            setPhoneType(obj.optString(PHONE_TYPE));
    }

    /**
     * Returns the country code.
     */
    public String getCountryCode() 
    {
        return optString(COUNTRY_CODE);
    }

    /**
     * Sets the country code.
     */
    public void setCountryCode(String countryCode) 
    {
        put(COUNTRY_CODE, countryCode);
    }

    /**
     * Returns the national number.
     */
    public String getNationalNumber() 
    {
        return optString(NATIONAL_NUMBER);
    }

    /**
     * Sets the national number.
     */
    public void setNationalNumber(String nationalNumber) 
    {
        put(NATIONAL_NUMBER, nationalNumber);
    }

    /**
     * Returns the phone type.
     */
    public String getPhoneType() 
    {
        return optString(PHONE_TYPE);
    }

    /**
     * Sets the phone type.
     */
    public void setPhoneType(String phoneType) 
    {
        put(PHONE_TYPE, phoneType);
    }
}