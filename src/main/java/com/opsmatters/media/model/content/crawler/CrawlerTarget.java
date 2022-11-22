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
import com.opsmatters.media.model.content.crawler.field.FieldFilter;

/**
 * Class that represents a YAML configuration for a crawler.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class CrawlerTarget implements ConfigElement
{
    private String name = "";
    private CrawlerContent teasers;
    private CrawlerContent articles;
    private FieldMap fields;
    private String sites = "";
    private String provider = "";

//GERALD: remove later
    private ContentLoading teaserLoading;
    private List<Fields> teaserFields = new ArrayList<Fields>();
    private ContentLoading articleLoading; 
    private List<Fields> articleFields = new ArrayList<Fields>();

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
            setName(obj.getName());

//GERALD: remove later
            if(obj.getTeaserLoading() != null)
                setTeaserLoading(new ContentLoading(obj.getTeaserLoading()));
//GERALD: remove later
            if(obj.getArticleLoading() != null)
                setArticleLoading(new ContentLoading(obj.getArticleLoading()));

//GERALD: remove later
            teaserFields.clear();
            for(Fields teaserFields : obj.getTeaserFields())
                addTeaserFields(new Fields(teaserFields));

//GERALD: remove later
            articleFields.clear();
            for(Fields articleFields : obj.getArticleFields())
                addArticleFields(new Fields(articleFields));

            if(obj.hasTeasers())
                setTeasers(new CrawlerContent(obj.getTeasers()));
            if(obj.hasArticles())
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

//GERALD: remove later
    /**
     * Returns the teaser page loading configuration.
     */
    public ContentLoading getTeaserLoading()
    {
        return teaserLoading;
    }

//GERALD: remove later
    /**
     * Sets the teaser page loading configuration.
     */
    public void setTeaserLoading(ContentLoading teaserLoading)
    {
        this.teaserLoading = teaserLoading;
    }

//GERALD: remove later
    /**
     * Returns <CODE>true</CODE> if teaser page loading configuration has been set.
     */
    public boolean hasTeaserLoading()
    {
        return teaserLoading != null;
    }

//GERALD: remove later
    /**
     * Returns the article page loading configuration.
     */
    public ContentLoading getArticleLoading()
    {
        return articleLoading;
    }

//GERALD: remove later
    /**
     * Sets the article page loading configuration.
     */
    public void setArticleLoading(ContentLoading articleLoading)
    {
        this.articleLoading = articleLoading;
    }

//GERALD: remove later
    /**
     * Returns <CODE>true</CODE> if article page loading configuration has been set.
     */
    public boolean hasArticleLoading()
    {
        return articleLoading != null;
    }

//GERALD: remove later
    /**
     * Returns the teaser fields list for this configuration.
     */
    public List<Fields> getTeaserFields()
    {
        return teaserFields;
    }

//GERALD: remove later
    /**
     * Sets the teaser fields list for this configuration.
     */
    public void setTeaserFields(List<Fields> teaserFields)
    {
        this.teaserFields = teaserFields;
    }

//GERALD: remove later
    /**
     * Adds the teaser fields for this configuration.
     */
    public void addTeaserFields(Fields teaserFields)
    {
        this.teaserFields.add(teaserFields);
    }

//GERALD: remove later
    /**
     * Returns <CODE>true</CODE> if the teaser fields list has been set for this configuration.
     */
    public boolean hasTeaserFields()
    {
        return teaserFields != null && teaserFields.size() > 0;
    }

//GERALD: remove later
    /**
     * Returns the article fields list for this configuration.
     */
    public List<Fields> getArticleFields()
    {
        return articleFields;
    }

//GERALD: remove later
    /**
     * Sets the article fields list for this configuration.
     */
    public void setArticleFields(List<Fields> articleFields)
    {
        this.articleFields = articleFields;
    }

//GERALD: remove later
    /**
     * Adds the article fields for this configuration.
     */
    public void addArticleFields(Fields articleFields)
    {
        this.articleFields.add(articleFields);
    }

//GERALD: remove later
    /**
     * Returns <CODE>true</CODE> if the article fields list has been set for this configuration.
     */
    public boolean hasArticleFields()
    {
        return articleFields != null && articleFields.size() > 0;
    }

    /**
     * Returns the filters for this configuration.
     */
    public List<FieldFilter> getFilters()
    {
        List<FieldFilter> ret = new ArrayList<FieldFilter>();
//GERALD: remove later
        if(getArticleFields() != null)
        {
            for(Fields fields : getArticleFields())
            {
                if(fields.getBody() != null)
                {
                    Field body = fields.getBody();
                    if(body.getFilters() != null)
                        ret.addAll(body.getFilters());
                }
            }
        }

        if(getArticles() != null)
        {
            for(Fields fields : getArticles().getFields())
            {
                if(fields.getBody() != null)
                {
                    Field body = fields.getBody();
                    if(body.getFilters() != null)
                        ret.addAll(body.getFilters());
                }
            }
        }

        return ret;
    }

    /**
     * Returns the teasers for this configuration.
     */
    public CrawlerContent getTeasers()
    {
        return teasers;
    }

    /**
     * Sets the teasers for this configuration.
     */
    public void setTeasers(CrawlerContent teasers)
    {
        this.teasers = teasers;
    }

    /**
     * Returns <CODE>true</CODE> if the teasers have been set for this configuration.
     */
    public boolean hasTeasers()
    {
        return teasers != null && teasers.hasFields();
    }

    /**
     * Returns the articles for this configuration.
     */
    public CrawlerContent getArticles()
    {
        return articles;
    }

    /**
     * Sets the articles for this configuration.
     */
    public void setArticles(CrawlerContent articles)
    {
        this.articles = articles;
    }

    /**
     * Returns <CODE>true</CODE> if the articles have been set for this configuration.
     */
    public boolean hasArticles()
    {
        return articles != null && articles.hasFields();
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
        if(this.fields == null)
            this.fields = new FieldMap();
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
//GERALD: remove later
    public static final String TEASER_LOADING = "teaser-loading";
//GERALD: remove later
    public static final String TEASER_FIELDS = "teaser-fields";
//GERALD: remove later
    public static final String ARTICLE_LOADING = "article-loading";
//GERALD: remove later
    public static final String ARTICLE_FIELDS = "article-fields";
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
//GERALD: remove later
        if(map.containsKey(TEASER_LOADING))
        {
            ret.setTeaserLoading(ContentLoading.builder()
                .parse((Map<String,Object>)map.get(TEASER_LOADING)).build());
        }

//GERALD: remove later
        if(map.containsKey(TEASER_FIELDS))
        {
            List<Map<String,Object>> teasers = (List<Map<String,Object>>)map.get(TEASER_FIELDS);
            for(Map<String,Object> teaser : teasers)
                ret.addTeaserFields(Fields.builder().parse(teaser).build());
        }

//GERALD: remove later
        if(map.containsKey(ARTICLE_LOADING))
        {
            ret.setArticleLoading(ContentLoading.builder()
                .parse((Map<String,Object>)map.get(ARTICLE_LOADING)).build());
        }

//GERALD: remove later
        if(map.containsKey(ARTICLE_FIELDS))
        {
            List<Map<String,Object>> articles = (List<Map<String,Object>>)map.get(ARTICLE_FIELDS);
            for(Map<String,Object> article : articles)
                ret.addArticleFields(Fields.builder().parse(article).build());
        }

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