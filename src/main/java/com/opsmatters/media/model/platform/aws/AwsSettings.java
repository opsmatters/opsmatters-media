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

package com.opsmatters.media.model.platform.aws;

import java.util.Map;

/**
 * Represents the base class for AWS settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class AwsSettings
{
    public static final String REGION = "region";

    private String id = "";
    private String region = "";

    /**
     * Default Constructor.
     */
    protected AwsSettings()
    {
    }

    /**
     * Copy constructor.
     */
    public AwsSettings(AwsSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(AwsSettings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setRegion(obj.getRegion());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public AwsSettings(String id, Map<String, Object> map)
    {
        setId(id);

        if(map.containsKey(REGION))
            setRegion((String)map.get(REGION));
    }

    /**
     * Returns the id of the settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the region for the settings.
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * Sets the region for the settings.
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
}