/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.cache.content.util;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.util.ContentProxy;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the set of content proxies.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentProxies extends StaticCache
{
    private static final Logger logger = Logger.getLogger(ContentProxies.class.getName());

    private static Map<String,ContentProxy> proxyMap = new LinkedHashMap<String,ContentProxy>();
    private static List<ContentProxy> proxyList = new ArrayList<ContentProxy>();
    private static Iterator<ContentProxy> iterator = null;

    /**
     * Private constructor.
     */
    private ContentProxies()
    {
    }

    /**
     * Loads the set of proxies.
     */
    public static void load(List<ContentProxy> proxies)
    {
        setInitialised(false);

        clear();
        for(ContentProxy proxy : proxies)
        {
            if(proxy.isActive())
            {
                add(proxy);
            }
        }

        setIterator();

        logger.info("Loaded "+size()+" content proxies");

        setInitialised(true);
    }

    /**
     * Clears the proxies.
     */
    public static void clear()
    {
        proxyMap.clear();
        proxyList.clear();
    }

    /**
     * Adds the given proxy.
     */
    private static void add(ContentProxy proxy)
    {
        proxyMap.put(proxy.getId(), proxy);
        proxyList.add(proxy);
    }

    /**
     * Removes the given proxy.
     */
    public static void remove(ContentProxy proxy)
    {
        proxyMap.remove(proxy.getId());
    }

    /**
     * Returns the list of proxies.
     */
    public static List<ContentProxy> list()
    {
        return proxyList;
    }

    /**
     * Resets the iterator.
     */
    private static void setIterator()
    {
        if(size() > 0)
            iterator = proxyList.iterator();
    }

    /**
     * Resets the iterator if it has reached the end.
     */
    private static void checkNext()
    {
        // Go back to the beginning if none left
        if(!iterator.hasNext())
            setIterator();
    }

    /**
     * Returns the next proxy from the iterator.
     */
    public static synchronized ContentProxy next()
    {
        ContentProxy ret = null;
        if(iterator != null)
        {
            checkNext();
            ret = iterator.next();
        }

        return ret;
    }

    /**
     * Returns the count of proxies.
     */
    public static int size()
    {
        return proxyList.size();
    }
}