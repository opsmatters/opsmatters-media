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
package com.opsmatters.media.model.content.video;

import com.opsmatters.media.model.feed.video.YouTubeEntry;
import com.opsmatters.media.model.content.ArticleTeaser;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a video teaser.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoTeaser extends ArticleTeaser
{
    private String videoId = "";
    private VideoProvider provider;
    private long duration = -1L;

    /**
     * Default constructor.
     */
    public VideoTeaser()
    {
    }

    /**
     * Constructor that takes a video ID.
     */
    public VideoTeaser(String videoId)
    {
        setVideoId(videoId);
    }

    /**
     * Copy constructor.
     */
    public VideoTeaser(VideoTeaser obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoTeaser obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setVideoId(obj.getVideoId());
            setProvider(obj.getProvider());
            setDuration(obj.getDuration());
        }
    }

    /**
     * Constructor that takes a youtube feed entry.
     */
    public VideoTeaser(YouTubeEntry entry)
    {
        setVideoId(entry.getVideoId());
        setProvider(VideoProvider.YOUTUBE);
        setTitle(entry.getTitle());
        if(entry.getUpdatedDate() != null)
            setPublishedDate(entry.getUpdatedDate());
        else
            setPublishedDate(entry.getPublishedDate());
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
     * Sets the video provider.
     */
    public void setProvider(String provider)
    {
        setProvider(VideoProvider.fromCode(provider));
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

    /**
     * Returns the video duration (in seconds).
     */
    public long getDuration()
    {
        return duration;
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration(boolean replaceZero)
    {
        String ret = TimeUtils.toStringUTC(duration*1000L, Formats.TIME_FORMAT);
        if(replaceZero)
        {
            if(duration == 0L)
                ret = "N/A";
            else if (duration < 0L)
                ret = "-";
        }

        return ret;
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration()
    {
        return getFormattedDuration(false);
    }

    /**
     * Sets the video duration (in seconds).
     */
    public void setDuration(long duration)
    {
        this.duration = duration;
    }
}