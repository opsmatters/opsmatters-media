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
import com.opsmatters.media.model.platform.aws.SesConfig;

/**
 * Class that represents the configuration for the core curator platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PlatformConfig extends ConfigStore
{
    private static final Logger logger = Logger.getLogger(PlatformConfig.class.getName());

    public static final ConfigType TYPE = ConfigType.PLATFORM;
    public static final String FILENAME = TYPE.filename();

    private SesConfig ses;
    private List<Environment> environments = new ArrayList<Environment>();


    /**
     * Default constructor.
     */
    protected PlatformConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public PlatformConfig(PlatformConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PlatformConfig obj)
    {
        if(obj != null)
        {
            setSesConfig(new SesConfig(obj.getSesConfig()));
            for(Environment environment : obj.getEnvironments())
                addEnvironment(new Environment(environment));
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
     * Returns the SES configuration.
     */
    public SesConfig getSesConfig()
    {
        return ses;
    }

    /**
     * Sets the SES configuration.
     */
    public void setSesConfig(SesConfig ses)
    {
        this.ses = ses;
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
     * Returns a builder for the platform config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make platform config construction easier.
     */
    public static class Builder
        extends ConfigStore.Builder<PlatformConfig,Builder>
        implements ConfigParser<PlatformConfig>
    {
        // The config attribute names
        private static final String SES = "ses";
        private static final String ENVIRONMENTS = "environments";

        private PlatformConfig ret = new PlatformConfig();

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
            String id = TYPE.tag();

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
         * Returns the configured platform config instance
         * @return The platform config instance
         */
        @Override
        public PlatformConfig build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}