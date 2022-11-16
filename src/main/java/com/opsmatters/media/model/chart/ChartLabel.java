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
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents the config for a  label on a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartLabel implements ConfigElement
{
    private String text;
    private LabelPosition position = LabelPosition.BELOW;
    private String cssClass;
    private String cssStyle;

    /**
     * Default constructor.
     */
    public ChartLabel()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartLabel(ChartLabel obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartLabel obj)
    {
        if(obj != null)
        {
            setText(obj.getText());
            setPosition(obj.getPosition());
            setCssClass(obj.getCssClass());
            setCssStyle(obj.getCssStyle());
        }
    }

    /**
     * Returns the text for the label.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text for the label.
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns the position for the label.
     */
    public LabelPosition getPosition()
    {
        return position;
    }

    /**
     * Sets the position for the label.
     */
    public void setPosition(LabelPosition position)
    {
        this.position = position;
    }

    /**
     * Sets the position for the label.
     */
    public void setPosition(String position)
    {
        setPosition(LabelPosition.valueOf(position));
    }

    /**
     * Returns the css class for the label.
     */
    public String getCssClass()
    {
        return cssClass;
    }

    /**
     * Sets the css class for the label.
     */
    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    /**
     * Returns the css style for the label.
     */
    public String getCssStyle()
    {
        return cssStyle;
    }

    /**
     * Sets the css style for the label.
     */
    public void setCssStyle(String cssStyle)
    {
        this.cssStyle = cssStyle;
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
    public static class Builder implements ConfigParser<ChartLabel>
    {
        // The config attribute names
        private static final String TEXT = "text";
        private static final String POSITION = "position";
        private static final String CSS_CLASS = "css-class";
        private static final String CSS_STYLE = "css-style";

        private ChartLabel ret = new ChartLabel();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(TEXT))
                ret.setText((String)map.get(TEXT));
            if(map.containsKey(POSITION))
                ret.setPosition((String)map.get(POSITION));
            if(map.containsKey(CSS_CLASS))
                ret.setCssClass((String)map.get(CSS_CLASS));
            if(map.containsKey(CSS_STYLE))
                ret.setCssStyle((String)map.get(CSS_STYLE));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ChartLabel build()
        {
            return ret;
        }
    }
}