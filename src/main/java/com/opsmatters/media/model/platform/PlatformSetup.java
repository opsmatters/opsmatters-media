/*
 * Copyright 2022 Gerald Curley
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
import com.opsmatters.media.model.ConfigSetup;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.platform.aws.S3Config;
import com.opsmatters.media.model.platform.aws.SesConfig;

/**
 * Class that represents the configuration for platform components.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PlatformSetup extends ConfigSetup
{
    private static final Logger logger = Logger.getLogger(PlatformSetup.class.getName());

    public static final String FILENAME = "platform.yml";

    private static S3Config s3;
    private static SesConfig ses;

    private List<Environment> environments = new ArrayList<Environment>();
    private List<Site> sites = new ArrayList<Site>();


    /**
     * Default constructor.
     */
    protected PlatformSetup()
    {
    }

    /**
     * Copy constructor.
     */
    public PlatformSetup(PlatformSetup obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PlatformSetup obj)
    {
        if(obj != null)
        {
            setS3Config(new S3Config(obj.getS3Config()));
            setSesConfig(new SesConfig(obj.getSesConfig()));
            for(Environment environment : obj.getEnvironments())
                addEnvironment(new Environment(environment));
            for(Site site : obj.getSites())
                addSite(new Site(site));
        }
    }

    /**
     * Returns the name of the setup file.
     */
    @Override
    public String getFilename()
    {
        return FILENAME;
    }

    /**
     * Returns the S3 configuration for the sites.
     */
    public static S3Config getS3Config()
    {
        return s3;
    }

    /**
     * Sets the S3 configuration for the sites.
     */
    public static void setS3Config(S3Config s3)
    {
        PlatformSetup.s3 = s3;
    }

    /**
     * Returns the SES configuration for the sites.
     */
    public static SesConfig getSesConfig()
    {
        return ses;
    }

    /**
     * Sets the SES configuration for the sites.
     */
    public static void setSesConfig(SesConfig ses)
    {
        PlatformSetup.ses = ses;
    }

    /**
     * Returns the list of environments.
     */
    public List<Environment> getEnvironments()
    {
        return environments;
    }

    /**
     * Adds an environment to the list of environments.
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
     * Returns a builder for the platform setup.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make platform setup construction easier.
     */
    public static class Builder
        extends ConfigSetup.Builder<PlatformSetup,Builder>
        implements ConfigParser<PlatformSetup>
    {
        // The config attribute names
        private static final String S3 = "s3";
        private static final String SES = "ses";
        private static final String ENVIRONMENTS = "environments";
        private static final String SITES = "sites";

        private PlatformSetup ret = new PlatformSetup();

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
            String id = "platform";

            if(map.containsKey(S3))
                ret.setS3Config(S3Config.builder(id)
                    .parse((Map<String,Object>)map.get(S3)).build());

            if(map.containsKey(SES))
                ret.setSesConfig(SesConfig.builder(id)
                    .parse((Map<String,Object>)map.get(SES)).build());

            if(map.containsKey(ENVIRONMENTS))
            {
                List<Map<String,Object>> environments = (List<Map<String,Object>>)map.get(ENVIRONMENTS);
                for(Map<String,Object> config : environments)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                    {
                        ret.addEnvironment(Environment.builder(entry.getKey())
                            .parse((Map<String,Object>)entry.getValue()).build());
                    }
                }
            }

            if(map.containsKey(SITES))
            {
                List<Map<String,Object>> sites = (List<Map<String,Object>>)map.get(SITES);
                for(Map<String,Object> config : sites)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                    {
                        ret.addSite(Site.builder(entry.getKey())
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
         * Returns the configured platform setup instance
         * @return The platform setup instance
         */
        @Override
        public PlatformSetup build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}