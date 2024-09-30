/*
 * Copyright 2024 Gerald Curley
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

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigStore;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents the configuration for content crawlers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CrawlerConfig extends ConfigStore
{
    private static final Logger logger = Logger.getLogger(CrawlerConfig.class.getName());

    public static final String FILENAME = "crawler.yml";

    private List<ErrorPage> errorPages = new ArrayList<ErrorPage>();

    /**
     * Default constructor.
     */
    protected CrawlerConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public CrawlerConfig(CrawlerConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(CrawlerConfig obj)
    {
        if(obj != null)
        {
            for(ErrorPage errorPage : obj.getErrorPages())
                addErrorPage(new ErrorPage(errorPage));
        }
    }

    /**
     * Returns the name of the config file.
     */
    @Override
    public String getFilename()
    {
        return FILENAME;
    }

    /**
     * Returns the error pages for this configuration.
     */
    public List<ErrorPage> getErrorPages()
    {
        return errorPages;
    }

    /**
     * Sets the error pages for this configuration.
     */
    public void setErrorPages(List<ErrorPage> errorPages)
    {
        this.errorPages = errorPages;
    }

    /**
     * Adds an error page for this configuration.
     */
    public void addErrorPage(ErrorPage errorPage)
    {
        this.errorPages.add(errorPage);
    }

    /**
     * Returns the number of errorPages.
     */
    public int numErrorPages()
    {
        return errorPages.size();
    }

    /**
     * Returns the error page at the given index.
     */
    public ErrorPage getErrorPage(int i)
    {
        return errorPages.get(i);
    }

    /**
     * Returns a builder for the crawler config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make crawler config construction easier.
     */
    public static class Builder
        extends ConfigStore.Builder<CrawlerConfig,Builder>
        implements ConfigParser<CrawlerConfig>
    {
        // The config attribute names
        private static final String ERROR_PAGES = "error-pages";

        private CrawlerConfig ret = new CrawlerConfig();

        /**
         * Default constructor.
         */
        protected Builder()
        {
            filename(ret.getFilename());
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(ERROR_PAGES))
            {
                List<Map<String,Object>> errorTypes = (List<Map<String,Object>>)map.get(ERROR_PAGES);
                for(Map<String,Object> config : errorTypes)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                    {
                        List<Object> errorPages = (List<Object>)entry.getValue();
                        for(Object errorPage : errorPages)
                        {
                            ret.addErrorPage(ErrorPage.builder(entry.getKey())
                                .parse((Map<String,Object>)errorPage).build());
                        }
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
         * Returns the configured crawler config instance
         * @return The crawler config instance
         */
        @Override
        public CrawlerConfig build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}