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
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.EventResource;

/**
 * Class that represents the configuration for event content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventConfiguration extends ContentConfiguration<EventResource>
{
    private static final Logger logger = Logger.getLogger(EventConfiguration.class.getName());

    public static final String PAGES = "pages";
    public static final String PROVIDERS = "providers";

    private List<WebPageConfiguration> pages = new ArrayList<WebPageConfiguration>();
    private List<WebPageConfiguration> providers = new ArrayList<WebPageConfiguration>();

    /**
     * Default constructor.
     */
    public EventConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public EventConfiguration(EventConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EventConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(WebPageConfiguration page : obj.getPages())
                addPage(new WebPageConfiguration(page));
            for(WebPageConfiguration provider : obj.getProviders())
                addProvider(new WebPageConfiguration(provider));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.EVENT;
    }

    /**
     * Returns the pages for this configuration.
     */
    public List<WebPageConfiguration> getPages()
    {
        return pages;
    }

    /**
     * Sets the pages for this configuration.
     */
    public void setPages(List<WebPageConfiguration> pages)
    {
        this.pages = pages;
    }

    /**
     * Adds a page for this configuration.
     */
    public void addPage(WebPageConfiguration page)
    {
        this.pages.add(page);
    }

    /**
     * Returns the number of pages.
     */
    public int numPages()
    {
        return pages.size();
    }

    /**
     * Returns the page at the given index.
     */
    public WebPageConfiguration getPage(int i)
    {
        return pages.get(i);
    }

    /**
     * Returns the page with the given name.
     */
    public WebPageConfiguration getPage(String name)
    {
        WebPageConfiguration ret = null;
        for(WebPageConfiguration page : getPages())
        {
            if(page.getName().equals(name))
            {
                ret = page;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns the providers for this configuration.
     */
    public List<WebPageConfiguration> getProviders()
    {
        return providers;
    }

    /**
     * Sets the providers for this configuration.
     */
    public void setProviders(List<WebPageConfiguration> providers)
    {
        this.providers = providers;
    }

    /**
     * Adds a provider for this configuration.
     */
    public void addProvider(WebPageConfiguration provider)
    {
        this.providers.add(provider);
    }

    /**
     * Returns the number of providers.
     */
    public int numProviders()
    {
        return providers.size();
    }

    /**
     * Returns the provider at the given index.
     */
    public WebPageConfiguration getProvider(int i)
    {
        return providers.get(i);
    }

    /**
     * Returns the provider with the given name.
     */
    public WebPageConfiguration getProvider(String name)
    {
        WebPageConfiguration ret = null;
        for(WebPageConfiguration provider : getProviders())
        {
            if(provider.getName().equals(name))
            {
                ret = provider;
                break;
            }
        }
        return ret;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        super.parseDocument(map);

        if(map.containsKey(PROVIDERS))
        {
            List<Map<String,Object>> providers = (List<Map<String,Object>>)map.get(PROVIDERS);
            for(Map<String,Object> provider : providers)
            {
                for(Map.Entry<String,Object> entry : provider.entrySet())
                {
                    WebPageConfiguration config = new WebPageConfiguration(entry.getKey());
                    config.parseDocument((Map<String,Object>)entry.getValue());
                    addProvider(config);
                }
            }
        }

        if(map.containsKey(PAGES))
        {
            List<Map<String,Object>> pages = (List<Map<String,Object>>)map.get(PAGES);
            for(Map<String,Object> page : pages)
            {
                for(Map.Entry<String,Object> entry : page.entrySet())
                {
                    WebPageConfiguration config = new WebPageConfiguration(entry.getKey());
                    config.parseDocument((Map<String,Object>)entry.getValue());

                    WebPageConfiguration provider = getProvider(config.getProvider());
                    if(provider != null)
                    {
                        config.copyAttributes(provider);

                        // Parse again to make sure this config overrides the provider template
                        config.setName(entry.getKey());
                        config.parseDocument((Map<String,Object>)entry.getValue());
                    }

                    addPage(config);
                }
            }
        }
    }
}