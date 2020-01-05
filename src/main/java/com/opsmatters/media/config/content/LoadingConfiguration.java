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
 * Class that represents a YAML configuration for the loading of a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LoadingConfiguration extends YamlConfiguration
{
    public static final String WAIT = "wait";
    public static final String SELECTOR = "selector";
    public static final String INTERVAL = "interval";
    public static final String MAX_WAIT = "max-wait";
    public static final String JAVASCRIPT = "javascript";
    public static final String REMOVE_PARAMETERS = "remove-parameters";

    private long wait = 0L;
    private String selector = "";
    private long interval = 0L;
    private long maxWait = 0L;
    private boolean javascript = false;
    private boolean removeParameters = true;

    /**
     * Default constructor.
     */
    public LoadingConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public LoadingConfiguration(LoadingConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(LoadingConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSelector(obj.getSelector());
            setInterval(obj.getInterval());
            setWait(obj.getWait());
            setMaxWait(obj.getMaxWait());
            setJavaScriptEnabled(obj.isJavaScriptEnabled());
            setRemoveParameters(obj.removeParameters());
        }
    }

    /**
     * Returns the wait time after page loading (in milliseconds).
     */
    public long getWait()
    {
        return wait;
    }

    /**
     * Sets the wait time after page loading (in milliseconds).
     */
    public void setWait(long wait)
    {
        this.wait = wait;
    }

    /**
     * Returns the selector to look for when page loading.
     */
    public String getSelector()
    {
        return selector;
    }

    /**
     * Sets the selector to look for when page loading.
     */
    public void setSelector(String selector)
    {
        this.selector = selector;
    }

    /**
     * Returns the interval between selector queries when page loading.
     */
    public long getInterval()
    {
        return interval;
    }

    /**
     * Sets the interval between selector queries when page loading.
     */
    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    /**
     * Returns the maximum time to wait for selector queries when page loading.
     */
    public long getMaxWait()
    {
        return maxWait;
    }

    /**
     * Sets the maximum time to wait for selector queries when page loading.
     */
    public void setMaxWait(long maxWait)
    {
        this.maxWait = maxWait;
    }

    /**
     * Returns <CODE>true</CODE> if javascript is enabled for the page loading.
     */
    public boolean isJavaScriptEnabled()
    {
        return javascript;
    }

    /**
     * Set to <CODE>true</CODE> if javascript is enabled for the page loading.
     */
    public void setJavaScriptEnabled(boolean javascript)
    {
        this.javascript = javascript;
    }

    /**
     * Returns <CODE>true</CODE> if query parameters should be removed from the URL.
     */
    public boolean removeParameters()
    {
        return removeParameters;
    }

    /**
     * Set to <CODE>true</CODE> if query parameters should be removed from the URL.
     */
    public void setRemoveParameters(boolean removeParameters)
    {
        this.removeParameters = removeParameters;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            if(map.containsKey(WAIT))
                setWait((Integer)map.get(WAIT));
            if(map.containsKey(SELECTOR))
                setSelector((String)map.get(SELECTOR));
            if(map.containsKey(INTERVAL))
                setInterval((Integer)map.get(INTERVAL));
            if(map.containsKey(MAX_WAIT))
                setMaxWait((Integer)map.get(MAX_WAIT));
            if(map.containsKey(JAVASCRIPT))
                setJavaScriptEnabled((Boolean)map.get(JAVASCRIPT));
            if(map.containsKey(REMOVE_PARAMETERS))
                setRemoveParameters((Boolean)map.get(REMOVE_PARAMETERS));
        }
    }
}