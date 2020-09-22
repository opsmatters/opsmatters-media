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
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * Class that represents a field extractor for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFieldExtractor implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(ContentFieldExtractor.class.getName());

    public static final String EXPR = "expr";
    public static final String FORMAT = "format";
    public static final String MATCH = "match";

    private String name = "";
    private String expr = "";
    private Pattern exprPattern;
    private String format = "";
    private ContentFieldMatch match = ContentFieldMatch.FIRST;

    /**
     * Default constructor.
     */
    public ContentFieldExtractor()
    {
    }

    /**
     * Constructor that takes a selector.
     */
    public ContentFieldExtractor(String name, String expr)
    {
//GERALD: fix
        setName(name);
        setExpr(expr);
        setFormat("$1");
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public ContentFieldExtractor(String name, Map<String, Object> map)
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
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(EXPR))
            setExpr((String)map.get(EXPR));
        if(map.containsKey(FORMAT))
            setFormat((String)map.get(FORMAT));
        if(map.containsKey(MATCH))
            setMatch((String)map.get(MATCH));
    }
}