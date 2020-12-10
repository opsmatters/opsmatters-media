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
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.PostArticle;

/**
 * Class that represents the configuration for post content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostConfiguration extends ContentConfiguration<PostArticle>
{
    private static final Logger logger = Logger.getLogger(PostConfiguration.class.getName());

    public static final String IMAGE_PREFIX = "image-prefix";

    private String imagePrefix = "";

    /**
     * Default constructor.
     */
    public PostConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public PostConfiguration(PostConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PostConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setImagePrefix(obj.getImagePrefix());
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.POST;
    }

    /**
     * Returns the image prefix for this configuration.
     */
    public String getImagePrefix()
    {
        return imagePrefix;
    }

    /**
     * Sets the image prefix for this configuration.
     */
    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        super.parseDocument(map);
        if(map.containsKey(IMAGE_PREFIX))
            setImagePrefix((String)map.get(IMAGE_PREFIX));
    }
}