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
import nl.crashdata.chartjs.data.ChartJsChartType;
import nl.crashdata.chartjs.data.ChartJsInteractionMode;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsOptionsBuilder;

/**
 * Represents a chart containing datasets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ChartJsChart<E extends Serializable> extends Chart<E>
{
    public static final String DATASETS = "datasets";

    private List<ChartDataset<E>> datasets = new ArrayList<ChartDataset<E>>();

    /**
     * Constructor that takes an id.
     */
    protected ChartJsChart(String id)
    {
        super(id);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartJsChart<E> obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(ChartDataset<E> dataset : obj.getDatasets())
                addDataset(new ChartDataset<E>(dataset));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    protected void parse(Map<String, Object> map)
    {
        super.parse(map);

        if(map.containsKey(DATASETS))
        {
            List<Map<String,Object>> datasets = (List<Map<String,Object>>)map.get(DATASETS);
            for(Map<String,Object> config : datasets)
                addDataset(new ChartDataset<E>(getChartJsChartType(), (Map<String,Object>)config));
        }
    }

    /**
     * Returns the type of the chart.
     */
    public ChartJsChartType getChartJsChartType()
    {
        return ChartJsChartType.valueOf(getType());
    }

    /**
     * Adds a dataset to the datasets for the chart.
     */
    public List<ChartDataset<E>> getDatasets()
    {
        return this.datasets;
    }

    /**
     * Adds a dataset to the datasets for the chart.
     */
    public void addDataset(ChartDataset<E> dataset)
    {
        this.datasets.add(dataset);
    }

    /**
     * Returns the number of datasets.
     */
    public int numDatasets()
    {
        return datasets.size();
    }

    /**
     * Returns the dataset at the given index.
     */
    public ChartDataset<E> getDataset(int i)
    {
        return datasets.get(i);
    }

    /**
     * Returns the config for the current chart type.
     */
    public abstract SimpleChartJsConfigBuilder<E> configure();

    /**
     * Configure the config options.
     */
    protected void configure(SimpleChartJsOptionsBuilder options)
    {
        options.withResponsive(true);
        options.hoverConfig().withIntersect(true).withMode(ChartJsInteractionMode.NEAREST);
        options.tooltipConfig().withIntersect(false).withMode(ChartJsInteractionMode.INDEX);
    }
}