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
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.model.order.Country;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a company.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Company extends OwnedEntity
{
    private String name = "";
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
    private String additionalInfo = "";
    private CompanyStatus status = CompanyStatus.NEW;

    /**
     * Default constructor.
     */
    public Company()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Company(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public Company(Company obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Company obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
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
            setAdditionalInfo(obj.getAdditionalInfo());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the company name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the company name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the company name has been set.
     */
    public boolean hasName()
    {
        return getName() != null && getName().length() > 0;
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
     * Returns the given name and surname.
     */
    public String getFullName()
    {
        StringBuffer ret = new StringBuffer();
        if(hasGivenName())
            ret.append(getGivenName());
        if(ret.length() > 0)
            ret.append(" ");
        if(hasSurname())
            ret.append(getSurname());
        return ret.toString();
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
     * Returns the additional info.
     */
    public String getAdditionalInfo()
    {
        return additionalInfo;
    }

    /**
     * Sets the additional info.
     */
    public void setAdditionalInfo(String additionalInfo)
    {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Returns <CODE>true</CODE> if the additional info has been set.
     */
    public boolean hasAdditionalInfo()
    {
        return getAdditionalInfo() != null && getAdditionalInfo().length() > 0;
    }

    /**
     * Returns the company status.
     */
    public CompanyStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the company status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == CompanyStatus.ACTIVE;
    }

    /**
     * Sets the company status.
     */
    public void setStatus(String status)
    {
        setStatus(CompanyStatus.valueOf(status));
    }

    /**
     * Sets the company status.
     */
    public void setStatus(CompanyStatus status)
    {
        this.status = status;
    }
}