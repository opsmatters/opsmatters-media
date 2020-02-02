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
import org.json.JSONObject;

/**
 * Class representing a social media message.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialMessage extends SocialItem
{
    private static final String ENABLED = ".enabled";

    private String templateId = "";
    private Map<String,String> properties = new LinkedHashMap<String,String>();
    private String message = "";
    private MessageStatus status;

    /**
     * Default constructor.
     */
    public SocialMessage()
    {
    }

    /**
     * Copy constructor.
     */
    public SocialMessage(SocialMessage obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialMessage obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTemplateId(obj.getTemplateId());
            setProperties(obj.getProperties());
            setMessage(obj.getMessage());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the message template id.
     */
    public String getTemplateId()
    {
        return templateId;
    }

    /**
     * Sets the message template id.
     */
    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    /**
     * Returns <CODE>true</CODE> if the message template id has been set.
     */
    public boolean hasTemplateId()
    {
        return templateId != null && templateId.length() > 0;
    }

    /**
     * Returns the message properties.
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * Returns the message properties as a JSON object.
     */
    public JSONObject getPropertiesAsJson()
    {
        return new JSONObject(getProperties());
    }

    /**
     * Sets the message properties.
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Sets the message properties from a JSON object.
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
     * Returns <CODE>true</CODE> if the given message property has been set.
     */
    public boolean hasProperty(String key)
    {
        return getProperties().containsKey(key);
    }

    /**
     * Returns <CODE>true</CODE> if the enabled property for given channel has been set.
     */
    public boolean hasEnabled(SocialChannel channel)
    {
        return hasProperty(channel.getName()+ENABLED);
    }

    /**
     * Returns the value of the given message property.
     */
    public String getProperty(String key)
    {
        return getProperties().get(key);
    }

    /**
     * Returns <CODE>true</CODE> if the given channel is enabled.
     */
    public boolean isEnabled(SocialChannel channel)
    {
        return Boolean.parseBoolean(getProperty(channel.getName()+ENABLED));
    }

    /**
     * Sets the value of the given message property.
     */
    public void setProperty(String key, String value)
    {
        getProperties().put(key, value);
    }

    /**
     * Set to <CODE>true</CODE> if the given channel is enabled.
     */
    public void setEnabled(SocialChannel channel, boolean enabled)
    {
        setProperty(channel.getName()+ENABLED, Boolean.toString(enabled));
    }

    /**
     * Returns the message URL.
     */
    public String getUrl()
    {
        return getProperties().get(SocialTemplate.URL);
    }

    /**
     * Sets the message URL.
     */
    public void setUrl(String url)
    {
        getProperties().put(SocialTemplate.URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the message URL has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the message URL has been set and it has been shortened.
     */
    public boolean hasShortenedUrl()
    {
        return hasUrl() && getUrl().indexOf("bit.ly") != -1;
    }

    /**
     * Returns the message original URL.
     */
    public String getOriginalUrl()
    {
        return getProperties().get(SocialTemplate.ORIGINAL_URL);
    }

    /**
     * Sets the message original URL.
     */
    public void setOriginalUrl(String url)
    {
        getProperties().put(SocialTemplate.ORIGINAL_URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the message original URL has been set.
     */
    public boolean hasOriginalUrl()
    {
        return getOriginalUrl() != null && getOriginalUrl().length() > 0;
    }

    /**
     * Returns the message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns <CODE>true</CODE> if the message has been set.
     */
    public boolean hasMessage()
    {
        return message != null && message.length() > 0;
    }

    /**
     * Returns the message status.
     */
    public MessageStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the message status.
     */
    public void setStatus(String status)
    {
        setStatus(MessageStatus.valueOf(status));
    }

    /**
     * Sets the message status.
     */
    public void setStatus(MessageStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if the message status is PENDING.
     */
    public boolean isPending()
    {
        return status == MessageStatus.PENDING;
    }

    /**
     * Returns <CODE>true</CODE> if the message status is PROCESSING.
     */
    public boolean isProcessing()
    {
        return status == MessageStatus.PROCESSING;
    }

    /**
     * Returns <CODE>true</CODE> if the message status is PROCESSED.
     */
    public boolean isProcessed()
    {
        return status == MessageStatus.PROCESSED;
    }

    /**
     * Returns <CODE>true</CODE> if the message status is ERROR.
     */
    public boolean isError()
    {
        return status == MessageStatus.ERROR;
    }

    /**
     * Returns <CODE>true</CODE> if the message status is SKIPPED.
     */
    public boolean isSkipped()
    {
        return status == MessageStatus.SKIPPED;
    }
}