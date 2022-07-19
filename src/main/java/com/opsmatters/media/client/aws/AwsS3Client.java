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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.platform.aws.S3Settings;

/**
 * Class that represents a connection to AWS S3 buckets.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AwsS3Client extends Client
{
    private static final Logger logger = Logger.getLogger(AwsS3Client.class.getName());

    public static final String SUFFIX = ".s3";

    private S3Client client = null;
    private String region;
    private String accessKeyId = "";
    private String secretAccessKey = "";
    private String bucket = "";

    /**
     * Returns a new AWS S3 client using the S3 settings.
     */
    static public AwsS3Client newClient(S3Settings settings) throws IOException
    {
        AwsS3Client ret = AwsS3Client.builder()
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
        ClientOverrideConfiguration config = ClientOverrideConfiguration.builder()
            .build();

        // Get the credentials object
        AwsBasicCredentials credentials = null;
        if(accessKeyId != null && accessKeyId.length() > 0)
            credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // Create the client
        S3ClientBuilder builder = S3Client.builder();
        if(credentials != null)
            builder = builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        S3Client client = builder.overrideConfiguration(config)
            .region(Region.of(region))
            .build();

        // Issue command to test connectivity
        client.listBuckets();

        this.client = client;

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
        {
            try
            {
                HeadBucketRequest request = HeadBucketRequest.builder()
                    .bucket(bucket)
                    .build();
                client.headBucket(request);
                ret = true;
            }
            catch(S3Exception e)
            {
                if(e.statusCode() != 404) // Not Found
                    throw e;
            }
        }

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
            CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucket).build();
            CreateBucketResponse response = client.createBucket(request);
            ret = response.location().length() > 0;
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given file exists in the current bucket.
     */
    public boolean exists(String filename)
    {
        boolean ret = false;

        try
        {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();
            client.headObject(request);
            ret = true;
        }
        catch(S3Exception e)
        {
            if(e.statusCode() != 404) // Not Found
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
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();
            ret = client.getObject(request);
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
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();
            PutObjectResponse response = client.putObject(request, RequestBody.fromInputStream(stream, size));
            ret = response != null;
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
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(filename)
            .build();
        client.deleteObject(request);
    }

    /**
     * Returns the size (in bytes) of the given file.
     */
    public long getSize(String filename)
    {
        long ret = -1L;

        if(isConnected())
        {
            GetObjectAttributesRequest request = GetObjectAttributesRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();
            GetObjectAttributesResponse response = client.getObjectAttributes(request);
            ret = response.objectSize();
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
    public List<S3Object> listFiles(String bucket)
    {
        List<S3Object> ret = new ArrayList<S3Object>();

        if(bucket == null || bucket.length() == 0 || bucket.equals("."))
            bucket = this.bucket;

        ListObjectsRequest request = ListObjectsRequest.builder()
            .bucket(bucket)
            .build();

        ListObjectsResponse response;

        do 
        {
            response = client.listObjects(request);
            for(S3Object object : response.contents()) 
            {
                ret.add(object);
            }

            request = ListObjectsRequest.builder()
                .bucket(bucket)
                .marker(response.nextMarker())
                .build();
        } while(response.isTruncated());

        return ret;
    }

    /**
     * Return a list of files from the current bucket.
     */
    public List<S3Object> listFiles()
    {
        return listFiles(this.bucket);
    }

    /**
     * Download the files from the given S3 bucket into the given directory.
     */
    public List<S3Object> downloadFiles(String bucket, String directory, String ext) throws IOException
    {
        List<S3Object> ret = new ArrayList<S3Object>();

        if(changeBucket(bucket)) 
        {
            List<S3Object> items = listFiles(bucket);
            for(S3Object item : items)
            {
                if(ext != null && !item.key().endsWith(ext))
                    continue;

                File file = new File(directory, item.key());
                if(!file.exists() || file.lastModified() < item.lastModified().toEpochMilli())
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
        private AwsS3Client client = new AwsS3Client();

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
        public AwsS3Client build()
        {
            return client;
        }
    }
}