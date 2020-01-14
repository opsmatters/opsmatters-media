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

package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.VideoProvider;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class that represents the configuration for video content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoConfiguration extends ContentConfiguration
{
    private static final Logger logger = Logger.getLogger(VideoConfiguration.class.getName());

    public static final String CHANNELS = "channels";

    private List<VideoChannelConfiguration> channels = new ArrayList<VideoChannelConfiguration>();

    /**
     * Default constructor.
     */
    public VideoConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public VideoConfiguration(VideoConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(VideoChannelConfiguration channel : obj.getChannels())
                addChannel(new VideoChannelConfiguration(channel));
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
     * Returns the channels for this configuration.
     */
    public List<VideoChannelConfiguration> getChannels()
    {
        return channels;
    }


    /**
     * Sets the channels for this configuration.
     */
    public void setChannels(List<VideoChannelConfiguration> channels)
    {
        this.channels = channels;
    }

    /**
     * Adds a channel for this configuration.
     */
    public void addChannel(VideoChannelConfiguration channel)
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
    public VideoChannelConfiguration getChannel(int i)
    {
        return channels.get(i);
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            super.parseDocument(map);
            if(map.containsKey(CHANNELS))
            {
                List<Map<String,Object>> channels = (List<Map<String,Object>>)map.get(CHANNELS);
                for(Map<String,Object> channel : channels)
                {
                    for(Map.Entry<String,Object> entry : channel.entrySet())
                    {
                        VideoChannelConfiguration config = new VideoChannelConfiguration(entry.getKey());
                        config.parseDocument(entry.getValue());
                        addChannel(config);
                    }
                }
            }
        }
    }
}
