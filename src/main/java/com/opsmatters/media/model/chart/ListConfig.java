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
import java.io.Serializable;

/**
 * Represents the config for a list chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListConfig<E extends Serializable> implements Serializable
{
    private String name;
    private List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
    private List<E> data;

    /**
     * Default constructor.
     */
    public ListConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public ListConfig(ListConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ListConfig<E> obj)
    {
        if(obj != null)
        {
            setName(obj.getName());
            for(ColumnConfig column : obj.getColumns())
                addColumn(new ColumnConfig(column));
            setData(obj.getData());
        }
    }

    /**
     * Returns the name for the list.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the list.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the columns.
     */
    public List<ColumnConfig> getColumns()
    {
        return columns;
    }

    /**
     * Adds the given column.
     */
    public void addColumn(ColumnConfig column)
    {
        columns.add(column);
    }

    /**
     * Returns the column at the given index.
     */
    public ColumnConfig getColumn(int i)
    {
        return columns.get(i);
    }

    /**
     * Returns the data for the list.
     */
    public List<E> getData()
    {
        return data;
    }

    /**
     * Sets the data for the list.
     */
    public void setData(List<E> data)
    {
        this.data = data;
    }

    /**
     * Returns a builder for the config.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make config construction easier.
     */
    public static class Builder<E extends Serializable>
    {
        private ListConfig<E> config = new ListConfig<E>();

        /**
         * Sets the name to use with the config.
         * @param name The name
         * @return This object
         */
        public Builder withName(String name)
        {
            config.setName(name);
            return this;
        }

        /**
         * Sets the column to use with the config.
         * @param column The column
         * @return This object
         */
        public Builder addColumn(ColumnConfig column)
        {
            config.addColumn(column);
            return this;
        }

        /**
         * Sets the data to use with the config.
         * @param data The data
         * @return This object
         */
        public Builder withData(List<E> data)
        {
            config.setData(data);
            return this;
        }

        /**
         * Returns the configured config instance
         * @return The config instance
         */
        public ListConfig build()
        {
            return config;
        }
    }
}