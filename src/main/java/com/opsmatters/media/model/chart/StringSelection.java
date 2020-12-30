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

/**
 * Represents a String selection for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class StringSelection extends ChartSelection<String>
{
    /**
     * Default constructor.
     */
    public StringSelection(Parameter parameter)
    {
        setParameter(parameter);
    }

    /**
     * Copy constructor.
     */
    public StringSelection(StringSelection obj)
    {
        copyAttributes(obj);
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public StringSelection(Parameter parameter, Map<String, Object> map)
    {
        super(map);
        setParameter(parameter);
    }

    /**
     * Returns the parameter type for the selection.
     */
    public ParameterType getType()
    {
        return ParameterType.STRING;
    }

    /**
     * Returns the parameter value for the selection.
     */
    @Override
    public String value()
    {
        return getValue();
    }
}