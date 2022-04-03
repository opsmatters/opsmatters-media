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
package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import com.opsmatters.media.config.content.FilterResult;

/**
 * Class representing a field filter.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldFilter
{
    public static final String EXPR = "expr";
    public static final String SCOPE = "scope";
    public static final String STOP = "stop";

    private String expr = null;
    private Pattern pattern;
    private FilterScope scope = FilterScope.ALL;
    private boolean stop = false;

    /**
     * Default constructor.
     */
    public FieldFilter()
    {
    }

    /**
     * Copy constructor.
     */
    public FieldFilter(FieldFilter obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an expression.
     */
    public FieldFilter(String expr)
    {
        setExpr(expr);
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public FieldFilter(Map<String, Object> map)
    {
        parse(map);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldFilter obj)
    {
        if(obj != null)
        {
            setExpr(obj.getExpr());
            setScope(obj.getScope());
            setStop(obj.isStop());
        }
    }

    /**
     * Returns the expression for this configuration.
     */
    public String toString()
    {
        return getExpr();
    }

    /**
     * Returns the regular expression for this filter.
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
        return pattern;
    }

    /**
     * Sets the regular expression for this filter.
     */
    public void setExpr(String expr)
    {
        this.expr = expr;
        this.pattern = hasExpr() ? Pattern.compile(expr, Pattern.DOTALL) : null;
    }

    /**
     * Returns <CODE>true</CODE> if the regular expression has been set.
     */
    public boolean hasExpr()
    {
        return expr != null && expr.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the given text matches the regular expression.
     */
    public boolean matches(String str)
    {
        return pattern != null && pattern.matcher(str).matches();
    }

    /**
     * Returns the scope for this filter.
     */
    public FilterScope getScope()
    {
        return scope;
    }

    /**
     * Sets the scope for this filter.
     */
    public void setScope(FilterScope scope)
    {
        this.scope = scope;
    }

    /**
     * Sets the scope for this filter.
     */
    public void setScope(String scope)
    {
        setScope(FilterScope.valueOf(scope));
    }

    /**
     * Returns <CODE>true</CODE> if the scope has been set.
     */
    public boolean hasScope()
    {
        return scope != null;
    }

    /**
     * Returns <CODE>true</CODE> if the given scope is within the scope of this filter.
     */
    public boolean applies(FilterScope scope)
    {
        boolean ret = false;
        if(this.scope == FilterScope.ALL)
            ret = true;
        else
            ret = this.scope == scope;
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the filter should stop on a match.
     */
    public boolean isStop()
    {
        return stop;
    }

    /**
     * Set to <CODE>true</CODE> if the filter should stop on a match.
     */
    public void setStop(boolean stop)
    {
        this.stop = stop;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(EXPR))
            setExpr((String)map.get(EXPR));
        if(map.containsKey(SCOPE))
            setScope((String)map.get(SCOPE));
        if(map.containsKey(STOP))
            setStop((Boolean)map.get(STOP));
    }

    /**
     * Returns the result of applying the filters to the given string.
     */
    public static FilterResult apply(List<FieldFilter> filters, String str, FilterScope scope)
    {
        FilterResult ret = FilterResult.NONE;
        if(filters != null)
        {
            for(FieldFilter filter : filters)
            {
                if(filter.applies(scope) && filter.hasExpr())
                {
                    if(filter.matches(str) && ret != FilterResult.STOP)
                        ret = filter.isStop() ? FilterResult.STOP : FilterResult.SKIP;
                }
            }
        }

        return ret;
    }
}