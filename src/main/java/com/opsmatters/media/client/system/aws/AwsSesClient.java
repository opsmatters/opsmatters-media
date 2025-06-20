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
package com.opsmatters.media.client.system.aws;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.SesClientBuilder;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Body;
import com.opsmatters.media.model.admin.EmailProviderId;
import com.opsmatters.media.model.admin.Email;
import com.opsmatters.media.model.admin.EmailFormat;
import com.opsmatters.media.model.system.aws.SesConfig;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.client.system.EmailClient;

/**
 * Class that represents a connection to AWS SES for emails.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AwsSesClient extends Client implements EmailClient
{
    private static final Logger logger = Logger.getLogger(AwsSesClient.class.getName());

    public static final String SUFFIX = ".ses";

    private SesClient client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";

    /**
     * Returns a new AWS SES client using SES configuration.
     */
    static public AwsSesClient newClient(SesConfig config) throws IOException
    {
        AwsSesClient ret = AwsSesClient.builder()
            .region(config.getRegion())
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
    public EmailProviderId getProviderId()
    {
        return EmailProviderId.SES;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring SES client");

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
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

        // Get the client configuration
        ClientOverrideConfiguration config = ClientOverrideConfiguration.builder()
            .build();

        // Get the credentials object
        AwsBasicCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // Create the client
        SesClientBuilder builder = SesClient.builder();
        if(credentials != null)
            builder = builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        SesClient client = builder.overrideConfiguration(config)
            .region(Region.of(region))
            .build();

        // Issue command to test connectivity
        client.listIdentities();

        this.client = client;

        if(debug())
            logger.info("Created SES client successfully");

        return isConnected();
    }

    /**
     * Returns the region for the client.
     */
    public String getRegion() 
    {
        return region;
    }

    /**
     * Sets the region for the client.
     */
    public void setRegion(String region) 
    {
        this.region = region;
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

        // Get the subject
        Content subject = Content.builder()
            .charset("UTF-8")
            .data(email.getSubject())
            .build();

        // Get the recipients
        Destination recipients = Destination.builder()
            .toAddresses(email.getRecipients())
            .build();

        // Set the email body using the correct format
        Content content = Content.builder()
            .charset("UTF-8")
            .data(email.getBody())
            .build();

        Body body = null;
        if(email.getFormat() == EmailFormat.HTML)
        {
            body = Body.builder()
                .html(content)
                .build();
        }
        else
        {
            body = Body.builder()
                .text(content)
                .build();
        }

        SendEmailRequest request = SendEmailRequest.builder()
          .source(email.getFrom())
          .destination(recipients)
          .message(Message.builder()
              .subject(subject)
              .body(body)
              .build())
          .build();

        SendEmailResponse response = client.sendEmail(request);
        return response.messageId();
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        client.close();
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
        private AwsSesClient client = new AwsSesClient();

        /**
         * Sets the region for the client.
         * @param region The region for the client
         * @return This object
         */
        public Builder region(String region)
        {
            client.setRegion(region);
            return this;
        }

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
        public AwsSesClient build()
        {
            return client;
        }
    }
}