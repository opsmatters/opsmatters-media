package com.opsmatters.media.client;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.HttpClient;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.FileUtils;
import com.opsmatters.media.model.platform.FeedsConfig;

/**
 * Executes API calls using a http client.
 */
public class ApiClient extends Client
{
    private static final Logger logger = Logger.getLogger(ApiClient.class.getName());

    private String env = "";
    private String url = null;
    private String suffix = null;
    private String username = null;
    private String password = null;
    private HttpClient client;
    private HttpClientContext context;
    private StatusLine status;

    /**
     * Returns a new client using credentials from the given feeds construction.
     */
    static public ApiClient newClient(String key, String url, FeedsConfig config) 
        throws IOException
    {
        ApiClient ret = ApiClient.builder()
            .env(key)
            .url(url)
            .suffix(".feeds")
            .username(config.getUsername())
            .build();

        // Configure and create the API client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create API client: "+ret.getUrl());

        return ret;
    }

    /**
     * Returns a new client that does not use authentication.
     */
    static public ApiClient newClient() 
        throws IOException
    {
        ApiClient ret = ApiClient.builder().build();

        // Configure and create the API client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create API client");

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring API client: "+getUrl());

        if(suffix != null)
        {
            String directory = System.getProperty("app.auth", ".");

            File file = new File(directory, env+suffix);
            try
            {
                // Read password from auth directory
                password = FileUtils.readFileToString(file, "UTF-8");
            }
            catch(IOException e)
            {
                logger.severe("Unable to read API password file: "+e.getClass().getName()+": "+e.getMessage());
            }
        }

        if(debug())
            logger.info("Configured API client successfully: "+getUrl());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating API client: "+getUrl());

        CredentialsProvider provider = new BasicCredentialsProvider();

        // Uses basic authentication
        if(username != null)
        {
            // Create the credentials provider
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            provider.setCredentials(AuthScope.ANY, credentials);

            // Create the auth cache to support pre-emptive basic authentication
            URL url = new URL(getUrl());
            HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            AuthCache authCache = new BasicAuthCache();
            authCache.put(targetHost, new BasicScheme());

            // Create the context
            context = HttpClientContext.create();
            context.setCredentialsProvider(provider);
            context.setAuthCache(authCache);

            // Create the API client
            client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();
        }
        else // Client not using authentication
        {
            client = HttpClients.createMinimal();
        }

        if(debug())
            logger.info("Created API client successfully: "+getUrl());

        return client != null;
    }

    /**
     * Returns the environment for the client.
     */
    public String getEnv() 
    {
        return env;
    }

    /**
     * Sets the environment for the client.
     */
    public void setEnv(String env) 
    {
        this.env = env;
    }

    /**
     * Returns the environment url for the client.
     */
    public String getUrl() 
    {
        return url;
    }

    /**
     * Sets the environment url for the client.
     */
    public void setUrl(String url) 
    {
        this.url = url;
    }

    /**
     * Returns the suffix for the client.
     */
    public String getSuffix() 
    {
        return suffix;
    }

    /**
     * Sets the suffix for the client.
     */
    public void setSuffix(String suffix) 
    {
        this.suffix = suffix;
    }

    /**
     * Returns the username for the client.
     */
    public String getUsername() 
    {
        return username;
    }

    /**
     * Sets the username for the client.
     */
    public void setUsername(String username) 
    {
        this.username = username;
    }

    /**
     * Executes a POST operation with a set of url-encoded parameters.
     */
    public String post(String url, Map<String,String> params) throws IOException
    {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for(Map.Entry<String,String> param : params.entrySet())
            paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));

        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(paramList));
        return execute(request);
    }

    /**
     * Executes a POST operation with no message body.
     */
    public String post(String url) throws IOException
    {
        return post(url, null, null);
    }

    /**
     * Executes a POST operation with a content type.
     */
    public String post(String url, String contentType) throws IOException
    {
        return post(url, contentType, null);
    }

    /**
     * Executes a POST operation with an optional content type and message body.
     */
    public String post(String url, String contentType, String body) throws IOException
    {
        HttpPost request = new HttpPost(url);
        if(contentType != null)
            request.addHeader("Content-Type", contentType);
        if(body != null)
            request.setEntity(new StringEntity(body));
        return execute(request);
    }

    /**
     * Executes a DELETE operation without a message body.
     */
    public String delete(String url) throws IOException
    {
        return execute(new HttpDelete(url));
    }

    /**
     * Executes a request operation.
     */
    public String execute(HttpUriRequest request) throws IOException
    {
        String ret = null;

        try
        {
            // Execute the API call
            HttpResponse response = client.execute(request, context);
            status = response.getStatusLine();
            ret = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch(IOException e)
        {
            request.abort();
            throw e;
        }

        return ret;
    }

    /**
     * Returns the status line for the last call.
     */
    public StatusLine getStatusLine()
    {
        return status;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
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
        private ApiClient client = new ApiClient();

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
         * Sets the suffix for the client.
         * @param type The suffix for the client
         * @return This object
         */
        public Builder suffix(String suffix)
        {
            client.setSuffix(suffix);
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
        public ApiClient build()
        {
            return client;
        }
    }
}