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
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.PreparedPost;

/**
 * Class that represents a connection to Twitter for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TwitterClient extends Client implements SocialClient
{
    private static final Logger logger = Logger.getLogger(TwitterClient.class.getName());

    public static final String SUFFIX = ".twitter";

    private Twitter client;
    private AccessToken accessToken;
    private String consumerKey = "";
    private String consumerSecret = "";
    private String token = "";
    private String tokenSecret = "";
    private SocialChannel channel;

    /**
     * Private constructor.
     */
    private TwitterClient(SocialChannel channel)
    {
        setChannel(channel);
    }

    /**
     * Returns a new twitter client using the given channel.
     */
    static public TwitterClient newClient(SocialChannel channel) throws IOException, TwitterException
    {
        TwitterClient ret = new TwitterClient(channel);

        // Configure and create the twitter client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create twitter client: "+channel.getId());

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public SocialProvider getProvider()
    {
        return SocialProvider.TWITTER;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring twitter client: "+channel.getId());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, channel.getId()+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setConsumerKey(obj.optString("consumerKey"));
            setConsumerSecret(obj.optString("consumerSecret"));
            setAccessToken(obj.optString("accessToken"));
            setAccessTokenSecret(obj.optString("accessTokenSecret"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read twitter auth file: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(getAccessToken() != null && getAccessToken().length() > 0)
            accessToken = new AccessToken(getAccessToken(), getAccessTokenSecret());

        if(debug())
            logger.info("Configured twitter client successfully: "+channel.getId());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException, TwitterException
    {
        if(debug())
            logger.info("Creating twitter client: "+channel.getId());

        TwitterFactory factory = new TwitterFactory();

        // Create the client
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(accessToken);

        // Issue command to test connectivity
        long id = twitter.getId();

        client = twitter;

        if(debug())
            logger.info("Created twitter client successfully: "+channel.getId());

        return id > 0L;
    }

    /**
     * Close the client.
     */
    @Override
    public void close() 
    {
        client = null;
    }

    /**
     * Returns the consumer key for the client.
     */
    public String getConsumerKey() 
    {
        return consumerKey;
    }

    /**
     * Sets the consumer key for the client.
     */
    public void setConsumerKey(String consumerKey) 
    {
        this.consumerKey = consumerKey;
    }

    /**
     * Returns the consumer secret for the client.
     */
    public String getConsumerSecret() 
    {
        return consumerSecret;
    }

    /**
     * Sets the consumer secret for the client.
     */
    public void setConsumerSecret(String consumerSecret) 
    {
        this.consumerSecret = consumerSecret;
    }

    /**
     * Returns the access token for the client.
     */
    public String getAccessToken() 
    {
        return token;
    }

    /**
     * Sets the access token for the client.
     */
    public void setAccessToken(String accessToken) 
    {
        this.token = accessToken;
    }

    /**
     * Returns the access token secret for the client.
     */
    public String getAccessTokenSecret() 
    {
        return tokenSecret;
    }

    /**
     * Sets the access token secret for the client.
     */
    public void setAccessTokenSecret(String accessTokenSecret) 
    {
        this.tokenSecret = accessTokenSecret;
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
     * Returns the screen name of the current account.
     */
    public String getName() throws IOException, TwitterException
    {
        return client.getScreenName();
    }

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public PreparedPost sendPost(String text) throws IOException, TwitterException
    {
        Status status = client.updateStatus(text);
        return status != null ? new PreparedPost(status, channel) : null;
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public PreparedPost deletePost(String id) throws IOException, TwitterException
    {
        PreparedPost ret = null;

        try
        {
            Status status = client.destroyStatus(Long.parseLong(id));
            ret = new PreparedPost(status, channel);
        }
        catch(TwitterException e)
        {
            // Post already deleted
            if(e.getStatusCode() == 404)
                ret = new PreparedPost(id, channel);
            else
                throw e;
        }

        return ret;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<PreparedPost> getPosts() throws IOException, TwitterException
    {
        List<PreparedPost> ret = new ArrayList<PreparedPost>();
        List<Status> statuses = client.getUserTimeline();
        for(Status status : statuses)
            ret.add(new PreparedPost(status, channel));
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e)
    {
        int errorCode = getErrorCode(e);
        return errorCode != 186  // Message too long
            && errorCode != 187  // Status is a duplicate
            && errorCode != 261  // Application cannot perform write actions
            && errorCode != 326; // Account Locked
    }

    /**
     * Returns the error code from the given exception.
     */
    public int getErrorCode(Exception e)
    {
        int ret = -1;

        if(e instanceof TwitterException)
        {
            TwitterException ex = (TwitterException)e;
            ret = ex.getErrorCode();
        }

        return ret;
    }

    /**
     * Returns the error message from the given exception.
     */
    public String getErrorMessage(Exception e)
    {
        String ret = "";

        if(e instanceof TwitterException)
        {
            TwitterException ex = (TwitterException)e;
            ret = ex.getErrorMessage();
        }

        return ret;
    }
}