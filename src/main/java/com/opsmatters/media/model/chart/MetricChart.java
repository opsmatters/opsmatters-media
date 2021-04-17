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

/**
 * Represents a chart containing metrics.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MetricChart<E extends Serializable> extends Chart<E>
{
    public static final String METRICS = "metrics";
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
     * Reads the object from the given YAML Document.
     */
    protected void parse(Map<String, Object> map)
    {
        super.parse(map);
        if(map.containsKey(METRICS))
        {
            List<Map<String,Object>> metrics = (List<Map<String,Object>>)map.get(METRICS);
            for(Map<String,Object> config : metrics)
                addMetric(new ChartMetric<E>((Map<String,Object>)config));
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
}