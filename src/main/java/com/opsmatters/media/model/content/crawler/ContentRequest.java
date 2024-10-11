/*
 * Copyright 2023 Gerald Curley
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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.crawler.CrawlerBrowser;

/**
 * Class that represents a YAML configuration for a web page request.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentRequest implements ConfigElement
{
    private CrawlerBrowser browser;
    private List<String> urls = new ArrayList<String>();
    private Map<String,String> headers; // not used yet
    private String basePath = "";
    private boolean removeParameters = true;
    private boolean trailingSlash = false;
    private boolean headless = true;
    private boolean antiCache = false;

    /**
     * Default constructor.
     */
    public ContentRequest()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentRequest(ContentRequest obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentRequest obj)
    {
        if(obj != null)
        {
            setBrowser(obj.getBrowser());
            setUrls(obj.getUrls());
            if(obj.getHeaders() != null)
                setHeaders(new HashMap<String,String>(obj.getHeaders()));
            setBasePath(obj.getBasePath());
            setHeadless(obj.isHeadless());
            setRemoveParameters(obj.removeParameters());
            setTrailingSlash(obj.hasTrailingSlash());
            setAntiCache(obj.isAntiCache());
        }
    }

    /**
     * Returns the browser for this configuration.
     */
    public CrawlerBrowser getBrowser()
    {
        return browser;
    }

    /**
     * Sets the browser for this configuration.
     */
    public void setBrowser(String browser)
    {
        setBrowser(CrawlerBrowser.fromValue(browser));
    }

    /**
     * Sets the browser for this configuration.
     */
    public void setBrowser(CrawlerBrowser browser)
    {
        this.browser = browser;
    }

    /**
     * Returns the url at the given index for this configuration.
     */
    public String getUrl(int idx)
    {
        return urls.size() > idx ? urls.get(idx) : "";
    }

    /**
     * Returns the urls for this configuration.
     */
    public List<String> getUrls()
    {
        return urls;
    }

    /**
     * Sets the urls for this configuration.
     */
    public void setUrls(List<String> urls)
    {
        for(String url : urls)
            this.urls.add(url);
    }

    /**
     * Adds the url for this configuration.
     */
    public void addUrl(String url)
    {
        this.urls.add(url);
    }

    /**
     * Returns <CODE>true</CODE> if the urls for this configuration have been set.
     */
    public boolean hasUrls()
    {
        return urls.size() > 0;
    }

    /**
     * Returns the number of urls for this configuration.
     */
    public int numUrls()
    {
        return urls.size();
    }

    /**
     * Returns the HTTP headers for this configuration.
     */
    public Map<String,String> getHeaders()
    {
        return headers;
    }

    /**
     * Sets the HTTP headers for this configuration.
     */
    public void setHeaders(Map<String,String> headers)
    {
        if(this.headers == null)
            this.headers = new HashMap<String,String>();
        this.headers.putAll(headers);
    }

    /**
     * Returns the base path for this configuration.
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Sets the base path for this configuration.
     */
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    /**
     * Returns <CODE>true</CODE> if the browser is headless.
     */
    public boolean isHeadless()
    {
        return headless;
    }

    /**
     * Set to <CODE>true</CODE> if the browser is headless.
     */
    public void setHeadless(boolean headless)
    {
        this.headless = headless;
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
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<ContentRequest>
    {
        // The config attribute names
        private static final String BROWSER = "browser";
        private static final String URL = "url";
        private static final String URLS = "urls";
        private static final String HEADERS = "headers";
        private static final String BASE_PATH = "base-path";
        private static final String REMOVE_PARAMETERS = "remove-parameters";
        private static final String TRAILING_SLASH = "trailing-slash";
        private static final String HEADLESS = "headless";
        private static final String ANTI_CACHE = "anti-cache";

        private ContentRequest ret = new ContentRequest();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(BROWSER))
                ret.setBrowser((String)map.get(BROWSER));
            if(map.containsKey(URL))
                ret.addUrl((String)map.get(URL));
            if(map.containsKey(URLS))
                ret.setUrls((List<String>)map.get(URLS));
            if(map.containsKey(HEADERS))
                ret.setHeaders((Map<String,String>)map.get(HEADERS));
            if(map.containsKey(BASE_PATH))
                ret.setBasePath((String)map.get(BASE_PATH));
            if(map.containsKey(REMOVE_PARAMETERS))
                ret.setRemoveParameters((Boolean)map.get(REMOVE_PARAMETERS));
            if(map.containsKey(TRAILING_SLASH))
                ret.setTrailingSlash((Boolean)map.get(TRAILING_SLASH));
            if(map.containsKey(HEADLESS))
                ret.setHeadless((Boolean)map.get(HEADLESS));
            if(map.containsKey(ANTI_CACHE))
                ret.setAntiCache((Boolean)map.get(ANTI_CACHE));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ContentRequest build()
        {
            return ret;
        }
    }
}