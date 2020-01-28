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
package com.opsmatters.media.model.social;

import java.time.Instant;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.model.content.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;

/**
 * Class representing a social media template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialTemplate extends SocialItem
{
    public static final String HANDLE = "handle";
    public static final String HASHTAG = "hashtag";
    public static final String HASHTAGS = "hashtags";
    public static final String TITLE1 = "title1";
    public static final String TITLE2 = "title2";
    public static final String URL = "url";

    private String name = "";
    private String message = "";
    private ContentType contentType;
    private boolean isDefault = false;

    /**
     * Default constructor.
     */
    public SocialTemplate()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public SocialTemplate(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
    }

    /**
     * Copy constructor.
     */
    public SocialTemplate(SocialTemplate obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setMessage(obj.getMessage());
            setContentType(obj.getContentType());
            setDefault(obj.isDefault());
        }
    }

    /**
     * Returns the template name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the template name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the template name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the template message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the template message.
     */
    public void setMessage(String message)
    {
        this.message = message;
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

    /**
     * Returns <CODE>true</CODE> if this template is the default for the content type.
     */
    public boolean isDefault()
    {
        return isDefault;
    }

    /**
     * Returns <CODE>true</CODE> if this template is the default for the content type.
     */
    public Boolean getDefaultObject()
    {
        return new Boolean(isDefault());
    }

    /**
     * Set to <CODE>true</CODE> if this template is the default for the content type.
     */
    public void setDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }

    /**
     * Set to <CODE>true</CODE> if this template is the default for the content type.
     */
    public void setDefaultObject(Boolean isDefault)
    {
        setDefault(isDefault != null && isDefault.booleanValue());
    }
}