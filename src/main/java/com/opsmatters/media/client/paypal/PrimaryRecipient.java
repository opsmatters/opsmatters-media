package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the primary recipient object for a PayPal invoice.
 */
public class PrimaryRecipient extends JSONObject
{
    private static final String BILLING_INFO = "billing_info";
    private static final String SHIPPING_INFO = "shipping_info";

    private BillingInfo billingInfo;
    private ShippingInfo shippingInfo;

    /**
     * Default constructor.
     */
    public PrimaryRecipient() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PrimaryRecipient(JSONObject obj) 
    {
        if(obj.has(BILLING_INFO))
            setBillingInfo(new BillingInfo(obj.getJSONObject(BILLING_INFO)));
        if(obj.has(SHIPPING_INFO))
            setShippingInfo(new ShippingInfo(obj.getJSONObject(SHIPPING_INFO)));
    }

    /**
     * Returns the "billing_info" object.
     */
    public BillingInfo getBillingInfo()
    {
        if(billingInfo == null)
            setBillingInfo(new BillingInfo());
        return billingInfo;
    }

    /**
     * Sets the "billing_info" object for the invoice.
     */
    public void setBillingInfo(BillingInfo billingInfo)
    {
        this.billingInfo = billingInfo;
        put(BILLING_INFO, billingInfo);
    }

    /**
     * Returns the "shipping_info" object.
     */
    public ShippingInfo getShippingInfo()
    {
        if(shippingInfo == null)
            setShippingInfo(new ShippingInfo());
        return shippingInfo;
    }

    /**
     * Sets the "shipping_info" object for the invoice.
     */
    public void setShippingInfo(ShippingInfo shippingInfo)
    {
        this.shippingInfo = shippingInfo;
        put(SHIPPING_INFO, shippingInfo);
    }
}