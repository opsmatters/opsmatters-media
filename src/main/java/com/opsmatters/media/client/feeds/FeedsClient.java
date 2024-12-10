package com.opsmatters.media.client.feeds;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import com.opsmatters.media.model.platform.FeedsConfig;
import com.opsmatters.media.client.ApiClient;

/**
 * Executes Feeds API calls using a http client.
 */
public class FeedsClient extends ApiClient
{
    private static final Logger logger = Logger.getLogger(FeedsClient.class.getName());

    public static final String SUFFIX = ".feeds";

    /**
     * Returns a new client using the given config.
     */
    static public FeedsClient newClient(String key, String url, FeedsConfig config) 
        throws IOException
    {
        FeedsClient ret = FeedsClient._builder()
            .env(key)
            .url(url)
            .username(config.getUsername())
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
            // Read password from auth directory
            setPassword(FileUtils.readFileToString(file, "UTF-8"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read feeds password file: "+e.getClass().getName()+": "+e.getMessage());
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
         * Sets the username for the client.
         * @param username The username for the client
         * @return This object
         */
        public Builder username(String username)
        {
            client.setUsername(username);
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