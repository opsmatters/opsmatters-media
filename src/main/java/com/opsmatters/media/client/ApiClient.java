package com.opsmatters.media.client;

import java.net.URL;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.http.Header;
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
import org.apache.http.client.utils.URIBuilder;
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
import org.apache.http.entity.ByteArrayEntity;
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
    private Header[] headers;
    private String token = null;

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

        return isConnected();
    }

    /**
     * Returns <CODE>true</CODE> if the client is connected.
     */
    public boolean isConnected() 
    {
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
     * Returns the token name for the client.
     */
    public String getTokenName()
    {
        return "Bearer";
    }

    /**
     * Sets the token for the client.
     */
    public void setToken(String token)
    {
        this.token = token;
    }

    /**
     * Clears the token for the client.
     */
    public void clearToken()
    {
        setToken(null);
    }

    /**
     * Returns <CODE>true</CODE> if the token for the client has been set.
     */
    public boolean hasToken()
    {
        return token != null && token.length() > 0;
    }

    /**
     * Executes a GET operation with a set of url-encoded parameters.
     */
    public String get(String url, Map<String,String> params) throws IOException, URISyntaxException
    {
        HttpGet request = new HttpGet(url);

        if(params != null)
        {
            URIBuilder builder = new URIBuilder(request.getURI());
            for(Map.Entry<String,String> param : params.entrySet())
                builder = builder.addParameter(param.getKey(), param.getValue());
            request.setURI(builder.build());
        }

        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));

        return execute(request, true);
    }

    /**
     * Executes a GET operation.
     */
    public String get(String url) throws IOException
    {
        HttpGet request = new HttpGet(url);
        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));
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
        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));
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
     * Executes a POST operation with an optional content type and message body.
     */
    public String post(String url, String contentType, String body, boolean hasResponseEntity) throws IOException
    {
        HttpPost request = new HttpPost(url);
        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));
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
     * Executes a POST operation with a content type.
     */
    public String post(String url, String contentType) throws IOException
    {
        return post(url, contentType, (String)null);
    }

    /**
     * Executes a POST operation with no message body.
     */
    public String post(String url) throws IOException
    {
        return post(url, null, (String)null);
    }

    /**
     * Executes a POST operation with an optional content type and byte array.
     */
    public String post(String url, String contentType, byte[] bytes, boolean hasResponseEntity) throws IOException
    {
        HttpPost request = new HttpPost(url);
        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));
        if(contentType != null)
            request.addHeader("Content-Type", contentType);
        if(bytes != null)
            request.setEntity(new ByteArrayEntity(bytes));
        return execute(request, hasResponseEntity);
    }

    /**
     * Executes a POST operation with an optional content type and byte array.
     */
    public String post(String url, String contentType, byte[] bytes) throws IOException
    {
        return post(url, contentType, bytes, true);
    }

    /**
     * Executes a PUT operation with an optional content type and message body.
     */
    public String put(String url, String contentType, String body, boolean hasResponseEntity) throws IOException
    {
        HttpPut request = new HttpPut(url);
        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));
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
     * Executes a PUT operation with an optional content type and byte array.
     */
    public String put(String url, String contentType, byte[] bytes, boolean hasResponseEntity) throws IOException
    {
        HttpPut request = new HttpPut(url);
        if(token != null)
            request.addHeader("Authorization", String.format("%s %s", getTokenName(), token));
        if(contentType != null)
            request.addHeader("Content-Type", contentType);
        if(bytes != null)
            request.setEntity(new ByteArrayEntity(bytes));
        return execute(request, hasResponseEntity);
    }

    /**
     * Executes a PUT operation with an optional content type and byte array.
     */
    public String put(String url, String contentType, byte[] bytes) throws IOException
    {
        return put(url, contentType, bytes, true);
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
            headers = response.getAllHeaders();
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
     * Returns the status line from the last call.
     */
    public StatusLine getStatusLine()
    {
        return status;
    }

    /**
     * Returns the HTTP headers from the last call.
     */
    public Header[] getHeaders()
    {
        return headers;
    }

    /**
     * Logs the HTTP headers from the last call using the filter on header name.
     */
    public void logHeaders(String filter)
    {
        if(getHeaders() != null)
        {
            // Filter the headers
            List<Header> headers = new ArrayList<Header>();
            for(Header header : getHeaders())
            {
                if(filter == null || header.getName().indexOf(filter) != -1)
                    headers.add(header);
            }

            // Log the headers
            StringBuilder buff = new StringBuilder();
            buff.append(String.format("Status: %s", getStatusLine()));
            buff.append(String.format("Headers: (%d)", headers.size()));
            for(Header header : headers)
            {
                buff.append(String.format("\n  %s=[%s]",
                    header.getName(), header.getValue()));
            }

            logger.info(buff.toString());
        }
    }

    /**
     * Logs the HTTP headers from the last call.
     */
    public void logHeaders()
    {
        logHeaders(null);
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