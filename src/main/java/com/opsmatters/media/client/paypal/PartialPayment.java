package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the partial payment object for a PayPal invoice.
 */
public class PartialPayment extends JSONObject
{
    private static final String ALLOW_PARTIAL_PAYMENT = "allow_partial_payment";
    private static final String MINIMUM_AMOUNT_DUE = "minimum_amount_due";

    private MinimumAmountDue minimumAmountDue;

    /**
     * Default constructor.
     */
    public PartialPayment() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PartialPayment(JSONObject obj) 
    {
        if(obj.has(ALLOW_PARTIAL_PAYMENT))
            setAllowPartialPayment(obj.optBoolean(ALLOW_PARTIAL_PAYMENT));

        if(obj.has(MINIMUM_AMOUNT_DUE))
            setMinimumAmountDue(new MinimumAmountDue(obj.getJSONObject(MINIMUM_AMOUNT_DUE)));
    }

    /**
     * Returns the "minimum_amount_due" object.
     */
    public MinimumAmountDue getMinimumAmountDue()
    {
        if(minimumAmountDue == null)
            setMinimumAmountDue(new MinimumAmountDue());
        return minimumAmountDue;
    }

    /**
     * Sets the "minimum_amount_due" object for the invoice.
     */
    public void setMinimumAmountDue(MinimumAmountDue minimumAmountDue)
    {
        this.minimumAmountDue = minimumAmountDue;
        put(MINIMUM_AMOUNT_DUE, minimumAmountDue);
    }

    /**
     * Returns the allow partial payment flag
     */
    public boolean getAllowPartialPayment() 
    {
        return optBoolean(ALLOW_PARTIAL_PAYMENT);
    }

    /**
     * Sets the allow partial payment flag.
     */
    public void setAllowPartialPayment(boolean allowPartialPayment) 
    {
        put(ALLOW_PARTIAL_PAYMENT, allowPartialPayment);
    }
}