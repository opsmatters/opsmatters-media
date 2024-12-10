package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the shipping object for a PayPal invoice.
 */
public class Shipping extends JSONObject
{
    private static final String AMOUNT = "amount";
    private static final String TAX = "tax";

    private Amount amount;
    private Tax tax;

    /**
     * Default constructor.
     */
    public Shipping() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Shipping(JSONObject obj) 
    {
        if(obj.has(AMOUNT))
            setAmount(new Amount(obj.getJSONObject(AMOUNT)));
        if(obj.has(TAX))
            setTax(new Tax(obj.getJSONObject(TAX)));
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
     * Returns the "tax" object.
     */
    public Tax getTax()
    {
        if(tax == null)
            setTax(new Tax());
        return tax;
    }

    /**
     * Sets the "tax" object for the invoice.
     */
    public void setTax(Tax tax)
    {
        this.tax = tax;
        put(TAX, tax);
    }
}