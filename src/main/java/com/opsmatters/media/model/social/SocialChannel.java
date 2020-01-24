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
    public static final String PROVIDER = "provider";
    public static final String HANDLE = "handle";
    public static final String SCREEN_NAME = "screen-name";
    public static final String ENABLED = "enabled";

    private String name = "";
    private String handle = "";
    private String screenName = "";
    private SocialProvider provider;
    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public SocialChannel(String name)
    {
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public SocialChannel(SocialChannel obj)
    {
        if(obj != null)
        {
            setName(obj.getName());
            setHandle(obj.getHandle());
            setScreenName(obj.getScreenName());
            setProvider(obj.getProvider());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the name.
     */
    public String toString()
    {
        return getName();
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
     * Returns the channel screen name.
     */
    public String getScreenName()
    {
        return screenName;
    }

    /**
     * Sets the channel screen name.
     */
    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
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
        if(map.containsKey(SCREEN_NAME))
            setScreenName((String)map.get(SCREEN_NAME));
        if(map.containsKey(PROVIDER))
            setProvider((String)map.get(PROVIDER));
        if(map.containsKey(ENABLED))
            setEnabled((Boolean)map.get(ENABLED));
    }
}