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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.config.YamlConfiguration;

/**
 * Class that represents a YAML configuration for a page with fields.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class FieldsConfiguration extends YamlConfiguration
{
    public static final String TEASER_LOADING = "teaser-loading";
    public static final String TEASER_FIELDS = "teaser-fields";
    public static final String ARTICLE_LOADING = "article-loading";
    public static final String ARTICLE_FIELDS = "article-fields";
    public static final String FIELDS = "fields";

    private LoadingConfiguration teaserLoading;
    private List<ContentFields> teaserFields = new ArrayList<ContentFields>();
    private LoadingConfiguration articleLoading; 
    private List<ContentFields> articleFields = new ArrayList<ContentFields>();
    private Fields fields;

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
            if(obj.getTeaserLoading() != null)
                setTeaserLoading(new LoadingConfiguration(obj.getTeaserLoading()));
            if(obj.getArticleLoading() != null)
                setArticleLoading(new LoadingConfiguration(obj.getArticleLoading()));
            for(ContentFields teaserFields : obj.getTeaserFields())
                addTeaserFields(teaserFields);
            for(ContentFields articleFields : obj.getArticleFields())
                addArticleFields(articleFields);
            setFields(new Fields(obj.getFields()));
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
     * Returns the teaser page loading configuration.
     */
    public LoadingConfiguration getTeaserLoading()
    {
        return teaserLoading;
    }

    /**
     * Sets the teaser page loading configuration.
     */
    public void setTeaserLoading(LoadingConfiguration teaserLoading)
    {
        this.teaserLoading = teaserLoading;
    }

    /**
     * Returns the article page loading configuration.
     */
    public LoadingConfiguration getArticleLoading()
    {
        return articleLoading;
    }

    /**
     * Sets the article page loading configuration.
     */
    public void setArticleLoading(LoadingConfiguration articleLoading)
    {
        this.articleLoading = articleLoading;
    }

    /**
     * Returns the teaser fields list for this configuration.
     */
    public List<ContentFields> getTeaserFields()
    {
        return teaserFields;
    }

    /**
     * Sets the teaser fields list for this configuration.
     */
    public void setTeaserFields(List<ContentFields> teaserFields)
    {
        this.teaserFields = teaserFields;
    }

    /**
     * Adds the teaser fields for this configuration.
     */
    public void addTeaserFields(ContentFields teaserFields)
    {
        this.teaserFields.add(teaserFields);
    }

    /**
     * Returns <CODE>true</CODE> if the teaser fields list has been set for this configuration.
     */
    public boolean hasTeaserFields()
    {
        return teaserFields != null && teaserFields.size() > 0;
    }

    /**
     * Returns the article fields list for this configuration.
     */
    public List<ContentFields> getArticleFields()
    {
        return articleFields;
    }

    /**
     * Sets the article fields list for this configuration.
     */
    public void setArticleFields(List<ContentFields> articleFields)
    {
        this.articleFields = articleFields;
    }

    /**
     * Adds the article fields for this configuration.
     */
    public void addArticleFields(ContentFields articleFields)
    {
        this.articleFields.add(articleFields);
    }

    /**
     * Returns <CODE>true</CODE> if the article fields list has been set for this configuration.
     */
    public boolean hasArticleFields()
    {
        return articleFields != null && articleFields.size() > 0;
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
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(TEASER_LOADING))
        {
            teaserLoading = new LoadingConfiguration(getName());
            teaserLoading.parseDocument((Map<String,Object>)map.get(TEASER_LOADING));
        }

        if(map.containsKey(TEASER_FIELDS))
        {
            List<Map<String,Object>> teasers = (List<Map<String,Object>>)map.get(TEASER_FIELDS);
            for(Map<String,Object> teaser : teasers)
                addTeaserFields(new ContentFields(teaser));
        }

        if(map.containsKey(ARTICLE_LOADING))
        {
            articleLoading = new LoadingConfiguration(getName());
            articleLoading.parseDocument((Map<String,Object>)map.get(ARTICLE_LOADING));
        }

        if(map.containsKey(ARTICLE_FIELDS))
        {
            List<Map<String,Object>> articles = (List<Map<String,Object>>)map.get(ARTICLE_FIELDS);
            for(Map<String,Object> article : articles)
                addArticleFields(new ContentFields(article));
        }

        if(map.containsKey(FIELDS))
        {
            addFields((Map<String,String>)map.get(FIELDS));
        }
    }
}