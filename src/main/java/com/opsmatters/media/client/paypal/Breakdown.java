package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the breakdown object for a PayPal invoice.
 */
public class Breakdown extends JSONObject
{
    private static final String CUSTOM = "custom";
    private static final String SHIPPING = "shipping";
    private static final String DISCOUNT = "discount";

    private Custom custom;
    private Shipping shipping;
    private Discount discount;

    /**
     * Default constructor.
     */
    public Breakdown() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Breakdown(JSONObject obj) 
    {
        if(obj.has(CUSTOM))
            setCustom(new Custom(obj.getJSONObject(CUSTOM)));
        if(obj.has(SHIPPING))
            setShipping(new Shipping(obj.getJSONObject(SHIPPING)));
        if(obj.has(DISCOUNT))
            setDiscount(new Discount(obj.getJSONObject(DISCOUNT)));
    }

    /**
     * Returns the "custom" object.
     */
    public Custom getCustom()
    {
        if(custom == null)
            setCustom(new Custom());
        return custom;
    }

    /**
     * Sets the "custom" object for the invoice.
     */
    public void setCustom(Custom custom)
    {
        this.custom = custom;
        put(CUSTOM, custom);
    }

    /**
     * Returns the "shipping" object.
     */
    public Shipping getShipping()
    {
        if(shipping == null)
            setShipping(new Shipping());
        return shipping;
    }

    /**
     * Sets the "shipping" object for the invoice.
     */
    public void setShipping(Shipping shipping)
    {
        this.shipping = shipping;
        put(SHIPPING, shipping);
    }

    /**
     * Returns the "discount" object.
     */
    public Discount getDiscount()
    {
        if(discount == null)
            setDiscount(new Discount());
        return discount;
    }

    /**
     * Sets the "discount" object for the invoice.
     */
    public void setDiscount(Discount discount)
    {
        this.discount = discount;
        put(DISCOUNT, discount);
    }
}