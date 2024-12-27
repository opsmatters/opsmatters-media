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
     * Creates a new chart builder.
     */
    public static Chart.Builder builder(String type, String id)
    {
        if(type.equals(MetricChart.TYPE))
        {
            return MetricChart.builder(id);
        }
        else if(type.equals(ListChart.TYPE))
        {
            return ListChart.builder(id);
        }
        else if(type.equals(PIE.name()) || type.equals(DOUGHNUT.name())
            || type.equals(RADAR.name()) || type.equals(POLAR_AREA.name()))
        {
            return RadialChart.builder(id);
        }

        return CartesianChart.builder(id);
    }

    /**
     * Copy constructor.
     */
    public static Chart newInstance(Chart chart)
    {
        Chart ret = builder(chart.getType(), chart.getId()).build();
        ret.copyAttributes(chart);
        return ret;
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public static Chart newInstance(String id, Map<String,Object> map)
    {
        return builder((String)map.get(Chart.Builder.TYPE), id)
            .parse(map)
            .build();
    }
}