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
package com.opsmatters.media.cache.order.contact;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.Company;

/**
 * Class representing the list of companies.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Companies implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Companies.class.getName());

    private static Map<String,Company> companyMap = new LinkedHashMap<String,Company>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Companies()
    {
    }

    /**
     * Returns <CODE>true</CODE> if companies have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of companies.
     */
    public static void load(List<Company> companies)
    {
        initialised = false;

        clear();
        for(Company company : companies)
        {
            add(company);
        }

        logger.info("Loaded "+size()+" companies");

        initialised = true;
    }

    /**
     * Clears the companies.
     */
    public static void clear()
    {
        companyMap.clear();
    }

    /**
     * Returns the company with the given id.
     */
    public static Company get(String id)
    {
        return companyMap.get(id);
    }

    /**
     * Adds the company with the given id.
     */
    public static void add(Company company)
    {
        companyMap.put(company.getId(), company);
    }

    /**
     * Removes the company with the given id.
     */
    public static void remove(Company company)
    {
        companyMap.remove(company.getId());
    }

    /**
     * Returns the count of companies.
     */
    public static int size()
    {
        return companyMap.size();
    }

    /**
     * Returns the list of companies.
     */
    public static List<Company> list()
    {
        List<Company> ret = new ArrayList<Company>();
        for(Company company : companyMap.values())
        {
            if(company.isActive())
                ret.add(company);
        }

        return ret;
    }
}