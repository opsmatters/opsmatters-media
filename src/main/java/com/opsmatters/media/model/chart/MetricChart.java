/*
 * Copyright 2021 Gerald Curley
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
import java.util.ArrayList;
import java.util.Map;
import java.io.Serializable;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a chart containing metrics.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MetricChart<E extends Serializable> extends Chart<E>
{
    public static final String TYPE = "METRIC";

    private List<ChartMetric<E>> metrics = new ArrayList<ChartMetric<E>>();

    /**
     * Constructor that takes an id.
     */
    protected MetricChart(String id)
    {
        super(id);
        setType(TYPE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MetricChart<E> obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(ChartMetric<E> metric : obj.getMetrics())
                addMetric(new ChartMetric<E>(metric));
        }
    }

    /**
     * Adds a metric to the metrics for the chart.
     */
    public List<ChartMetric<E>> getMetrics()
    {
        return this.metrics;
    }

    /**
     * Adds a metric to the metrics for the chart.
     */
    public void addMetric(ChartMetric<E> metric)
    {
        this.metrics.add(metric);
    }

    /**
     * Returns the number of metrics.
     */
    public int numMetrics()
    {
        return metrics.size();
    }

    /**
     * Returns the metric at the given index.
     */
    public ChartMetric<E> getMetric(int i)
    {
        return metrics.get(i);
    }

    /**
     * Create the metric configuration.
     */
    public MetricsConfig.Builder configure()
    {
        return MetricsConfig.builder();
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
    public static class Builder<E extends Serializable>
        extends Chart.Builder<E, MetricChart<E>, Builder<E>>
    {
        // The config attribute names
        private static final String METRICS = "metrics";

        private MetricChart ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the chart
         */
        public Builder(String id)
        {
            ret = new MetricChart(id);
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

            if(map.containsKey(METRICS))
            {
                List<Map<String,Object>> metrics = (List<Map<String,Object>>)map.get(METRICS);
                for(Map<String,Object> config : metrics)
                    ret.addMetric(ChartMetric.builder()
                        .parse((Map<String,Object>)config).build());
            }

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
        public MetricChart build()
        {
            return ret;
        }
    }
}