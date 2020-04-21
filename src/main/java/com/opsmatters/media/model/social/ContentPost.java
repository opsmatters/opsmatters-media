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
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.model.content.Organisation;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;

/**
 * Class representing a draft social media post for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentPost extends DraftPost
{
    public static final String ORGANISATION = "organisation";
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_ID = "content-id";

    private String organisation = "";
    private ContentType contentType;
    private int contentId = -1;

    /**
     * Default constructor.
     */
    public ContentPost()
    {
    }

    /**
     * Constructor that takes an organisation.
     */
    public ContentPost(Organisation organisation)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setOrganisation(organisation.getCode());
        setTitle(organisation.getTitle());
        setContentId(organisation.getId());
        setContentType(ContentType.ORGANISATION);
        setStatus(DraftStatus.NEW);

        getProperties().put(PostTemplate.HANDLE, "@"+organisation.getTwitterUsername());
        getProperties().put(PostTemplate.HASHTAG, organisation.getHashtag());
        getProperties().put(PostTemplate.URL, organisation.getUrl(System.getProperty("om-config.site.prod")));
    }

    /**
     * Constructor that takes an organisation and a content item.
     */
    public ContentPost(Organisation organisation, ContentItem content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setOrganisation(organisation.getCode());
        if(content.getType() != ContentType.ROUNDUP)
            setContentId(content.getId());
        setContentType(content.getType());
        setStatus(DraftStatus.NEW);

        getProperties().put(PostTemplate.HANDLE, "@"+organisation.getTwitterUsername());
        getProperties().put(PostTemplate.HASHTAG, organisation.getHashtag());
        if(content.getType() == ContentType.ROUNDUP)
            getProperties().put(PostTemplate.URL, organisation.getUrl(System.getProperty("om-config.site.prod")));
    }

    /**
     * Constructor that takes a library post template.
     */
    public ContentPost(Organisation organisation, PostTemplate template)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setTemplateId(template.getId());
        setOrganisation(organisation.getCode());
        setContentType(template.getContentType());
        setStatus(DraftStatus.NEW);

        getProperties().put(PostTemplate.HANDLE, "@"+organisation.getTwitterUsername());
        getProperties().put(PostTemplate.HASHTAG, organisation.getHashtag());
        setTitle(template.getName());
        setHashtags(template.getHashtags());
        setUrl(template.getUrl());
    }

    /**
     * Copy constructor.
     */
    public ContentPost(ContentPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentPost obj)
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

        ret.putOpt(ORGANISATION, getOrganisation());
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
        setOrganisation(obj.optString(ORGANISATION));
        setContentType(obj.optString(CONTENT_TYPE));
        setContentId(obj.optInt(CONTENT_ID));
    }

    /**
     * Returns the post organisation.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the post organisation.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the post organisation has been set.
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
        return getContentType() == ContentType.POST || getContentType() == ContentType.EVENT;
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
        if(hasOrganisation() && contentId > 0 && contentType != null)
            ret = String.format("%s-%s-%05d", contentType.code(), organisation, contentId);
        return ret;
    }

    /**
     * Returns the post handle.
     */
    public String getHandle()
    {
        return getProperties().get(PostTemplate.HANDLE);
    }

    /**
     * Sets the post handle.
     */
    public void setHandle(String handle)
    {
        getProperties().put(PostTemplate.HANDLE, handle);
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
        return getProperties().get(PostTemplate.HASHTAG);
    }

    /**
     * Sets the post hashtag.
     */
    public void setHashtag(String hashtag)
    {
        getProperties().put(PostTemplate.HASHTAG, hashtag);
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
        return getProperties().get(PostTemplate.TITLE);
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        getProperties().put(PostTemplate.TITLE, title);
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
        return getProperties().get(PostTemplate.TITLE1);
    }

    /**
     * Sets the post title1.
     */
    public void setTitle1(String title1)
    {
        getProperties().put(PostTemplate.TITLE1, title1);
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
        return getProperties().get(PostTemplate.TITLE2);
    }

    /**
     * Sets the post title2.
     */
    public void setTitle2(String title2)
    {
        getProperties().put(PostTemplate.TITLE2, title2);
    }

    /**
     * Returns <CODE>true</CODE> if the post title2 has been set.
     */
    public boolean hasTitle2()
    {
        return getTitle2() != null && getTitle2().length() > 0;
    }
}