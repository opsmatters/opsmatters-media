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

package com.opsmatters.media.model.content.video;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;

/**
 * Class that represents the configuration for video content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoConfig extends ContentConfig<Video>
{
    private static final Logger logger = Logger.getLogger(VideoConfig.class.getName());

    private List<CrawlerVideoChannel> channels = new ArrayList<CrawlerVideoChannel>();

    /**
     * Constructor that takes a code.
     */
    public VideoConfig(String code)
    {
        super(code);
    }

    /**
     * Copy constructor.
     */
    public VideoConfig(VideoConfig obj)
    {
        super(obj);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoConfig obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(CrawlerVideoChannel channel : obj.getChannels())
                addChannel(new CrawlerVideoChannel(channel));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.VIDEO;
    }

    /**
     * Returns the list of HTML fields that need to be escaped.
     */
    @Override
    public String[] getHtmlFields()
    {
        return new String[] {"summary", "body"};
    }

    /**
     * Returns the channels for this configuration.
     */
    public List<CrawlerVideoChannel> getChannels()
    {
        return channels;
    }


    /**
     * Sets the channels for this configuration.
     */
    public void setChannels(List<CrawlerVideoChannel> channels)
    {
        this.channels = channels;
    }

    /**
     * Adds a channel for this configuration.
     */
    public void addChannel(CrawlerVideoChannel channel)
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
     * Returns the channel at the given index.
     */
    public CrawlerVideoChannel getChannel(int i)
    {
        return channels.size() > i ? channels.get(i) : null;
    }

    /**
     * Returns the channel with the given name.
     */
    public CrawlerVideoChannel getChannel(String name)
    {
        CrawlerVideoChannel ret = null;
        for(CrawlerVideoChannel channel : getChannels())
        {
            if(channel.getName().equals(name))
            {
                ret = channel;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the channel with the given name has been set.
     */
    public boolean hasChannel(String name)
    {
        return getChannel(name) != null;
    }

    /**
     * Returns a builder for the configuration.
     * @param code The code for the configuration
     * @return The builder instance.
     */
    public static Builder builder(String code)
    {
        return new Builder(code);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends ContentConfig.Builder<VideoConfig, Builder>
    {
        // The config attribute names
        private static final String CHANNELS = "channels";

        private VideoConfig ret = null;

        /**
         * Constructor that takes a code.
         * @param code The code for the configuration
         */
        public Builder(String code)
        {
            ret = new VideoConfig(code);
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

            if(map.containsKey(CHANNELS))
            {
                List<Map<String,Object>> channels = (List<Map<String,Object>>)map.get(CHANNELS);
                for(Map<String,Object> channel : channels)
                {
                    for(Map.Entry<String,Object> entry : channel.entrySet())
                    {
                        ret.addChannel(CrawlerVideoChannel.builder(entry.getKey())
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
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public VideoConfig build()
        {
            return ret;
        }
    }
}
