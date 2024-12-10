package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the invoicer object for a PayPal invoice.
 */
public class Invoicer extends JSONObject
{
    private static final String NAME = "name";
    private static final String BUSINESS_NAME = "business_name";
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String ADDRESS = "address";
    private static final String PHONES = "phones";
    private static final String WEBSITE = "website";
    private static final String TAX_ID = "tax_id";
    private static final String LOGO_URL = "logo_url";
    private static final String ADDITIONAL_NOTES = "additional_notes";

    private Name name;
    private Address address;
    private Phones phones;

    /**
     * Default constructor.
     */
    public Invoicer() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Invoicer(JSONObject obj) 
    {
        if(obj.has(BUSINESS_NAME))
            setBusinessName(obj.optString(BUSINESS_NAME));
        if(obj.has(EMAIL_ADDRESS))
            setEmailAddress(obj.optString(EMAIL_ADDRESS));
        if(obj.has(WEBSITE))
            setWebsite(obj.optString(WEBSITE));
        if(obj.has(TAX_ID))
            setTaxId(obj.optString(TAX_ID));
        if(obj.has(LOGO_URL))
            setLogoUrl(obj.optString(LOGO_URL));
        if(obj.has(ADDITIONAL_NOTES))
            setAdditionalNotes(obj.optString(ADDITIONAL_NOTES));

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
     * Returns the email address.
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
     * Returns the website.
     */
    public String getWebsite() 
    {
        return optString(WEBSITE);
    }

    /**
     * Sets the website.
     */
    public void setWebsite(String website) 
    {
        put(WEBSITE, website);
    }

    /**
     * Returns the tax id.
     */
    public String getTaxId() 
    {
        return optString(TAX_ID);
    }

    /**
     * Sets the tax id.
     */
    public void setTaxId(String taxId) 
    {
        put(TAX_ID, taxId);
    }

    /**
     * Returns the logo_url.
     */
    public String getLogoUrl() 
    {
        return optString(LOGO_URL);
    }

    /**
     * Sets the logo_url.
     */
    public void setLogoUrl(String logoUrl) 
    {
        put(LOGO_URL, logoUrl);
    }

    /**
     * Returns the additional notes.
     */
    public String getAdditionalNotes() 
    {
        return optString(ADDITIONAL_NOTES);
    }

    /**
     * Sets the additional notes.
     */
    public void setAdditionalNotes(String additionalNotes) 
    {
        put(ADDITIONAL_NOTES, additionalNotes);
    }
}