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

import java.time.Instant;
import com.opsmatters.media.cache.organisation.Organisations;
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
        setTitle(post.getTitle());
        setCode(post.getCode());
        setContentType(post.getContentType());
        setMessage(message);
        setHashtags(post.getHashtags());
        if(post.hasOriginalUrl())
            setUrl(post.getOriginalUrl());
        else
            setUrl(post.getUrl());
        setShortenUrl(post.hasShortenedUrl(site.getShortDomain()));
        setStatus(SavedStatus.ACTIVE);
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
        }
    }

    /**
     * Returns the post type.
     */
    @Override
    public SocialPostType getType()
    {
        return SocialPostType.CONTENT;
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
            setContentType(contentType != null ? ContentType.valueOf(contentType) : null);
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
}