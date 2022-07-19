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
package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.config.content.OrganisationContentConfiguration;
import com.opsmatters.media.config.content.OrganisationListingConfiguration;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing the set of content configurations.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentConfigurations
{
    private static final Logger logger = Logger.getLogger(ContentConfigurations.class.getName());

    private static List<OrganisationContentConfiguration> configurationList = new ArrayList<OrganisationContentConfiguration>();
    private static Map<String,OrganisationContentConfiguration> configurationMap = new HashMap<String,OrganisationContentConfiguration>();
    private static Map<String,OrganisationContentConfiguration> nameMap = new HashMap<String,OrganisationContentConfiguration>();
    private static Map<ContentType,Map<String,OrganisationContentConfiguration>> typeMap = new HashMap<ContentType,Map<String,OrganisationContentConfiguration>>();
    private static OrganisationListingConfiguration listing;

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private ContentConfigurations()
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
    public static void load(List<OrganisationContentConfiguration> configurations)
    {
        initialised = false;

        clear();
        for(OrganisationContentConfiguration configuration : configurations)
            add(configuration);

        logger.info("Loaded "+size()+" content configurations");

        initialised = true;
    }

    /**
     * Loads the set of archived configurations.
     */
    public static void loadArchived(List<OrganisationContentConfiguration> configurations)
    {
        for(OrganisationContentConfiguration configuration : configurations)
            add(configuration);

        logger.info("Loaded "+size()+" archived content configurations");
    }

    /**
     * Clears the configurations.
     */
    private static void clear()
    {
        configurationList.clear();
        configurationMap.clear();
        nameMap.clear();
        typeMap.clear();
    }

    /**
     * Adds the given configuration.
     */
    private static void add(OrganisationContentConfiguration configuration)
    {
        configurationList.add(configuration);
        configurationMap.put(configuration.getCode(), configuration);
        nameMap.put(configuration.getName(), configuration);
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
        Map<String,OrganisationContentConfiguration> map = new TreeMap<String,OrganisationContentConfiguration>();
        typeMap.put(type, map);

        // Get the config files for the given content type
        for(OrganisationContentConfiguration configuration : configurationList)
        {
            if(configuration.hasConfiguration(type))
                map.put(configuration.getName(), configuration);
        }

        logger.info(String.format("Loaded %d %s configurations", 
            map.size(), type != null ? type.value() : "organisation"));
    }

    /**
     * Returns the configuration for the given code.
     */
    public static OrganisationContentConfiguration get(String code)
    {
        return configurationMap.get(code);
    }

    /**
     * Returns the configuration for the given name.
     */
    public static OrganisationContentConfiguration getByName(String name)
    {
        return nameMap.get(name);
    }

    /**
     * Sets the given configuration.
     */
    public static void set(OrganisationContentConfiguration configuration)
    {
        OrganisationContentConfiguration existing = configurationMap.get(configuration.getCode());

        if(existing != null)
        {
            configurationList.set(configurationList.indexOf(existing), configuration);
        }
        else
        {
            configurationList.add(configuration);
        }

        for(ContentType type : ContentType.values())
        {
            if(type != ContentType.ORGANISATION)
            {
                Map<String,OrganisationContentConfiguration> map = typeMap.get(type);
                if(configuration.hasConfiguration(type))
                    map.put(configuration.getName(), configuration);
            }
        }

        configurationMap.put(configuration.getCode(), configuration);
        nameMap.put(configuration.getName(), configuration);
    }

    /**
     * Returns the list of configurations.
     */
    public static List<OrganisationContentConfiguration> list()
    {
        return configurationList;
    }

    /**
     * Returns the map of configurations for the given type.
     */
    public static Map<String,OrganisationContentConfiguration> mapByType(ContentType type)
    {
        return typeMap.get(type);
    }

    /**
     * Returns the organisation listing configuration.
     */
    public static OrganisationListingConfiguration listing()
    {
        return listing;
    }

    /**
     * Sets the organisation listing configuration.
     */
    public static void set(OrganisationListingConfiguration listing)
    {
        ContentConfigurations.listing = listing;
    }

    /**
     * Returns the number of configurations.
     */
    public static int size()
    {
        return configurationList.size();
    }
}