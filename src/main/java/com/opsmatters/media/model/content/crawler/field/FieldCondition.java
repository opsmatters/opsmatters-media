/*
 * Copyright 2024 Gerald Curley
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
import java.util.regex.Pattern;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

import static com.opsmatters.media.model.content.crawler.field.ConditionAction.*;

/**
 * Class representing a field condition.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldCondition implements ConfigElement
{
    private String expr = null;
    private Pattern pattern;
    private ConditionAction action = ConditionAction.ACCEPT;

    /**
     * Default constructor.
     */
    public FieldCondition()
    {
    }

    /**
     * Constructor that takes an expression.
     */
    public FieldCondition(String expr)
    {
        setExpr(expr);
    }

    /**
     * Copy constructor.
     */
    public FieldCondition(FieldCondition obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldCondition obj)
    {
        if(obj != null)
        {
            setExpr(obj.getExpr());
            setAction(obj.getAction());
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
     * Returns the regular expression for this condition.
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
     * Sets the regular expression for this condition.
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
     * Returns the action for this condition.
     */
    public ConditionAction getAction()
    {
        return action;
    }

    /**
     * Sets the action for this condition.
     */
    public void setAction(ConditionAction action)
    {
        this.action = action;
    }

    /**
     * Sets the action for this condition.
     */
    public void setAction(String action)
    {
        setAction(ConditionAction.valueOf(action));
    }

    /**
     * Returns <CODE>true</CODE> if the action has been set.
     */
    public boolean hasAction()
    {
        return action != null;
    }

    /**
     * Returns <CODE>true</CODE> if the conditions match the given string.
     */
    public static boolean accept(List<FieldCondition> conditions, String str)
    {
        boolean ret = false;

        if(conditions != null)
        {
            for(FieldCondition condition : conditions)
            {
                if(condition.hasExpr())
                {
                    boolean matches = condition.matches(str);
                    if(matches)
                    {
                        if(condition.getAction() == ACCEPT)
                        {
                            ret = true;
                            break;
                        }
                        else if(condition.getAction() == REJECT)
                        {
                            ret = false;
                            break;
                        }
                    }
                }
            }
        }

        return ret;
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
    public static class Builder implements ConfigParser<FieldCondition>
    {
        // The config attribute names
        private static final String EXPR = "expr";
        private static final String ACTION = "action";

        private FieldCondition ret = new FieldCondition();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(EXPR))
                ret.setExpr((String)map.get(EXPR));
            if(map.containsKey(ACTION))
                ret.setAction((String)map.get(ACTION));

            return this;
        }

        /**
         * Sets the expression of the condition.
         * @param expr The expression of the condition
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
        public FieldCondition build()
        {
            return ret;
        }
    }
}