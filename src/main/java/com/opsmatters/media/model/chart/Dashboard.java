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
import java.util.Map;
import java.util.LinkedHashMap;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a dashboard containing widgets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Dashboard implements ConfigElement
{
    private String id = "";
    private String title = "";
    private Map<String,Widget> widgets = new LinkedHashMap<String,Widget>();
    private Map<ChartParameter,ChartSelection<?>> selections = new LinkedHashMap<ChartParameter,ChartSelection<?>>();

    /**
     * Default constructor.
     */
    public Dashboard(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public Dashboard(Dashboard obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Dashboard obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setTitle(obj.getTitle());
            for(Widget widget : obj.getWidgets().values())
                addWidget(new Widget(widget));
            for(ChartSelection<?> selection : obj.getSelections().values())
                addSelection(ChartSelectionFactory.newInstance(selection));
        }
    }

    /**
     * Returns the id of the dashboard.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the id of the dashboard.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the dashboard.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the title of the dashboard.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title for the dashboard.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Adds a widget to the widgets for the dashboard.
     */
    public Map<String,Widget> getWidgets()
    {
        return this.widgets;
    }

    /**
     * Adds a widget to the widgets for the dashboard.
     */
    public void addWidget(Widget widget)
    {
        this.widgets.put(widget.getId(), widget);
    }

    /**
     * Returns the number of widgets.
     */
    public int numWidgets()
    {
        return widgets.size();
    }

    /**
     * Returns the widget at the given index.
     */
    public Widget getWidget(int i)
    {
        return widgets.get(i);
    }

    /**
     * Adds a selection for the dashboard.
     */
    public Map<ChartParameter,ChartSelection<?>> getSelections()
    {
        return this.selections;
    }

    /**
     * Adds a selection for the dashboard.
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
     * Returns a builder for the dashboard.
     * @param id The id of the dashboard
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make dashboard construction easier.
     */
    public static class Builder implements ConfigParser<Dashboard>
    {
        // The config attribute names
        private static final String TITLE = "title";
        private static final String WIDGETS = "widgets";
        private static final String SELECTIONS = "selections";

        private Dashboard ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the configuration
         */
        public Builder(String id)
        {
            ret = new Dashboard(id);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(TITLE))
                ret.setTitle((String)map.get(TITLE));

            if(map.containsKey(WIDGETS))
            {
                List<Map<String,Object>> widgets = (List<Map<String,Object>>)map.get(WIDGETS);
                for(Map<String,Object> config : widgets)
                {
                    for(Map.Entry<String,Object> entry : config.entrySet())
                        ret.addWidget(Widget.builder(entry.getKey())
                            .parse((Map<String,Object>)entry.getValue()).build());
                }
            }

            if(map.containsKey(SELECTIONS))
            {
                Map<String,Map<String,Object>> selections = (Map<String,Map<String,Object>>)map.get(SELECTIONS);
                for(Map.Entry<String,Map<String,Object>> entry : selections.entrySet())
                    ret.addSelection(ChartSelectionFactory.newInstance(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }

            // Use the dashboard selections as default selections for the widgets
            if(ret.getWidgets() != null)
            {
                for(Widget widget : ret.getWidgets().values())
                {
                    if(widget.getSelections().size() == 0) // Widget has no selections
                    {
                        for(ChartSelection<?> selection : ret.getSelections().values())
                            widget.addSelection(selection);
                    }
                }
            }

            return this;
        }

        /**
         * Returns the configured dashboard instance
         * @return The dashboard instance
         */
        public Dashboard build()
        {
            return ret;
        }
    }
}