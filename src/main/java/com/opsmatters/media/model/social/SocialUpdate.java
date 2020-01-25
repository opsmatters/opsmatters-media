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
public class SocialUpdate extends SocialItem
{
    public static final String HANDLE = "social.handle";
    public static final String HASHTAG = "social.hashtag";
    public static final String HASHTAGS = "social.hashtags";
    public static final String TITLE1 = "content.title1";
    public static final String TITLE2 = "content.title2";

    private String organisation = "";
    private String templateId = "";
    private String url = "";
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
        setContentId(content.getId());
        setUrl(organisation.getUrl(System.getProperty("om-config.site.prod")));
        setContentType(content.getType());
        setStatus(SocialUpdateStatus.PENDING);
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
            setUrl(obj.getUrl());
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
     * Returns the update URL.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the update URL.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the update URL has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.length() > 0;
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