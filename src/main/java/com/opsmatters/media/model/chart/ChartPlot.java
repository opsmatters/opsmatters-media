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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;
import nl.crashdata.chartjs.data.ChartJsChartType;
import nl.crashdata.chartjs.data.colors.ChartJsRGBAColor;
import nl.crashdata.chartjs.data.simple.SimpleChartJsXYDataPoint;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsDatasetBuilder;

/**
 * Represents a single plot on a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartPlot<X extends Serializable, Y extends Serializable>
{
    public static final String LABEL = "label";
    public static final String TYPE = "type";
    public static final String BORDER_COLOR = "border-color";
    public static final String BORDER_WIDTH = "border-width";
    public static final String FILL = "fill";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String SOURCE = "source";

    private String label;
    private ChartJsChartType type;
    private ChartColor borderColor;
    private Integer borderWidth;
    private ChartFill fill;
    private ChartColor backgroundColor;
    private ChartSource source;

    /**
     * Default constructor.
     */
    public ChartPlot(ChartJsChartType type)
    {
        setType(type);
    }

    /**
     * Copy constructor.
     */
    public ChartPlot(ChartPlot obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartPlot<X,Y> obj)
    {
        if(obj != null)
        {
            setLabel(obj.getLabel());
            setType(obj.getType());
            setBorderColor(obj.getBorderColor());
            setBorderWidth(obj.getBorderWidth());
            setFill(new ChartFill(obj.getFill()));
            setBackgroundColor(obj.getBackgroundColor());
            setSource(new ChartSource(obj.getSource()));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public ChartPlot(ChartJsChartType type, Map<String, Object> map)
    {
        setType(type);
        if(map.containsKey(LABEL))
            setLabel((String)map.get(LABEL));
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));
        if(map.containsKey(BORDER_COLOR))
            setBorderColor((String)map.get(BORDER_COLOR));
        if(map.containsKey(BORDER_WIDTH))
            setBorderWidth((Integer)map.get(BORDER_WIDTH));
        if(map.containsKey(FILL))
            setFill(new ChartFill((Map<String,Object>)map.get(FILL)));
        if(map.containsKey(BACKGROUND_COLOR))
            setBackgroundColor((String)map.get(BACKGROUND_COLOR));
        if(map.containsKey(SOURCE))
            setSource(new ChartSource((Map<String,Object>)map.get(SOURCE)));
    }

    /**
     * Returns the label for the plot.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets the label for the plot.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Returns the type of the plot.
     */
    public ChartJsChartType getType()
    {
        return type;
    }

    /**
     * Sets the type for the plot.
     */
    public void setType(ChartJsChartType type)
    {
        this.type = type;
    }

    /**
     * Sets the type for the plot.
     */
    public void setType(String type)
    {
        setType(ChartJsChartType.valueOf(type));
    }

    /**
     * Returns the color for the plot border.
     */
    public ChartColor getBorderColor()
    {
        return borderColor;
    }

    /**
     * Sets the color for the plot border.
     */
    public void setBorderColor(ChartColor borderColor)
    {
        this.borderColor = borderColor;
    }

    /**
     * Sets the color for the plot border.
     */
    public void setBorderColor(String borderColor)
    {
        setBorderColor(ChartColor.valueOf(borderColor));
    }

    /**
     * Returns the width of the plot border.
     */
    public Integer getBorderWidth()
    {
        return borderWidth;
    }

    /**
     * Sets the width of the plot border.
     */
    public void setBorderWidth(Integer borderWidth)
    {
        this.borderWidth = borderWidth;
    }

    /**
     * Returns the fill for the plot.
     */
    public ChartFill getFill()
    {
        return fill;
    }

    /**
     * Sets the fill for the plot.
     */
    public void setFill(ChartFill fill)
    {
        this.fill = fill;
    }

    /**
     * Returns the color for the plot background.
     */
    public ChartColor getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Sets the color for the plot background.
     */
    public void setBackgroundColor(ChartColor backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Sets the color for the plot background.
     */
    public void setBackgroundColor(String backgroundColor)
    {
        setBackgroundColor(ChartColor.valueOf(backgroundColor));
    }

    /**
     * Returns the source for the plot.
     */
    public ChartSource getSource()
    {
        return source;
    }

    /**
     * Sets the source for the plot.
     */
    public void setSource(ChartSource source)
    {
        this.source = source;
    }

    /**
     * Configure the plot.
     */
    public void configure(SimpleChartJsConfigBuilder<SimpleChartJsXYDataPoint<X,Y>> config, Map<X,Y> points)
    {
        SimpleChartJsDatasetBuilder<SimpleChartJsXYDataPoint<X,Y>> dataset = config.data().addDataset();
        if(points != null)
            dataset = dataset.withDataPoints(points.entrySet(), SimpleChartJsXYDataPoint::new);
        if(getLabel() != null)
            dataset = dataset.withLabel(getLabel());
        // Mixed charts not supported yet
        //if(getType() != null)
        //    dataset = dataset.withType(getType());
        if(getFill() != null)
            dataset = dataset.withFill(getFill().configure());

        // Provide an array if the attribute is indexable
        if(type == ChartJsChartType.LINE)
        {
            if(getBorderWidth() != null)
                dataset = dataset.withBorderWidth(getBorderWidth());
            if(getBorderColor() != null)
                dataset = dataset.withBorderColor(getBorderColor().rgba());
            if(getBackgroundColor() != null)
                dataset = dataset.withBackgroundColor(getBackgroundColor().rgba());
        }
        else
        {
            if(getBorderWidth() != null)
                dataset = dataset.withBorderWidths(Collections.nCopies(points.size(), getBorderWidth()));
            if(getBorderColor() != null)
                dataset = dataset.withBorderColors(Collections.nCopies(points.size(), getBorderColor().rgba()));
            if(getBackgroundColor() != null)
                dataset = dataset.withBackgroundColors(Collections.nCopies(points.size(), getBackgroundColor().rgba()));
        }
    }
}