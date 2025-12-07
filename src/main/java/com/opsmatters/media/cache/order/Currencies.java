/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.cache.order;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.Currency;

/**
 * Class representing the list of currencies.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Currencies implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Currencies.class.getName());

    private static Map<String,Currency> codeMap = new LinkedHashMap<String,Currency>();
    private static Map<String,Currency> nameMap = new TreeMap<String,Currency>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Currencies()
    {
    }

    /**
     * Returns <CODE>true</CODE> if currencies have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of currencies.
     */
    public static void load(List<Currency> currencies)
    {
        initialised = false;

        clear();
        for(Currency currency : currencies)
        {
            add(currency);
        }

        logger.info("Loaded "+size()+" currencies");

        initialised = true;
    }

    /**
     * Clears the currencies.
     */
    public static void clear()
    {
        codeMap.clear();
        nameMap.clear();
    }

    /**
     * Returns the currency with the given code.
     */
    public static Currency get(String code)
    {
        return code != null ? codeMap.get(code) : null;
    }

    /**
     * Adds the currency with the given code.
     */
    public static void add(Currency currency)
    {
        codeMap.put(currency.getCode(), currency);
        nameMap.put(currency.getName(), currency);
    }

    /**
     * Removes the given currency.
     */
    public static void remove(Currency currency)
    {
        codeMap.remove(currency.getCode());
        nameMap.remove(currency.getName());
    }

    /**
     * Returns the count of currencies.
     */
    public static int size()
    {
        return codeMap.size();
    }

    /**
     * Returns the list of currencies.
     */
    public static List<Currency> list()
    {
        List<Currency> ret = new ArrayList<Currency>();
        for(Currency currency : nameMap.values())
        {
            if(currency.isEnabled())
                ret.add(currency);
        }

        return ret;
    }
}