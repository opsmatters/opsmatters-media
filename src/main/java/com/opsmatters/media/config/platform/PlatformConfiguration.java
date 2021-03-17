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

package com.opsmatters.media.config.platform;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.EnvironmentName;
import com.opsmatters.media.model.platform.S3Settings;
import com.opsmatters.media.model.platform.SesSettings;

/**
 * Class that represents the configuration for platform components.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PlatformConfiguration extends YamlConfiguration
{
    private static final Logger logger = Logger.getLogger(PlatformConfiguration.class.getName());

    public static final String FILENAME = "platform.yml";

    public static final String S3 = "s3";
    public static final String SES = "ses";
    public static final String ENVIRONMENTS = "environments";
    public static final String SITES = "sites";

    private S3Settings s3;
    private SesSettings ses;
    private List<Environment> environments = new ArrayList<Environment>();
    private List<Site> sites = new ArrayList<Site>();

    /**
     * Default constructor.
     */
    public PlatformConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public PlatformConfiguration(PlatformConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PlatformConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setS3Settings(new S3Settings(obj.getS3Settings()));
            setSesSettings(new SesSettings(obj.getSesSettings()));
            for(Environment environment : obj.getEnvironments())
                addEnvironment(new Environment(environment));
            for(Site site : obj.getSites())
                addSite(new Site(site));
        }
    }

    /**
     * Returns the S3 settings for the environment.
     */
    public S3Settings getS3Settings()
    {
        return s3;
    }

    /**
     * Sets the S3 settings for the environment.
     */
    public void setS3Settings(S3Settings s3)
    {
        this.s3 = s3;
    }

    /**
     * Returns the SES settings for the environment.
     */
    public SesSettings getSesSettings()
    {
        return ses;
    }

    /**
     * Sets the SES settings for the environment.
     */
    public void setSesSettings(SesSettings ses)
    {
        this.ses = ses;
    }

    /**
     * Adds a environment to the environments for the site.
     */
    public List<Environment> getEnvironments()
    {
        return this.environments;
    }

    /**
     * Adds a environment to the environments for the site.
     */
    public void addEnvironment(Environment environment)
    {
        this.environments.add(environment);
    }

    /**
     * Returns the number of environments.
     */
    public int numEnvironments()
    {
        return environments.size();
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
        if(map.containsKey(S3))
            setS3Settings(new S3Settings("platform", (Map<String,Object>)map.get(S3)));

        if(map.containsKey(SES))
            setSesSettings(new SesSettings("platform", (Map<String,Object>)map.get(SES)));

        if(map.containsKey(ENVIRONMENTS))
        {
            List<Map<String,Object>> environments = (List<Map<String,Object>>)map.get(ENVIRONMENTS);
            for(Map<String,Object> config : environments)
            {
                for(Map.Entry<String,Object> entry : config.entrySet())
                    addEnvironment(new Environment(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }
        }

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
        public PlatformConfiguration build(boolean read)
        {
            PlatformConfiguration ret = new PlatformConfiguration(name);

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