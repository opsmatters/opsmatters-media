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
package com.opsmatters.media.model.content;

import java.util.logging.Logger;

/**
 * Class representing a video summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoSummary extends ArticleSummary
{
    private static final Logger logger = Logger.getLogger(VideoSummary.class.getName());

    private String videoId = "";
    private VideoProvider provider;

    /**
     * Default constructor.
     */
    public VideoSummary()
    {
    }

    /**
     * Constructor that takes a video ID.
     */
    public VideoSummary(String videoId)
    {
        setVideoId(videoId);
    }

    /**
     * Copy constructor.
     */
    public VideoSummary(VideoSummary obj)
    {
        super(obj);

        if(obj != null)
        {
            setVideoId(obj.getVideoId());
            setProvider(obj.getProvider());
        }
    }

    /**
     * Returns the video ID.
     */
    public String toString()
    {
        return getVideoId();
    }

    /**
     * Returns the video ID.
     */
    @Override
    public String getUniqueId()
    {
        return getVideoId();
    }

    /**
     * Returns the video ID.
     */
    public String getVideoId()
    {
        return videoId;
    }

    /**
     * Sets the video ID.
     */
    public void setVideoId(String videoId)
    {
        this.videoId = videoId;
    }

    /**
     * Returns <CODE>true</CODE> if the video ID has been set.
     */
    public boolean hasVideoId()
    {
        return videoId != null && videoId.length() > 0;
    }

    /**
     * Returns the video provider.
     */
    public VideoProvider getProvider()
    {
        return provider;
    }

    /**
     * Sets the video provider.
     */
    public void setProvider(VideoProvider provider)
    {
        this.provider = provider;
    }

    /**
     * Returns the url of the video.
     */
    public String getVideoUrl()
    {
        return provider != null ? String.format(provider.videoUrl(), videoId) : null;
    }

    /**
     * Returns the embed link of the video.
     */
    public String getEmbed(int width, int height, boolean autoplay)
    {
        return provider != null ? String.format(provider.embed(), videoId, autoplay ? "1" : "0", width, height) : "";
    }
}