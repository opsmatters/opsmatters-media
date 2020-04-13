/*
 * Copyright 2019 Gerald Curley
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
package com.opsmatters.media.config.content;

import java.util.Map;
import com.opsmatters.media.config.YamlConfiguration;

/**
 * Class that represents a YAML configuration for a Read More link on a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MoreLinkConfiguration extends YamlConfiguration
{
    public static final String SELECTOR = "selector";
    public static final String INTERVAL = "interval";
    public static final String MAX_WAIT = "max-wait";
    public static final String COUNT = "count";

    private String selector = "";
    private long interval = 0L;
    private long maxWait = 0L;
    private int count = 1;

    /**
     * Constructor that takes a selector.
     */
    public MoreLinkConfiguration(String name, String selector)
    {
        super(name);
        setSelector(selector);
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public MoreLinkConfiguration(String name, Map<String, Object> map)
    {
        super(name);
        parseDocument(map);
    }

    /**
     * Copy constructor.
     */
    public MoreLinkConfiguration(MoreLinkConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MoreLinkConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSelector(obj.getSelector());
            setInterval(obj.getInterval());
            setMaxWait(obj.getMaxWait());
            setCount(obj.getCount());
        }
    }

    /**
     * Returns the selector for the more link.
     */
    public String getSelector()
    {
        return selector;
    }

    /**
     * Sets the selector for the more link.
     */
    public void setSelector(String selector)
    {
        this.selector = selector;
    }

    /**
     * Returns the number of times to click the more link.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets the number of times to click the more link.
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Returns the interval between selector queries.
     */
    public long getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between selector queries.
     */
    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the maximum wait time before each click (in milliseconds).
     */
    public long getMaxWait()
    {
        return maxWait;
    }

    /**
     * Sets the maximum wait time before each click (in milliseconds).
     */
    public void setMaxWait(long maxWait)
    {
        this.maxWait = maxWait;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(SELECTOR))
            setSelector((String)map.get(SELECTOR));
        if(map.containsKey(INTERVAL))
            setInterval((Integer)map.get(INTERVAL));
        if(map.containsKey(MAX_WAIT))
            setMaxWait((Integer)map.get(MAX_WAIT));
        if(map.containsKey(COUNT))
            setCount((Integer)map.get(COUNT));
    }
}