/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.cache.organisation;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.organisation.Organisation;

/**
 * Class representing the set of organisations.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Organisations
{
    private static final Logger logger = Logger.getLogger(Organisations.class.getName());

    private static List<Organisation> organisationList = new ArrayList<Organisation>();
    private static Map<String,Organisation> organisationMap = new HashMap<String,Organisation>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Organisations()
    {
    }

    /**
     * Returns <CODE>true</CODE> if organisations have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of organisations.
     */
    public static void load(List<Organisation> organisations)
    {
        initialised = false;

        for(Organisation organisation : organisations)
                add(organisation);

        logger.info(String.format("Loaded %d organisations", size()));

        initialised = true;
    }

    /**
     * Clears the organisations.
     */
    public static void clear()
    {
        organisationMap.clear();
        organisationList.clear();
    }

    /**
     * Adds the given organisation.
     */
    private static void add(Organisation organisation)
    {
        organisationMap.put(organisation.getCode(), organisation);
        organisationList.add(organisation);
    }

    /**
     * Returns the organisation with the given code.
     */
    public static Organisation get(String code)
    {
        return code != null ? organisationMap.get(code) : null;
    }

    /**
     * Returns the organisation with the given name.
     */
    public static Organisation getByName(String name)
    {
        Organisation ret = null;
        if(name != null)
        {
            name = name.toLowerCase();
//            for(Organisation organisation : organisationList.get(site.getId()))
            for(Organisation organisation : organisationList)
            {
                if(organisation.getName().toLowerCase().equals(name))
                {
                    ret = organisation;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Sets the organisation.
     */
    public static void set(Organisation organisation)
    {
        Organisation existing = get(organisation.getCode());
        organisationMap.put(organisation.getCode(), organisation);
        if(existing != null)
            organisationList.remove(existing);
        organisationList.add(organisation);
    }

    /**
     * Returns the organisations.
     */
    public static List<Organisation> list()
    {
        return organisationList;
    }

    /**
     * Removes the given organisation.
     */
    public static void remove(Organisation organisation)
    {
        organisation = organisationMap.remove(organisation.getCode());
        organisationList.remove(organisation);
    }

    /**
     * Returns the number of organisations.
     */
    public static int size()
    {
        return organisationList.size();
    }
}