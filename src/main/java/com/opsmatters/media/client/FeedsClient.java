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

package com.opsmatters.media.client;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * Executes Feeds API calls using a REST client.
 */
public class FeedsClient extends RestClient
{
    private static final Logger logger = Logger.getLogger(FeedsClient.class.getName());

    public static final String SUFFIX = ".feeds";

    /**
     * Returns a new client using the given config.
     */
    static public FeedsClient newClient(String key, String url) 
        throws IOException
    {
        FeedsClient ret = FeedsClient._builder()
            .env(key)
            .url(url)
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