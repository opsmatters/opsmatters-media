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
package com.opsmatters.media.model.order.billing;

import java.time.Instant;
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.model.order.Country;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a billing profile.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BillingProfile extends OwnedEntity
{
    private String email = "";
    private String givenName = "";
    private String surname = "";
    private String companyName = "";
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String addressArea1 = "";
    private String addressArea2 = "";
    private String postalCode = "";
    private Country country = Country.UNDEFINED;
    private String phoneCode = "";
    private String phoneNumber = "";
    private String additionalInfo = "";
    private ProfileStatus status = ProfileStatus.NEW;

    /**
     * Default constructor.
     */
    public BillingProfile()
    {
    }

    /**
     * Constructor that takes an email.
     */
    public BillingProfile(String email)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setEmail(email);
    }

    /**
     * Copy constructor.
     */
    public BillingProfile(BillingProfile obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(BillingProfile obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setEmail(obj.getEmail());
            setGivenName(obj.getGivenName());
            setSurname(obj.getSurname());
            setCompanyName(obj.getCompanyName());
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
     * Returns the email.
     */
    public String toString()
    {
        return getEmail();
    }

    /**
     * Returns the email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the email.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns <CODE>true</CODE> if the email has been set.
     */
    public boolean hasEmail()
    {
        return getEmail() != null && getEmail().length() > 0;
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
    public String getName()
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
     * Returns the company name.
     */
    public String getCompanyName()
    {
        return companyName;
    }

    /**
     * Sets the company name.
     */
    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    /**
     * Returns <CODE>true</CODE> if the company name has been set.
     */
    public boolean hasCompanyName()
    {
        return getCompanyName() != null && getCompanyName().length() > 0;
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
     * Returns the profile status.
     */
    public ProfileStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the profile status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == ProfileStatus.ACTIVE;
    }

    /**
     * Sets the profile status.
     */
    public void setStatus(String status)
    {
        setStatus(ProfileStatus.valueOf(status));
    }

    /**
     * Sets the profile status.
     */
    public void setStatus(ProfileStatus status)
    {
        this.status = status;
    }
}