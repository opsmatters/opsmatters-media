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
package com.opsmatters.media.config.platform;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.platform.Site;

/**
 * Class representing the set of sites.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Sites
{
    private static final Logger logger = Logger.getLogger(Sites.class.getName());

    private static Map<String,Site> siteMap = new LinkedHashMap<String,Site>();
    private static List<Site> siteList = new ArrayList<Site>();
    private static List<String> idList = new ArrayList<String>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Sites()
    {
    }

    /**
     * Returns <CODE>true</CODE> if sites have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of sites.
     */
    public static void load(List<Site> sites)
    {
        initialised = false;

        clear();
        for(Site site : sites)
            add(site);

        logger.info("Loaded "+size()+" sites");

        initialised = true;
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
    private static void add(Site site)
    {
        siteMap.put(site.getId(), site);
        siteList.add(site);
        idList.add(site.getId());
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
     * Returns the list of site ids.
     */
    public static List<String> listIds()
    {
        return idList;
    }

    /**
     * Returns the number of sites.
     */
    public static int size()
    {
        return siteList.size();
    }
}