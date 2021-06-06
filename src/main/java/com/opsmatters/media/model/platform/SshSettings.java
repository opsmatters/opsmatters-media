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

/**
 * Represents the SSH settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SshSettings implements java.io.Serializable
{
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";
    public static final String USERNAME = "username";

    private String id = "";
    private String hostname = "";
    private int port = -1;
    private String username = "";

    /**
     * Default Constructor.
     */
    protected SshSettings(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public SshSettings(SshSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SshSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setHostname(obj.getHostname());
            setPort(obj.getPort());
            setUsername(obj.getUsername());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public SshSettings(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(HOSTNAME))
            setHostname((String)map.get(HOSTNAME));
        if(map.containsKey(PORT))
            setPort((Integer)map.get(PORT));
        if(map.containsKey(USERNAME))
            setUsername((String)map.get(USERNAME));
    }

    /**
     * Returns the id of the SSH settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the SSH settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the SSH settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the hostname for the SSH settings.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the hostname for the SSH settings.
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * Returns the port for the SSH settings.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Sets the port for the SSH settings.
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Returns the username for the SSH settings.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username for the SSH settings.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }
}