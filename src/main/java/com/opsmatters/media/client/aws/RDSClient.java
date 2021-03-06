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
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.AmazonRDSException;
import com.amazonaws.services.rds.model.DescribeDBClustersRequest;
import com.amazonaws.services.rds.model.DescribeDBClustersResult;
import com.amazonaws.services.rds.model.StartDBClusterRequest;
import com.amazonaws.services.rds.model.StopDBClusterRequest;
import com.amazonaws.services.rds.model.DBCluster;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.platform.EnvironmentStatus;
import com.opsmatters.media.model.platform.aws.RDSSettings;

/**
 * Class that represents a connection to RDS databases.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RDSClient extends Client
{
    private static final Logger logger = Logger.getLogger(RDSClient.class.getName());

    public static final String SUFFIX = ".rds";

    private AmazonRDS client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";

    /**
     * Returns a new RDS client using the RDS settings.
     */
    static public RDSClient newClient(RDSSettings settings) throws IOException
    {
        RDSClient ret = RDSClient.builder()
            .region(settings.getRegion())
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
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTPS);

        // Get the credentials object
        AWSCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        // Create the client
        AmazonRDSClientBuilder builder = AmazonRDSClientBuilder.standard();
        if(credentials != null)
            builder = builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        AmazonRDS rdsclient = builder.withClientConfiguration(config)
            .withRegion(region)
            .build();

        // Issue command to test connectivity
        rdsclient.describeDBClusters();

        client = rdsclient;

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
            DescribeDBClustersRequest request = new DescribeDBClustersRequest().withDBClusterIdentifier(clusterId);
            DescribeDBClustersResult result = client.describeDBClusters(request);
            List<DBCluster> clusters = result.getDBClusters();
            if(clusters.size() > 0)
                ret = clusters.get(0);
        }
        catch(AmazonRDSException e)
        {
            if(e.getStatusCode() != 404) // Not Found
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
            String status = cluster.getStatus();
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
            StartDBClusterRequest request = new StartDBClusterRequest().withDBClusterIdentifier(clusterId);
            DBCluster result = client.startDBCluster(request);
            logger.info("Starting cluster "+result.getDBClusterIdentifier());
            ret = true;
        }
        catch(AmazonRDSException e)
        {
            if(e.getStatusCode() == 404) // Not Found
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
            StopDBClusterRequest request = new StopDBClusterRequest().withDBClusterIdentifier(clusterId);
            DBCluster result = client.stopDBCluster(request);
            logger.info("Stopping cluster "+result.getDBClusterIdentifier());
            ret = true;
        }
        catch(AmazonRDSException e)
        {
            if(e.getStatusCode() == 404) // Not Found
                ret = false;
            else
                throw e;
        }

        return ret;
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
        private RDSClient client = new RDSClient();

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
        public RDSClient build()
        {
            return client;
        }
    }
}