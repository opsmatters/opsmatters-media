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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.time.Duration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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
import com.opsmatters.media.model.content.VideoSummary;
import com.opsmatters.media.model.content.VideoDetails;
import com.opsmatters.media.model.content.VideoProvider;
import com.opsmatters.media.util.StringUtils;

/**
 * Print a list of videos uploaded to the user's YouTube channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public class YouTubeClient implements VideoClient
{
    private static final Logger logger = Logger.getLogger(YouTubeClient.class.getName());

    private static YouTube youtube;

    public static final String APPLICATION = "opsmatters";
    public static final String LIST_FIELDS = "id,snippet";
    public static final String DETAIL_FIELDS = "snippet,contentDetails";

    private boolean debug = false;

    /**
     * Returns the provider for this client.
     */
    public VideoProvider getProvider()
    {
        return VideoProvider.YOUTUBE;
    }

    /**
     * Check if the YouTube Data API needs initialising.
     */
    public void checkInitialize() throws IOException
    {
        if(youtube == null)
            initialize();
    }

    /**
     * Initialise the YouTube Data API using OAuth2.
     */
    public void initialize() throws IOException
    {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        // Authorize the request.
        Credential credential = YouTubeAuth.authorize(scopes, APPLICATION);

        if(debug())
            logger.info("Creating youtube client object");

        // This object is used to make YouTube Data API requests.
        youtube = new YouTube.Builder(YouTubeAuth.HTTP_TRANSPORT, YouTubeAuth.JSON_FACTORY, credential).setApplicationName(
            APPLICATION).build();

        if(debug())
            logger.info("Created youtube client object successfully");
    }

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug()
    {
        return debug;
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Returns the details of the video for the given video ID.
     *
     * @param videoId The ID of the video to be retrieved
     */
    public VideoDetails getVideo(String videoId) throws IOException
    {
        VideoDetails ret = null;

        try
        {
            checkInitialize();

            YouTube.Videos.List videoRequest = youtube.videos().list(DETAIL_FIELDS);
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

                    ret = new VideoDetails(videoId);
                    ret.setTitle(snippet.getTitle());
                    ret.setPublishedDateMillis(snippet.getPublishedAt().getValue());
                    ret.setDescription(snippet.getDescription());
                    ret.setDuration(Duration.parse(details.getDuration()).getSeconds());
                    ret.setChannelId(snippet.getChannelId());
                    ret.setChannelTitle(snippet.getChannelTitle());
                    ret.setProvider(VideoProvider.YOUTUBE);
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
    public List<VideoSummary> listVideosOld(String channelId, String userId, int maxResults) throws IOException
    {
        List<VideoSummary> list = new ArrayList<VideoSummary>();

        try
        {
            checkInitialize();
            YouTube.Search.List searchRequest = youtube.search().list(LIST_FIELDS);
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

                    VideoSummary video = new VideoSummary(resource.getVideoId());
                    video.setTitle(snippet.getTitle());
                    video.setPublishedDateMillis(snippet.getPublishedAt().getValue());
                    video.setProvider(VideoProvider.YOUTUBE);

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
    public List<VideoSummary> listVideos(String channelId, String userId, int maxResults) throws IOException
    {
        List<VideoSummary> list = new ArrayList<VideoSummary>();

        try
        {
            checkInitialize();
            YouTube.Channels.List channelRequest = youtube.channels().list(DETAIL_FIELDS);
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

            YouTube.PlaylistItems.List itemRequest = youtube.playlistItems().list(LIST_FIELDS);
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

                    VideoSummary video = new VideoSummary(resource.getVideoId());
                    video.setTitle(snippet.getTitle());
                    video.setPublishedDateMillis(snippet.getPublishedAt().getValue());
                    video.setProvider(VideoProvider.YOUTUBE);

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