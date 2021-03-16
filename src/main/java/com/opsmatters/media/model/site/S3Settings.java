/*
 * Copyright 2021 Gerald Curley
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

package com.opsmatters.media.model.site;

import java.util.Map;

/**
 * Represents the S3 settings.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class S3Settings
{
    public static final String CONFIG = "config";
    public static final String CONTENT = "content";

    private String id = "";
    private String config = "";
    private String content = "";

    /**
     * Default Constructor.
     */
    protected S3Settings(String id)
    {
        setId(id);
    }

    /**
     * Copy constructor.
     */
    public S3Settings(S3Settings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(S3Settings obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setConfigBucket(obj.getConfigBucket());
            setContentBucket(obj.getContentBucket());
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public S3Settings(String id, Map<String, Object> map)
    {
        this(id);

        if(map.containsKey(CONFIG))
            setConfigBucket((String)map.get(CONFIG));
        if(map.containsKey(CONTENT))
            setContentBucket((String)map.get(CONTENT));
    }

    /**
     * Returns the id of the S3 settings.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the id of the S3 settings.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the S3 settings.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the config bucket for the S3 settings.
     */
    public String getConfigBucket()
    {
        return config;
    }

    /**
     * Sets the config bucket for the S3 settings.
     */
    public void setConfigBucket(String config)
    {
        this.config = config;
    }

    /**
     * Returns the content bucket for the S3 settings.
     */
    public String getContentBucket()
    {
        return content;
    }

    /**
     * Sets the content bucket for the S3 settings.
     */
    public void setContentBucket(String content)
    {
        this.content = content;
    }
}