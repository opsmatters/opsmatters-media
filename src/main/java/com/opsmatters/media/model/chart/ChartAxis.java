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
import nl.crashdata.chartjs.data.ChartJsTimeUnit;
import nl.crashdata.chartjs.data.simple.builder.AbstractSimpleChartJsAxisConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsLocalDateAxisConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsLocalDateTimeAxisConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsLocalTimeAxisConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsLinearAxisConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsLogarithmicAxisConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsScaleLabelConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsTimeConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.AbstractSimpleChartJsTickConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsScalesConfigBuilder;

/**
 * Represents an axis of a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ChartAxis<T extends Serializable>
{
    public static final String DISPLAY = "display";
    public static final String TYPE = "type";
    public static final String LABEL = "label";
    public static final String TIME_UNIT = "time-unit";
    public static final String STEP_SIZE = "step-size";
    public static final String MIN = "min";
    public static final String MAX = "max";

    private boolean display;
    private AxisType type;
    private String label;
    private ChartJsTimeUnit timeUnit;
    private Number stepSize;
    private T min, max;

    /**
     * Default constructor.
     */
    public ChartAxis()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartAxis(ChartAxis obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartAxis<T> obj)
    {
        if(obj != null)
        {
            setDisplay(obj.getDisplay());
            setType(obj.getType());
            setLabel(obj.getLabel());
            setTimeUnit(obj.getTimeUnit());
            setStepSize(obj.getStepSize());
            setMin(obj.getMin());
            setMax(obj.getMax());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public ChartAxis(Map<String, Object> map)
    {
        if(map.containsKey(DISPLAY))
            setDisplay((Boolean)map.get(DISPLAY));
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));
        if(map.containsKey(LABEL))
            setLabel((String)map.get(LABEL));
        if(map.containsKey(TIME_UNIT))
            setTimeUnit((String)map.get(TIME_UNIT));
        if(map.containsKey(STEP_SIZE))
            setStepSize((Number)map.get(STEP_SIZE));
        if(map.containsKey(MIN))
            setMin((T)map.get(MIN));
        if(map.containsKey(MAX))
            setMax((T)map.get(MAX));
    }

    /**
     * Returns <CODE>true</CODE> if the axis should be displayed.
     */
    public boolean getDisplay()
    {
        return display;
    }

    /**
     * Set to <CODE>true</CODE> if the axis should be displayed.
     */
    public void setDisplay(boolean display)
    {
        this.display = display;
    }

    /**
     * Returns the type for the axis.
     */
    public AxisType getType()
    {
        return type;
    }

    /**
     * Sets the type for the axis.
     */
    public void setType(AxisType type)
    {
        this.type = type;
    }

    /**
     * Sets the type for the axis.
     */
    public void setType(String type)
    {
        setType(AxisType.valueOf(type));
    }

    /**
     * Returns the label for the axis.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets the label for the axis.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Returns the time unit for the axis.
     */
    public ChartJsTimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Sets the time unit for the axis.
     */
    public void setTimeUnit(ChartJsTimeUnit timeUnit)
    {
        this.timeUnit = timeUnit;
    }

    /**
     * Sets the time unit for the axis.
     */
    public void setTimeUnit(String timeUnit)
    {
        setTimeUnit(ChartJsTimeUnit.valueOf(timeUnit));
    }

    /**
     * Returns the step size for the axis.
     */
    public Number getStepSize()
    {
        return stepSize;
    }

    /**
     * Sets the step size for the axis.
     */
    public void setStepSize(Number stepSize)
    {
        this.stepSize = stepSize;
    }

    /**
     * Returns the min value for the axis.
     */
    public T getMin()
    {
        return min;
    }

    /**
     * Sets the min value for the axis.
     */
    public void setMin(T min)
    {
        this.min = min;
    }

    /**
     * Returns the max value for the axis.
     */
    public T getMax()
    {
        return max;
    }

    /**
     * Sets the max value for the axis.
     */
    public void setMax(T max)
    {
        this.max = max;
    }

    /**
     * Configure the axis.
     */
    public abstract void configure(SimpleChartJsScalesConfigBuilder scalesConfig);

    /**
     * Configure a date-based axis.
     */
    protected void configureLocalDateAxis(SimpleChartJsLocalDateAxisConfigBuilder axisConfig)
    {
        configureAxis(axisConfig);
        configureTime(axisConfig.timeConfig());
    }

    /**
     * Configure a datetime-based axis.
     */
    protected void configureLocalDateTimeAxis(SimpleChartJsLocalDateTimeAxisConfigBuilder axisConfig)
    {
        configureAxis(axisConfig);
        configureTime(axisConfig.timeConfig());
    }

    /**
     * Configure a time-based axis.
     */
    protected void configureLocalTimeAxis(SimpleChartJsLocalTimeAxisConfigBuilder axisConfig)
    {
        configureAxis(axisConfig);
        configureTime(axisConfig.timeConfig());
    }

    /**
     * Configure the units for a time-based axis.
     */
    private void configureTime(SimpleChartJsTimeConfigBuilder timeConfig)
    {
        if(getTimeUnit() != null)
            timeConfig = timeConfig.withTimeUnit(getTimeUnit());
        if(getStepSize() != null)
            timeConfig = timeConfig.withStepSize(getStepSize());
    }

    /**
     * Configure a linear axis.
     */
    protected void configureLinearAxis(SimpleChartJsLinearAxisConfigBuilder axisConfig)
    {
        configureAxis(axisConfig);
    }

    /**
     * Configure a logarithmic axis.
     */
    protected void configureLogarithmicAxis(SimpleChartJsLogarithmicAxisConfigBuilder axisConfig)
    {
        configureAxis(axisConfig);
    }

    /**
     * Configure a generic axis.
     */
    private void configureAxis(AbstractSimpleChartJsAxisConfigBuilder<?> axisConfig)
    {
        axisConfig.withDisplay(getDisplay());

        SimpleChartJsScaleLabelConfigBuilder labelConfig = axisConfig.labelConfig();
        if(getLabel() != null)
        {
            labelConfig = labelConfig.withDisplay(true);
            labelConfig = labelConfig.withLabelString(getLabel());
        }

        AbstractSimpleChartJsTickConfigBuilder tickConfig = axisConfig.tickConfig();
        if(getMin() != null)
            tickConfig = tickConfig.withForcedMinimum(getMin());
        if(getMax() != null)
            tickConfig = tickConfig.withForcedMaximum(getMax());
    }
}