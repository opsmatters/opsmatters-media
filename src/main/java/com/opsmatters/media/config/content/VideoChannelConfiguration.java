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
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.content.VideoProvider;

/**
 * Class that represents a YAML configuration for a video channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoChannelConfiguration extends YamlConfiguration
{
    public static final String CHANNEL_ID = "channelId";
    public static final String USER_ID = "userId";
    public static final String PROVIDER = "provider";

    private String channelId = "";
    private String userId = "";
    private VideoProvider provider;

    /**
     * Default constructor.
     */
    public VideoChannelConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public VideoChannelConfiguration(VideoChannelConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoChannelConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setChannelId(obj.getChannelId());
            setUserId(obj.getUserId());
            setProvider(obj.getProvider());
        }
    }

    /**
     * Returns the name of this configuration.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the channel id for this configuration.
     */
    public String getChannelId()
    {
        return channelId;
    }

    /**
     * Sets the channel id for this configuration.
     */
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    /**
     * Returns the user id for this configuration.
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Sets the user id for this configuration.
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * Returns the provider for this configuration.
     */
    public VideoProvider getProvider()
    {
        return provider;
    }

    /**
     * Sets the provider for this configuration.
     */
    public void setProvider(VideoProvider provider)
    {
        this.provider = provider;
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

            if(map.containsKey(CHANNEL_ID))
                setChannelId((String)map.get(CHANNEL_ID));
            if(map.containsKey(USER_ID))
                setUserId((String)map.get(USER_ID));
            if(map.containsKey(PROVIDER))
                setProvider(VideoProvider.fromCode((String)map.get(PROVIDER)));
        }
    }
}