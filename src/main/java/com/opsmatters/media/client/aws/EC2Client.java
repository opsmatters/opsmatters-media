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
package com.opsmatters.media.client.aws;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Instance;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.platform.aws.EC2Settings;

/**
 * Class that represents a connection to EC2 servers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EC2Client extends Client
{
    private static final Logger logger = Logger.getLogger(EC2Client.class.getName());

    public static final String SUFFIX = ".ec2";

    private AmazonEC2 client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";

    /**
     * Returns a new EC2 client using the EC2 settings.
     */
    static public EC2Client newClient(EC2Settings settings) throws IOException
    {
        EC2Client ret = EC2Client.builder()
            .region(settings.getRegion())
            .build();

        // Configure and create the EC2 client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create EC2 client: "+ret.getRegion());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring EC2 client: "+getRegion());

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
            logger.severe("Unable to read EC2 auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured EC2 client successfully: "+getRegion());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() 
    {
        if(debug())
            logger.info("Creating EC2 client: "+getRegion());

        // Get the client configuration
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTPS);

        // Get the credentials object
        AWSCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        // Create the client
        AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard();
        if(credentials != null)
            builder = builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        AmazonEC2 ec2client = builder.withClientConfiguration(config)
            .withRegion(region)
            .build();

        // Issue command to test connectivity
        ec2client.describeHosts();

        client = ec2client;

        if(debug())
            logger.info("Created EC2 client successfully: "+getRegion());

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
     * Returns a list containing the given instance id.
     */
    public List<String> toList(String instanceId)
    {
        List<String> instances = new ArrayList<String>();
        instances.add(instanceId);
        return instances;
    }

    /**
     * Returns the details of the given EC2 instances.
     */
    public List<Instance> describeInstances(List<String> instanceIds)
    {
        List<Instance> ret = new ArrayList<Instance>();

        try
        {
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request.setInstanceIds(instanceIds);
            DescribeInstancesResult result = client.describeInstances(request);
            for(Reservation reservation : result.getReservations())
            {
                for(Instance instance : reservation.getInstances())
                    ret.add(instance);
            }
        }
        catch(AmazonEC2Exception e)
        {
            if(e.getStatusCode() != 404) // Not Found
                throw e;
        }

        return ret;
    }

    /**
     * Returns the details of the given EC2 instance.
     */
    public Instance describeInstance(String instanceId)
    {
        List<Instance> instances = describeInstances(toList(instanceId));
        return instances.size() > 0 ? instances.get(0) : null;
    }

    /**
     * Starts the given EC2 instances.
     */
    public boolean startInstances(List<String> instanceIds)
    {
        boolean ret = false;

        try
        {
            StartInstancesResult result = client.startInstances(new StartInstancesRequest(instanceIds));
            logger.info("Starting instances "+result.getStartingInstances());
            ret = true;
        }
        catch(AmazonEC2Exception e)
        {
            if(e.getStatusCode() == 404) // Not Found
                ret = false;
            else
                throw e;
        }

        return ret;
    }

    /**
     * Starts the given EC2 instance.
     */
    public boolean startInstance(String instanceId)
    {
        return startInstances(toList(instanceId));
    }

    /**
     * Stops the given EC2 instances.
     */
    public boolean stopInstances(List<String> instanceIds)
    {
        boolean ret = false;

        try
        {
            StopInstancesResult result = client.stopInstances(new StopInstancesRequest(instanceIds));
            logger.info("Stopping instances "+result.getStoppingInstances());
            ret = true;
        }
        catch(AmazonEC2Exception e)
        {
            if(e.getStatusCode() == 404) // Not Found
                ret = false;
            else
                throw e;
        }

        return ret;
    }

    /**
     * Stops the given EC2 instance.
     */
    public boolean stopInstance(String instanceId)
    {
        return stopInstances(toList(instanceId));
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
        private EC2Client client = new EC2Client();

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
        public EC2Client build()
        {
            return client;
        }
    }
}