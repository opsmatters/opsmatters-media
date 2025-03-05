package com.opsmatters.media.client.proxy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.FileUtils;
import com.opsmatters.media.client.ApiClient;

/**
 * Executes Webshare API calls using a http client.
 */
public class WebshareClient extends ApiClient
{
    private static final Logger logger = Logger.getLogger(WebshareClient.class.getName());

    public static final String SUFFIX = ".webshare";

    private static final String BASE_URL = "https://proxy.webshare.io/api";

    /**
     * Returns a new client using the given credentials.
     */
    static public WebshareClient newClient() 
        throws IOException
    {
        WebshareClient ret = WebshareClient._builder()
            .url(BASE_URL)
            .build();

        // Configure and create the Webshare client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create Webshare client: "+ret.getUrl());

        return ret;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring Webshare client");

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setToken(obj.optString("token"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read Webshare auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured Webshare client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating Webshare client");

        if(!super.create())
        {
            logger.severe("Unable to create Webshare client");
            return false;
        }

        return getIPAddress() != null;
    }

    /**
     * Returns the token name for the client.
     */
    @Override
    public String getTokenName()
    {
        return "Token";
    }

    /**
     * Gets the current external IP address.
     */
    public String getIPAddress() throws IOException
    {
        String ret = null;
        String response = get(String.format("%s/v2/proxy/ipauthorization/whatsmyip/", BASE_URL));
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("ip_address"))
            {
                ret = obj.optString("ip_address");
            }
            else // IP address not found
            {
                logger.severe("IP address not found for webshare get IP address: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for webshare get IP address: "+response);
        }

        return ret;
    }

    /**
     * Gets the list of IP authorizations.
     */
    public List<String> listIPAuthorizations() throws IOException
    {
        List<String> ret = null;
        String response = get(String.format("%s/v2/proxy/ipauthorization/", BASE_URL));
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("count"))
            {
                int count = obj.getInt("count");
                ret = new ArrayList<String>();
                if(obj.has("results"))
                {
                    JSONArray results = obj.getJSONArray("results");
                    for(int i = 0; i < results.length(); i++)
                    {
                        JSONObject result = results.getJSONObject(i);
                        ret.add(result.getString("ip_address"));
                    }
                }
            }
            else // IP authorizations not found
            {
                logger.severe("IP authorizations not found for webshare get IP authorizations: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for webshare get IP authorizations: "+response);
        }

        return ret;
    }

    /**
     * Creates an IP authorization for the given ip address.
     */
    public String createIPAuthorization(String ipAddress) throws IOException
    {
        JSONObject request = new JSONObject();
        request.put("ip_address", ipAddress);

        String ret = null;
        String response = post(String.format("%s/v2/proxy/ipauthorization/", BASE_URL),
            "application/json", request.toString());
        int statusCode = getStatusLine().getStatusCode();
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("non_field_errors"))
            {
                logger.severe(String.format("Error response for webshare create IP authorization: %d %s",
                    statusCode, obj));
            }
            else if(obj.has("ip_address"))
            {
                ret = obj.optString("ip_address");
            }
            else // IP address not found
            {
                logger.severe("IP address not found for webshare create IP authorization: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for webshare create IP authorization: "+response);
        }

        return ret;
    }

    /**
     * Checks that an IP authorization exists for the current ip address.
     * @return <CODE>true</CODE> if an authorization was found.
     */
    public boolean checkIPAuthorization() throws IOException
    {
        boolean ret = false;

        logger.info("Checking proxy IP authorization...");

        String currentIP = getIPAddress();
        List<String> authorizations = listIPAuthorizations();
        if(!authorizations.contains(currentIP))
        {
            createIPAuthorization(currentIP);
            logger.info(String.format("Added IP authorization for current IP address: %s", ret));
            ret = true;
        }
        else
        {
            logger.info(String.format("IP authorization found for current IP address: %s", currentIP));
            ret = true;
        }

        return ret;
    }

    /**
     * Gets the list of proxies.
     */
    public List<WebshareProxy> listProxies() throws IOException
    {
        List<WebshareProxy> ret = null;
        String response = get(String.format("%s/v2/proxy/list/?mode=direct", BASE_URL));
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("count"))
            {
                int count = obj.getInt("count");
                ret = new ArrayList<WebshareProxy>();
                if(obj.has("results"))
                {
                    JSONArray results = obj.getJSONArray("results");
                    for(int i = 0; i < results.length(); i++)
                    {
                        JSONObject result = results.getJSONObject(i);
                        ret.add(new WebshareProxy(result));
                    }
                }
            }
            else // Proxies not found
            {
                logger.severe("Proxies not found for webshare get proxies: "+response);
            }
        }
        else // Invalid JSON response
        {
            logger.severe("Invalid JSON response for webshare get proxies: "+response);
        }

        return ret;
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
        private WebshareClient client = new WebshareClient();

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
         * Returns the configured client instance
         * @return The client instance
         */
        public WebshareClient build()
        {
            return client;
        }
    }
}