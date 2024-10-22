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
package com.opsmatters.media.cache.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.EventPlatform;

/**
 * Class representing the list of event platforms.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventPlatforms implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(EventPlatforms.class.getName());

    private static Map<String,EventPlatform> platformMap = new LinkedHashMap<String,EventPlatform>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private EventPlatforms()
    {
    }

    /**
     * Returns <CODE>true</CODE> if platforms have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of platforms.
     */
    public static void load(List<EventPlatform> platforms)
    {
        initialised = false;

        clear();
        for(EventPlatform platform : platforms)
        {
            add(platform);
        }

        logger.info("Loaded "+size()+" event platforms");

        initialised = true;
    }

    /**
     * Clears the event platforms.
     */
    public static void clear()
    {
        platformMap.clear();
    }

    /**
     * Returns the event platform with the given code.
     */
    public static EventPlatform get(String code)
    {
        return platformMap.get(code);
    }

    /**
     * Returns the platform for the given url.
     */
    public static EventPlatform getByUrl(String url)
    {
        EventPlatform ret = null;

        for(EventPlatform platform : platformMap.values())
        {
            if(url.indexOf(platform.getDomain()) != -1
                && platform.isActive())
            {
                ret = platform;
                break;
            }
        }

        return ret;
    }

    /**
     * Adds the event platform with the given code.
     */
    public static void add(EventPlatform platform)
    {
        platformMap.put(platform.getCode(), platform);
    }

    /**
     * Removes the event platform with the given code.
     */
    public static void remove(EventPlatform platform)
    {
        platformMap.remove(platform.getCode());
    }

    /**
     * Returns the list of event platforms.
     */
    public static List<EventPlatform> list()
    {
        return new ArrayList<EventPlatform>(platformMap.values());
    }

    /**
     * Returns the count of event platforms.
     */
    public static int size()
    {
        return platformMap.size();
    }
}