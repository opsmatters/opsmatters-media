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
import java.util.regex.Pattern;
import java.util.logging.Logger;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a field extractor for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldExtractor implements ConfigElement
{
    private static final Logger logger = Logger.getLogger(FieldExtractor.class.getName());

    public static final String DEFAULT_FORMAT = "$1";

    private String name = "";
    private String validator = "";
    private Pattern validatorPattern;
    private String expr = "";
    private Pattern exprPattern;
    private String format = DEFAULT_FORMAT;
    private FieldMatch match = FieldMatch.FIRST;

    /**
     * Constructor that takes a name.
     */
    public FieldExtractor(String name)
    {
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public FieldExtractor(FieldExtractor obj)
    {
        setName(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an expression.
     */
    public FieldExtractor(String name, String expr)
    {
        setName(name);
        setExpr(expr);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldExtractor obj)
    {
        if(obj != null)
        {
            setValidator(obj.getValidator());
            setExpr(obj.getExpr());
            setFormat(obj.getFormat());
            setMatch(obj.getMatch());
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
     * Returns the validator for this configuration.
     */
    public String getValidator()
    {
        return validator;
    }

    /**
     * Returns the validator regular expression pattern for this configuration.
     */
    public Pattern getValidatorPattern()
    {
        return validatorPattern;
    }

    /**
     * Sets the validator regular expression for this configuration.
     */
    public void setValidator(String validator)
    {
        this.validator = validator;
        this.validatorPattern = hasValidator() ? Pattern.compile(validator, Pattern.DOTALL) : null;
    }

    /**
     * Returns <CODE>true</CODE> if the validator regular expression has been set.
     */
    public boolean hasValidator()
    {
        return validator != null && validator.length() > 0;
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
    public FieldMatch getMatch()
    {
        return match;
    }

    /**
     * Sets the match for this configuration (ALL/FIRST).
     */
    public void setMatch(FieldMatch match)
    {
        this.match = match;
    }

    /**
     * Sets the match for this configuration (ALL/FIRST).
     */
    public void setMatch(String match)
    {
        setMatch(FieldMatch.fromValue(match));
    }

    /**
     * Returns <CODE>true</CODE> if the match has been set.
     */
    public boolean hasMatch()
    {
        return match != null;
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
    public static class Builder implements ConfigParser<FieldExtractor>
    {
        // The config attribute names
        private static final String VALIDATOR = "validator";
        private static final String EXPR = "expr";
        private static final String FORMAT = "format";
        private static final String MATCH = "match";

        private FieldExtractor ret = null;

        /**
         * Constructor that takes a name.
         * @param name The name for the configuration
         */
        public Builder(String name)
        {
            ret = new FieldExtractor(name);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(VALIDATOR))
                ret.setValidator((String)map.get(VALIDATOR));
            if(map.containsKey(EXPR))
                ret.setExpr((String)map.get(EXPR));
            if(map.containsKey(FORMAT))
                ret.setFormat((String)map.get(FORMAT));
            if(map.containsKey(MATCH))
                ret.setMatch((String)map.get(MATCH));

            return this;
        }

        /**
         * Sets the expression of the extractor.
         * @param expr The expression of the extractor
         * @return This object
         */
        public Builder expr(String expr)
        {
            ret.setExpr(expr);
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public FieldExtractor build()
        {
            return ret;
        }
    }
}