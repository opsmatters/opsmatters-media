/*
 * Copyright 2019 Gerald Curley
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
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * Class that represents a field in a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentField implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(ContentField.class.getName());

    public static final String SOURCE = "source";
    public static final String SELECTOR = "selector";
    public static final String SELECTOR2 = "selector2";
    public static final String ATTRIBUTE = "attribute";
    public static final String TEXT_CASE = "text-case";
    public static final String MULTIPLE = "multiple";
    public static final String SEPARATOR = "separator";
    public static final String EXPR = "expr";
    public static final String EXPR2 = "expr2";
    public static final String DATE_PATTERN = "date-pattern";
    public static final String DATE_PATTERN2 = "date-pattern2";
    public static final String FORMAT = "format";
    public static final String MATCH = "match";
    public static final String STOP_EXPR = "stop-expr";
    public static final String REMOVE_PARAMETERS = "remove-parameters";
    public static final String GENERATE = "generate";

    private String name = "";
    private ContentFieldSource source = ContentFieldSource.PAGE;
    private String selector = "";
    private String selector2 = "";
    private String attribute = "";
    private ContentFieldCase textCase = ContentFieldCase.NONE;
    private boolean multiple = false;
    private String separator = "";
    private String expr = "";
    private String expr2 = "";
    private String datePattern = "";
    private String datePattern2 = "";
    private String format = "";
    private ContentFieldMatch match = ContentFieldMatch.FIRST;
    private String stopExpr = "";
    private Pattern exprPattern, exprPattern2, stopExprPattern;
    private boolean removeParameters = true;
    private boolean generate = false;

    /**
     * Default constructor.
     */
    public ContentField()
    {
    }

    /**
     * Constructor that takes a selector.
     */
    public ContentField(String name, String selector)
    {
        setName(name);
        setSource(ContentFieldSource.PAGE);
        setSelector(selector);
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public ContentField(String name, Map<String, Object> map)
    {
        setName(name);
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
    public ContentFieldSource getSource()
    {
        return source;
    }

    /**
     * Sets the source for this configuration.
     */
    public void setSource(ContentFieldSource source)
    {
        this.source = source;
    }

    /**
     * Sets the source for this configuration.
     */
    public void setSource(String source)
    {
        setSource(ContentFieldSource.fromValue(source));
    }

    /**
     * Returns the selector for this configuration.
     */
    public String getSelector()
    {
        return selector;
    }

    /**
     * Sets the selector for this configuration.
     */
    public void setSelector(String selector)
    {
        this.selector = selector;
    }

    /**
     * Returns <CODE>true</CODE> if the selector has been set.
     */
    public boolean hasSelector()
    {
        return selector != null && selector.length() > 0;
    }

    /**
     * Returns the 2nd selector for this configuration.
     */
    public String getSelector2()
    {
        return selector2;
    }

    /**
     * Sets the 2nd selector for this configuration.
     */
    public void setSelector2(String selector2)
    {
        this.selector2 = selector2;
    }

    /**
     * Returns <CODE>true</CODE> if the 2nd selector has been set.
     */
    public boolean hasSelector2()
    {
        return selector2 != null && selector2.length() > 0;
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
     * Returns the case for the field value.
     */
    public ContentFieldCase getTextCase()
    {
        return textCase;
    }

    /**
     * Sets the case for the field value.
     */
    public void setTextCase(ContentFieldCase textCase)
    {
        this.textCase = textCase;
    }

    /**
     * Sets the case for the field value.
     */
    public void setTextCase(String textCase)
    {
        setTextCase(ContentFieldCase.fromValue(textCase));
    }

    /**
     * Returns <CODE>true</CODE> if the case has been set.
     */
    public boolean hasTextCase()
    {
        return textCase != null;
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
     * Returns the regular expression for this configuration.
     */
    public String getExpr()
    {
        return expr;
    }

    /**
     * Returns the regular expression pattern for this configuration.
     */
    public Pattern getExprPattern()
    {
        return exprPattern;
    }

    /**
     * Sets the regular expression for this configuration.
     */
    public void setExpr(String expr)
    {
        this.expr = expr;
        this.exprPattern = hasExpr() ? Pattern.compile(expr, Pattern.DOTALL) : null;
    }

    /**
     * Returns <CODE>true</CODE> if the regular expression has been set.
     */
    public boolean hasExpr()
    {
        return expr != null && expr.length() > 0;
    }

    /**
     * Returns the 2nd regular expression for this configuration.
     */
    public String getExpr2()
    {
        return expr2;
    }

    /**
     * Returns the 2nd regular expression pattern for this configuration.
     */
    public Pattern getExprPattern2()
    {
        return exprPattern2;
    }

    /**
     * Sets the 2nd regular expression for this configuration.
     */
    public void setExpr2(String expr2)
    {
        this.expr2 = expr2;
        this.exprPattern2 = hasExpr2() ? Pattern.compile(expr2, Pattern.DOTALL) : null;
    }

    /**
     * Returns <CODE>true</CODE> if the 2nd regular expression has been set.
     */
    public boolean hasExpr2()
    {
        return expr2 != null && expr2.length() > 0;
    }

    /**
     * Returns the date pattern for this configuration.
     */
    public String getDatePattern()
    {
        return datePattern;
    }

    /**
     * Sets the date pattern for this configuration.
     */
    public void setDatePattern(String datePattern)
    {
        this.datePattern = datePattern;
    }

    /**
     * Returns <CODE>true</CODE> if the date pattern has been set.
     */
    public boolean hasDatePattern()
    {
        return datePattern != null && datePattern.length() > 0;
    }

    /**
     * Returns the 2nd date pattern for this configuration.
     */
    public String getDatePattern2()
    {
        return datePattern2;
    }

    /**
     * Sets the 2nd date pattern for this configuration.
     */
    public void setDatePattern2(String datePattern2)
    {
        this.datePattern2 = datePattern2;
    }

    /**
     * Returns <CODE>true</CODE> if the 2nd date pattern has been set.
     */
    public boolean hasDatePattern2()
    {
        return datePattern2 != null && datePattern2.length() > 0;
    }

    /**
     * Returns the format for this configuration.
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * Sets the format for this configuration.
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * Returns <CODE>true</CODE> if the format has been set.
     */
    public boolean hasFormat()
    {
        return format != null && format.length() > 0;
    }

    /**
     * Returns the match for this configuration (ALL/FIRST).
     */
    public ContentFieldMatch getMatch()
    {
        return match;
    }

    /**
     * Sets the match for this configuration (ALL/FIRST).
     */
    public void setMatch(ContentFieldMatch match)
    {
        this.match = match;
    }

    /**
     * Sets the match for this configuration (ALL/FIRST).
     */
    public void setMatch(String match)
    {
        setMatch(ContentFieldMatch.fromValue(match));
    }

    /**
     * Returns <CODE>true</CODE> if the match has been set.
     */
    public boolean hasMatch()
    {
        return match != null;
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
     * Returns <CODE>true</CODE> if the URL query parameters should be removed.
     */
    public boolean removeParameters()
    {
        return removeParameters;
    }

    /**
     * Set to <CODE>true</CODE> if the URL query parameters should be removed.
     */
    public void setRemoveParameters(boolean removeParameters)
    {
        this.removeParameters = removeParameters;
    }

    /**
     * Returns <CODE>true</CODE> if the field should be generated.
     */
    public boolean generate()
    {
        return generate;
    }

    /**
     * Set to <CODE>true</CODE> if the field should be generated.
     */
    public void setGenerate(boolean generate)
    {
        this.generate = generate;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(SOURCE))
            setSource((String)map.get(SOURCE));
        if(map.containsKey(SELECTOR))
            setSelector((String)map.get(SELECTOR));
        if(map.containsKey(SELECTOR2))
            setSelector2((String)map.get(SELECTOR2));
        if(map.containsKey(ATTRIBUTE))
            setAttribute((String)map.get(ATTRIBUTE));
        if(map.containsKey(TEXT_CASE))
            setTextCase((String)map.get(TEXT_CASE));
        if(map.containsKey(MULTIPLE))
            setMultiple((Boolean)map.get(MULTIPLE));
        if(map.containsKey(SEPARATOR))
            setSeparator((String)map.get(SEPARATOR));
        if(map.containsKey(EXPR))
            setExpr((String)map.get(EXPR));
        if(map.containsKey(EXPR2))
            setExpr2((String)map.get(EXPR2));
        if(map.containsKey(DATE_PATTERN))
            setDatePattern((String)map.get(DATE_PATTERN));
        if(map.containsKey(DATE_PATTERN2))
            setDatePattern2((String)map.get(DATE_PATTERN2));
        if(map.containsKey(FORMAT))
            setFormat((String)map.get(FORMAT));
        if(map.containsKey(MATCH))
            setMatch((String)map.get(MATCH));
        if(map.containsKey(STOP_EXPR))
            setStopExpr((String)map.get(STOP_EXPR));
        if(map.containsKey(REMOVE_PARAMETERS))
            setRemoveParameters((Boolean)map.get(REMOVE_PARAMETERS));
        if(map.containsKey(GENERATE))
            setGenerate((Boolean)map.get(GENERATE));
    }
}