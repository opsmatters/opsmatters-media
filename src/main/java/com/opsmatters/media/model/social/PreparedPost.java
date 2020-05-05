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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import twitter4j.Status;
import facebook4j.Post;
import com.echobox.api.linkedin.types.ugc.UGCShare;
import com.opsmatters.media.client.social.SocialClient;
import com.opsmatters.media.client.social.SocialClientFactory;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a social media post that has been prepared and assigned to a channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PreparedPost extends SocialPost
{
    private String draftId = "";
    private String code = "";
    private String title = "";
    private SocialChannel channel;
    private PostType type;
    private DeliveryStatus status;
    private String externalId = "";
    private Instant scheduledDate;
    private int errorCode;
    private String errorMessage = "";

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
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setScheduledDate(post.getScheduledDate());
        setDraftId(post.getId());
        if(post.getType() == PostType.CONTENT)
            setCode(((ContentPost)post).getCode());
        setTitle(post.getTitle());
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
        setStatus(DeliveryStatus.RECEIVED);
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
        setStatus(DeliveryStatus.RECEIVED);
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
        setStatus(DeliveryStatus.RECEIVED);
    }

    /**
     * Constructor that takes an external id and a channel.
     */
    public PreparedPost(String externalId, SocialChannel channel)
    {
        setId(externalId);
        setCreatedDate(Instant.now());
        setChannel(channel);
        setType(PostType.EXTERNAL);
        setStatus(DeliveryStatus.RECEIVED);
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
            setDraftId(obj.getDraftId());
            setCode(obj.getCode());
            setTitle(obj.getTitle());
            setChannel(obj.getChannel());
            setStatus(obj.getStatus());
            setExternalId(obj.getExternalId());
            setScheduledDate(obj.getScheduledDate());
            setErrorCode(obj.getErrorCode());
            setErrorMessage(obj.getErrorMessage());
        }
    }

    /**
     * Returns the draft post id.
     */
    public String getDraftId()
    {
        return draftId;
    }

    /**
     * Sets the draft post id.
     */
    public void setDraftId(String draftId)
    {
        this.draftId = draftId;
    }

    /**
     * Returns <CODE>true</CODE> if the draft post id has been set.
     */
    public boolean hasDraftId()
    {
        return draftId != null && draftId.length() > 0;
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
    }

    /**
     * Returns <CODE>true</CODE> if the post organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
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
     * Returns <CODE>true</CODE> if the post title has been set.
     */
    public boolean hasTitle()
    {
        return title != null && title.length() > 0;
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

    /**
     * Returns the post external id.
     */
    public String getExternalId()
    {
        return externalId;
    }

    /**
     * Sets the post external id.
     */
    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    /**
     * Returns <CODE>true</CODE> if the post external id has been set.
     */
    public boolean hasExternalId()
    {
        return externalId != null && externalId.length() > 0;
    }

    /**
     * Returns the date the item was scheduled.
     */
    public Instant getScheduledDate()
    {
        return scheduledDate;
    }

    /**
     * Returns the date the item was scheduled.
     */
    public long getScheduledDateMillis()
    {
        return getScheduledDate() != null ? getScheduledDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the item was scheduled.
     */
    public LocalDateTime getScheduledDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getScheduledDate());
    }

    /**
     * Returns the date the item was scheduled.
     */
    public String getScheduledDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(scheduledDate, pattern);
    }

    /**
     * Returns the date the item was scheduled.
     */
    public String getScheduledDateAsString()
    {
        return getScheduledDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDate(Instant scheduledDate)
    {
        this.scheduledDate = scheduledDate;
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateMillis(long millis)
    {
        if(millis > 0L)
            this.scheduledDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setScheduledDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateAsString(String str) throws DateTimeParseException
    {
        setScheduledDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was scheduled.
     */
    public void setScheduledDateUTC(LocalDateTime scheduledDate)
    {
        if(scheduledDate != null)
            setScheduledDate(TimeUtils.toInstantUTC(scheduledDate));
    }

    /**
     * Returns the post error code.
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * Sets the post error code.
     */
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * Returns the post error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the post error message.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Send the post using a client.
     */
    public void send() throws Exception
    {
        SocialClient client = SocialClientFactory.newClient(getChannel());
        if(client == null)
            throw new IllegalArgumentException("unknown channel provider: "+getChannel());

        try
        {
            setStatus(DeliveryStatus.SENDING);
            PreparedPost sent = client.sendPost(getMessage());
            if(sent != null)
            {
                setExternalId(sent.getId());
                setStatus(DeliveryStatus.SENT);
            }
        }
        catch(Exception e)
        {
            if(client.isRecoverable(e))
            {
                throw e;
            }
            else
            {
                setStatus(DeliveryStatus.ERROR);
                setErrorCode(client.getErrorCode(e));
                setErrorMessage(client.getErrorMessage(e));
            }
        }
    }
}