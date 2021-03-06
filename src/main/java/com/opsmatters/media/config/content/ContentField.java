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
import java.util.List;
import java.util.ArrayList;
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

    public static final String SELECTOR = "selector";
    public static final String SELECTORS = "selectors";
    public static final String EXTRACTOR = "extractor";
    public static final String EXTRACTORS = "extractors";
    public static final String TEXT_CASE = "text-case";
    public static final String DATE_PATTERN = "date-pattern";
    public static final String DATE_PATTERNS = "date-patterns";
    public static final String STOP_EXPR = "stop-expr";
    public static final String REMOVE_PARAMETERS = "remove-parameters";
    public static final String GENERATE = "generate";

    private String name = "";
    private List<ContentFieldSelector> selectors;
    private List<ContentFieldExtractor> extractors;
    private ContentFieldCase textCase = ContentFieldCase.NONE;
    private List<String> datePatterns;
    private String stopExpr = "";
    private Pattern stopExprPattern;
    private boolean removeParameters = true;
    private boolean generate = false;

    /**
     * Default constructor.
     */
    public ContentField()
    {
    }

    /**
     * Constructor that takes a selector expression.
     */
    public ContentField(String name, String expr)
    {
        setName(name);
        addSelector(new ContentFieldSelector(name, expr));
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
     * Returns the selectors for this configuration.
     */
    public List<ContentFieldSelector> getSelectors()
    {
        return selectors;
    }

    /**
     * Returns the given selector for this configuration.
     */
    public ContentFieldSelector getSelector(int i)
    {
        return selectors.get(i);
    }

    /**
     * Adds a selector object for the given field.
     */
    private void addSelector(ContentFieldSelector selector)
    {
        if(selectors == null)
            selectors = new ArrayList<ContentFieldSelector>(2);
        selectors.add(selector);
    }

    /**
     * Returns <CODE>true</CODE> if there are selectors for this configuration.
     */
    public boolean hasSelectors()
    {
        return selectors != null && selectors.size() > 0;
    }

    /**
     * Returns the extractors for this configuration.
     */
    public List<ContentFieldExtractor> getExtractors()
    {
        return extractors;
    }

    /**
     * Adds an extractor object for the given field.
     */
    private void addExtractor(ContentFieldExtractor extractor)
    {
        if(extractors == null)
            extractors = new ArrayList<ContentFieldExtractor>(2);
        extractors.add(extractor);
    }

    /**
     * Returns <CODE>true</CODE> if there are extractors for this configuration.
     */
    public boolean hasExtractors()
    {
        return extractors != null && extractors.size() > 0;
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
     * Returns the date patterns for this configuration.
     */
    public List<String> getDatePatterns()
    {
        return datePatterns;
    }

    /**
     * Returns the first date pattern for this configuration.
     */
    public String getDatePattern()
    {
        return hasDatePatterns() ? getDatePatterns().get(0) : null ;
    }

    /**
     * Adds a date pattern objects for the given field.
     */
    private void addDatePattern(String datePattern)
    {
        if(datePatterns == null)
            datePatterns = new ArrayList<String>(2);
        datePatterns.add(datePattern);
    }

    /**
     * Returns <CODE>true</CODE> if there are date patterns for this configuration.
     */
    public boolean hasDatePatterns()
    {
        return datePatterns != null && datePatterns.size() > 0;
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
        if(map.containsKey(TEXT_CASE))
            setTextCase((String)map.get(TEXT_CASE));
        if(map.containsKey(STOP_EXPR))
            setStopExpr((String)map.get(STOP_EXPR));
        if(map.containsKey(REMOVE_PARAMETERS))
            setRemoveParameters((Boolean)map.get(REMOVE_PARAMETERS));
        if(map.containsKey(GENERATE))
            setGenerate((Boolean)map.get(GENERATE));

        if(map.containsKey(DATE_PATTERN))
        {
            addDatePattern((String)map.get(DATE_PATTERN));
        }

        if(map.containsKey(DATE_PATTERNS))
        {
            List<String> values = (List<String>)map.get(DATE_PATTERNS);
            for(String value : values)
              addDatePattern(value);
        }

        if(map.containsKey(SELECTOR))
        {
            addSelector(getName(), map.get(SELECTOR));
        }

        if(map.containsKey(SELECTORS))
        {
            List<Object> values = (List<Object>)map.get(SELECTORS);
            for(Object value : values)
                addSelector(getName(), value);
        }

        if(map.containsKey(EXTRACTOR))
        {
            addExtractor(getName(), map.get(EXTRACTOR));
        }

        if(map.containsKey(EXTRACTORS))
        {
            List<Object> values = (List<Object>)map.get(EXTRACTORS);
            for(Object value : values)
                addExtractor(getName(), value);
        }
    }

    /**
     * Add a selector object for the given field.
     */
    private void addSelector(String name, Object value)
    {
        ContentFieldSelector selector = null;
        if(value instanceof String)
            selector = new ContentFieldSelector(name, (String)value);
        else if(value instanceof Map)
            selector = new ContentFieldSelector(name, (Map<String,Object>)value);
        if(selector != null)
          addSelector(selector);
    }

    /**
     * Add an extractor object for the given field.
     */
    private void addExtractor(String name, Object value)
    {
        ContentFieldExtractor extractor = null;
        if(value instanceof String)
            extractor = new ContentFieldExtractor(name, (String)value);
        else if(value instanceof Map)
            extractor = new ContentFieldExtractor(name, (Map<String,Object>)value);
        if(extractor != null)
          addExtractor(extractor);
    }
}