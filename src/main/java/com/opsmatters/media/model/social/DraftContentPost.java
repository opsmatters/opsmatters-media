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
import org.json.JSONObject;
import com.opsmatters.media.config.organisation.Organisations;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.EnvironmentName;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a draft social media post for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DraftContentPost extends DraftPost
{
    private String code = "";
    private String organisation = "";
    private ContentType contentType;
    private int contentId = -1;

    /**
     * Default constructor.
     */
    public DraftContentPost()
    {
    }

    /**
     * Constructor that takes an organisation.
     */
    public DraftContentPost(Site site, Organisation organisation)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(site.getId());
        setCode(organisation.getCode());
        setTitle(organisation.getName());
        setContentType(ContentType.ORGANISATION);
        setStatus(DraftStatus.NEW);

        getProperties().put(HANDLE, "@"+organisation.getHandle());
        getProperties().put(HASHTAG, organisation.getHashtag());
        getProperties().put(URL, organisation.getUrl(site.getEnvironment(EnvironmentName.PROD).getUrl()));
    }

    /**
     * Constructor that takes an organisation listing and a content item.
     */
    public DraftContentPost(Site site, Organisation organisation, ContentItem content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(site.getId());
        setCode(organisation.getCode());
        if(content.getType() != ContentType.ROUNDUP)
            setContentId(content.getId());
        setContentType(content.getType());
        setStatus(DraftStatus.NEW);

        getProperties().put(HANDLE, "@"+organisation.getHandle());
        getProperties().put(HASHTAG, organisation.getHashtag());
        if(content.getType() == ContentType.ROUNDUP)
            getProperties().put(URL, organisation.getUrl(site.getEnvironment(EnvironmentName.PROD).getUrl()));
    }

    /**
     * Constructor that takes a saved post.
     */
    public DraftContentPost(Site site, Organisation organisation, SavedContentPost post)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSourceId(post.getId());
        setSiteId(site.getId());
        setCode(organisation.getCode());
        setContentType(post.getContentType());
        setStatus(DraftStatus.NEW);

        getProperties().put(HANDLE, "@"+organisation.getHandle());
        getProperties().put(HASHTAG, organisation.getHashtag());
        setTitle(post.getName());
        setHashtags(post.getHashtags());
        setUrl(post.getUrl());
    }

    /**
     * Copy constructor.
     */
    public DraftContentPost(DraftContentPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DraftContentPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation());
            setContentId(obj.getContentId());
            setContentType(obj.getContentType());
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
        ret.putOpt(CONTENT_ID, getContentId());

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
        setContentId(obj.optInt(CONTENT_ID));
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
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the post content type.
     */
    public void setContentType(String contentType)
    {
        setContentType(ContentType.valueOf(contentType));
    }

    /**
     * Sets the post content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns <CODE>true</CODE> if the post can be added to the library.
     */
    public boolean isLibraryType()
    {
        return getContentType() == ContentType.POST
            || getContentType() == ContentType.EVENT
            || getContentType() == ContentType.TOOL
            || getContentType() == ContentType.JOB;
    }

    /**
     * Returns the post content id.
     */
    public int getContentId()
    {
        return contentId;
    }

    /**
     * Sets the post content id.
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
        if(hasCode() && contentId > 0 && contentType != null)
            ret = String.format("%s-%s-%05d", contentType.code(), code, contentId);
        return ret;
    }

    /**
     * Returns the post handle.
     */
    public String getHandle()
    {
        return getProperties().get(HANDLE);
    }

    /**
     * Sets the post handle.
     */
    public void setHandle(String handle)
    {
        getProperties().put(HANDLE, handle);
    }

    /**
     * Returns <CODE>true</CODE> if the post handle has been set.
     */
    public boolean hasHandle()
    {
        return getHandle() != null && getHandle().length() > 0;
    }

    /**
     * Returns the post hashtag.
     */
    public String getHashtag()
    {
        return getProperties().get(HASHTAG);
    }

    /**
     * Sets the post hashtag.
     */
    public void setHashtag(String hashtag)
    {
        getProperties().put(HASHTAG, hashtag);
    }

    /**
     * Returns <CODE>true</CODE> if the post hashtag has been set.
     */
    public boolean hasHashtag()
    {
        return getHashtag() != null && getHashtag().length() > 0;
    }

    /**
     * Returns the post title.
     */
    @Override
    public String getTitle()
    {
        return getProperties().get(TITLE);
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        getProperties().put(TITLE, title);
    }

    /**
     * Returns <CODE>true</CODE> if the post title has been set.
     */
    public boolean hasTitle()
    {
        return getTitle() != null && getTitle().length() > 0;
    }

    /**
     * Returns the post title1.
     */
    public String getTitle1()
    {
        return getProperties().get(TITLE1);
    }

    /**
     * Sets the post title1.
     */
    public void setTitle1(String title1)
    {
        getProperties().put(TITLE1, title1);
    }

    /**
     * Returns <CODE>true</CODE> if the post title1 has been set.
     */
    public boolean hasTitle1()
    {
        return getTitle1() != null && getTitle1().length() > 0;
    }

    /**
     * Returns the post title2.
     */
    public String getTitle2()
    {
        return getProperties().get(TITLE2);
    }

    /**
     * Sets the post title2.
     */
    public void setTitle2(String title2)
    {
        getProperties().put(TITLE2, title2);
    }

    /**
     * Returns <CODE>true</CODE> if the post title2 has been set.
     */
    public boolean hasTitle2()
    {
        return getTitle2() != null && getTitle2().length() > 0;
    }
}