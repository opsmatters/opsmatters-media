package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the name object for a PayPal invoice.
 */
public class Name extends JSONObject
{
    private static final String GIVEN_NAME = "given_name";
    private static final String SURNAME = "surname";

    /**
     * Default constructor.
     */
    public Name() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Name(JSONObject obj) 
    {
        if(obj.has(GIVEN_NAME))
            setGivenName(obj.optString(GIVEN_NAME));
        if(obj.has(SURNAME))
            setSurname(obj.optString(SURNAME));
    }

    /**
     * Returns the given name.
     */
    public String getGivenName() 
    {
        return optString(GIVEN_NAME);
    }

    /**
     * Sets the given name.
     */
    public void setGivenName(String givenName) 
    {
        put(GIVEN_NAME, givenName);
    }

    /**
     * Returns the surname.
     */
    public String getSurname() 
    {
        return optString(SURNAME);
    }

    /**
     * Sets the surname.
     */
    public void setSurname(String surname) 
    {
        put(SURNAME, surname);
    }
}