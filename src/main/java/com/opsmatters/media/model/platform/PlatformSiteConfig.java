/*
 * Copyright 2025 Gerald Curley
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

package com.opsmatters.media.model.platform;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigType;
import com.opsmatters.media.model.ConfigStore;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents the configuration for curator platform sites.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PlatformSiteConfig extends ConfigStore
{
    private static final Logger logger = Logger.getLogger(PlatformSiteConfig.class.getName());

    public static final ConfigType TYPE = ConfigType.PLATFORM_SITES;
    public static final String FILENAME = TYPE.filename();

    private List<SiteConfig> sites = new ArrayList<SiteConfig>();


    /**
     * Default constructor.
     */
    protected PlatformSiteConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public PlatformSiteConfig(PlatformSiteConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PlatformSiteConfig obj)
    {
        if(obj != null)
        {
            for(SiteConfig site : obj.getSiteConfigs())
                addSiteConfig(new SiteConfig(site));
        }
    }

    /**
     * Returns the type of the config.
     */
    @Override
    public ConfigType getType()
    {
        return TYPE;
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
     * Returns the site configs for this configuration.
     */
    public List<SiteConfig> getSiteConfigs()
    {
        return sites;
    }

    /**
     * Sets the site configs for this configuration.
     */
    public void setSiteConfigs(List<SiteConfig> sites)
    {
        this.sites = sites;
    }

    /**
     * Adds a site config for this configuration.
     */
    public void addSiteConfig(SiteConfig site)
    {
        this.sites.add(site);
    }

    /**
     * Returns the number of site configs.
     */
    public int numSiteConfigs()
    {
        return sites.size();
    }

    /**
     * Returns the site config at the given index.
     */
    public SiteConfig getSiteConfig(int i)
    {
        return sites.get(i);
    }

    /**
     * Returns a builder for the platform site config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make site config construction easier.
     */
    public static class Builder
        extends ConfigStore.Builder<PlatformSiteConfig,Builder>
        implements ConfigParser<PlatformSiteConfig>
    {
        // The config attribute names
        private static final String SITES = "sites";

        private PlatformSiteConfig ret = new PlatformSiteConfig();

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
            if(map.containsKey(SITES))
            {
                List<Map<String,Object>> sites = (List<Map<String,Object>>)map.get(SITES);
                for(Map<String,Object> config : sites)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                    {
                        ret.addSiteConfig(SiteConfig.builder(entry.getKey())
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
         * Returns the configured site config instance
         * @return The site config instance
         */
        @Override
        public PlatformSiteConfig build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}