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

package com.opsmatters.media.model.social;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigSetup;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents the configuration for social media components.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialSetup extends ConfigSetup
{
    private static final Logger logger = Logger.getLogger(SocialSetup.class.getName());

    public static final String FILENAME = "social.yml";

    private List<SocialChannel> channels = new ArrayList<SocialChannel>();

    /**
     * Default constructor.
     */
    protected SocialSetup()
    {
    }

    /**
     * Copy constructor.
     */
    public SocialSetup(SocialSetup obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialSetup obj)
    {
        if(obj != null)
        {
            for(SocialChannel channel : obj.getChannels())
                addChannel(new SocialChannel(channel));
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
     * Returns the list of channels.
     */
    public List<SocialChannel> getChannels()
    {
        return channels;
    }

    /**
     * Adds a channel to the list of channels.
     */
    public void addChannel(SocialChannel channel)
    {
        this.channels.add(channel);
    }

    /**
     * Returns the number of channels.
     */
    public int numChannels()
    {
        return channels.size();
    }

    /**
     * Returns a builder for the social setup.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make social setup construction easier.
     */
    public static class Builder
        extends ConfigSetup.Builder<SocialSetup,Builder>
        implements ConfigParser<SocialSetup>
    {
        // The config attribute names
        private static final String SOCIAL_CHANNELS = "social-channels";

        private SocialSetup ret = new SocialSetup();

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
            if(map.containsKey(SOCIAL_CHANNELS))
            {
                List<Map<String,Object>> channels = (List<Map<String,Object>>)map.get(SOCIAL_CHANNELS);
                for(Map<String,Object> channelMap : channels)
                {
                    for(Map.Entry<String,Object> entry : channelMap.entrySet())
                    {
                        ret.addChannel(SocialChannel.builder(entry.getKey())
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
         * Returns the configured social setup instance
         * @return The social setup instance
         */
        @Override
        public SocialSetup build() throws IOException
        {
            read(this);
            return ret;
        }
    }
}