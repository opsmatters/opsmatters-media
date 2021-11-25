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
package com.opsmatters.media.config.social;

import java.util.Map;
import com.opsmatters.media.model.social.SocialChannel;

/**
 * Class representing the configuration of a social media channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannelConfiguration extends SocialChannel
{
    public static final String NAME = "name";
    public static final String HANDLE = "handle";
    public static final String ICON = "icon";
    public static final String PROVIDER = "provider";
    public static final String SITES = "sites";
    public static final String ENABLED = "enabled";

    /**
     * Constructor that takes an id.
     */
    public SocialChannelConfiguration(String id)
    {
        super(id);
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(NAME))
            setName((String)map.get(NAME));
        if(map.containsKey(HANDLE))
            setHandle((String)map.get(HANDLE));
        if(map.containsKey(ICON))
            setIcon((String)map.get(ICON));
        if(map.containsKey(PROVIDER))
            setProvider((String)map.get(PROVIDER));
        if(map.containsKey(SITES))
            setSites((String)map.get(SITES));
        if(map.containsKey(ENABLED))
            setEnabled((Boolean)map.get(ENABLED));
    }
}