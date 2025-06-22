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
import java.util.List;
import org.json.JSONObject;
import com.opsmatters.media.model.provider.VideoProviderId;

/**
 * Methods to interact with a user's video channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public interface VideoClient
{
    /**
     * Close the client.
     */
    public void close();

    /**
     * Returns <CODE>true</CODE> if debug is enabled.
     */
    public boolean debug();

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    public void setDebug(boolean debug);

    /**
     * Returns the provider for this client.
     */
    public VideoProviderId getProviderId();

    /**
     * Returns the details of the video for the given video ID.
     *
     * @param videoId The ID of the video to be retrieved
     */
    public JSONObject getVideo(String videoId) throws IOException;

    /**
     * Returns the list of most recent videos for the given channel ID.
     *
     * @param id The ID of the channel or user for the videos
     * @param maxResults The maximum number of results to retrieve
     * @return The list of summary videos retrieved
     */
    public List<JSONObject> listVideos(String id, int maxResults) throws IOException;
}