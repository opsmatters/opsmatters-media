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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsClientBuilder;
import software.amazon.awssdk.services.rds.model.RdsException;
import software.amazon.awssdk.services.rds.model.DescribeDbClustersRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbClustersResponse;
import software.amazon.awssdk.services.rds.model.StartDbClusterRequest;
import software.amazon.awssdk.services.rds.model.StartDbClusterResponse;
import software.amazon.awssdk.services.rds.model.StopDbClusterRequest;
import software.amazon.awssdk.services.rds.model.StopDbClusterResponse;
import software.amazon.awssdk.services.rds.model.DBCluster;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.platform.EnvironmentStatus;
import com.opsmatters.media.model.platform.aws.RdsConfig;

/**
 * Class that represents a connection to AWS RDS databases.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AwsRdsClient extends Client
{
    private static final Logger logger = Logger.getLogger(AwsRdsClient.class.getName());

    public static final String SUFFIX = ".rds";

    private RdsClient client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";

    /**
     * Returns a new AWS RDS client using the RDS configuration.
     */
    static public AwsRdsClient newClient(RdsConfig config) throws IOException
    {
        AwsRdsClient ret = AwsRdsClient.builder()
            .region(config.getRegion())
            .build();

        // Configure and create the RDS client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create RDS client: "+ret.getRegion());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring RDS client: "+getRegion());

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
            logger.severe("Unable to read RDS auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured RDS client successfully: "+getRegion());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() 
    {
        if(debug())
            logger.info("Creating RDS client: "+getRegion());

        // Get the client configuration
        ClientOverrideConfiguration config = ClientOverrideConfiguration.builder()
            .build();

        // Get the credentials object
        AwsBasicCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // Create the client
        RdsClientBuilder builder = RdsClient.builder();
        if(credentials != null)
            builder = builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        RdsClient client = builder.overrideConfiguration(config)
            .region(Region.of(region))
            .build();

        // Issue command to test connectivity
        client.describeDBClusters();

        this.client = client;

        if(debug())
            logger.info("Created RDS client successfully: "+getRegion());

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
     * Returns the details of the given RDS cluster.
     */
    public DBCluster describeCluster(String clusterId)
    {
        DBCluster ret = null;

        try
        {
            DescribeDbClustersRequest request = DescribeDbClustersRequest.builder()
                .dbClusterIdentifier(clusterId)
                .build();
            DescribeDbClustersResponse response = client.describeDBClusters(request);
            List<DBCluster> clusters = response.dbClusters();
            if(clusters.size() > 0)
                ret = clusters.get(0);
        }
        catch(RdsException e)
        {
            if(e.statusCode() != 404) // Not Found
                throw e;
        }

        return ret;
    }

    /**
     * Returns the status of the given RDS cluster.
     */
    public EnvironmentStatus getStatus(String clusterId)
    {
        EnvironmentStatus ret = EnvironmentStatus.UNKNOWN;
        DBCluster cluster = describeCluster(clusterId);
        if(cluster != null)
        {
            String status = cluster.status();
            if(status.equals("starting"))
                ret = EnvironmentStatus.STARTING;
            else if(status.equals("available"))
                ret = EnvironmentStatus.RUNNING;
            else if(status.equals("stopping"))
                ret = EnvironmentStatus.STOPPING;
            else
                ret = EnvironmentStatus.STOPPED;
        }

        return ret;
    }

    /**
     * Starts the given RDS cluster.
     */
    public boolean startCluster(String clusterId)
    {
        boolean ret = false;

        try
        {
            StartDbClusterRequest request = StartDbClusterRequest.builder()
                .dbClusterIdentifier(clusterId)
                .build();
            StartDbClusterResponse response = client.startDBCluster(request);
            logger.info("Starting cluster "+response.dbCluster().dbClusterIdentifier());
            ret = true;
        }
        catch(RdsException e)
        {
            if(e.statusCode() == 404) // Not Found
                ret = false;
            else
                throw e;
        }

        return ret;
    }

    /**
     * Stops the given RDS cluster.
     */
    public boolean stopCluster(String clusterId)
    {
        boolean ret = false;

        try
        {
            StopDbClusterRequest request = StopDbClusterRequest.builder()
                .dbClusterIdentifier(clusterId)
                .build();
            StopDbClusterResponse response = client.stopDBCluster(request);
            logger.info("Stopping cluster "+response.dbCluster().dbClusterIdentifier());
            ret = true;
        }
        catch(RdsException e)
        {
            if(e.statusCode() == 400      // Invalid State
                || e.statusCode() == 404) // Not Found
            {
                ret = false;
            }
            else
            {
                throw e;
            }
        }

        return ret;
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
        private AwsRdsClient client = new AwsRdsClient();

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
        public AwsRdsClient build()
        {
            return client;
        }
    }
}