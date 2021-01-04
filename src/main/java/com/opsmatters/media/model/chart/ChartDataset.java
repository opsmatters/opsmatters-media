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
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsConfigBuilder;
import nl.crashdata.chartjs.data.simple.builder.SimpleChartJsDatasetBuilder;

/**
 * Represents a single dataset on a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartDataset<E extends Serializable>
{
    public static final String LABEL = "label";
    public static final String TYPE = "type";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String BACKGROUND_COLORS = "background-colors";
    public static final String BORDER_COLOR = "border-color";
    public static final String BORDER_COLORS = "border-colors";
    public static final String BORDER_WIDTH = "border-width";
    public static final String BORDER_WIDTHS = "border-widths";
    public static final String FILL = "fill";
    public static final String SOURCE = "source";

    private String label;
    private ChartJsChartType type;
    private ChartColor backgroundColor;
    private List<ChartColor> backgroundColors;
    private ChartColor borderColor;
    private List<ChartColor> borderColors;
    private Integer borderWidth;
    private List<Integer> borderWidths;
    private ChartFill fill;
    private ChartSource source;

    /**
     * Default constructor.
     */
    public ChartDataset(ChartJsChartType type)
    {
        setType(type);
    }

    /**
     * Copy constructor.
     */
    public ChartDataset(ChartDataset obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartDataset<E> obj)
    {
        if(obj != null)
        {
            setLabel(obj.getLabel());
            setType(obj.getType());
            setBackgroundColor(obj.getBackgroundColor());
            setBackgroundColors(obj.getBackgroundColors());
            setBorderColor(obj.getBorderColor());
            setBorderColors(obj.getBorderColors());
            setBorderWidth(obj.getBorderWidth());
            setBorderWidths(obj.getBorderWidths());
            setFill(new ChartFill(obj.getFill()));
            setSource(new ChartSource(obj.getSource()));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public ChartDataset(ChartJsChartType type, Map<String, Object> map)
    {
        setType(type);
        if(map.containsKey(LABEL))
            setLabel((String)map.get(LABEL));
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));
        if(map.containsKey(BACKGROUND_COLOR))
            setBackgroundColor((String)map.get(BACKGROUND_COLOR));
        if(map.containsKey(BACKGROUND_COLORS))
            setStringBackgroundColors((List<String>)map.get(BACKGROUND_COLORS));
        if(map.containsKey(BORDER_COLOR))
            setBorderColor((String)map.get(BORDER_COLOR));
        if(map.containsKey(BORDER_COLORS))
            setStringBorderColors((List<String>)map.get(BORDER_COLORS));
        if(map.containsKey(BORDER_WIDTH))
            setBorderWidth((Integer)map.get(BORDER_WIDTH));
        if(map.containsKey(BORDER_WIDTHS))
            setBorderWidths((List<Integer>)map.get(BORDER_WIDTHS));
        if(map.containsKey(FILL))
            setFill(new ChartFill((Map<String,Object>)map.get(FILL)));
        if(map.containsKey(SOURCE))
            setSource(new ChartSource((Map<String,Object>)map.get(SOURCE)));
    }

    /**
     * Returns the label for the dataset.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets the label for the dataset.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Returns the type of the dataset.
     */
    public ChartJsChartType getType()
    {
        return type;
    }

    /**
     * Sets the type for the dataset.
     */
    public void setType(ChartJsChartType type)
    {
        this.type = type;
    }

    /**
     * Sets the type for the dataset.
     */
    public void setType(String type)
    {
        setType(ChartJsChartType.valueOf(type));
    }

    /**
     * Returns the color for the dataset background.
     */
    public ChartColor getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Sets the color for the dataset background.
     */
    public void setBackgroundColor(ChartColor backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Sets the color for the dataset background.
     */
    public void setBackgroundColor(String backgroundColor)
    {
        setBackgroundColor(ChartColor.valueOf(backgroundColor));
    }

    /**
     * Returns the colors for the dataset backgrounds.
     */
    public List<ChartColor> getBackgroundColors()
    {
        return backgroundColors;
    }

    /**
     * Sets the colors for the dataset backgrounds.
     */
    public void setBackgroundColors(List<ChartColor> backgroundColors)
    {
        this.backgroundColors = backgroundColors;
    }

    /**
     * Sets the colors for the dataset backgrounds.
     */
    public void setStringBackgroundColors(List<String> backgroundColors)
    {
        setBackgroundColors(new ArrayList<ChartColor>());
        for(String backgroundColor : backgroundColors)
            getBackgroundColors().add(ChartColor.valueOf(backgroundColor));
    }

    /**
     * Returns the color for the dataset border.
     */
    public ChartColor getBorderColor()
    {
        return borderColor;
    }

    /**
     * Sets the color for the dataset border.
     */
    public void setBorderColor(ChartColor borderColor)
    {
        this.borderColor = borderColor;
    }

    /**
     * Sets the color for the dataset border.
     */
    public void setBorderColor(String borderColor)
    {
        setBorderColor(ChartColor.valueOf(borderColor));
    }

    /**
     * Returns the colors for the dataset borders.
     */
    public List<ChartColor> getBorderColors()
    {
        return borderColors;
    }

    /**
     * Sets the colors for the dataset borders.
     */
    public void setBorderColors(List<ChartColor> borderColors)
    {
        this.borderColors = borderColors;
    }

    /**
     * Sets the colors for the dataset borders.
     */
    public void setStringBorderColors(List<String> borderColors)
    {
        setBorderColors(new ArrayList<ChartColor>());
        for(String borderColor : borderColors)
            getBorderColors().add(ChartColor.valueOf(borderColor));
    }

    /**
     * Returns the width of the dataset border.
     */
    public Integer getBorderWidth()
    {
        return borderWidth;
    }

    /**
     * Sets the width of the dataset border.
     */
    public void setBorderWidth(Integer borderWidth)
    {
        this.borderWidth = borderWidth;
    }

    /**
     * Returns the widths of the dataset borders.
     */
    public List<Integer> getBorderWidths()
    {
        return borderWidths;
    }

    /**
     * Sets the widths of the dataset borders.
     */
    public void setBorderWidths(List<Integer> borderWidths)
    {
        this.borderWidths = borderWidths;
    }

    /**
     * Returns the fill for the dataset.
     */
    public ChartFill getFill()
    {
        return fill;
    }

    /**
     * Sets the fill for the dataset.
     */
    public void setFill(ChartFill fill)
    {
        this.fill = fill;
    }

    /**
     * Returns the source for the dataset.
     */
    public ChartSource getSource()
    {
        return source;
    }

    /**
     * Sets the source for the dataset.
     */
    public void setSource(ChartSource source)
    {
        this.source = source;
    }

    /**
     * Configure the dataset.
     */
    public void configure(SimpleChartJsConfigBuilder<E> config, List<E> points)
    {
        SimpleChartJsDatasetBuilder<E> dataset = config.data().addDataset();
        if(points != null)
            dataset = dataset.withDataPoints(points);
        if(getLabel() != null)
            dataset = dataset.withLabel(getLabel());
        // Mixed charts not supported yet
        //if(getType() != null)
        //    dataset = dataset.withType(getType());

        if(getBackgroundColor() != null)
            dataset = dataset.withBackgroundColor(getBackgroundColor().rgba());

        if(getBackgroundColors() != null)
        {
            List<ChartJsRGBAColor> colors = new ArrayList<ChartJsRGBAColor>();
            for(ChartColor color : getBackgroundColors())
                colors.add(color.rgba());
            dataset = dataset.withBackgroundColors(colors);
        }

        if(getBorderColor() != null)
            dataset = dataset.withBorderColor(getBorderColor().rgba());

        if(getBorderColors() != null)
        {
            List<ChartJsRGBAColor> colors = new ArrayList<ChartJsRGBAColor>();
            for(ChartColor color : getBackgroundColors())
                colors.add(color.rgba());
            dataset = dataset.withBorderColors(colors);
        }

        if(getBorderWidth() != null)
            dataset = dataset.withBorderWidth(getBorderWidth());

        if(getBorderWidths() != null)
            dataset = dataset.withBorderWidths(getBorderWidths());

        // Fill in the arrays if the attribute is indexable and only a single value is provided
        if(getBackgroundColor() != null && getBackgroundColors() == null)
            dataset = dataset.withBackgroundColors(Collections.nCopies(points.size(), getBackgroundColor().rgba()));
        if(getBorderColor() != null && getBorderColors() == null)
            dataset = dataset.withBorderColors(Collections.nCopies(points.size(), getBorderColor().rgba()));
        if(getBorderWidth() != null && getBorderWidths() == null)
            dataset = dataset.withBorderWidths(Collections.nCopies(points.size(), getBorderWidth()));

        if(getFill() != null)
            dataset = dataset.withFill(getFill().configure());
    }
}