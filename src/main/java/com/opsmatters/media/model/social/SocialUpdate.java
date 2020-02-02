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
 * Class representing a social media update.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialUpdate extends SocialMessage
{
    private String organisation = "";
    private ContentType contentType;
    private int contentId = -1;

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
        setStatus(MessageStatus.PENDING);

        getProperties().put(SocialTemplate.HANDLE, "@"+organisation.getTwitterUsername());
        getProperties().put(SocialTemplate.HASHTAG, organisation.getSocialHashtag());
        getProperties().put(SocialTemplate.HASHTAGS, organisation.getSocialHashtags());
        if(content.getType() == ContentType.ROUNDUP)
            getProperties().put(SocialTemplate.URL, organisation.getUrl(System.getProperty("om-config.site.prod")));
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
            setContentId(obj.getContentId());
            setContentType(obj.getContentType());
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
     * Returns the update handle.
     */
    public String getHandle()
    {
        return getProperties().get(SocialTemplate.HANDLE);
    }

    /**
     * Sets the update handle.
     */
    public void setHandle(String handle)
    {
        getProperties().put(SocialTemplate.HANDLE, handle);
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
        return getProperties().get(SocialTemplate.HASHTAG);
    }

    /**
     * Sets the update hashtag.
     */
    public void setHashtag(String hashtag)
    {
        getProperties().put(SocialTemplate.HASHTAG, hashtag);
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
        return getProperties().get(SocialTemplate.HASHTAGS);
    }

    /**
     * Sets the update hashtags.
     */
    public void setHashtags(String hashtags)
    {
        getProperties().put(SocialTemplate.HASHTAGS, hashtags);
    }

    /**
     * Returns <CODE>true</CODE> if the update hashtags have been set.
     */
    public boolean hasHashtags()
    {
        return getHashtags() != null && getHashtags().length() > 0;
    }

    /**
     * Returns the update title1.
     */
    public String getTitle1()
    {
        return getProperties().get(SocialTemplate.TITLE1);
    }

    /**
     * Sets the update title1.
     */
    public void setTitle1(String title1)
    {
        getProperties().put(SocialTemplate.TITLE1, title1);
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
        return getProperties().get(SocialTemplate.TITLE2);
    }

    /**
     * Sets the update title2.
     */
    public void setTitle2(String title2)
    {
        getProperties().put(SocialTemplate.TITLE2, title2);
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
}