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
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;

/**
 * Class that represents the configuration for publication content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PublicationConfig extends ContentConfig<Publication>
{
    private static final Logger logger = Logger.getLogger(PublicationConfig.class.getName());

    private List<CrawlerWebPage> pages = new ArrayList<CrawlerWebPage>();

    /**
     * Constructor that takes a code.
     */
    public PublicationConfig(String code)
    {
        super(code);
    }

    /**
     * Copy constructor.
     */
    public PublicationConfig(PublicationConfig obj)
    {
        super(obj);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PublicationConfig obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(CrawlerWebPage page : obj.getPages())
                addPage(new CrawlerWebPage(page));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.PUBLICATION;
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
     * Returns <CODE>true</CODE> if the page with the given name has been set.
     */
    public boolean hasPage(String name)
    {
        return getPage(name) != null;
    }

    /**
     * Returns a builder for the configuration.
     * @param code The code for the configuration
     * @return The builder instance.
     */
    public static Builder builder(String code)
    {
        return new Builder(code);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends ContentConfig.Builder<PublicationConfig, Builder>
    {
        // The config attribute names
        private static final String PAGES = "pages";

        private PublicationConfig ret = null;

        /**
         * Constructor that takes a code.
         * @param code The code for the configuration
         */
        public Builder(String code)
        {
            ret = new PublicationConfig(code);
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
        public PublicationConfig build()
        {
            return ret;
        }
    }
}