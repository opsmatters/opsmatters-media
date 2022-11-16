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
import static com.opsmatters.media.model.chart.ChartParameter.*;

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
     * Create a new selection builder.
     */
    public static ChartSelection.Builder builder(String parameter)
    {
        return builder(ChartParameter.valueOf(parameter));
    }

    /**
     * Create a new selection builder.
     */
    public static ChartSelection.Builder builder(ChartParameter parameter)
    {
        if(parameter == FROM_DATE || parameter == TO_DATE)
            return LocalDateTimeSelection.builder(parameter);
        return StringSelection.builder(parameter, parameter.multiple());
    }

    /**
     * Copy constructor.
     */
    public static ChartSelection newInstance(ChartSelection<?> selection)
    {
        ChartSelection ret = builder(selection.getParameter()).build();
        ret.copyAttributes(selection);
        return ret;
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public static ChartSelection newInstance(String parameter, Map<String,Object> map)
    {
        return builder(parameter)
            .parse(map)
            .build();
    }
}