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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.FieldName;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;

/**
 * Class that represents a YAML configuration for a crawler.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class CrawlerTarget implements ConfigElement
{
    private String name = "";
    private CrawlerContent teasers = new CrawlerContent();
    private CrawlerContent articles = new CrawlerContent();
    private FieldMap fields = new FieldMap();
    private String sites = "";
    private String provider = "";

    /**
     * Constructor that takes a name.
     */
    public CrawlerTarget(String name)
    {
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public CrawlerTarget(CrawlerTarget obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(CrawlerTarget obj)
    {
        if(obj != null)
        {
            setTeasers(new CrawlerContent(obj.getTeasers()));
            setArticles(new CrawlerContent(obj.getArticles()));
            setFields(new FieldMap(obj.getFields()));
            setSites(obj.getSites());
            setProvider(obj.getProvider());
        }
    }

    /**
     * Returns the name of this configuration.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of this configuration.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this configuration.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the teasers for this configuration.
     */
    public CrawlerContent getTeasers()
    {
        return teasers;
    }

    /**
     * Returns <CODE>true</CODE> if the teasers have been configured for this configuration.
     */
    public boolean hasTeasers()
    {
        return teasers != null;
    }

    /**
     * Sets the teasers for this configuration.
     */
    public void setTeasers(CrawlerContent teasers)
    {
        this.teasers = teasers;
    }

    /**
     * Returns the articles for this configuration.
     */
    public CrawlerContent getArticles()
    {
        return articles;
    }

    /**
     * Returns <CODE>true</CODE> if the articles have been configured for this configuration.
     */
    public boolean hasArticles()
    {
        return articles != null;
    }

    /**
     * Sets the articles for this configuration.
     */
    public void setArticles(CrawlerContent articles)
    {
        this.articles = articles;
    }

    /**
     * Returns the fields for this configuration.
     */
    public FieldMap getFields()
    {
        return fields;
    }

    /**
     * Sets the fields for this configuration.
     */
    public void setFields(FieldMap fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Map<String,String> fields)
    {
        this.fields.putAll(fields);
    }

    /**
     * Returns <CODE>true</CODE> if given field has been set.
     */
    public boolean hasField(FieldName name)
    {
        return fields != null ? fields.containsKey(name) : false;
    }

    /**
     * Returns the value of the given field.
     */
    public String getField(FieldName name)
    {
        return fields != null ? fields.get(name) : null;
    }

    /**
     * Returns the value of the given field.
     * <p>
     * Returns the fallback if the field is not found.
     */
    public String getField(FieldName name, String fallback)
    {
        return fields != null ? fields.get(name, fallback) : null;
    }

    /**
     * Returns the configuration sites.
     */
    public String getSites()
    {
        return sites;
    }

    /**
     * Sets the configuration sites.
     */
    public void setSites(String sites)
    {
        this.sites = sites;
    }

    /**
     * Returns the configuration provider.
     */
    public String getProvider()
    {
        return provider;
    }

    /**
     * Sets the configuration provider.
     */
    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    /**
     * Builder to make configuration construction easier.
     */
    protected abstract static class Builder<T extends CrawlerTarget, B extends Builder<T,B>>
        implements ConfigParser<CrawlerTarget>
    {
        // The config attribute names
        private static final String TEASERS = "teasers";
        private static final String ARTICLES = "articles";
        private static final String FIELDS = "fields";
        private static final String SITES = "sites";
        private static final String PROVIDER = "provider";

        private CrawlerTarget ret = null;

        /**
         * Sets the configuration.
         * @param config The configuration
         */
        public void set(CrawlerTarget config)
        {
            ret = config;
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public B parse(Map<String, Object> map)
        {
            if(map.containsKey(TEASERS))
            {
                ret.setTeasers(CrawlerContent.builder()
                    .parse((Map<String,Object>)map.get(TEASERS)).build());
            }

            if(map.containsKey(ARTICLES))
            {
                ret.setArticles(CrawlerContent.builder()
                    .parse((Map<String,Object>)map.get(ARTICLES)).build());
            }

            if(map.containsKey(FIELDS))
            {
                ret.addFields((Map<String,String>)map.get(FIELDS));
            }

            if(map.containsKey(SITES))
            {
                ret.setSites((String)map.get(SITES));
            }

            if(map.containsKey(PROVIDER))
            {
                ret.setProvider((String)map.get(PROVIDER));
            }

            return self();
        }

        /**
         * Returns this object.
         * @return This object
         */
        protected abstract B self();

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public abstract T build();
    }
}