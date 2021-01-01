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
import java.io.Serializable;
import com.opsmatters.media.util.StringUtils;

/**
 * Represents a widget containing a chart with pre-defined selections.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Widget implements Serializable
{
    public static final String ID = "id";
    public static final String CHART_ID = "chart-id";
    public static final String CSS_CLASS = "css-class";
    public static final String SELECTIONS = "selections";

    private String id = "";
    private String chartId = "";
    private String cssClass = "";
    private Map<Parameter,ChartSelection<?>> selections = new LinkedHashMap<Parameter,ChartSelection<?>>();

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
            setCssClass(obj.getCssClass());
            for(ChartSelection<?> selection : obj.getSelections().values())
                addSelection(ChartSelectionFactory.newInstance(selection));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public Widget(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(CHART_ID))
            setChartId((String)map.get(CHART_ID));
        if(map.containsKey(CSS_CLASS))
            setCssClass((String)map.get(CSS_CLASS));

        if(map.containsKey(SELECTIONS))
        {
            Map<String,Map<String,Object>> selections = (Map<String,Map<String,Object>>)map.get(SELECTIONS);
            for(Map.Entry<String,Map<String,Object>> entry : selections.entrySet())
                addSelection(ChartSelectionFactory.newInstance(entry.getKey(), (Map<String,Object>)entry.getValue()));
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
     * Returns the parameters for the widget selections.
     */
    public Parameters getParameters()
    {
        Parameters parameters = new Parameters();

        for(ChartSelection<?> selection : selections.values())
        {
            Object value = selection.value();
            if(selection.isMultiple())
                value = StringUtils.toList((String)value);
            parameters.put(selection.getParameter(), value);
        }

        return parameters;
    }
}