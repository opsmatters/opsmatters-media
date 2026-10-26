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

package com.opsmatters.media.client.drupal;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.time.Instant;
import java.time.Duration;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.system.SiteEnvironment;
import com.opsmatters.media.model.content.feed.ContentFeed;
import com.opsmatters.media.model.content.feed.FeedImport;
import com.opsmatters.media.model.content.feed.FeedDelete;
import com.opsmatters.media.model.content.feed.FeedStatus;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.client.RestClient;

/**
 * Executes Drupal Feeds API calls using a REST client.
 */
public class FeedsClient extends RestClient
{
    private static final Logger logger = Logger.getLogger(FeedsClient.class.getName());

    public static final String SUFFIX = ".feeds";

    /**
     * Returns a new client using the given config.
     */
    public static FeedsClient newClient(SiteEnvironment environment) throws IOException
    {
        FeedsClient ret = FeedsClient._builder()
            .env(environment.getKey())
            .url(environment.getUrl())
            .build();

        // Configure and create the feeds client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create feeds client");

        return ret;
    }

    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring feeds client: "+getUrl());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, getEnv()+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setUsername(obj.optString("username"));
            setPassword(obj.optString("password"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read feeds auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured feeds client successfully: "+getUrl());
    }

    /**
     * Import the content files for the given feed.
     */
    public FeedImport importContent(ContentFeed feed) throws IOException
    {
        FeedImport ret = new FeedImport(feed);

        Instant now = Instant.now();
        String env = feed.getEnvironment().code();
        String url = String.format("%s%s/%s",
            getUrl(), System.getProperty("app.api.feeds.import"), feed.getExternalId());

        try
        {
            // Execute the API call
            String str = post(url, "application/json");

            // Get the returned values
            String message = "";
            if(str.startsWith("{")) // Valid JSON
            {
                JSONObject obj = new JSONObject(str);
                long imported = obj.optLong("imported", 0L);
                int items = obj.optInt("items", -1);
                int created = obj.optInt("created", -1);
                int updated = obj.optInt("updated", -1);
                int failed = obj.optInt("failed", -1);
                message = obj.optString("message");

                // Update the feed and import with the results
                if(imported > 0L)
                    feed.setExecutedDateMillis(imported*1000L);
                if(items >= 0)
                    feed.setItemCount(items);
                if(created >= 0)
                    ret.setCreatedCount(created);
                if(updated >= 0)
                    ret.setUpdatedCount(updated);
                if(failed >= 0)
                    ret.setFailedCount(failed);

                // Set the status and error message based on the results
                if(failed == 0)
                {
                    feed.setStatus(FeedStatus.COMPLETED);
                    logger.info(String.format("%s %s import successful: %s %s",
                        env, feed.getName(), getStatusLine(), obj));
                }
                else
                {
                    if(failed > 0)
                    {
                        message = String.format("created=%d updated=%d failed=%d",
                            created, updated, failed);
                    }

                    feed.setStatus(FeedStatus.ERROR);
                    logger.severe(String.format("%s %s import failed: %s %s",
                        env, feed.getName(), getStatusLine(), message));
                }
            }
            else
            {
                feed.setStatus(FeedStatus.ERROR);
                message = "Invalid JSON response";
                logger.severe(String.format("%s %s import failed: %s: %s",
                    env, feed.getName(), message, str));
            }

            ret.setErrorMessage(message);
            ret.setExecutionTime(Duration.between(now, Instant.now()).toMillis());
            feed.setUpdatedDate(Instant.now());
        }
        catch(IOException e)
        {
            feed.setStatus(FeedStatus.ERROR);
            ret.setErrorMessage(e.getClass().getName()+": "+e.getMessage());
            logger.severe(String.format("%s %s import failed: %s",
                env, feed.getName(), e.getMessage()));
            throw e;
        }

        return ret;
    }

    /**
     * Delete nodes and files using the criteria in the given object.
     */
    private FeedDelete delete(JSONObject body) throws IOException
    {
        FeedDelete ret = new FeedDelete();

        String url = String.format("%s%s",
            getUrl(), System.getProperty("app.api.feeds.delete"));

        // Execute the API call
        String str = post(url, "application/json", body.toString());

        // Get the returned values
        String message = "";
        if(str.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(str);
            int nodes_found = obj.optInt("nodes_found", -1);
            int nodes_deleted = obj.optInt("nodes_deleted", -1);
            int files_found = obj.optInt("files_found", -1);
            int files_deleted = obj.optInt("files_deleted", -1);
            message = obj.optString("message", "");

            // Update the return object with the results
            if(nodes_found >= 0)
                ret.setNodesFoundCount(nodes_found);
            if(nodes_deleted >= 0)
                ret.setNodesDeletedCount(nodes_deleted);
            if(files_found >= 0)
                ret.setFilesFoundCount(files_found);
            if(files_deleted >= 0)
                ret.setFilesDeletedCount(files_deleted);
            ret.setValid(true);
        }
        else
        {
            message = "Invalid JSON response";
        }

        ret.setResponse(str);
        ret.setErrorMessage(message);

        return ret;
    }

    /**
     * Delete the node with the given id.
     */
    public FeedDelete deleteNode(int nodeId) throws IOException
    {
        JSONObject body = new JSONObject();
        body.put("nid", nodeId);
        return delete(body);
    }

    /**
     * Delete the node with the given alias.
     */
    public FeedDelete deleteNode(String alias) throws IOException
    {
        JSONObject body = new JSONObject();
        body.put("alias", alias);
        return delete(body);
    }

    /**
     * Delete the file with the given filename.
     */
    public FeedDelete deleteFile(String filename) throws IOException
    {
        JSONObject body = new JSONObject();
        body.put("path", System.getProperty("app.path.files.images").substring(1));
        body.put("filename", filename);
        return delete(body);
    }

    /**
     * Delete the files with the given prefix.
     */
    public FeedDelete deleteFiles(String prefix) throws IOException
    {
        JSONObject body = new JSONObject();
        body.put("path", System.getProperty("app.path.files.images").substring(1));
        body.put("prefix", prefix);
        return delete(body);
    }

    /**
     * Delete the nodes and files for the given organisation and prefix.
     */
    public FeedDelete deleteFiles(Organisation organisation, String prefix) throws IOException
    {
        JSONObject body = new JSONObject();
        body.put("field", "field_organisation");
        body.put("vocabulary", "organisation");
        body.put("term", organisation.getName());
        body.put("path", System.getProperty("app.path.files.images").substring(1));
        body.put("prefix", prefix);
        return delete(body);
    }

    /**
     * Returns a builder for the client.
     * @return The builder instance.
     */
    public static Builder _builder()
    {
        return new Builder();
    }

    /**
     * Builder to make client construction easier.
     */
    public static class Builder
    {
        private FeedsClient client = new FeedsClient();

        /**
         * Sets the base url for the client.
         * @param url The base url for the client
         * @return This object
         */
        public Builder url(String url)
        {
            client.setUrl(url);
            return this;
        }

        /**
         * Sets the env for the client.
         * @param env The env for the client
         * @return This object
         */
        public Builder env(String env)
        {
            client.setEnv(env);
            return this;
        }

        /**
         * Returns the configured client instance
         * @return The client instance
         */
        public FeedsClient build()
        {
            return client;
        }
    }
}