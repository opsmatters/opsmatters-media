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
package com.opsmatters.media.config.monitor;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.monitor.ContentMonitor;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing the set of content monitors.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitors
{
    private static final Logger logger = Logger.getLogger(ContentMonitors.class.getName());

    private static List<ContentMonitor> monitorList = new ArrayList<ContentMonitor>();
    private static Map<String,ContentMonitor> monitorMap = new HashMap<String,ContentMonitor>();
    private static Map<String,ContentMonitor> guidMap = new HashMap<String,ContentMonitor>();
    private static Map<ContentType,Map<String,ContentMonitor>> monitorTypeMap = new HashMap<ContentType,Map<String,ContentMonitor>>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private ContentMonitors()
    {
    }

    /**
     * Returns <CODE>true</CODE> if monitors have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of monitors.
     */
    public static void load(List<ContentMonitor> monitors)
    {
        initialised = false;

        clear();
        for(ContentMonitor monitor : monitors)
            add(monitor);

        logger.info(String.format("Loaded %d monitors", size()));

        initialised = true;
    }

    /**
     * Organise the monitors by content type.
     */
    public static void populateContentTypes()
    {
        for(ContentType type : ContentType.values())
        {
            if(type != ContentType.ORGANISATION)
                populateContentType(type);
        }
    }

    /**
     * Organise the monitors for the given content type.
     */
    private static void populateContentType(ContentType type)
    {
        Map<String,ContentMonitor> map = monitorTypeMap.get(type);
        if(map == null)
        {
            map = new TreeMap<String,ContentMonitor>();
            monitorTypeMap.put(type, map);
        }

        // Get the config files for the given content type
        for(ContentMonitor monitor : list())
        {
            if(monitor.getContentType() == type)
                map.put(monitor.getId(), monitor);
        }

        if(map.size() > 0)
            logger.info(String.format("Loaded %d %s monitors", 
                map.size(), type != null ? type.value() : "organisation"));
    }

    /**
     * Clears the monitors.
     */
    private static void clear()
    {
        monitorList.clear();
        monitorMap.clear();
        guidMap.clear();
        monitorTypeMap.clear();
    }

    /**
     * Adds the given monitor.
     */
    public static void add(ContentMonitor monitor)
    {
        monitorList.add(monitor);
        monitorMap.put(monitor.getId(), monitor);
        guidMap.put(monitor.getGuid(), monitor);
    }

    /**
     * Returns the monitor for the given id.
     */
    public static ContentMonitor get(String id)
    {
        return monitorMap.get(id);
    }

    /**
     * Returns the monitor for the given guid.
     */
    public static ContentMonitor getByGuid(String guid)
    {
        return guidMap.get(guid);
    }

    /**
     * Sets the given monitor.
     */
    public static void set(ContentMonitor monitor)
    {
        if(monitor != null)
        {
            ContentMonitor existing = get(monitor.getId());
            if(existing != null)
                monitorList.remove(existing);
            add(monitor);
        }
    }

    /**
     * Returns the list of monitors.
     */
    public static List<ContentMonitor> list()
    {
        return monitorList;
    }

    /**
     * Returns the video monitors with the given channel id.
     */
    public static List<ContentMonitor> listByChannelId(String channelId)
    {
        List<ContentMonitor> ret = new ArrayList<ContentMonitor>();

        for(ContentMonitor monitor : monitorList)
        {
            if(monitor.getContentType() == ContentType.VIDEO
                && monitor.getChannelId().equals(channelId))
            {
                ret.add(monitor);
            }
        }

        return ret;
    }

    /**
     * Returns the number of monitors.
     */
    public static int size()
    {
        return monitorList.size();
    }
}