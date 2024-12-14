/*
 * Copyright 2024 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opsmatters.media.model.order;

/**
 * Class representing an order invoice.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Invoice
{
    private String id = "";
    private String number = "";
    private String email = "";
    private String url = "";
    private String note = "";
    private Currency currency = Currency.UNDEFINED;
    private InvoiceStatus status = InvoiceStatus.NONE;

    /**
     * Default constructor.
     */
    public Invoice()
    {
    }

    /**
     * Copy constructor.
     */
    public Invoice(Invoice obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Invoice obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setNumber(obj.getNumber());
            setEmail(obj.getEmail());
            setUrl(obj.getUrl());
            setNote(obj.getNote());
            setCurrency(obj.getCurrency());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the invoice id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the invoice id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns <CODE>true</CODE> if the invoice id has been set.
     */
    public boolean hasId()
    {
        return getId() != null && getId().length() > 0;
    }

    /**
     * Returns the invoice number.
     */
    public String getNumber()
    {
        return number;
    }

    /**
     * Sets the invoice number.
     */
    public void setNumber(String number)
    {
        this.number = number;
    }

    /**
     * Returns the invoice email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the invoice email.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns the invoice URL.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the invoice URL.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the invoice URL has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }

    /**
     * Returns the invoice note.
     */
    public String getNote()
    {
        return note;
    }

    /**
     * Sets the invoice note.
     */
    public void setNote(String note)
    {
        this.note = note;
    }

    /**
     * Returns the invoice currency.
     */
    public Currency getCurrency()
    {
        return currency;
    }

    /**
     * Sets the invoice currency.
     */
    public void setCurrency(String currency)
    {
        setCurrency(Currency.fromCode(currency));
    }

    /**
     * Sets the invoice currency.
     */
    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    /**
     * Returns the invoice status.
     */
    public InvoiceStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the invoice status.
     */
    public void setStatus(String status)
    {
        setStatus(InvoiceStatus.valueOf(status));
    }

    /**
     * Sets the invoice status.
     */
    public void setStatus(InvoiceStatus status)
    {
        this.status = status;
    }
}