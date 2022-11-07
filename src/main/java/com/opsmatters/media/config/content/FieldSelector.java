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
package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class that represents a field selector for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldSelector implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(FieldSelector.class.getName());

    public static final String SOURCE = "source";
    public static final String EXPR = "expr";
    public static final String ATTRIBUTE = "attribute";
    public static final String MULTIPLE = "multiple";
    public static final String SEPARATOR = "separator";
    public static final String EXCLUDE = "exclude";
    public static final String EXCLUDES = "excludes";
    public static final String SIZE = "size";

    private SelectorSource source = SelectorSource.PAGE;
    private String name = "";
    private String expr = "";
    private String attribute = "";
    private boolean multiple = false;
    private String separator = "";
    private List<FieldExclude> excludes;
    private String size = "";

    /**
     * Default constructor.
     */
    public FieldSelector()
    {
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
        setMultiple(getMultipleDefault(name));
        setSource(SelectorSource.PAGE);
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public FieldSelector(String name, Map<String, Object> map)
    {
        setName(name);
        setMultiple(getMultipleDefault(name));
        parse(map);
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
    public SelectorSource getSource()
    {
        return source;
    }

    /**
     * Sets the source for this configuration.
     */
    public void setSource(SelectorSource source)
    {
        this.source = source;
    }

    /**
     * Sets the source for this configuration.
     */
    public void setSource(String source)
    {
        setSource(SelectorSource.fromValue(source));
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
    public boolean getMultipleDefault(String name)
    {
        return name == ContentFields.BODY ? true : false;
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
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(SOURCE))
            setSource((String)map.get(SOURCE));
        if(map.containsKey(EXPR))
            setExpr((String)map.get(EXPR));
        if(map.containsKey(ATTRIBUTE))
            setAttribute((String)map.get(ATTRIBUTE));
        if(map.containsKey(MULTIPLE))
            setMultiple((Boolean)map.get(MULTIPLE));
        if(map.containsKey(SEPARATOR))
            setSeparator((String)map.get(SEPARATOR));
        if(map.containsKey(SIZE))
            setSize((String)map.get(SIZE));

        if(map.containsKey(EXCLUDE))
        {
            addExclude((String)map.get(EXCLUDE));
        }

        if(map.containsKey(EXCLUDES))
        {
            List<String> values = (List<String>)map.get(EXCLUDES);
            for(String value : values)
              addExclude(value);
        }
    }
}