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
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Instance;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.system.EnvironmentId;
import com.opsmatters.media.model.system.aws.Ec2Config;
import com.opsmatters.media.model.system.aws.InstanceStatus;

import static com.opsmatters.media.model.system.aws.InstanceStatus.*;

/**
 * Class that represents a connection to AWS EC2 servers.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AwsEc2Client extends Client
{
    private static final Logger logger = Logger.getLogger(AwsEc2Client.class.getName());

    public static final String SUFFIX = ".ec2";
    public static final int MIN_UPTIME = 20000;

    private Ec2Client client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";

    /**
     * Returns a new AWS EC2 client using the EC2 configuration.
     */
    static public AwsEc2Client newClient(Ec2Config config) throws IOException
    {
        AwsEc2Client ret = AwsEc2Client.builder()
            .region(config.getRegion())
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
        ClientOverrideConfiguration config = ClientOverrideConfiguration.builder()
            .build();

        // Get the credentials object
        AwsBasicCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // Create the client
        Ec2ClientBuilder builder = Ec2Client.builder();
        if(credentials != null)
            builder = builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        Ec2Client client = builder.overrideConfiguration(config)
            .region(Region.of(region))
            .build();

        // Issue command to test connectivity
        client.describeHosts();

        this.client = client;

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
            DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();
            DescribeInstancesResponse response = client.describeInstances(request);
            for(Reservation reservation : response.reservations())
            {
                for(Instance instance : reservation.instances())
                    ret.add(instance);
            }
        }
        catch(Ec2Exception e)
        {
            if(e.statusCode() != 404) // Not Found
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
     * Returns the status of the given EC2 instance.
     */
    public InstanceStatus getStatus(String instanceId)
    {
        InstanceStatus ret = UNKNOWN;
        Instance instance = describeInstance(instanceId);
        if(instance != null)
        {
            int state = instance.state().code();
            if(state == 0)
            {
                ret = STARTING;
            }
            else if(state == 16)
            {
                long uptime = Duration.between(instance.launchTime(), Instant.now()).toMillis();
                // Check the server has been up for at least 20s to prevent errors
                if(uptime < MIN_UPTIME)
                {
                    ret = STARTING;
                }
                else
                {
                    ret = RUNNING;
                }
            }
            else if(state == 64 || state == 32) // stopping or shutting-down
            {
                ret = STOPPING;
            }
            else if(state == 80 || state == 48) // stopped or terminated
            {
                ret = STOPPED;
            }

            if(ret == UNKNOWN)
                logger.warning("EC2 instance has unknown state: "+state);
        }

        return ret;
    }

    /**
     * Starts the given EC2 instances.
     */
    public boolean startInstances(List<String> instanceIds)
    {
        boolean ret = false;

        try
        {
            StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();
            StartInstancesResponse response = client.startInstances(request);
            logger.info("Starting instances "+response.startingInstances());
            ret = true;
        }
        catch(Ec2Exception e)
        {
            if(e.statusCode() == 404) // Not Found
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
            StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();
            StopInstancesResponse response = client.stopInstances(request);
            logger.info("Stopping instances "+response.stoppingInstances());
            ret = true;
        }
        catch(Ec2Exception e)
        {
            if(e.statusCode() == 404) // Not Found
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
        private AwsEc2Client client = new AwsEc2Client();

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
        public AwsEc2Client build()
        {
            return client;
        }
    }
}