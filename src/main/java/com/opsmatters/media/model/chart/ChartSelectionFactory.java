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
 * Creates a selection for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartSelectionFactory
{
    /**
     * Private constructor.
     */
    private ChartSelectionFactory()
    {
    }

    /**
     * Create a new selection.
     */
    public static ChartSelection newInstance(String parameter)
    {
        return newInstance(Parameter.valueOf(parameter));
    }

    /**
     * Create a new selection.
     */
    public static ChartSelection newInstance(Parameter parameter)
    {
        if(parameter == Parameter.FROM_DATE || parameter == Parameter.TO_DATE)
            return new LocalDateTimeSelection(parameter);
        else if(parameter == Parameter.ORGANISATIONS)
            return new StringSelection(parameter, true);
        return new StringSelection(parameter);
    }

    /**
     * Copy constructor.
     */
    public static ChartSelection newInstance(ChartSelection<?> selection)
    {
        ChartSelection ret = newInstance(selection.getParameter());
        ret.copyAttributes(selection);
        return ret;
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public static ChartSelection newInstance(String parameter, Map<String,Object> map)
    {
        ChartSelection ret = newInstance(parameter);
        ret.parse(map);
        return ret;
    }
}