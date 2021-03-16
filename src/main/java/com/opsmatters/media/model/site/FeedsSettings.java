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
 * Represents the feeds settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedsSettings
{
    public static final String PATH = "path";
    public static final String USERNAME = "username";

    private String id = "";
    private String path = "";
    private String username = "";

    /**
     * Default Constructor.
     */
    protected FeedsSettings(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public FeedsSettings(FeedsSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FeedsSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setPath(obj.getPath());
            setUsername(obj.getUsername());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public FeedsSettings(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(PATH))
            setPath((String)map.get(PATH));
        if(map.containsKey(USERNAME))
            setUsername((String)map.get(USERNAME));
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
}