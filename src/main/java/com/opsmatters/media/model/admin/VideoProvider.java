/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model.admin;

import java.io.IOException;
import java.util.logging.Logger;
import com.opsmatters.media.client.video.YouTubeClient;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.UserMessage.*;
import static com.opsmatters.media.model.admin.VideoProviderId.*;

/**
 * Class that represents a video provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoProvider extends ExternalProvider
{
    private static final Logger logger = Logger.getLogger(VideoProvider.class.getName());

    private VideoProviderId providerId;
    private String tag = "";
    private String url = "";
    private String channelUrl = "";
    private String videoUrl = "";
    private String embed = "";

    /**
     * Default constructor.
     */
    public VideoProvider()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public VideoProvider(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public VideoProvider(VideoProvider obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoProvider obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTag(obj.getTag());
            setUrl(obj.getUrl());
            setChannelUrl(obj.getChannelUrl());
            setVideoUrl(obj.getVideoUrl());
            setEmbed(obj.getEmbed());
        }
    }

    /**
     * Sets the code for the provider.
     */
    @Override
    public void setCode(String code)
    {
        super.setCode(code);
        setProviderId(code);
    }

    /**
     * Returns the tag for the provider.
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Sets the tag for the provider.
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    /**
     * Returns <CODE>true</CODE> if the tag for the provider has been set.
     */
    public boolean hasTag()
    {
        return getTag() != null && getTag().length() > 0;
    }

    /**
     * Returns the provider id.
     */
    public VideoProviderId getProviderId()
    {
        return providerId;
    }

    /**
     * Sets the provider id.
     */
    private void setProviderId(VideoProviderId providerId)
    {
        this.providerId = providerId;
    }

    /**
     * Sets the provider id.
     */
    private void setProviderId(String code)
    {
        setProviderId(VideoProviderId.fromCode(code));
    }

    /**
     * Returns the url for the provider.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url for the provider.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the url for the provider has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }

    /**
     * Returns the channel url for the provider.
     */
    public String getChannelUrl()
    {
        return channelUrl;
    }

    /**
     * Sets the channel url for the provider.
     */
    public void setChannelUrl(String channelUrl)
    {
        this.channelUrl = channelUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the channel url for the provider has been set.
     */
    public boolean hasChannelUrl()
    {
        return getChannelUrl() != null && getChannelUrl().length() > 0;
    }

    /**
     * Returns the video url for the provider.
     */
    public String getVideoUrl()
    {
        return videoUrl;
    }

    /**
     * Sets the video url for the provider.
     */
    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the video url for the provider has been set.
     */
    public boolean hasVideoUrl()
    {
        return getVideoUrl() != null && getVideoUrl().length() > 0;
    }

    /**
     * Returns the embed for the provider.
     */
    public String getEmbed()
    {
        return embed;
    }

    /**
     * Sets the embed for the provider.
     */
    public void setEmbed(String embed)
    {
        this.embed = embed;
    }

    /**
     * Returns <CODE>true</CODE> if the embed for the provider has been set.
     */
    public boolean hasEmbed()
    {
        return getEmbed() != null && getEmbed().length() > 0;
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
            if(providerId == YOUTUBE)
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

            if(providerId == YOUTUBE)
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
                            ret = client.getChannelIdFromUserId(id);
                        }
                        else // handle
                        {
                            if(id.startsWith("@"))
                                id = id.substring(1);
                            ret = client.getChannelIdFromHandle(id);
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
}