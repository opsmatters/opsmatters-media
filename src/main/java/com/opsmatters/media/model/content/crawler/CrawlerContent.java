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
import com.opsmatters.media.model.content.crawler.field.Fields;

/**
 * Class that represents a YAML configuration for crawler content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CrawlerContent implements ConfigElement
{
    private ContentLoading loading;
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