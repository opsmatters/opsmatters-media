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
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsScalesConfigBuilder;

/**
 * Represents an x-axis of a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartXAxis<T extends Serializable> extends ChartAxis<T>
{
    /**
     * Default constructor.
     */
    public ChartXAxis()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartXAxis(ChartXAxis obj)
    {
        copyAttributes(obj);
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public ChartXAxis(Map<String, Object> map)
    {
        super(map);
    }

    /**
     * Configure the axis.
     */
    @Override
    public void configure(SimpleChartJsScalesConfigBuilder scalesConfig)
    {
        if(getType() == AxisType.LOCAL_DATE)
            configureLocalDateAxis(scalesConfig.withLocalDateXAxisConfig());
        else if(getType() == AxisType.LOCAL_TIME)
            configureLocalTimeAxis(scalesConfig.withLocalTimeXAxisConfig());
        else if(getType() == AxisType.LOCAL_DATE_TIME)
            configureLocalDateTimeAxis(scalesConfig.withLocalDateTimeXAxisConfig());
        else if(getType() == AxisType.LINEAR)
            configureLinearAxis(scalesConfig.withLinearXAxisConfig());
        else if(getType() == AxisType.LOGARITHMIC)
            configureLogarithmicAxis(scalesConfig.withLogarithmicXAxisConfig());
    }
}