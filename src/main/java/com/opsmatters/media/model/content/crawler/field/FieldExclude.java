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
package com.opsmatters.media.model.content.crawler.field;

import java.util.Map;

import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class representing a field exclude.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldExclude implements ConfigElement
{
    private String expr = null;
    private String id = null;
    private String tag = null;
    private String className = null;

    /**
     * Default constructor.
     */
    public FieldExclude()
    {
    }

    /**
     * Copy constructor.
     */
    public FieldExclude(FieldExclude obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an expression.
     */
    public FieldExclude(String expr)
    {
        setExpr(expr);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldExclude obj)
    {
        if(obj != null)
        {
            setExpr(obj.getExpr());
        }
    }

    /**
     * Returns the expression for this exclude.
     */
    public String getExpr()
    {
        return expr;
    }

    /**
     * Sets the expression for this exclude.
     */
    public void setExpr(String expr)
    {
        this.expr = expr;
        parseExpr();
    }

    /**
     * Returns <CODE>true</CODE> if the expression has been set.
     */
    public boolean hasExpr()
    {
        return expr != null && expr.length() > 0;
    }

    /**
     * Parse the expression to extract the id, tag and class name.
     */
    private void parseExpr()
    {
        id = "";
        tag = "";
        className = "";

        if(expr != null && expr.length() > 0)
        {
            tag = expr;
            className = "";
            id = "";

            // Look for the class name
            int pos = expr.indexOf(".");
            if(pos != -1)
            {
                tag = expr.substring(0, pos);
                className = expr.substring(pos+1);
            }
            else // Look for the id
            {
                pos = expr.indexOf("#");
                if(pos != -1)
                {
                    tag = expr.substring(0, pos);
                    id = expr.substring(pos+1);
                }
            }
        }
    }

    /**
     * Returns the id for this exclude.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the tag for this exclude.
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Returns the class name for this exclude.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Returns <CODE>true</CODE> if the given node should be excluded.
     */
    public static boolean apply(List<FieldExclude> excludes, Node node)
    {
        boolean ret = false;
        if(node instanceof Element && excludes != null)
        {
            Element element = (Element)node;
            for(FieldExclude exclude : excludes)
            {
                String tag = exclude.getTag();
                String className = exclude.getClassName();
                String id = exclude.getId();

                ret = (tag.length() == 0 || tag.equals(element.tagName()))
                    && (className.length() == 0 || element.hasClass(className))
                    && (id.length() == 0 || id.equals(element.id()));
                if(ret)
                    break;
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
    public static class Builder implements ConfigParser<FieldExclude>
    {
        // The config attribute names
        private static final String EXPR = "expr";

        private FieldExclude ret = new FieldExclude();

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

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public FieldExclude build()
        {
            return ret;
        }
    }
}