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

package com.opsmatters.media.model.content.publication;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;

/**
 * Class that represents the configuration for publication content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class PublicationConfig<C extends ContentItem> extends ContentConfig<C>
{
    private static final Logger logger = Logger.getLogger(PublicationConfig.class.getName());

    private List<CrawlerWebPage> pages = new ArrayList<CrawlerWebPage>();

    /**
     * Constructor that takes a name.
     */
    public PublicationConfig(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public PublicationConfig(PublicationConfig obj)
    {
        super(obj);
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
     * Builder to make configuration construction easier.
     */
    protected abstract static class Builder<T extends PublicationConfig, B extends Builder<T,B>>
        extends ContentConfig.Builder<T,B>
    {
        // The config attribute names
        private static final String PAGES = "pages";

        private PublicationConfig ret = null;

        /**
         * Sets the configuration.
         * @param config The configuration
         */
        public void set(PublicationConfig config)
        {
            ret = config;
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public B parse(Map<String, Object> map)
        {
            super.parse(map);

            if(map.containsKey(PAGES))
            {
                List<Map<String,Object>> pages = (List<Map<String,Object>>)map.get(PAGES);
                for(Map<String,Object> page : pages)
                {
                    for(Map.Entry<String,Object> entry : page.entrySet())
                    {
                        ret.addPage(CrawlerWebPage.builder(entry.getKey())
                            .parse((Map<String,Object>)entry.getValue()).build());
                    }
                }
            }

            return self();
        }
    }
}