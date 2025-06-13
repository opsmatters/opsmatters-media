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
 * Represents the database configuration.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DatabaseConfig implements ConfigElement
{
    private String id = "";
    private String name = "";
    private String type = "";
    private String hostname = "";
    private int port = -1;
    private String parameters = "";
    private int connectTimeout = 10;
    private long pingDelay = -1L;
    private long pingInterval = -1L;

    /**
     * Constructor that takes an id.
     */
    protected DatabaseConfig(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public DatabaseConfig(DatabaseConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DatabaseConfig obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setName(obj.getName());
            setType(obj.getType());
            setHostname(obj.getHostname());
            setPort(obj.getPort());
            setParameters(obj.getParameters());
            setConnectTimeout(obj.getConnectTimeout());
            setPingDelay(obj.getPingDelay());
            setPingInterval(obj.getPingInterval());
        }
    }

    /**
     * Returns the id of the database configuration.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the database configuration.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the database configuration.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the name for the database configuration.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the database configuration.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the type for the database configuration.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type for the database configuration.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the hostname for the database configuration.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the hostname for the database configuration.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * Returns the port for the database configuration.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Sets the port for the database configuration.
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Returns the parameters for the database configuration.
     */
    public String getParameters()
    {
        return parameters;
    }

    /**
     * Sets the parameters for the database configuration.
     */
    public void setParameters(String parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Returns the connect timeout for the database configuration.
     */
    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout for the database configuration.
     */
    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Returns the ping delay for the database configuration.
     */
    public long getPingDelay()
    {
        return pingDelay;
    }

    /**
     * Sets the ping delay for the database configuration.
     */
    public void setPingDelay(long pingDelay)
    {
        this.pingDelay = pingDelay;
    }

    /**
     * Returns the ping interval for the database configuration.
     */
    public long getPingInterval()
    {
        return pingInterval;
    }

    /**
     * Sets the ping interval for the database configuration.
     */
    public void setPingInterval(long pingInterval)
    {
        this.pingInterval = pingInterval;
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
    public static class Builder implements ConfigParser<DatabaseConfig>
    {
        // The config attribute names
        private static final String NAME = "name";
        private static final String TYPE = "type";
        private static final String HOSTNAME = "hostname";
        private static final String PORT = "port";
        private static final String PARAMETERS = "parameters";
        private static final String CONNECT_TIMEOUT = "connect-timeout";
        private static final String PING_DELAY = "ping-delay";
        private static final String PING_INTERVAL = "ping-interval";

        private DatabaseConfig ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new DatabaseConfig(id);
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
            if(map.containsKey(TYPE))
                ret.setType((String)map.get(TYPE));
            if(map.containsKey(HOSTNAME))
                ret.setHostname((String)map.get(HOSTNAME));
            if(map.containsKey(PORT))
                ret.setPort((Integer)map.get(PORT));
            if(map.containsKey(PARAMETERS))
                ret.setParameters((String)map.get(PARAMETERS));
            if(map.containsKey(CONNECT_TIMEOUT))
                ret.setConnectTimeout((Integer)map.get(CONNECT_TIMEOUT));
            if(map.containsKey(PING_DELAY))
                ret.setPingDelay((Integer)map.get(PING_DELAY));
            if(map.containsKey(PING_INTERVAL))
                ret.setPingInterval((Integer)map.get(PING_INTERVAL));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public DatabaseConfig build()
        {
            return ret;
        }
    }
}