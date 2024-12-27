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

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a single column on a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartColumn implements ConfigElement
{
    private String name;
    private String cssClass;
    private String cssStyle;
    private ChartLabel label;

    /**
     * Default constructor.
     */
    public ChartColumn()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartColumn(ChartColumn obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartColumn obj)
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
     * Configure the dataset.
     */
    public void configure(ListConfig.Builder config)
    {
        ColumnConfig column = ColumnConfig.builder()
            .withName(getName())
            .withCssClass(getCssClass())
            .withCssStyle(getCssStyle())
            .withLabel(getLabel())
            .build();
        config.addColumn(column);
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder
        implements ConfigParser<ChartColumn>
    {
        // The config attribute names
        private static final String NAME = "name";
        private static final String CSS_CLASS = "css-class";
        private static final String CSS_STYLE = "css-style";
        private static final String LABEL = "label";

        private ChartColumn ret = new ChartColumn();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(NAME))
                ret.setName((String)map.get(NAME));
            if(map.containsKey(CSS_CLASS))
                ret.setCssClass((String)map.get(CSS_CLASS));
            if(map.containsKey(CSS_STYLE))
                ret.setCssStyle((String)map.get(CSS_STYLE));
            if(map.containsKey(LABEL))
                ret.setLabel(ChartLabel.builder()
                    .parse((Map<String,Object>)map.get(LABEL)).build());

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ChartColumn build()
        {
            return ret;
        }
    }
}