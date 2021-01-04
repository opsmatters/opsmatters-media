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
import nl.crashdata.chartjs.data.ChartJsChartType;

import static nl.crashdata.chartjs.data.ChartJsChartType.*;

/**
 * Creates a chart of the given types.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartFactory
{
    /**
     * Private constructor.
     */
    private ChartFactory()
    {
    }

    /**
     * Creates a new chart.
     */
    public static Chart newInstance(String type, String id)
    {
        return newInstance(ChartJsChartType.valueOf(type), id);
    }

    /**
     * Creates a new chart.
     */
    public static Chart newInstance(ChartJsChartType type, String id)
    {
        if(type == PIE || type == DOUGHNUT || type == RADAR || type == POLAR_AREA)
            return new RadialChart(id);
        return new CartesianChart(id);
    }

    /**
     * Copy constructor.
     */
    public static Chart newInstance(Chart chart)
    {
        Chart ret = newInstance(chart.getType(), chart.getId());
        ret.copyAttributes(chart);
        return ret;
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public static Chart newInstance(String id, Map<String,Object> map)
    {
        Chart ret = newInstance((String)map.get(Chart.TYPE), id);
        ret.parse(map);
        return ret;
    }
}