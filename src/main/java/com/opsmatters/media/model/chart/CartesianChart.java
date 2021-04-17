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
import nl.crashdata.chartjs.data.ChartJsChartType;
import nl.crashdata.chartjs.data.simple.SimpleChartJsXYDataPoint;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsOptionsBuilder;

/**
 * Represents a chart containing x and y axes with datasets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CartesianChart<X extends Serializable,Y extends Serializable> extends ChartJsChart<SimpleChartJsXYDataPoint<X,Y>>
{
    public static final String X_AXIS = "x-axis";
    public static final String Y_AXIS = "y-axis";
    public static final String STACKED = "stacked";

    private ChartXAxis<X> xAxis;
    private ChartYAxis<Y> yAxis;
    private boolean stacked;

    /**
     * Constructor that takes an id.
     */
    public CartesianChart(String id)
    {
        super(id);
    }

    /**
     * Copy constructor.
     */
    public CartesianChart(CartesianChart<X,Y> obj)
    {
        super(obj.getId());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(CartesianChart<X,Y> obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setXAxis(new ChartXAxis<X>(obj.getXAxis()));
            setYAxis(new ChartYAxis<Y>(obj.getYAxis()));
            setStacked(obj.getStacked());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    @Override
    protected void parse(Map<String, Object> map)
    {
        super.parse(map);

        if(map.containsKey(X_AXIS))
            setXAxis(new ChartXAxis<X>((Map<String,Object>)map.get(X_AXIS)));
        if(map.containsKey(Y_AXIS))
            setYAxis(new ChartYAxis<Y>((Map<String,Object>)map.get(Y_AXIS)));
        if(map.containsKey(STACKED))
            setStacked((Boolean)map.get(STACKED));
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
     * Returns <CODE>true</CODE> if the axis should be stacked.
     */
    public boolean getStacked()
    {
        return stacked;
    }

    /**
     * Set to <CODE>true</CODE> if the axis should be stacked.
     */
    public void setStacked(boolean stacked)
    {
        this.stacked = stacked;
    }

    /**
     * Returns the config for the current chart type.
     */
    @Override
    public SimpleChartJsConfigBuilder<SimpleChartJsXYDataPoint<X,Y>> configure()
    {
        SimpleChartJsConfigBuilder<SimpleChartJsXYDataPoint<X,Y>> ret = null;

        if(getChartJsChartType() == ChartJsChartType.LINE)
            ret = SimpleChartJsConfigBuilder.lineChart();
        else if(getChartJsChartType() == ChartJsChartType.BAR)
            ret = SimpleChartJsConfigBuilder.barChart();
        else if(getChartJsChartType() == ChartJsChartType.SCATTER)
            ret = SimpleChartJsConfigBuilder.scatterPlot();
        else if(getChartJsChartType() == ChartJsChartType.BUBBLE)
            ret = SimpleChartJsConfigBuilder.bubbleChart();

        if(ret != null)
        {
            configure(ret.options());
            if(getXAxis() != null)
                getXAxis().configure(ret.options().scalesConfig());
            if(getYAxis() != null)
                getYAxis().configure(ret.options().scalesConfig());
        }

        return ret;
    }

    /**
     * Configure the config options.
     */
    @Override
    protected void configure(SimpleChartJsOptionsBuilder options)
    {
        super.configure(options);
    }
}