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
package com.opsmatters.media.config.content;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.content.ContentFields;

/**
 * Class that represents a YAML configuration for a web page.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class WebPageConfiguration extends YamlConfiguration
{
    public static final String URL = "url";
    public static final String BASE_PATH = "base-path";
    public static final String MORE_LINK = "more-link";
    public static final String TEASER_LOADING = "teaser-loading";
    public static final String TEASER_FIELDS = "teaser-fields";
    public static final String CONTENT_LOADING = "content-loading";
    public static final String CONTENT_FIELDS = "content-fields";

    private String url = "";
    private String basePath = "";
    private MoreLinkConfiguration moreLink;
    private LoadingConfiguration teaserLoading; 
    private List<ContentFields> teaserFields = new ArrayList<ContentFields>();
    private LoadingConfiguration contentLoading; 
    private ContentFields contentFields;

    /**
     * Default constructor.
     */
    public WebPageConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public WebPageConfiguration(WebPageConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(WebPageConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUrl(obj.getUrl());
            setBasePath(obj.getBasePath());
            if(obj.getMoreLink() != null)
                setMoreLink(new MoreLinkConfiguration(obj.getMoreLink()));
            if(obj.getTeaserLoading() != null)
                setTeaserLoading(new LoadingConfiguration(obj.getTeaserLoading()));
            if(obj.getContentLoading() != null)
                setContentLoading(new LoadingConfiguration(obj.getContentLoading()));
            for(ContentFields teaserFields : obj.getTeaserFields())
                addTeaserFields(teaserFields);
            setContentFields(obj.getContentFields());
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
     * Returns the url for this configuration.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url for this configuration.
     */
    public void setUrl(String url)
    {
        this.url = url;
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
     * Returns the link to get more items.
     */
    public MoreLinkConfiguration getMoreLink()
    {
        return moreLink;
    }

    /**
     * Sets the link to get more items.
     */
    public void setMoreLink(MoreLinkConfiguration moreLink)
    {
        this.moreLink = moreLink;
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
     * Returns the content page loading configuration.
     */
    public LoadingConfiguration getContentLoading()
    {
        return contentLoading;
    }

    /**
     * Sets the content page loading configuration.
     */
    public void setContentLoading(LoadingConfiguration contentLoading)
    {
        this.contentLoading = contentLoading;
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
     * Returns the content fields for this configuration.
     */
    public ContentFields getContentFields()
    {
        return contentFields;
    }

    /**
     * Sets the content fields for this configuration.
     */
    public void setContentFields(ContentFields contentFields)
    {
        this.contentFields = contentFields;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            if(map.containsKey(URL))
                setUrl((String)map.get(URL));
            if(map.containsKey(BASE_PATH))
                setBasePath((String)map.get(BASE_PATH));
            if(map.containsKey(MORE_LINK))
                setMoreLink(createMoreLink(MORE_LINK, map.get(MORE_LINK)));

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

            if(map.containsKey(CONTENT_LOADING))
            {
                contentLoading = new LoadingConfiguration(getName());
                contentLoading.parseDocument((Map<String,Object>)map.get(CONTENT_LOADING));
            }

            if(map.containsKey(CONTENT_FIELDS))
            {
                setContentFields(new ContentFields((Map<String,Object>)map.get(CONTENT_FIELDS)));
            }
        }
    }

    /**
     * Create a More link object for the given value.
     */
    public MoreLinkConfiguration createMoreLink(String name, Object value)
    {
        MoreLinkConfiguration ret = null;
        if(value instanceof String)
            ret = new MoreLinkConfiguration(name, (String)value);
        else if(value instanceof Map)
            ret = new MoreLinkConfiguration(name, (Map<String,Object>)value);
        return ret;
    }
}