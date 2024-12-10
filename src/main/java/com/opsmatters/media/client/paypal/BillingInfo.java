package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the billing info object for a PayPal invoice.
 */
public class BillingInfo extends JSONObject
{
    private static final String NAME = "name";
    private static final String BUSINESS_NAME = "business_name";
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String ADDRESS = "address";
    private static final String PHONES = "phones";
    private static final String ADDITIONAL_INFO_VALUE = "additional_info_value";

    private Name name;
    private Address address;
    private Phones phones;

    /**
     * Default constructor.
     */
    public BillingInfo() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public BillingInfo(JSONObject obj) 
    {
        if(obj.has(BUSINESS_NAME))
            setBusinessName(obj.optString(BUSINESS_NAME));
        if(obj.has(EMAIL_ADDRESS))
            setEmailAddress(obj.optString(EMAIL_ADDRESS));
        if(obj.has(ADDITIONAL_INFO_VALUE))
            setAdditionalInfoValue(obj.optString(ADDITIONAL_INFO_VALUE));

        if(obj.has(NAME))
            setName(new Name(obj.getJSONObject(NAME)));
        if(obj.has(ADDRESS))
            setAddress(new Address(obj.getJSONObject(ADDRESS)));
        if(obj.has(PHONES))
            setPhones(new Phones(obj.getJSONArray(PHONES)));
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
     * Returns the "phones" object.
     */
    public Phones getPhones()
    {
        if(phones == null)
            setPhones(new Phones());
        return phones;
    }

    /**
     * Sets the "phones" object for the invoice.
     */
    public void setPhones(Phones phones)
    {
        this.phones = phones;
        put(PHONES, phones);
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

    /**
     * Returns the email address
     */
    public String getEmailAddress() 
    {
        return optString(EMAIL_ADDRESS);
    }

    /**
     * Sets the email address.
     */
    public void setEmailAddress(String emailAddress) 
    {
        put(EMAIL_ADDRESS, emailAddress);
    }

    /**
     * Returns the additional info value.
     */
    public String getAdditionalInfoValue() 
    {
        return optString(ADDITIONAL_INFO_VALUE);
    }

    /**
     * Sets the additional info value.
     */
    public void setAdditionalInfoValue(String additionalInfoValue) 
    {
        put(ADDITIONAL_INFO_VALUE, additionalInfoValue);
    }
}