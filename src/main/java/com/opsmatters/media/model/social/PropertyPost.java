/*
 * Copyright 2024 Gerald Curley
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
import org.json.JSONObject;

/**
 * Class representing a social media property post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class PropertyPost extends SocialPost
{
    private SocialPostProperties properties = new SocialPostProperties();

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PropertyPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setProperties(obj.getProperties());
        }
    }

    /**
     * Returns the post properties.
     */
    public SocialPostProperties getProperties()
    {
        return properties;
    }

    /**
     * Returns the post properties as a JSON object.
     */
    public JSONObject getPropertiesAsJson()
    {
        return getProperties().toJson();
    }

    /**
     * Sets the post properties.
     */
    public void setProperties(Map<String,String> properties)
    {
        getProperties().set(properties);
    }

    /**
     * Sets the post properties from a JSON object.
     */
    public void setProperties(JSONObject obj)
    {
        getProperties().set(obj);
    }

    /**
     * Returns <CODE>true</CODE> if the given post property has been set.
     */
    public boolean hasProperty(SocialPostProperty property)
    {
        return getProperties().containsKey(property);
    }

    /**
     * Sets the value of the given post property.
     */
    public void setProperty(SocialPostProperty property, String value)
    {
        getProperties().put(property, value);
    }
}