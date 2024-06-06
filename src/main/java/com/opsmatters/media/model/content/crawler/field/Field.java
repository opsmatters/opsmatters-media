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
package com.opsmatters.media.model.content.crawler.field;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a field in a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Field implements ConfigElement
{
    private static final Logger logger = Logger.getLogger(Field.class.getName());

    private String name = "";
    private List<FieldSelector> selectors;
    private List<FieldExtractor> extractors;
    private FieldCase textCase = FieldCase.NONE;
    private List<String> datePatterns;
    private List<FieldFilter> filters;
    private String basePath = "";
    private boolean removeParameters = true;
    private boolean trailingSlash = false;
    private boolean optional = false;

    /**
     * Constructor that takes a name.
     */
    public Field(String name)
    {
        setName(name);
    }

    /**
     * Constructor that takes a selector expression.
     */
    public Field(String name, String expr)
    {
        setName(name);
        addSelector(new FieldSelector(name, expr));
    }

    /**
     * Copy constructor.
     */
    public Field(Field obj)
    {
        setName(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Field obj)
    {
        if(obj != null)
        {
            if(obj.getSelectors() != null)
            {
                for(FieldSelector selector : obj.getSelectors())
                    addSelector(new FieldSelector(selector));
            }

            if(obj.getExtractors() != null)
            {
                for(FieldExtractor extractor : obj.getExtractors())
                    addExtractor(new FieldExtractor(extractor));
            }

            if(obj.getFilters() != null)
            {
                for(FieldFilter filter : obj.getFilters())
                    addFilter(new FieldFilter(filter));
            }

            if(obj.getDatePatterns() != null)
            {
                for(String datePattern : obj.getDatePatterns())
                    addDatePattern(datePattern);
            }

            setTextCase(obj.getTextCase());
            setBasePath(obj.getBasePath());
            setRemoveParameters(obj.removeParameters());
            setTrailingSlash(obj.hasTrailingSlash());
            setOptional(obj.isOptional());
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
     * Returns the selectors for this configuration.
     */
    public List<FieldSelector> getSelectors()
    {
        return selectors;
    }

    /**
     * Returns the given selector for this configuration.
     */
    public FieldSelector getSelector(int i)
    {
        return selectors.get(i);
    }

    /**
     * Adds a selector object for the given field.
     */
    public void addSelector(FieldSelector selector)
    {
        if(selectors == null)
            selectors = new ArrayList<FieldSelector>(2);
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
    public List<FieldExtractor> getExtractors()
    {
        return extractors;
    }

    /**
     * Adds an extractor object for the given field.
     */
    public void addExtractor(FieldExtractor extractor)
    {
        if(extractors == null)
            extractors = new ArrayList<FieldExtractor>(2);
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
    public FieldCase getTextCase()
    {
        return textCase;
    }

    /**
     * Sets the case for the field value.
     */
    public void setTextCase(FieldCase textCase)
    {
        this.textCase = textCase;
    }

    /**
     * Sets the case for the field value.
     */
    public void setTextCase(String textCase)
    {
        setTextCase(FieldCase.fromValue(textCase));
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
     * Adds a date pattern object for the given field.
     */
    public void addDatePattern(String datePattern)
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
     * Returns the filters for this configuration.
     */
    public List<FieldFilter> getFilters()
    {
        return filters;
    }

    /**
     * Returns the first filter for this configuration.
     */
    public FieldFilter getFilter()
    {
        return hasFilters() ? getFilters().get(0) : null ;
    }

    /**
     * Adds a filter for this configuration.
     */
    public void addFilter(FieldFilter filter)
    {
        if(filters == null)
            filters = new ArrayList<FieldFilter>(2);
        filters.add(filter);
    }

    /**
     * Returns <CODE>true</CODE> if there are filters for this configuration.
     */
    public boolean hasFilters()
    {
        return filters != null && filters.size() > 0;
    }

    /**
     * Returns the base path for this configuration.
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Sets the base path for this configuration.
     */
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    /**
     * Returns <CODE>true</CODE> if the base path has been set.
     */
    public boolean hasBasePath()
    {
        return basePath != null && basePath.length() > 0;
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
     * Returns <CODE>true</CODE> if the URL should have a trailing slash.
     */
    public boolean hasTrailingSlash()
    {
        return trailingSlash;
    }

    /**
     * Set to <CODE>true</CODE> if the URL should have a trailing slash.
     */
    public void setTrailingSlash(boolean trailingSlash)
    {
        this.trailingSlash = trailingSlash;
    }

    /**
     * Returns <CODE>true</CODE> if the field is optional and should not raise warnings if missing.
     */
    public boolean isOptional()
    {
        return optional;
    }

    /**
     * Set to <CODE>true</CODE> if the field is optional and should not raise warnings if missing.
     */
    public void setOptional(boolean optional)
    {
        this.optional = optional;
    }

    /**
     * Add a selector object for the given field.
     */
    private void addSelector(String name, Object value)
    {
        FieldSelector.Builder builder = FieldSelector.builder(name);
        if(value instanceof String)
            builder = builder.expr((String)value);
        else if(value instanceof Map)
            builder = builder.parse((Map<String,Object>)value);
        addSelector(builder.build());
    }

    /**
     * Add an extractor object for the given field.
     */
    private void addExtractor(String name, Object value)
    {
        FieldExtractor.Builder builder = FieldExtractor.builder(name);
        if(value instanceof String)
            builder = builder.expr((String)value);
        else if(value instanceof Map)
            builder = builder.parse((Map<String,Object>)value);
        addExtractor(builder.build());
    }

    /**
     * Add a filter object for the given selector.
     */
    private void addFilter(Object value)
    {
        FieldFilter.Builder builder = FieldFilter.builder();
        if(value instanceof String)
            builder = builder.expr((String)value);
        else if(value instanceof Map)
            builder = builder.parse((Map<String,Object>)value);
        addFilter(builder.build());
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
    public static class Builder implements ConfigParser<Field>
    {
        // The config attribute names
        private static final String SELECTOR = "selector";
        private static final String SELECTORS = "selectors";
        private static final String EXTRACTOR = "extractor";
        private static final String EXTRACTORS = "extractors";
        private static final String TEXT_CASE = "text-case";
        private static final String DATE_PATTERN = "date-pattern";
        private static final String DATE_PATTERNS = "date-patterns";
        private static final String FILTER = "filter";
        private static final String FILTERS = "filters";
        private static final String BASE_PATH = "base-path";
        private static final String REMOVE_PARAMETERS = "remove-parameters";
        private static final String TRAILING_SLASH = "trailing-slash";
        private static final String OPTIONAL = "optional";

        private Field ret = null;

        /**
         * Constructor that takes an name.
         * @param name The name for the configuration
         */
        public Builder(String name)
        {
            ret = new Field(name);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            String name = ret.getName();

            if(map.containsKey(TEXT_CASE))
                ret.setTextCase((String)map.get(TEXT_CASE));
            if(map.containsKey(BASE_PATH))
                ret.setBasePath((String)map.get(BASE_PATH));
            if(map.containsKey(REMOVE_PARAMETERS))
                ret.setRemoveParameters((Boolean)map.get(REMOVE_PARAMETERS));
            if(map.containsKey(TRAILING_SLASH))
                ret.setTrailingSlash((Boolean)map.get(TRAILING_SLASH));
            if(map.containsKey(OPTIONAL))
                ret.setOptional((Boolean)map.get(OPTIONAL));

            if(map.containsKey(SELECTOR))
            {
                ret.addSelector(name, map.get(SELECTOR));
            }

            if(map.containsKey(SELECTORS))
            {
                List<Object> values = (List<Object>)map.get(SELECTORS);
                for(Object value : values)
                    ret.addSelector(name, value);
            }

            if(map.containsKey(EXTRACTOR))
            {
                ret.addExtractor(name, map.get(EXTRACTOR));
            }

            if(map.containsKey(EXTRACTORS))
            {
                List<Object> values = (List<Object>)map.get(EXTRACTORS);
                for(Object value : values)
                    ret.addExtractor(name, value);
            }

            if(map.containsKey(DATE_PATTERN))
            {
                ret.addDatePattern((String)map.get(DATE_PATTERN));
            }

            if(map.containsKey(DATE_PATTERNS))
            {
                List<String> values = (List<String>)map.get(DATE_PATTERNS);
                for(String value : values)
                  ret.addDatePattern(value);
            }

            if(map.containsKey(FILTER))
            {
                ret.addFilter(map.get(FILTER));
            }

            if(map.containsKey(FILTERS))
            {
                List<Object> values = (List<Object>)map.get(FILTERS);
                for(Object value : values)
                    ret.addFilter(value);
            }

            return this;
        }

        /**
         * Sets the expression of the field.
         * @param expr The expression of the field
         * @return This object
         */
        public Builder expr(String expr)
        {
            ret.addSelector(FieldSelector.builder(ret.getName()).expr(expr).build());
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public Field build()
        {
            return ret;
        }
    }
}