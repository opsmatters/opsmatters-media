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
import com.opsmatters.media.model.social.SocialPost;

/**
 * Class that represents a connection to Twitter for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TwitterClient extends Client implements SocialClient
{
    private static final Logger logger = Logger.getLogger(TwitterClient.class.getName());

    public static final String AUTH = ".twitter";

    private static Twitter client;
    private static AccessToken accessToken;
    private String consumerKey = "";
    private String consumerSecret = "";
    private String token = "";
    private String tokenSecret = "";

    /**
     * Returns a new twitter client using credentials.
     */
    static public TwitterClient newClient() throws IOException
    {
        TwitterClient ret = new TwitterClient();

        // Configure and create the twitter client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create twitter client");

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
            logger.info("Configuring twitter client");

        String directory = System.getProperty("om-config.auth", ".");

        File auth = new File(directory, AUTH);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(auth, "UTF-8"));
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
            logger.info("Configured twitter client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating twitter client");

        TwitterFactory factory = new TwitterFactory();

        // Create the client
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(accessToken);

        // Issue command to test connectivity
        long id = 0L;
        try
        {
            id = twitter.getId();
        }
        catch(TwitterException e)
        {
            throwException(e);
        }

        client = twitter;

        if(debug())
            logger.info("Created twitter client successfully");

        return id > 0L;
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
     * Sets the acess token for the client.
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
     * Sets the acess token secret for the client.
     */
    public void setAccessTokenSecret(String accessTokenSecret) 
    {
        this.tokenSecret = accessTokenSecret;
    }

    /**
     * Returns the given TwitterException as an IOException.
     */
    private void throwException(TwitterException e) throws IOException
    {
        IOException ex = new IOException("Twitter Error: "+e.getStatusCode()+": "+e.getErrorCode());
        ex.initCause(e);
        throw ex;
    }

    /**
     * Returns the screen name of the connected account.
     */
    public String getName() throws IOException
    {
        String ret = "";

        try
        {
            ret = client.getScreenName();
        }
        catch(TwitterException e)
        {
            throwException(e);
        }

        return ret;
    }

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public SocialPost sendPost(String text) throws IOException, TwitterException
    {
        Status status = client.updateStatus(text);
        return status != null ? new SocialPost(status) : null;
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public SocialPost deletePost(String id) throws IOException, TwitterException
    {
        Status status = client.destroyStatus(Long.parseLong(id));
        return status != null ? new SocialPost(status) : null;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<SocialPost> getPosts() throws IOException, TwitterException
    {
        List<SocialPost> ret = new ArrayList<SocialPost>();
        List<Status> tweets = client.getUserTimeline();
        for(Status tweet : tweets)
            ret.add(new SocialPost(tweet));
        return ret;
    }
}