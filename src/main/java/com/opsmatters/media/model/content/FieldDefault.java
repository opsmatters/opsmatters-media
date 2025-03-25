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
package com.opsmatters.media.model.content;

import java.time.Instant;
import java.util.logging.Logger;
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a default value for a field.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldDefault extends OwnedEntity
{
    private static final Logger logger = Logger.getLogger(FieldDefault.class.getName());

    private String name = "";
    private String value = "";
    private ContentType type;
    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public FieldDefault()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public FieldDefault(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setType(ContentType.ROUNDUP);
        setEnabled(true);
    }

    /**
     * Copy constructor.
     */
    public FieldDefault(FieldDefault obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldDefault obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setValue(obj.getValue());
            setType(obj.getType());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the name of the default.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the default.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the default.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the value of the default.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value of the default.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Returns the type of the default.
     */
    public ContentType getType()
    {
        return type;
    }

    /**
     * Returns the value of the type of the default.
     */
    public String getTypeValue()
    {
        return type != null ? type.value() : "";
    }

    /**
     * Sets the type of the default.
     */
    public void setType(ContentType type)
    {
        this.type = type;
    }

    /**
     * Sets the type of the default.
     */
    public void setType(String type)
    {
        setType(ContentType.valueOf(type));
    }

    /**
     * Sets the type of the default from a value.
     */
    public void setTypeValue(String type)
    {
        setType(ContentType.fromValue(type));
    }

    /**
     * Returns <CODE>true</CODE> if the default is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Returns <CODE>true</CODE> if this default is enabled.
     */
    public Boolean getEnabledObject()
    {
        return Boolean.valueOf(isEnabled());
    }

    /**
     * Set to <CODE>true</CODE> if the default is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set to <CODE>true</CODE> if this default is enabled.
     */
    public void setEnabledObject(Boolean enabled)
    {
        setEnabled(enabled != null && enabled.booleanValue());
    }
}