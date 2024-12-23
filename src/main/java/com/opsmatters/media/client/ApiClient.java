package com.opsmatters.media.client;

import java.net.URL;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Executes API calls using a http client.
 */
public class ApiClient extends Client
{
    private static final Logger logger = Logger.getLogger(ApiClient.class.getName());

    private String env = "";
    private String url = null;
    private String username = null;
    private String password = null;
    private HttpClient client;
    private HttpClientContext context;
    private StatusLine status;
    private String bearer = null;

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
     *
     * <P> Empty implementation
     */
    @Override
    public void configure() throws IOException
    {
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
     * Returns the password for the client.
     */
    public String getPassword() 
    {
        return password;
    }

    /**
     * Sets the password for the client.
     */
    public void setPassword(String password) 
    {
        this.password = password;
    }

    /**
     * Sets the bearer token for the client.
     */
    public void setBearer(String bearer)
    {
        this.bearer = bearer;
    }

    /**
     * Clears the bearer token for the client.
     */
    public void clearBearer()
    {
        setBearer(null);
    }

    /**
     * Returns <CODE>true</CODE> if the bearer token for the client has been set.
     */
    public boolean hasBearer()
    {
        return bearer != null && bearer.length() > 0;
    }

    /**
     * Executes a GET operation.
     */
    public String get(String url) throws IOException
    {
        HttpGet request = new HttpGet(url);
        if(bearer != null)
            request.addHeader("Authorization", String.format("Bearer %s", bearer));
        return execute(request, true);
    }

    /**
     * Executes a POST operation with a set of url-encoded parameters.
     */
    public String post(String url, Map<String,String> params, boolean hasResponseEntity) throws IOException
    {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for(Map.Entry<String,String> param : params.entrySet())
            paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));

        HttpPost request = new HttpPost(url);
        if(bearer != null)
            request.addHeader("Authorization", String.format("Bearer %s", bearer));
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(paramList));
        return execute(request, hasResponseEntity);
    }

    /**
     * Executes a POST operation with a set of url-encoded parameters.
     */
    public String post(String url, Map<String,String> params) throws IOException
    {
        return post(url, params, true);
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
    public String post(String url, String contentType, String body, boolean hasResponseEntity) throws IOException
    {
        HttpPost request = new HttpPost(url);
        if(contentType != null)
            request.addHeader("Content-Type", contentType);
        if(body != null)
            request.setEntity(new StringEntity(body));
        return execute(request, hasResponseEntity);
    }

    /**
     * Executes a POST operation with an optional content type and message body.
     */
    public String post(String url, String contentType, String body) throws IOException
    {
        return post(url, contentType, body, true);
    }

    /**
     * Executes a PUT operation with an optional content type and message body.
     */
    public String put(String url, String contentType, String body, boolean hasResponseEntity) throws IOException
    {
        HttpPut request = new HttpPut(url);
        if(contentType != null)
            request.addHeader("Content-Type", contentType);
        if(body != null)
            request.setEntity(new StringEntity(body));
        return execute(request, hasResponseEntity);
    }

    /**
     * Executes a PUT operation with an optional content type and message body.
     */
    public String put(String url, String contentType, String body) throws IOException
    {
        return put(url, contentType, body, true);
    }

    /**
     * Executes a DELETE operation without a message body.
     */
    public String delete(String url) throws IOException
    {
        return execute(new HttpDelete(url), true);
    }

    /**
     * Executes a request operation.
     */
    public String execute(HttpUriRequest request, boolean hasResponseEntity) throws IOException
    {
        String ret = null;

        try
        {
            // Execute the API call
            HttpResponse response = client.execute(request, context);
            status = response.getStatusLine();
            if(hasResponseEntity)
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