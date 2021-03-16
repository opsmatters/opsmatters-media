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

package com.opsmatters.media.model.site;

import java.util.Map;

/**
 * Represents the database settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DatabaseSettings
{
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PARAMETERS = "parameters";
    public static final String CONNECT_TIMEOUT = "connect-timeout";
    public static final String PING_DELAY = "ping-delay";
    public static final String PING_INTERVAL = "ping-interval";

    private String id = "";
    private String name = "";
    private String type = "";
    private String hostname = "";
    private int port = -1;
    private String username = "";
    private String parameters = "";
    private int connectTimeout = 10;
    private long pingDelay = -1L;
    private long pingInterval = -1L;

    /**
     * Default Constructor.
     */
    protected DatabaseSettings(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public DatabaseSettings(DatabaseSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DatabaseSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setName(obj.getName());
            setType(obj.getType());
            setHostname(obj.getHostname());
            setPort(obj.getPort());
            setUsername(obj.getUsername());
            setParameters(obj.getParameters());
            setConnectTimeout(obj.getConnectTimeout());
            setPingDelay(obj.getPingDelay());
            setPingInterval(obj.getPingInterval());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public DatabaseSettings(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(NAME))
            setName((String)map.get(NAME));
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));
        if(map.containsKey(HOSTNAME))
            setHostname((String)map.get(HOSTNAME));
        if(map.containsKey(PORT))
            setPort((Integer)map.get(PORT));
        if(map.containsKey(USERNAME))
            setUsername((String)map.get(USERNAME));
        if(map.containsKey(PARAMETERS))
            setParameters((String)map.get(PARAMETERS));
        if(map.containsKey(CONNECT_TIMEOUT))
            setConnectTimeout((Integer)map.get(CONNECT_TIMEOUT));
        if(map.containsKey(PING_DELAY))
            setPingDelay((Integer)map.get(PING_DELAY));
        if(map.containsKey(PING_INTERVAL))
            setPingInterval((Integer)map.get(PING_INTERVAL));
    }

    /**
     * Returns the id of the database settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the database settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the database settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the name for the database settings.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the database settings.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the type for the database settings.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type for the database settings.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the hostname for the database settings.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the hostname for the database settings.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * Returns the port for the database settings.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Sets the port for the database settings.
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Returns the username for the database settings.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username for the database settings.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns the parameters for the database settings.
     */
    public String getParameters()
    {
        return parameters;
    }

    /**
     * Sets the parameters for the database settings.
     */
    public void setParameters(String parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Returns the connect timeout for the database settings.
     */
    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout for the database settings.
     */
    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Returns the ping delay for the database settings.
     */
    public long getPingDelay()
    {
        return pingDelay;
    }

    /**
     * Sets the ping delay for the database settings.
     */
    public void setPingDelay(long pingDelay)
    {
        this.pingDelay = pingDelay;
    }

    /**
     * Returns the ping interval for the database settings.
     */
    public long getPingInterval()
    {
        return pingInterval;
    }

    /**
     * Sets the ping interval for the database settings.
     */
    public void setPingInterval(long pingInterval)
    {
        this.pingInterval = pingInterval;
    }
}