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

import java.net.SocketTimeoutException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.cache.social.SocialChannels;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.model.DeliveryStatus;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.admin.Email;
import com.opsmatters.media.model.admin.EmailBody;
import com.opsmatters.media.model.content.FieldName;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.client.social.SocialClient;
import com.opsmatters.media.client.social.SocialClientFactory;
import com.opsmatters.media.client.social.SocialTimeoutException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a social media post that has been prepared for a channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChannelPost extends SocialPost
{
    private static final Logger logger = Logger.getLogger(ChannelPost.class.getName());

    private String draftId = "";
    private String code = "";
    private String organisation = "";
    private String title = "";
    private String channel;
    private SocialPostType type;
    private ContentType contentType;
    private int contentId = -1;
    private DeliveryStatus status;
    private String externalId = "";
    private Instant scheduledDate;
    private int errorCode;
    private String errorMessage = "";

    /**
     * Default constructor.
     */
    public ChannelPost()
    {
    }

    /**
     * Constructor that takes a draft post, channel and a message.
     */
    public ChannelPost(DraftPost post, SocialChannel channel, String message)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(post.getSiteId());
        setScheduledDate(post.getScheduledDate());
        setDraftId(post.getId());
        setTitle(post.getTitle());
        setChannel(channel.getCode());
        setMessage(message);
        setType(post.getType());
        setStatus(DeliveryStatus.NEW);

        if(post.getType() == SocialPostType.CONTENT)
        {
            DraftContentPost contentPost = (DraftContentPost)post;
            setCode(contentPost.getCode());
            setContentType(contentPost.getContentType());
            setContentId(contentPost.getContentId());
        }
    }

    /**
     * Constructor that takes an id, channel and a message.
     */
    public ChannelPost(String id, SocialChannel channel, String message)
    {
        setId(id);
        setCreatedDate(Instant.now());
        setUpdatedDate(Instant.now());
        setChannel(channel.getCode());
        setMessage(message);
        setType(SocialPostType.EXTERNAL);
        setStatus(DeliveryStatus.RECEIVED);
    }

    /**
     * Constructor that takes an id and a channel.
     */
    public ChannelPost(String id, SocialChannel channel)
    {
        this(id, channel, "");
    }

    /**
     * Copy constructor.
     */
    public ChannelPost(ChannelPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChannelPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setDraftId(obj.getDraftId());
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation() != null ? obj.getOrganisation() : "");
            setTitle(obj.getTitle());
            setChannel(obj.getChannel());
            setContentType(obj.getContentType());
            setContentId(obj.getContentId());
            setStatus(obj.getStatus());
            setExternalId(obj.getExternalId());
            setScheduledDate(obj.getScheduledDate());
            setErrorCode(obj.getErrorCode());
            setErrorMessage(obj.getErrorMessage());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(FieldName.EXTERNAL_ID.value(), getExternalId());
        if(getScheduledDate() != null)
            ret.put(FieldName.SCHEDULED_DATE.value(), getScheduledDateMillis());
        ret.putOpt(FieldName.ERROR_CODE.value(), getErrorCode());
        ret.putOpt(FieldName.ERROR_MESSAGE.value(), getErrorMessage());

        if(getType() == SocialPostType.CONTENT)
        {
            ret.putOpt(FieldName.ORGANISATION.value(), getCode());
            if(getContentType() != null)
                ret.putOpt(FieldName.CONTENT_TYPE.value(), getContentType().name());
            if(getContentId() > 0)
                ret.putOpt(FieldName.CONTENT_ID.value(), getContentId());
        }

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setExternalId(obj.optString(FieldName.EXTERNAL_ID.value()));
        long scheduledDateMillis = obj.optLong(FieldName.SCHEDULED_DATE.value());
        if(scheduledDateMillis > 0L)
            setScheduledDateMillis(scheduledDateMillis);
        setErrorCode(obj.optInt(FieldName.ERROR_CODE.value()));
        setErrorMessage(obj.optString(FieldName.ERROR_MESSAGE.value()));

        if(getType() == SocialPostType.CONTENT)
        {
            setCode(obj.optString(FieldName.ORGANISATION.value()));
            setContentType(obj.optString(FieldName.CONTENT_TYPE.value()));
            setContentId(obj.optInt(FieldName.CONTENT_ID.value()));
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
     * Returns the social channel code.
     */
    public String getChannel()
    {
        return channel;
    }

    /**
     * Sets the social channel code.
     */
    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    /**
     * Returns the post type.
     */
    @Override
    public SocialPostType getType()
    {
        return type;
    }

    /**
     * Sets the post type.
     */
    public void setType(String type)
    {
        setType(SocialPostType.valueOf(type));
    }

    /**
     * Sets the post type.
     */
    public void setType(SocialPostType type)
    {
        this.type = type;
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
        setContentType(contentType != null && contentType.length() > 0 ? ContentType.valueOf(contentType) : null);
    }

    /**
     * Sets the post content type.
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
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
     * Returns <CODE>true</CODE> if the delivery status is PENDING.
     */
    public boolean isPending()
    {
        return status == DeliveryStatus.PENDING;
    }

    /**
     * Returns <CODE>true</CODE> if the delivery status is COMPLETED.
     */
    public boolean isCompleted()
    {
        return status == DeliveryStatus.COMPLETED;
    }

    /**
     * Returns <CODE>true</CODE> if the delivery status is ERROR.
     */
    public boolean isError()
    {
        return status == DeliveryStatus.ERROR;
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
     * Returns <CODE>true</CODE> if the post error message has been set.
     */
    public boolean hasErrorMessage()
    {
        return getErrorMessage() != null && getErrorMessage().length() > 0;
    }

    /**
     * Send the post using a client.
     */
    public void send() throws Exception
    {
        SocialClient client = null;

        try
        {
            client = SocialClientFactory.newClient(SocialChannels.get(getChannel()));
            if(client == null)
                throw new IllegalArgumentException("unknown channel provider: "+getChannel());
            setStatus(DeliveryStatus.SENDING);
            ChannelPost sent = client.sendPost(getMessage(MessageFormat.DECODED));
            if(sent != null)
            {
                setExternalId(sent.getId());
                setStatus(DeliveryStatus.COMPLETED);
                setErrorCode(0);
                setErrorMessage("");
            }
        }
        catch(SocketTimeoutException e)
        {
            setErrorMessage(e.getMessage());

            // A timeout while crawling an internal page is recoverable
            if(e instanceof SocialTimeoutException)
            {
                throw e;
            }
            else
            {
                setStatus(DeliveryStatus.ERROR);
                logger.severe(StringUtils.serialize(e));
            }
        }
        catch(Exception e)
        {
            if(client != null)
            {
                setErrorCode(client.getErrorCode(e));
                setErrorMessage(client.getErrorMessage(e));
                if(client.isRecoverable(e))
                {
                    throw e;
                }
                else
                {
                    setStatus(DeliveryStatus.ERROR);
                    logger.severe(StringUtils.serialize(e));
                }
            }
            else // eg. OAuth token expired
            {
                setStatus(DeliveryStatus.ERROR);
                setErrorMessage(e.getMessage());
            }
        }
        finally
        {
            if(client != null)
                client.close();
        }
    }

    /**
     * Mark the post as PAUSED if the limit has been exceeded.
     */
    public void pause() throws Exception
    {
        setStatus(DeliveryStatus.PAUSED);
        setErrorCode(0);
        setErrorMessage("");
    }

    /**
     * Returns the email for an errored post.
     */
    public Email getAlertEmail()
    {
        String subject = String.format("Post %s: %s",
            getStatus().name(), getId());
        EmailBody body = new EmailBody()
            .addParagraph("The status of the following post has changed:")
            .addTable(new String[][]
            {
                {"ID", getId()},
                {"Organisation", getOrganisation()},
                {"Channel", getChannel()},
                {"Title", getTitle()},
                {"Status", getStatus().name()},
                {"Updated", getUpdatedDateAsString(Formats.CONTENT_DATE_FORMAT)},
                {"Error Code", Integer.toString(getErrorCode())},
                {"Error Message", getErrorMessage()},
            });
        return new Email(subject, body);
    }
}