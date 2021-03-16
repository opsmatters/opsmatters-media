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

/**
 * Class representing a social media channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannel implements java.io.Serializable
{
    public static final String NAME = "name";
    public static final String HANDLE = "handle";
    public static final String PROVIDER = "provider";
    public static final String SITE = "site";
    public static final String ENABLED = "enabled";

    private String id = "";
    private String name = "";
    private String handle = "";
    private SocialProvider provider;
    private String siteId = "";
    private boolean enabled = false;

    /**
     * Default constructor.
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
            setProvider(obj.getProvider());
            setSiteId(obj.getSiteId());
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
     * Returns the channel site id.
     */
    public String getSiteId()
    {
        return siteId;
    }

    /**
     * Sets the channel site id.
     */
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
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
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(HANDLE))
            setHandle((String)map.get(HANDLE));
        if(map.containsKey(NAME))
            setName((String)map.get(NAME));
        if(map.containsKey(PROVIDER))
            setProvider((String)map.get(PROVIDER));
        if(map.containsKey(SITE))
            setSiteId((String)map.get(SITE));
        if(map.containsKey(ENABLED))
            setEnabled((Boolean)map.get(ENABLED));
    }
}