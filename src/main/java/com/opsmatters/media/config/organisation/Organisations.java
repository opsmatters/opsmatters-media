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
package com.opsmatters.media.config.organisation;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.config.platform.Sites;
import com.opsmatters.media.config.content.ContentConfiguration;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationContentType;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing the set of organisations.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Organisations
{
    private static final Logger logger = Logger.getLogger(Organisations.class.getName());

    private static Map<String,List<Organisation>> organisationList = new HashMap<String,List<Organisation>>();
    private static Map<String,Map<String,Organisation>> organisationMap = new HashMap<String,Map<String,Organisation>>();
    private static List<OrganisationContentType> types = new ArrayList<OrganisationContentType>();

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

        for(Site site : Sites.list())
        {
            if(site.isEnabled())
            {
                List<Organisation> list = organisationList.get(site.getId());
                if(list == null)
                {
                    list = new ArrayList<Organisation>();
                    organisationList.put(site.getId(), list);
                }

                Map<String,Organisation> map = organisationMap.get(site.getId());
                if(map == null)
                {
                    map = new TreeMap<String,Organisation>();
                    organisationMap.put(site.getId(), map);
                }

                for(Organisation organisation : organisations)
                {
                    if(organisation.getSiteId().equals(site.getId()))
                        add(organisation);
                }

                logger.info(String.format("Loaded %d organisations for site %s",
                    list.size(), site.getName()));
            }
        }

        initialised = true;
    }

    /**
     * Load the set of organisation content types.
     */
    public static void add(List<OrganisationContentType> types)
    {
        Organisations.types.addAll(types);
    }

    /**
     * Clears the organisations.
     */
    public static void clear()
    {
        organisationMap.clear();
        organisationList.clear();
        types.clear();
    }

    /**
     * Adds the given organisation.
     */
    private static void add(Organisation organisation)
    {
        organisationMap.get(organisation.getSiteId()).put(organisation.getCode(), organisation);
        organisationList.get(organisation.getSiteId()).add(organisation);
        for(OrganisationContentType type : types)
        {
            if(type.getSiteId().equals(organisation.getSiteId())
                && type.getCode().equals(organisation.getCode()))
            {
                organisation.setContentType(type);
            }
        }
    }

    /**
     * Returns the organisation with the given code.
     */
    public static Organisation get(Site site, String code)
    {
        return get(site.getId(), code);
    }

    /**
     * Returns the organisation with the given code.
     */
    public static Organisation get(String siteId, String code)
    {
        return code != null ? organisationMap.get(siteId).get(code) : null;
    }

    /**
     * Returns the first organisation with the given code.
     */
    public static Organisation get(String code)
    {
        Organisation ret = null;
        for(Site site : Sites.list())
        {
            ret = get(site.getId(), code);
            if(ret != null)
                break;
        }

        return ret;
    }

    /**
     * Returns the organisation with the given name.
     */
    public static Organisation getByName(Site site, String name)
    {
        Organisation ret = null;
        if(name != null)
        {
            name = name.toLowerCase();
            for(Organisation organisation : organisationList.get(site.getId()))
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
     * Returns the content type for the given organisation and content type.
     */
    public static OrganisationContentType getContentType(String siteId, String code, ContentType type)
    {
        OrganisationContentType ret = null;
        Organisation organisation = Organisations.get(siteId, code);
        if(organisation != null)
        {
            ret = organisation.getContentType(type);
            if(ret == null)
                logger.warning("Unable to find organisation content type for "+code+": "+type);
        }

        return ret;
    }

    /**
     * Returns the content type for the given configuration.
     */
    public static OrganisationContentType getContentType(Site site, ContentConfiguration config)
    {
        return getContentType(site.getId(), config.getCode(), config.getType());
    }

    /**
     * Sets the organisation.
     */
    public static void set(Organisation organisation)
    {
        Organisation existing = get(organisation.getSiteId(), organisation.getCode());
        organisationMap.get(organisation.getSiteId()).put(organisation.getCode(), organisation);
        if(existing != null)
            organisationList.get(organisation.getSiteId()).remove(existing);
        organisationList.get(organisation.getSiteId()).add(organisation);
    }

    /**
     * Returns the organisations for the given site.
     */
    public static List<Organisation> list(Site site)
    {
        return organisationList.get(site.getId());
    }

    /**
     * Returns the organisations with the given code.
     */
    public static List<Organisation> list(String code, boolean includeArchived)
    {
        List<Organisation> ret = new ArrayList<Organisation>();
        for(Site site : Sites.list())
        {
            Organisation organisation = get(site.getId(), code);
            if(organisation != null)
            {
                if(!includeArchived && organisation.isArchived())
                    continue;

                ret.add(organisation);
            }
        }

        return ret;
    }

    /**
     * Returns the organisations with the given code.
     */
    public static List<Organisation> list(String code)
    {
        return list(code, false);
    }

    /**
     * Removes the given organisation.
     */
    public static void remove(Organisation organisation)
    {
        organisation = organisationMap.get(organisation.getSiteId()).remove(organisation.getCode());
        organisationList.get(organisation.getSiteId()).remove(organisation);
    }

    /**
     * Returns the number of organisations.
     */
    public static int size()
    {
        return organisationList.size();
    }
}