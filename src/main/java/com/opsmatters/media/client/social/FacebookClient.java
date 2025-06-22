/*
 * Copyright 2020 Gerald Curley
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
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.PostUpdate;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import com.opsmatters.media.model.provider.SocialProviderId;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.ChannelPost;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a connection to Facebook for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FacebookClient extends Client implements SocialClient
{
    private static final Logger logger = Logger.getLogger(FacebookClient.class.getName());

    public static final String SUFFIX = ".social";

    private Facebook client;
    private AccessToken accessToken;
    private String appId = "";
    private String appSecret = "";
    private String token = "";
    private String permissions = "";
    private SocialChannel channel;

    /**
     * Private constructor.
     */
    private FacebookClient(SocialChannel channel)
    {
        setChannel(channel);
    }

    /**
     * Returns a new facebook client using the given channel.
     */
    static public FacebookClient newClient(SocialChannel channel) throws IOException, FacebookException
    {
        FacebookClient ret = new FacebookClient(channel);

        // Configure and create the facebook client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create facebook client: "+channel.getCode());

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public SocialProviderId getProviderId()
    {
        return SocialProviderId.FACEBOOK;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring facebook client: "+channel.getCode());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, channel.getCode().toLowerCase()+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setAppId(obj.optString("appId"));
            setAppSecret(obj.optString("appSecret"));
            setAccessToken(obj.optString("accessToken"));
            setPermissions(obj.optString("permissions"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read facebook auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(getAccessToken() != null && getAccessToken().length() > 0)
            accessToken = new AccessToken(getAccessToken(), null);

        if(debug())
            logger.info("Configured facebook client successfully: "+channel.getCode());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException, FacebookException
    {
        if(debug())
            logger.info("Creating facebook client: "+channel.getCode());

        FacebookFactory factory = new FacebookFactory();

        // Create the client
        Facebook facebook = factory.getInstance();
        facebook.setOAuthAppId(appId, appSecret);
        facebook.setOAuthAccessToken(accessToken);
        facebook.setOAuthPermissions(permissions);

        // Issue command to test connectivity
        String id = facebook.getId();

        client = facebook;

        if(debug())
            logger.info("Created facebook client successfully: "+channel.getCode());

        return id != null;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        client.shutdown();
        client = null;
    }

    /**
     * Returns the app id for the client.
     */
    public String getAppId() 
    {
        return appId;
    }

    /**
     * Sets the app id for the client.
     */
    public void setAppId(String appId) 
    {
        this.appId = appId;
    }

    /**
     * Returns the app secret for the client.
     */
    public String getAppSecret() 
    {
        return appSecret;
    }

    /**
     * Sets the app secret for the client.
     */
    public void setAppSecret(String appSecret) 
    {
        this.appSecret = appSecret;
    }

    /**
     * Returns the access token for the client.
     */
    public String getAccessToken() 
    {
        return token;
    }

    /**
     * Sets the acess token for the client.
     */
    public void setAccessToken(String accessToken) 
    {
        this.token = accessToken;
    }

    /**
     * Returns the permissions for the client.
     */
    public String getPermissions() 
    {
        return permissions;
    }

    /**
     * Sets the permissions for the client.
     */
    public void setPermissions(String permissions) 
    {
        this.permissions = permissions;
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
     * Returns the name of the current account.
     */
    public String getName() throws IOException, FacebookException
    {
        return client.getName();
    }

    /**
     * Returns a channel post for the given post.
     */
    private ChannelPost newChannelPost(Post post, SocialChannel channel)
    {
        ChannelPost ret = new ChannelPost(post.getId(), channel, post.getMessage());
        ret.setCreatedDateMillis(post.getCreatedTime().getTime());
        return ret;
    }

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public ChannelPost sendPost(String text) throws IOException, FacebookException
    {
        String id = null;
        String url = StringUtils.extractUrl(text);
        if(url != null)
            id = client.postFeed(new PostUpdate(new URL(url)).message(text));
        else
            id = client.postStatusMessage(text);
        Post post = client.getPost(id);
        return post != null ? newChannelPost(post, channel) : null;
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public ChannelPost deletePost(String id) throws IOException, FacebookException
    {
        ChannelPost ret = null;

        try
        {
            Post post = client.getPost(id);
            ret = newChannelPost(post, channel);
        }
        catch(FacebookException e)
        {
            // Post already deleted
            if(e.getStatusCode() == 400)
                ret = new ChannelPost(id, channel);
            else
                throw e;
        }

        return ret;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<ChannelPost> getPosts() throws IOException, FacebookException
    {
        List<ChannelPost> ret = new ArrayList<ChannelPost>();
        ResponseList<Post> posts = client.getPosts();
        for(Post post : posts)
        {
            ret.add(newChannelPost(post, channel));
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e)
    {
        int errorCode = getErrorCode(e);

        if(errorCode == -1)
        {
            // Sometimes if FB is down the only code you get is a 500 status code
            int statusCode = getStatusCode(e);
            if(statusCode >= 400)
                errorCode = statusCode;
        }

        return errorCode != 1     // Unknown Error
            && errorCode != 506   // Duplicate Post
            && errorCode != 200   // Permission Error
            && errorCode != 368   // Temporarily Blocked
            && errorCode != 1500  // Invalid URL
            && errorCode != 500;  // Server Error
    }

    /**
     * Returns the error code from the given exception.
     */
    public int getErrorCode(Exception e)
    {
        int ret = -1;

        if(e instanceof FacebookException)
        {
            FacebookException ex = (FacebookException)e;
            ret = ex.getErrorCode();
        }

        return ret;
    }

    /**
     * Returns the status code from the given exception.
     */
    public int getStatusCode(Exception e)
    {
        int ret = -1;

        if(e instanceof FacebookException)
        {
            FacebookException ex = (FacebookException)e;
            ret = ex.getStatusCode();
        }

        return ret;
    }

    /**
     * Returns the error message from the given exception.
     */
    public String getErrorMessage(Exception e)
    {
        String ret = "";

        if(e instanceof FacebookException)
        {
            FacebookException ex = (FacebookException)e;
            ret = ex.getErrorMessage();
        }

        return ret;
    }
}