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
package com.opsmatters.media.model.content.crawler.field;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a field selector for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldSelector implements ConfigElement
{
    private static final Logger logger = Logger.getLogger(FieldSelector.class.getName());

    private FieldSelectorSource source = FieldSelectorSource.PAGE;
    private String name = "";
    private String expr = "";
    private String attribute = "";
    private boolean multiple = false;
    private String separator = "";
    private List<FieldExclude> excludes;
    private String size = "";
    private boolean background = false;

    /**
     * Constructor that takes a name.
     */
    public FieldSelector(String name)
    {
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public FieldSelector(FieldSelector obj)
    {
        setName(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an expression.
     */
    public FieldSelector(String name, String expr)
    {
        setName(name);
        setExpr(expr);
        setMultiple(getMultipleDefault());
        setSource(FieldSelectorSource.PAGE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldSelector obj)
    {
        if(obj != null)
        {
            setSource(obj.getSource());
            setExpr(obj.getExpr());
            setAttribute(obj.getAttribute());
            setMultiple(obj.isMultiple());
            setSeparator(obj.getSeparator());
            setSize(obj.getSize());
            setBackground(obj.isBackground());

            if(obj.getExcludes() != null)
            {
                for(FieldExclude exclude : obj.getExcludes())
                    addExclude(new FieldExclude(exclude));
            }
        }
    }

    /**
     * Returns the name for this configuration.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name for this configuration.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name for this configuration.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the source for this configuration.
     */
    public FieldSelectorSource getSource()
    {
        return source;
    }

    /**
     * Sets the source for this configuration.
     */
    public void setSource(FieldSelectorSource source)
    {
        this.source = source;
    }

    /**
     * Sets the source for this configuration.
     */
    public void setSource(String source)
    {
        setSource(FieldSelectorSource.fromValue(source));
    }

    /**
     * Returns the CSS selector for this configuration.
     */
    public String getExpr()
    {
        return expr;
    }

    /**
     * Sets the CSS selector for this configuration.
     */
    public void setExpr(String expr)
    {
        this.expr = expr;
    }

    /**
     * Returns <CODE>true</CODE> if the CSS selector has been set.
     */
    public boolean hasExpr()
    {
        return expr != null && expr.length() > 0;
    }

    /**
     * Returns the attribute for this configuration.
     */
    public String getAttribute()
    {
        return attribute;
    }

    /**
     * Sets the attribute for this configuration.
     */
    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }

    /**
     * Returns <CODE>true</CODE> if the attribute has been set.
     */
    public boolean hasAttribute()
    {
        return attribute != null && attribute.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the expression can return multiple results.
     */
    public boolean isMultiple()
    {
        return multiple;
    }

    /**
     * Set to <CODE>true</CODE> if the expression can return multiple results.
     */
    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    /**
     * Set the default for the multiple field depending on the field name.
     */
    public boolean getMultipleDefault()
    {
        return name == Fields.Builder.BODY ? true : false;
    }

    /**
     * Returns the separator for this configuration.
     */
    public String getSeparator()
    {
        return separator;
    }

    /**
     * Sets the separator for this configuration.
     */
    public void setSeparator(String separator)
    {
        this.separator = separator;
    }

    /**
     * Returns <CODE>true</CODE> if the separator has been set.
     */
    public boolean hasSeparator()
    {
        return separator != null && separator.length() > 0;
    }

    /**
     * Returns the image size for this configuration.
     */
    public String getSize()
    {
        return size;
    }

    /**
     * Sets the image size for this configuration.
     */
    public void setSize(String size)
    {
        this.size = size;
    }

    /**
     * Returns <CODE>true</CODE> if the image size has been set.
     */
    public boolean hasSize()
    {
        return size != null && size.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the image should use a background url.
     */
    public boolean isBackground()
    {
        return background;
    }

    /**
     * Set to <CODE>true</CODE> if the image should use a background url.
     */
    public void setBackground(boolean background)
    {
        this.background = background;
    }

    /**
     * Returns the classes to exclude for this configuration.
     */
    public List<FieldExclude> getExcludes()
    {
        return excludes;
    }

    /**
     * Returns the first class to exclude for this configuration.
     */
    public FieldExclude getExclude()
    {
        return hasExcludes() ? getExcludes().get(0) : null ;
    }

    /**
     * Adds a class to exclude for this configuration.
     */
    private void addExclude(FieldExclude exclude)
    {
        if(excludes == null)
            excludes = new ArrayList<FieldExclude>(2);
        excludes.add(exclude);
    }

    /**
     * Adds a class to exclude for this configuration.
     */
    private void addExclude(String exclude)
    {
        addExclude(new FieldExclude(exclude));
    }

    /**
     * Returns <CODE>true</CODE> if there are classes to exclude for this configuration.
     */
    public boolean hasExcludes()
    {
        return excludes != null && excludes.size() > 0;
    }

    /**
     * Returns a builder for the configuration.
     * @param name The name for the configuration
     * @return The builder instance.
     */
    public static Builder builder(String name)
    {
        return new Builder(name);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<FieldSelector>
    {
        // The config attribute names
        private static final String SOURCE = "source";
        private static final String EXPR = "expr";
        private static final String ATTRIBUTE = "attribute";
        private static final String MULTIPLE = "multiple";
        private static final String SEPARATOR = "separator";
        private static final String EXCLUDE = "exclude";
        private static final String EXCLUDES = "excludes";
        private static final String SIZE = "size";
        private static final String BACKGROUND = "background";

        private FieldSelector ret = null;

        /**
         * Constructor that takes a name.
         * @param name The name for the configuration
         */
        public Builder(String name)
        {
            ret = new FieldSelector(name);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            ret.setMultiple(ret.getMultipleDefault());

            if(map.containsKey(SOURCE))
                ret.setSource((String)map.get(SOURCE));
            if(map.containsKey(EXPR))
                ret.setExpr((String)map.get(EXPR));
            if(map.containsKey(ATTRIBUTE))
                ret.setAttribute((String)map.get(ATTRIBUTE));
            if(map.containsKey(MULTIPLE))
                ret.setMultiple((Boolean)map.get(MULTIPLE));
            if(map.containsKey(SEPARATOR))
                ret.setSeparator((String)map.get(SEPARATOR));
            if(map.containsKey(SIZE))
                ret.setSize((String)map.get(SIZE));
            if(map.containsKey(BACKGROUND))
                ret.setBackground((Boolean)map.get(BACKGROUND));

            if(map.containsKey(EXCLUDE))
            {
                ret.addExclude((String)map.get(EXCLUDE));
            }

            if(map.containsKey(EXCLUDES))
            {
                List<String> values = (List<String>)map.get(EXCLUDES);
                for(String value : values)
                  ret.addExclude(value);
            }

            return this;
        }

        /**
         * Sets the expression of the selector.
         * @param expr The expression of the selector
         * @return This object
         */
        public Builder expr(String expr)
        {
            ret.setExpr(expr);
            ret.setMultiple(ret.getMultipleDefault());
            ret.setSource(FieldSelectorSource.PAGE);

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public FieldSelector build()
        {
            return ret;
        }
    }
}