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
import com.opsmatters.media.cache.platform.Sites;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.ContentSiteSettings;

/**
 * Class representing the set of organisation sites.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationSites
{
    private static final Logger logger = Logger.getLogger(OrganisationSites.class.getName());

    private static Map<String,List<OrganisationSite>> organisationList = new HashMap<String,List<OrganisationSite>>();
    private static Map<String,Map<String,OrganisationSite>> organisationMap = new HashMap<String,Map<String,OrganisationSite>>();
    private static List<ContentSiteSettings> settings = new ArrayList<ContentSiteSettings>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private OrganisationSites()
    {
    }

    /**
     * Returns <CODE>true</CODE> if organisation sites have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of organisation sites.
     */
    public static void load(List<OrganisationSite> organisations)
    {
        initialised = false;

        for(Site site : Sites.list())
        {
            if(site.isEnabled())
            {
                List<OrganisationSite> list = organisationList.get(site.getId());
                if(list == null)
                {
                    list = new ArrayList<OrganisationSite>();
                    organisationList.put(site.getId(), list);
                }

                Map<String,OrganisationSite> map = organisationMap.get(site.getId());
                if(map == null)
                {
                    map = new TreeMap<String,OrganisationSite>();
                    organisationMap.put(site.getId(), map);
                }

                for(OrganisationSite organisation : organisations)
                {
                    if(organisation.getSiteId().equals(site.getId()))
                        add(organisation);
                }

                logger.info(String.format("Loaded %d organisation sites for site %s",
                    list.size(), site.getName()));
            }
        }

        initialised = true;
    }

    /**
     * Load the set of content settings.
     */
    public static void add(List<ContentSiteSettings> settings)
    {
        OrganisationSites.settings.addAll(settings);
    }

    /**
     * Clears the organisation sites.
     */
    public static void clear()
    {
        organisationMap.clear();
        organisationList.clear();
        settings.clear();
    }

    /**
     * Adds the given organisation site.
     */
    private static void add(OrganisationSite organisation)
    {
        organisationMap.get(organisation.getSiteId()).put(organisation.getCode(), organisation);
        organisationList.get(organisation.getSiteId()).add(organisation);
        for(ContentSiteSettings settings : OrganisationSites.settings)
        {
            if(settings.getSiteId().equals(organisation.getSiteId())
                && settings.getCode().equals(organisation.getCode()))
            {
                organisation.setSettings(settings);
            }
        }
    }

    /**
     * Returns the organisation site with the given code.
     */
    public static OrganisationSite get(Site site, String code)
    {
        return get(site.getId(), code);
    }

    /**
     * Returns the organisation site with the given code.
     */
    public static OrganisationSite get(String siteId, String code)
    {
        Map<String,OrganisationSite> map = organisationMap.get(siteId);
        return map != null && code != null ? map.get(code) : null;
    }

    /**
     * Returns the first organisation with the given code.
     */
    public static OrganisationSite get(String code)
    {
        OrganisationSite ret = null;
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
    public static OrganisationSite getByName(Site site, String name)
    {
        OrganisationSite ret = null;
        if(name != null)
        {
            name = name.toLowerCase();
            for(OrganisationSite organisationSite : organisationList.get(site.getId()))
            {
                Organisation organisation = Organisations.get(organisationSite.getCode());
                if(organisation.getName().toLowerCase().equals(name))
                {
                    ret = organisationSite;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given organisation already exists.
     */
    public static boolean exists(OrganisationSite organisationSite)
    {
        OrganisationSite existing = get(organisationSite.getSiteId(), organisationSite.getCode());
        return existing != null ? !existing.getId().equals(organisationSite.getId()) : false;
    }

    /**
     * Returns the content settings for the given organisation site and content type.
     */
    public static ContentSiteSettings getSettings(String siteId, String code, ContentType type)
    {
        ContentSiteSettings ret = null;
        OrganisationSite organisation = OrganisationSites.get(siteId, code);
        if(organisation != null)
        {
            ret = organisation.getSettings(type);
        }

        return ret;
    }

    /**
     * Returns the content settings for the given configuration.
     */
    public static ContentSiteSettings getSettings(String siteId, ContentConfig config)
    {
        return getSettings(siteId, config.getCode(), config.getType());
    }

    /**
     * Returns the content settings for the given configuration.
     */
    public static ContentSiteSettings getSettings(Site site, ContentConfig config)
    {
        return getSettings(site.getId(), config);
    }

    /**
     * Sets the organisation site.
     */
    public static void set(OrganisationSite organisation)
    {
        OrganisationSite existing = get(organisation.getSiteId(), organisation.getCode());
        organisationMap.get(organisation.getSiteId()).put(organisation.getCode(), organisation);
        if(existing != null)
            organisationList.get(organisation.getSiteId()).remove(existing);
        organisationList.get(organisation.getSiteId()).add(organisation);
    }

    /**
     * Returns the organisation sites.
     */
    public static List<OrganisationSite> list(Site site)
    {
        return organisationList.get(site.getId());
    }

    /**
     * Returns the organisations with the given code.
     */
    public static List<OrganisationSite> list(String code, boolean includeArchived)
    {
        List<OrganisationSite> ret = new ArrayList<OrganisationSite>();
        for(Site site : Sites.list())
        {
            OrganisationSite organisation = get(site.getId(), code);
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
    public static List<OrganisationSite> list(String code)
    {
        return list(code, false);
    }

    /**
     * Removes the given organisation site.
     */
    public static void remove(OrganisationSite organisation)
    {
        organisation = organisationMap.get(organisation.getSiteId()).remove(organisation.getCode());
        organisationList.get(organisation.getSiteId()).remove(organisation);
    }

    /**
     * Returns the number of organisation sites.
     */
    public static int size()
    {
        return organisationList.size();
    }
}