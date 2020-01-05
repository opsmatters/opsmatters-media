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
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.time.format.DateTimeParseException;
import com.opsmatters.wistia.Wistia;
import com.opsmatters.wistia.WistiaResponse;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import com.opsmatters.media.model.content.VideoSummary;
import com.opsmatters.media.model.content.VideoDetails;
import com.opsmatters.media.model.content.VideoProvider;
import com.opsmatters.media.util.FormatUtils;

/**
 * Print a list of videos uploaded to the user's Wistia channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public class WistiaClient implements VideoClient
{
    private static final Logger logger = Logger.getLogger(WistiaClient.class.getName());

    private static Wistia wistia;

    public static final String AUTH = ".wistia";

    private boolean debug = false;

    /**
     * Returns the provider for this client.
     */
    public VideoProvider getProvider()
    {
        return VideoProvider.WISTIA;
    }

    /**
     * Check if the Wistia client needs initialising.
     */
    public void checkInitialize() throws IOException
    {
        if(wistia == null)
            initialize();
    }

    /**
     * Initialise the Wistia client using OAuth2.
     */
    public void initialize() throws IOException
    {
        String directory = System.getProperty("om-config.auth", ".");

        if(debug())
            logger.info("Creating wistia client object");

        // This object is used to make Wistia API requests
        File file = new File(directory, AUTH);
        wistia = new Wistia(FileUtils.readFileToString(file, "UTF-8"));

        if(debug())
            logger.info("Created wistia client object successfully");
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

            if(debug())
                logger.info("Getting wistia video for ID: "+videoId);

            String endpoint = String.format("/medias/%s.json", videoId);
            WistiaResponse response = wistia.get(endpoint);
            JSONObject item = response.getJson();

            if(item != null && item.has("project"))
            {
                if(debug())
                    logger.info("Found wistia video: "+item.optString("name"));

                JSONObject project = item.getJSONObject("project");

                ret = new VideoDetails(videoId);
                ret.setTitle(item.optString("name"));
                ret.setPublishedDateAsString(item.optString("created"), "yyyy-MM-dd'T'HH:mm:ssX");
                ret.setDescription(item.optString("description"));
                ret.setDuration(item.optLong("duration"));
                ret.setChannelId(project.optString("hashed_id"));
                ret.setChannelTitle(project.optString("name"));
                ret.setProvider(VideoProvider.WISTIA);
            }
            else
            {
                logger.severe("Unable to find wistia video for ID: "+videoId);
            }
        }
        catch(DateTimeParseException e)
        {
            String message = e.getMessage();
            logger.severe("Parse error: "+message);

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
     * @param userId The ID of the user for the videos
     * @param maxResults The maximum number of results to retrieve
     * @return The list of summary videos retrieved
     */
    public List<VideoSummary> listVideos(String channelId, String userId, int maxResults) throws IOException
    {
        List<VideoSummary> list = new ArrayList<VideoSummary>();

        try
        {
            checkInitialize();

            if(debug())
                logger.info("Search for wistia videos for channel: "+channelId);

            String endpoint = String.format("/medias.json?project_id=%s&type=Video&sort_by=created&sort_direction=0&per_page=%d",
                channelId, maxResults);
            WistiaResponse response = wistia.get(endpoint);
            JSONArray data = response.getJsonArray();

            if(data != null && data.length() > 0)
            {
                if(debug())
                    logger.info("Found "+data.length()+" wistia videos for channel: "+channelId);
                for(int i = 0; i < data.length(); i++)
                {
                    JSONObject item = data.getJSONObject(i);

                    VideoSummary video = new VideoSummary(item.optString("hashed_id"));
                    video.setTitle(item.optString("name"));
                    video.setPublishedDateAsString(item.optString("created"), "yyyy-MM-dd'T'HH:mm:ssX");
                    video.setProvider(VideoProvider.WISTIA);

                    list.add(video);
                }
            }
            else
            {
                if(debug())
                    logger.info("No wistia videos found for channel: "+channelId);
            }

        }
        catch(DateTimeParseException e)
        {
            String message = e.getMessage();
            logger.severe("Parse error: "+message);

            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }

        return list;
    }
}