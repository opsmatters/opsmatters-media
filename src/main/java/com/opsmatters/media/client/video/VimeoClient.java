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
import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoResponse;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import com.opsmatters.media.client.Client;
import com.opsmatters.media.model.admin.VideoProviderId;
import com.opsmatters.media.util.FormatUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Print a list of videos uploaded to the user's Vimeo channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public class VimeoClient extends Client implements VideoClient
{
    private static final Logger logger = Logger.getLogger(VimeoClient.class.getName());

    public static final String SUFFIX = ".vimeo";
    public static final String LIST_FIELDS = "uri,name,created_time";
    public static final String DETAIL_FIELDS = "uri,name,created_time,description,duration,user.name,user.link";

    private Vimeo client;
    private String accessToken = "";

    /**
     * Returns a new vimeo client using an access token.
     */
    static public VimeoClient newClient() throws IOException
    {
        VimeoClient ret = new VimeoClient();

        // Configure and create the vimeo client
        ret.configure();
        if(!ret.create())
            logger.severe("Unable to create vimeo client");

        return ret;
    }

    /**
     * Returns the provider for this client.
     */
    public VideoProviderId getProviderId()
    {
        return VideoProviderId.VIMEO;
    }

    /**
     * Configure the client.
     */
    @Override
    public void configure() throws IOException
    {
        if(debug())
            logger.info("Configuring vimeo client");

        String directory = System.getProperty("app.auth", ".");

        File file = new File(directory, SUFFIX);
        try
        {
            // Read access token from auth directory
            accessToken = FileUtils.readFileToString(file, "UTF-8");
        }
        catch(IOException e)
        {
            logger.severe("Unable to read vimeo access token: "+e.getClass().getName()+": "+e.getMessage());
        }

        if(debug())
            logger.info("Configured vimeo client successfully");
    }

    /**
     * Create the client using the configured credentials.
     */
    @Override
    public boolean create() throws IOException
    {
        if(debug())
            logger.info("Creating vimeo client");

        // Authenticate using access token
        if(accessToken != null && accessToken.length() > 0)
            client = new Vimeo(accessToken);

        if(debug())
            logger.info("Created vimeo client successfully");

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
                logger.info("Getting vimeo video for ID: "+videoId);

            String endpoint = String.format("/videos/%s?fields=%s",
                videoId, DETAIL_FIELDS);
            VimeoResponse response = client.get(endpoint);
            JSONObject item = response.getJson();

            if(item != null)
            {
                if(debug())
                    logger.info("Found vimeo video: "+item.optString("name"));

                JSONObject user = item.getJSONObject("user");
                String link = user.optString("link");

                ret = new JSONObject();
                ret.put(VIDEO_ID.value(), videoId);
                ret.put(TITLE.value(), item.optString("name"));
                ret.put(PUBLISHED_DATE.value(), item.optString("created_time"));
                ret.putOpt(DESCRIPTION.value(), item.optString("description"));
                ret.put(DURATION.value(), item.optLong("duration"));
                ret.put(CHANNEL_ID.value(), link.substring(link.lastIndexOf("/")+1));
                ret.put(CHANNEL_TITLE.value(), user.optString("name"));
                ret.put(PROVIDER.value(), getProviderId().code());
            }
            else
            {
                logger.severe("Unable to find vimeo video for ID: "+videoId);
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
     * @param userId The ID of the user for the videos
     * @param maxResults The maximum number of results to retrieve
     * @return The list of summary videos retrieved
     */
    public List<JSONObject> listVideos(String userId, int maxResults) throws IOException
    {
        List<JSONObject> list = new ArrayList<JSONObject>();

        try
        {
            if(debug())
                logger.info("Search for vimeo videos for user: "+userId);

            String endpoint = String.format("/users/%s/videos?fields=%s&sort=date&direction=desc&per_page=%d",
                userId, LIST_FIELDS, maxResults);
            VimeoResponse response = client.get(endpoint);
            JSONArray data = response.getJson().getJSONArray("data");

            if(data != null && data.length() > 0)
            {
                if(debug())
                    logger.info("Found "+data.length()+" vimeo videos for user: "+userId);
                for(int i = 0; i < data.length(); i++)
                {
                    JSONObject item = data.getJSONObject(i);
                    String uri = item.optString("uri");

                    JSONObject video = new JSONObject();
                    video.put(VIDEO_ID.value(), uri.substring(uri.lastIndexOf("/")+1));
                    video.put(TITLE.value(), item.optString("name"));
                    video.put(PUBLISHED_DATE.value(), item.optString("created_time"));
                    video.put(PROVIDER.value(), getProviderId().code());

                    list.add(video);
                }
            }
            else
            {
                if(debug())
                    logger.info("No vimeo videos found for user: "+userId);
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