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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a social media channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannel implements java.io.Serializable
{
    private String id = "";
    private String name = "";
    private String handle = "";
    private String icon = "";
    private SocialProvider provider;
    private String sites = "";
    private Map<String,String> siteMap = new HashMap<String,String>();
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
}