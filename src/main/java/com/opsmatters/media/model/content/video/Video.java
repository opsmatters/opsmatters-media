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

import java.util.List;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.cache.provider.VideoProviders;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.provider.VideoProviderId;
import com.opsmatters.media.model.provider.VideoProvider;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Article;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSiteSettings;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.model.content.crawler.field.FieldFilter;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.SessionId;

import static com.opsmatters.media.model.content.FieldName.*;
import static com.opsmatters.media.model.content.video.VideoType.*;

/**
 * Class representing a video.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Video extends Article<VideoDetails>
{
    private String videoType = "";

    /**
     * Default constructor.
     */
    public Video()
    {
        setDetails(new VideoDetails());
    }

    /**
     * Constructor that takes a video.
     */
    public Video(Video obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Video(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Video obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
        setVideoType(new String(obj.getVideoType() != null ? obj.getVideoType() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Video(Site site, String code, String[] values) throws DateTimeParseException
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

        VideoProvider provider = VideoProviders.matchesTag(videoUrl);

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
        setProviderId(provider.getProviderId());
        setChannelTitle(channelTitle);
        if(provider != null)
            setChannelId(provider.getChannelId(channelUrl));
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
        setNewsletter(newsletter != null && newsletter.equals("1"));
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        if(getDescription() != null && getDescription().length() > 0)
            ret.putOpt(DESCRIPTION.value(), EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(VIDEO_ID.value(), getVideoId());
        ret.putOpt(VIDEO_TYPE.value(), getVideoType());
        ret.putOpt(PROVIDER.value(), getProviderId().code());
        if(getDuration() > 0L)
            ret.putOpt(DURATION.value(), getDuration());
        ret.putOpt(CHANNEL_TITLE.value(), getChannelTitle());
        ret.putOpt(CHANNEL_ID.value(), getChannelId());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setDescription(EmojiParser.parseToUnicode(obj.optString(DESCRIPTION.value())));
        setVideoId(obj.optString(VIDEO_ID.value()));
        setVideoType(obj.optString(VIDEO_TYPE.value()));
        setProviderId(VideoProviderId.fromCode(obj.optString(PROVIDER.value())));
        if(obj.has(DURATION.value()))
            setDuration(obj.optLong(DURATION.value()));
        setChannelTitle(obj.optString(CHANNEL_TITLE.value()));
        setChannelId(obj.optString(CHANNEL_ID.value()));
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
    public void copyExternalAttributes(Content obj)
    {
        if(obj instanceof Video)
        {
            Video video = (Video)obj;
            if(video.getDuration() > 0L)
                setDuration(video.getDuration());
        }
    }

    /**
     * Returns a new content item with defaults.
     */
    public static Video getDefault(Site site, VideoConfig config)
        throws DateTimeParseException
    {
        Video video = new Video();

        video.init();
        video.setSiteId(site.getId());
        video.setTitle("New Video");
        video.setPublishedDateAsString(TimeUtils.toStringUTC(SessionId.date(),
            config.getField(PUBLISHED_DATE)));

        return video;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite,
        VideoConfig config, CrawlerVideoChannel channel)
    {
        super.init(organisation, organisationSite, config);

        if(organisationSite != null)
        {
            ContentSiteSettings settings = organisationSite.getSettings(getType());
            if(settings != null)
            {
                setPromoted(settings.isPromoted());
            }
        }

        if(channel.hasField(TAGS))
            setTags(channel.getField(TAGS, ""));
        if(channel.hasField(NEWSLETTER))
            setNewsletter(channel.getField(NEWSLETTER, "0").equals("0") ? false : true);

        setVideoType(config.getField(VIDEO_TYPE, ""));
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(VideoConfig config, CrawlerVideoChannel channel, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        formatSummary(config, channel.getArticles().getFilters(), false, debug);

        // Attempt to guess the video type from the text
        String[] texts = new String[] { getTitle(), getDescription() };
        VideoType guessed = VideoType.guess(texts, getDuration());
        if(guessed != null)
            setVideoType(guessed);
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
     * Sets the video details from a teaser.
     */
    @Override
    public void setTeaserDetails(VideoDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setVideoId(new String(obj.getVideoId()));
            setProviderId(obj.getProviderId());
            setDuration(obj.getDuration());
        }
    }

    /**
     * Sets the video details.
     */
    @Override
    public void setContentDetails(VideoDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setDescription(new String(obj.getDescription() != null ? obj.getDescription() : ""));
            setChannelId(new String(obj.getChannelId()));
            setChannelTitle(new String(obj.getChannelTitle()));
            setConfigured(true);
        }
    }

    /**
     * Format the video body and summary.
     */
    public void formatSummary(VideoConfig config, List<FieldFilter> filters, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), filters, debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(getType()));
        }
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
        return getDetails().getVideoId();
    }

    /**
     * Sets the video ID.
     */
    public void setVideoId(String videoId)
    {
        getDetails().setVideoId(videoId);
    }

    /**
     * Returns <CODE>true</CODE> if the video ID has been set.
     */
    public boolean hasVideoId()
    {
        return getDetails().hasVideoId();
    }

    /**
     * Returns the video description.
     */
    public String getDescription()
    {
        return getDetails().getDescription();
    }

    /**
     * Sets the video description.
     */
    public void setDescription(String description)
    {
        getDetails().setDescription(description);
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
        return getDetails().getDuration();
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration(boolean replaceZero)
    {
        return getDetails().getFormattedDuration(replaceZero);
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
        getDetails().setDuration(duration);
    }

    /**
     * Returns the ID of the video channel.
     */
    public String getChannelId()
    {
        return getDetails().getChannelId();
    }

    /**
     * Sets the ID of the video channel.
     */
    public void setChannelId(String channelId)
    {
        getDetails().setChannelId(channelId);
    }

    /**
     * Returns <CODE>true</CODE> if the channel ID has been set.
     */
    public boolean hasChannelId()
    {
        return getDetails().hasChannelId();
    }

    /**
     * Returns the title of the video channel.
     */
    public String getChannelTitle()
    {
        return getDetails().getChannelTitle();
    }

    /**
     * Sets the title of the video channel.
     */
    public void setChannelTitle(String channelTitle)
    {
        getDetails().setChannelTitle(channelTitle);
    }

    /**
     * Returns <CODE>true</CODE> if the channel title has been set.
     */
    public boolean hasChannelTitle()
    {
        return getDetails().hasChannelTitle();
    }

    /**
     * Returns the provider of the video.
     */
    public VideoProviderId getProviderId()
    {
        return getDetails().getProviderId();
    }

    /**
     * Sets the provider of the video.
     */
    public void setProviderId(VideoProviderId providerId)
    {
        getDetails().setProviderId(providerId);
    }

    /**
     * Returns the URL of the video.
     */
    public String getVideoUrl()
    {
        return getDetails().getVideoUrl();
    }

    /**
     * Returns the URL of the video channel.
     */
    public String getChannelUrl()
    {
        return getDetails().getChannelUrl();
    }

    /**
     * Returns the embed link of the video.
     */
    public String getEmbed(int width, int height, boolean autoplay)
    {
        return getDetails().getEmbed(width, height, autoplay);
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