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
package com.opsmatters.media.model.social;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.site.Site;

/**
 * Class representing the list of social media channels.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannels implements java.io.Serializable
{
    static private Map<String,SocialChannel> map = new LinkedHashMap<String,SocialChannel>();

    /**
     * Private constructor.
     */
    private SocialChannels()
    {
    }

    /**
     * Clears the social channels.
     */
    public static void clear()
    {
        map.clear();
    }

    /**
     * Returns the social channel with the given id.
     */
    public static SocialChannel getChannel(String id)
    {
        return map.get(id);
    }

    /**
     * Adds the social channel with the given name.
     */
    public static void add(SocialChannel channel)
    {
        map.put(channel.getId(), channel);
    }

    /**
     * Returns the count of social channels.
     */
    public static int size()
    {
        return map.size();
    }

    /**
     * Returns the list of social channels.
     */
    public static List<SocialChannel> getChannels(Site site)
    {
        List<SocialChannel> ret = new ArrayList<SocialChannel>();
        for(SocialChannel channel : map.values())
        {
            if(channel.getSiteId().equals(site.getId()))
                ret.add(channel);
        }

        return ret;
    }
}