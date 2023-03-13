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
import java.util.HashMap;
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
    private Map<String,String> headers;

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
            if(obj.getHeaders() != null)
                setHeaders(obj.getHeaders());
        }
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
        private static final String HEADERS = "headers";

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

            if(map.containsKey(HEADERS))
                ret.setHeaders((Map<String,String>)map.get(HEADERS));

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
        public CrawlerWebPage build()
        {
            return ret;
        }
    }
}