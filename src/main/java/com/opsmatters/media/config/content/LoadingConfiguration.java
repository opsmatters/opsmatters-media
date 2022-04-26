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
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.config.YamlConfiguration;

/**
 * Class that represents a YAML configuration for the loading of a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LoadingConfiguration extends YamlConfiguration
{
    public static final String WAIT = "wait";
    public static final String SLEEP = "sleep";
    public static final String SELECTOR = "selector";
    public static final String INTERVAL = "interval";
    public static final String MAX_WAIT = "max-wait";
    public static final String REMOVE_PARAMETERS = "remove-parameters";
    public static final String TRAILING_SLASH = "trailing-slash";
    public static final String ANTI_CACHE = "anti-cache";
    public static final String KEYWORDS = "keywords";
    public static final String SCROLL_X = "scroll-x";
    public static final String SCROLL_Y = "scroll-y";
    public static final String MOVE_TO = "move-to";

    private long wait = 0L;
    private long sleep = 0L;
    private String selector = "";
    private long interval = 0L;
    private long maxWait = 0L;
    private boolean removeParameters = true;
    private boolean trailingSlash = false;
    private boolean antiCache = false;
    private String keywords = "";
    private List<String> keywordList;
    private int scrollX = 0;
    private int scrollY = 0;
    private String moveTo = "";

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
            setSleep(obj.getSleep());
            setWait(obj.getWait());
            setMaxWait(obj.getMaxWait());
            setRemoveParameters(obj.removeParameters());
            setTrailingSlash(obj.hasTrailingSlash());
            setAntiCache(obj.isAntiCache());
            setKeywords(obj.getKeywords());
            setScrollX(obj.getScrollX());
            setScrollY(obj.getScrollY());
            setMoveTo(obj.getMoveTo());
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
     * Returns the sleep time after page loading (in milliseconds).
     */
    public long getSleep()
    {
        return sleep;
    }

    /**
     * Sets the sleep time after page loading (in milliseconds).
     */
    public void setSleep(long sleep)
    {
        this.sleep = sleep;
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
     * Returns <CODE>true</CODE> if the URL should have a trailing slash.
     */
    public boolean hasTrailingSlash()
    {
        return trailingSlash;
    }

    /**
     * Set to <CODE>true</CODE> if the URL should have a trailing slash.
     */
    public void setTrailingSlash(boolean trailingSlash)
    {
        this.trailingSlash = trailingSlash;
    }

    /**
     * Returns <CODE>true</CODE> if a timestamp parameter should be appended to the URL to prevent caching.
     */
    public boolean isAntiCache()
    {
        return antiCache;
    }

    /**
     * Set to <CODE>true</CODE> if a timestamp parameter should be appended to the URL to prevent caching.
     */
    public void setAntiCache(boolean antiCache)
    {
        this.antiCache = antiCache;
    }

    /**
     * Returns the keywords to filter on when page loading.
     */
    public String getKeywords()
    {
        return keywords;
    }

    /**
     * Returns the list of keywords to filter on when page loading.
     */
    public List<String> getKeywordList()
    {
        return keywordList;
    }

    /**
     * Sets the keywords to filter on when page loading.
     */
    public void setKeywords(String keywords)
    {
        this.keywords = keywords;

        // Parse the keywords
        if(hasKeywords())
        {
            if(keywordList == null)
                keywordList = new ArrayList<String>();
            keywordList.clear();
            String[] array = keywords.split(",");
            for(String keyword : array)
            {
                String str = keyword.toLowerCase().trim();
                if(str.length() > 0)
                    keywordList.add(str);
            }
        }
    }

    /**
     * Returns <CODE>true</CODE> if there are keywords to filter on when page loading.
     */
    public boolean hasKeywords()
    {
        return keywords != null && keywords.length() > 0;
    }

    /**
     * Returns how much to scroll the page in the x direction after loading (in pixels).
     */
    public int getScrollX()
    {
        return scrollX;
    }

    /**
     * Sets how much to scroll the page in the x direction after loading (in pixels).
     */
    public void setScrollX(int scrollX)
    {
        this.scrollX = scrollX;
    }

    /**
     * Returns how much to scroll the page in the y direction after loading (in pixels).
     */
    public int getScrollY()
    {
        return scrollY;
    }

    /**
     * Sets how much to scroll the page in the y direction after loading (in pixels).
     */
    public void setScrollY(int scrollY)
    {
        this.scrollY = scrollY;
    }

    /**
     * Returns the css selector of an element to scroll and move to.
     */
    public String getMoveTo()
    {
        return moveTo;
    }

    /**
     * Sets the css selector of an element to scroll and move to.
     */
    public void setMoveTo(String moveTo)
    {
        this.moveTo = moveTo;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(WAIT))
            setWait((Integer)map.get(WAIT));
        if(map.containsKey(SLEEP))
            setSleep((Integer)map.get(SLEEP));
        if(map.containsKey(SELECTOR))
            setSelector((String)map.get(SELECTOR));
        if(map.containsKey(INTERVAL))
            setInterval((Integer)map.get(INTERVAL));
        if(map.containsKey(MAX_WAIT))
            setMaxWait((Integer)map.get(MAX_WAIT));
        if(map.containsKey(REMOVE_PARAMETERS))
            setRemoveParameters((Boolean)map.get(REMOVE_PARAMETERS));
        if(map.containsKey(TRAILING_SLASH))
            setTrailingSlash((Boolean)map.get(TRAILING_SLASH));
        if(map.containsKey(ANTI_CACHE))
            setAntiCache((Boolean)map.get(ANTI_CACHE));
        if(map.containsKey(KEYWORDS))
            setKeywords((String)map.get(KEYWORDS));
        if(map.containsKey(SCROLL_X))
            setScrollX((Integer)map.get(SCROLL_X));
        if(map.containsKey(SCROLL_Y))
            setScrollY((Integer)map.get(SCROLL_Y));
        if(map.containsKey(MOVE_TO))
            setMoveTo((String)map.get(MOVE_TO));
    }
}