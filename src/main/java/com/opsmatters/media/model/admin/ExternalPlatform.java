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
package com.opsmatters.media.model.admin;

import com.opsmatters.media.model.OwnedEntity;

import java.time.Instant;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents an external platform.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ExternalPlatform extends OwnedEntity
{
    private String code = "";
    private String name = "";
    private PlatformStatus status = PlatformStatus.ACTIVE;

    /**
     * Default constructor.
     */
    public ExternalPlatform()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public ExternalPlatform(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public ExternalPlatform(ExternalPlatform obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ExternalPlatform obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the platform code.
     */
    public String toString()
    {
        return getCode();
    }

    /**
     * Returns the code for the platform.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code for the platform.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the name for the platform.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the platform.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the platform status.
     */
    public PlatformStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the platform status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == PlatformStatus.ACTIVE;
    }

    /**
     * Sets the platform status.
     */
    public void setStatus(PlatformStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the platform status.
     */
    public void setStatus(String status)
    {
        setStatus(PlatformStatus.valueOf(status));
    }
}