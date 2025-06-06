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

/**
 * Represents the config for a chart column.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ColumnConfig
{
    private String name;
    private String cssClass;
    private String cssStyle;
    private ChartLabel label;

    /**
     * Default constructor.
     */
    public ColumnConfig()
    {
    }

    /**
     * Copy constructor.
     */
    public ColumnConfig(ColumnConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ColumnConfig obj)
    {
        if(obj != null)
        {
            setName(obj.getName());
            setCssClass(obj.getCssClass());
            setCssStyle(obj.getCssStyle());
            setLabel(new ChartLabel(obj.getLabel()));
        }
    }

    /**
     * Returns the name for the metric.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for the metric.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the css class for the metric.
     */
    public String getCssClass()
    {
        return cssClass;
    }

    /**
     * Sets the css class for the metric.
     */
    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    /**
     * Returns the css style for the metric.
     */
    public String getCssStyle()
    {
        return cssStyle;
    }

    /**
     * Sets the css style for the metric.
     */
    public void setCssStyle(String cssStyle)
    {
        this.cssStyle = cssStyle;
    }

    /**
     * Returns the label for the metric.
     */
    public ChartLabel getLabel()
    {
        return label;
    }

    /**
     * Sets the label for the metric.
     */
    public void setLabel(ChartLabel label)
    {
        this.label = label;
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
    public static class Builder
    {
        private ColumnConfig config = new ColumnConfig();

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
         * Sets the css class to use with the config.
         * @param cssClass The css class
         * @return This object
         */
        public Builder withCssClass(String cssClass)
        {
            config.setCssClass(cssClass);
            return this;
        }

        /**
         * Sets the css style to use with the config.
         * @param cssStyle The css style
         * @return This object
         */
        public Builder withCssStyle(String cssStyle)
        {
            config.setCssStyle(cssStyle);
            return this;
        }

        /**
         * Sets the label to use with the config.
         * @param label The label
         * @return This object
         */
        public Builder withLabel(ChartLabel label)
        {
            config.setLabel(label);
            return this;
        }

        /**
         * Returns the configured config instance
         * @return The config instance
         */
        public ColumnConfig build()
        {
            return config;
        }
    }
}