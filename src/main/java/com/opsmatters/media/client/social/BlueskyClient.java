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

    private static final int READ_TIMEOUT = 15000;

    private String identifier = "";
    private String password = "";
    private SocialChannel channel;

    enum FacetType
    {
        MENTION("mention", "did"),
        HASHTAG("tag", "tag"),
        LINK("link", "uri");

        String value;
        String attr;

        FacetType(String value, String attr)
        {
            this.value = value;
            this.attr = attr;
        }

        String value()
        {
            return value;
        }

        String attr()
        {
            return attr;
        }
    }

    class Facet
    {
        FacetType type;
        int start = -1;
        int end = -1;
        String text = null;

        Facet(FacetType type, Match match)
        {
            this.type = type;
            this.start = match.getStart();
            this.end = match.getEnd();
            text = match.getText();
            if(type == FacetType.MENTION || type == FacetType.HASHTAG)
                text = text.substring(1); // Strip # or @
        }

        String text()
        {
            return text;
        }
    }

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
        JSONObject obj = null;
        try
        {
            // Read file from auth directory
            obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setIdentifier(obj.optString("identifier"));
            setPassword(obj.optString("password"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read default bluesky auth file: "
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

        clearBearer();

        JSONObject params = new JSONObject();
        params.put("identifier", getIdentifier());
        params.put("password", getPassword());

        String response = post(String.format("%s/xrpc/com.atproto.server.createSession", BASE_URL),
            "application/json", params.toString());

        int statusCode = getStatusLine().getStatusCode();
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky authenticate: %d %s",
                    statusCode, obj));
                throw new BlueskyException(statusCode, response);
            }
            else
            {
                setBearer(obj.optString("accessJwt"));
            }
        }
        else // Invalid JSON response
        {
            logger.severe(String.format("Invalid JSON response for bluesky authenticate: %d %s",
                statusCode, response));
            throw new IOException("Invalid JSON response: "+response);
        }

        if(debug())
            logger.info("Created bluesky client successfully: "+channel.getCode());

        return hasBearer();
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
     * Returns the handle.
     */
    public String getName()
    {
        return getIdentifier();
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
        String cid = null;

        text = StringUtils.convertToAscii(text, false);
        List<Match> hashtags = StringUtils.extractHashtags(text);
        List<Match> links = StringUtils.extractUrls(text);

        if(links.size() == 0)
            throw new IllegalArgumentException("missing url");

        JSONArray facets = getFacets(hashtags, links);
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
            "application/json", request.toString());

        int statusCode = getStatusLine().getStatusCode();
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky send post: %d %s",
                    statusCode, obj));
                throw new BlueskyException(statusCode, response);
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

        URL url = new URL(link);
        Document doc = Jsoup.parse(url, READ_TIMEOUT);
        List<Element> tags = doc.getElementsByTag("meta");
        String title = getMetatag(tags, "og:title");
        String description = getMetatag(tags, "og:description");
        String imageUrl = getMetatag(tags, "og:image");
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        String mimeType = String.format("image/%s",
            com.opsmatters.media.util.FileUtils.getExtension(filename));
        byte[] bytes = getBytes(imageUrl);

        String response = post(String.format("%s/xrpc/com.atproto.repo.uploadBlob", BASE_URL),
            mimeType, bytes);

        int statusCode = getStatusLine().getStatusCode();
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky upload blob: %d %s",
                    statusCode, obj));
                throw new BlueskyException(statusCode, response);
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
     * @param hashtags The list of hashtags.
     * @param links The list of links.
     * @return The facets array in JSON format.
     */
    private JSONArray getFacets(List<Match> hashtags, List<Match> links) throws IOException
    {
        JSONArray ret = null;
        List<Facet> facets = new ArrayList<Facet>();
        for(Match link : links)
            facets.add(new Facet(FacetType.LINK, link));
        for(Match hashtag : hashtags)
            facets.add(new Facet(FacetType.HASHTAG, hashtag));

        if(facets.size() > 0)
        {
            ret = new JSONArray();
            for(Facet facet : facets)
            {
                if(facet.start > 0)
                {
                    JSONObject obj = new JSONObject();

                    JSONObject index = new JSONObject();
                    index.put("byteStart", facet.start);
                    index.put("byteEnd", facet.end);
                    obj.put("index", index);

                    JSONArray features = new JSONArray();
                    JSONObject feature = new JSONObject();
                    feature.put("$type", "app.bsky.richtext.facet#"+facet.type.value());
                    feature.put(facet.type.attr(), facet.text());
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
        ChannelPost ret = null;
        JSONObject request = new JSONObject();
        request.put("repo", getIdentifier());
        request.put("collection", "app.bsky.feed.post");
        request.put("swapRecord", cid);

        String response = post(String.format("%s/xrpc/com.atproto.server.deleteSession", BASE_URL),
            "application/json", request.toString());

        int statusCode = getStatusLine().getStatusCode();
        if(response.startsWith("{")) // Valid JSON
        {
            JSONObject obj = new JSONObject(response);
            if(obj.has("error"))
            {
                logger.severe(String.format("Error response for bluesky delete post: %d %s",
                    statusCode, obj));
                throw new BlueskyException(statusCode, response);
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
                throw new BlueskyException(statusCode, response);
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
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e)
    {
        int errorCode = getErrorCode(e);
        return errorCode != 200;
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