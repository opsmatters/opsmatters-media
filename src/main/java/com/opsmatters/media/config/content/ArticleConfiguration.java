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
package com.opsmatters.media.config.content;

//GERALD: check
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.config.YamlConfiguration;
//import com.opsmatters.media.config.content.FieldFilter;

/**
 * Class that represents a YAML configuration for an article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ArticleConfiguration extends YamlConfiguration
{
    public static final String LOADING = "loading";
    public static final String FIELDS = "fields";

    private LoadingConfiguration loading;
    private List<ContentFields> fields = new ArrayList<ContentFields>();

    /**
     * Default constructor.
     */
    public ArticleConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public ArticleConfiguration(ArticleConfiguration obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ArticleConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);

            if(obj.getLoading() != null)
                setLoading(new LoadingConfiguration(obj.getLoading()));

            fields.clear();
            if(obj.hasFields())
            {
                for(ContentFields fields : obj.getFields())
                    addFields(new ContentFields(fields));
            }
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
     * Returns the loading configuration.
     */
    public LoadingConfiguration getLoading()
    {
        return loading;
    }

    /**
     * Sets the loading configuration.
     */
    public void setLoading(LoadingConfiguration loading)
    {
        if(this.loading == null)
            this.loading = new LoadingConfiguration(getName());
        this.loading = loading;
    }

    /**
     * Returns the fields list for this configuration.
     */
    public List<ContentFields> getFields()
    {
        return fields;
    }

    /**
     * Sets the fields list for this configuration.
     */
    public void setFields(List<ContentFields> fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(ContentFields fields)
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
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(LOADING))
        {
            loading = new LoadingConfiguration(getName());
            loading.parseDocument((Map<String,Object>)map.get(LOADING));
        }

        if(map.containsKey(FIELDS))
        {
            List<Map<String,Object>> fields = (List<Map<String,Object>>)map.get(FIELDS);
            for(Map<String,Object> field : fields)
                addFields(new ContentFields(field));
        }
    }
}