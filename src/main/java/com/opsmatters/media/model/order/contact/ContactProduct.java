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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.cache.order.product.Products;
import com.opsmatters.media.cache.system.Sites;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.order.Currency;
import com.opsmatters.media.model.order.Frequency;
import com.opsmatters.media.model.order.product.Product;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a contact product.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactProduct extends BaseEntity
{
    private String contactId = "";
    private String productCode = "";
    private String siteId = "";
    private int price = 0;
    private Currency currency = Currency.UNDEFINED;
    private Instant startDate;
    private Frequency frequency = Frequency.NONE;
    private boolean deliveryEmail = false;
    private boolean enabled = true;

    /**
     * Default constructor.
     */
    public ContactProduct()
    {
    }

    /**
     * Constructor that takes a contact.
     */
    public ContactProduct(Contact contact)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setContactId(contact.getId());
        setCurrency(contact.getCurrency());
    }

    /**
     * Copy constructor.
     */
    public ContactProduct(ContactProduct obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContactProduct obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setContactId(obj.getContactId());
            setProductCode(obj.getProductCode());
            setSiteId(obj.getSiteId());
            setPrice(obj.getPrice());
            setCurrency(obj.getCurrency());
            setStartDate(obj.getStartDate());
            setFrequency(obj.getFrequency());
            setDeliveryEmail(obj.hasDeliveryEmail());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the product and site.
     */
    public String toString()
    {
        Product product = Products.get(getProductCode());
        Site site = Sites.get(getSiteId());
        return String.format("%s / %s",
            product != null ? product.getName() : "-",
            site != null ? site.getName() : "-");
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
     * Returns the product code.
     */
    public String getProductCode()
    {
        return productCode;
    }

    /**
     * Sets the product code.
     */
    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    /**
     * Returns the site id.
     */
    public String getSiteId()
    {
        return siteId;
    }

    /**
     * Sets the site id.
     */
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }

    /**
     * Returns the price.
     */
    public int getPrice()
    {
        return price;
    }

    /**
     * Sets the price.
     */
    public void setPrice(int price)
    {
        this.price = price;
    }

    /**
     * Returns the currency.
     */
    public Currency getCurrency()
    {
        return currency;
    }

    /**
     * Sets the currency.
     */
    public void setCurrency(String currency)
    {
        setCurrency(Currency.fromCode(currency));
    }

    /**
     * Sets the currency.
     */
    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    /**
     * Returns the start date.
     */
    public Instant getStartDate()
    {
        return startDate;
    }

    /**
     * Returns the start date.
     */
    public long getStartDateMillis()
    {
        return getStartDate() != null ? getStartDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the start date.
     */
    public LocalDateTime getStartDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getStartDate());
    }

    /**
     * Returns the start date.
     */
    public String getStartDateAsString()
    {
        return getStartDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the start date.
     */
    public String getStartDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getStartDate(), pattern);
    }

    /**
     * Sets the start date.
     */
    public void setStartDate(Instant startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Sets the start date.
     */
    public void setStartDateMillis(long millis)
    {
        if(millis > 0L)
            setStartDate(Instant.ofEpochMilli(millis));
    }

    /**
     * Sets the start date.
     */
    public void setStartDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setStartDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the start date.
     */
    public void setStartDateAsString(String str) throws DateTimeParseException
    {
        setStartDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the start date.
     */
    public void setStartDateUTC(LocalDateTime startDate)
    {
        if(startDate != null)
            setStartDate(TimeUtils.toInstantUTC(startDate));
    }

    /**
     * Returns the frequency.
     */
    public Frequency getFrequency()
    {
        return frequency;
    }

    /**
     * Sets the frequency.
     */
    public void setFrequency(String frequency)
    {
        setFrequency(Frequency.valueOf(frequency));
    }

    /**
     * Sets the frequency.
     */
    public void setFrequency(Frequency frequency)
    {
        this.frequency = frequency;
    }

    /**
     * Returns <CODE>true</CODE> if this product requires a delivery email.
     */
    public boolean hasDeliveryEmail()
    {
        return deliveryEmail;
    }

    /**
     * Returns <CODE>true</CODE> if this product requires a delivery email.
     */
    public Boolean getDeliveryEmailObject()
    {
        return Boolean.valueOf(deliveryEmail);
    }

    /**
     * Set to <CODE>true</CODE> if this product requires a delivery email.
     */
    public void setDeliveryEmail(boolean deliveryEmail)
    {
        this.deliveryEmail = deliveryEmail;
    }

    /**
     * Set to <CODE>true</CODE> if this product requires a delivery email.
     */
    public void setDeliveryEmailObject(Boolean deliveryEmail)
    {
        setDeliveryEmail(deliveryEmail != null && deliveryEmail.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if the person is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this person is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the person is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this person is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }
}