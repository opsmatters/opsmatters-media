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

package com.opsmatters.media.model.monitor.video;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.cache.content.organisation.OrganisationContentConfigs;
import com.opsmatters.media.crawler.video.VideoCrawler;
import com.opsmatters.media.model.content.video.VideoConfig;
import com.opsmatters.media.model.content.video.VideoTeaser;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.model.monitor.ContentMonitor;
import com.opsmatters.media.model.monitor.ContentSnapshot;

/**
 * Class representing a video monitor.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoMonitor extends ContentMonitor<VideoTeaser>
{
    private static final Logger logger = Logger.getLogger(VideoMonitor.class.getName());

    public static final String CHANNEL_ID = "channel-id";

    private String channelId = "";

    private List<VideoTeaser> subscribed = new ArrayList<VideoTeaser>();

    /**
     * Default constructor.
     */
    public VideoMonitor()
    {
    }

    /**
     * Copy constructor.
     */
    public VideoMonitor(VideoMonitor obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoMonitor obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setChannelId(obj.getChannelId());
        }
    }

    /**
     * Returns the monitor video channel id.
     */
    public String getChannelId()
    {
        return channelId;
    }

    /**
     * Sets the monitor video channel id.
     */
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    /**
     * Returns <CODE>true</CODE> if the video monitor channel id has been set.
     */
    public boolean hasChannelId()
    {
        return channelId != null && channelId.length() > 0;
    }

    /**
     * Adds a subscribed content item to the monitor.
     */
    public void addSubscribedContent(VideoTeaser content)
    {
        subscribed.add(content);
    }

    /**
     * Returns the subscribed content items for the monitor.
     */
    public List<VideoTeaser> getSubscribedContent()
    {
        return subscribed;
    }

    /**
     * Clears the subscribed content items for the monitor.
     */
    public void clearSubscribedContent()
    {
        subscribed.clear();
    }

    /**
     * Returns the number of subscribed content items for the monitor.
     */
    public int getSubscribedContentCount()
    {
        return subscribed.size();
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(CHANNEL_ID, getChannelId());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);
        setChannelId(obj.optString(CHANNEL_ID));
    }

    /**
     * Executes a check using this monitor.
     */
    @Override
    public ContentSnapshot check(int maxResults, boolean cache, boolean debug)
        throws IOException
    {
        ContentSnapshot ret = null;
        VideoCrawler crawler = null;
        VideoConfig config = OrganisationContentConfigs.get(getCode()).getVideos();

        try
        {
            CrawlerVideoChannel channel = config.getChannel(getName());
            crawler = new VideoCrawler(config, channel);
            crawler.setDebug(debug);
            crawler.setMaxResults(maxResults);
            int count = crawler.processTeasers(cache);

            List<VideoTeaser> teasers = crawler.getTeasers();

            if(debug)
                logger.info("VideoMonitor.check: found content: monitor="+getGuid()
                    +" teasers="+teasers.size()
                    +" subscribed="+getSubscribedContent().size());

            if(getSubscribedContent().size() > 0)
            {
                Map<String,VideoTeaser> teaserMap = new LinkedHashMap<String,VideoTeaser>();
                for(VideoTeaser video : teasers)
                    teaserMap.put(video.getVideoId(), video);

                for(VideoTeaser item : getSubscribedContent())
                {
                    VideoTeaser video = (VideoTeaser)item;
                    if(teaserMap.get(video.getVideoId()) == null)
                    {
                        video.setTitle(video.getTitle()+" **UNLISTED**");
                        teasers.add(0, video);

                        if(debug)
                            logger.info("VideoMonitor.check: adding subscribed video="+getGuid()
                                +" video="+video.getVideoId());
                    }
                }

                clearSubscribedContent();
            }

            ret = new ContentSnapshot(getContentType(), teasers);

            setTitle(crawler.getTitle());
            setChannelId(channel.getChannelId());
            setUrl(channel.getChannelUrl());
            setSites(channel.getSites());
            setKeywords(channel.getTeasers().getLoading().getKeywords());
        }
        finally
        {
            if(crawler != null)
                crawler.close();
        }

        return ret;
    }
}