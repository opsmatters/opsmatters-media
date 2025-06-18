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
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.VideoProviderId;
import com.opsmatters.media.model.admin.VideoProvider;

/**
 * Factory class to create a client for a video provider.
 *
 * @author Gerald Curley (opsmatters)
 */
public class VideoClientFactory
{
    private static final Logger logger = Logger.getLogger(VideoClientFactory.class.getName());

    /**
     * Private constructor.
     */
    private VideoClientFactory()
    {
    }

    /**
     * Returns a client for the given provider.
     */
    public static VideoClient newClient(VideoProviderId providerId) throws IOException
    {
        switch(providerId)
        {
            case YOUTUBE:
                return YouTubeClient.newClient();
            case VIMEO:
                return VimeoClient.newClient();
            case WISTIA:
                return WistiaClient.newClient();
        }

        throw new IllegalArgumentException("Video provider id not found: "+providerId);
    }

    /**
     * Returns a client for the given provider.
     */
    public static VideoClient newClient(VideoProvider provider) throws IOException
    {
        return newClient(provider.getProviderId());
    }
}