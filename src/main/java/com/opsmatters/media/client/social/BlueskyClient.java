/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.client.social;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.ChannelPost;
import com.opsmatters.media.client.ApiClient;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.Match;

/**
 * Class that represents a connection to Bluesky for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class BlueskyClient extends ApiClient implements SocialClient
{
    private static final Logger logger = Logger.getLogger(BlueskyClient.class.getName());

    public static final String SUFFIX = ".social";

    private static final String BASE_URL = "https://bsky.social";
    private static final String EXPIRED_TOKEN = "ExpiredToken";
    private static final String RATE_LIMIT_EXCEEDED = "RateLimitExceeded";
    private static final String UPSTREAM_FAILURE = "UpstreamFailure";
    private static final String UPSTREAM_TIMEOUT = "UpstreamTimeout";

    private static final int READ_TIMEOUT = 15000;
    private static final int REFRESH_RETRIES = 3;

    private String identifier = "";
    private String password = "";
    private SocialChannel channel;

    private static String accessToken;
    private static String refreshToken;

    /**
     * Private constructor.
     */
    private BlueskyClient(SocialChannel channel)
    {
        setChannel(channel);
    }

    /**
     * Returns a new bluesky client using the given channel.
     */
    static public BlueskyClient newClient(SocialChannel channel) throws IOException
    {
        BlueskyClient ret = new BlueskyClient(channel);
        ret.setUrl(BASE_URL);

        // Configure and create the linkedin client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create bluesky client: "+channel.getCode());

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public SocialProvider getProvider()
    {
        return SocialProvider.BLUESKY;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring bluesky client: "+channel.getCode());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, channel.getCode().toLowerCase()+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setIdentifier(obj.optString("identifier"));
            setPassword(obj.optString("password"));
            setAccessToken(obj.optString("accessToken"));
            setRefreshToken(obj.optString("refreshToken"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read bluesky auth file: "
                +e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured bluesky client successfully: "+channel.getCode());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating bluesky client: "+channel.getCode());

        if(!super.create())
        {
            logger.severe("Unable to create bluesky client: "+getUrl());
            return false;
        }

        clearToken();

        if(!hasAccessToken())
        {
            if(hasRefreshToken())
            {
                // Try to refresh the session
                if(refreshSession())
                {
                    writeCredentials();
                }
            }
            else
            {
                // Otherwise create a new session
                if(createSession())
                {
                    writeCredentials();
                }
            }
        }

        setToken(getAccessToken());

        if(debug())
            logger.info("Created bluesky client successfully: "+channel.getCode());

        return hasToken();
    }

    /**
     * Write the credentials when the tokens change.
     */
    private void writeCredentials()
    {
        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, channel.getCode().toLowerCase()+SUFFIX);
        try
        {
            // Write file to auth directory
            JSONObject obj = new JSONObject();
            obj.put("identifier", getIdentifier());
            obj.put("password", getPassword());
            obj.put("accessToken", getAccessToken());
            obj.put("refreshToken", getRefreshToken());
            FileUtils.writeStringToFile(file, obj.toString(), "UTF-8");
        }
        catch(IOException e)
        {
            logger.severe("Unable to write bluesky auth file: "
                +e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
    }

    /**
     * Returns the social channel.
     */
    public SocialChannel getChannel()
    {
        return channel;
    }

    /**
     * Sets the social channel.
     */
    public void setChannel(SocialChannel channel)
    {
        this.channel = channel;
    }

    /**
     * Returns the identifier for the client.
     */
    public String getIdentifier() 
    {
        return identifier;
    }

    /**
     * Sets the identifier for the client.
     */
    public void setIdentifier(String identifier) 
    {
        this.identifier = identifier;
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
     * Returns the access token for the client.
     */
    public static String getAccessToken() 
    {
        return accessToken;
    }

    /**
     * Sets the access token for the client.
     */
    public static void setAccessToken(String accessToken) 
    {
        BlueskyClient.accessToken = accessToken;
    }

    /**
     * Clears the access token for the client.
     */
    public static void clearAccessToken() 
    {
        setAccessToken(null);
    }

    /**
     * Returns <CODE>true</CODE> if the access token for the client has been set.
     */
    public static boolean hasAccessToken() 
    {
        return getAccessToken() != null && getAccessToken().length() > 0;
    }

    /**
     * Returns the refresh token for the client.
     */
    public static String getRefreshToken() 
    {
        return refreshToken;
    }

    /**
     * Sets the refresh token for the client.
     */
    public static void setRefreshToken(String refreshToken) 
    {
        BlueskyClient.refreshToken = refreshToken;
    }

    /**
     * Clears the refresh token for the client.
     */
    public static void clearRefreshToken() 
    {
        setRefreshToken(null);
    }

    /**
     * Returns <CODE>true</CODE> if the refresh token for the client has been set.
     */
    public static boolean hasRefreshToken() 
    {
        return getRefreshToken() != null && getRefreshToken().length() > 0;
    }

    /**
     * Returns the handle.
     */
    public String getName()
    {
        return getIdentifier();
    }

    /**
     * Create a new session and get the access tokens.
     */
    private boolean createSession() throws BlueskyException
    {
        boolean ret = false;

        try
        {
                if(debug())
                    logger.info("bluesky createSession");

                JSONObject params = new JSONObject();
                params.put("identifier", getIdentifier());
                params.put("password", getPassword());

                String response = post(String.format("%s/xrpc/com.atproto.server.createSession", BASE_URL),
                    "application/json", params.toString());

                int statusCode = getStatusLine().getStatusCode();

                if(debug())
                    logHeaders();

                if(response.startsWith("{")) // Valid JSON
                {
                    JSONObject obj = new JSONObject(response);
                    if(obj.has("error"))
                    {
                        logger.severe(String.format("Error response for bluesky create session: %d %s",
                            statusCode, obj));
                        processErrorResponse(statusCode, obj);
                    }
                    else
                    {
                        setAccessToken(obj.optString("accessJwt"));
                        setRefreshToken(obj.optString("refreshJwt"));
                        logger.info("Created bluesky session successfully");
                        ret = true;
                    }
                }
                else // Invalid JSON response
                {
                    logger.severe(String.format("Invalid JSON response for bluesky create session: %d %s",
                        statusCode, response));
                    throw new IOException("Invalid JSON response: "+response);
                }
        }
        catch(IOException e)
        {
            throw new BlueskyException(e);
        }

        return ret;
    }

    /**
     * Refresh a session to get new access tokens.
     */
    private boolean refreshSession() throws BlueskyException
    {
        boolean ret = false;

        try
        {
            if(debug())
                logger.info("bluesky refreshSession");

            String response = null;
            Exception ex = null;

            setToken(getRefreshToken());
            for(int i = 0; i < REFRESH_RETRIES; i++)
            {
                try
                {
                    if(i > 0)
                    {
                        logger.info("Retrying bluesky session refresh...");
                        Thread.sleep(1000);
                    }

                    response = post(String.format("%s/xrpc/com.atproto.server.refreshSession", BASE_URL));
                    ex = null;
                    break;
                }
                catch(Exception e)
                {
                    logger.severe(String.format("Bluesky session refresh attempt %d failed: %s",
                        i+1, e.getMessage()));
                    ex = e;
                }
            }

            // Retries exhausted without success
            if(ex != null)
            {
                logger.severe("Bluesky session refresh attempts failed");
                logger.severe(StringUtils.serialize(ex));
                throw new BlueskyException(ex);
            }

            int statusCode = getStatusLine().getStatusCode();

            if(debug())
                logHeaders();

            if(response.startsWith("{")) // Valid JSON
            {
                JSONObject obj = new JSONObject(response);
                if(obj.has("error"))
                {
                    logger.severe(String.format("Error response for bluesky refresh session: %d %s",
                        statusCode, obj));

                    String message = obj.optString("message");
                    if(message != null && message.indexOf("revoked") != -1)
                    {
                        // Clear the tokens if the refresh token has been revoked
                        clearAccessToken();
                        clearRefreshToken();
                        writeCredentials();
                        logger.info("Cleared refresh token as it has been revoked");
                    }
                    else
                    {
                        processErrorResponse(statusCode, obj);
                    }
                }
                else
                {
                    setAccessToken(obj.optString("accessJwt"));
                    setRefreshToken(obj.optString("refreshJwt"));
                    logger.info("Refreshed bluesky session successfully");
                    ret = true;
                }
            }
            else // Invalid JSON response
            {
                logger.severe(String.format("Invalid JSON response for bluesky refresh session: %d %s",
                    statusCode, response));
                throw new IOException("Invalid JSON response: "+response);
            }
        }
        catch(IOException e)
        {
            throw new BlueskyException(e);
        }

        return ret;
    }

    /**
     * Returns a channel post for the given post.
     */
    private ChannelPost newChannelPost(String cid, SocialChannel channel, String text)
    {
        return new ChannelPost(cid, channel, text);
    }

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public ChannelPost sendPost(String text) throws IOException
    {
        if(!hasToken())
            throw new IllegalArgumentException("missing access token");

        if(debug())
            logger.info("bluesky sendPost");

        String cid = null;

        List<Match> hashtags = StringUtils.extractHashtags(text);
        List<Match> links = StringUtils.extractUrls(text);

        if(links.size() == 0)
            throw new IllegalArgumentException("missing url");

        JSONArray facets = getFacets(text, hashtags, links);
        JSONObject embed = getEmbed(links.get(0).getText());

        JSONObject record = new JSONObject();
        record.put("$type", "app.bsky.feed.post");
        record.put("text", text);
        record.put("createdAt", TimeUtils.toStringUTC(Formats.ISO8601_FORMAT)+"Z");
        if(facets != null)
            record.put("facets", facets);
        if(embed != null)
            record.put("embed", embed);

        JSONObject request = new JSONObject();
        request.put("repo", getIdentifier());
        request.put("collection", "app.bsky.feed.post");
        request.put("record", record);

        String response = post(String.format("%s/xrpc/com.atproto.repo.createRecord", BASE_URL),
            "application/json", request.toString().getBytes("UTF-8"));

        int statusCode = getStatusLine().getStatusCode();

        if(debug())
            logHeaders();

        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky send post: %d %s",
                    statusCode, obj));
                processErrorResponse(statusCode, obj);
            }
            else
            {
                cid = obj.optString("cid");
            }
        }
        else // Invalid JSON response
        {
            logger.severe(String.format("Invalid JSON response for bluesky send post: %d %s",
                statusCode, response));
            throw new IOException("Invalid JSON response: "+response);
        }

        return cid != null ? newChannelPost(cid, channel, text) : null;
    }

    /**
     * Get the embed object for the given image URL.
     *
     * @param link The link to the URL to embed.
     * @param alt The alt text attribute for the image.
     * @return The embed object in JSON format.
     */
    private JSONObject getEmbed(String link) throws IOException
    {
        JSONObject ret = null;

        if(debug())
            logger.info("bluesky getEmbed");

        URL url = new URL(link);

        String title = null;
        String description = null;
        String imageUrl = null;

        try
        {
            // Crawl the link's page to get the metatags
            Document doc = Jsoup.parse(url, READ_TIMEOUT);
            List<Element> tags = doc.getElementsByTag("meta");
            title = getMetatag(tags, "og:title");
            description = getMetatag(tags, "og:description");
            imageUrl = getMetatag(tags, "og:image");
        }
        catch(SocketTimeoutException e)
        {
            logger.severe("Error crawling page for bluesky send post: "+url);
            throw new SocialTimeoutException("Error crawling page for url: "+url, e);
        }

        String mimeType = null;
        byte[] bytes = null;

        try
        {
            // Download the image file
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
            mimeType = String.format("image/%s",
                com.opsmatters.media.util.FileUtils.getExtension(filename));
            bytes = getBytes(imageUrl);
        }
        catch(SocketTimeoutException e)
        {
            logger.severe("Error downloading image for bluesky send post: "+imageUrl);
            throw new SocialTimeoutException("Error downloading image: "+imageUrl, e);
        }

        String response = post(String.format("%s/xrpc/com.atproto.repo.uploadBlob", BASE_URL),
            mimeType, bytes);

        int statusCode = getStatusLine().getStatusCode();

        if(debug())
            logHeaders();

        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky upload blob: %d %s",
                    statusCode, obj));
                processErrorResponse(statusCode, obj);
            }
            else if(obj.has("blob"))
            {
                ret = new JSONObject();
                ret.put("$type", "app.bsky.embed.external");
                JSONObject external = new JSONObject();
                external.put("uri", link);
                external.put("title", title);
                external.put("description", description);
                external.put("thumb", obj.get("blob"));
                ret.put("external", external);
            }
            else // Missing blob
            {
                logger.severe(String.format("Blob not found for bluesky upload blob: %d %s",
                    statusCode, obj));
            }
        }
        else // Invalid JSON response
        {
            logger.severe(String.format("Invalid JSON response for bluesky upload blob: %d %s",
                statusCode, response));
            throw new IOException("Invalid JSON response: "+response);
        }

        return ret;
    }

    /**
     * Get the array of facets for the given hashtags and links.
     *
     * @param text The text of the post.
     * @param hashtags The list of hashtags.
     * @param links The list of links.
     * @return The facets array in JSON format.
     */
    private JSONArray getFacets(String text, List<Match> hashtags, List<Match> links) throws IOException
    {
        JSONArray ret = null;

        FacetParser parser = new FacetParser(text);
        parser.addHashtags(hashtags);
        parser.addLinks(links);

        List<Facet> facets = parser.getFacets();
        if(facets.size() > 0)
        {
            ret = new JSONArray();
            for(Facet facet : facets)
            {
                if(facet.getStart() > 0)
                {
                    JSONObject obj = new JSONObject();

                    JSONObject index = new JSONObject();
                    index.put("byteStart", facet.getStart());
                    index.put("byteEnd", facet.getEnd());
                    obj.put("index", index);

                    JSONArray features = new JSONArray();
                    JSONObject feature = new JSONObject();
                    feature.put("$type", "app.bsky.richtext.facet#"+facet.getType().value());
                    feature.put(facet.getType().attr(), facet.getText());
                    features.put(feature);

                    obj.put("features", features);

                    ret.put(obj);
                }
            }
        }

        return ret;
    }

    /**
     * Get the content of the given metatag.
     *
     * @param tags The list of metatags.
     * @param name The name of the metatag.
     * @return The content of the metatag.
     */
    private String getMetatag(List<Element> tags, String name)
    {
        String ret = null;
        for(Element tag : tags)
        {
            String attr = tag.attr("property");
            if(attr != null && attr.equals(name))
            {
                ret = tag.attr("content");
                break;
            }
        }

        return ret;
    }

    /**
     * Get a byte array from the given image URL.
     *
     * @param imageUrl The image URL.
     * @return The byte array for the image.
     */
    private byte[] getBytes(String imageUrl) throws IOException
    {
        URL url = new URL(imageUrl);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try(InputStream stream = url.openStream())
        {
            byte[] buffer = new byte[4096];

            while (true)
            {
                int bytesRead = stream.read(buffer);
                if (bytesRead < 0) { break; }
                output.write(buffer, 0, bytesRead);
            }
        }

        return output.toByteArray();
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public ChannelPost deletePost(String cid) throws IOException
    {
        if(debug())
            logger.info("bluesky deletePost");

        ChannelPost ret = null;
        JSONObject request = new JSONObject();
        request.put("repo", getIdentifier());
        request.put("collection", "app.bsky.feed.post");
        request.put("swapRecord", cid);

        String response = post(String.format("%s/xrpc/com.atproto.server.deleteSession", BASE_URL),
            "application/json", request.toString());

        int statusCode = getStatusLine().getStatusCode();

        if(debug())
            logHeaders();

        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky delete post: %d %s",
                    statusCode, obj));
                processErrorResponse(statusCode, obj);
            }
            else
            {
                cid = obj.optString("cid");
            }
        }
        else // Invalid JSON response
        {
            logger.severe(String.format("Invalid JSON response for bluesky delete post: %d %s",
                statusCode, response));
            throw new IOException("Invalid JSON response: "+response);
        }

        return ret;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<ChannelPost> getPosts() throws IOException, URISyntaxException
    {
        List<ChannelPost> ret = new ArrayList<ChannelPost>();

        Map<String,String> params = new HashMap<String,String>();
        params.put("repo", getIdentifier());
        params.put("collection", "app.bsky.feed.post");

        String response = get(String.format("%s/xrpc/com.atproto.repo.listRecords", BASE_URL), params);

        int statusCode = getStatusLine().getStatusCode();
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky get posts: %d %s",
                    statusCode, obj));
                processErrorResponse(statusCode, obj);
            }
            else
            {
                JSONArray records = obj.optJSONArray("records");
                for(int i = 0; i < records.length(); i++)
                {
                    JSONObject record = records.getJSONObject(i);
                    String cid = record.optString("cid");
                    String uri = record.optString("uri");
                    if(cid != null && cid.length() > 0)
                        ret.add(newChannelPost(cid, channel, uri));
                }
            }
        }
        else // Invalid JSON response
        {
            logger.severe(String.format("Invalid JSON response for bluesky get posts: %d %s",
                statusCode, response));
            throw new IOException("Invalid JSON response: "+response);
        }

        return ret;
    }

    /**
     * Process an error response from the bluesky API.
     */
    private void processErrorResponse(int statusCode, JSONObject obj)
        throws BlueskyException
    {
        String error = obj.optString("error");
        if(error != null)
        {
            if(error.equals(EXPIRED_TOKEN))
            {
                clearAccessToken();
                writeCredentials();
                logger.info("Cleared access token as it has expired");
            }

            if(error.equals(RATE_LIMIT_EXCEEDED))
            {
                logHeaders("RateLimit");
            }
        }

        throw new BlueskyException(statusCode, obj);
    }

    /**
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e)
    {
        boolean ret = true;

        if(e instanceof BlueskyException)
        {
            BlueskyException ex = (BlueskyException)e;
            if(ex.getError().equals(EXPIRED_TOKEN))
            {
                // Bluesky tokens expire after 2 hours
                ret = true;
            }
            else if(ex.getError().equals(UPSTREAM_FAILURE)
                || ex.getError().equals(UPSTREAM_TIMEOUT))
            {
                ret = true;
            }
            else if(ex.getCode() != 200)
            {
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Returns the error code from the given exception.
     */
    public int getErrorCode(Exception e)
    {
        int ret = -1;

        if(e instanceof BlueskyException)
        {
            BlueskyException ex = (BlueskyException)e;
            ret = ex.getCode();
        }

        return ret;
    }

    /**
     * Returns the error message from the given exception.
     */
    public String getErrorMessage(Exception e)
    {
        String ret = "";

        if(e instanceof BlueskyException)
        {
            BlueskyException ex = (BlueskyException)e;
            ret = ex.getMessage();
        }

        return ret;
    }
}