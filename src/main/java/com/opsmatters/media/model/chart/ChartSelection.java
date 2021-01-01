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

package com.opsmatters.media.model.chart;

import java.util.Map;
import java.io.Serializable;

/**
 * Represents a selection for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ChartSelection<T extends Serializable> implements Serializable
{
    public static final String VALUE = "value";
    public static final String DEFAULT = "default";
    public static final String MULTIPLE = "multiple";

    private ParameterName parameter;
    private T value;
    private ParameterValue defaultValue;
    private boolean multiple = false;

    /**
     * Default constructor.
     */
    public ChartSelection()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartSelection(ChartSelection obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartSelection<T> obj)
    {
        if(obj != null)
        {
            setParameter(obj.getParameter());
            setValue(obj.getValue());
            setDefaultValue(obj.getDefaultValue());
            setMultiple(obj.isMultiple());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public ChartSelection(Map<String, Object> map)
    {
        parse(map);
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(VALUE))
            setValue((T)map.get(VALUE));
        if(map.containsKey(DEFAULT))
            setDefaultValue((String)map.get(DEFAULT));
        if(map.containsKey(MULTIPLE))
            setMultiple((Boolean)map.get(MULTIPLE));
    }

    /**
     * Returns the parameter type for the selection.
     */
    public abstract ParameterType getType();

    /**
     * Returns the parameter value for the selection.
     */
    public abstract T value();

    /**
     * Returns the parameter for the selection.
     */
    public ParameterName getParameter()
    {
        return parameter;
    }

    /**
     * Sets the parameter for the selection.
     */
    public void setParameter(ParameterName parameter)
    {
        this.parameter = parameter;
    }

    /**
     * Sets the parameter for the selection.
     */
    public void setParameter(String parameter)
    {
        setParameter(ParameterName.valueOf(parameter));
    }

    /**
     * Returns the parameter value for the selection.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * Sets the parameter value for the selection.
     */
    public void setValue(T value)
    {
        this.value = value;
    }

    /**
     * Returns the default default value for the selection.
     */
    public ParameterValue getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Sets the parameter default value for the selection.
     */
    public void setDefaultValue(ParameterValue defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the parameter default Value for the selection.
     */
    public void setDefaultValue(String defaultValue)
    {
        setDefaultValue(ParameterValue.valueOf(defaultValue));
    }

    /**
     * Returns <CODE>true</CODE> if the selection is list of items.
     */
    public boolean isMultiple()
    {
        return multiple;
    }

    /**
     * Set to <CODE>true</CODE> if the selection is list of items.
     */
    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }
}