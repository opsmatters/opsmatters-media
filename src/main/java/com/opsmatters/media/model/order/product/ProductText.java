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
package com.opsmatters.media.model.order.product;

import java.time.Instant;
import com.opsmatters.media.model.ManagedEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a product text.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProductText extends ManagedEntity
{
    private String productId = "";
    private TextKey key = TextKey.NONE;
    private String value = "";

    /**
     * Default constructor.
     */
    public ProductText()
    {
    }

    /**
     * Constructor that takes a product.
     */
    public ProductText(Product product)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setProductId(product.getId());
    }

    /**
     * Copy constructor.
     */
    public ProductText(ProductText obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ProductText obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setProductId(obj.getProductId());
            setKey(obj.getKey());
            setValue(obj.getValue());
        }
    }

    /**
     * Returns the text key.
     */
    public String toString()
    {
        return getKey().name();
    }

    /**
     * Returns the product id.
     */
    public String getProductId()
    {
        return productId;
    }

    /**
     * Sets the product id.
     */
    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    /**
     * Returns the text key.
     */
    public TextKey getKey()
    {
        return key;
    }

    /**
     * Sets the text key.
     */
    public void setKey(String key)
    {
        setKey(TextKey.valueOf(key));
    }

    /**
     * Sets the text key.
     */
    public void setKey(TextKey key)
    {
        this.key = key;
    }

    /**
     * Returns the text value.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the text value.
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}