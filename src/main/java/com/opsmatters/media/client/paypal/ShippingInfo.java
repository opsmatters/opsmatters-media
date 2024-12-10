package com.opsmatters.media.client.paypal;

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