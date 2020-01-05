/*
 * Copyright 2018 Gerald Curley
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

/**
 * Represents a video provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum VideoProvider
{
    YOUTUBE("youtube", 
        "https://www.youtube.com/channel/%s",
        "https://www.youtube.com/watch?v=%s",
        "<iframe src=\"https://www.youtube.com/embed/%s?modestbranding=1&autohide=1&autoplay=%s\" width=\"%d\" height=\"%d\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"),
    VIMEO("vimeo", 
        "https://vimeo.com/%s",
        "https://vimeo.com/%s",
        "<iframe src=\"https://player.vimeo.com/video/%s?title=0&byline=0&portrait=0\" width=\"%d&autoplay=%s\" height=\"%d\" frameborder=\"0\" allow=\"autoplay; fullscreen\" allowfullscreen></iframe>"),
    WISTIA("wistia", 
        "https://fast.wistia.com/projects/%s",
        "https://fast.wistia.com/medias/%s",
        "<iframe src=\"//fast.wistia.net/embed/iframe/%s?autoplay=%s\" allowtransparency=\"true\" frameborder=\"0\" scrolling=\"no\" class=\"wistia_embed\" name=\"wistia_embed\" allowfullscreen mozallowfullscreen webkitallowfullscreen oallowfullscreen msallowfullscreen width=\"%d\" height=\"%d\"></iframe>");

    private String code;
    private String channelUrl;
    private String videoUrl;
    private String embed;

    /**
     * Constructor that takes the channel information.
     * @param code The code for the provider
     * @param channelUrl The channel URL template for the provider
     * @param videoUrl The video URL template for the provider
     * @param embed The embedded player template for the provider
     */
    VideoProvider(String code, String channelUrl, String videoUrl, String embed)
    {
        this.code = code;
        this.channelUrl = channelUrl;
        this.videoUrl = videoUrl;
        this.embed = embed;
    }

    /**
     * Returns the code of the provider.
     * @return The code of the provider.
     */
    public String code()
    {
        return code;
    }

    /**
     * Returns the channel URL template.
     * @return The channel URL template.
     */
    public String channelUrl()
    {
        return channelUrl;
    }

    /**
     * Returns the video URL template.
     * @return The video URL template.
     */
    public String videoUrl()
    {
        return videoUrl;
    }

    /**
     * Returns the embedded player template.
     * @return The embedded player template.
     */
    public String embed()
    {
        return embed;
    }

    /**
     * Returns the video id from the given video URL.
     * @return The video id.
     */
    public String getVideoId(String videoUrl)
    {
        String ret = null;
        if(videoUrl != null)
        {
            ret = videoUrl.substring(videoUrl.lastIndexOf("/")+1);
            if(this == YOUTUBE)
                ret = ret.substring(ret.lastIndexOf("=")+1);
        }
        return ret;
    }

    /**
     * Returns the channel id from the given channel URL.
     * @return The channel id.
     */
    public String getChannelId(String channelUrl)
    {
        String ret = null;
        if(channelUrl != null)
            ret = channelUrl.substring(channelUrl.lastIndexOf("/")+1);
        return ret;
    }

    /**
     * Returns the type for the given code.
     * @param code The type code
     * @return The type for the given code
     */
    public static VideoProvider fromCode(String code)
    {
        VideoProvider[] types = values();
        for(VideoProvider type : types)
        {
            if(type.code().equals(code))
                return type;
        }
        return null;
    }

    /**
     * Returns the type for the given video url.
     * @param videoUrl The video url
     * @return The type for the given video url
     */
    public static VideoProvider fromVideoUrl(String videoUrl)
    {
        VideoProvider[] types = values();
        for(VideoProvider type : types)
        {
            if(videoUrl.indexOf(type.code()) != -1)
                return type;
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the given code is contained in the list of types.
     * @param code The type code
     * @return <CODE>true</CODE> if the given code is contained in the list of types
     */
    public static boolean contains(String code)
    {
        return fromCode(code) != null;
    }
}