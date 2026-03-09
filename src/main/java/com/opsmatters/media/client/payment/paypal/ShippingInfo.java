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
 * Represents the shipping info object for a PayPal invoice.
 */
public class ShippingInfo extends JSONObject
{
    private static final String NAME = "name";
    private static final String BUSINESS_NAME = "business_name";
    private static final String ADDRESS = "address";

    private Name name;
    private Address address;

    /**
     * Default constructor.
     */
    public ShippingInfo() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public ShippingInfo(JSONObject obj) 
    {
        if(obj.has(BUSINESS_NAME))
            setBusinessName(obj.optString(BUSINESS_NAME));

        if(obj.has(NAME))
            setName(new Name(obj.getJSONObject(NAME)));
        if(obj.has(ADDRESS))
            setAddress(new Address(obj.getJSONObject(ADDRESS)));
    }

    /**
     * Returns the "name" object.
     */
    public Name getName()
    {
        if(name == null)
            setName(new Name());
        return name;
    }

    /**
     * Sets the "name" object for the invoice.
     */
    public void setName(Name name)
    {
        this.name = name;
        put(NAME, name);
    }

    /**
     * Returns the "address" object.
     */
    public Address getAddress()
    {
        if(address == null)
            setAddress(new Address());
        return address;
    }

    /**
     * Sets the "address" object for the invoice.
     */
    public void setAddress(Address address)
    {
        this.address = address;
        put(ADDRESS, address);
    }

    /**
     * Returns the business name.
     */
    public String getBusinessName() 
    {
        return optString(BUSINESS_NAME);
    }

    /**
     * Sets the business name.
     */
    public void setBusinessName(String businessName) 
    {
        put(BUSINESS_NAME, businessName);
    }
}