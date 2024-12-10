package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the invoice discount object for a PayPal invoice.
 */
public class InvoiceDiscount extends JSONObject
{
    private static final String PERCENT = "percent";

    /**
     * Default constructor.
     */
    public InvoiceDiscount() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public InvoiceDiscount(JSONObject obj) 
    {
        if(obj.has(PERCENT))
            setPercent(obj.optString(PERCENT));
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