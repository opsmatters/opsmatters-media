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
 * Class representing a social media update.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialUpdate extends SocialItem
{
    private String organisation = "";
    private String templateId = "";
    private Map<String,String> properties = new LinkedHashMap<String,String>();
    private ContentType contentType;
    private int contentId = -1;
    private String message = "";
    private SocialUpdateStatus status;

    /**
     * Default constructor.
     */
    public SocialUpdate()
    {
    }

    /**
     * Constructor that takes an organisation and a content item.
     */
    public SocialUpdate(Organisation organisation, ContentItem content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setOrganisation(organisation.getCode());
        if(content.getType() != ContentType.ROUNDUP)
            setContentId(content.getId());
        setContentType(content.getType());
        setStatus(SocialUpdateStatus.PENDING);

        properties.put(SocialTemplate.HANDLE, "@"+organisation.getTwitterUsername());
        properties.put(SocialTemplate.HASHTAG, organisation.getSocialHashtag());
        properties.put(SocialTemplate.HASHTAGS, organisation.getSocialHashtags());
        if(content.getType() == ContentType.ROUNDUP)
            properties.put(SocialTemplate.URL, organisation.getUrl(System.getProperty("om-config.site.prod")));
    }

    /**
     * Copy constructor.
     */
    public SocialUpdate(SocialUpdate obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialUpdate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setOrganisation(obj.getOrganisation());
            setTemplateId(obj.getTemplateId());
            setContentId(obj.getContentId());
            setProperties(obj.getProperties());
            setContentType(obj.getContentType());
            setMessage(obj.getMessage());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the update organisation.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the update organisation.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the update organisation has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
    }

    /**
     * Returns the update template id.
     */
    public String getTemplateId()
    {
        return templateId;
    }

    /**
     * Sets the update template id.
     */
    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    /**
     * Returns <CODE>true</CODE> if the update template id has been set.
     */
    public boolean hasTemplateId()
    {
        return templateId != null && templateId.length() > 0;
    }

    /**
     * Returns the update content id.
     */
    public int getContentId()
    {
        return contentId;
    }

    /**
     * Sets the update content id.
     */
    public void setContentId(int contentId)
    {
        this.contentId = contentId;
    }

    /**
     * Returns the content GUID.
     */
    public String getGuid()
    {
        String ret = null;
        if(hasOrganisation() && contentId > 0 && contentType != null)
            ret = String.format("%s-%s-%05d", contentType.code(), organisation, contentId);
        return ret;
    }

    /**
     * Returns the update properties.
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * Returns the update properties as a JSON object.
     */
    public JSONObject getPropertiesAsJson()
    {
        return new JSONObject(properties);
    }

    /**
     * Sets the update properties.
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Sets the update properties from a JSON object.
     */
    public void setProperties(JSONObject obj)
    {
        this.properties.clear();
        Iterator<String> keys = obj.keys();
        while(keys.hasNext())
        {
            String key = keys.next();
            properties.put(key, obj.getString(key));
        }
    }

    /**
     * Sets the given update property.
     */
    public void setProperty(String key, String value)
    {
        this.properties.put(key, value);
    }

    /**
     * Returns the update handle.
     */
    public String getHandle()
    {
        return properties.get(SocialTemplate.HANDLE);
    }

    /**
     * Sets the update handle.
     */
    public void setHandle(String handle)
    {
        properties.put(SocialTemplate.HANDLE, handle);
    }

    /**
     * Returns <CODE>true</CODE> if the update handle has been set.
     */
    public boolean hasHandle()
    {
        return getHandle() != null && getHandle().length() > 0;
    }

    /**
     * Returns the update hashtag.
     */
    public String getHashtag()
    {
        return properties.get(SocialTemplate.HASHTAG);
    }

    /**
     * Sets the update hashtag.
     */
    public void setHashtag(String hashtag)
    {
        properties.put(SocialTemplate.HASHTAG, hashtag);
    }

    /**
     * Returns <CODE>true</CODE> if the update hashtag has been set.
     */
    public boolean hasHashtag()
    {
        return getHashtag() != null && getHashtag().length() > 0;
    }

    /**
     * Returns the update hashtags.
     */
    public String getHashtags()
    {
        return properties.get(SocialTemplate.HASHTAGS);
    }

    /**
     * Sets the update hashtags.
     */
    public void setHashtags(String hashtags)
    {
        properties.put(SocialTemplate.HASHTAGS, hashtags);
    }

    /**
     * Returns <CODE>true</CODE> if the update hashtags have been set.
     */
    public boolean hasHashtags()
    {
        return getHashtags() != null && getHashtags().length() > 0;
    }

    /**
     * Returns the update URL.
     */
    public String getUrl()
    {
        return properties.get(SocialTemplate.URL);
    }

    /**
     * Sets the update URL.
     */
    public void setUrl(String url)
    {
        properties.put(SocialTemplate.URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the update URL has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the update URL has been set and it has been shortened.
     */
    public boolean hasShortenedUrl()
    {
        return hasUrl() && getUrl().indexOf("bit.ly") != -1;
    }

    /**
     * Returns the update original URL.
     */
    public String getOriginalUrl()
    {
        return properties.get(SocialTemplate.ORIGINAL_URL);
    }

    /**
     * Sets the update original URL.
     */
    public void setOriginalUrl(String url)
    {
        properties.put(SocialTemplate.ORIGINAL_URL, url);
    }

    /**
     * Returns <CODE>true</CODE> if the update original URL has been set.
     */
    public boolean hasOriginalUrl()
    {
        return getOriginalUrl() != null && getOriginalUrl().length() > 0;
    }

    /**
     * Returns the update title1.
     */
    public String getTitle1()
    {
        return properties.get(SocialTemplate.TITLE1);
    }

    /**
     * Sets the update title1.
     */
    public void setTitle1(String title1)
    {
        properties.put(SocialTemplate.TITLE1, title1);
    }

    /**
     * Returns <CODE>true</CODE> if the update title1 has been set.
     */
    public boolean hasTitle1()
    {
        return getTitle1() != null && getTitle1().length() > 0;
    }

    /**
     * Returns the update title2.
     */
    public String getTitle2()
    {
        return properties.get(SocialTemplate.TITLE2);
    }

    /**
     * Sets the update title2.
     */
    public void setTitle2(String title2)
    {
        properties.put(SocialTemplate.TITLE2, title2);
    }

    /**
     * Returns <CODE>true</CODE> if the update title2 has been set.
     */
    public boolean hasTitle2()
    {
        return getTitle2() != null && getTitle2().length() > 0;
    }

    /**
     * Returns the update content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the update content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(ContentType.valueOf(contentType));
    }

    /**
     * Sets the update content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns the update message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the update message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns <CODE>true</CODE> if the update message has been set.
     */
    public boolean hasMessage()
    {
        return message != null && message.length() > 0;
    }

    /**
     * Returns the update status.
     */
    public SocialUpdateStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the update status.
     */
    public void setStatus(String status)
    {
        setStatus(SocialUpdateStatus.valueOf(status));
    }

    /**
     * Sets the update status.
     */
    public void setStatus(SocialUpdateStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if the update status is PENDING.
     */
    public boolean isPending()
    {
        return status == SocialUpdateStatus.PENDING;
    }

    /**
     * Returns <CODE>true</CODE> if the update status is PROCESSED.
     */
    public boolean isProcessed()
    {
        return status == SocialUpdateStatus.PROCESSED;
    }

    /**
     * Returns <CODE>true</CODE> if the update status is SKIPPED.
     */
    public boolean isSkipped()
    {
        return status == SocialUpdateStatus.SKIPPED;
    }
}