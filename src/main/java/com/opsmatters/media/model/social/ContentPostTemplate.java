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
package com.opsmatters.media.model.social;

import java.time.Instant;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a social media content post template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentPostTemplate extends PostTemplate
{
    private ContentType contentType;

    /**
     * Default constructor.
     */
    public ContentPostTemplate()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public ContentPostTemplate(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setStatus(SourceStatus.NEW);
    }

    /**
     * Copy constructor.
     */
    public ContentPostTemplate(ContentPostTemplate obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentPostTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setContentType(obj.getContentType());
        }
    }

    /**
     * Returns the post type.
     */
    @Override
    public PostType getType()
    {
        return PostType.CONTENT;
    }

    /**
     * Returns the template content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Returns the template content type value.
     */
    public String getContentTypeValue()
    {
        return contentType != null ? contentType.value() : "";
    }

    /**
     * Sets the template content type.
     */
    public void setContentType(String contentType)
    {
        try
        {
            setContentType(ContentType.valueOf(contentType));
        }
        catch(IllegalArgumentException e)
        {
            setContentType((ContentType)null);
        }
    }

    /**
     * Sets the template content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Sets the template content type from a value.
     */
    public void setContentTypeValue(String contentType)
    {
        setContentType(ContentType.fromValue(contentType));
    }
}