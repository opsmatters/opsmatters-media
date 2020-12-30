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
import nl.crashdata.chartjs.data.simple.SimpleChartJsXYDataPoint;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsOptionsBuilder;

/**
 * Represents a chart containing plots.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Chart<X extends Serializable, Y extends Serializable>
{
    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String X_AXIS = "x-axis";
    public static final String Y_AXIS = "y-axis";
    public static final String PLOTS = "plots";
    public static final String SELECTIONS = "selections";

    private String code = "";
    private String title = "";
    private ChartJsChartType type;
    private ChartXAxis<X> xAxis;
    private ChartYAxis<Y> yAxis;
    private List<ChartPlot<X,Y>> plots = new ArrayList<ChartPlot<X,Y>>();
    private Map<Parameter,ChartSelection<?>> selections = new LinkedHashMap<Parameter,ChartSelection<?>>();

    /**
     * Default constructor.
     */
    public Chart(String code)
    {
        setCode(code);
    }

    /**
     * Copy constructor.
     */
    public Chart(Chart obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Chart<X,Y> obj)
    {
        if(obj != null)
        {
            setCode(obj.getCode());
            setTitle(obj.getTitle());
            setType(obj.getType());
            setXAxis(new ChartXAxis<X>(obj.getXAxis()));
            setYAxis(new ChartYAxis<Y>(obj.getYAxis()));
            for(ChartPlot<X,Y> plot : obj.getPlots())
                addPlot(new ChartPlot<X,Y>(plot));
            for(ChartSelection<?> selection : obj.getSelections().values())
                addSelection(ChartSelectionFactory.newInstance(selection));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public Chart(String code, Map<String, Object> map)
    {
        this(code);
        if(map.containsKey(TITLE))
            setTitle((String)map.get(TITLE));
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));
        if(map.containsKey(X_AXIS))
            setXAxis(new ChartXAxis<X>((Map<String,Object>)map.get(X_AXIS)));
        if(map.containsKey(Y_AXIS))
            setYAxis(new ChartYAxis<Y>((Map<String,Object>)map.get(Y_AXIS)));

        if(map.containsKey(PLOTS))
        {
            List<Map<String,Object>> plots = (List<Map<String,Object>>)map.get(PLOTS);
            for(Map<String,Object> config : plots)
                addPlot(new ChartPlot<X,Y>(getType(), (Map<String,Object>)config));
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
     * Returns the code of the chart.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code for the chart.
     */
    public void setCode(String code)
    {
        this.code = code;
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
     * Returns the x-axis for the chart.
     */
    public ChartXAxis<X> getXAxis()
    {
        return xAxis;
    }

    /**
     * Sets the x-axis for the chart.
     */
    public void setXAxis(ChartXAxis<X> xAxis)
    {
        this.xAxis = xAxis;
    }

    /**
     * Returns the y-axis for the chart.
     */
    public ChartYAxis<Y> getYAxis()
    {
        return yAxis;
    }

    /**
     * Sets the y-axis for the chart.
     */
    public void setYAxis(ChartYAxis<Y> yAxis)
    {
        this.yAxis = yAxis;
    }

    /**
     * Adds a plot to the plots for the chart.
     */
    public List<ChartPlot<X,Y>> getPlots()
    {
        return this.plots;
    }

    /**
     * Adds a plot to the plots for the chart.
     */
    public void addPlot(ChartPlot<X,Y> plot)
    {
        this.plots.add(plot);
    }

    /**
     * Returns the number of plots.
     */
    public int numPlots()
    {
        return plots.size();
    }

    /**
     * Returns the plot at the given index.
     */
    public ChartPlot<X,Y> getPlot(int i)
    {
        return plots.get(i);
    }

    /**
     * Adds a selection for the chart.
     */
    public Map<Parameter,ChartSelection<?>> getSelections()
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
    public ChartSelection<?> getSelection(Parameter parameter)
    {
        return selections.get(parameter);
    }

    /**
     * Returns the string selection for the given parameter.
     */
    public StringSelection getStringSelection(Parameter parameter)
    {
        return (StringSelection)getSelection(parameter);
    }

    /**
     * Returns the LocalDate selection for the given parameter.
     */
    public LocalDateTimeSelection getLocalDateTimeSelection(Parameter parameter)
    {
        return (LocalDateTimeSelection)getSelection(parameter);
    }

    /**
     * Returns <CODE>true</CODE> if there exists a selection for the given parameter.
     */
    public boolean hasSelection(Parameter parameter)
    {
        return selections.get(parameter) != null;
    }

    /**
     * Returns the config for the current chart type.
     */
    public SimpleChartJsConfigBuilder<SimpleChartJsXYDataPoint<X,Y>> configure()
    {
        SimpleChartJsConfigBuilder<SimpleChartJsXYDataPoint<X,Y>> ret = null;

        if(type == ChartJsChartType.LINE)
            ret = SimpleChartJsConfigBuilder.lineChart();
        else if(type == ChartJsChartType.BAR)
            ret = SimpleChartJsConfigBuilder.barChart();
        else if(type == ChartJsChartType.PIE)
            ret = SimpleChartJsConfigBuilder.pieChart();
        else if(type == ChartJsChartType.RADAR)
            ret = SimpleChartJsConfigBuilder.radarChart();
        else if(type == ChartJsChartType.SCATTER)
            ret = SimpleChartJsConfigBuilder.scatterPlot();
        else if(type == ChartJsChartType.BUBBLE)
            ret = SimpleChartJsConfigBuilder.bubbleChart();
        else if(type == ChartJsChartType.POLAR_AREA)
            ret = SimpleChartJsConfigBuilder.polarAreaChart();
        else if(type == ChartJsChartType.DOUGHNUT)
            ret = SimpleChartJsConfigBuilder.doughnut();

        if(ret != null)
        {
            SimpleChartJsOptionsBuilder options = ret.options();
            options.withResponsive(true);
            options.hoverConfig().withIntersect(true).withMode(ChartJsInteractionMode.NEAREST);
            options.tooltipConfig().withIntersect(false).withMode(ChartJsInteractionMode.INDEX);
            getXAxis().configure(options.scalesConfig());
            getYAxis().configure(options.scalesConfig());
        }

        return ret;
    }
}