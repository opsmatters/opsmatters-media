package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the payment term object for a PayPal invoice.
 */
public class PaymentTerm extends JSONObject
{
    private static final String TERM_TYPE = "term_type";
    private static final String DUE_DATE = "due_date";

    /**
     * Default constructor.
     */
    public PaymentTerm() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PaymentTerm(JSONObject obj) 
    {
        if(obj.has(TERM_TYPE))
            setTermType(obj.optString(TERM_TYPE));
        if(obj.has(DUE_DATE))
            setDueDate(obj.optString(DUE_DATE));
    }

    /**
     * Returns the term type.
     */
    public String getTermType() 
    {
        return optString(TERM_TYPE);
    }

    /**
     * Sets the term type.
     */
    public void setTermType(String termType) 
    {
        put(TERM_TYPE, termType);
    }

    /**
     * Returns the due date.
     */
    public String getDueDate() 
    {
        return optString(DUE_DATE);
    }

    /**
     * Sets the due date.
     */
    public void setDueDate(String dueDate) 
    {
        put(DUE_DATE, dueDate);
    }
}