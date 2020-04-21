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
import com.opsmatters.media.config.monitor.MonitorConfiguration;

/**
 * Class that represents a YAML configuration for a page with fields.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class FieldsConfiguration extends YamlConfiguration
{
    public static final String TEASER_LOADING = "teaser-loading";
    public static final String TEASER_MONITOR = "teaser-monitor";
    public static final String TEASER_FIELDS = "teaser-fields";
    public static final String CONTENT_LOADING = "content-loading";
    public static final String CONTENT_FIELDS = "content-fields";

    private LoadingConfiguration teaserLoading;
    private MonitorConfiguration teaserMonitor; 
    private List<ContentFields> teaserFields = new ArrayList<ContentFields>();
    private LoadingConfiguration contentLoading; 
    private ContentFields contentFields;

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
            if(obj.getTeaserMonitor() != null)
                setTeaserMonitor(new MonitorConfiguration(obj.getTeaserMonitor()));
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
     * Returns the teaser page monitor configuration.
     */
    public MonitorConfiguration getTeaserMonitor()
    {
        return teaserMonitor;
    }

    /**
     * Sets the teaser page monitor configuration.
     */
    public void setTeaserMonitor(MonitorConfiguration teaserMonitor)
    {
        this.teaserMonitor = teaserMonitor;
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
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(TEASER_LOADING))
        {
            teaserLoading = new LoadingConfiguration(getName());
            teaserLoading.parseDocument((Map<String,Object>)map.get(TEASER_LOADING));
        }

        if(map.containsKey(TEASER_MONITOR))
        {
            teaserMonitor = new MonitorConfiguration(getName());
            teaserMonitor.parseDocument((Map<String,Object>)map.get(TEASER_MONITOR));
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