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
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a content output column.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OutputColumn extends BaseEntity
{
    private static final Logger logger = Logger.getLogger(OutputColumn.class.getName());

    private String name = "";
    private String value = "";
    private ContentType type;
    private int position = -1;
    private boolean enabled = false;

    /**
     * Default constructor.
     */
    public OutputColumn()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public OutputColumn(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public OutputColumn(OutputColumn obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OutputColumn obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setValue(obj.getValue());
            setType(obj.getType());
            setPosition(obj.getPosition());
            setEnabled(obj.isEnabled());
        }
    }

    /**
     * Returns the name of the column.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the column.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the column.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the value of the column.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value of the column.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Returns the type of the column.
     */
    public ContentType getType()
    {
        return type;
    }

    /**
     * Sets the type of the column.
     */
    public void setType(ContentType type)
    {
        this.type = type;
    }

    /**
     * Sets the type of the column.
     */
    public void setType(String type)
    {
        setType(ContentType.valueOf(type));
    }

    /**
     * Returns the position of the column.
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Sets the position of the column.
     */
    public void setPosition(int position)
    {
        this.position = position;
    }

    /**
     * Returns <CODE>true</CODE> if the column is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set to <CODE>true</CODE> if the column is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}