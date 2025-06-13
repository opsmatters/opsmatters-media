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

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigType;
import com.opsmatters.media.model.ConfigStore;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.system.aws.S3Config;

/**
 * Class that represents the minimum configuration to boostrap the application.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BootstrapConfig extends ConfigStore
{
    private static final Logger logger = Logger.getLogger(BootstrapConfig.class.getName());

    public static final ConfigType TYPE = ConfigType.BOOTSTRAP;
    public static final String FILENAME = TYPE.filename();

    private S3Config s3;

    /**
     * Default constructor.
     */
    protected BootstrapConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public BootstrapConfig(BootstrapConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(BootstrapConfig obj)
    {
        if(obj != null)
        {
            setS3Config(new S3Config(obj.getS3Config()));
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
     * Returns the S3 configuration.
     */
    public S3Config getS3Config()
    {
        return s3;
    }

    /**
     * Sets the S3 configuration.
     */
    public void setS3Config(S3Config s3)
    {
        this.s3 = s3;
    }

    /**
     * Returns a builder for the bootstrap config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make bootstrap config construction easier.
     */
    public static class Builder
        extends ConfigStore.Builder<BootstrapConfig,Builder>
        implements ConfigParser<BootstrapConfig>
    {
        // The config attribute names
        private static final String S3 = "s3";

        private BootstrapConfig ret = new BootstrapConfig();

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

            if(map.containsKey(S3))
                ret.setS3Config(S3Config.builder(id)
                    .parse((Map<String,Object>)map.get(S3)).build());

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
         * Returns the configured bootstrap config instance
         * @return The bootstrap config instance
         */
        @Override
        public BootstrapConfig build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}