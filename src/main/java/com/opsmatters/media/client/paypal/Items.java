package com.opsmatters.media.client.paypal;

import org.json.JSONArray;

/**
 * Represents the items object for a PayPal invoice.
 */
public class Items extends JSONArray
{
    /**
     * Default constructor.
     */
    public Items() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Items(JSONArray obj) 
    {
        for(int i = 0; i < obj.length(); i++)
            put(new Item(obj.getJSONObject(i)));
    }
}