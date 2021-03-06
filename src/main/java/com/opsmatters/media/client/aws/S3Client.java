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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.platform.aws.S3Settings;

/**
 * Class that represents a connection to S3 buckets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class S3Client extends Client
{
    private static final Logger logger = Logger.getLogger(S3Client.class.getName());

    public static final String SUFFIX = ".s3";

    private AmazonS3 client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";
    private String bucket = "";

    /**
     * Returns a new S3 client using the S3 settings.
     */
    static public S3Client newClient(S3Settings settings) throws IOException
    {
        S3Client ret = S3Client.builder()
            .region(settings.getRegion())
            .build();

        // Configure and create the S3 client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create S3 client: "+ret.getRegion());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring S3 client: "+getRegion());

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
            logger.severe("Unable to read s3 auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured S3 client successfully: "+getRegion());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() 
    {
        if(debug())
            logger.info("Creating S3 client: "+getRegion());

        // Get the client configuration
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTPS);

        // Get the credentials object
        AWSCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        // Create the client
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        if(credentials != null)
            builder = builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        AmazonS3 s3client = builder.withClientConfiguration(config)
            .withRegion(region)
            .build();

        // Issue command to test connectivity
        s3client.getS3AccountOwner();

        client = s3client;

        if(debug())
            logger.info("Created S3 client successfully: "+getRegion());

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
     * Returns <CODE>true</CODE> if the given bucket exists.
     */
    public boolean bucketExists(String bucket) 
    {
        boolean ret = false;
        if(isConnected() && bucket.length() > 0)
            ret = client.doesBucketExist(bucket);
        return ret;
    }

    /**
     * Change to the given bucket.
     */
    public boolean changeBucket(String bucket) 
    {
        boolean ret = bucketExists(bucket);
        if(ret)
            this.bucket = bucket;
        return ret;
    }

    /**
     * Returns the current bucket.
     */
    public String getBucket()
    {
        return bucket;
    }

    /**
     * Creates the given bucket on S3.
     */
    public boolean createBucket(String bucket)
    {
        boolean ret = false;

        if(isConnected() && bucket.length() > 0)
        {
            client.createBucket(bucket);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given file exists in the current directory.
     */
    public boolean exists(String filename)
    {
        boolean ret = false;

        try
        {
            ret = client.getObjectMetadata(bucket, filename) != null;
        }
        catch(AmazonS3Exception e)
        {
            if(e.getStatusCode() == 404) // Not Found
                ret = false;
            else
                throw e;
        }

        return ret;
    }

    /**
     * Returns an input stream for the given file on S3.
     */
    public InputStream get(String filename)
    {
        InputStream ret = null;

        if(isConnected())
        {
            S3Object object = client.getObject(bucket, filename);
            if(object != null)
                ret = object.getObjectContent();
        }

        return ret;
    }

    /**
     * Write the given file to S3.
     */
    public boolean put(InputStream stream, String filename, long size)
    {
        return put(stream, filename, bucket, size);
    }

    /**
     * Write the given file to S3.
     */
    public boolean put(InputStream stream, String filename, String bucket, long size)
    {
        boolean ret = false;

        if(isConnected())
        {
            ObjectMetadata metadata = new ObjectMetadata();
            if(size > 0L)
                metadata.setContentLength(size);
            PutObjectResult result = client.putObject(bucket, filename, stream, metadata);
            ret = result != null;
        }

        return ret;
    }

    /**
     * Move the given file from the current bucket to the target bucket in S3.
     */
    public boolean move(String filename, String targetBucket)
    {
        boolean ret = put(get(filename), filename, targetBucket, getSize(filename));
        delete(filename);
        return ret;
    }

    /**
     * Delete the given file from S3.
     */
    public void delete(String filename)
    {
        client.deleteObject(bucket, filename);
    }

    /**
     * Returns the size (in bytes) of the given file.
     */
    public long getSize(String filename)
    {
        long ret = -1L;

        if(isConnected())
        {
            ObjectMetadata object = client.getObjectMetadata(bucket, filename);
            if(object != null)
                ret = object.getInstanceLength();
        }

        return ret;
    }

    /**
     * Get the file with the given name and download it to the given local output file.
     */
    public boolean getFile(InputStream is, File file) throws IOException
    {
        boolean ret = false;

        // Download the file
        FileOutputStream os = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        if(is != null)
        {
            int buffer = 4096;
            os = new FileOutputStream(file, false);
            bis = new BufferedInputStream(is, buffer);
            bos = new BufferedOutputStream(os, buffer);
            byte[] array = new byte[buffer];

            int len = 0;
            int bytes = 0;
            while(len != -1)
            {
                len = bis.read(array);
                if(len != -1)
                {
                    bos.write(array, 0, len);
                    bytes += len;
                }
            }

            ret = true;
        }

        try
        {
            // Close the input stream
            if(bis != null)
                bis.close();
        }
        catch(IOException e)
        {
        }

        try
        {
            // Close the input stream
            if(is != null)
                is.close();
        }
        catch(IOException e)
        {
        }

        try
        {
            // Close the output stream
            if(bos != null)
            {
                bos.flush();
                bos.close();
            }
        }
        catch(IOException e)
        {
        }

        try
        {
            // Close the output stream
            if(os != null)
            {
                os.flush();
                os.close();
            }
        }
        catch(IOException e)
        {
        }

        return ret;
    }

    /**
     * Return a list of files from the given bucket.
     */
    public List<S3ObjectSummary> listFiles(String bucket)
    {
        List<S3ObjectSummary> ret = new ArrayList<S3ObjectSummary>();

        if(bucket == null || bucket.length() == 0 || bucket.equals("."))
            bucket = this.bucket;

        ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucket);
        ObjectListing listing;            
        do 
        {
            listing = client.listObjects(request);
            for(S3ObjectSummary summary : listing.getObjectSummaries()) 
            {
                ret.add(summary);
            }
            request.setMarker(listing.getNextMarker());
        } while(listing.isTruncated());

        return ret;
    }

    /**
     * Return a list of files from the current bucket.
     */
    public List<S3ObjectSummary> listFiles()
    {
        return listFiles(this.bucket);
    }

    /**
     * Download the files from the given S3 bucket into the given directory.
     */
    public List<S3ObjectSummary> downloadFiles(String bucket, String directory, String ext) throws IOException
    {
        List<S3ObjectSummary> ret = new ArrayList<S3ObjectSummary>();
        if(changeBucket(bucket)) 
        {
            List<S3ObjectSummary> items = listFiles(bucket);
            for(S3ObjectSummary item : items)
            {
                if(ext != null && !item.getKey().endsWith(ext))
                    continue;

                File file = new File(directory, item.getKey());
                if(!file.exists() || file.lastModified() < item.getLastModified().getTime())
                {
                    if(getFile(get(file.getName()), file))
                    {
                        ret.add(item);
                        logger.info("Downloaded config file: "+file.getName());
                    }
                }
            }
        }
        else
        {
            logger.warning("Bucket does not exist: "+bucket);
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
        private S3Client client = new S3Client();

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
        public S3Client build()
        {
            return client;
        }
    }
}