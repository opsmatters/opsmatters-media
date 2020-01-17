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
import twitter4j.Status;
import facebook4j.Post;
import com.echobox.api.linkedin.types.Share;

/**
 * Class representing a social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPost extends SocialItem
{
    private String organisation = "";
    private String message = "";
    private SocialChannel channel;
    private PostStatus status;

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
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setOrganisation(obj.getOrganisation());
            setMessage(obj.getMessage());
            setChannel(obj.getChannel());
            setStatus(obj.getStatus());
        }
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
     * Returns the post status.
     */
    public PostStatus getStatus()
    {
        return status;
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
}