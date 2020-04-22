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

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.model.content.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;

/**
 * Class representing a social media post template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostTemplate extends SocialPost
{
    public static final String HANDLE = "handle";
    public static final String HASHTAG = "hashtag";
    public static final String HASHTAGS = "hashtags";
    public static final String TITLE = "title";
    public static final String TITLE1 = "title1";
    public static final String TITLE2 = "title2";
    public static final String URL = "url";
    public static final String ORIGINAL_URL = "original-url";

    private String name = "";
    private PostType type;
    private String code = "";
    private ContentType contentType;
    private boolean isDefault = false;
    private boolean shortenUrl = false;
    private Map<String,String> properties = new LinkedHashMap<String,String>();

    /**
     * Default constructor.
     */
    public PostTemplate()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public PostTemplate(String name, PostType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setType(type);
    }

    /**
     * Constructor that takes a content post.
     */
    public PostTemplate(ContentPost post, String message)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(post.getTitle());
        setType(PostType.LIBRARY);
        setCode(post.getCode());
        setContentType(post.getContentType());
        setMessage(message);
        setHashtags(post.getHashtags());
        if(post.hasOriginalUrl())
            setUrl(post.getOriginalUrl());
        else
            setUrl(post.getUrl());
        setShortenUrl(post.hasShortenedUrl());
    }

    /**
     * Copy constructor.
     */
    public PostTemplate(PostTemplate obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PostTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setType(obj.getType());
            setCode(obj.getCode());
            setContentType(obj.getContentType());
            setDefault(obj.isDefault());
            setShortenUrl(obj.isShortenUrl());
            setProperties(obj.getProperties());
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
     * Returns the template post type.
     */
    @Override
    public PostType getType()
    {
        return type;
    }

    /**
     * Returns the template post type value.
     */
    public String getTypeValue()
    {
        return type != null ? type.value() : "";
    }

    /**
     * Sets the template post type.
     */
    public void setType(String type)
    {
        try
        {
            setType(PostType.valueOf(type));
        }
        catch(IllegalArgumentException e)
        {
            setType((PostType)null);
        }
    }

    /**
     * Sets the template post type.
     */
    public void setType(PostType type)
    {
        this.type = type;
    }

    /**
     * Sets the template post type from a value.
     */
    public void setTypeValue(String type)
    {
        setType(PostType.fromValue(type));
    }

    /**
     * Returns the template organisation.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the template organisation.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the template organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
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

    /**
     * Returns <CODE>true</CODE> if the template URL should be automatically shortened.
     */
    public boolean isShortenUrl()
    {
        return shortenUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the template URL should be automatically shortened.
     */
    public Boolean getShortenUrlObject()
    {
        return new Boolean(isShortenUrl());
    }

    /**
     * Set to <CODE>true</CODE> if the template URL should be automatically shortened.
     */
    public void setShortenUrl(boolean shortenUrl)
    {
        this.shortenUrl = shortenUrl;
    }

    /**
     * Set to <CODE>true</CODE> if the template URL should be automatically shortened.
     */
    public void setShortenUrlObject(Boolean shortenUrl)
    {
        setShortenUrl(shortenUrl != null && shortenUrl.booleanValue());
    }

    /**
     * Returns the template properties.
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * Returns the template properties as a JSON object.
     */
    public JSONObject getPropertiesAsJson()
    {
        return new JSONObject(getProperties());
    }

    /**
     * Sets the template properties.
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Sets the template properties from a JSON object.
     */
    public void setProperties(JSONObject obj)
    {
        getProperties().clear();
        Iterator<String> keys = obj.keys();
        while(keys.hasNext())
        {
            String key = keys.next();
            getProperties().put(key, obj.getString(key));
        }
    }

    /**
     * Returns <CODE>true</CODE> if the given template property has been set.
     */
    public boolean hasProperty(String key)
    {
        return getProperties().containsKey(key);
    }

    /**
     * Returns the post hashtags.
     */
    public String getHashtags()
    {
        return getProperties().get(HASHTAGS);
    }

    /**
     * Sets the post hashtags.
     */
    public void setHashtags(String hashtags)
    {
        getProperties().put(HASHTAGS, hashtags);
    }

    /**
     * Returns <CODE>true</CODE> if the post hashtags have been set.
     */
    public boolean hasHashtags()
    {
        return getHashtags() != null && getHashtags().length() > 0;
    }

    /**
     * Returns the post URL.
     */
    public String getUrl()
    {
        return getProperties().get(URL);
    }

    /**
     * Sets the post URL.
     */
    public void setUrl(String url)
    {
        getProperties().put(URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the post URL has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }
}