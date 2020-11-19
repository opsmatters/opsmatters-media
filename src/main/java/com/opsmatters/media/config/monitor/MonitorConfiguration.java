/*
 * Copyright 2020 Gerald Curley
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
import com.opsmatters.media.config.YamlConfiguration;

/**
 * Class that represents a YAML configuration for the monitoring of a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MonitorConfiguration extends YamlConfiguration
{
    public static final String ACTIVE = "active";
    public static final String INTERVAL = "interval";
    public static final String DIFFERENCE = "difference";
    public static final String SORT = "sort";
    public static final String MAX_RESULTS = "max-results";

    private boolean active = false;
    private int interval = 0;
    private int difference = 0;
    private String sort = "";
    private int maxResults = 0;

    /**
     * Default constructor.
     */
    public MonitorConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public MonitorConfiguration(MonitorConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MonitorConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setActive(obj.isActive());
            setInterval(obj.getInterval());
            setMinDifference(obj.getMinDifference());
            setSort(obj.getSort());
            setMaxResults(obj.getMaxResults());
        }
    }

    /**
     * Returns <CODE>true</CODE> if the monitor is active.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Set to <CODE>true</CODE> if the monitor is active.
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * Returns the interval between monitor checks (in minutes).
     */
    public int getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between monitor checks (in minutes).
     */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the minimum % difference between monitor checks.
     */
    public int getMinDifference()
    {
        return difference;
    }

    /**
     * Sets the minimum % difference between monitor checks.
     */
    public void setMinDifference(int difference)
    {
        this.difference = difference;
    }

    /**
     * Returns the monitor content sort.
     */
    public String getSort()
    {
        return sort;
    }

    /**
     * Sets the monitor content sort.
     */
    public void setSort(String sort)
    {
        this.sort = sort;
    }

    /**
     * Returns the maximum results to be returned by a monitor check.
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * Sets the maximum results to be returned by a monitor check.
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    public void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(ACTIVE))
            setActive((Boolean)map.get(ACTIVE));
        if(map.containsKey(INTERVAL))
            setInterval((Integer)map.get(INTERVAL));
        if(map.containsKey(DIFFERENCE))
            setMinDifference((Integer)map.get(DIFFERENCE));
        if(map.containsKey(SORT))
            setSort((String)map.get(SORT));
        if(map.containsKey(MAX_RESULTS))
            setMaxResults((Integer)map.get(MAX_RESULTS));
    }
}