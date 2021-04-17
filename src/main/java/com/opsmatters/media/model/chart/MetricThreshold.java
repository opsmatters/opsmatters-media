/*
 * Copyright 2021 Gerald Curley
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

/**
 * Represents the config for a metric threshold.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MetricThreshold<N extends Number>
{
    public static final String VALUE = "value";
    public static final String CSS_CLASS = "css-class";

    private N value;
    private String cssClass;

    /**
     * Default constructor.
     */
    public MetricThreshold()
    {
    }

    /**
     * Copy constructor.
     */
    public MetricThreshold(MetricThreshold<N> obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(MetricThreshold<N> obj)
    {
        if(obj != null)
        {
            setValue(obj.getValue());
            setCssClass(obj.getCssClass());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public MetricThreshold(Map<String, Object> map)
    {
        if(map.containsKey(VALUE))
            setValue((N)map.get(VALUE));
        if(map.containsKey(CSS_CLASS))
            setCssClass((String)map.get(CSS_CLASS));
    }

    /**
     * Returns the value for the threshold.
     */
    public String toString()
    {
        return value.toString();
    }

    /**
     * Returns the value for the threshold.
     */
    public N getValue()
    {
        return value;
    }

    /**
     * Sets the value for the threshold.
     */
    public void setValue(N value)
    {
        this.value = value;
    }

    /**
     * Returns the css class for the threshold.
     */
    public String getCssClass()
    {
        return cssClass;
    }

    /**
     * Sets the css class for the threshold.
     */
    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }
}