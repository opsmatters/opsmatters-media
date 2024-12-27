/*
 * Copyright 2024 Gerald Curley
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
import java.util.ArrayList;
import java.util.Map;
import java.io.Serializable;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a chart containing a list.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListChart<E extends Serializable> extends Chart<E>
{
    public static final String TYPE = "LIST";

    private List<ChartColumn> columns = new ArrayList<ChartColumn>();
    private ChartSource source;

    /**
     * Constructor that takes an id.
     */
    protected ListChart(String id)
    {
        super(id);
        setType(TYPE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ListChart<E> obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            for(ChartColumn column : obj.getColumns())
                addColumn(new ChartColumn(column));
            setSource(new ChartSource(obj.getSource()));
        }
    }

    /**
     * Adds a column to the columns for the chart.
     */
    public List<ChartColumn> getColumns()
    {
        return this.columns;
    }

    /**
     * Adds a column to the columns for the chart.
     */
    public void addColumn(ChartColumn column)
    {
        this.columns.add(column);
    }

    /**
     * Returns the number of columns.
     */
    public int numColumns()
    {
        return columns.size();
    }

    /**
     * Returns the column at the given index.
     */
    public ChartColumn getColumn(int i)
    {
        return columns.get(i);
    }

    /**
     * Returns the source for the list.
     */
    public ChartSource getSource()
    {
        return source;
    }

    /**
     * Sets the source for the list.
     */
    public void setSource(ChartSource source)
    {
        this.source = source;
    }

    /**
     * Create the list configuration.
     */
    public ListConfig.Builder configure()
    {
        return ListConfig.builder();
    }
    /**
     * Configure the dataset.
     */
    public void configure(ListConfig.Builder<E> config, List<E> items)
    {
        if(items != null && items.size() > 0)
        {
            config.withName(getTitle());
            config.withData(items);
        }
    }

    /**
     * Returns a builder for the chart.
     * @param id The id of the chart
     * @return The builder instance.
     */
    public static Builder builder(String id)
    {
        return new Builder(id);
    }

    /**
     * Builder to make chart construction easier.
     */
    public static class Builder<E extends Serializable>
        extends Chart.Builder<E, ListChart<E>, Builder<E>>
    {
        // The config attribute names
        private static final String COLUMNS = "columns";
        private static final String SOURCE = "source";

        private ListChart ret = null;

        /**
         * Constructor that takes an id.
         * @param id The id for the chart
         */
        public Builder(String id)
        {
            ret = new ListChart(id);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);

            if(map.containsKey(COLUMNS))
            {
                List<Map<String,Object>> columns = (List<Map<String,Object>>)map.get(COLUMNS);
                for(Map<String,Object> config : columns)
                    ret.addColumn(ChartColumn.builder()
                        .parse((Map<String,Object>)config).build());
            }

            if(map.containsKey(SOURCE))
                ret.setSource(ChartSource.builder()
                    .parse((Map<String,Object>)map.get(SOURCE)).build());

            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured chart instance
         * @return The chart instance
         */
        @Override
        public ListChart build()
        {
            return ret;
        }
    }
}