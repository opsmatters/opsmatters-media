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
package com.opsmatters.media.cache.social;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.cache.platform.Sites;
import com.opsmatters.media.model.platform.Site;

/**
 * Class representing the list of social media hashtags.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Hashtags implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Hashtags.class.getName());

    private static final String GLOBAL = "GLOBAL";

    private static Map<String,List<String>> hashtagMap = new LinkedHashMap<String,List<String>>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private Hashtags()
    {
    }

    /**
     * Returns <CODE>true</CODE> if hashtags have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of hashtags for each site.
     */
    public static void load(Map<String,List<String>> hashtags)
    {
        initialised = false;

        clear();

        set(GLOBAL, hashtags.get(GLOBAL));
        logger.info(String.format("Loaded %d %s hashtags",
            size(GLOBAL), GLOBAL));

        for(Site site : Sites.list())
        {
            set(site.getId(), hashtags.get(site.getId()));
            logger.info(String.format("Loaded %d hashtags for site %s",
                size(site.getId()), site.getName()));
        }

        initialised = true;
    }

    /**
     * Clears the hashtags.
     */
    public static void clear()
    {
        hashtagMap.clear();
    }

    /**
     * Returns the list of hashtags for the given site.
     * <P>
     * Includes global hashtags too.
     */
    public static List<String> list(String siteId, boolean global)
    {
        List<String> ret = new ArrayList<String>();
        ret.addAll(hashtagMap.get(GLOBAL));
        ret.addAll(hashtagMap.get(siteId));
        return ret;
    }

    /**
     * Sets the hashtags for the given site.
     */
    public static void set(String siteId, List<String> hashtags)
    {
        List<String> list = new ArrayList<String>();
        for(String hashtag : hashtags)
            list.add("#"+hashtag);
        hashtagMap.put(siteId, list);
    }

    /**
     * Returns the count of hashtags for the given site.
     */
    public static int size(String siteId)
    {
        return hashtagMap.get(siteId).size();
    }
}