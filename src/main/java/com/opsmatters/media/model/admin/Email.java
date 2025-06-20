/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.model.admin;

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.DeliveryStatus;
import com.opsmatters.media.model.system.aws.SesConfig;
import com.opsmatters.media.client.system.EmailClient;
import com.opsmatters.media.client.system.EmailClientFactory;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing an email.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Email extends BaseEntity
{
    public static final String FROM = "from";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";
    public static final String RECIPIENTS = "recipients";
    public static final String ERROR_MESSAGE = "error-message";

    private String from = "";
    private String subject = "";
    private String body = "";
    private List<String> recipients = new ArrayList<String>();
    private EmailProviderId providerId;
    private DeliveryStatus status;
    private String messageId = "";
    private String errorMessage = "";
    private EmailFormat format = EmailFormat.TEXT;
    private EmailType type = EmailType.NONE;

    /**
     * Default constructor.
     */
    public Email()
    {
    }

    /**
     * Constructor that takes a subject and message body.
     */
    public Email(String subject, String body)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSubject(subject);
        setBody(body);
        setStatus(DeliveryStatus.NEW);
    }

    /**
     * Constructor that takes a subject and message body.
     */
    public Email(String subject, EmailBody body)
    {
        this(subject, body.getText());
        setFormat(EmailFormat.HTML);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Email obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setFrom(obj.getFrom());
            setSubject(obj.getSubject());
            setBody(obj.getBody());
            setRecipients(obj.getRecipients());
            setProviderId(obj.getProviderId());
            setStatus(obj.getStatus());
            setMessageId(obj.getMessageId());
            setErrorMessage(obj.getErrorMessage());
            setFormat(obj.getFormat());
            setType(obj.getType());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(FROM, getFrom());
        ret.putOpt(SUBJECT, getSubject());
        ret.putOpt(BODY, getBody());
        ret.putOpt(RECIPIENTS, StringUtils.fromList(getRecipients()));
        ret.putOpt(ERROR_MESSAGE, getErrorMessage());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setFrom(obj.optString(FROM));
        setSubject(obj.optString(SUBJECT));
        setBody(obj.optString(BODY));
        setRecipients(StringUtils.toList(obj.optString(RECIPIENTS)));
        setErrorMessage(obj.optString(ERROR_MESSAGE));
    }

    /**
     * Returns the subject.
     */
    public String toString()
    {
        return getSubject();
    }

    /**
     * Returns the from address.
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * Sets the from address.
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /**
     * Returns <CODE>true</CODE> if the from address has been set.
     */
    public boolean hasFrom()
    {
        return from != null && from.length() > 0;
    }

    /**
     * Returns the email subject.
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * Sets the email subject.
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * Returns <CODE>true</CODE> if the email subject has been set.
     */
    public boolean hasSubject()
    {
        return subject != null && subject.length() > 0;
    }

    /**
     * Returns the email body.
     */
    public String getBody()
    {
        return body;
    }

    /**
     * Sets the email body.
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * Returns <CODE>true</CODE> if the email body has been set.
     */
    public boolean hasBody()
    {
        return body != null && body.length() > 0;
    }

    /**
     * Returns the email's recipients.
     */
    public List<String> getRecipients()
    {
        return recipients;
    }

    /**
     * Sets the email's recipients.
     */
    public void setRecipients(List<String> recipients)
    {
        for(String recipient : recipients)
            addRecipient(recipient);
    }

    /**
     * Adds a recipient to the list.
     */
    public void addRecipient(String recipient)
    {
        recipients.add(recipient);
    }

    /**
     * Returns the number of recipients.
     */
    public int getRecipientCount()
    {
        return recipients.size();
    }

    /**
     * Returns the email provider.
     */
    public EmailProviderId getProviderId()
    {
        return providerId;
    }

    /**
     * Sets the email's provider.
     */
    public void setProviderId(String providerId)
    {
        setProviderId(providerId != null ? EmailProviderId.valueOf(providerId) : null);
    }

    /**
     * Sets the email provider.
     */
    public void setProviderId(EmailProviderId providerId)
    {
        this.providerId = providerId;
    }

    /**
     * Returns <CODE>true</CODE> if the email provider has been set.
     */
    public boolean hasProviderId()
    {
        return providerId != null;
    }

    /**
     * Returns the email's status.
     */
    public DeliveryStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the email's status.
     */
    public void setStatus(DeliveryStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the email's status.
     */
    public void setStatus(String status)
    {
        setStatus(DeliveryStatus.valueOf(status));
    }

    /**
     * Returns the email's type.
     */
    public EmailType getType()
    {
        return type;
    }

    /**
     * Sets the email's type.
     */
    public void setType(EmailType type)
    {
        this.type = type;
    }

    /**
     * Sets the email's type.
     */
    public void setType(String type)
    {
        setType(EmailType.valueOf(type));
    }

    /**
     * Returns the email's format.
     */
    public EmailFormat getFormat()
    {
        return format;
    }

    /**
     * Sets the email's format.
     */
    public void setFormat(EmailFormat format)
    {
        this.format = format;
    }

    /**
     * Sets the email's format.
     */
    public void setFormat(String format)
    {
        setFormat(EmailFormat.valueOf(format));
    }

    /**
     * Returns the email error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the email error message.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the email message id.
     */
    public String getMessageId()
    {
        return messageId;
    }

    /**
     * Sets the email message id.
     */
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    /**
     * Send the email using a client.
     */
    public void send(EmailProviderId providerId, SesConfig config) throws Exception
    {
        EmailClient client = EmailClientFactory.newClient(providerId, config);
        if(client == null)
            throw new IllegalArgumentException("unknown email provider id: "+providerId);

        try
        {
            setProviderId(providerId);
            setStatus(DeliveryStatus.SENDING);
            String messageId = client.sendEmail(this);
            if(messageId != null)
            {
                setMessageId(messageId);
                setStatus(DeliveryStatus.COMPLETED);
            }
        }
        catch(Exception e)
        {
            setStatus(DeliveryStatus.ERROR);
            setErrorMessage(e.getClass().getName()+": "+e.getMessage());
        }
    }
}