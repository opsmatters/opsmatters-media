/*
 * Copyright 2024 Gerald Curley
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
import java.util.Iterator;
import org.json.JSONObject;

/**
 * Class representing the properties for a social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPostProperties extends LinkedHashMap<String,String>
{
    private static final String ENABLED = ".enabled";

    /**
     * Constructor that takes a set of properties.
     */
    public SocialPostProperties(SocialPostProperties properties)
    {
        putAll(properties);
    }

    /**
     * Returns the properties as a JSON object.
     */
    public JSONObject toJson()
    {
        return new JSONObject(this);
    }

    /**
     * Sets the properties.
     */
    public void set(Map<String,String> properties)
    {
        clear();
        putAll(properties);
    }

    /**
     * Sets the properties from a JSON object.
     */
    public void set(JSONObject obj)
    {
        clear();
        Iterator<String> keys = obj.keys();
        while(keys.hasNext())
        {
            String key = keys.next();
            put(key, obj.getString(key));
        }
    }

    /**
     * Returns the value of the given property.
     */
    public String get(SocialPostProperty property)
    {
        return get(property.value());
    }

    /**
     * Returns <CODE>true</CODE> if the given property has been set.
     */
    public boolean containsKey(SocialPostProperty property)
    {
        return containsKey(property.value());
    }

    /**
     * Sets the value of the given property.
     */
    public void put(SocialPostProperty property, String value)
    {
        put(property.value(), value);
    }

    /**
     * Removes the given property.
     */
    public void remove(SocialPostProperty property)
    {
        remove(property.value());
    }

    /**
     * Returns <CODE>true</CODE> if the enabled property for given channel has been set.
     */
    public boolean containsEnabled(SocialChannel channel)
    {
        return containsKey(channel.getId()+ENABLED);
    }

    /**
     * Returns <CODE>true</CODE> if the given channel is enabled.
     */
    public boolean isEnabled(SocialChannel channel)
    {
        return Boolean.parseBoolean(get(channel.getId()+ENABLED));
    }

    /**
     * Set to <CODE>true</CODE> if the given channel is enabled.
     */
    public void setEnabled(SocialChannel channel, boolean enabled)
    {
        put(channel.getId()+ENABLED, Boolean.toString(enabled));
    }
}