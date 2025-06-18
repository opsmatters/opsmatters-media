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

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents an external provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ExternalProvider extends BaseEntity
{
    private String code = "";
    private String name = "";
    private ProviderStatus status = ProviderStatus.ACTIVE;

    /**
     * Default constructor.
     */
    public ExternalProvider()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public ExternalProvider(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public ExternalProvider(ExternalProvider obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ExternalProvider obj)
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
     * Returns the provider code.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the code for the provider.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code for the provider.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the code for the provider has been set.
     */
    public boolean hasCode()
    {
        return getCode() != null && getCode().length() > 0;
    }

    /**
     * Returns the name for the provider.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the provider.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the provider status.
     */
    public ProviderStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the provider status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == ProviderStatus.ACTIVE;
    }

    /**
     * Sets the provider status.
     */
    public void setStatus(ProviderStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the provider status.
     */
    public void setStatus(String status)
    {
        setStatus(ProviderStatus.valueOf(status));
    }
}