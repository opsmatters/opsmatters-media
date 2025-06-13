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

package com.opsmatters.media.model.system;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import com.opsmatters.media.cache.system.Sites;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.system.aws.S3Config;

/**
 * Represents a site config containing environments.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SiteConfig implements ConfigElement
{
    private String id = "";
    private S3Config s3;
    private Map<EnvironmentId,SiteEnvironment> environments = new LinkedHashMap<EnvironmentId,SiteEnvironment>();

    /**
     * Default constructor.
     */
    public SiteConfig()
    {
    }

    /**
     * Constructor that takes an id.
     */
    public SiteConfig(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public SiteConfig(SiteConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SiteConfig obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setS3Config(new S3Config(obj.getS3Config()));
            for(SiteEnvironment environment : obj.getEnvironments().values())
                addEnvironment(new SiteEnvironment(environment));
        }
    }

    /**
     * Returns the id of the site config.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the site config.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the site config.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the S3 configuration for the site.
     */
    public S3Config getS3Config()
    {
        return s3;
    }

    /**
     * Sets the S3 configuration for the site.
     */
    public void setS3Config(S3Config s3)
    {
        this.s3 = s3;
    }

    /**
     * Adds a environment to the environments for the site.
     */
    public Map<EnvironmentId,SiteEnvironment> getEnvironments()
    {
        return this.environments;
    }

    /**
     * Adds an environment to the environments for the site.
     */
    public void addEnvironment(SiteEnvironment environment)
    {
        this.environments.put(environment.getId(), environment);
    }

    /**
     * Returns the number of environments.
     */
    public int numEnvironments()
    {
        return environments.size();
    }

    /**
     * Returns the environment with the given name.
     */
    public SiteEnvironment getEnvironment(EnvironmentId id)
    {
        return environments.get(id);
    }

    /**
     * Returns a builder for the site config.
     * @param name The name of the site config
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make site construction easier.
     */
    public static class Builder implements ConfigParser<SiteConfig>
    {
        // The config attribute names
        private static final String ID = "id";
        private static final String S3 = "s3";
        private static final String ENVIRONMENTS = "environments";

        private SiteConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the site
         */
        public Builder(String id)
        {
            ret = new SiteConfig(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            String id = ret.getId();

            if(map.containsKey(S3))
                ret.setS3Config(S3Config.builder(id)
                    .parse((Map<String,Object>)map.get(S3)).build());

            if(map.containsKey(ENVIRONMENTS))
            {
                List<Map<String,Object>> environments = (List<Map<String,Object>>)map.get(ENVIRONMENTS);
                for(Map<String,Object> config : environments)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                    {
                        ret.addEnvironment(SiteEnvironment.builder(entry.getKey(), Sites.get(id))
                            .parse((Map<String,Object>)entry.getValue()).build());
                    }
                }
            }

            return this;
        }

        /**
         * Returns the configured site config instance
         * @return The site instance
         */
        public SiteConfig build()
        {
            return ret;
        }
    }
}