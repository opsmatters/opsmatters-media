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
package com.opsmatters.media.crawler;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.JSONObject;
import com.opsmatters.media.model.content.VideoSummary;
import com.opsmatters.media.model.content.VideoDetails;
import com.opsmatters.media.config.content.VideoConfiguration;
import com.opsmatters.media.config.content.VideoChannelConfiguration;
import com.opsmatters.media.config.content.ContentField;
import com.opsmatters.media.config.content.ContentFields;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.client.video.VideoClient;
import com.opsmatters.media.client.video.VideoClientFactory;

/**
 * Class representing a crawler for videos.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoCrawler extends FieldsCrawler<VideoSummary>
{
    private static final Logger logger = Logger.getLogger(VideoCrawler.class.getName());

    private transient VideoClient client;
    private VideoConfiguration config;
    private VideoChannelConfiguration channel;

    /**
     * Constructor that takes a video channel configuration.
     */
    public VideoCrawler(VideoConfiguration config, VideoChannelConfiguration channel) throws IOException
    {
        super(channel);
        this.config = config;
        this.channel = channel;

        client = VideoClientFactory.newClient(channel.getProvider());
    }

    /**
     * Close the crawler and release resources.
     */
    public void close()
    {
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
    public VideoConfiguration getConfig()
    {
        return config;
    }

    /**
     * Returns the video channel configuration of the crawler.
     */
    public VideoChannelConfiguration getChannel()
    {
        return channel;
    }

    /**
     * Create the video summary from a selected node.
     */
    public VideoSummary getContentSummary(JSONObject video, ContentFields fields)
    {
        VideoSummary content = new VideoSummary();

        populateSummaryFields(video, fields, content, "teaser");

        return content;
    }

    /**
     * Process all the configured teaser fields.
     */
    public void processTeaserFields() throws IOException
    {
        initialised = true;

        // Process selections
        Map<String,String> map = new HashMap<String,String>();
        for(ContentFields fields : getTeaserFields())
        {
            List<JSONObject> results = client.listVideos(channel.getChannelId(),
                channel.getUserId(), getMaxResults());
            if(debug())
                logger.info("Found "+results.size()+" teasers for channel: "+channel.getChannelId());
            for(JSONObject result : results)
            {
                VideoSummary content = getContentSummary(result, fields);
                if(content.isValid() && !map.containsKey(content.getUniqueId()))
                {
                    addContent(content);
                    map.put(content.getUniqueId(), content.getUniqueId());
                }

                if(numContentItems() >= getMaxResults())
                    break;
            }
        }

        if(debug())
            logger.info("Found "+numContentItems()+" items");
    }

    /**
     * Populate the given video content.
     */
    public VideoDetails getVideo(String videoId) throws IOException
    {
        JSONObject video = client.getVideo(videoId);

        if(video == null)
            return null;

        ContentFields fields = getContentFields();
        VideoDetails content = new VideoDetails(videoId);
        populateSummaryFields(video, fields, content, "content");

        content.setDuration(video.getInt(Fields.DURATION));
        content.setChannelTitle(video.getString(Fields.CHANNEL_TITLE));
        content.setChannelId(video.getString(Fields.CHANNEL_ID));

        if(fields.hasBody())
        {
            ContentField field = fields.getBody();
            String body = getBody(field, video.getString(field.getSelector()), "content");
            if(body != null)
                content.setDescription(body);
        }

        return content;
    }

    /**
     * Populate the content fields from the given node.
     */
    private void populateSummaryFields(JSONObject video, 
        ContentFields fields, VideoSummary content, String type)
    {
        content.setVideoId(video.getString(Fields.VIDEO_ID));
        content.setProvider(video.getString(Fields.PROVIDER));

        if(fields.hasTitle())
        {
            ContentField field = fields.getTitle();
            String title = getValue(field, video.getString(field.getSelector()));
            if(title != null)
                content.setTitle(title);
        }

        if(fields.hasPublishedDate())
        {
            ContentField field = fields.getPublishedDate();
            String publishedDate = getValue(field, video.getString(field.getSelector()));
            if(publishedDate != null)
                content.setPublishedDateAsString(publishedDate, field.getDatePattern());
        }
    }

    /**
     * Process the body field.
     */
    protected String getBody(ContentField field, String value, String type)
    {
        String ret = null;

        StringBuilder body = new StringBuilder();
        String lines[] = value.split("\\r?\\n");
        Pattern stopExprPattern = field.getStopExprPattern();

        for(String line : lines)
        {
            String text = line.trim();

            // Stop if the text matches the stop expression
            if(stopExprPattern != null && stopExprPattern.matcher(text).matches())
                break;

            if(body.length() > 0 && text.length() > 0)
                body.append("\n");
            body.append(text);
        }

        if(body.length() > 0)
        {
            ret = body.toString();

            if(debug())
                logger.info("Found body for "+type+" field "+field.getName()+": "+ret);
        }

        return ret;
    }
}