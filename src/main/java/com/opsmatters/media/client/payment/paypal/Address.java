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
 * Represents the address object for a PayPal invoice.
 */
public class Address extends JSONObject
{
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String ADMIN_AREA_1 = "admin_area_1";
    private static final String ADMIN_AREA_2 = "admin_area_2";
    private static final String POSTAL_CODE = "postal_code";
    private static final String COUNTRY_CODE = "country_code";

    /**
     * Default constructor.
     */
    public Address() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Address(JSONObject obj) 
    {
        if(obj.has(ADDRESS_LINE_1))
            setAddressLine1(obj.optString(ADDRESS_LINE_1));
        if(obj.has(ADDRESS_LINE_2))
            setAddressLine2(obj.optString(ADDRESS_LINE_2));
        if(obj.has(ADMIN_AREA_1))
            setAdminArea1(obj.optString(ADMIN_AREA_1));
        if(obj.has(ADMIN_AREA_2))
            setAdminArea2(obj.optString(ADMIN_AREA_2));
        if(obj.has(POSTAL_CODE))
            setPostalCode(obj.optString(POSTAL_CODE));
        if(obj.has(COUNTRY_CODE))
            setCountryCode(obj.optString(COUNTRY_CODE));
    }

    /**
     * Returns the address line 1.
     */
    public String getAddressLine1() 
    {
        return optString(ADDRESS_LINE_1);
    }

    /**
     * Sets the address line 1.
     */
    public void setAddressLine1(String addressLine1) 
    {
        put(ADDRESS_LINE_1, addressLine1);
    }

    /**
     * Returns the address line 2.
     */
    public String getAddressLine2() 
    {
        return optString(ADDRESS_LINE_2);
    }

    /**
     * Sets the address line 2.
     */
    public void setAddressLine2(String addressLine2) 
    {
        put(ADDRESS_LINE_2, addressLine2);
    }

    /**
     * Returns the admin area 1.
     */
    public String getAdminArea1() 
    {
        return optString(ADMIN_AREA_1);
    }

    /**
     * Sets the admin area 1.
     */
    public void setAdminArea1(String adminArea1) 
    {
        put(ADMIN_AREA_1, adminArea1);
    }

    /**
     * Returns the admin area 2.
     */
    public String getAdminArea2() 
    {
        return optString(ADMIN_AREA_2);
    }

    /**
     * Sets the admin area 2.
     */
    public void setAdminArea2(String adminArea2) 
    {
        put(ADMIN_AREA_2, adminArea2);
    }

    /**
     * Returns the postal code.
     */
    public String getPostalCode() 
    {
        return optString(POSTAL_CODE);
    }

    /**
     * Sets the postal code.
     */
    public void setPostalCode(String postalCode) 
    {
        put(POSTAL_CODE, postalCode);
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
}