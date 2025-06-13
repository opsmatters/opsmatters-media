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

import java.util.List;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.Article;
import com.opsmatters.media.model.content.post.Post;
import com.opsmatters.media.model.content.video.Video;
import com.opsmatters.media.model.content.event.Event;
import com.opsmatters.media.model.content.publication.Publication;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.system.EnvironmentId.*;
import static com.opsmatters.media.model.social.SocialPostProperty.*;

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
    private String postType = "";
    private String videoType = "";
    private String eventType = "";
    private String publicationType = "";
    private String tags = "";
    private boolean sponsored = false;

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
        setStatus(DraftPostStatus.NEW);

        setHandle(organisation.hasHandle() ? "@"+organisation.getHandle() : "");
        setHashtag(organisation.getHashtag());
        setUrl(organisation.getUrl(site.getEnvironment(PROD).getUrl()));
    }

    /**
     * Constructor that takes an organisation listing and a content item.
     */
    public DraftContentPost(Site site, Organisation organisation, Content content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(site.getId());
        setCode(organisation.getCode());
        setContentType(content.getType());
        setStatus(DraftPostStatus.NEW);

        if(content.getType() != ContentType.ROUNDUP)
            setContentId(content.getId());

        if(content.getType() == ContentType.POST)
        {
            Post post = (Post)content;
            setPostType(post.getPostType());
        }
        else if(content.getType() == ContentType.VIDEO)
        {
            Video video = (Video)content;
            setVideoType(video.getVideoType());
        }
        else if(content.getType() == ContentType.EVENT)
        {
            Event event = (Event)content;
            setEventType(event.getEventType());
        }
        else if(content.getType() == ContentType.PUBLICATION)
        {
            Publication publication = (Publication)content;
            setPublicationType(publication.getPublicationType());
        }

        if(content instanceof Article)
        {
            Article article = (Article)content;
            setSummary(article.getSummary());
            setTags(article.getTags());
            setSponsored(article.isSponsored());
        }

        setHandle(organisation.hasHandle() ? "@"+organisation.getHandle() : "");
        setHashtag(organisation.getHashtag());
        if(content.getType() == ContentType.ROUNDUP)
            setUrl(organisation.getUrl(site.getEnvironment(PROD).getUrl()));
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
        setStatus(DraftPostStatus.NEW);

        setHandle(organisation.hasHandle() ? "@"+organisation.getHandle() : "");
        setHashtag(organisation.getHashtag());
        setTitle(post.getTitle());
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
            setPostType(obj.getPostType());
            setVideoType(obj.getVideoType());
            setEventType(obj.getEventType());
            setPublicationType(obj.getPublicationType());
            setTags(obj.getTags());
            setSponsored(obj.isSponsored());
        }
    }

    /**
     * Update the post using the given content item.
     */
    public void update(Content content)
    {
        setUpdatedDate(Instant.now());

        if(content.getType() == ContentType.POST)
        {
            Post post = (Post)content;
            setPostType(post.getPostType());
        }
        else if(content.getType() == ContentType.VIDEO)
        {
            Video video = (Video)content;
            setVideoType(video.getVideoType());
        }
        else if(content.getType() == ContentType.EVENT)
        {
            Event event = (Event)content;
            setEventType(event.getEventType());
        }
        else if(content.getType() == ContentType.PUBLICATION)
        {
            Publication publication = (Publication)content;
            setPublicationType(publication.getPublicationType());
        }

        if(content instanceof Article)
        {
            Article article = (Article)content;
            setSummary(article.getSummary());
            setTags(article.getTags());
            setSponsored(article.isSponsored());
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
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(ORGANISATION.value(), getCode());
        ret.putOpt(CONTENT_TYPE.value(), getContentType().name());
        ret.putOpt(CONTENT_ID.value(), getContentId());
        if(getContentType() == ContentType.POST)
            ret.putOpt(POST_TYPE.value(), getPostType());
        if(getContentType() == ContentType.VIDEO)
            ret.putOpt(VIDEO_TYPE.value(), getVideoType());
        if(getContentType() == ContentType.EVENT)
            ret.putOpt(EVENT_TYPE.value(), getEventType());
        if(getContentType() == ContentType.PUBLICATION)
            ret.putOpt(PUBLICATION_TYPE.value(), getPublicationType());
        ret.putOpt(TAGS.value(), getTags());
        ret.putOpt(SPONSORED.value(), isSponsored());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);
        setCode(obj.optString(ORGANISATION.value()));
        setContentType(obj.optString(CONTENT_TYPE.value()));
        setContentId(obj.optInt(CONTENT_ID.value()));
        setPostType(obj.optString(POST_TYPE.value()));
        setVideoType(obj.optString(VIDEO_TYPE.value()));
        setEventType(obj.optString(EVENT_TYPE.value()));
        setPublicationType(obj.optString(PUBLICATION_TYPE.value()));
        setTags(obj.optString(TAGS.value()));
        setSponsored(obj.optBoolean(SPONSORED.value(), false));
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
        setContentType(contentType != null ? ContentType.valueOf(contentType) : null);
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
            || getContentType() == ContentType.TOOL;
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
    @Override
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
    @Override
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

    /**
     * Returns the post summary.
     */
    public String getSummary()
    {
        return getProperties().get(SUMMARY);
    }

    /**
     * Sets the post summary.
     */
    public void setSummary(String summary)
    {
        getProperties().put(SUMMARY, summary);
    }

    /**
     * Returns <CODE>true</CODE> if the post summary has been set.
     */
    public boolean hasSummary()
    {
        return getSummary() != null && getSummary().length() > 0;
    }

    /**
     * Returns the post preamble.
     */
    public String getPreamble()
    {
        return getProperties().get(PREAMBLE);
    }

    /**
     * Sets the post preamble.
     */
    public void setPreamble(String preamble)
    {
        getProperties().put(PREAMBLE, preamble);
    }

    /**
     * Returns <CODE>true</CODE> if the post preamble has been set.
     */
    public boolean hasPreamble()
    {
        return getPreamble() != null && getPreamble().length() > 0;
    }

    /**
     * Returns the post description.
     */
    public String getDescription()
    {
        return getProperties().get(DESCRIPTION);
    }

    /**
     * Sets the post description.
     */
    public void setDescription(String description)
    {
        getProperties().put(DESCRIPTION, description);
    }

    /**
     * Returns <CODE>true</CODE> if the post description has been set.
     */
    public boolean hasDescription()
    {
        return getDescription() != null && getDescription().length() > 0;
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
     * Returns the post tags.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Returns the list of post tags.
     */
    public List<String> getTagsList()
    {
        return StringUtils.toList(getTags());
    }

    /**
     * Sets the post tags.
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

    /**
     * Sets the list of post tags.
     */
    public void setTagsList(List<String> tags)
    {
        setTags(StringUtils.fromList(tags));
    }

    /**
     * Returns <CODE>true</CODE> if the post tags has been set.
     */
    public boolean hasTags()
    {
        return getTags() != null && getTags().length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the post is sponsored.
     */
    public boolean isSponsored()
    {
        return sponsored;
    }

    /**
     * Set to <CODE>true</CODE> if the post is sponsored.
     */
    public void setSponsored(boolean sponsored)
    {
        this.sponsored = sponsored;
    }
}