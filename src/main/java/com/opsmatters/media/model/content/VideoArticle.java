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

import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.config.content.VideoConfiguration;
import com.opsmatters.media.config.content.VideoChannelConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.content.VideoType.*;

/**
 * Class representing a video article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoArticle extends Article
{
    private VideoDetails details = new VideoDetails();
    private String videoType = "";

    /**
     * Default constructor.
     */
    public VideoArticle()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes a video.
     */
    public VideoArticle(VideoArticle obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a video.
     */
    public VideoArticle(Site site, String code, VideoDetails obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setVideoDetails(obj);
    }

    /**
     * Constructor that takes a video summary.
     */
    public VideoArticle(Site site, String code, VideoSummary obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setContentSummary(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(VideoArticle obj)
    {
        super.copyAttributes(obj);

        setVideoDetails(obj.getVideoDetails());
        setVideoType(new String(obj.getVideoType() != null ? obj.getVideoType() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public VideoArticle(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String description = values[4];
        String organisation = values[5];
        String tags = values[6];
        String videoUrl = values[7];
        String videoType = values[8];
        String thumbnail = values[9];
        String thumbnailText = values[10];
        String thumbnailTitle = values[11];
        String channelTitle = values[12];
        String channelUrl = values[13];
        String createdBy = values[14];
        String published = values[15];
        String promote = values[16];
        String newsletter = values[17];

        VideoProvider provider = VideoProvider.fromVideoUrl(videoUrl);

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setTags(tags);
        if(provider != null)
            setVideoId(provider.getVideoId(videoUrl));
        setVideoType(videoType);
        setProvider(provider);
        setChannelTitle(channelTitle);
        if(provider != null)
            setChannelId(provider.getChannelId(channelUrl));
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setNewsletter(newsletter != null && newsletter.equals("1"));
    }

    /**
     * Constructor that takes a JSON object.
     */
    public VideoArticle(JSONObject obj)
    {
        this();
        fromJson(obj);
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setDescription(EmojiParser.parseToUnicode(obj.optString(Fields.DESCRIPTION)));
        setVideoId(obj.optString(Fields.VIDEO_ID));
        setVideoType(obj.optString(Fields.VIDEO_TYPE));
        setProvider(VideoProvider.fromCode(obj.optString(Fields.PROVIDER)));
        setDuration(obj.optLong(Fields.DURATION));
        setChannelTitle(obj.optString(Fields.CHANNEL_TITLE));
        setChannelId(obj.optString(Fields.CHANNEL_ID));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        if(getDescription() != null && getDescription().length() > 0)
            ret.putOpt(Fields.DESCRIPTION, EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(Fields.VIDEO_ID, getVideoId());
        ret.putOpt(Fields.VIDEO_TYPE, getVideoType());
        ret.putOpt(Fields.PROVIDER, getProvider().code());
        if(getDuration() > 0L)
            ret.putOpt(Fields.DURATION, getDuration());
        ret.putOpt(Fields.CHANNEL_TITLE, getChannelTitle());
        ret.putOpt(Fields.CHANNEL_ID, getChannelId());

        return ret;
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.VIDEO_ID, getVideoId());
        ret.put(Fields.VIDEO_URL, getVideoUrl());
        ret.put(Fields.DESCRIPTION, EmojiParser.parseToHtmlDecimal(getDescription()));
        ret.put(Fields.CHANNEL_ID, getChannelId());
        ret.put(Fields.CHANNEL_URL, getChannelUrl());
        ret.put(Fields.CHANNEL_TITLE, getChannelTitle());
        ret.put(Fields.VIDEO_TYPE, getVideoType());

        return ret;
    }

    /**
     * Copies any external attributes of the given object.
     */
    @Override
    public void copyExternalAttributes(ContentItem obj)
    {
        if(obj instanceof VideoArticle)
        {
            VideoArticle article = (VideoArticle)obj;
            if(article.getDuration() > 0L)
                setDuration(article.getDuration());
        }
    }

    /**
     * Returns a new content item with defaults.
     */
    public static VideoArticle getDefault(Site site, VideoConfiguration config) throws DateTimeParseException
    {
        VideoArticle article = new VideoArticle();

        article.init();
        article.setSiteId(site.getId());
        article.setTitle("New Video");
        article.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));

        return article;
    }

    /**
     * Use the given organisation to set defaults for the content.
     */
    public void init(Organisation organisation)
    {
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, VideoConfiguration config, VideoChannelConfiguration channel)
    {
        super.init(organisation, config);

        if(channel.hasField(Fields.TAGS))
            setTags(channel.getField(Fields.TAGS, ""));
        if(channel.hasField(Fields.NEWSLETTER))
            setNewsletter(channel.getField(Fields.NEWSLETTER, "0").equals("0") ? false : true);

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(VideoConfiguration config, VideoChannelConfiguration channel, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));

        BodyParser parser = new BodyParser(getDescription(), channel.getFilters(), debug);
        if(parser.converted())
            setDescription(parser.formatBody());
        setSummary(parser.formatSummary(config.getSummary()));

        String text = String.format("%s %s", getTitle(), getDescription());
        setVideoType(VideoType.guess(text, getDuration()));
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.VIDEO;
    }

    /**
     * Returns the video details.
     */
    public VideoDetails getVideoDetails()
    {
        return details;
    }

    /**
     * Sets the video details.
     */
    public void setVideoDetails(VideoDetails obj)
    {
        if(obj != null)
        {
            setContentSummary(obj);
            setDuration(obj.getDuration());
            setDescription(new String(obj.getDescription() != null ? obj.getDescription() : ""));
            setChannelId(new String(obj.getChannelId()));
            setChannelTitle(new String(obj.getChannelTitle()));
            setContentDetails(true);
        }
    }

    /**
     * Sets the video details from a summary.
     */
    public void setContentSummary(VideoSummary obj)
    {
        super.setContentSummary(obj);
        setVideoId(new String(obj.getVideoId()));
        setProvider(obj.getProvider());
    }

    /**
     * Returns the video type.
     */
    public String getVideoType()
    {
        return videoType;
    }

    /**
     * Sets the video type.
     */
    public void setVideoType(String videoType)
    {
        this.videoType = videoType;
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
        return details.getVideoId();
    }

    /**
     * Sets the video ID.
     */
    public void setVideoId(String videoId)
    {
        details.setVideoId(videoId);
    }

    /**
     * Returns <CODE>true</CODE> if the video ID has been set.
     */
    public boolean hasVideoId()
    {
        return details.hasVideoId();
    }

    /**
     * Returns the video description.
     */
    public String getDescription()
    {
        return details.getDescription();
    }

    /**
     * Sets the video description.
     */
    public void setDescription(String description)
    {
        details.setDescription(description);
    }

    /**
     * Returns <CODE>true</CODE> if the video description has been set.
     */
    public boolean hasDescription()
    {
        return getDescription() != null && getDescription().length() > 0;
    }

    /**
     * Returns the video duration (in seconds).
     */
    public long getDuration()
    {
        return details.getDuration();
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration()
    {
        return details.getFormattedDuration();
    }

    /**
     * Sets the video duration (in seconds).
     */
    public void setDuration(long duration)
    {
        details.setDuration(duration);
    }

    /**
     * Returns the ID of the video channel.
     */
    public String getChannelId()
    {
        return details.getChannelId();
    }

    /**
     * Sets the ID of the video channel.
     */
    public void setChannelId(String channelId)
    {
        details.setChannelId(channelId);
    }

    /**
     * Returns <CODE>true</CODE> if the channel ID has been set.
     */
    public boolean hasChannelId()
    {
        return details.hasChannelId();
    }

    /**
     * Returns the title of the video channel.
     */
    public String getChannelTitle()
    {
        return details.getChannelTitle();
    }

    /**
     * Sets the title of the video channel.
     */
    public void setChannelTitle(String channelTitle)
    {
        details.setChannelTitle(channelTitle);
    }

    /**
     * Returns <CODE>true</CODE> if the channel title has been set.
     */
    public boolean hasChannelTitle()
    {
        return details.hasChannelTitle();
    }

    /**
     * Returns the provider of the video.
     */
    public VideoProvider getProvider()
    {
        return details.getProvider();
    }

    /**
     * Sets the provider of the video.
     */
    public void setProvider(VideoProvider provider)
    {
        details.setProvider(provider);
    }

    /**
     * Returns the URL of the video.
     */
    public String getVideoUrl()
    {
        return details.getVideoUrl();
    }

    /**
     * Returns the URL of the video channel.
     */
    public String getChannelUrl()
    {
        return details.getChannelUrl();
    }

    /**
     * Returns the embed link of the video.
     */
    public String getEmbed(int width, int height, boolean autoplay)
    {
        return details.getEmbed(width, height, autoplay);
    }
}