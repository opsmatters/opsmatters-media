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
package com.opsmatters.media.cache.social;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.cache.StaticCache;

/**
 * Class representing the list of social media channels.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannels extends StaticCache
{
    private static final Logger logger = Logger.getLogger(SocialChannels.class.getName());

    private static Map<String,SocialChannel> channelMap = new LinkedHashMap<String,SocialChannel>();

    /**
     * Private constructor.
     */
    private SocialChannels()
    {
    }

    /**
     * Loads the set of channels.
     */
    public static void load(List<SocialChannel> channels)
    {
        setInitialised(false);

        clear();
        for(SocialChannel channel : channels)
        {
            add(channel);
        }

        logger.info("Loaded "+size()+" social channels");

        setInitialised(true);
    }

    /**
     * Clears the social channels.
     */
    public static void clear()
    {
        channelMap.clear();
    }

    /**
     * Returns the social channel with the given code.
     */
    public static SocialChannel get(String code)
    {
        return channelMap.get(code);
    }

    /**
     * Adds the social channel with the given code.
     */
    public static void add(SocialChannel channel)
    {
        channelMap.put(channel.getCode(), channel);
    }

    /**
     * Removes the social channel with the given code.
     */
    public static void remove(SocialChannel channel)
    {
        channelMap.remove(channel.getCode());
    }

    /**
     * Returns the count of social channels.
     */
    public static int size()
    {
        return channelMap.size();
    }

    /**
     * Returns the list of social channels.
     */
    public static List<SocialChannel> getChannels(Site site)
    {
        List<SocialChannel> ret = new ArrayList<SocialChannel>();
        for(SocialChannel channel : channelMap.values())
        {
            if(channel.hasSite(site))
                ret.add(channel);
        }

        return ret;
    }
}