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
package com.opsmatters.media.cache.provider;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.provider.SocialProvider;
import com.opsmatters.media.model.provider.SocialProviderId;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the list of social media providers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialProviders extends StaticCache
{
    private static final Logger logger = Logger.getLogger(SocialProviders.class.getName());

    private static Map<String,SocialProvider> providerMap = new LinkedHashMap<String,SocialProvider>();

    /**
     * Private constructor.
     */
    private SocialProviders()
    {
    }

    /**
     * Loads the set of providers.
     */
    public static void load(List<SocialProvider> providers)
    {
        setInitialised(false);

        clear();
        for(SocialProvider provider : providers)
        {
            add(provider);
        }

        logger.info("Loaded "+size()+" social providers");

        setInitialised(true);
    }

    /**
     * Clears the social providers.
     */
    public static void clear()
    {
        providerMap.clear();
    }

    /**
     * Returns the social provider with the given code.
     */
    public static SocialProvider get(String code)
    {
        return providerMap.get(code);
    }

    /**
     * Returns the social provider with the given id.
     */
    public static SocialProvider get(SocialProviderId provider)
    {
        return provider != null ? providerMap.get(provider.code()) : null;
    }

    /**
     * Adds the social provider with the given code.
     */
    public static void add(SocialProvider provider)
    {
        providerMap.put(provider.getCode(), provider);
    }

    /**
     * Removes the social provider with the given code.
     */
    public static void remove(SocialProvider provider)
    {
        providerMap.remove(provider.getCode());
    }

    /**
     * Returns the list of social providers.
     */
    public static List<SocialProvider> list()
    {
        return new ArrayList<SocialProvider>(providerMap.values());
    }

    /**
     * Returns the count of social providers.
     */
    public static int size()
    {
        return providerMap.size();
    }
}