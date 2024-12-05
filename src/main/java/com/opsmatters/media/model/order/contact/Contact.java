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
package com.opsmatters.media.model.order.contact;

import java.time.Instant;
import com.opsmatters.media.cache.admin.Parameters;
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.model.order.Currency;
import com.opsmatters.media.model.order.PaymentMethod;
import com.opsmatters.media.model.order.PaymentMode;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.admin.ParameterType.*;
import static com.opsmatters.media.model.admin.ParameterName.*;

/**
 * Class representing a contact.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Contact extends OwnedEntity
{
    private String name = "";
    private ContactType type = ContactType.INDIVIDUAL;
    private String contactEmail = "";
    private String billingEmail = "";
    private String companyId = "";
    private String website = "";
    private String notes = "";
    private PaymentMethod method = PaymentMethod.UNDEFINED;
    private PaymentMode mode = PaymentMode.UNDEFINED;
    private Currency currency = Currency.UNDEFINED;
    private ContactStatus status = ContactStatus.NEW;
    private SuspendReason reason = SuspendReason.NONE;

    /**
     * Default constructor.
     */
    public Contact()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Contact(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);

        setPaymentMethod(Parameters.get(ORDER, PAYMENT_METHOD).getValue());
        setPaymentMode(Parameters.get(ORDER, PAYMENT_MODE).getValue());
        setCurrency(Parameters.get(ORDER, CURRENCY).getValue());
    }

    /**
     * Copy constructor.
     */
    public Contact(Contact obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Contact obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setType(obj.getType());
            setContactEmail(obj.getContactEmail());
            setBillingEmail(obj.getBillingEmail());
            setCompanyId(obj.getCompanyId());
            setWebsite(obj.getWebsite());
            setNotes(obj.getNotes());
            setPaymentMethod(obj.getPaymentMethod());
            setPaymentMode(obj.getPaymentMode());
            setCurrency(obj.getCurrency());
            setStatus(obj.getStatus());
            setReason(obj.getReason());
        }
    }

    /**
     * Returns the contact name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the contact name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the contact name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the type.
     */
    public ContactType getType()
    {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(String type)
    {
        setType(ContactType.valueOf(type));
    }

    /**
     * Sets the type.
     */
    public void setType(ContactType type)
    {
        this.type = type;
    }

    /**
     * Returns the contact email.
     */
    public String getContactEmail()
    {
        return contactEmail;
    }

    /**
     * Sets the contact email.
     */
    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    /**
     * Returns <CODE>true</CODE> if the contact email has been set.
     */
    public boolean hasContactEmail()
    {
        return getContactEmail() != null && getContactEmail().length() > 0;
    }

    /**
     * Returns the billing email.
     */
    public String getBillingEmail()
    {
        return billingEmail;
    }

    /**
     * Sets the billing email.
     */
    public void setBillingEmail(String billingEmail)
    {
        this.billingEmail = billingEmail;
    }

    /**
     * Returns <CODE>true</CODE> if the billing email has been set.
     */
    public boolean hasBillingEmail()
    {
        return getBillingEmail() != null && getBillingEmail().length() > 0;
    }

    /**
     * Returns the company id.
     */
    public String getCompanyId()
    {
        return companyId;
    }

    /**
     * Sets the company id.
     */
    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    /**
     * Returns <CODE>true</CODE> if the company id has been set.
     */
    public boolean hasCompanyId()
    {
        return getCompanyId() != null && getCompanyId().length() > 0;
    }

    /**
     * Returns the contact website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the contact website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Returns <CODE>true</CODE> if the contact website has been set.
     */
    public boolean hasWebsite()
    {
        return getWebsite() != null && getWebsite().length() > 0;
    }

    /**
     * Returns the notes.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the notes.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Returns the payment method.
     */
    public PaymentMethod getPaymentMethod()
    {
        return method;
    }

    /**
     * Sets the payment method.
     */
    public void setPaymentMethod(String method)
    {
        setPaymentMethod(PaymentMethod.valueOf(method));
    }

    /**
     * Sets the payment method.
     */
    public void setPaymentMethod(PaymentMethod method)
    {
        this.method = method;
    }

    /**
     * Returns the payment mode.
     */
    public PaymentMode getPaymentMode()
    {
        return mode;
    }

    /**
     * Sets the payment mode.
     */
    public void setPaymentMode(String mode)
    {
        setPaymentMode(PaymentMode.valueOf(mode));
    }

    /**
     * Sets the payment mode.
     */
    public void setPaymentMode(PaymentMode mode)
    {
        this.mode = mode;
    }

    /**
     * Returns the payment currency.
     */
    public Currency getCurrency()
    {
        return currency;
    }

    /**
     * Sets the payment currency.
     */
    public void setCurrency(String currency)
    {
        setCurrency(Currency.fromCode(currency));
    }

    /**
     * Sets the payment currency.
     */
    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    /**
     * Returns the contact status.
     */
    public ContactStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the contact status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == ContactStatus.ACTIVE;
    }

    /**
     * Sets the contact status.
     */
    public void setStatus(String status)
    {
        setStatus(ContactStatus.valueOf(status));
    }

    /**
     * Sets the contact status.
     */
    public void setStatus(ContactStatus status)
    {
        this.status = status;
    }

    /**
     * Returns the suspension reason.
     */
    public SuspendReason getReason()
    {
        return reason;
    }

    /**
     * Sets the suspension reason.
     */
    public void setReason(String reason)
    {
        setReason(SuspendReason.valueOf(reason));
    }

    /**
     * Sets the suspension reason.
     */
    public void setReason(SuspendReason reason)
    {
        this.reason = reason;
    }
}