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
import java.util.LinkedHashMap;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.util.StringUtils;

/**
 * Represents a widget containing a chart with pre-defined selections.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Widget implements ConfigElement
{
    private String id = "";
    private boolean showTitle = false;
    private String chartId = "";
    private String cssClass = "";
    private String cssStyle = "";
    private Map<ChartParameter,ChartSelection<?>> selections = new LinkedHashMap<ChartParameter,ChartSelection<?>>();

    /**
     * Default constructor.
     */
    public Widget(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public Widget(Widget obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Widget obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setChartId(obj.getChartId());
            setShowTitle(obj.showTitle());
            setCssClass(obj.getCssClass());
            setCssStyle(obj.getCssStyle());
            for(ChartSelection<?> selection : obj.getSelections().values())
                addSelection(ChartSelectionFactory.newInstance(selection));
        }
    }

    /**
     * Returns the id of the widget.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the widget.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the widget.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the markup id of the widget.
     */
    public String getMarkupId()
    {
        return getId().toLowerCase();
    }

    /**
     * Returns the id of the chart.
     */
    public String getChartId()
    {
        return chartId;
    }

    /**
     * Sets the id of the chart.
     */
    public void setChartId(String chartId)
    {
        this.chartId = chartId;
    }

    /**
     * Returns <CODE>true</CODE> if the title should be shown for the widget.
     */
    public boolean showTitle()
    {
        return showTitle;
    }

    /**
     * Set to <CODE>true</CODE> if the title should be shown for the widget.
     */
    public void setShowTitle(boolean showTitle)
    {
        this.showTitle = showTitle;
    }

    /**
     * Returns the css classes for the widget.
     */
    public String getCssClass()
    {
        return cssClass;
    }

    /**
     * Sets the css classes for the widget.
     */
    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    /**
     * Returns the css styles for the widget.
     */
    public String getCssStyle()
    {
        return cssStyle;
    }

    /**
     * Sets the css styles for the widget.
     */
    public void setCssStyle(String cssStyle)
    {
        this.cssStyle = cssStyle;
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
     * Returns the parameters for the widget selections.
     */
    public ChartParameters getParameters()
    {
        ChartParameters parameters = new ChartParameters();

        for(ChartParameter parameter : ChartParameter.values())
        {
            Object value = hasSelection(parameter) ? getSelection(parameter).value() : "";
            if(parameter.multiple())
                value = StringUtils.toList((String)value);
            parameters.put(parameter, value);
        }

        return parameters;
    }

    /**
     * Returns a builder for the configuration.
     * @param id The id of the configuration
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<Widget>
    {
        // The config attribute names
        private static final String ID = "id";
        private static final String CHART_ID = "chart-id";
        private static final String SHOW_TITLE = "show-title";
        private static final String CSS_CLASS = "css-class";
        private static final String CSS_STYLE = "css-style";
        private static final String SELECTIONS = "selections";

        private Widget ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new Widget(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(CHART_ID))
                ret.setChartId((String)map.get(CHART_ID));
            if(map.containsKey(SHOW_TITLE))
                ret.setShowTitle((Boolean)map.get(SHOW_TITLE));
            if(map.containsKey(CSS_CLASS))
                ret.setCssClass((String)map.get(CSS_CLASS));
            if(map.containsKey(CSS_STYLE))
                ret.setCssStyle((String)map.get(CSS_STYLE));

            if(map.containsKey(SELECTIONS))
            {
                Map<String,Map<String,Object>> selections = (Map<String,Map<String,Object>>)map.get(SELECTIONS);
                for(Map.Entry<String,Map<String,Object>> entry : selections.entrySet())
                    ret.addSelection(ChartSelectionFactory.newInstance(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public Widget build()
        {
            return ret;
        }
    }
}