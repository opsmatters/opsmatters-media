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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.time.Duration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.ResourceId;
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
import org.json.JSONObject;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.content.VideoProvider;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.util.StringUtils;

/**
 * Print a list of videos uploaded to the user's YouTube channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public class YouTubeClient extends Client implements VideoClient
{
    private static final Logger logger = Logger.getLogger(YouTubeClient.class.getName());

    public static final String AUTH = ".youtube";
    public static final String CREDENTIALS = ".oauth";

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static final String APPLICATION = "opsmatters";
    public static final String LIST_FIELDS = "id,snippet";
    public static final String DETAIL_FIELDS = "snippet,contentDetails";

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
    public VideoProvider getProvider()
    {
        return VideoProvider.YOUTUBE;
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
        File secrets = new File(directory, AUTH);
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
            videoRequest.setId(videoId);

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
                    ret.put(Fields.VIDEO_ID, videoId);
                    ret.put(Fields.TITLE, snippet.getTitle());
                    ret.put(Fields.PUBLISHED_DATE, snippet.getPublishedAt().toString());
                    ret.putOpt(Fields.DESCRIPTION, snippet.getDescription());
                    ret.put(Fields.DURATION, Duration.parse(details.getDuration()).getSeconds());
                    ret.put(Fields.CHANNEL_ID, snippet.getChannelId());
                    ret.put(Fields.CHANNEL_TITLE, snippet.getChannelTitle());
                    ret.put(Fields.PROVIDER, VideoProvider.YOUTUBE.code());
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
            searchRequest.setType("video");
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
                    video.put(Fields.VIDEO_ID, resource.getVideoId());
                    video.put(Fields.TITLE, snippet.getTitle());
                    video.put(Fields.PUBLISHED_DATE, snippet.getPublishedAt().toString());
                    video.put(Fields.PROVIDER, VideoProvider.YOUTUBE.code());

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
    public List<JSONObject> listVideos(String channelId, String userId, int maxResults) throws IOException
    {
        List<JSONObject> list = new ArrayList<JSONObject>();

        try
        {
            YouTube.Channels.List channelRequest = client.channels().list(DETAIL_FIELDS);
            channelRequest.setId(channelId);
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
                    video.put(Fields.VIDEO_ID, resource.getVideoId());
                    video.put(Fields.TITLE, snippet.getTitle());
                    video.put(Fields.PUBLISHED_DATE, snippet.getPublishedAt().toString());
                    video.put(Fields.PROVIDER, VideoProvider.YOUTUBE.code());

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
}