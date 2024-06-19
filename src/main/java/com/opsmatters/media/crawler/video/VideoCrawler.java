/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.crawler.video;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.JSONObject;
import net.fellbaum.jemoji.EmojiManager;
import com.opsmatters.media.cache.content.Teasers;
import com.opsmatters.media.model.content.ContentDetails;
import com.opsmatters.media.model.content.video.VideoDetails;
import com.opsmatters.media.model.content.video.VideoConfig;
import com.opsmatters.media.model.content.crawler.ContentLoading;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.content.crawler.field.FieldSelector;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;
import com.opsmatters.media.model.content.crawler.field.FilterScope;
import com.opsmatters.media.model.content.crawler.field.FilterResult;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.crawler.ContentCrawler;
import com.opsmatters.media.client.video.VideoClient;
import com.opsmatters.media.client.video.VideoClientFactory;

import static com.opsmatters.media.model.content.crawler.field.FilterScope.*;
import static com.opsmatters.media.model.content.crawler.field.FilterResult.*;
import static com.opsmatters.media.model.content.FieldName.*;
import static com.opsmatters.media.model.logging.EventCategory.*;

/**
 * Class representing a crawler for videos.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoCrawler extends ContentCrawler<VideoDetails>
{
    private static final Logger logger = Logger.getLogger(VideoCrawler.class.getName());

    private transient VideoClient client;
    private VideoConfig config;
    private CrawlerVideoChannel channel;
    private String channelTitle = "";
    private boolean initialised = false;

    /**
     * Constructor that takes a video channel.
     */
    public VideoCrawler(VideoConfig config, CrawlerVideoChannel channel) throws IOException
    {
        super(channel);
        this.config = config;
        this.channel = channel;

        client = VideoClientFactory.newClient(channel.getVideoProvider());
    }

    /**
     * Close the crawler and release resources.
     */
    public void close()
    {
        if(client != null)
            client.close();
    }

    /**
     * Set to <CODE>true</CODE> if debug is enabled.
     */
    @Override
    public void setDebug(boolean debug)
    {
        super.setDebug(debug);
        if(client != null)
            client.setDebug(debug);
    }

    /**
     * Returns the video configuration of the crawler.
     */
    public VideoConfig getConfig()
    {
        return config;
    }

    /**
     * Returns the video channel configuration of the crawler.
     */
    public CrawlerVideoChannel getChannel()
    {
        return channel;
    }

    /**
     * Returns the title of the crawled channel.
     */
    @Override
    public String getTitle()
    {
        return channelTitle;
    }

    /**
     * Create the video teaser from a selected node.
     */
    protected VideoDetails getTeaser(JSONObject video, Fields fields)
    {
        VideoDetails teaser = new VideoDetails();

        populateTeaserFields(video, fields, teaser, "teaser");

        return teaser;
    }

    /**
     * Process the configured teasers.
     */
    @Override
    public int processTeasers(boolean cache) throws IOException
    {
        int ret = 0;
        initialised = true;

        String channelId = channel.getChannelId();
        String userId = channel.getUserId();

        // Try to get the teasers from the cache
        List<ContentDetails> teasers = Teasers.getTeasers(config.getCode(), channelId);
        if(teasers != null)
        {
            for(ContentDetails teaser : teasers)
                addTeaser((VideoDetails)teaser);
            ret += numTeasers();
            if(debug())
                logger.info("Retrieved "+numTeasers()+" teasers from cache");
        }
        else
        {
            // Process selections
            Map<String,String> map = new HashMap<String,String>();
            ContentLoading loading = channel.getTeasers().getLoading();
            for(Fields fields : channel.getTeasers().getFields())
            {
                String id = userId != null && userId.length() > 0 ? userId : channelId;
                List<JSONObject> results = client.listVideos(id, getMaxResults());
                if(debug())
                    logger.info("Found "+results.size()+" teasers for channel: "+channelId);
                ret += results.size();
                for(JSONObject result : results)
                {
                    VideoDetails teaser = getTeaser(result, fields);
                    if(teaser.isValid() && !map.containsKey(teaser.getUniqueId()))
                    {
                        // Check that the teaser matches the configured keywords
                        if(loading != null && loading.hasKeywords() && !teaser.matches(loading.getKeywordList()))
                        {
                            logger.info(String.format("Skipping article as it does not match keywords: %s (%s)",
                                teaser.getTitle(), loading.getKeywords()));
                            continue;
                        }

                        addTeaser(teaser);
                        map.put(teaser.getUniqueId(), teaser.getUniqueId());
                        channelTitle = result.optString(CHANNEL_TITLE.value()); // Only for YouTube
                    }

                    if(numTeasers() >= getMaxResults())
                        break;
                }
            }

            if(cache)
                Teasers.set(channelId, getTeasers(), config, null);

            if(debug())
                logger.info("Found "+numTeasers()+" teasers");
        }

        return ret;
    }

    /**
     * Create a video content item from the given video id.
     */
    public VideoDetails getDetails(String videoId) throws IOException
    {
        return getDetails(new VideoDetails(videoId));
    }

    /**
     * Populate the video details.
     */
    @Override
    public VideoDetails getDetails(VideoDetails teaser) throws IOException
    {
        JSONObject video = client.getVideo(teaser.getVideoId());

        if(video == null)
            return null;

        VideoDetails content = new VideoDetails(teaser);
        List<Fields> articles = channel.getArticles().getFields(hasRootError());
        for(Fields fields : articles)
        {
            populateTeaserFields(video, fields, content, "article");

            content.setChannelTitle(video.getString(CHANNEL_TITLE.value()));
            content.setChannelId(video.getString(CHANNEL_ID.value()));

            if(fields.hasBody())
            {
                Field field = fields.getBody();
                String body = getBody(field, field.getSelector(0), video, "article");
                if(body != null)
                    content.setDescription(body);
            }
        }

        Teasers.update(channel.getChannelId(), content);

        return content;
    }

    /**
     * Populate the teaser fields from the given video.
     */
    private void populateTeaserFields(JSONObject video, Fields fields, VideoDetails teaser, String type)
    {
        teaser.setVideoId(video.getString(VIDEO_ID.value()));
        teaser.setProvider(video.getString(PROVIDER.value()));
        if(video.has(DURATION.value()))
            teaser.setDuration(video.getInt(DURATION.value()));

        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getValue(field, video.getString(field.getSelector(0).getExpr()), TEASER);
            if(title != null && title.length() > 0)
                teaser.setTitle(EmojiManager.removeAllEmojis(title));
        }

        if(fields.hasPublishedDate())
        {
            Field field = fields.getPublishedDate();
            String publishedDate = getValue(field, video.getString(field.getSelector(0).getExpr()), TEASER);
            if(publishedDate != null)
                teaser.setPublishedDateAsString(publishedDate, field.getDatePattern());
        }
    }

    /**
     * Process the body field.
     */
    protected String getBody(Field field, FieldSelector selector, JSONObject video, String type)
    {
        String ret = null;

        StringBuilder body = new StringBuilder();
        String value = video.getString(selector.getExpr());
        String lines[] = value.split("\\r?\\n");

        for(String line : lines)
        {
            String text = line.trim();

            // Apply the filters to skip elements or truncate the text
            FilterResult result = FieldFilter.apply(field.getFilters(), text, BODY);
            if(result == SKIP)
                continue;
            else if(result == STOP)
                break;

            if(body.length() > 0)
                body.append("\n");
            body.append(text);
        }

        if(body.length() > 0)
        {
            ret = body.toString().trim();

            if(debug())
                logger.info("Found body for "+type+" field "+field.getName()+": "+ret);
        }

        return ret;
    }
}