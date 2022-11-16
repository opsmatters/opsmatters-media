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

//GERALD: check
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.config.content.FieldFilter;

/**
 * Class that represents a YAML configuration for a page with fields.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class FieldsConfiguration extends YamlConfiguration
{
//GERALD: remove later
    public static final String TEASER_LOADING = "teaser-loading";
//GERALD: remove later
    public static final String TEASER_FIELDS = "teaser-fields";
//GERALD: remove later
    public static final String ARTICLE_LOADING = "article-loading";
//GERALD: remove later
    public static final String ARTICLE_FIELDS = "article-fields";
//GERALD
    public static final String TEASERS = "teasers";
//GERALD
    public static final String ARTICLES = "articles";
    public static final String FIELDS = "fields";
    public static final String SITES = "sites";
    public static final String PROVIDER = "provider";

//GERALD: remove later
    private LoadingConfiguration teaserLoading;
//GERALD: remove later
    private List<ContentFields> teaserFields = new ArrayList<ContentFields>();
//GERALD: remove later
    private LoadingConfiguration articleLoading; 
//GERALD: remove later
    private List<ContentFields> articleFields = new ArrayList<ContentFields>();
//GERALD
    private ArticleConfiguration teasers;
//GERALD
    private ArticleConfiguration articles;
    private Fields fields;
    private String sites = "";
    private String provider = "";

    /**
     * Default constructor.
     */
    public FieldsConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public FieldsConfiguration(FieldsConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(FieldsConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);

//GERALD: remove later
            if(obj.getTeaserLoading() != null)
                setTeaserLoading(new LoadingConfiguration(obj.getTeaserLoading()));
//GERALD: remove later
            if(obj.getArticleLoading() != null)
                setArticleLoading(new LoadingConfiguration(obj.getArticleLoading()));

//GERALD: remove later
            teaserFields.clear();
            for(ContentFields teaserFields : obj.getTeaserFields())
                addTeaserFields(new ContentFields(teaserFields));

//GERALD: remove later
            articleFields.clear();
            for(ContentFields articleFields : obj.getArticleFields())
                addArticleFields(new ContentFields(articleFields));

//GERALD
            if(obj.hasTeasers())
                setTeasers(new ArticleConfiguration(obj.getTeasers()));
            if(obj.hasArticles())
                setArticles(new ArticleConfiguration(obj.getArticles()));

            setFields(new Fields(obj.getFields()));
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

//GERALD: remove later
    /**
     * Returns the teaser page loading configuration.
     */
    public LoadingConfiguration getTeaserLoading()
    {
        return teaserLoading;
    }

//GERALD: remove later
    /**
     * Sets the teaser page loading configuration.
     */
    public void setTeaserLoading(LoadingConfiguration teaserLoading)
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
    public LoadingConfiguration getArticleLoading()
    {
        return articleLoading;
    }

//GERALD: remove later
    /**
     * Sets the article page loading configuration.
     */
    public void setArticleLoading(LoadingConfiguration articleLoading)
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
    public List<ContentFields> getTeaserFields()
    {
        return teaserFields;
    }

//GERALD: remove later
    /**
     * Sets the teaser fields list for this configuration.
     */
    public void setTeaserFields(List<ContentFields> teaserFields)
    {
        this.teaserFields = teaserFields;
    }

//GERALD: remove later
    /**
     * Adds the teaser fields for this configuration.
     */
    public void addTeaserFields(ContentFields teaserFields)
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
    public List<ContentFields> getArticleFields()
    {
        return articleFields;
    }

//GERALD: remove later
    /**
     * Sets the article fields list for this configuration.
     */
    public void setArticleFields(List<ContentFields> articleFields)
    {
        this.articleFields = articleFields;
    }

//GERALD: remove later
    /**
     * Adds the article fields for this configuration.
     */
    public void addArticleFields(ContentFields articleFields)
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

//GERALD: fix or move?
    /**
     * Returns the filters for this configuration.
     */
    public List<FieldFilter> getFilters()
    {
        List<FieldFilter> ret = new ArrayList<FieldFilter>();
//GERALD: remove later
        if(getArticleFields() != null)
        {
            for(ContentFields fields : getArticleFields())
            {
                if(fields.getBody() != null)
                {
                    ContentField body = fields.getBody();
                    if(body.getFilters() != null)
                        ret.addAll(body.getFilters());
                }
            }
        }

//GERALD
        if(getArticles() != null)
        {
            for(ContentFields fields : getArticles().getFields())
            {
                if(fields.getBody() != null)
                {
                    ContentField body = fields.getBody();
                    if(body.getFilters() != null)
                        ret.addAll(body.getFilters());
                }
            }
        }

        return ret;
    }

//GERALD
    /**
     * Returns the teasers for this configuration.
     */
    public ArticleConfiguration getTeasers()
    {
        return teasers;
    }

//GERALD
    /**
     * Sets the teasers for this configuration.
     */
    public void setTeasers(ArticleConfiguration teasers)
    {
//GERALD
//        if(this.teasers == null)
//            this.teasers = new ArticleConfiguration(getName());
        this.teasers = teasers;
    }

//GERALD
    /**
     * Returns <CODE>true</CODE> if the teasers have been set for this configuration.
     */
    public boolean hasTeasers()
    {
        return teasers != null && teasers.hasFields();
    }

//GERALD
    /**
     * Returns the articles for this configuration.
     */
    public ArticleConfiguration getArticles()
    {
        return articles;
    }

//GERALD
    /**
     * Sets the articles for this configuration.
     */
    public void setArticles(ArticleConfiguration articles)
    {
//GERALD
//        if(this.articles == null)
//            this.articles = new ArticleConfiguration(getName());
        this.articles = articles;
    }

//GERALD
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
    public Fields getFields()
    {
        return fields;
    }

    /**
     * Sets the fields for this configuration.
     */
    public void setFields(Fields fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Map<String,String> fields)
    {
        if(this.fields == null)
            this.fields = new Fields();
        this.fields.putAll(fields);
    }

    /**
     * Returns <CODE>true</CODE> if given field has been set.
     */
    public boolean hasField(String name)
    {
        return fields != null ? fields.containsKey(name) : false;
    }

    /**
     * Returns the value of the given field.
     */
    public String getField(String name)
    {
        return fields != null ? fields.get(name) : null;
    }

    /**
     * Returns the value of the given field.
     * <p>
     * Returns the fallback if the field is not found.
     */
    public String getField(String name, String fallback)
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
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
//GERALD: remove later
        if(map.containsKey(TEASER_LOADING))
        {
            teaserLoading = new LoadingConfiguration(getName());
            teaserLoading.parseDocument((Map<String,Object>)map.get(TEASER_LOADING));
        }

//GERALD: remove later
        if(map.containsKey(TEASER_FIELDS))
        {
            List<Map<String,Object>> teasers = (List<Map<String,Object>>)map.get(TEASER_FIELDS);
            for(Map<String,Object> teaser : teasers)
                addTeaserFields(new ContentFields(teaser));
        }

//GERALD: remove later
        if(map.containsKey(ARTICLE_LOADING))
        {
            articleLoading = new LoadingConfiguration(getName());
            articleLoading.parseDocument((Map<String,Object>)map.get(ARTICLE_LOADING));
        }

//GERALD: remove later
        if(map.containsKey(ARTICLE_FIELDS))
        {
            List<Map<String,Object>> articles = (List<Map<String,Object>>)map.get(ARTICLE_FIELDS);
            for(Map<String,Object> article : articles)
                addArticleFields(new ContentFields(article));
        }

//GERALD
        if(map.containsKey(TEASERS))
        {
            teasers = new ArticleConfiguration(getName());
            teasers.parseDocument((Map<String,Object>)map.get(TEASERS));
        }

//GERALD
        if(map.containsKey(ARTICLES))
        {
            articles = new ArticleConfiguration(getName());
            articles.parseDocument((Map<String,Object>)map.get(ARTICLES));
        }

        if(map.containsKey(FIELDS))
        {
            addFields((Map<String,String>)map.get(FIELDS));
        }

        if(map.containsKey(SITES))
        {
            setSites((String)map.get(SITES));
        }

        if(map.containsKey(PROVIDER))
        {
            setProvider((String)map.get(PROVIDER));
        }
    }
}