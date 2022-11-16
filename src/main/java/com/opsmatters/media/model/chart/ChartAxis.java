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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents an axis of a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ChartAxis<E extends Serializable> implements ConfigElement
{
    private boolean display;
    private AxisType type;
    private String label;
    private ChartJsTimeUnit timeUnit;
    private Number stepSize;
    private E min, max;
    private boolean stacked;

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
    public void copyAttributes(ChartAxis<E> obj)
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
            setStacked(obj.getStacked());
        }
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
    public E getMin()
    {
        return min;
    }

    /**
     * Sets the min value for the axis.
     */
    public void setMin(E min)
    {
        this.min = min;
    }

    /**
     * Returns the max value for the axis.
     */
    public E getMax()
    {
        return max;
    }

    /**
     * Sets the max value for the axis.
     */
    public void setMax(E max)
    {
        this.max = max;
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

        axisConfig.withStacked(getStacked());
    }

    /**
     * Builder to make configuration construction easier.
     */
    public abstract static class Builder<E extends Serializable, T extends ChartAxis<E>, B extends Builder<E,T,B>>
        implements ConfigParser<ChartAxis<E>>
    {
        // The config attribute names
        private static final String DISPLAY = "display";
        private static final String TYPE = "type";
        private static final String LABEL = "label";
        private static final String TIME_UNIT = "time-unit";
        private static final String STEP_SIZE = "step-size";
        private static final String MIN = "min";
        private static final String MAX = "max";
        private static final String STACKED = "stacked";

        private ChartAxis<E> ret = null;

        /**
         * Sets the axis.
         * @param axis The axis
         */
        public void set(ChartAxis<E> axis)
        {
            ret = axis;
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(DISPLAY))
                ret.setDisplay((Boolean)map.get(DISPLAY));
            if(map.containsKey(TYPE))
                ret.setType((String)map.get(TYPE));
            if(map.containsKey(LABEL))
                ret.setLabel((String)map.get(LABEL));
            if(map.containsKey(TIME_UNIT))
                ret.setTimeUnit((String)map.get(TIME_UNIT));
            if(map.containsKey(STEP_SIZE))
                ret.setStepSize((Number)map.get(STEP_SIZE));
            if(map.containsKey(MIN))
                ret.setMin((E)map.get(MIN));
            if(map.containsKey(MAX))
                ret.setMax((E)map.get(MAX));
            if(map.containsKey(STACKED))
                ret.setStacked((Boolean)map.get(STACKED));

            return self();
        }

        /**
         * Returns this object.
         * @return This object
         */
        protected abstract B self();

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public abstract T build();
    }
}