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

import java.util.Map;
import com.opsmatters.media.cache.admin.Parameters;
import com.opsmatters.media.model.admin.Parameter;
import com.opsmatters.media.model.admin.ParameterName;

import static com.opsmatters.media.model.admin.ParameterType.*;
import static com.opsmatters.media.model.admin.ParameterName.*;

/**
 * Class representing an order sender.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Sender
{
    private static Sender _instance;

    private String billingName = "";
    private String billingEmail = "";
    private String givenName = "";
    private String surname = "";
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String addressArea1 = "";
    private String addressArea2 = "";
    private String postalCode = "";
    private Country country = Country.UNDEFINED;
    private String phoneCode = "";
    private String phoneNumber = "";
    private String website = "";
    private String logoUrl = "";
    private String taxId = "";
    private String additionalNotes = "";

    /**
     * Instantiates the singleton instance.
     */
    public static Sender get()
    {
        if(_instance == null)
        {
            _instance = new Sender(Parameters.map(ORDER));
        }

        return _instance;
    }

    /**
     * Private constructor.
     */
    private Sender()
    {
    }

    /**
     * Constructor that takes a map of parameters.
     */
    public Sender(Map<ParameterName,Parameter> map)
    {
        if(map.containsKey(SENDER_GIVEN_NAME))
            setGivenName(map.get(SENDER_GIVEN_NAME).getValue());
        if(map.containsKey(SENDER_SURNAME))
            setSurname(map.get(SENDER_SURNAME).getValue());
        if(map.containsKey(SENDER_BILLING_NAME))
            setBillingName(map.get(SENDER_BILLING_NAME).getValue());
        if(map.containsKey(SENDER_BILLING_EMAIL))
            setBillingEmail(map.get(SENDER_BILLING_EMAIL).getValue());
        if(map.containsKey(SENDER_ADDRESS_LINE_1))
            setAddressLine1(map.get(SENDER_ADDRESS_LINE_1).getValue());
        if(map.containsKey(SENDER_ADDRESS_LINE_2))
            setAddressLine2(map.get(SENDER_ADDRESS_LINE_2).getValue());
        if(map.containsKey(SENDER_ADDRESS_AREA_1))
            setAddressArea1(map.get(SENDER_ADDRESS_AREA_1).getValue());
        if(map.containsKey(SENDER_ADDRESS_AREA_2))
            setAddressArea2(map.get(SENDER_ADDRESS_AREA_2).getValue());
        if(map.containsKey(SENDER_POSTAL_CODE))
            setPostalCode(map.get(SENDER_POSTAL_CODE).getValue());
        if(map.containsKey(SENDER_COUNTRY))
            setCountry(map.get(SENDER_COUNTRY).getValue());
        if(map.containsKey(SENDER_PHONE_CODE))
            setPhoneCode(map.get(SENDER_PHONE_CODE).getValue());
        if(map.containsKey(SENDER_PHONE_NUMBER))
            setPhoneNumber(map.get(SENDER_PHONE_NUMBER).getValue());
        if(map.containsKey(SENDER_WEBSITE))
            setWebsite(map.get(SENDER_WEBSITE).getValue());
        if(map.containsKey(SENDER_LOGO))
            setLogoUrl(map.get(SENDER_LOGO).getValue());
        if(map.containsKey(SENDER_TAX_ID))
            setTaxId(map.get(SENDER_TAX_ID).getValue());
        if(map.containsKey(SENDER_ADDITIONAL_NOTES))
            setAdditionalNotes(map.get(SENDER_ADDITIONAL_NOTES).getValue());
    }

    /**
     * Copy constructor.
     */
    public Sender(Sender obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Sender obj)
    {
        if(obj != null)
        {
            setBillingName(obj.getBillingName());
            setBillingEmail(obj.getBillingEmail());
            setGivenName(obj.getGivenName());
            setSurname(obj.getSurname());
            setAddressLine1(obj.getAddressLine1());
            setAddressLine2(obj.getAddressLine2());
            setAddressArea1(obj.getAddressArea1());
            setAddressArea2(obj.getAddressArea2());
            setPostalCode(obj.getPostalCode());
            setCountry(obj.getCountry());
            setPhoneCode(obj.getPhoneCode());
            setPhoneNumber(obj.getPhoneNumber());
            setWebsite(obj.getWebsite());
            setLogoUrl(obj.getLogoUrl());
            setTaxId(obj.getTaxId());
            setAdditionalNotes(obj.getAdditionalNotes());
        }
    }

    /**
     * Returns the billing name.
     */
    public String toString()
    {
        return getBillingName();
    }

    /**
     * Returns the billing name.
     */
    public String getBillingName()
    {
        return billingName;
    }

    /**
     * Sets the billing name.
     */
    public void setBillingName(String billingName)
    {
        this.billingName = billingName;
    }

    /**
     * Returns <CODE>true</CODE> if the billing name has been set.
     */
    public boolean hasBillingName()
    {
        return getBillingName() != null && getBillingName().length() > 0;
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
     * Returns the given name.
     */
    public String getGivenName()
    {
        return givenName;
    }

    /**
     * Sets the given name.
     */
    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    /**
     * Returns <CODE>true</CODE> if the given name has been set.
     */
    public boolean hasGivenName()
    {
        return getGivenName() != null && getGivenName().length() > 0;
    }

    /**
     * Returns the surname.
     */
    public String getSurname()
    {
        return surname;
    }

    /**
     * Sets the surname.
     */
    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    /**
     * Returns <CODE>true</CODE> if the surname has been set.
     */
    public boolean hasSurname()
    {
        return getSurname() != null && getSurname().length() > 0;
    }

    /**
     * Returns the address line 1.
     */
    public String getAddressLine1()
    {
        return addressLine1;
    }

    /**
     * Sets the address line 1.
     */
    public void setAddressLine1(String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    /**
     * Returns the address line 2.
     */
    public String getAddressLine2()
    {
        return addressLine2;
    }

    /**
     * Sets the address line 2.
     */
    public void setAddressLine2(String addressLine2)
    {
        this.addressLine2 = addressLine2;
    }

    /**
     * Returns the address area 1.
     */
    public String getAddressArea1()
    {
        return addressArea1;
    }

    /**
     * Sets the address area 1.
     */
    public void setAddressArea1(String addressArea1)
    {
        this.addressArea1 = addressArea1;
    }

    /**
     * Returns the address area 2.
     */
    public String getAddressArea2()
    {
        return addressArea2;
    }

    /**
     * Sets the address area 2.
     */
    public void setAddressArea2(String addressArea2)
    {
        this.addressArea2 = addressArea2;
    }

    /**
     * Returns the postal code.
     */
    public String getPostalCode()
    {
        return postalCode;
    }

    /**
     * Sets the postal code.
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    /**
     * Returns the country.
     */
    public Country getCountry()
    {
        return country;
    }

    /**
     * Sets the country.
     */
    public void setCountry(String country)
    {
        setCountry(Country.fromCode(country));
    }

    /**
     * Sets the country.
     */
    public void setCountry(Country country)
    {
        this.country = country;
    }

    /**
     * Returns the phone code.
     */
    public String getPhoneCode()
    {
        return phoneCode;
    }

    /**
     * Sets the phone code.
     */
    public void setPhoneCode(String phoneCode)
    {
        this.phoneCode = phoneCode;
    }

    /**
     * Returns <CODE>true</CODE> if the phone code has been set.
     */
    public boolean hasPhoneCode()
    {
        return getPhoneCode() != null && getPhoneCode().length() > 0;
    }

    /**
     * Returns the phone number.
     */
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     */
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Returns the logo url.
     */
    public String getLogoUrl()
    {
        return logoUrl;
    }

    /**
     * Sets the logo url.
     */
    public void setLogoUrl(String logoUrl)
    {
        this.logoUrl = logoUrl;
    }

    /**
     * Returns the tax id.
     */
    public String getTaxId()
    {
        return taxId;
    }

    /**
     * Sets the tax id.
     */
    public void setTaxId(String taxId)
    {
        this.taxId = taxId;
    }

    /**
     * Returns the additional notes.
     */
    public String getAdditionalNotes()
    {
        return additionalNotes;
    }

    /**
     * Sets the additional notes.
     */
    public void setAdditionalNotes(String additionalNotes)
    {
        this.additionalNotes = additionalNotes;
    }

    /**
     * Returns <CODE>true</CODE> if the additional notes has been set.
     */
    public boolean hasAdditionalNotes()
    {
        return getAdditionalNotes() != null && getAdditionalNotes().length() > 0;
    }
}