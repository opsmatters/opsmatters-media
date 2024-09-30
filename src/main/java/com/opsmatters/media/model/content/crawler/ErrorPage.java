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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents an error page for a crawler.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ErrorPage implements ConfigElement
{
    private String title = "";
    private ErrorPageType type;

    /**
     * Constructor that takes a type.
     */
    public ErrorPage(String type)
    {
        setType(type);
    }

    /**
     * Copy constructor.
     */
    public ErrorPage(ErrorPage obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ErrorPage obj)
    {
        if(obj != null)
        {
            setTitle(obj.getTitle());
            setType(obj.getType());
        }
    }

    /**
     * Returns the title of the error page.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the title of the error page.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title of the error page.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the type of the error page.
     */
    public ErrorPageType getType()
    {
        return type;
    }

    /**
     * Sets the type of the error page.
     */
    public void setType(ErrorPageType type)
    {
        this.type = type;
    }

    /**
     * Sets the type of the error page.
     */
    public void setType(String type)
    {
        setType(ErrorPageType.valueOf(type));
    }

    /**
     * Returns a builder for the error page.
     * @param type The type of the error page
     * @return The builder instance.
     */
    public static Builder builder(String type)
    {
        return new Builder(type);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<ErrorPage>
    {
        // The config attribute names
        private static final String TITLE = "title";

        private ErrorPage ret = null;

        /**
         * Constructor that takes a type.
         * @param type The type for the error page
         */
        public Builder(String type)
        {
            ret = new ErrorPage(type);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(TITLE))
                ret.setTitle((String)map.get(TITLE));

            return this;
        }

        /**
         * Returns the configured error page instance
         * @return The error page instance
         */
        public ErrorPage build()
        {
            return ret;
        }
    }
}