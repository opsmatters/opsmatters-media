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

import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a video.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoDetails extends VideoSummary
{
    private String description = "";
    private long duration = -1L;
    private String channelId = "";
    private String channelTitle = "";

    /**
     * Default constructor.
     */
    public VideoDetails()
    {
    }

    /**
     * Copy constructor.
     */
    public VideoDetails(VideoSummary obj)
    {
        super(obj);
    }

    /**
     * Constructor that takes a video ID.
     */
    public VideoDetails(String id)
    {
        super(id);
    }

    /**
     * Returns the video description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the video description.
     */
    public void setDescription(String description)
    {
        this.description = description;
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
    public String getFormattedDuration(String dflt)
    {
        String ret = TimeUtils.toStringUTC(duration*1000L, Formats.TIME_FORMAT);
        if(duration == 0 && dflt != null && dflt.length() > 0)
            ret = dflt;
        return ret;
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration()
    {
        return getFormattedDuration(null);
    }

    /**
     * Sets the video duration (in seconds).
     */
    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    /**
     * Returns the ID of the video channel.
     */
    public String getChannelId()
    {
        return channelId;
    }

    /**
     * Sets the ID of the video channel.
     */
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    /**
     * Returns <CODE>true</CODE> if the channel ID has been set.
     */
    public boolean hasChannelId()
    {
        return channelId != null && channelId.length() > 0;
    }

    /**
     * Returns the url of the video channel.
     */
    public String getChannelUrl()
    {
        return getProvider() != null ? String.format(getProvider().channelUrl(), channelId) : null;
    }

    /**
     * Returns the title of the video channel.
     */
    public String getChannelTitle()
    {
        return channelTitle;
    }

    /**
     * Sets the title of the video channel.
     */
    public void setChannelTitle(String channelTitle)
    {
        this.channelTitle = channelTitle;
    }

    /**
     * Returns <CODE>true</CODE> if the channel title has been set.
     */
    public boolean hasChannelTitle()
    {
        return channelTitle != null && channelTitle.length() > 0;
    }
}