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

/**
 * Class representing a video.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoDetails extends VideoTeaser
{
    private String description = "";
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
    public VideoDetails(VideoDetails obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoDetails obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setDescription(obj.getDescription());
            setChannelId(obj.getChannelId());
            setChannelTitle(obj.getChannelTitle());
        }
    }

    /**
     * Constructor that takes a teaser.
     */
    public VideoDetails(VideoTeaser obj)
    {
        super(obj);
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