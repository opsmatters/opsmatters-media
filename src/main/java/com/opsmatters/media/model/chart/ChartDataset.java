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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a single dataset on a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartDataset<E extends Serializable> implements ConfigElement
{
    private String label;
    private ChartJsChartType type;
    private ChartColor backgroundColor;
    private List<ChartColor> backgroundColors;
    private ChartColor borderColor;
    private List<ChartColor> borderColors;
    private Integer borderWidth;
    private List<Integer> borderWidths;
    private ChartColor pointBackgroundColor;
    private List<ChartColor> pointBackgroundColors;
    private ChartColor pointBorderColor;
    private List<ChartColor> pointBorderColors;
    private Integer pointBorderWidth;
    private List<Integer> pointBorderWidths;
    private Integer pointRadius;
    private List<Integer> pointRadiuses;
    private ChartFill fill;
    private ChartSource source;
    private String stack;
    private Integer order;

    /**
     * Constructor that takes a type.
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
            setPointBackgroundColor(obj.getPointBackgroundColor());
            setPointBackgroundColors(obj.getPointBackgroundColors());
            setPointBorderColor(obj.getPointBorderColor());
            setPointBorderColors(obj.getPointBorderColors());
            setPointBorderWidth(obj.getPointBorderWidth());
            setPointBorderWidths(obj.getPointBorderWidths());
            setPointRadius(obj.getPointRadius());
            setPointRadiuses(obj.getPointRadiuses());
            setFill(new ChartFill(obj.getFill()));
            setSource(new ChartSource(obj.getSource()));
            setStack(obj.getStack());
            setOrder(obj.getOrder());
        }
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
     * Returns the color for the dataset point background.
     */
    public ChartColor getPointBackgroundColor()
    {
        return pointBackgroundColor;
    }

    /**
     * Sets the color for the dataset point background.
     */
    public void setPointBackgroundColor(ChartColor pointBackgroundColor)
    {
        this.pointBackgroundColor = pointBackgroundColor;
    }

    /**
     * Sets the color for the dataset point background.
     */
    public void setPointBackgroundColor(String pointBackgroundColor)
    {
        setPointBackgroundColor(ChartColor.valueOf(pointBackgroundColor));
    }

    /**
     * Returns the colors for the dataset point backgrounds.
     */
    public List<ChartColor> getPointBackgroundColors()
    {
        return pointBackgroundColors;
    }

    /**
     * Sets the colors for the dataset point backgrounds.
     */
    public void setPointBackgroundColors(List<ChartColor> pointBackgroundColors)
    {
        this.pointBackgroundColors = pointBackgroundColors;
    }

    /**
     * Sets the colors for the dataset point backgrounds.
     */
    public void setStringPointBackgroundColors(List<String> pointBackgroundColors)
    {
        setPointBackgroundColors(new ArrayList<ChartColor>());
        for(String pointBackgroundColor : pointBackgroundColors)
            getPointBackgroundColors().add(ChartColor.valueOf(pointBackgroundColor));
    }

    /**
     * Returns the color for the dataset point border.
     */
    public ChartColor getPointBorderColor()
    {
        return pointBorderColor;
    }

    /**
     * Sets the color for the dataset point border.
     */
    public void setPointBorderColor(ChartColor pointBorderColor)
    {
        this.pointBorderColor = pointBorderColor;
    }

    /**
     * Sets the color for the dataset point border.
     */
    public void setPointBorderColor(String pointBorderColor)
    {
        setPointBorderColor(ChartColor.valueOf(pointBorderColor));
    }

    /**
     * Returns the colors for the dataset point borders.
     */
    public List<ChartColor> getPointBorderColors()
    {
        return pointBorderColors;
    }

    /**
     * Sets the colors for the dataset point borders.
     */
    public void setPointBorderColors(List<ChartColor> pointBorderColors)
    {
        this.pointBorderColors = pointBorderColors;
    }

    /**
     * Sets the colors for the dataset point borders.
     */
    public void setStringPointBorderColors(List<String> pointBorderColors)
    {
        setPointBorderColors(new ArrayList<ChartColor>());
        for(String pointBorderColor : pointBorderColors)
            getPointBorderColors().add(ChartColor.valueOf(pointBorderColor));
    }

    /**
     * Returns the width of the dataset point border.
     */
    public Integer getPointBorderWidth()
    {
        return pointBorderWidth;
    }

    /**
     * Sets the width of the dataset point border.
     */
    public void setPointBorderWidth(Integer pointBorderWidth)
    {
        this.pointBorderWidth = pointBorderWidth;
    }

    /**
     * Returns the widths of the dataset point borders.
     */
    public List<Integer> getPointBorderWidths()
    {
        return pointBorderWidths;
    }

    /**
     * Sets the widths of the dataset point borders.
     */
    public void setPointBorderWidths(List<Integer> pointBorderWidths)
    {
        this.pointBorderWidths = pointBorderWidths;
    }

    /**
     * Returns the width of the dataset point radius.
     */
    public Integer getPointRadius()
    {
        return pointRadius;
    }

    /**
     * Sets the width of the dataset point radius.
     */
    public void setPointRadius(Integer pointRadius)
    {
        this.pointRadius = pointRadius;
    }

    /**
     * Returns the widths of the dataset point radiuses.
     */
    public List<Integer> getPointRadiuses()
    {
        return pointRadiuses;
    }

    /**
     * Sets the widths of the dataset point radiuses.
     */
    public void setPointRadiuses(List<Integer> pointRadiuses)
    {
        this.pointRadiuses = pointRadiuses;
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
     * Returns the stack for the dataset.
     */
    public String getStack()
    {
        return stack;
    }

    /**
     * Sets the stack for the dataset.
     */
    public void setStack(String stack)
    {
        this.stack = stack;
    }

    /**
     * Returns the order for the dataset.
     */
    public Integer getOrder()
    {
        return order;
    }

    /**
     * Sets the order for the dataset.
     */
    public void setOrder(Integer order)
    {
        this.order = order;
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

        // Fill in the array if only a single value was provided
        if(getBackgroundColor() != null && getBackgroundColors() == null)
            dataset = dataset.withBackgroundColors(Collections.nCopies(points.size(), getBackgroundColor().rgba()));

        if(getBorderColor() != null)
            dataset = dataset.withBorderColor(getBorderColor().rgba());

        if(getBorderColors() != null)
        {
            List<ChartJsRGBAColor> colors = new ArrayList<ChartJsRGBAColor>();
            for(ChartColor color : getBackgroundColors())
                colors.add(color.rgba());
            dataset = dataset.withBorderColors(colors);
        }

        // Fill in the array if only a single value was provided
        if(getBorderColor() != null && getBorderColors() == null)
            dataset = dataset.withBorderColors(Collections.nCopies(points.size(), getBorderColor().rgba()));

        if(getBorderWidth() != null)
            dataset = dataset.withBorderWidth(getBorderWidth());

        if(getBorderWidths() != null)
            dataset = dataset.withBorderWidths(getBorderWidths());

        // Fill in the array if only a single value was provided
        if(getBorderWidth() != null && getBorderWidths() == null)
            dataset = dataset.withBorderWidths(Collections.nCopies(points.size(), getBorderWidth()));

        if(getPointBackgroundColor() != null)
            dataset = dataset.withPointBackgroundColor(getPointBackgroundColor().rgba());

        if(getPointBackgroundColors() != null)
        {
            List<ChartJsRGBAColor> colors = new ArrayList<ChartJsRGBAColor>();
            for(ChartColor color : getPointBackgroundColors())
                colors.add(color.rgba());
            dataset = dataset.withPointBackgroundColors(colors);
        }

        // Fill in the array if only a single value was provided
        if(getPointBackgroundColor() != null && getPointBackgroundColors() == null)
            dataset = dataset.withPointBackgroundColors(Collections.nCopies(points.size(), getPointBackgroundColor().rgba()));

        if(getPointBorderColor() != null)
            dataset = dataset.withPointBorderColor(getPointBorderColor().rgba());

        if(getPointBorderColors() != null)
        {
            List<ChartJsRGBAColor> colors = new ArrayList<ChartJsRGBAColor>();
            for(ChartColor color : getPointBackgroundColors())
                colors.add(color.rgba());
            dataset = dataset.withPointBorderColors(colors);
        }

        // Fill in the array if only a single value was provided
        if(getPointBorderColor() != null && getPointBorderColors() == null)
            dataset = dataset.withPointBorderColors(Collections.nCopies(points.size(), getPointBorderColor().rgba()));

        if(getPointBorderWidth() != null)
            dataset = dataset.withPointBorderWidth(getPointBorderWidth());

        if(getPointBorderWidths() != null)
            dataset = dataset.withPointBorderWidths(getPointBorderWidths());

        // Fill in the array if only a single value was provided
        if(getPointBorderWidth() != null && getPointBorderWidths() == null)
            dataset = dataset.withPointBorderWidths(Collections.nCopies(points.size(), getPointBorderWidth()));

        if(getPointRadius() != null)
            dataset = dataset.withPointRadius(getPointRadius());

        if(getPointRadiuses() != null)
            dataset = dataset.withPointRadiuses(getPointRadiuses());

        // Fill in the array if only a single value was provided
        if(getPointRadius() != null && getPointRadiuses() == null)
            dataset = dataset.withPointRadiuses(Collections.nCopies(points.size(), getPointRadius()));

        if(getFill() != null)
            dataset = dataset.withFill(getFill().configure());

        if(getStack() != null)
            dataset = dataset.withStack(getStack());

        if(getOrder() != null)
            dataset = dataset.withOrder(getOrder());
    }

    /**
     * Returns a builder for the configuration.
     * @param type The type of the configuration
     * @return The builder instance.
     */
    public static Builder builder(ChartJsChartType type)
    {
        return new Builder(type);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder<E extends Serializable>
        implements ConfigParser<ChartDataset<E>>
    {
        // The config attribute names
        private static final String LABEL = "label";
        private static final String TYPE = "type";
        private static final String BACKGROUND_COLOR = "background-color";
        private static final String BACKGROUND_COLORS = "background-colors";
        private static final String BORDER_COLOR = "border-color";
        private static final String BORDER_COLORS = "border-colors";
        private static final String BORDER_WIDTH = "border-width";
        private static final String BORDER_WIDTHS = "border-widths";
        private static final String POINT_BACKGROUND_COLOR = "point-background-color";
        private static final String POINT_BACKGROUND_COLORS = "point-background-colors";
        private static final String POINT_BORDER_COLOR = "point-border-color";
        private static final String POINT_BORDER_COLORS = "point-border-colors";
        private static final String POINT_BORDER_WIDTH = "point-border-width";
        private static final String POINT_BORDER_WIDTHS = "point-border-widths";
        private static final String POINT_RADIUS = "point-radius";
        private static final String POINT_RADIUSES = "point-radiuses";
        private static final String FILL = "fill";
        private static final String SOURCE = "source";
        private static final String STACK = "stack";
        private static final String ORDER = "order";

        private ChartDataset<E> ret = null;

        /**
         * Constructor that takes a type.
         * @param type The type for the configuration
         */
        public Builder(ChartJsChartType type)
        {
            ret = new ChartDataset<E>(type);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(LABEL))
                ret.setLabel((String)map.get(LABEL));
            if(map.containsKey(TYPE))
                ret.setType((String)map.get(TYPE));
            if(map.containsKey(BACKGROUND_COLOR))
                ret.setBackgroundColor((String)map.get(BACKGROUND_COLOR));
            if(map.containsKey(BACKGROUND_COLORS))
                ret.setStringBackgroundColors((List<String>)map.get(BACKGROUND_COLORS));
            if(map.containsKey(BORDER_COLOR))
                ret.setBorderColor((String)map.get(BORDER_COLOR));
            if(map.containsKey(BORDER_COLORS))
                ret.setStringBorderColors((List<String>)map.get(BORDER_COLORS));
            if(map.containsKey(BORDER_WIDTH))
                ret.setBorderWidth((Integer)map.get(BORDER_WIDTH));
            if(map.containsKey(BORDER_WIDTHS))
                ret.setBorderWidths((List<Integer>)map.get(BORDER_WIDTHS));
            if(map.containsKey(POINT_BACKGROUND_COLOR))
                ret.setPointBackgroundColor((String)map.get(POINT_BACKGROUND_COLOR));
            if(map.containsKey(POINT_BACKGROUND_COLORS))
                ret.setStringPointBackgroundColors((List<String>)map.get(POINT_BACKGROUND_COLORS));
            if(map.containsKey(POINT_BORDER_COLOR))
                ret.setPointBorderColor((String)map.get(POINT_BORDER_COLOR));
            if(map.containsKey(POINT_BORDER_COLORS))
                ret.setStringPointBorderColors((List<String>)map.get(POINT_BORDER_COLORS));
            if(map.containsKey(POINT_BORDER_WIDTH))
                ret.setPointBorderWidth((Integer)map.get(POINT_BORDER_WIDTH));
            if(map.containsKey(POINT_BORDER_WIDTHS))
                ret.setPointBorderWidths((List<Integer>)map.get(POINT_BORDER_WIDTHS));
            if(map.containsKey(POINT_RADIUS))
                ret.setPointRadius((Integer)map.get(POINT_RADIUS));
            if(map.containsKey(POINT_RADIUSES))
                ret.setPointRadiuses((List<Integer>)map.get(POINT_RADIUSES));
            if(map.containsKey(FILL))
                ret.setFill(ChartFill.builder()
                    .parse((Map<String,Object>)map.get(FILL)).build());
            if(map.containsKey(SOURCE))
                ret.setSource(ChartSource.builder()
                    .parse((Map<String,Object>)map.get(SOURCE)).build());
            if(map.containsKey(STACK))
                ret.setStack((String)map.get(STACK));
            if(map.containsKey(ORDER))
                ret.setOrder((Integer)map.get(ORDER));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ChartDataset<E> build()
        {
            return ret;
        }
    }
}