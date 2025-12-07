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
import com.opsmatters.media.model.order.Country;

/**
 * Class representing the list of countries.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Countries implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Countries.class.getName());

    private static Map<String,Country> codeMap = new LinkedHashMap<String,Country>();
    private static Map<String,Country> nameMap = new TreeMap<String,Country>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Countries()
    {
    }

    /**
     * Returns <CODE>true</CODE> if countries have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of countries.
     */
    public static void load(List<Country> countries)
    {
        initialised = false;

        clear();
        for(Country country : countries)
        {
            add(country);
        }

        logger.info("Loaded "+size()+" countries");

        initialised = true;
    }

    /**
     * Clears the countries.
     */
    public static void clear()
    {
        codeMap.clear();
        nameMap.clear();
    }

    /**
     * Returns the country with the given code.
     */
    public static Country get(String code)
    {
        return code != null ? codeMap.get(code) : null;
    }

    /**
     * Adds the country with the given code.
     */
    public static void add(Country country)
    {
        codeMap.put(country.getCode(), country);
        nameMap.put(country.getName(), country);
    }

    /**
     * Removes the given country.
     */
    public static void remove(Country country)
    {
        codeMap.remove(country.getCode());
        nameMap.remove(country.getName());
    }

    /**
     * Returns the count of countries.
     */
    public static int size()
    {
        return codeMap.size();
    }

    /**
     * Returns the list of countries.
     */
    public static List<Country> list()
    {
        List<Country> ret = new ArrayList<Country>();
        for(Country country : nameMap.values())
        {
            if(country.isEnabled())
                ret.add(country);
        }

        return ret;
    }
}