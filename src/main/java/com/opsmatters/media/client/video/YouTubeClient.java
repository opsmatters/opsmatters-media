/*
 * Copyright 2019 Gerald Curley
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

package com.opsmatters.media.client.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.time.Duration;
import org.json.JSONObject;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.common.collect.Lists;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.admin.VideoProviderId;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Print a list of videos uploaded to the user's YouTube channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public class YouTubeClient extends Client implements VideoClient
{
    private static final Logger logger = Logger.getLogger(YouTubeClient.class.getName());

    private static final String YT_URL = "https://yt.lemnoslife.com";

    public static final String SUFFIX = ".youtube";
    public static final String CREDENTIALS = ".oauth";

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static final String APPLICATION = "opsmatters";
    public static final List<String> ID_FIELD = Lists.newArrayList("id");
    public static final List<String> LIST_FIELDS = Lists.newArrayList("id","snippet");
    public static final List<String> DETAIL_FIELDS = Lists.newArrayList("snippet","contentDetails");

    private YouTube client;
    private Credential credential;

    /**
     * Returns a new youtube client using credentials.
     */
    static public YouTubeClient newClient() throws IOException
    {
        YouTubeClient ret = new YouTubeClient();

        // Configure and create the youtube client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create youtube client");

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public VideoProviderId getProviderId()
    {
        return VideoProviderId.YOUTUBE;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring youtube client");

        String directory = System.getProperty("app.auth", ".");

        // Load client secrets
        File secrets = new File(directory, SUFFIX);
        Reader clientSecretReader = new InputStreamReader(new FileInputStream(secrets));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        // Create the credentials datastore
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(directory, CREDENTIALS));
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(APPLICATION);

        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
            .build();

        // Build the local server and bind it to port 8099
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8099).build();

        credential = new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");

        if(debug())
            logger.info("Configured youtube client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating youtube client");

        // Used to make YouTube Data API requests
        client = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION)
            .build();

        if(debug())
            logger.info("Created youtube client successfully");

        return true;
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
     * Returns the details of the video for the given video ID.
     *
     * @param videoId The ID of the video to be retrieved
     */
    public JSONObject getVideo(String videoId) throws IOException
    {
        JSONObject ret = null;

        try
        {
            YouTube.Videos.List videoRequest = client.videos().list(DETAIL_FIELDS);
            videoRequest.setId(Lists.newArrayList(videoId));

            VideoListResponse videoResult = videoRequest.execute();
            List<Video> videoList = videoResult.getItems();

            if(debug())
                logger.info("Getting youtube video for ID: "+videoId);
            if(videoList != null)
            {
                if(videoList.isEmpty())
                {
                    logger.severe("Unable to find youtube video for ID: "+videoId);
                }
                else
                {
                    Video video = videoList.get(0);
                    VideoSnippet snippet = video.getSnippet();
                    VideoContentDetails details = video.getContentDetails();
                    if(debug())
                        logger.info("Found youtube video: "+snippet.getTitle());

                    ret = new JSONObject();
                    ret.put(VIDEO_ID.value(), videoId);
                    ret.put(TITLE.value(), snippet.getTitle());
                    ret.put(PUBLISHED_DATE.value(), snippet.getPublishedAt().toString());
                    ret.putOpt(DESCRIPTION.value(), snippet.getDescription());
                    ret.put(CHANNEL_ID.value(), snippet.getChannelId());
                    ret.put(CHANNEL_TITLE.value(), snippet.getChannelTitle());
                    ret.put(PROVIDER.value(), getProviderId().code());

                    try
                    {
                        ret.put(DURATION.value(), Duration.parse(details.getDuration()).getSeconds());
                    }
                    catch(NullPointerException e)
                    {
                        ret.put(DURATION.value(), 0);
                    }
                }
            }
        }
        catch(GoogleJsonResponseException e)
        {
            String message = e.getDetails().getCode()+": "+e.getDetails().getMessage();
            logger.severe("Service error: "+message);

            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }

        return ret;
    }

    /**
     * Returns the list of most recent videos for the given channel ID.
     *
     * @param channelId The ID of the channel for the videos
     * @param userId The ID of the user for the videos (not used)
     * @param maxResults The maximum number of results to retrieve
     * @return The list of summary videos retrieved
     */
    public List<JSONObject> listVideosOld(String channelId, String userId, int maxResults) throws IOException
    {
        List<JSONObject> list = new ArrayList<JSONObject>();

        try
        {
            YouTube.Search.List searchRequest = client.search().list(LIST_FIELDS);
            searchRequest.setChannelId(channelId);
            searchRequest.setMaxResults((long)maxResults);
            searchRequest.setType(Lists.newArrayList("video"));
            searchRequest.setOrder("date");

            if(debug())
                logger.info("Search for youtube videos for channel: "+channelId);

            SearchListResponse searchResult = searchRequest.execute();
            List<SearchResult> searchList = searchResult.getItems();

            if(searchList != null)
            {
                if(debug())
                    logger.info("Found "+searchList.size()+" youtube videos for channel: "+channelId);
                for(SearchResult result : searchList)
                {
                    ResourceId resource = result.getId();
                    SearchResultSnippet snippet = result.getSnippet();

                    JSONObject video = new JSONObject();
                    video.put(VIDEO_ID.value(), resource.getVideoId());
                    video.put(TITLE.value(), snippet.getTitle());
                    video.put(PUBLISHED_DATE.value(), snippet.getPublishedAt().toString());
                    video.put(PROVIDER.value(), getProviderId().code());

                    list.add(video);
                }
            }
            else
            {
                if(debug())
                    logger.info("No youtube videos found for channel: "+channelId);
            }
        }
        catch (GoogleJsonResponseException e)
        {
            String message = e.getDetails().getCode()+": "+e.getDetails().getMessage();
            logger.severe("Service error: "+message);

            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }

        return list;
    }

    /**
     * Returns the list of most recent videos for the given channel ID.
     *
     * @param channelId The ID of the channel for the videos
     * @param userId The ID of the user for the videos (not used)
     * @param maxResults The maximum number of results to retrieve
     * @return The list of summary videos retrieved
     */
    public List<JSONObject> listVideos(String channelId, int maxResults) throws IOException
    {
        List<JSONObject> list = new ArrayList<JSONObject>();

        try
        {
            YouTube.Channels.List channelRequest = client.channels().list(DETAIL_FIELDS);
            channelRequest.setId(Lists.newArrayList(channelId));
            channelRequest.setMaxResults((long)maxResults);

            if(debug())
                logger.info("Search for youtube channels: "+channelId);

            ChannelListResponse channelResult = channelRequest.execute();
            List<Channel> channelList = channelResult.getItems();

            String playlistId = null;
            if(channelList != null)
            {
                if(debug())
                    logger.info("Found "+channelList.size()+" channels: "+channelId);
                if(channelList.isEmpty())
                {
                    logger.severe("Unable to find youtube channel for ID: "+channelId);
                }
                else
                {
                    Channel channel = channelList.get(0);
                    ChannelSnippet snippet = channel.getSnippet();
                    ChannelContentDetails details = channel.getContentDetails();
                    playlistId = details.getRelatedPlaylists().getUploads();
                    if(debug())
                        logger.info("Found youtube upload playlist: "+playlistId);
                }
            }

            // First look for a playlist for the channel
            if(playlistId != null)
            {
                if(debug())
                    logger.info("Search for youtube videos for playlistId: "+playlistId);

                YouTube.PlaylistItems.List itemRequest = client.playlistItems().list(LIST_FIELDS);
                itemRequest.setPlaylistId(playlistId);
                itemRequest.setMaxResults((long)maxResults);

                PlaylistItemListResponse itemResult = itemRequest.execute();
                List<PlaylistItem> itemList = itemResult.getItems();

                if(itemList != null)
                {
                    if(debug())
                        logger.info("Found "+itemList.size()+" youtube videos for playlist: "+playlistId);
                    for(PlaylistItem item : itemList)
                    {
                        PlaylistItemSnippet snippet = item.getSnippet();
                        ResourceId resource = snippet.getResourceId();

                        JSONObject video = new JSONObject();
                        video.put(VIDEO_ID.value(), resource.getVideoId());
                        video.put(TITLE.value(), snippet.getTitle());
                        video.put(PUBLISHED_DATE.value(), snippet.getPublishedAt().toString());
                        video.put(CHANNEL_TITLE.value(), snippet.getChannelTitle());
                        video.put(PROVIDER.value(), getProviderId().code());

                        list.add(video);
                    }
                }
                else
                {
                    if(debug())
                        logger.info("No youtube videos found for channel: "+channelId);
                }
            }
            else // Search by channel id instead
            {
                YouTube.Search.List searchRequest = client.search().list(LIST_FIELDS);
                searchRequest.setChannelId(channelId);
                searchRequest.setMaxResults((long)maxResults);
                searchRequest.setType(Lists.newArrayList("video"));
                searchRequest.setOrder("date");

                if(debug())
                    logger.info("Search for youtube videos for channel: "+channelId);

                SearchListResponse searchResult = searchRequest.execute();
                List<SearchResult> searchList = searchResult.getItems();

                if(searchList != null)
                {
                    if(debug())
                        logger.info("Found "+searchList.size()+" youtube videos for channel: "+channelId);
                    for(SearchResult result : searchList)
                    {
                        ResourceId resource = result.getId();
                        SearchResultSnippet snippet = result.getSnippet();

                        JSONObject video = new JSONObject();
                        video.put(VIDEO_ID.value(), resource.getVideoId());
                        video.put(TITLE.value(), snippet.getTitle());
                        video.put(PUBLISHED_DATE.value(), snippet.getPublishedAt().toString());
                        video.put(CHANNEL_TITLE.value(), snippet.getChannelTitle());
                        video.put(PROVIDER.value(), getProviderId().code());

                        list.add(video);
                    }
                }
                else
                {
                    if(debug())
                        logger.info("No youtube videos found for channel: "+channelId);
                }
            }
        }
        catch (GoogleJsonResponseException e)
        {
            String message = e.getDetails().getCode()+": "+e.getDetails().getMessage();
            logger.severe("Service error: "+message);

            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }

        return list;
    }

    /**
     * Returns the channel ID for the given handle.
     *
     * @param handle The handle of the channel
     * @return channel ID of the channel
     */
    public String getChannelIdFromHandle(String handle) throws IOException
    {
        String ret = null;

        try
        {
            YouTube.Channels.List channelRequest = client.channels().list(ID_FIELD);
            channelRequest.setForHandle(handle);

            if(debug())
                logger.info("Search for youtube handle: "+handle);

            ChannelListResponse channelResult = channelRequest.execute();
            List<Channel> channelList = channelResult.getItems();
            if(channelList != null)
            {
                if(debug())
                    logger.info("Found "+channelList.size()+" channels: "+handle);
                if(channelList.isEmpty())
                {
                    logger.severe("Unable to find youtube channel for handle: "+handle);
                }
                else
                {
                    Channel channel = channelList.get(0);
                    ChannelSnippet snippet = channel.getSnippet();
                    ret = channel.getId();
                    if(debug())
                        logger.info("Found youtube channel: "+channel.getId());
                }
            }
        }
        catch (GoogleJsonResponseException e)
        {
            String message = e.getDetails().getCode()+": "+e.getDetails().getMessage();
            logger.severe("Service error: "+message);

            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }

        return ret;
    }

    /**
     * Returns the channel ID for the given user ID.
     *
     * @param userId The userId of the channel
     * @return channel ID of the channel
     */
    public String getChannelIdFromUserId(String userId) throws IOException
    {
        String ret = null;

        try
        {
            YouTube.Channels.List channelRequest = client.channels().list(ID_FIELD);
            channelRequest.setForUsername(userId);

            if(debug())
                logger.info("Search for youtube userId: "+userId);

            ChannelListResponse channelResult = channelRequest.execute();
            List<Channel> channelList = channelResult.getItems();
            if(channelList != null)
            {
                if(debug())
                    logger.info("Found "+channelList.size()+" channels: "+userId);
                if(channelList.isEmpty())
                {
                    logger.severe("Unable to find youtube channel for userId: "+userId);
                }
                else
                {
                    Channel channel = channelList.get(0);
                    ChannelSnippet snippet = channel.getSnippet();
                    ret = channel.getId();
                   if(debug())
                        logger.info("Found youtube channel: "+channel.getId());
                }
            }
        }
        catch (GoogleJsonResponseException e)
        {
            String message = e.getDetails().getCode()+": "+e.getDetails().getMessage();
            logger.severe("Service error: "+message);

            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }

        return ret;
    }
}