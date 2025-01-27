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
import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.ApiClientCallback;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.UsersApi;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.model.User;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.model.Get2UsersMeResponse;
import com.twitter.clientlib.model.Get2UsersIdTweetsResponse;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;
import com.twitter.clientlib.model.TweetDeleteResponse;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.model.social.ChannelPost;

/**
 * Class that represents a connection to the Twitter v2 API for social media posts.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TwitterClient extends Client implements SocialClient
{
    private static final Logger logger = Logger.getLogger(TwitterClient.class.getName());

    public static final String SUFFIX = ".social";

    private ApiClient client;
    private TwitterCredentialsOAuth2 credentials;
    private final TweetsApi tweets = new TweetsApi();
    private final UsersApi users = new UsersApi();
    private String clientId = "";
    private String clientSecret = "";
    private String accessToken = "";
    private String refreshToken = "";
    private SocialChannel channel;

    private static User me;

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
    static public TwitterClient newClient(SocialChannel channel) throws IOException, ApiException
    {
        TwitterClient ret = new TwitterClient(channel);

        // Configure and create the twitter client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create twitter client: "+channel.getCode());

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
            logger.info("Configuring twitter client: "+channel.getCode());

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, channel.getCode().toLowerCase()+SUFFIX);
        try
        {
            // Read file from auth directory
            JSONObject obj = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            setClientId(obj.optString("clientId"));
            setClientSecret(obj.optString("clientSecret"));
            setAccessToken(obj.optString("accessToken"));
            setRefreshToken(obj.optString("refreshToken"));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read twitter auth file: "
                +e.getClass().getName()+": "+e.getMessage());
        }


        credentials = new TwitterCredentialsOAuth2(getClientId(), getClientSecret(),
            getAccessToken(), getRefreshToken(), true);

        if(debug())
            logger.info("Configured twitter client successfully: "+channel.getCode());
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException, ApiException
    {
        if(debug())
            logger.info("Creating twitter client: "+channel.getCode());

        // Create the client
        ApiClient twitter = new ApiClient();
        twitter.setTwitterCredentials(credentials);
        twitter.setBasePath("https://api.twitter.com");
        twitter.addCallback(new TokenUpdater());
        tweets.setClient(twitter);
        users.setClient(twitter);

        client = twitter;

        if(debug())
            logger.info("Created twitter client successfully: "+channel.getCode());

        return client != null;
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
     * Returns the client id for the client.
     */
    public String getClientId() 
    {
        return clientId;
    }

    /**
     * Sets the client id for the client.
     */
    public void setClientId(String clientId) 
    {
        this.clientId = clientId;
    }

    /**
     * Returns the client secret for the client.
     */
    public String getClientSecret() 
    {
        return clientSecret;
    }

    /**
     * Sets the client secret for the client.
     */
    public void setClientSecret(String clientSecret) 
    {
        this.clientSecret = clientSecret;
    }

    /**
     * Returns the access token for the client.
     */
    public String getAccessToken() 
    {
        return accessToken;
    }

    /**
     * Sets the access token for the client.
     */
    public void setAccessToken(String accessToken) 
    {
        this.accessToken = accessToken;
    }

    /**
     * Returns the refresh token for the client.
     */
    public String getRefreshToken() 
    {
        return refreshToken;
    }

    /**
     * Sets the refresh token for the client.
     */
    public void setRefreshToken(String refreshToken) 
    {
        this.refreshToken = refreshToken;
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
     * Sets the current user.
     */
    public void setMe() throws ApiException
    {
        if(me == null)
        {
            Get2UsersMeResponse result = users.findMyUser().execute();
            if(result != null)
                me = result.getData();
        }
    }

    /**
     * Returns the user name of the current account.
     */
    public String getName() throws ApiException
    {
        setMe();
        return me != null ? me.getUsername() : null;
    }

    class TokenUpdater implements ApiClientCallback
    {
        @Override
        public void onAfterRefreshToken(OAuth2AccessToken accessToken)
        {
            setAccessToken(accessToken.getAccessToken());
            setRefreshToken(accessToken.getRefreshToken());

            String directory = System.getProperty("app.auth", ".");
            File file = new File(directory, channel.getCode().toLowerCase()+SUFFIX);
            try
            {
                // Write file to auth directory
                JSONObject obj = new JSONObject();
                obj.put("clientId", getClientId());
                obj.put("clientSecret", getClientSecret());
                obj.put("accessToken", getAccessToken());
                obj.put("refreshToken", getRefreshToken());
                FileUtils.writeStringToFile(file, obj.toString(), "UTF-8");
            }
            catch(IOException e)
            {
                logger.severe("Unable to write twitter auth file: "
                    +e.getClass().getName()+": "+e.getMessage());
            }
        }
    }

    /**
     * Returns a channel post for the given tweet.
     */
    private ChannelPost newChannelPost(Tweet tweet, SocialChannel channel)
    {
        return new ChannelPost(tweet.getId(), channel, tweet.getText());
    }

    /**
     * Sends the given post.
     *
     * @param text The text of the post to be sent.
     */
    public ChannelPost sendPost(String text) throws IOException, ApiException
    {
        ChannelPost ret = null;
        TweetCreateRequest request = new TweetCreateRequest().text(text);
        TweetCreateResponse result = tweets.createTweet(request).execute();
        if(result != null)
        {
            ret = new ChannelPost(result.getData().getId(),
                channel, result.getData().getText());
        }

        return ret;
    }

    /**
     * Deletes the given post.
     *
     * @param id The id of the post to be deleted.
     */
    public ChannelPost deletePost(String id) throws IOException, ApiException
    {
        ChannelPost ret = null;

        try
        {
            TweetDeleteResponse result = tweets.deleteTweetById(id).execute();
            if(result != null)
                ret = new ChannelPost(id, channel);
        }
        catch(ApiException e)
        {
            // Post already deleted
            if(e.getCode() == 404)
                ret = new ChannelPost(id, channel);
            else
                throw e;
        }

        return ret;
    }

    /**
     * Returns the posts for the current user.
     */
    public List<ChannelPost> getPosts() throws IOException, ApiException
    {
        List<ChannelPost> ret = new ArrayList<ChannelPost>();

        setMe();
        if(me != null)
        {
            Get2UsersIdTweetsResponse result = tweets.usersIdTweets(me.getId()).execute();
            if(result != null)
            {
                for(Tweet tweet : result.getData())
                {
                    ret.add(newChannelPost(tweet, channel));
                }
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given error is recoverable.
     */
    public boolean isRecoverable(Exception e)
    {
        int errorCode = getErrorCode(e);
        return errorCode < 400 || errorCode >= 500; // 4xx error codes
    }

    /**
     * Returns the error code from the given exception.
     */
    public int getErrorCode(Exception e)
    {
        int ret = -1;
        if(e instanceof ApiException)
        {
            ApiException ex = (ApiException)e;
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
        if(e instanceof ApiException)
        {
            ApiException ex = (ApiException)e;
            ret = ex.getMessage();
        }

        return ret;
    }
}