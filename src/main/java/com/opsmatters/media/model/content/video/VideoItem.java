/*
 * Copyright 2023 Gerald Curley
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

import com.opsmatters.media.model.content.ArticleItem;

/**
 * Class representing a video list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoItem extends ArticleItem<Video>
{
    private Video content = new Video();

    /**
     * Default constructor.
     */
    public VideoItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public VideoItem(VideoItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a video.
     */
    public VideoItem(Video obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Video obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Video get()
    {
        return content;
    }

    /**
     * Returns the video type.
     */
    public String getVideoType()
    {
        return content.getVideoType();
    }

    /**
     * Sets the video type.
     */
    public void setVideoType(String videoType)
    {
        content.setVideoType(videoType);
    }

    /**
     * Sets the video type.
     */
    public void setVideoType(VideoType videoType)
    {
        setVideoType(videoType.value());
    }

    /**
     * Returns the video ID.
     */
    public String getVideoId()
    {
        return content.getVideoId();
    }

    /**
     * Sets the video ID.
     */
    public void setVideoId(String videoId)
    {
        content.setVideoId(videoId);
    }

    /**
     * Returns <CODE>true</CODE> if the video ID has been set.
     */
    public boolean hasVideoId()
    {
        return content.hasVideoId();
    }

    /**
     * Returns the video duration (in seconds).
     */
    public long getDuration()
    {
        return content.getDuration();
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration(boolean replaceZero)
    {
        return content.getFormattedDuration(replaceZero);
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
        content.setDuration(duration);
    }
}