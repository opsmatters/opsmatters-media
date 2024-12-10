package com.opsmatters.media.client.paypal;

import org.json.JSONArray;

/**
 * Represents the phone object for a PayPal invoice.
 */
public class Phones extends JSONArray
{
    /**
     * Default constructor.
     */
    public Phones() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Phones(JSONArray obj) 
    {
        for(int i = 0; i < obj.length(); i++)
            put(new Phone(obj.getJSONObject(i)));
    }
}