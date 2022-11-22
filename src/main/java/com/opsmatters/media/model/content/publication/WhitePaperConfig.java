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
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;

/**
 * Class that represents a YAML configuration for white papers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WhitePaperConfig extends PublicationConfig<WhitePaperResource>
{
    /**
     * Constructor that takes a name.
     */
    public WhitePaperConfig(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public WhitePaperConfig(WhitePaperConfig obj)
    {
        super(obj);
        if(obj != null)
        {
            copyAttributes(obj);
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
        return ContentType.WHITE_PAPER;
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
    public static class Builder extends PublicationConfig.Builder<WhitePaperConfig, Builder>
    {
        private WhitePaperConfig ret = null;

        /**
         * Constructor that takes a name.
         * @param name The name for the configuration
         */
        public Builder(String name)
        {
            ret = new WhitePaperConfig(name);
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
            return this;
        }

        /**
         * Copy constructor.
         * @param obj The object to copy attributes from
         * @return This object
         */
        public Builder copy(WhitePaperConfig obj)
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
        public WhitePaperConfig build()
        {
            return ret;
        }
    }
}