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

import java.util.List;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.model.OwnedEntity;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a social media template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialTemplate extends SocialPost
{
    public static final String DEFAULT = "New Template";

    private String name = "";
    private ContentType contentType;
    private String postType = "";
    private String videoType = "";
    private String eventType = "";
    private String publicationType = "";
    private int weight = 0;
    private boolean sponsored = false;
    private String tags = "";
    private boolean shortenUrl = false;
    private TemplateStatus status;

    /**
     * Default constructor.
     */
    public SocialTemplate()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public SocialTemplate(String name)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setName(name);
        setStatus(TemplateStatus.NEW);
    }

    /**
     * Copy constructor.
     */
    public SocialTemplate(SocialTemplate obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setContentType(obj.getContentType());
            setPostType(obj.getPostType());
            setVideoType(obj.getVideoType());
            setEventType(obj.getEventType());
            setPublicationType(obj.getPublicationType());
            setWeight(obj.getWeight());
            setSponsored(obj.isSponsored());
            setTags(obj.getTags());
            setShortenUrl(obj.isShortenUrl());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(POST_TYPE.value(), getPostType());
        ret.putOpt(VIDEO_TYPE.value(), getVideoType());
        ret.putOpt(EVENT_TYPE.value(), getEventType());
        ret.putOpt(PUBLICATION_TYPE.value(), getPublicationType());
        ret.put(SPONSORED.value(), isSponsored());
        ret.put(TAGS.value(), getTags());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setPostType(obj.optString(POST_TYPE.value()));
        setVideoType(obj.optString(VIDEO_TYPE.value()));
        setEventType(obj.optString(EVENT_TYPE.value()));
        setPublicationType(obj.optString(PUBLICATION_TYPE.value()));
        setSponsored(obj.optBoolean(SPONSORED.value()));
        setTags(obj.optString(TAGS.value()));
    }

    /**
     * Returns the template name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the template name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the template name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the content type.
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the content type.
     */
    public void setContentType(String contentType)
    {
        try
        {
            setContentType(ContentType.valueOf(contentType));
        }
        catch(IllegalArgumentException e)
        {
            setContentType((ContentType)null);
        }
    }

    /**
     * Sets the content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns <CODE>true</CODE> if the URL should be automatically shortened.
     */
    public boolean isShortenUrl()
    {
        return shortenUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the URL should be automatically shortened.
     */
    public Boolean getShortenUrlObject()
    {
        return Boolean.valueOf(isShortenUrl());
    }

    /**
     * Set to <CODE>true</CODE> if the URL should be automatically shortened.
     */
    public void setShortenUrl(boolean shortenUrl)
    {
        this.shortenUrl = shortenUrl;
    }

    /**
     * Set to <CODE>true</CODE> if the URL should be automatically shortened.
     */
    public void setShortenUrlObject(Boolean shortenUrl)
    {
        setShortenUrl(shortenUrl != null && shortenUrl.booleanValue());
    }

    /**
     * Returns the weight of the template.
     */
    public int getWeight()
    {
        return weight;
    }

    /**
     * Sets the weight of the template.
     */
    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    /**
     * Returns the post type.
     */
    public String getPostType()
    {
        return postType;
    }

    /**
     * Sets the post type.
     */
    public void setPostType(String postType)
    {
        this.postType = postType;
    }

    /**
     * Returns <CODE>true</CODE> if the post type has been set.
     */
    public boolean hasPostType()
    {
        return getPostType() != null && getPostType().length() > 0;
    }

    /**
     * Returns the video type.
     */
    public String getVideoType()
    {
        return videoType;
    }

    /**
     * Sets the video type.
     */
    public void setVideoType(String videoType)
    {
        this.videoType = videoType;
    }

    /**
     * Returns <CODE>true</CODE> if the video type has been set.
     */
    public boolean hasVideoType()
    {
        return getVideoType() != null && getVideoType().length() > 0;
    }

    /**
     * Returns the event type.
     */
    public String getEventType()
    {
        return eventType;
    }

    /**
     * Sets the event type.
     */
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    /**
     * Returns <CODE>true</CODE> if the event type has been set.
     */
    public boolean hasEventType()
    {
        return getEventType() != null && getEventType().length() > 0;
    }

    /**
     * Returns the publication type.
     */
    public String getPublicationType()
    {
        return publicationType;
    }

    /**
     * Sets the publication type.
     */
    public void setPublicationType(String publicationType)
    {
        this.publicationType = publicationType;
    }

    /**
     * Returns <CODE>true</CODE> if the publication type has been set.
     */
    public boolean hasPublicationType()
    {
        return getPublicationType() != null && getPublicationType().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the template is for sponsored posts.
     */
    public boolean isSponsored()
    {
        return sponsored;
    }

    /**
     * Returns <CODE>true</CODE> if the template is for sponsored posts.
     */
    public Boolean getSponsoredObject()
    {
        return Boolean.valueOf(isSponsored());
    }

    /**
     * Set to <CODE>true</CODE> if the template is for sponsored posts.
     */
    public void setSponsored(boolean sponsored)
    {
        this.sponsored = sponsored;
    }

    /**
     * Set to <CODE>true</CODE> if the template is for sponsored posts.
     */
    public void setSponsoredObject(Boolean sponsored)
    {
        setSponsored(sponsored != null && sponsored.booleanValue());
    }

    /**
     * Returns the tags.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Returns the list of tags.
     */
    public List<String> getTagsList()
    {
        return StringUtils.toList(getTags());
    }

    /**
     * Sets the tags.
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

    /**
     * Sets the list of tags.
     */
    public void setTagsList(List<String> tags)
    {
        setTags(StringUtils.fromList(tags));
    }

    /**
     * Returns <CODE>true</CODE> if the tags has been set.
     */
    public boolean hasTags()
    {
        return tags != null && tags.length() > 0;
    }

    /**
     * Returns the status.
     */
    public TemplateStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the status.
     */
    public void setStatus(String status)
    {
        setStatus(TemplateStatus.valueOf(status));
    }

    /**
     * Sets the status.
     */
    public void setStatus(TemplateStatus status)
    {
        this.status = status;
    }
}