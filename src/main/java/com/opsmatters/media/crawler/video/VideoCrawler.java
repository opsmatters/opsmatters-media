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
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.cache.content.Teasers;
import com.opsmatters.media.model.content.ContentSummary;
import com.opsmatters.media.model.content.video.VideoSummary;
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

/**
 * Class representing a crawler for videos.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoCrawler extends ContentCrawler<VideoSummary>
{
    private static final Logger logger = Logger.getLogger(VideoCrawler.class.getName());

    private transient VideoClient client;
    private VideoConfig config;
    private CrawlerVideoChannel channel;
    private String channelTitle = "";
    private boolean initialised = false;

    /**
     * Constructor that takes a video channel configuration.
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
    protected VideoSummary getTeaser(JSONObject video, Fields fields)
    {
        VideoSummary content = new VideoSummary();

        populateSummaryFields(video, fields, content, "teaser");

        return content;
    }

    /**
     * Process the configured teasers.
     */
    @Override
    public int processTeasers(boolean cache) throws IOException
    {
        int ret = 0;
        initialised = true;

        // Try to get the teasers from the cache
        List<ContentSummary> teasers = Teasers.get(channel.getChannelId());
        if(teasers != null)
        {
            for(ContentSummary teaser : teasers)
                addTeaser((VideoSummary)teaser);
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
                List<JSONObject> results = client.listVideos(channel.getChannelId(),
                    channel.getUserId(), getMaxResults());
                if(debug())
                    logger.info("Found "+results.size()+" teasers for channel: "+channel.getChannelId());
                ret += results.size();
                for(JSONObject result : results)
                {
                    VideoSummary teaser = getTeaser(result, fields);
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

            if(cache && !debug())
                Teasers.set(channel.getChannelId(), getTeasers());

            if(debug())
                logger.info("Found "+numTeasers()+" teasers");
        }

        return ret;
    }

    /**
     * Populate the given video content.
     */
    @Override
    public VideoDetails getContent(String videoId) throws IOException
    {
        JSONObject video = client.getVideo(videoId);

        if(video == null)
            return null;

        VideoDetails content = new VideoDetails(videoId);
        List<Fields> articles = channel.getArticles().getFields(hasRootError());
        for(Fields fields : articles)
        {
            populateSummaryFields(video, fields, content, "content");

            content.setDuration(video.getInt(DURATION.value()));
            content.setChannelTitle(video.getString(CHANNEL_TITLE.value()));
            content.setChannelId(video.getString(CHANNEL_ID.value()));

            if(fields.hasBody())
            {
                Field field = fields.getBody();
                String body = getBody(field, field.getSelector(0), video, "content");
                if(body != null)
                    content.setDescription(body);
            }
        }

        return content;
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(JSONObject video, 
        Fields fields, VideoSummary content, String type)
    {
        content.setVideoId(video.getString(VIDEO_ID.value()));
        content.setProvider(video.getString(PROVIDER.value()));

        if(fields.hasTitle())
        {
            Field field = fields.getTitle();
            String title = getValue(field, video.getString(field.getSelector(0).getExpr()));
            if(title != null && title.length() > 0)
                content.setTitle(EmojiParser.removeAllEmojis(title));
        }

        if(fields.hasPublishedDate())
        {
            Field field = fields.getPublishedDate();
            String publishedDate = getValue(field, video.getString(field.getSelector(0).getExpr()));
            if(publishedDate != null)
                content.setPublishedDateAsString(publishedDate, field.getDatePattern());
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