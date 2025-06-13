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

package com.opsmatters.media.model.system;

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents the configuration for an SSH connection.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SshConfig implements ConfigElement
{
    private String id = "";
    private String hostname = "";
    private int port = -1;

    /**
     * Constructor that takes an id.
     */
    protected SshConfig(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public SshConfig(SshConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SshConfig obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setHostname(obj.getHostname());
            setPort(obj.getPort());
        }
    }

    /**
     * Returns the id of the SSH configuration.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the SSH configuration.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the SSH configuration.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the hostname for the SSH configuration.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the hostname for the SSH configuration.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * Returns the port for the SSH configuration.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Sets the port for the SSH configuration.
     */
    public void setPort(int port)
    {
        this.port = port;
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
    public static class Builder implements ConfigParser<SshConfig>
    {
        // The config attribute names
        private static final String HOSTNAME = "hostname";
        private static final String PORT = "port";

        private SshConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new SshConfig(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(HOSTNAME))
                ret.setHostname((String)map.get(HOSTNAME));
            if(map.containsKey(PORT))
                ret.setPort((Integer)map.get(PORT));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public SshConfig build()
        {
            return ret;
        }
    }
}