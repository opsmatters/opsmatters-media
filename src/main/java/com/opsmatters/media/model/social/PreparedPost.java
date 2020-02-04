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
import com.echobox.api.linkedin.types.ugc.UGCShare;

/**
 * Class representing a social media post that has been prepared and assigned to a channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PreparedPost extends SocialPost
{
    private String organisation = "";
    private String title = "";
    private SocialChannel channel;
    private PostType type;
    private DeliveryStatus status;

    /**
     * Default constructor.
     */
    public PreparedPost()
    {
    }

    /**
     * Constructor that takes a type, channel and message.
     */
    /**
     * Constructor that takes a draft post, a message and a channel.
     */
    public PreparedPost(DraftPost post, String message, SocialChannel channel)
    {
        setCreatedDate(Instant.now());
        if(post.getType() == PostType.CONTENT)
            setOrganisation(((ContentPost)post).getOrganisation());
        setMessage(message);
        setChannel(channel);
        setType(post.getType());
        setStatus(DeliveryStatus.NEW);
    }

    /**
     * Constructor that takes a Twitter status and a channel.
     */
    public PreparedPost(Status status, SocialChannel channel)
    {
        setId(Long.toString(status.getId()));
        setCreatedDateMillis(status.getCreatedAt().getTime());
        setUpdatedDate(Instant.now());
        setMessage(status.getText());
        setChannel(channel);
        setType(PostType.EXTERNAL);
        setStatus(DeliveryStatus.PUBLISHED);
    }

    /**
     * Constructor that takes a Facebook post and a channel.
     */
    public PreparedPost(Post post, SocialChannel channel)
    {
        setId(post.getId());
        setCreatedDateMillis(post.getCreatedTime().getTime());
        setUpdatedDate(Instant.now());
        setMessage(post.getMessage());
        setChannel(channel);
        setType(PostType.EXTERNAL);
        setStatus(DeliveryStatus.PUBLISHED);
    }

    /**
     * Constructor that takes a LinkedIn share and a channel.
     */
    public PreparedPost(UGCShare share, SocialChannel channel)
    {
        setId(share.getId().getId());
        if(share.getCreated() != null)
            setCreatedDateMillis(share.getCreated().getTime());
        else
            setCreatedDate(Instant.now());
        setUpdatedDate(Instant.now());
        if(share.getSpecificContent() != null)
            setMessage(share.getSpecificContent().getShareContent().getShareCommentary().getText());
        setChannel(channel);
        setType(PostType.EXTERNAL);
        setStatus(DeliveryStatus.PUBLISHED);
    }

    /**
     * Copy constructor.
     */
    public PreparedPost(PreparedPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PreparedPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setOrganisation(obj.getOrganisation());
            setTitle(obj.getTitle());
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
     * Returns the post title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        this.title = title;
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
     * Returns the post type.
     */
    @Override
    public PostType getType()
    {
        return type;
    }

    /**
     * Sets the post type.
     */
    public void setType(String type)
    {
        setType(PostType.valueOf(type));
    }

    /**
     * Sets the post type.
     */
    public void setType(PostType type)
    {
        this.type = type;
    }

    /**
     * Returns the post delivery status.
     */
    public DeliveryStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the post delivery status.
     */
    public void setStatus(String status)
    {
        setStatus(DeliveryStatus.valueOf(status));
    }

    /**
     * Sets the post delivery status.
     */
    public void setStatus(DeliveryStatus status)
    {
        this.status = status;
    }
}