/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.cache.provider;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.provider.EventProvider;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the list of event providers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventProviders extends StaticCache
{
    private static final Logger logger = Logger.getLogger(EventProviders.class.getName());

    private static Map<String,EventProvider> providerMap = new LinkedHashMap<String,EventProvider>();

    /**
     * Private constructor.
     */
    private EventProviders()
    {
    }

    /**
     * Loads the set of providers.
     */
    public static void load(List<EventProvider> providers)
    {
        setInitialised(false);

        clear();
        for(EventProvider provider : providers)
        {
            add(provider);
        }

        logger.info("Loaded "+size()+" event providers");

        setInitialised(true);
    }

    /**
     * Clears the event providers.
     */
    public static void clear()
    {
        providerMap.clear();
    }

    /**
     * Returns the event provider with the given code.
     */
    public static EventProvider get(String code)
    {
        return providerMap.get(code);
    }

    /**
     * Returns the provider for the given url.
     */
    public static EventProvider getByUrl(String url)
    {
        EventProvider ret = null;

        for(EventProvider provider : providerMap.values())
        {
            if(url.indexOf(provider.getDomain()) != -1
                && provider.isActive())
            {
                ret = provider;
                break;
            }
        }

        return ret;
    }

    /**
     * Adds the event provider with the given code.
     */
    public static void add(EventProvider provider)
    {
        providerMap.put(provider.getCode(), provider);
    }

    /**
     * Removes the event provider with the given code.
     */
    public static void remove(EventProvider provider)
    {
        providerMap.remove(provider.getCode());
    }

    /**
     * Returns the list of event providers.
     */
    public static List<EventProvider> list()
    {
        return new ArrayList<EventProvider>(providerMap.values());
    }

    /**
     * Returns the count of event providers.
     */
    public static int size()
    {
        return providerMap.size();
    }
}