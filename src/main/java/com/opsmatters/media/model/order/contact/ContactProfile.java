/*
 * Copyright 2025 Gerald Curley
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
import com.opsmatters.media.cache.order.Currencies;
import com.opsmatters.media.cache.order.contact.Companies;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.order.Currency;
import com.opsmatters.media.model.order.PaymentMethod;
import com.opsmatters.media.model.order.PaymentMode;
import com.opsmatters.media.model.order.PaymentTerm;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.admin.ParameterType.*;
import static com.opsmatters.media.model.admin.ParameterName.*;

/**
 * Class representing a contact profile.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactProfile extends BaseEntity
{
    public static final String DEFAULT = "Default";

    private String contactId = "";
    private String name = "";
    private String contactEmail = "";
    private String billingEmail = "";
    private String companyId = "";
    private PaymentMethod method = PaymentMethod.UNDEFINED;
    private PaymentMode mode = PaymentMode.UNDEFINED;
    private PaymentTerm term = PaymentTerm.UNDEFINED;
    private Currency currency = null;
    private boolean prePayment = false;
    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public ContactProfile()
    {
    }

    /**
     * Constructor that takes a contact.
     */
    public ContactProfile(Contact contact)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setContactId(contact.getId());
        setName(DEFAULT);

        setPaymentMethod(Parameters.get(ORDER, PAYMENT_METHOD).getValue());
        setPaymentMode(Parameters.get(ORDER, PAYMENT_MODE).getValue());
        setPaymentTerm(Parameters.get(ORDER, PAYMENT_TERM).getValue());
        setCurrency(Parameters.get(ORDER, CURRENCY).getValue());
        setEnabled(true);
    }

    /**
     * Copy constructor.
     */
    public ContactProfile(ContactProfile obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContactProfile obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setContactId(obj.getContactId());
            setName(obj.getName());
            setContactEmail(obj.getContactEmail());
            setBillingEmail(obj.getBillingEmail());
            setCompanyId(obj.getCompanyId());
            setPaymentMethod(obj.getPaymentMethod());
            setPaymentMode(obj.getPaymentMode());
            setPaymentTerm(obj.getPaymentTerm());
            setCurrency(obj.getCurrency());
            setPrePayment(obj.hasPrePayment());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the profile name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the contact id.
     */
    public String getContactId()
    {
        return contactId;
    }

    /**
     * Sets the contact id.
     */
    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    /**
     * Returns the profile name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the profile name.
     */
    public void setName(String name)
    {
        this.name = name;
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
     * Returns the company, billing or contact email.
     */
    public String getEmail()
    {
        String ret = null;

        Company company = getCompany();
        if(company != null)
        {
            if(company.hasBillingEmail())
                ret = company.getBillingEmail();
        }

        if(ret == null)
        {
            if(hasBillingEmail())
                ret = getBillingEmail();
        }

        if(ret == null)
        {
            ret = getContactEmail();
        }

        return ret;
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
     * Returns the company.
     */
    public Company getCompany()
    {
        return hasCompanyId() ? Companies.getById(getCompanyId()) : null;
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
     * Returns the payment term.
     */
    public PaymentTerm getPaymentTerm()
    {
        return term;
    }

    /**
     * Sets the payment term.
     */
    public void setPaymentTerm(String term)
    {
        setPaymentTerm(PaymentTerm.valueOf(term));
    }

    /**
     * Sets the payment term.
     */
    public void setPaymentTerm(PaymentTerm term)
    {
        this.term = term;
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
        setCurrency(Currencies.get(currency));
    }

    /**
     * Sets the payment currency.
     */
    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    /**
     * Returns <CODE>true</CODE> if pre-payment is enabled for orders for this profile.
     */
    public boolean hasPrePayment()
    {
        return prePayment;
    }

    /**
     * Returns <CODE>true</CODE> if pre-payment is enabled for orders for this profile.
     */
    public Boolean getPrePaymentObject()
    {
        return Boolean.valueOf(hasPrePayment());
    }

    /**
     * Set to <CODE>true</CODE> if pre-payment is enabled for orders for this profile.
     */
    public void setPrePayment(boolean prePayment)
    {
        this.prePayment = prePayment;
    }

    /**
     * Set to <CODE>true</CODE> if pre-payment is enabled for orders for this profile.
     */
    public void setPrePaymentObject(Boolean prePayment)
    {
        setPrePayment(prePayment != null && prePayment.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if the profile is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this profile is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the profile is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this profile is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }
}