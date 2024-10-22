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

package com.opsmatters.media.model.content.video;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.client.video.YouTubeClient;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.UserMessage.*;

/**
 * Represents a video provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum VideoProvider
{
    YOUTUBE("YTB", "YouTube", "youtube",
        "https://www.youtube.com",
        "/channel/%s",
        "/watch?v=%s",
        "<iframe src=\"https://www.youtube.com/embed/%s?modestbranding=1&autohide=1&autoplay=%s\" width=\"%d\" height=\"%d\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"),
    VIMEO("VIM", "Vimeo", "vimeo",
        "https://vimeo.com",
        "/%s",
        "/%s",
        "<iframe src=\"https://player.vimeo.com/video/%s?title=0&byline=0&portrait=0&autoplay=%s\" width=\"%d\" height=\"%d\" frameborder=\"0\" allow=\"autoplay; fullscreen\" allowfullscreen></iframe>"),
    WISTIA("WST", "Wistia", "wistia",
        "https://fast.wistia.com",
        "/projects/%s",
        "/medias/%s",
        "<iframe src=\"//fast.wistia.net/embed/iframe/%s?autoplay=%s\" allowtransparency=\"true\" frameborder=\"0\" scrolling=\"no\" class=\"wistia_embed\" name=\"wistia_embed\" allowfullscreen mozallowfullscreen webkitallowfullscreen oallowfullscreen msallowfullscreen width=\"%d\" height=\"%d\"></iframe>");

    private static final Logger logger = Logger.getLogger(VideoProvider.class.getName());

    private String code;
    private String value;
    private String tag;
    private String url;
    private String channelUrl;
    private String videoUrl;
    private String embed;

    /**
     * Constructor that takes the channel information.
     * @param code The code for the provider
     * @param value The value for the provider
     * @param tag The tag for the provider
     * @param url The base URL for the provider
     * @param channelUrl The channel URL template for the provider
     * @param videoUrl The video URL template for the provider
     * @param embed The embedded player template for the provider
     */
    VideoProvider(String code, String value, String tag, String url, String channelUrl, String videoUrl, String embed)
    {
        this.code = code;
        this.value = value;
        this.tag = tag;
        this.url = url;
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
     * Returns the value of the provider.
     * @return The value of the provider.
     */
    public String value()
    {
        return value;
    }

    /**
     * Returns the tag of the provider.
     * @return The tag of the provider.
     */
    public String tag()
    {
        return tag;
    }

    /**
     * Returns the base URL.
     * @return The base URL.
     */
    public String url()
    {
        return url;
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
     * Returns the video id from the given URL.
     * @return The video id.
     */
    public String getVideoId(String url)
    {
        String ret = null;
        if(url != null)
        {
            ret = url.substring(url.lastIndexOf("/")+1);
            if(this == YOUTUBE)
                ret = ret.substring(ret.lastIndexOf("=")+1);
        }

        return ret;
    }

    /**
     * Returns the channel id from the given URL.
     * @return The channel id.
     */
    public String getChannelId(String url)
    {
        String ret = null;
        if(url != null)
        {
            if(url.endsWith("/")) // strip trailing slash
                url = url.substring(0, url.length()-1);

            if(this == YOUTUBE)
            {
                String id = url.substring(url.lastIndexOf("/")+1);
                if(id.startsWith("UC")) // channel id
                {
                    ret = id;
                }
                else
                {
                    YouTubeClient client = null;

                    try
                    {
                        client = YouTubeClient.newClient();

                        if(url.indexOf("/user") != -1) // user id
                        {
                            ret = client.userIdToChannelId(id);
                        }
                        else // handle
                        {
                            if(id.startsWith("@"))
                                id = id.substring(1);
                            ret = client.handleToChannelId(id);
                        }
                    }
                    catch(IOException e)
                    {
                        ret = UNKNOWN.value();
                        logger.severe(StringUtils.serialize(e));
                    }
                    finally
                    {
                        if(client != null)
                            client.close();
                    }
                }
            }
            else
            {
                ret = url.substring(url.lastIndexOf("/")+1);
            }
        }

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
     * Returns the type for the given value.
     * @param value The type value
     * @return The type for the given value
     */
    public static VideoProvider fromValue(String value)
    {
        VideoProvider[] types = values();
        for(VideoProvider type : types)
        {
            if(type.value().equals(value))
                return type;
        }

        return null;
    }

    /**
     * Returns the type for the given tag.
     * @param code The type tag
     * @return The type for the given tag
     */
    public static VideoProvider fromTag(String tag)
    {
        VideoProvider[] types = values();
        for(VideoProvider type : types)
        {
            if(type.tag().equals(tag))
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
            if(videoUrl.indexOf(type.tag()) != -1)
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

    /**
     * Returns a list of the providers.
     */
    public static List<VideoProvider> toList()
    {
        List<VideoProvider> ret = new ArrayList<VideoProvider>();

        ret.add(YOUTUBE);
        ret.add(VIMEO);
        ret.add(WISTIA);

        return ret;
    }
}