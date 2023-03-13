/*
 * Copyright 2022 Gerald Curley
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
import com.opsmatters.media.model.content.FieldName;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;

/**
 * Class that represents a YAML configuration for crawler content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CrawlerContent implements ConfigElement
{
    private ContentRequest request = new ContentRequest();
    private ContentLoading loading = new ContentLoading();
    private List<Fields> fields = new ArrayList<Fields>();

    /**
     * Default constructor.
     */
    public CrawlerContent()
    {
    }

    /**
     * Copy constructor.
     */
    public CrawlerContent(CrawlerContent obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(CrawlerContent obj)
    {
        if(obj != null)
        {
            if(obj.getRequest() != null)
                setRequest(new ContentRequest(obj.getRequest()));
            if(obj.getLoading() != null)
                setLoading(new ContentLoading(obj.getLoading()));

            fields.clear();
            if(obj.hasFields())
            {
                for(Fields fields : obj.getFields())
                    addFields(new Fields(fields));
            }
        }
    }

    /**
     * Returns the request configuration.
     */
    public ContentRequest getRequest()
    {
        return request;
    }

    /**
     * Sets the request configuration.
     */
    public void setRequest(ContentRequest request)
    {
        this.request = request;
    }

    /**
     * Returns the loading configuration.
     */
    public ContentLoading getLoading()
    {
        return loading;
    }

    /**
     * Sets the loading configuration.
     */
    public void setLoading(ContentLoading loading)
    {
        this.loading = loading;
    }

    /**
     * Returns the fields list for this configuration.
     */
    public List<Fields> getFields()
    {
        return fields;
    }

    /**
     * Sets the fields list for this configuration.
     */
    public void setFields(List<Fields> fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Fields fields)
    {
        this.fields.add(fields);
    }

    /**
     * Returns <CODE>true</CODE> if the fields list has been set for this configuration.
     */
    public boolean hasFields()
    {
        return fields != null && fields.size() > 0;
    }

    /**
     * Returns the fields, adjusting the root if there was an error.
     */
    public List<Fields> getFields(boolean error)
    {
        List<Fields> ret = new ArrayList<Fields>();
        for(Fields f : getFields())
        {
            Fields fields = new Fields(f);
            if(error)
                fields.setRoot("body");
            ret.add(fields);
        }

        return ret;
    }

    /**
     * Returns the first field with the given name.
     */
    public Field getField(FieldName name)
    {
        Field ret = null;
        if(hasFields())
        {
            for(Fields fields : getFields())
            {
                ret = fields.getField(name);
                if(ret != null)
                    break;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given field should have a trailing slash.
     */
    public boolean hasTrailingSlash(FieldName name)
    {
        Field field = getField(name);
        return field != null ? field.hasTrailingSlash() : false;
    }

    /**
     * Returns the filters for this configuration.
     */
    public List<FieldFilter> getFilters()
    {
        List<FieldFilter> ret = new ArrayList<FieldFilter>();
        for(Fields fields : getFields())
        {
            if(fields.getBody() != null)
            {
                Field body = fields.getBody();
                if(body.getFilters() != null)
                    ret.addAll(body.getFilters());
            }
        }

        return ret;
    }

    /**
     * Returns the first url for this configuration.
     */
    public String getUrl()
    {
        return getRequest().getUrl(0);
    }

    /**
     * Returns the urls for this configuration.
     */
    public List<String> getUrls()
    {
        return getRequest().getUrls();
    }

    /**
     * Returns the number of urls for this configuration.
     */
    public int numUrls()
    {
        return getRequest().numUrls();
    }

    /**
     * Returns <CODE>true</CODE> if parameters should be removed from the URL.
     */
    public boolean removeParameters()
    {
        return getRequest().removeParameters();
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
    public static class Builder implements ConfigParser<CrawlerContent>
    {
        // The config attribute names
        private static final String REQUEST = "request";
        private static final String LOADING = "loading";
        private static final String FIELDS = "fields";

        private CrawlerContent ret = new CrawlerContent();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(REQUEST))
            {
                ret.setRequest(ContentRequest.builder()
                    .parse((Map<String,Object>)map.get(REQUEST)).build());
            }

            if(map.containsKey(LOADING))
            {
                ret.setLoading(ContentLoading.builder()
                    .parse((Map<String,Object>)map.get(LOADING)).build());
            }

            if(map.containsKey(FIELDS))
            {
                List<Map<String,Object>> fields = (List<Map<String,Object>>)map.get(FIELDS);
                for(Map<String,Object> field : fields)
                    ret.addFields(Fields.builder().parse(field).build());
            }

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public CrawlerContent build()
        {
            return ret;
        }
    }
}