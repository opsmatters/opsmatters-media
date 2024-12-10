package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the detail object for a PayPal invoice.
 */
public class Detail extends JSONObject
{
    private static final String CURRENCY_CODE = "currency_code";
    private static final String INVOICE_NUMBER = "invoice_number";
    private static final String REFERENCE = "reference";
    private static final String INVOICE_DATE = "invoice_date";
    private static final String NOTE = "note";
    private static final String TERM = "term";
    private static final String MEMO = "memo";
    private static final String PAYMENT_TERM = "payment_term";
    private static final String METADATA = "metadata";

    private PaymentTerm paymentTerm;
    private Metadata metadata;

    /**
     * Default constructor.
     */
    public Detail() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Detail(JSONObject obj) 
    {
        if(obj.has(CURRENCY_CODE))
            setCurrencyCode(obj.optString(CURRENCY_CODE));
        if(obj.has(INVOICE_NUMBER))
            setInvoiceNumber(obj.optString(INVOICE_NUMBER));
        if(obj.has(REFERENCE))
            setReference(obj.optString(REFERENCE));
        if(obj.has(INVOICE_DATE))
            setInvoiceDate(obj.optString(INVOICE_DATE));
        if(obj.has(NOTE))
            setNote(obj.optString(NOTE));
        if(obj.has(TERM))
            setTerm(obj.optString(TERM));
        if(obj.has(MEMO))
            setMemo(obj.optString(MEMO));

        if(obj.has(PAYMENT_TERM))
            setPaymentTerm(new PaymentTerm(obj.getJSONObject(PAYMENT_TERM)));
        if(obj.has(METADATA))
            setMetadata(new Metadata(obj.getJSONObject(METADATA)));
    }

    /**
     * Returns the "payment_term" object for the invoice.
     */
    public PaymentTerm getPaymentTerm()
    {
        if(paymentTerm == null)
            setPaymentTerm(new PaymentTerm());
        return paymentTerm;
    }

    /**
     * Sets the "payment_term" object for the invoice.
     */
    public void setPaymentTerm(PaymentTerm paymentTerm)
    {
        this.paymentTerm = paymentTerm;
        put(PAYMENT_TERM, paymentTerm);
    }

    /**
     * Returns the "metadata" object for the invoice.
     */
    public Metadata getMetadata()
    {
        if(metadata == null)
            setMetadata(new Metadata());
        return metadata;
    }

    /**
     * Sets the "metadata" object for the invoice.
     */
    public void setMetadata(Metadata metadata)
    {
        this.metadata = metadata;
        put(METADATA, metadata);
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
     * Returns the invoice number.
     */
    public String getInvoiceNumber() 
    {
        return optString(INVOICE_NUMBER);
    }

    /**
     * Sets the invoice number.
     */
    public void setInvoiceNumber(String invoiceNumber) 
    {
        put(INVOICE_NUMBER, invoiceNumber);
    }

    /**
     * Returns the reference.
     */
    public String getReference() 
    {
        return optString(REFERENCE);
    }

    /**
     * Sets the reference.
     */
    public void setReference(String reference) 
    {
        put(REFERENCE, reference);
    }

    /**
     * Returns the invoice date.
     */
    public String getInvoiceDate() 
    {
        return optString(INVOICE_DATE);
    }

    /**
     * Sets the invoice date.
     */
    public void setInvoiceDate(String invoiceDate) 
    {
        put(INVOICE_DATE, invoiceDate);
    }

    /**
     * Returns the note.
     */
    public String getNote() 
    {
        return optString(NOTE);
    }

    /**
     * Sets the note.
     */
    public void setNote(String note) 
    {
        put(NOTE, note);
    }

    /**
     * Returns the term.
     */
    public String getTerm() 
    {
        return optString(TERM);
    }

    /**
     * Sets the term.
     */
    public void setTerm(String term) 
    {
        put(TERM, term);
    }

    /**
     * Returns the memo.
     */
    public String getMemo() 
    {
        return optString(MEMO);
    }

    /**
     * Sets the memo.
     */
    public void setMemo(String memo) 
    {
        put(MEMO, memo);
    }
}