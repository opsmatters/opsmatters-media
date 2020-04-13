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
import com.opsmatters.media.crawler.CrawlerBrowser;

/**
 * Class that represents a YAML configuration for a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WebPageConfiguration extends FieldsConfiguration
{
    public static final String URL = "url";
    public static final String BROWSER = "browser";
    public static final String BASE_PATH = "base-path";
    public static final String MORE_LINK = "more-link";

    private String url = "";
    private CrawlerBrowser browser;
    private String basePath = "";
    private MoreLinkConfiguration moreLink;

    /**
     * Default constructor.
     */
    public WebPageConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public WebPageConfiguration(WebPageConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(WebPageConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUrl(obj.getUrl());
            setBrowser(obj.getBrowser());
            setBasePath(obj.getBasePath());
            if(obj.getMoreLink() != null)
                setMoreLink(new MoreLinkConfiguration(obj.getMoreLink()));
        }
    }

    /**
     * Returns the url for this configuration.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url for this configuration.
     */
    public void setUrl(String url)
    {
        this.url = url;
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
     * Returns the link to get more items.
     */
    public MoreLinkConfiguration getMoreLink()
    {
        return moreLink;
    }

    /**
     * Sets the link to get more items.
     */
    public void setMoreLink(MoreLinkConfiguration moreLink)
    {
        this.moreLink = moreLink;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        super.parseDocument(map);
        if(map.containsKey(URL))
            setUrl((String)map.get(URL));
        if(map.containsKey(BROWSER))
            setBrowser((String)map.get(BROWSER));
        if(map.containsKey(BASE_PATH))
            setBasePath((String)map.get(BASE_PATH));
        if(map.containsKey(MORE_LINK))
            setMoreLink(createMoreLink(MORE_LINK, map.get(MORE_LINK)));
    }

    /**
     * Create a More link object for the given value.
     */
    public MoreLinkConfiguration createMoreLink(String name, Object value)
    {
        MoreLinkConfiguration ret = null;
        if(value instanceof String)
            ret = new MoreLinkConfiguration(name, (String)value);
        else if(value instanceof Map)
            ret = new MoreLinkConfiguration(name, (Map<String,Object>)value);
        return ret;
    }
}