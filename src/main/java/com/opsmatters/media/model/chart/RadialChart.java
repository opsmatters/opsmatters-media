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

import java.util.List;
import java.util.Map;
import nl.crashdata.chartjs.data.ChartJsChartType;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;

/**
 * Represents a chart containing a single radial axis with datasets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RadialChart<N extends Number> extends Chart<N>
{
    public static final String LABELS = "labels";

    private List<String> labels;

    /**
     * Constructor that takes an id.
     */
    public RadialChart(String id)
    {
        super(id);
    }

    /**
     * Copy constructor.
     */
    public RadialChart(RadialChart<N> obj)
    {
        super(obj.getId());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RadialChart<N> obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);

            setLabels(obj.getLabels());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    @Override
    protected void parse(Map<String, Object> map)
    {
        super.parse(map);

        if(map.containsKey(LABELS))
            setLabels((List<String>)map.get(LABELS));
    }

    /**
     * Returns the labels for the chart.
     */
    public List<String> getLabels()
    {
        return labels;
    }

    /**
     * Sets the labels for the chart.
     */
    public void setLabels(List<String> labels)
    {
        this.labels = labels;
    }

    /**
     * Returns the config for the current chart type.
     */
    @Override
    public SimpleChartJsConfigBuilder<N> configure()
    {
        SimpleChartJsConfigBuilder<N> ret = null;

        if(getType() == ChartJsChartType.PIE)
            ret = SimpleChartJsConfigBuilder.pieChart();
        else if(getType() == ChartJsChartType.RADAR)
            ret = SimpleChartJsConfigBuilder.radarChart();
        else if(getType() == ChartJsChartType.POLAR_AREA)
            ret = SimpleChartJsConfigBuilder.polarAreaChart();
        else if(getType() == ChartJsChartType.DOUGHNUT)
            ret = SimpleChartJsConfigBuilder.doughnut();

        if(ret != null)
        {
            configure(ret.options());
            ret.data().withLabels(labels);
        }

        return ret;
    }
}