package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the tax object for a PayPal invoice.
 */
public class Tax extends JSONObject
{
    private static final String NAME = "name";
    private static final String PERCENT = "percent";

    /**
     * Default constructor.
     */
    public Tax() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Tax(JSONObject obj) 
    {
        if(obj.has(NAME))
            setName(obj.optString(NAME));
        if(obj.has(PERCENT))
            setPercent(obj.optString(PERCENT));
    }

    /**
     * Returns the name.
     */
    public String getName() 
    {
        return optString(NAME);
    }

    /**
     * Sets the name.
     */
    public void setName(String name) 
    {
        put(NAME, name);
    }

    /**
     * Returns the percent.
     */
    public String getPercent() 
    {
        return optString(PERCENT);
    }

    /**
     * Sets the percent.
     */
    public void setPercent(String percent) 
    {
        put(PERCENT, percent);
    }
}