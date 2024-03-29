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
import org.json.JSONObject;
import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing a saved social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class SavedPost extends PostSource
{
    public static final String DEFAULT = "New Post";

    private String title = "";
    private Map<String,String> properties = new LinkedHashMap<String,String>();

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SavedPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTitle(obj.getTitle());
            setProperties(obj.getProperties());
        }
    }

    /**
     * Returns the title.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the post title.
     */
    @Override
    public String getName()
    {
        return getTitle();
    }

    /**
     * Returns the post title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the post content type.
     */
    public ContentType getContentType()
    {
        return null;
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