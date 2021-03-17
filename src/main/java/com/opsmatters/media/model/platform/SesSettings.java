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
 * Represents the SES settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SesSettings
{
    public static final String FROM = "from";
    public static final String REGION = "region";

    private String id = "";
    private String from = "";
    private String region = "";

    /**
     * Default Constructor.
     */
    protected SesSettings(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public SesSettings(SesSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SesSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setFrom(obj.getFrom());
            setRegion(obj.getRegion());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public SesSettings(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(FROM))
            setFrom((String)map.get(FROM));
        if(map.containsKey(REGION))
            setRegion((String)map.get(REGION));
    }

    /**
     * Returns the id of the SES settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the SES settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the SES settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the from address for the SES settings.
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * Sets the from address for the SES settings.
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /**
     * Returns the region for the SES settings.
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * Sets the region for the SES settings.
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
}