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

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.config.organisation.Organisations;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a saved social media content post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SavedContentPost extends SavedPost
{
    private String code = "";
    private String organisation = "";
    private ContentType contentType;
    private Map<String,String> properties = new LinkedHashMap<String,String>();

    /**
     * Default constructor.
     */
    public SavedContentPost()
    {
    }

    /**
     * Constructor that takes a draft content post.
     */
    public SavedContentPost(Site site, DraftContentPost post, String message)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setPostedDate(post.getUpdatedDate());
        setSiteId(site.getId());
        setName(post.getTitle());
        setCode(post.getCode());
        setContentType(post.getContentType());
        setMessage(message);
        setHashtags(post.getHashtags());
        if(post.hasOriginalUrl())
            setUrl(post.getOriginalUrl());
        else
            setUrl(post.getUrl());
        setShortenUrl(post.hasShortenedUrl(site.getShortDomain()));
        setStatus(SourceStatus.ACTIVE);
    }

    /**
     * Copy constructor.
     */
    public SavedContentPost(SavedContentPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SavedContentPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation());
            setContentType(obj.getContentType());
            setProperties(obj.getProperties());
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
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(ORGANISATION, getCode());
        ret.putOpt(CONTENT_TYPE, getContentType().name());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        setCode(obj.optString(ORGANISATION));
        setContentType(obj.optString(CONTENT_TYPE));
    }

    /**
     * Returns the post organisation.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the post organisation.
     */
    public void setCode(String code)
    {
        this.code = code;

        Organisation organisation = Organisations.get(code);
        setOrganisation(organisation != null ? organisation.getName() : "");
    }

    /**
     * Returns <CODE>true</CODE> if the post organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the organisation name.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
    }

    /**
     * Returns the post content type.
     */
    @Override
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Returns the post content type value.
     */
    public String getContentTypeValue()
    {
        return contentType != null ? contentType.value() : "";
    }

    /**
     * Sets the post content type.
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
     * Sets the post content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Sets the post content type from a value.
     */
    public void setContentTypeValue(String contentType)
    {
        setContentType(ContentType.fromValue(contentType));
    }

    /**
     * Returns the post properties.
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * Returns the post properties as a JSON object.
     */
    public JSONObject getPropertiesAsJson()
    {
        return new JSONObject(getProperties());
    }

    /**
     * Sets the post properties.
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Sets the post properties from a JSON object.
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
     * Returns <CODE>true</CODE> if the given post property has been set.
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