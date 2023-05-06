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
import java.util.HashMap;
import java.util.List;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a social media channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
//public class SocialChannel implements java.io.Serializable
public class SocialChannel implements ConfigElement
{
    private String id = "";
    private String name = "";
    private String handle = "";
    private String icon = "";
    private SocialProvider provider;
    private String sites = "";
    private Map<String,String> siteMap = new HashMap<String,String>();
    private int delay = -1;
    private int maxPosts = -1;
    private boolean enabled = false;

    /**
     * Constructor that takes an id.
     */
    public SocialChannel(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public SocialChannel(SocialChannel obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setName(obj.getName());
            setHandle(obj.getHandle());
            setIcon(obj.getIcon());
            setProvider(obj.getProvider());
            setSites(obj.getSites());
            setDelay(obj.getDelay());
            setMaxPosts(obj.getMaxPosts());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the id.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the channel id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the channel id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the channel name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the channel name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the channel handle.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the channel handle.
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Returns the channel icon.
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Sets the channel icon.
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the provider for the channel.
     */
    public SocialProvider getProvider()
    {
        return provider;
    }

    /**
     * Sets the provider for the channel.
     */
    public void setProvider(String code)
    {
        setProvider(SocialProvider.fromCode(code));
    }

    /**
     * Sets the provider for the channel.
     */
    public void setProvider(SocialProvider provider)
    {
        this.provider = provider;
    }

    /**
     * Returns the channel sites.
     */
    public String getSites()
    {
        return sites;
    }

    /**
     * Sets the channel sites.
     */
    public void setSites(String sites)
    {
        this.sites = sites;

        siteMap.clear();
        List<String> siteList = StringUtils.toList(sites);
        for(String site : siteList)
            siteMap.put(site, site);
    }

    /**
     * Returns <CODE>true</CODE> if this channel is configured for the given site.
     */
    public boolean hasSite(Site site)
    {
        return siteMap.get(site.getId()) != null;
    }

    /**
     * Returns the delay between social posts for this channel.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Sets the delay between social posts for this channel.
     */
    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    /**
     * Returns the maximum social posts per session for this channel.
     */
    public int getMaxPosts()
    {
        return maxPosts;
    }

    /**
     * Sets the maximum social posts per session for this channel.
     */
    public void setMaxPosts(int maxPosts)
    {
        this.maxPosts = maxPosts;
    }

    /**
     * Returns <CODE>true</CODE> if this channel is enabled to send messages.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this channel is enabled to send messages.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Returns a builder for the social channel.
     * @param id The id of the social channel
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make social channel construction easier.
     */
    public static class Builder implements ConfigParser<SocialChannel>
    {
        // The config attribute names
        private static final String NAME = "name";
        private static final String HANDLE = "handle";
        private static final String ICON = "icon";
        private static final String PROVIDER = "provider";
        private static final String SITES = "sites";
        private static final String DELAY = "delay";
        private static final String MAX_POSTS = "max-posts";
        private static final String ENABLED = "enabled";

        private SocialChannel ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new SocialChannel(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(NAME))
                ret.setName((String)map.get(NAME));
            if(map.containsKey(HANDLE))
                ret.setHandle((String)map.get(HANDLE));
            if(map.containsKey(ICON))
                ret.setIcon((String)map.get(ICON));
            if(map.containsKey(PROVIDER))
                ret.setProvider((String)map.get(PROVIDER));
            if(map.containsKey(SITES))
                ret.setSites((String)map.get(SITES));
            if(map.containsKey(MAX_POSTS))
                ret.setMaxPosts((Integer)map.get(MAX_POSTS));
            if(map.containsKey(DELAY))
                ret.setDelay((Integer)map.get(DELAY));
            if(map.containsKey(ENABLED))
                ret.setEnabled((Boolean)map.get(ENABLED));

            return this;
        }

        /**
         * Returns the configured social channel instance
         * @return The social channel instance
         */
        public SocialChannel build()
        {
            return ret;
        }
    }
}