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

import java.time.Instant;
import org.apache.commons.text.StringSubstitutor;
import com.opsmatters.media.cache.platform.Sites;
import com.opsmatters.media.cache.order.product.Products;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.order.contact.ContactProduct;
import com.opsmatters.media.model.order.product.Product;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.SessionId;

/**
 * Class representing an order item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrderItem extends BaseEntity
{
    private String orderId = "";
    private String productCode = "";
    private String siteId = "";
    private String contentId = "";
    private ContentType contentType;
    private int quantity = 1;
    private int price = 0;
    private Currency currency = Currency.UNDEFINED;
    private String name = "";
    private String description = "";
    private boolean enabled = true;

    /**
     * Default constructor.
     */
    public OrderItem()
    {
    }

    /**
     * Constructor that takes an order.
     */
    public OrderItem(Order order)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setOrderId(order.getId());
    }

    /**
     * Copy constructor.
     */
    public OrderItem(OrderItem obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OrderItem obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setOrderId(obj.getOrderId());
            setProductCode(obj.getProductCode());
            setSiteId(obj.getSiteId());
            setContentId(obj.getContentId());
            setContentType(obj.getContentType());
            setQuantity(obj.getQuantity());
            setPrice(obj.getPrice());
            setCurrency(obj.getCurrency());
            setName(obj.getName());
            setDescription(obj.getDescription());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the order id.
     */
    public String getOrderId()
    {
        return orderId;
    }

    /**
     * Sets the order id.
     */
    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
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
     * Returns the content id.
     */
    public String getContentId()
    {
        return contentId;
    }

    /**
     * Sets the content id.
     */
    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    /**
     * Returns the content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(contentType != null ? ContentType.valueOf(contentType) : null);
    }

    /**
     * Sets the content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Sets the content id and type using the given content.
     */
    public void setContent(Content content)
    {
        if(content != null)
        {
            setContentId(content.getUuid());
            setContentType(content.getType());
        }
    }

    /**
     * Returns the quantity.
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * Sets the quantity.
     */
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
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
     * Sets the product information using the given contact product.
     */
    public void setContactProduct(ContactProduct contactProduct)
    {
        if(contactProduct != null)
        {
            setProductCode(contactProduct.getProductCode());
            setSiteId(contactProduct.getSiteId());
            setPrice(contactProduct.getPrice());
            setCurrency(contactProduct.getCurrency());

            // Set the item name from the product template
            Product product = Products.get(getProductCode());
            if(product != null)
            {
                OrderItemProperties properties = new OrderItemProperties();
                properties.setDate(SessionId.now());
                properties.setSite(Sites.get(getSiteId()));
                setName(new StringSubstitutor(properties).replace(product.getTemplate()));
            }
        }
    }

    /**
     * Returns the item name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the item name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the item description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the item description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns <CODE>true</CODE> if the item description has been set.
     */
    public boolean hasDescription()
    {
        return getDescription() != null && getDescription().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the item is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this item is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the item is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this item is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }
}