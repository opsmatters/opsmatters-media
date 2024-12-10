package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the custom object for a PayPal invoice.
 */
public class Custom extends JSONObject
{
    private static final String LABEL = "label";
    private static final String AMOUNT = "amount";

    private Amount amount;

    /**
     * Default constructor.
     */
    public Custom() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Custom(JSONObject obj) 
    {
        if(obj.has(LABEL))
            setLabel(obj.optString(LABEL));

        if(obj.has(AMOUNT))
            setAmount(new Amount(obj.getJSONObject(AMOUNT)));
    }

    /**
     * Returns the "amount" object.
     */
    public Amount getAmount()
    {
        if(amount == null)
            setAmount(new Amount());
        return amount;
    }

    /**
     * Sets the "amount" object for the invoice.
     */
    public void setAmount(Amount amount)
    {
        this.amount = amount;
        put(AMOUNT, amount);
    }

    /**
     * Returns the label.
     */
    public String getLabel() 
    {
        return optString(LABEL);
    }

    /**
     * Sets the label.
     */
    public void setLabel(String label) 
    {
        put(LABEL, label);
    }
}