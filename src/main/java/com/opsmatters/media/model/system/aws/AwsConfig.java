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

package com.opsmatters.media.model.system.aws;

import com.opsmatters.media.model.ConfigElement;

/**
 * Represents the base class for AWS configuration.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class AwsConfig implements ConfigElement
{
    private String id = "";
    private String region = "";

    /**
     * Default Constructor.
     */
    protected AwsConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public AwsConfig(AwsConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(AwsConfig obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setRegion(obj.getRegion());
        }
    }

    /**
     * Returns the id of the configuration.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the configuration.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the configuration.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the region for the configuration.
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * Sets the region for the configuration.
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
}