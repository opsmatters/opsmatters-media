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

/**
 * Represents the metrics config for a metric chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MetricsConfig<N extends Number>
{
    private List<MetricConfig<N>> metrics = new ArrayList<MetricConfig<N>>();

    /**
     * Default constructor.
     */
    public MetricsConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public MetricsConfig(MetricsConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MetricsConfig<N> obj)
    {
        if(obj != null)
        {
            for(MetricConfig<N> metric : obj.getMetrics())
                addMetric(new MetricConfig<N>(metric));
        }
    }

    /**
     * Returns the metrics.
     */
    public List<MetricConfig<N>> getMetrics()
    {
        return metrics;
    }

    /**
     * Adds the given metric.
     */
    public void addMetric(MetricConfig<N> metric)
    {
        metrics.add(metric);
    }

    /**
     * Returns the metric at the given index.
     */
    public MetricConfig<N> getMetric(int i)
    {
        return metrics.get(i);
    }

    /**
     * Returns a builder for the config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make config construction easier.
     */
    public static class Builder<N extends Number>
    {
        private MetricsConfig<N> config = new MetricsConfig<N>();

        /**
         * Sets the data to use with the config.
         * @param data The data
         * @return This object
         */
        public Builder addMetric(MetricConfig<N> metric)
        {
            config.addMetric(metric);
            return this;
        }

        /**
         * Returns the configured config instance
         * @return The config instance
         */
        public MetricsConfig<N> build()
        {
            return config;
        }
    }
}