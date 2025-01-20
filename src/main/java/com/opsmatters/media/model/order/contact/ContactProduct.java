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
import com.opsmatters.media.cache.order.product.Products;
import com.opsmatters.media.cache.platform.Sites;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.order.Currency;
import com.opsmatters.media.model.order.product.Product;
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
}