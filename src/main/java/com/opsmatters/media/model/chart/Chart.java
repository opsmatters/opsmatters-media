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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Chart<E extends Serializable> implements ConfigElement
{
    private String id = "";
    private String title = "";
    private String type;
    private Map<ChartParameter,ChartSelection<?>> selections = new LinkedHashMap<ChartParameter,ChartSelection<?>>();

    /**
     * Constructor that takes an id.
     */
    protected Chart(String id)
    {
        setId(id);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Chart<E> obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setTitle(obj.getTitle());
            setType(obj.getType());
            for(ChartSelection<?> selection : obj.getSelections().values())
                addSelection(ChartSelectionFactory.newInstance(selection));
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
     * Returns the id of the chart.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the chart.
     */
    public void setId(String id)
    {
        this.id = id;
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
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type for the chart.
     */
    public void setType(String type)
    {
        this.type = type;
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
     * Builder to make chart construction easier.
     */
    protected abstract static class Builder<E extends Serializable, T extends Chart<E>, B extends Builder<E,T,B>>
        implements ConfigParser<Chart<E>>
    {
        // The config attribute names
        public static final String TYPE = "type";
        private static final String ID = "id";
        private static final String TITLE = "title";
        private static final String SELECTIONS = "selections";

        private Chart ret = null;

        /**
         * Sets the chart.
         * @param chart The chart
         */
        public void set(Chart chart)
        {
            ret = chart;
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public B parse(Map<String, Object> map)
        {
            if(map.containsKey(TITLE))
                ret.setTitle((String)map.get(TITLE));
            if(map.containsKey(TYPE))
                ret.setType((String)map.get(TYPE));
            if(map.containsKey(SELECTIONS))
            {
                Map<String,Map<String,Object>> selections = (Map<String,Map<String,Object>>)map.get(SELECTIONS);
                for(Map.Entry<String,Map<String,Object>> entry : selections.entrySet())
                    ret.addSelection(ChartSelectionFactory.newInstance(entry.getKey(), (Map<String,Object>)entry.getValue()));
            }

            return self();
        }

        /**
         * Returns this object.
         * @return This object
         */
        protected abstract B self();

        /**
         * Returns the configured chart instance
         * @return The chart instance
         */
        public abstract T build();
    }
}