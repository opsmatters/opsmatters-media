/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.config.site;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.site.Site;

/**
 * Class that represents the configuration for sites and environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SiteConfiguration extends YamlConfiguration
{
    private static final Logger logger = Logger.getLogger(SiteConfiguration.class.getName());

    public static final String FILENAME = "sites.yml";

    public static final String SITES = "sites";

    private List<Site> sites = new ArrayList<Site>();

    /**
     * Default constructor.
     */
    public SiteConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public SiteConfiguration(SiteConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SiteConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(Site site : obj.getSites())
                addSite(new Site(site));
        }
    }

    /**
     * Returns the sites for this configuration.
     */
    public List<Site> getSites()
    {
        return sites;
    }

    /**
     * Sets the sites for this configuration.
     */
    public void setSites(List<Site> sites)
    {
        this.sites = sites;
    }

    /**
     * Adds a site for this configuration.
     */
    public void addSite(Site site)
    {
        this.sites.add(site);
    }

    /**
     * Returns the number of sites.
     */
    public int numSites()
    {
        return sites.size();
    }

    /**
     * Returns the site at the given index.
     */
    public Site getSite(int i)
    {
        return sites.get(i);
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(SITES))
        {
            List<Map<String,Object>> sites = (List<Map<String,Object>>)map.get(SITES);
            for(Map<String,Object> config : sites)
            {
                for(Map.Entry<String,Object> entry : config.entrySet())
                    addSite(new Site(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }
        }
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder
    {
        protected String name = "";
        protected String directory = "";
        protected String filename = "";

        /**
         * Default constructor.
         */
        public Builder()
        {
        }

        /**
         * Sets the name of the configuration.
         * <P>
         * @param name The name of the configuration
         * @return This object
         */
        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        /**
         * Sets the config directory.
         * @param key The config directory
         * @return This object
         */
        public Builder directory(String directory)
        {
            this.directory = directory;
            return this;
        }

        /**
         * Sets the config filename.
         * @param key The config filename
         * @return This object
         */
        public Builder filename(String filename)
        {
            this.filename = filename;
            return this;
        }

        /**
         * Returns the configuration
         * @return The configuration
         */
        public SiteConfiguration build(boolean read)
        {
            SiteConfiguration ret = new SiteConfiguration(name);

            if(read)
            {
                // Read the config file
                File config = new File(directory, filename);
                ret.read(config.getAbsolutePath());
            }

            return ret;
        }
    }
}