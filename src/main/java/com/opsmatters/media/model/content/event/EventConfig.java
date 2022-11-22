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

package com.opsmatters.media.model.content.event;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;

/**
 * Class that represents the configuration for event content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventConfig extends ContentConfig<EventResource>
{
    private static final Logger logger = Logger.getLogger(EventConfig.class.getName());

    private List<CrawlerWebPage> pages = new ArrayList<CrawlerWebPage>();
    private List<CrawlerWebPage> providers = new ArrayList<CrawlerWebPage>();

    /**
     * Constructor that takes a name.
     */
    public EventConfig(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public EventConfig(EventConfig obj)
    {
        super(obj);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EventConfig obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(CrawlerWebPage page : obj.getPages())
                addPage(new CrawlerWebPage(page));
            for(CrawlerWebPage provider : obj.getProviders())
                addProvider(new CrawlerWebPage(provider));
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
    public List<CrawlerWebPage> getPages()
    {
        return pages;
    }

    /**
     * Sets the pages for this configuration.
     */
    public void setPages(List<CrawlerWebPage> pages)
    {
        this.pages = pages;
    }

    /**
     * Adds a page for this configuration.
     */
    public void addPage(CrawlerWebPage page)
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
    public CrawlerWebPage getPage(int i)
    {
        return pages.get(i);
    }

    /**
     * Returns the page with the given name.
     */
    public CrawlerWebPage getPage(String name)
    {
        CrawlerWebPage ret = null;
        for(CrawlerWebPage page : getPages())
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
    public List<CrawlerWebPage> getProviders()
    {
        return providers;
    }

    /**
     * Adds a provider for this configuration.
     */
    public void addProvider(CrawlerWebPage provider)
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
    public CrawlerWebPage getProvider(int i)
    {
        return providers.get(i);
    }

    /**
     * Returns the provider with the given name.
     */
    public CrawlerWebPage getProvider(String name)
    {
        CrawlerWebPage ret = null;
        for(CrawlerWebPage provider : getProviders())
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
     * Returns a builder for the configuration.
     * @param name The name for the configuration
     * @return The builder instance.
     */
    public static Builder builder(String name)
    {
        return new Builder(name);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends ContentConfig.Builder<EventConfig, Builder>
    {
        // The config attribute names
        private static final String PAGES = "pages";
        private static final String PROVIDERS = "providers";

        private EventConfig ret = null;

        /**
         * Constructor that takes a name.
         * @param name The name for the configuration
         */
        public Builder(String name)
        {
            ret = new EventConfig(name);
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

            if(map.containsKey(PROVIDERS))
            {
                List<Map<String,Object>> providers = (List<Map<String,Object>>)map.get(PROVIDERS);
                for(Map<String,Object> provider : providers)
                {
                    for(Map.Entry<String,Object> entry : provider.entrySet())
                    {
                        ret.addProvider(CrawlerWebPage.builder(entry.getKey())
                            .parse((Map<String,Object>)entry.getValue()).build());
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
                        CrawlerWebPage config = CrawlerWebPage.builder(entry.getKey())
                            .parse((Map<String,Object>)entry.getValue()).build();

                        CrawlerWebPage provider = ret.getProvider(config.getProvider());
                        if(provider != null)
                        {
                            config.copyAttributes(provider);
                            config.copyAttributes(CrawlerWebPage.builder(entry.getKey())
                                .parse((Map<String,Object>)entry.getValue()).build());
                        }

                        ret.addPage(config);
                    }
                }
            }

            return this;
        }

        /**
         * Copy constructor.
         * @param obj The object to copy attributes from
         * @return This object
         */
        public Builder copy(EventConfig obj)
        {
            ret.copyAttributes(obj);
            return this;
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
        public EventConfig build()
        {
            return ret;
        }
    }
}