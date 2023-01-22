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
import java.util.Iterator;
import java.util.logging.Logger;
import com.opsmatters.media.cache.platform.Sites;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.social.Hashtag;

/**
 * Class representing the list of social media hashtags.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Hashtags implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(Hashtags.class.getName());

    private static List<Hashtag> hashtagList = new ArrayList<Hashtag>();
    private static Map<String,List<Hashtag>> hashtagMap = new LinkedHashMap<String,List<Hashtag>>();

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
    public static void load(List<Hashtag> hashtags)
    {
        initialised = false;

        clear();

        for(Site site : Sites.list())
        {
            for(Hashtag hashtag : hashtags)
            {
                if(hashtag.hasSite(site))
                    add(site.getId(), hashtag);
            }

            logger.info(String.format("Loaded %d hashtags for site %s",
                size(site.getId()), site.getName()));
        }

        logger.info(String.format("Loaded %d hashtags", size()));

        initialised = true;
    }

    /**
     * Clears the hashtags.
     */
    public static void clear()
    {
        hashtagList.clear();
        hashtagMap.clear();
    }

    /**
     * Returns the hashtag with the given name.
     */
    public static Hashtag get(String name)
    {
        Hashtag ret = null;
        for(Hashtag hashtag : hashtagList)
        {
            if(hashtag.getName().equals(name))
            {
                ret = hashtag;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the list of all hashtags.
     */
    public static List<Hashtag> list()
    {
        return hashtagList;
    }

    /**
     * Returns the list of hashtags for the given site.
     */
    public static List<Hashtag> list(String siteId)
    {
        return hashtagMap.get(siteId);
    }

    /**
     * Returns the list of hashtags for the given site.
     */
    public static List<Hashtag> list(Site site)
    {
        return list(site.getId());
    }

    /**
     * Adds the given hashtag for the given site.
     */
    private static void add(String siteId, Hashtag hashtag)
    {
        if(hashtag.isActive())
        {
            List<Hashtag> list = hashtagMap.get(siteId);
            if(list == null)
            {
                list = new ArrayList<Hashtag>();
                hashtagMap.put(siteId, list);
            }

            list.add(hashtag);
        }

        if(!hashtagList.contains(hashtag))
            hashtagList.add(hashtag);
    }

    /**
     * Adds the given hashtag.
     */
    public static void add(Hashtag hashtag)
    {
        for(String siteId : hashtag.getSiteList())
            add(siteId, hashtag);
    }

    /**
     * Removes the given hashtag.
     */
    public static void remove(Hashtag hashtag)
    {
        for(Map.Entry<String,List<Hashtag>> entry : hashtagMap.entrySet())
        {
            if(hashtag.hasSite(entry.getKey()))
            {
                Iterator<Hashtag> iterator = entry.getValue().iterator();
                while(iterator.hasNext())
                {
                    Hashtag item = iterator.next();
                    if(item.getName().equals(hashtag.getName()))
                    {
                        iterator.remove();
                    }
                }
            }
        }

        hashtagList.remove(hashtag);
    }

    /**
     * Returns the count of hashtags for the given site.
     */
    public static int size(String siteId)
    {
        List<Hashtag> list = hashtagMap.get(siteId);
        return list != null ? list.size() : -1;
    }

    /**
     * Returns the count of hashtags.
     */
    public static int size()
    {
        return hashtagList.size();
    }
}