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
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import com.opsmatters.media.model.provider.ImageProvider;
import com.opsmatters.media.model.provider.ImageProviderType;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the list of image providers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageProviders extends StaticCache
{
    private static final Logger logger = Logger.getLogger(ImageProviders.class.getName());

    private static Map<String,ImageProvider> providerMap = new LinkedHashMap<String,ImageProvider>();

    private static Comparator comparator = new Comparator<ImageProvider>()
    {
        public int compare(ImageProvider arg1, ImageProvider arg2)
        {
            return arg1.getTag().compareTo(arg2.getTag());
        }
    };

    /**
     * Private constructor.
     */
    private ImageProviders()
    {
    }

    /**
     * Loads the set of providers.
     */
    public static void load(List<ImageProvider> providers)
    {
        setInitialised(false);

        clear();
        for(ImageProvider provider : providers)
        {
            add(provider);
        }

        logger.info("Loaded "+size()+" image providers");

        setInitialised(true);
    }

    /**
     * Clears the image providers.
     */
    public static void clear()
    {
        providerMap.clear();
    }

    /**
     * Returns the image provider with the given code.
     */
    public static ImageProvider get(String code)
    {
        return providerMap.get(code);
    }

    /**
     * Adds the image provider with the given code.
     */
    public static void add(ImageProvider provider)
    {
        providerMap.put(provider.getCode(), provider);
    }

    /**
     * Removes the image provider with the given code.
     */
    public static void remove(ImageProvider provider)
    {
        providerMap.remove(provider.getCode());
    }

    /**
     * Returns the list of image providers.
     */
    public static List<ImageProvider> list()
    {
        return new ArrayList<ImageProvider>(providerMap.values());
    }

    /**
     * Returns the list of image providers for the given type.
     */
    public static List<ImageProvider> list(ImageProviderType type)
    {
        List<ImageProvider> ret = new ArrayList<ImageProvider>();
        for(ImageProvider provider : providerMap.values())
        {
            if(provider.getType() == type && provider.isActive())
            {
                ret.add(provider);
            }
        }

        Collections.sort(ret, comparator);

        return ret;
    }

    /**
     * Returns the count of image providers.
     */
    public static int size()
    {
        return providerMap.size();
    }

    /**
     * Returns the provider for the given filename.
     * @param filename The filename to be checked
     * @return the provider for the given filename
     */
    public static ImageProvider getByFilename(String filename)
    {
        ImageProvider ret = null;
        if(filename != null)
        {
            for(ImageProvider provider : providerMap.values())
            {
                if(provider.matchesFilename(filename))
                {
                    ret = provider;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given filename matches an image provider.
     * @param text The filename to be checked
     * @return <CODE>true</CODE> if the given filename matches an image provider
     */
    public static boolean matchesFilename(String filename)
    {
        return getByFilename(filename) != null;
    }
}