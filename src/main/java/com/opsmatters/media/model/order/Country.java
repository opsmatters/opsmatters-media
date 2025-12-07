/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model.order;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a country.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Country extends BaseEntity
{
    private String code = "";
    private String name = "";
    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public Country()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Country(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setEnabled(true);
    }

    /**
     * Copy constructor.
     */
    public Country(Country obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Country obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the country name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the country code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the country code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the country name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the country name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the country is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this country is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the country is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this country is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }
}