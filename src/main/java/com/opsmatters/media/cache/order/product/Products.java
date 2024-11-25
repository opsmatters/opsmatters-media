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
package com.opsmatters.media.cache.order.product;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.product.Product;

/**
 * Class representing the list of products.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Products implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Products.class.getName());

    private static Map<String,Product> productMap = new LinkedHashMap<String,Product>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Products()
    {
    }

    /**
     * Returns <CODE>true</CODE> if products have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of products.
     */
    public static void load(List<Product> products)
    {
        initialised = false;

        clear();
        for(Product product : products)
        {
            add(product);
        }

        logger.info("Loaded "+size()+" products");

        initialised = true;
    }

    /**
     * Clears the products.
     */
    public static void clear()
    {
        productMap.clear();
    }

    /**
     * Returns the product with the given code.
     */
    public static Product get(String code)
    {
        return productMap.get(code);
    }

    /**
     * Adds the product with the given code.
     */
    public static void add(Product product)
    {
        productMap.put(product.getCode(), product);
    }

    /**
     * Removes the product with the given code.
     */
    public static void remove(Product product)
    {
        productMap.remove(product.getCode());
    }

    /**
     * Returns the count of products.
     */
    public static int size()
    {
        return productMap.size();
    }

    /**
     * Returns the list of products.
     */
    public static List<Product> list()
    {
        List<Product> ret = new ArrayList<Product>();
        for(Product product : productMap.values())
        {
            if(product.isActive())
                ret.add(product);
        }

        return ret;
    }
}