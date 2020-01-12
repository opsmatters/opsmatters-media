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

    private String name = "";
    private String handle = "";
    private SocialProvider provider;

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
            setProvider(obj.getProvider());
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
     * Returns the post name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the post name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the post handle.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the post handle.
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
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
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(HANDLE))
            setHandle((String)map.get(HANDLE));
        if(map.containsKey(PROVIDER))
            setProvider((String)map.get(PROVIDER));
    }
}