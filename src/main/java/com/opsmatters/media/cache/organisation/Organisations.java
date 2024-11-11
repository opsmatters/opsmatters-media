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
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.ContentSettings;

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
    private static Map<ContentType,Map<String,Organisation>> typeMap = new HashMap<ContentType,Map<String,Organisation>>();
    private static List<ContentSettings> settingsList = new ArrayList<ContentSettings>();

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
     * Load the set of content settings.
     */
    public static void add(List<ContentSettings> settings)
    {
        settingsList.addAll(settings);
    }

    /**
     * Clears the organisations.
     */
    public static void clear()
    {
        organisationMap.clear();
        organisationList.clear();
        typeMap.clear();
        settingsList.clear();
    }

    /**
     * Adds the given organisation.
     */
    private static void add(Organisation organisation)
    {
        organisationMap.put(organisation.getCode(), organisation);
        organisationList.add(organisation);

        // Add the settings for the organisation
        for(ContentSettings settings : settingsList)
        {
            if(settings.getCode().equals(organisation.getCode()))
            {
                organisation.setSettings(settings);
            }
        }

        // Add the organisation to the map of config types
        for(ContentType type : ContentType.values())
        {
            if(type != ContentType.ORGANISATION
                && type != ContentType.ARTICLE)
            {
                Map<String,Organisation> map = typeMap.get(type);
                if(map == null)
                {
                    map = new TreeMap<String,Organisation>();
                    typeMap.put(type, map);
                }

                if(organisation.hasContentConfig(type))
                    map.put(organisation.getName(), organisation);
            }
        }
    }

    /**
     * Returns the organisation with the given code.
     */
    public static Organisation get(String code)
    {
        return code != null ? organisationMap.get(code) : null;
    }

    /**
     * Returns <CODE>true</CODE> if the given organisation already exists.
     */
    public static boolean exists(String code)
    {
        return code != null && organisationMap.containsKey(code);
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
     * Returns <CODE>true</CODE> if the given organisation already exists.
     */
    public static boolean exists(Organisation organisation)
    {
        Organisation existing = get(organisation.getCode());
        return existing != null ? !existing.getId().equals(organisation.getId()) : false;
    }

    /**
     * Returns the content settings for the given content type.
     */
    public static ContentSettings getSettings(String code, ContentType type)
    {
        ContentSettings ret = null;
        Organisation organisation = Organisations.get(code);
        if(organisation != null)
        {
            ret = organisation.getSettings(type);
            if(ret == null && type != ContentType.ORGANISATION)
                logger.warning("Unable to find organisation content type for "+code+": "+type);
        }

        return ret;
    }

    /**
     * Returns the content settings for the given configuration.
     */
    public static ContentSettings getSettings(ContentConfig config)
    {
        return getSettings(config.getCode(), config.getType());
    }

    /**
     * Sets the organisation and its settings.
     */
    public static void set(Organisation organisation, List<ContentSettings> settings)
    {
        remove(organisation);
        add(settings);
        add(organisation);
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
        for(ContentType type : ContentType.values())
        {
            ContentSettings settings = getSettings(organisation.getCode(), type);
            if(settings != null)
                settingsList.remove(settings);
        }

        Organisation existing = get(organisation.getCode());
        if(existing != null)
        {
            organisationMap.remove(organisation.getCode());
            organisationList.remove(existing);
        }
    }

    /**
     * Returns the map of organisations for the given type.
     */
    public static Map<String,Organisation> mapByType(ContentType type)
    {
        return typeMap.get(type);
    }

    /**
     * Returns the number of organisations.
     */
    public static int size()
    {
        return organisationList.size();
    }
}