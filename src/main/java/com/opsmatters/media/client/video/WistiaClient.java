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
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.content.VideoProvider;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.wistia.Wistia;
import com.opsmatters.wistia.WistiaResponse;

/**
 * Print a list of videos uploaded to the user's Wistia channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public class WistiaClient extends Client implements VideoClient
{
    private static final Logger logger = Logger.getLogger(WistiaClient.class.getName());

    public static final String AUTH = ".wistia";

    private Wistia client;
    private String accessToken = "";

    /**
     * Returns a new wistia client using an access token.
     */
    static public WistiaClient newClient() throws IOException
    {
        WistiaClient ret = new WistiaClient();

        // Configure and create the wistia client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create wistia client");

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public VideoProvider getProvider()
    {
        return VideoProvider.WISTIA;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring wistia client");

        String directory = System.getProperty("app.auth", ".");

        File auth = new File(directory, AUTH);
        try
        {
            // Read access token from auth directory
            accessToken = FileUtils.readFileToString(auth, "UTF-8");
        }
        catch(IOException e)
        {
            logger.severe("Unable to read wistia access token: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured wistia client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating wistia client");

        // Authenticate using access token
        if(accessToken != null && accessToken.length() > 0)
            client = new Wistia(accessToken);

        if(debug())
            logger.info("Created wistia client successfully");

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
            if(debug())
                logger.info("Getting wistia video for ID: "+videoId);

            String endpoint = String.format("/medias/%s.json", videoId);
            WistiaResponse response = client.get(endpoint);
            JSONObject item = response.getJson();

            if(item != null && item.has("project"))
            {
                if(debug())
                    logger.info("Found wistia video: "+item.optString("name"));

                JSONObject project = item.getJSONObject("project");

                ret = new JSONObject();
                ret.put(Fields.VIDEO_ID, videoId);
                ret.put(Fields.TITLE, item.optString("name"));
                ret.put(Fields.PUBLISHED_DATE, item.optString("created"));
                ret.putOpt(Fields.DESCRIPTION, item.optString("description"));
                ret.put(Fields.DURATION, item.optLong("duration"));
                ret.put(Fields.CHANNEL_ID, project.optString("hashed_id"));
                ret.put(Fields.CHANNEL_TITLE, project.optString("name"));
                ret.put(Fields.PROVIDER, VideoProvider.WISTIA.code());
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
    public List<JSONObject> listVideos(String channelId, String userId, int maxResults) throws IOException
    {
        List<JSONObject> list = new ArrayList<JSONObject>();

        try
        {
            if(debug())
                logger.info("Search for wistia videos for channel: "+channelId);

            String endpoint = String.format("/medias.json?project_id=%s&type=Video&sort_by=created&sort_direction=0&per_page=%d",
                channelId, maxResults);
            WistiaResponse response = client.get(endpoint);
            JSONArray data = response.getJsonArray();

            if(data != null && data.length() > 0)
            {
                if(debug())
                    logger.info("Found "+data.length()+" wistia videos for channel: "+channelId);
                for(int i = 0; i < data.length(); i++)
                {
                    JSONObject item = data.getJSONObject(i);

                    JSONObject video = new JSONObject();
                    video.put(Fields.VIDEO_ID, item.optString("hashed_id"));
                    video.put(Fields.TITLE, item.optString("name"));
                    video.put(Fields.PUBLISHED_DATE, item.optString("created"));
                    video.put(Fields.PROVIDER, VideoProvider.WISTIA.code());

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