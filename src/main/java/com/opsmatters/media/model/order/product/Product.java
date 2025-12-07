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
 * Class representing a product.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Product extends BaseEntity
{
    private String code = "";
    private String name = "";
    private ProductCategory category = ProductCategory.UNDEFINED;
    private ProductStatus status = ProductStatus.NEW;

    /**
     * Default constructor.
     */
    public Product()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Product(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public Product(Product obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Product obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setCategory(obj.getCategory());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the product name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the product code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the product code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the product name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the product name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the product category.
     */
    public ProductCategory getCategory()
    {
        return category;
    }

    /**
     * Sets the product category.
     */
    public void setCategory(String category)
    {
        setCategory(ProductCategory.valueOf(category));
    }

    /**
     * Sets the product category.
     */
    public void setCategory(ProductCategory category)
    {
        this.category = category;
    }

    /**
     * Returns the product status.
     */
    public ProductStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the product status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == ProductStatus.ACTIVE;
    }

    /**
     * Sets the product status.
     */
    public void setStatus(String status)
    {
        setStatus(ProductStatus.valueOf(status));
    }

    /**
     * Sets the product status.
     */
    public void setStatus(ProductStatus status)
    {
        this.status = status;
    }
}