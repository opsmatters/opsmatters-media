package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the discount object for a PayPal invoice.
 */
public class Discount extends JSONObject
{
    private static final String PERCENT = "percent";
    private static final String INVOICE_DISCOUNT = "invoice_discount";

    private InvoiceDiscount invoiceDiscount;

    /**
     * Default constructor.
     */
    public Discount() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Discount(JSONObject obj) 
    {
        if(obj.has(PERCENT))
            setPercent(obj.optString(PERCENT));

        if(obj.has(INVOICE_DISCOUNT))
            setInvoiceDiscount(new InvoiceDiscount(obj.getJSONObject(INVOICE_DISCOUNT)));
    }

    /**
     * Returns the "invoice_discount" object.
     */
    public InvoiceDiscount getInvoiceDiscount()
    {
        if(invoiceDiscount == null)
            setInvoiceDiscount(new InvoiceDiscount());
        return invoiceDiscount;
    }

    /**
     * Sets the "invoice_discount" object for the invoice.
     */
    public void setInvoiceDiscount(InvoiceDiscount invoiceDiscount)
    {
        this.invoiceDiscount = invoiceDiscount;
        put(INVOICE_DISCOUNT, invoiceDiscount);
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