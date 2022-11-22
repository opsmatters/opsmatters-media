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

import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Article;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;

import static com.opsmatters.media.model.content.FieldName.*;
import static com.opsmatters.media.model.content.video.VideoType.*;

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

        setDescription(EmojiParser.parseToUnicode(obj.optString(DESCRIPTION.value())));
        setVideoId(obj.optString(VIDEO_ID.value()));
        setVideoType(obj.optString(VIDEO_TYPE.value()));
        setProvider(VideoProvider.fromCode(obj.optString(PROVIDER.value())));
        if(obj.has(DURATION.value()))
            setDuration(obj.optLong(DURATION.value()));
        setChannelTitle(obj.optString(CHANNEL_TITLE.value()));
        setChannelId(obj.optString(CHANNEL_ID.value()));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        if(getDescription() != null && getDescription().length() > 0)
            ret.putOpt(DESCRIPTION.value(), EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(VIDEO_ID.value(), getVideoId());
        ret.putOpt(VIDEO_TYPE.value(), getVideoType());
        ret.putOpt(PROVIDER.value(), getProvider().code());
        if(getDuration() > 0L)
            ret.putOpt(DURATION.value(), getDuration());
        ret.putOpt(CHANNEL_TITLE.value(), getChannelTitle());
        ret.putOpt(CHANNEL_ID.value(), getChannelId());

        return ret;
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(VIDEO_ID, getVideoId());
        ret.put(VIDEO_URL, getVideoUrl());
        ret.put(DESCRIPTION, EmojiParser.parseToHtmlDecimal(getDescription()));
        ret.put(CHANNEL_ID, getChannelId());
        ret.put(CHANNEL_URL, getChannelUrl());
        ret.put(CHANNEL_TITLE, getChannelTitle());
        ret.put(VIDEO_TYPE, getVideoType());

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
    public static VideoArticle getDefault(Site site, VideoConfig config)
        throws DateTimeParseException
    {
        VideoArticle article = new VideoArticle();

        article.init();
        article.setSiteId(site.getId());
        article.setTitle("New Video");
        article.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));

        return article;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite,
        VideoConfig config, CrawlerVideoChannel channel)
    {
        super.init(organisation, organisationSite, config);

        if(channel.hasField(TAGS))
            setTags(channel.getField(TAGS, ""));
        if(channel.hasField(NEWSLETTER))
            setNewsletter(channel.getField(NEWSLETTER, "0").equals("0") ? false : true);

        String promote = config.getField(PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(VideoConfig config, CrawlerVideoChannel channel, boolean debug) throws DateTimeParseException
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
    public String getFormattedDuration(boolean replaceZero)
    {
        return details.getFormattedDuration(replaceZero);
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

    /**
     * Returns <CODE>true</CODE> if this content can be skipped.
     */
    @Override
    public boolean canSkip()
    {
        return super.canSkip() && getDuration() > 0L;
    }
}