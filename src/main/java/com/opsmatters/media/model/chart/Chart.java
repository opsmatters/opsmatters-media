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
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
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
public abstract class Chart<E extends Serializable>
{
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String DATASETS = "datasets";
    public static final String SELECTIONS = "selections";

    private String id = "";
    private String title = "";
    private ChartJsChartType type;
    private List<ChartDataset<E>> datasets = new ArrayList<ChartDataset<E>>();
    private Map<ChartParameter,ChartSelection<?>> selections = new LinkedHashMap<ChartParameter,ChartSelection<?>>();

    /**
     * Constructor that takes an id.
     */
    protected Chart(String id)
    {
        setId(id);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Chart<E> obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setTitle(obj.getTitle());
            setType(obj.getType());
            for(ChartDataset<E> dataset : obj.getDatasets())
                addDataset(new ChartDataset<E>(dataset));
            for(ChartSelection<?> selection : obj.getSelections().values())
                addSelection(ChartSelectionFactory.newInstance(selection));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    protected void parse(Map<String, Object> map)
    {
        if(map.containsKey(TITLE))
            setTitle((String)map.get(TITLE));
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));

        if(map.containsKey(DATASETS))
        {
            List<Map<String,Object>> datasets = (List<Map<String,Object>>)map.get(DATASETS);
            for(Map<String,Object> config : datasets)
                addDataset(new ChartDataset<E>(getType(), (Map<String,Object>)config));
        }

        if(map.containsKey(SELECTIONS))
        {
            Map<String,Map<String,Object>> selections = (Map<String,Map<String,Object>>)map.get(SELECTIONS);
            for(Map.Entry<String,Map<String,Object>> entry : selections.entrySet())
                addSelection(ChartSelectionFactory.newInstance(entry.getKey(), (Map<String,Object>)entry.getValue()));
        }
    }

    /**
     * Returns the title of the chart.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the id of the chart.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the chart.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the title of the chart.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title for the chart.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the type of the chart.
     */
    public ChartJsChartType getType()
    {
        return type;
    }

    /**
     * Sets the type for the chart.
     */
    public void setType(ChartJsChartType type)
    {
        this.type = type;
    }

    /**
     * Sets the type for the chart.
     */
    public void setType(String type)
    {
        setType(ChartJsChartType.valueOf(type));
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
     * Adds a selection for the chart.
     */
    public Map<ChartParameter,ChartSelection<?>> getSelections()
    {
        return this.selections;
    }

    /**
     * Adds a selection for the chart.
     */
    public void addSelection(ChartSelection<?> selection)
    {
        this.selections.put(selection.getParameter(), selection);
    }

    /**
     * Returns the number of selections.
     */
    public int numSelections()
    {
        return selections.size();
    }

    /**
     * Returns the selection for the given parameter.
     */
    public ChartSelection<?> getSelection(ChartParameter parameter)
    {
        return selections.get(parameter);
    }

    /**
     * Returns the string selection for the given parameter.
     */
    public StringSelection getStringSelection(ChartParameter parameter)
    {
        return (StringSelection)getSelection(parameter);
    }

    /**
     * Returns the LocalDate selection for the given parameter.
     */
    public LocalDateTimeSelection getLocalDateTimeSelection(ChartParameter parameter)
    {
        return (LocalDateTimeSelection)getSelection(parameter);
    }

    /**
     * Returns <CODE>true</CODE> if there exists a selection for the given parameter.
     */
    public boolean hasSelection(ChartParameter parameter)
    {
        return selections.get(parameter) != null;
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