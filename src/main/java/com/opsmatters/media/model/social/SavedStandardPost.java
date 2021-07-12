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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a saved standard social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SavedStandardPost extends SavedPost
{
    private Map<String,String> properties = new LinkedHashMap<String,String>();

    /**
     * Default constructor.
     */
    public SavedStandardPost()
    {
    }

    /**
     * Constructor that takes a site and name.
     */
    public SavedStandardPost(Site site, String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(site.getId());
        setName(name);
        setStatus(SourceStatus.NEW);
    }

    /**
     * Copy constructor.
     */
    public SavedStandardPost(SavedStandardPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SavedStandardPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setProperties(obj.getProperties());
        }
    }

    /**
     * Returns the post type.
     */
    @Override
    public PostType getType()
    {
        return PostType.STANDARD;
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        return new JSONObject();
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
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