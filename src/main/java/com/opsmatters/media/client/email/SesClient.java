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
package com.opsmatters.media.client.email;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Body;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.admin.EmailProvider;
import com.opsmatters.media.model.admin.Email;
import com.opsmatters.media.model.admin.EmailFormat;

/**
 * Class that represents a connection to SES for emails.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SesClient extends Client implements EmailClient
{
    private static final Logger logger = Logger.getLogger(SesClient.class.getName());

    public static final String AUTH = ".ses";

    private AmazonSimpleEmailService client;
    private String accessKeyId = "";
    private String secretAccessKey = "";

    /**
     * Returns a new SES client using an access token.
     */
    static public SesClient newClient() throws IOException
    {
        SesClient ret = SesClient.builder()
            .accessKeyId(System.getProperty("opsmatters.ses.accessKeyId"))
            .secretAccessKey(System.getProperty("opsmatters.ses.secretAccessKey"))
            .build();

        // Configure and create the SES client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create SES client");

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public EmailProvider getProvider()
    {
        return EmailProvider.SES;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring SES client");

        String directory = System.getProperty("opsmatters.auth", ".");

        File auth = new File(directory, AUTH);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(auth, "UTF-8"));
            setAccessKeyId(obj.optString("accessKeyId"));
            setSecretAccessKey(obj.optString("secretAccessKey"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read SES access token: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured SES client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating SES client");

        // Get the credentials object
        AWSCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        // Create the client
        AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(System.getProperty("opsmatters.ses.region"))
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();

        // Issue command to test connectivity
        sesClient.listIdentities();

        client = sesClient;

        if(debug())
            logger.info("Created SES client successfully");

        return isConnected();
    }

    /**
     * Returns the accessKeyId for the client.
     */
    public String getAccessKeyId() 
    {
        return accessKeyId;
    }

    /**
     * Sets the accessKeyId for the client.
     */
    public void setAccessKeyId(String accessKeyId) 
    {
        this.accessKeyId = accessKeyId;
    }

    /**
     * Returns the secretAccessKey for the client.
     */
    public String getSecretAccessKey() 
    {
        return secretAccessKey;
    }

    /**
     * Sets the secretAccessKey for the client.
     */
    public void setSecretAccessKey(String secretAccessKey) 
    {
        this.secretAccessKey = secretAccessKey;
    }

    /**
     * Returns <CODE>true</CODE> if the client is connected.
     */
    public boolean isConnected() 
    {
        return client != null;
    }

    /**
     * Sends the given email using SES.
     */
    public String sendEmail(Email email) throws IOException
    {
        String ret = null;

        // Set the email body using the correct format
        Body body = new Body();
        Content content = new Content().withCharset("UTF-8").withData(email.getBody());
        body = email.getFormat() == EmailFormat.HTML ? body.withHtml(content) : body.withText(content);

        SendEmailRequest request = new SendEmailRequest()
          .withSource(email.getFrom())
          .withDestination(new Destination().withToAddresses(email.getRecipients()))
          .withMessage(new Message()
              .withSubject(new Content().withCharset("UTF-8").withData(email.getSubject()))
              .withBody(body));
        return client.sendEmail(request).getMessageId();
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        client.shutdown();
        client = null;
    }

    /**
     * Returns a builder for the client.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make client construction easier.
     */
    public static class Builder
    {
        private SesClient client = new SesClient();

        /**
         * Sets the accessKeyId for the client.
         * @param accessKeyId The accessKeyId for the client
         * @return This object
         */
        public Builder accessKeyId(String accessKeyId)
        {
            client.setAccessKeyId(accessKeyId);
            return this;
        }

        /**
         * Sets the secretAccessKey for the client.
         * @param secretAccessKey The secretAccessKey for the client
         * @return This object
         */
        public Builder secretAccessKey(String secretAccessKey)
        {
            client.setSecretAccessKey(secretAccessKey);
            return this;
        }

        /**
         * Returns the configured client instance
         * @return The client instance
         */
        public SesClient build()
        {
            return client;
        }
    }
}