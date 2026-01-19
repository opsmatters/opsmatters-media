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
package com.opsmatters.media.cache.system;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.system.SiteConfig;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the set of sites.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Sites extends StaticCache
{
    private static final Logger logger = Logger.getLogger(Sites.class.getName());

    private static Map<String,Site> siteMap = new LinkedHashMap<String,Site>();
    private static List<Site> siteList = new ArrayList<Site>();
    private static List<String> idList = new ArrayList<String>();

    /**
     * Private constructor.
     */
    private Sites()
    {
    }

    /**
     * Loads the set of sites.
     */
    public static void load(List<Site> sites)
    {
        setInitialised(false);

        clear();
        for(Site site : sites)
        {
            add(site);
        }

        logger.info("Loaded "+size()+" sites");

        setInitialised(true);
    }

    /**
     * Loads the set of site configs.
     */
    public static void loadConfigs(List<SiteConfig> configs)
    {
        int count = 0;
        for(SiteConfig config : configs)
        {
            add(config);
            ++count;
        }

        logger.info("Loaded "+count+" site configs");
    }

    /**
     * Clears the sites.
     */
    private static void clear()
    {
        siteMap.clear();
        siteList.clear();
        idList.clear();
    }

    /**
     * Adds the given site.
     */
    public static void add(Site site)
    {
        Site existing = siteMap.get(site.getId());
        siteMap.put(site.getId(), site);
        if(existing != null)
            siteList.remove(existing);
        siteList.add(site);
        idList.add(site.getId());
    }

    /**
     * Adds the given site config.
     */
    public static void add(SiteConfig config)
    {
        Site site = siteMap.get(config.getId());
        if(site != null)
            site.setConfig(config);
    }

    /**
     * Returns the site for the given id.
     */
    public static Site get(String id)
    {
        return siteMap.get(id);
    }

    /**
     * Returns the site at the given index.
     */
    public static Site get(int idx)
    {
        return siteList.size() > idx ? siteList.get(idx) : null;
    }

    /**
     * Returns the list of sites.
     */
    public static List<Site> list()
    {
        return siteList;
    }

    /**
     * Returns the list of sites.
     */
    public static List<Site> list(boolean blank)
    {
        List<Site> ret = new ArrayList<Site>();
        if(blank)
            ret.add(null);
        ret.addAll(siteList);
        return ret;
    }

    /**
     * Returns the list of site ids.
     */
    public static List<String> listIds()
    {
        return idList;
    }

    /**
     * Removes the site with the given id.
     */
    public static void remove(Site site)
    {
        Site existing = siteMap.get(site.getId());
        if(existing != null)
        {
            siteMap.remove(existing.getId());
            siteList.remove(existing);
            idList.remove(existing.getId());
        }
    }

    /**
     * Returns the number of sites.
     */
    public static int size()
    {
        return siteList.size();
    }
}