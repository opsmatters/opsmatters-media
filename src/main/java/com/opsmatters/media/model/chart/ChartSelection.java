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
public abstract class ChartSelection<T extends Serializable>
{
    public static final String PARAMETER = "parameter";
    public static final String VALUE = "value";
    public static final String DEFAULT = "default";

    private Parameter parameter;
    private T value;
    private ParameterDefaultName defaultName;

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
            setDefaultName(obj.getDefaultName());
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
        if(map.containsKey(PARAMETER))
            setParameter((String)map.get(PARAMETER));
        if(map.containsKey(VALUE))
            setValue((T)map.get(VALUE));
        if(map.containsKey(DEFAULT))
            setDefaultName((String)map.get(DEFAULT));
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
    public Parameter getParameter()
    {
        return parameter;
    }

    /**
     * Sets the parameter for the selection.
     */
    public void setParameter(Parameter parameter)
    {
        this.parameter = parameter;
    }

    /**
     * Sets the parameter for the selection.
     */
    public void setParameter(String parameter)
    {
        setParameter(Parameter.valueOf(parameter));
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
    public ParameterDefaultName getDefaultName()
    {
        return defaultName;
    }

    /**
     * Sets the parameter default name for the selection.
     */
    public void setDefaultName(ParameterDefaultName defaultName)
    {
        this.defaultName = defaultName;
    }

    /**
     * Sets the parameter default name for the selection.
     */
    public void setDefaultName(String defaultName)
    {
        setDefaultName(ParameterDefaultName.valueOf(defaultName));
    }
}