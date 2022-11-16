/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.model.platform;

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents the feeds settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedsConfig implements ConfigElement
{
    private String id = "";
    private String path = "";
    private String username = "";

    /**
     * Constructor that takes an id.
     */
    protected FeedsConfig(String id)
    {
        setId(id);
    }

    /**
     * Constructor that takes an id.
     */
    public FeedsConfig(FeedsConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FeedsConfig obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setPath(obj.getPath());
            setUsername(obj.getUsername());
        }
    }

    /**
     * Returns the id of the feeds settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the feeds settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the feeds settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the path for the feeds settings.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the path for the feeds settings.
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Returns the username for the feeds settings.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username for the feeds settings.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns a builder for the configuration.
     * @param id The id of the configuration
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<FeedsConfig>
    {
        // The config attribute names
        private static final String PATH = "path";
        private static final String USERNAME = "username";

        private FeedsConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new FeedsConfig(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(PATH))
                ret.setPath((String)map.get(PATH));
            if(map.containsKey(USERNAME))
                ret.setUsername((String)map.get(USERNAME));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public FeedsConfig build()
        {
            return ret;
        }
    }
}