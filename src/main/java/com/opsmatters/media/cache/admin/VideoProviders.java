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
package com.opsmatters.media.cache.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.VideoProvider;
import com.opsmatters.media.model.admin.VideoProviderId;

/**
 * Class representing the list of video providers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoProviders implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(VideoProviders.class.getName());

    private static Map<String,VideoProvider> providerMap = new LinkedHashMap<String,VideoProvider>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private VideoProviders()
    {
    }

    /**
     * Returns <CODE>true</CODE> if providers have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of providers.
     */
    public static void load(List<VideoProvider> providers)
    {
        initialised = false;

        clear();
        for(VideoProvider provider : providers)
        {
            add(provider);
        }

        logger.info("Loaded "+size()+" video providers");

        initialised = true;
    }

    /**
     * Clears the video providers.
     */
    public static void clear()
    {
        providerMap.clear();
    }

    /**
     * Returns the video provider with the given code.
     */
    public static VideoProvider get(String code)
    {
        return providerMap.get(code);
    }

    /**
     * Returns the video provider with the given id.
     */
    public static VideoProvider get(VideoProviderId provider)
    {
        return provider != null ? providerMap.get(provider.code()) : null;
    }


    /**
     * Returns the provider for the given name.
     */
    public static VideoProvider getByName(String name)
    {
        VideoProvider ret = null;

        for(VideoProvider provider : providerMap.values())
        {
            if(provider.getName().equals(name))
            {
                ret = provider;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the provider for the given tag.
     */
    public static VideoProvider getByTag(String tag)
    {
        VideoProvider ret = null;

        for(VideoProvider provider : providerMap.values())
        {
            if(provider.getTag().equals(tag))
            {
                ret = provider;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the provider with a tag that matches the given url.
     */
    public static VideoProvider matchesTag(String url)
    {
        VideoProvider ret = null;

        for(VideoProvider provider : providerMap.values())
        {
            if(url.indexOf(provider.getTag()) != -1
                && provider.isActive())
            {
                ret = provider;
                break;
            }
        }

        return ret;
    }

    /**
     * Adds the video provider with the given code.
     */
    public static void add(VideoProvider provider)
    {
        providerMap.put(provider.getCode(), provider);
    }

    /**
     * Removes the video provider with the given code.
     */
    public static void remove(VideoProvider provider)
    {
        providerMap.remove(provider.getCode());
    }

    /**
     * Returns the list of video providers.
     */
    public static List<VideoProvider> list()
    {
        return new ArrayList<VideoProvider>(providerMap.values());
    }

    /**
     * Returns the count of video providers.
     */
    public static int size()
    {
        return providerMap.size();
    }
}