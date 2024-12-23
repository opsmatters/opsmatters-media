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
import com.opsmatters.media.model.order.product.ProductText;
import com.opsmatters.media.model.order.product.TextKey;

/**
 * Class representing the list of products.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Products implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Products.class.getName());

    private static Map<String,Product> codeMap = new LinkedHashMap<String,Product>();
    private static Map<String,ProductText> textMap = new LinkedHashMap<String,ProductText>();
    private static Map<String,Map<TextKey,ProductText>> textProductMap = new LinkedHashMap<String,Map<TextKey,ProductText>>();

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
     * Loads the set of products and texts.
     */
    public static void load(List<Product> products, List<ProductText> texts)
    {
        initialised = false;

        clear();
        for(Product product : products)
        {
            add(product);
        }

        logger.info("Loaded "+size()+" products");

        for(ProductText text : texts)
        {
            add(text);
        }

        logger.info("Loaded "+textMap.size()+" product texts");

        initialised = true;
    }

    /**
     * Clears the products.
     */
    public static void clear()
    {
        codeMap.clear();
        textMap.clear();
        textProductMap.clear();
    }

    /**
     * Returns the product with the given code.
     */
    public static Product get(String code)
    {
        return code != null ? codeMap.get(code) : null;
    }

    /**
     * Adds the product with the given code.
     */
    public static void add(Product product)
    {
        codeMap.put(product.getCode(), product);
    }

    /**
     * Adds the given product text.
     */
    public static void add(ProductText text)
    {
        Map<TextKey,ProductText> texts = textProductMap.get(text.getProductId());
        if(texts == null)
        {
            texts = new LinkedHashMap<TextKey,ProductText>();
            textProductMap.put(text.getProductId(), texts);
        }

        texts.put(text.getKey(), text);
        textMap.put(text.getId(), text);
    }

    /**
     * Removes the product with the given code.
     */
    public static void remove(Product product)
    {
        codeMap.remove(product.getCode());
    }

    /**
     * Removes the given product text.
     */
    public static void remove(ProductText text)
    {
        textProductMap.get(text.getProductId()).remove(text.getKey());
        textMap.remove(text.getId());
    }

    /**
     * Returns the count of products.
     */
    public static int size()
    {
        return codeMap.size();
    }

    /**
     * Returns the list of products.
     */
    public static List<Product> list()
    {
        List<Product> ret = new ArrayList<Product>();
        for(Product product : codeMap.values())
        {
            if(product.isActive())
                ret.add(product);
        }

        return ret;
    }

    /**
     * Returns the text for the given product and key.
     */
    public static String getText(Product product, TextKey key)
    {
        String ret = null;
        Map<TextKey,ProductText> texts = textProductMap.get(product.getId());
        if(texts != null)
        {
            ProductText text = texts.get(key);
            if(text != null)
                ret = text.getValue();
        }

        return ret;
    }
}