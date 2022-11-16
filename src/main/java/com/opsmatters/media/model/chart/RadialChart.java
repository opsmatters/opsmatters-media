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
public class RadialChart<N extends Number> extends ChartJsChart<N>
{
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

        if(getChartJsChartType() == ChartJsChartType.PIE)
            ret = SimpleChartJsConfigBuilder.pieChart();
        else if(getChartJsChartType() == ChartJsChartType.RADAR)
            ret = SimpleChartJsConfigBuilder.radarChart();
        else if(getChartJsChartType() == ChartJsChartType.POLAR_AREA)
            ret = SimpleChartJsConfigBuilder.polarAreaChart();
        else if(getChartJsChartType() == ChartJsChartType.DOUGHNUT)
            ret = SimpleChartJsConfigBuilder.doughnut();

        if(ret != null)
        {
            configure(ret.options());
            ret.data().withLabels(labels);
        }

        return ret;
    }

    /**
     * Returns a builder for the chart.
     * @param id The id of the chart
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make chart construction easier.
     */
    public static class Builder<N extends Number>
        extends ChartJsChart.Builder<N, RadialChart<N>, Builder<N>>
    {
        // The config attribute names
        private static final String LABELS = "labels";

        private RadialChart ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the chart
         */
        public Builder(String id)
        {
            ret = new RadialChart(id);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);

            if(map.containsKey(LABELS))
                ret.setLabels((List<String>)map.get(LABELS));

            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured chart instance
         * @return The chart instance
         */
        @Override
        public RadialChart build()
        {
            return ret;
        }
    }
}