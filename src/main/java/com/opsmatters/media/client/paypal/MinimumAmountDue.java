package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the minimum amount due object for a PayPal invoice.
 */
public class MinimumAmountDue extends JSONObject
{
    private static final String CURRENCY_CODE = "currency_code";
    private static final String VALUE = "value";

    /**
     * Default constructor.
     */
    public MinimumAmountDue() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public MinimumAmountDue(JSONObject obj) 
    {
        if(obj.has(CURRENCY_CODE))
            setCurrencyCode(obj.optString(CURRENCY_CODE));
        if(obj.has(VALUE))
            setValue(obj.optString(VALUE));
    }

    /**
     * Returns the currency code.
     */
    public String getCurrencyCode() 
    {
        return optString(CURRENCY_CODE);
    }

    /**
     * Sets the currency code.
     */
    public void setCurrencyCode(String currencyCode) 
    {
        put(CURRENCY_CODE, currencyCode);
    }

    /**
     * Returns the value.
     */
    public String getValue() 
    {
        return optString(VALUE);
    }

    /**
     * Sets the value.
     */
    public void setValue(String value) 
    {
        put(VALUE, value);
    }
}