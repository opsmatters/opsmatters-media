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
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a product text.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProductText extends BaseEntity
{
    private String productId = "";
    private String code = "";
    private ProductTextId textId;
    private String value = "";
    private String variation = "";

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
            setCode(obj.getCode());
            setValue(obj.getValue());
            setVariation(obj.getVariation());
        }
    }

    /**
     * Returns the text code.
     */
    public String toString()
    {
        return getCode();
    }

    /**
     * Returns the code for the text.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code for the text.
     */
    public void setCode(String code)
    {
        this.code = code;
        setTextId(code);
    }

    /**
     * Returns <CODE>true</CODE> if the code for the text has been set.
     */
    public boolean hasCode()
    {
        return getCode() != null && getCode().length() > 0;
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
     * Returns the text id.
     */
    public ProductTextId getTextId()
    {
        return textId;
    }

    /**
     * Sets the text id.
     */
    private void setTextId(ProductTextId textId)
    {
        this.textId = textId;
    }

    /**
     * Sets the text id.
     */
    private void setTextId(String code)
    {
        setTextId(ProductTextId.fromCode(code));
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

    /**
     * Returns <CODE>true</CODE> if the value for the text has been set.
     */
    public boolean hasValue()
    {
        return getValue() != null && getValue().length() > 0;
    }

    /**
     * Returns the text variation.
     */
    public String getVariation()
    {
        return variation;
    }

    /**
     * Sets the text variation.
     */
    public void setVariation(String variation)
    {
        this.variation = variation;
    }

    /**
     * Returns <CODE>true</CODE> if the variation for the text has been set.
     */
    public boolean hasVariation()
    {
        return getVariation() != null && getVariation().length() > 0;
    }
}