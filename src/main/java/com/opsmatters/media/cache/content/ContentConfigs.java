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
package com.opsmatters.media.cache.content;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.organisation.OrganisationContentConfig;
import com.opsmatters.media.model.content.organisation.OrganisationListingConfig;

/**
 * Class representing the set of content configurations.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentConfigs
{
    private static final Logger logger = Logger.getLogger(ContentConfigs.class.getName());

    private static List<OrganisationContentConfig> configList = new ArrayList<OrganisationContentConfig>();
    private static Map<String,OrganisationContentConfig> configMap = new HashMap<String,OrganisationContentConfig>();
    private static Map<String,OrganisationContentConfig> nameMap = new HashMap<String,OrganisationContentConfig>();
    private static Map<ContentType,Map<String,OrganisationContentConfig>> typeMap = new HashMap<ContentType,Map<String,OrganisationContentConfig>>();
    private static OrganisationListingConfig listing;

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private ContentConfigs()
    {
    }

    /**
     * Returns <CODE>true</CODE> if configurations have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of configurations.
     */
    public static void load(List<OrganisationContentConfig> configs)
    {
        initialised = false;

        clear();
        for(OrganisationContentConfig config : configs)
            add(config);

        logger.info("Loaded "+size()+" content configurations");

        initialised = true;
    }

    /**
     * Loads the set of archived configurations.
     */
    public static void loadArchived(List<OrganisationContentConfig> configs)
    {
        for(OrganisationContentConfig config : configs)
            add(config);

        logger.info("Loaded "+size()+" archived content configurations");
    }

    /**
     * Clears the configurations.
     */
    private static void clear()
    {
        configList.clear();
        configMap.clear();
        nameMap.clear();
        typeMap.clear();
    }

    /**
     * Adds the given configuration.
     */
    private static void add(OrganisationContentConfig config)
    {
        configList.add(config);
        configMap.put(config.getCode(), config);
        nameMap.put(config.getName(), config);
    }

    /**
     * Organise the configurations by content type.
     */
    public static void populateContentTypes()
    {
        for(ContentType type : ContentType.values())
        {
            if(type != ContentType.ORGANISATION)
                populateContentType(type);
        }
    }

    /**
     * Organise the configurations for the given content type.
     */
    private static void populateContentType(ContentType type)
    {
        Map<String,OrganisationContentConfig> map = new TreeMap<String,OrganisationContentConfig>();
        typeMap.put(type, map);

        // Get the config files for the given content type
        for(OrganisationContentConfig config : configList)
        {
            if(config.hasConfig(type))
                map.put(config.getName(), config);
        }

        logger.info(String.format("Loaded %d %s configurations", 
            map.size(), type != null ? type.value() : "organisation"));
    }

    /**
     * Returns the configuration for the given code.
     */
    public static OrganisationContentConfig get(String code)
    {
        return configMap.get(code);
    }

    /**
     * Returns the configuration for the given name.
     */
    public static OrganisationContentConfig getByName(String name)
    {
        return nameMap.get(name);
    }

    /**
     * Sets the given configuration.
     */
    public static void set(OrganisationContentConfig config)
    {
        OrganisationContentConfig existing = configMap.get(config.getCode());

        if(existing != null)
        {
            configList.set(configList.indexOf(existing), config);
        }
        else
        {
            configList.add(config);
        }

        for(ContentType type : ContentType.values())
        {
            if(type != ContentType.ORGANISATION)
            {
                Map<String,OrganisationContentConfig> map = typeMap.get(type);
                if(config.hasConfig(type))
                    map.put(config.getName(), config);
            }
        }

        configMap.put(config.getCode(), config);
        nameMap.put(config.getName(), config);
    }

    /**
     * Returns the list of configurations.
     */
    public static List<OrganisationContentConfig> list()
    {
        return configList;
    }

    /**
     * Returns the map of configurations for the given type.
     */
    public static Map<String,OrganisationContentConfig> mapByType(ContentType type)
    {
        return typeMap.get(type);
    }

    /**
     * Returns the organisation listing configuration.
     */
    public static OrganisationListingConfig listing()
    {
        return listing;
    }

    /**
     * Sets the organisation listing configuration.
     */
    public static void set(OrganisationListingConfig listing)
    {
        ContentConfigs.listing = listing;
    }

    /**
     * Returns the number of configurations.
     */
    public static int size()
    {
        return configList.size();
    }
}