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
import java.util.regex.Pattern;

/**
 * Class that represents a field selector for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFieldSelector implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(ContentFieldSelector.class.getName());

    public static final String SOURCE = "source";
    public static final String EXPR = "expr";
    public static final String ATTRIBUTE = "attribute";
    public static final String MULTIPLE = "multiple";
    public static final String SEPARATOR = "separator";
    public static final String EXCLUDE = "exclude";
    public static final String EXCLUDES = "excludes";
    public static final String STOP_EXPR = "stop-expr";

    private SelectorSource source = SelectorSource.PAGE;
    private String name = "";
    private String expr = "";
    private String attribute = "";
    private boolean multiple = false;
    private String separator = "";
    private List<String> excludes;
    private String stopExpr = "";
    private Pattern stopExprPattern;

    /**
     * Default constructor.
     */
    public ContentFieldSelector()
    {
    }

    /**
     * Constructor that takes an expression.
     */
    public ContentFieldSelector(String name, String expr)
    {
        setName(name);
        setExpr(expr);
        setMultiple(getMultipleDefault(name));
        setSource(SelectorSource.PAGE);
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public ContentFieldSelector(String name, Map<String, Object> map)
    {
        setName(name);
        setMultiple(getMultipleDefault(name));
        parse(map);
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
     * Returns the classes to exclude for this configuration.
     */
    public List<String> getExcludes()
    {
        return excludes;
    }

    /**
     * Returns the first class to exclude for this configuration.
     */
    public String getExclude()
    {
        return hasExcludes() ? getExcludes().get(0) : null ;
    }

    /**
     * Adds a class to exclude for this configuration.
     */
    private void addExclude(String exclude)
    {
        if(excludes == null)
            excludes = new ArrayList<String>(2);
        excludes.add(exclude);
    }

    /**
     * Returns <CODE>true</CODE> if there are classes to exclude for this configuration.
     */
    public boolean hasExcludes()
    {
        return excludes != null && excludes.size() > 0;
    }

    /**
     * Returns the stopping regular expression for this configuration.
     */
    public String getStopExpr()
    {
        return stopExpr;
    }

    /**
     * Returns the stopping regular expression pattern for this configuration.
     */
    public Pattern getStopExprPattern()
    {
        return stopExprPattern;
    }

    /**
     * Sets the stopping regular expression for this configuration.
     */
    public void setStopExpr(String stopExpr)
    {
        this.stopExpr = stopExpr;
        this.stopExprPattern = hasStopExpr() ? Pattern.compile(stopExpr, Pattern.DOTALL) : null;
    }

    /**
     * Returns <CODE>true</CODE> if the stopping regular expression has been set.
     */
    public boolean hasStopExpr()
    {
        return stopExpr != null && stopExpr.length() > 0;
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
        if(map.containsKey(STOP_EXPR))
            setStopExpr((String)map.get(STOP_EXPR));

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