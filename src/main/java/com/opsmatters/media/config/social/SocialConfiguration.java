/*
 * Copyright 2020 Gerald Curley
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

package com.opsmatters.media.config.social;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.SocialChannels;

/**
 * Class that represents a YAML configuration for the social media channels.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialConfiguration extends YamlConfiguration implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(SocialConfiguration.class.getName());

    public static final String FILENAME = "social.yml";

    public static final String SOCIAL_CHANNELS = "social-channels";

    /**
     * Default constructor.
     */
    public SocialConfiguration(String name)
    {
        super(name);
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(SOCIAL_CHANNELS))
        {
            List<Map<String,Object>> channels = (List<Map<String,Object>>)map.get(SOCIAL_CHANNELS);
            for(Map<String,Object> channelMap : channels)
            {
                for(Map.Entry<String,Object> entry : channelMap.entrySet())
                {
                    SocialChannelConfiguration config = new SocialChannelConfiguration(entry.getKey());
                    config.parse((Map<String,Object>)entry.getValue());
                    SocialChannels.add(new SocialChannel(config));
                }
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
        private String name = "social";
        private String directory = "";
        private String filename = FILENAME;

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
        public SocialConfiguration build(boolean read)
        {
            SocialConfiguration ret = new SocialConfiguration(name);

            if(read)
            {
                File file = new File(directory, filename);
                ret.read(file.getAbsolutePath());
            }

            return ret;
        }
    }
}
