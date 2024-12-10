package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the configuration object for a PayPal invoice.
 */
public class Configuration extends JSONObject
{
    private static final String PARTIAL_PAYMENT = "partial_payment";
    private static final String ALLOW_TIP = "allow_tip";
    private static final String TAX_CALCULATED_AFTER_DISCOUNT = "tax_calculated_after_discount";
    private static final String TAX_INCLUSIVE = "tax_inclusive";
    private static final String TEMPLATE_ID = "template_id";

    private PartialPayment partialPayment;

    /**
     * Default constructor.
     */
    public Configuration() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Configuration(JSONObject obj) 
    {
        if(obj.has(ALLOW_TIP))
            setAllowTip(obj.optBoolean(ALLOW_TIP));
        if(obj.has(TAX_CALCULATED_AFTER_DISCOUNT))
            setTaxCalculatedAfterDiscount(obj.optBoolean(TAX_CALCULATED_AFTER_DISCOUNT));
        if(obj.has(TAX_INCLUSIVE))
            setTaxInclusive(obj.optBoolean(TAX_INCLUSIVE));
        if(obj.has(TEMPLATE_ID))
            setTemplateId(obj.optString(TEMPLATE_ID));

        if(obj.has(PARTIAL_PAYMENT))
            setPartialPayment(new PartialPayment(obj.getJSONObject(PARTIAL_PAYMENT)));
    }

    /**
     * Returns the "partial_payment" object.
     */
    public PartialPayment getPartialPayment()
    {
        if(partialPayment == null)
            setPartialPayment(new PartialPayment());
        return partialPayment;
    }

    /**
     * Sets the "partial_payment" object for the invoice.
     */
    public void setPartialPayment(PartialPayment partialPayment)
    {
        this.partialPayment = partialPayment;
        put(PARTIAL_PAYMENT, partialPayment);
    }

    /**
     * Returns the allow tip flag
     */
    public boolean getAllowTip() 
    {
        return optBoolean(ALLOW_TIP);
    }

    /**
     * Sets the allow tip flag.
     */
    public void setAllowTip(boolean allowTip) 
    {
        put(ALLOW_TIP, allowTip);
    }

    /**
     * Returns the tax calculated after discount flag
     */
    public boolean getTaxCalculatedAfterDiscount() 
    {
        return optBoolean(TAX_CALCULATED_AFTER_DISCOUNT);
    }

    /**
     * Sets the tax calculated after discount flag.
     */
    public void setTaxCalculatedAfterDiscount(boolean taxCalculatedAfterDiscount) 
    {
        put(TAX_CALCULATED_AFTER_DISCOUNT, taxCalculatedAfterDiscount);
    }

    /**
     * Returns the tax inclusive flag
     */
    public boolean getTaxInclusive() 
    {
        return optBoolean(TAX_INCLUSIVE);
    }

    /**
     * Sets the tax inclusive flag.
     */
    public void setTaxInclusive(boolean taxInclusive) 
    {
        put(TAX_INCLUSIVE, taxInclusive);
    }

    /**
     * Returns the template id.
     */
    public String getTemplateId() 
    {
        return optString(TEMPLATE_ID);
    }

    /**
     * Sets the template id.
     */
    public void setTemplateId(String templateId) 
    {
        put(TEMPLATE_ID, templateId);
    }
}