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
import java.time.format.DateTimeParseException;
import twitter4j.Status;
import facebook4j.Post;
import com.echobox.api.linkedin.types.Share;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPost implements java.io.Serializable
{
    private String id = "";
    private Instant createdDate;
    private Instant updatedDate;
    private String organisation = "";
    private String message = "";
    private SocialChannel channel;
    private PostStatus status;
    private String createdBy = "";

    static public enum PostStatus
    {
        NEW,
        PENDING,
        SENT,
        EXTERNAL;
    };

    /**
     * Default constructor.
     */
    public SocialPost()
    {
    }

    /**
     * Constructor that takes a Twitter status.
     */
    public SocialPost(Status status, SocialChannel channel)
    {
        setId(Long.toString(status.getId()));
        setCreatedDateMillis(status.getCreatedAt().getTime());
        setUpdatedDate(Instant.now());
        setMessage(status.getText());
        setChannel(channel);
        setStatus(PostStatus.EXTERNAL);
    }

    /**
     * Constructor that takes a Facebook post.
     */
    public SocialPost(Post post, SocialChannel channel)
    {
        setId(post.getId());
        setCreatedDateMillis(post.getCreatedTime().getTime());
        setUpdatedDate(Instant.now());
        setMessage(post.getMessage());
        setChannel(channel);
        setStatus(PostStatus.EXTERNAL);
    }

    /**
     * Constructor that takes a LinkedIn share.
     */
    public SocialPost(Share share, SocialChannel channel)
    {
        setId(Long.toString(share.getId()));
        setCreatedDateMillis(share.getCreated().getTime());
        setUpdatedDate(Instant.now());
        setMessage(share.getText().getText());
        setChannel(channel);
        setStatus(PostStatus.EXTERNAL);
    }

    /**
     * Copy constructor.
     */
    public SocialPost(SocialPost obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setCreatedDate(obj.getCreatedDate());
            setUpdatedDate(obj.getUpdatedDate());
            setOrganisation(obj.getOrganisation());
            setMessage(obj.getMessage());
            setChannel(obj.getChannel());
            setStatus(obj.getStatus());
            setCreatedBy(obj.getCreatedBy());
        }
    }

    /**
     * Returns the id.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the post id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the post id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the date the post was created.
     */
    public Instant getCreatedDate()
    {
        return createdDate;
    }

    /**
     * Returns the date the post was created.
     */
    public long getCreatedDateMillis()
    {
        return getCreatedDate() != null ? getCreatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the post was created.
     */
    public String getCreatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(createdDate, pattern);
    }

    /**
     * Returns the date the post was created.
     */
    public String getCreatedDateAsString()
    {
        return getCreatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.createdDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setCreatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDateAsString(String str) throws DateTimeParseException
    {
        setCreatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the date the post status was last updated.
     */
    public Instant getUpdatedDate()
    {
        return updatedDate;
    }

    /**
     * Returns the date the post was last updated.
     */
    public long getUpdatedDateMillis()
    {
        return getUpdatedDate() != null ? getUpdatedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the post status was last updated.
     */
    public String getUpdatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(updatedDate, pattern);
    }

    /**
     * Returns the date the post status was last updated.
     */
    public String getUpdatedDateAsString()
    {
        return getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the post was status was last updated.
     */
    public void setUpdatedDate(Instant updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    /**
     * Sets the date the post status was last updated.
     */
    public void setUpdatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.updatedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the post status was last updated.
     */
    public void setUpdatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setUpdatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the post status was last updated.
     */
    public void setUpdatedDateAsString(String str) throws DateTimeParseException
    {
        setUpdatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
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
     * Returns the post message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the post message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Returns the post status.
     */
    public PostStatus getStatus()
    {
        return status;
    }

    /**
     * Returns the social channel.
     */
    public SocialChannel getChannel()
    {
        return channel;
    }

    /**
     * Sets the social channel.
     */
    public void setChannel(SocialChannel channel)
    {
        this.channel = channel;
    }

    /**
     * Sets the post status.
     */
    public void setStatus(String status)
    {
        setStatus(PostStatus.valueOf(status));
    }

    /**
     * Sets the post status.
     */
    public void setStatus(PostStatus status)
    {
        this.status = status;
    }

    /**
     * Returns the post creator.
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the post creator.
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
}