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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a selection for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ChartSelection<E extends Serializable> implements ConfigElement
{
    private ChartParameter parameter;
    private E value;
    private ChartParameterValue defaultValue;
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
    public void copyAttributes(ChartSelection<E> obj)
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
     * Returns the parameter type for the selection.
     */
    public abstract ChartParameterType getType();

    /**
     * Returns the parameter value for the selection.
     */
    public abstract E value();

    /**
     * Returns the parameter for the selection.
     */
    public ChartParameter getParameter()
    {
        return parameter;
    }

    /**
     * Sets the parameter for the selection.
     */
    public void setParameter(ChartParameter parameter)
    {
        this.parameter = parameter;
    }

    /**
     * Sets the parameter for the selection.
     */
    public void setParameter(String parameter)
    {
        setParameter(ChartParameter.valueOf(parameter));
    }

    /**
     * Returns the parameter value for the selection.
     */
    public E getValue()
    {
        return value;
    }

    /**
     * Sets the parameter value for the selection.
     */
    public void setValue(E value)
    {
        this.value = value;
    }

    /**
     * Returns the default default value for the selection.
     */
    public ChartParameterValue getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Sets the parameter default value for the selection.
     */
    public void setDefaultValue(ChartParameterValue defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the parameter default Value for the selection.
     */
    public void setDefaultValue(String defaultValue)
    {
        setDefaultValue(ChartParameterValue.valueOf(defaultValue));
    }

    /**
     * Returns <CODE>true</CODE> if the selection is a list of items.
     */
    public boolean isMultiple()
    {
        return multiple;
    }

    /**
     * Set to <CODE>true</CODE> if the selection is a list of items.
     */
    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    /**
     * Builder to make configuration construction easier.
     */
    public abstract static class Builder<E extends Serializable, T extends ChartSelection<E>, B extends Builder<E,T,B>>
        implements ConfigParser<ChartSelection<E>>
    {
        // The config attribute names
        private static final String VALUE = "value";
        private static final String DEFAULT = "default";
        private static final String MULTIPLE = "multiple";

        private ChartSelection<E> ret = null;

        /**
         * Sets the selection.
         * @param selection The selection
         */
        public void set(ChartSelection<E> selection)
        {
            ret = selection;
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public B parse(Map<String, Object> map)
        {
            if(map.containsKey(VALUE))
                ret.setValue((E)map.get(VALUE));
            if(map.containsKey(DEFAULT))
                ret.setDefaultValue((String)map.get(DEFAULT));
            if(map.containsKey(MULTIPLE))
                ret.setMultiple((Boolean)map.get(MULTIPLE));

            return self();
        }

        /**
         * Returns this object.
         * @return This object
         */
        protected abstract B self();

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public abstract T build();
    }
}