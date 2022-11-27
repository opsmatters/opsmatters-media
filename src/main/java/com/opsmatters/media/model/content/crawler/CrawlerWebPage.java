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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.crawler.CrawlerBrowser;

/**
 * Class that represents a YAML configuration for a web page crawler.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CrawlerWebPage extends CrawlerTarget
{
    private List<String> urls = new ArrayList<String>();
    private CrawlerBrowser browser;
    private boolean headless = true;
    private String basePath = "";
    private String userAgent = "";
    private MoreLink moreLink;

    /**
     * Constructor that takes a name.
     */
    public CrawlerWebPage(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public CrawlerWebPage(CrawlerWebPage obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(CrawlerWebPage obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUrls(obj.getUrls());
            setBrowser(obj.getBrowser());
            setHeadless(obj.isHeadless());
            setBasePath(obj.getBasePath());
            if(obj.getMoreLink() != null)
                setMoreLink(new MoreLink(obj.getMoreLink()));
        }
    }

    /**
     * Returns the url at the given index for this configuration.
     */
    public String getUrl(int idx)
    {
        return urls.size() > idx ? urls.get(idx) : "";
    }

    /**
     * Adds the url for this configuration.
     */
    public void addUrl(String url)
    {
        this.urls.add(url);
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
     * Returns <CODE>true</CODE> if the urls for this configuration have been set.
     */
    public boolean hasUrls()
    {
        return urls.size() > 0;
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
     * Returns the user agent for this configuration.
     */
    public String getUserAgent()
    {
        return userAgent;
    }

    /**
     * Sets the user agent for this configuration.
     */
    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    /**
     * Returns the link to get more items.
     */
    public MoreLink getMoreLink()
    {
        return moreLink;
    }

    /**
     * Sets the link to get more items.
     */
    public void setMoreLink(MoreLink moreLink)
    {
        this.moreLink = moreLink;
    }

    /**
     * Returns <CODE>true</CODE> if the link to get more items has been set for this configuration.
     */
    public boolean hasMoreLink()
    {
        return getMoreLink() != null;
    }

    /**
     * Returns a builder for the configuration.
     * @param name The name of the configuration
     * @return The builder instance.
     */
    public static Builder builder(String name)
    {
        return new Builder(name);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends CrawlerTarget.Builder<CrawlerWebPage, Builder>
    {
        // The config attribute names
        private static final String URL = "url";
        private static final String URLS = "urls";
        private static final String BROWSER = "browser";
        private static final String HEADLESS = "headless";
        private static final String BASE_PATH = "base-path";
        private static final String USER_AGENT = "user-agent";
        private static final String MORE_LINK = "more-link";

        private CrawlerWebPage ret = null;

        /**
         * Constructor that takes a name.
         * @param name The nam for the configuration
         */
        public Builder(String name)
        {
            ret = new CrawlerWebPage(name);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);

            if(map.containsKey(URL))
                ret.addUrl((String)map.get(URL));
            if(map.containsKey(URLS))
                ret.setUrls((List<String>)map.get(URLS));
            if(map.containsKey(BROWSER))
                ret.setBrowser((String)map.get(BROWSER));
            if(map.containsKey(HEADLESS))
                ret.setHeadless((Boolean)map.get(HEADLESS));
            if(map.containsKey(BASE_PATH))
                ret.setBasePath((String)map.get(BASE_PATH));
            if(map.containsKey(USER_AGENT))
                ret.setUserAgent((String)map.get(USER_AGENT));
            if(map.containsKey(MORE_LINK))
                ret.setMoreLink(createMoreLink(MORE_LINK, map.get(MORE_LINK)));

            return this;
        }

        /**
         * Create a More link object for the given value.
         */
        private MoreLink createMoreLink(String name, Object value)
        {
            MoreLink.Builder builder = MoreLink.builder();
            if(value instanceof String)
                builder = builder.selector((String)value);
            else if(value instanceof Map)
                builder = builder.parse((Map<String,Object>)value);
            return builder.build();
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public CrawlerWebPage build()
        {
            return ret;
        }
    }
}