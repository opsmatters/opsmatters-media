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
import com.opsmatters.media.model.content.VideoSummary;
import com.opsmatters.media.model.content.VideoDetails;
import com.opsmatters.media.model.content.VideoProvider;

/**
 * Methods to interact with a user's video channel.
 *
 * @author Gerald Curley (opsmatters)
 */
public interface VideoClient
{
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
    public VideoProvider getProvider();

    /**
     * Returns the details of the video for the given video ID.
     *
     * @param videoId The ID of the video to be retrieved
     */
    public VideoDetails getVideo(String videoId) throws IOException;

    /**
     * Returns the list of most recent videos for the given channel ID.
     *
     * @param channelId The ID of the channel for the videos
     * @param userId The ID of the user for the videos
     * @param maxResults The maximum number of results to retrieve
     * @return The list of summary videos retrieved
     */
    public List<VideoSummary> listVideos(String channelId, String userId, int maxResults) throws IOException;
}