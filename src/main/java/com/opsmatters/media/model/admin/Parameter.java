/*
 * Copyright 2020 Gerald Curley
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

import com.opsmatters.media.model.BaseItem;

/**
 * Class representing a parameter.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Parameter extends BaseItem
{
    private ParameterType type;
    private ParameterName name;
    private String value = "";

    /**
     * Default constructor.
     */
    public Parameter()
    {
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Parameter obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setType(obj.getType());
            setName(obj.getName());
            setValue(obj.getValue());
        }
    }

    /**
     * Returns the name.
     */
    public String toString()
    {
        return getName().value();
    }

    /**
     * Returns the type.
     */
    public ParameterType getType()
    {
        return type;
    }

    /**
     * Sets the type.
     */
    public void setType(ParameterType type)
    {
        this.type = type;
    }

    /**
     * Returns <CODE>true</CODE> if the type has been set.
     */
    public boolean hasType()
    {
        return type != null;
    }

    /**
     * Returns the name.
     */
    public ParameterName getName()
    {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(ParameterName name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the name has been set.
     */
    public boolean hasName()
    {
        return name != null;
    }

    /**
     * Returns the value.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Returns the value as an integer.
     */
    public int getValueAsInt()
    {
        return Integer.parseInt(value);
    }

    /**
     * Sets the value.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Returns <CODE>true</CODE> if the value has been set.
     */
    public boolean hasValue()
    {
        return value != null && value.length() > 0;
    }
}