package com.opsmatters.media.client.paypal;

import org.json.JSONObject;
import com.opsmatters.media.util.TimeUtils;

/**
 * Represents a payment used with PayPal.
 */
public class PayPalPayment extends JSONObject
{
    private static final String METHOD = "method";
    private static final String PAYMENT_DATE = "payment_date";
    private static final String NOTE = "note";
    private static final String AMOUNT = "amount";

    private Amount amount;

    /**
     * Default constructor.
     */
    public PayPalPayment() 
    {
        setPaymentDate(TimeUtils.toStringUTC("yyyy-MM-dd"));
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public PayPalPayment(JSONObject obj) 
    {
        if(obj.has(METHOD))
            setMethod(obj.optString(METHOD));
        if(obj.has(PAYMENT_DATE))
            setPaymentDate(obj.optString(PAYMENT_DATE));
        if(obj.has(NOTE))
            setNote(obj.optString(NOTE));

        if(obj.has(AMOUNT))
            setAmount(new Amount(obj.getJSONObject(AMOUNT)));
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PayPalPayment obj)
    {
        if(obj != null)
        {
            setMethod(obj.getMethod());
            setPaymentDate(obj.getPaymentDate());
            setNote(obj.getNote());
            setAmount(obj.getAmount());
        }
    }

    /**
     * Returns the "amount" object for the invoice.
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
     * Returns the payment method.
     */
    public String getMethod() 
    {
        return optString(METHOD);
    }

    /**
     * Sets the payment method.
     */
    public void setMethod(String method) 
    {
        put(METHOD, method);
    }

    /**
     * Returns the payment date.
     */
    public String getPaymentDate() 
    {
        return optString(PAYMENT_DATE);
    }

    /**
     * Sets the payment date.
     */
    public void setPaymentDate(String paymentDate) 
    {
        put(PAYMENT_DATE, paymentDate);
    }

    /**
     * Returns the payment note.
     */
    public String getNote() 
    {
        return optString(NOTE);
    }

    /**
     * Sets the payment note.
     */
    public void setNote(String note) 
    {
        put(NOTE, note);
    }
}