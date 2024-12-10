package com.opsmatters.media.client.paypal;

import org.json.JSONArray;

/**
 * Represents the primary recipients object for a PayPal invoice.
 */
public class PrimaryRecipients extends JSONArray
{
    /**
     * Default constructor.
     */
    public PrimaryRecipients() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PrimaryRecipients(JSONArray obj) 
    {
        for(int i = 0; i < obj.length(); i++)
            put(new PrimaryRecipient(obj.getJSONObject(i)));
    }
}