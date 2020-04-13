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
import com.opsmatters.media.config.content.VideoConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a video article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoArticle extends Article
{
    private VideoDetails details = new VideoDetails();
    private String videoType = "";
    private String link = "";
    private String linkText = "";

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
    public VideoArticle(String code, VideoDetails obj)
    {
        this();
        init();
        setCode(code);
        setVideoDetails(obj);
    }

    /**
     * Constructor that takes a video summary.
     */
    public VideoArticle(String code, VideoSummary obj)
    {
        this();
        init();
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
        setLink(new String(obj.getLink() != null ? obj.getLink() : ""));
        setLinkText(new String(obj.getLinkText() != null ? obj.getLinkText() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public VideoArticle(String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String description = values[4];
        String organisation = values[5];
        String tags = values[6];
        String videoUrl = values[7];
        String videoType = values[8];
        String link = values[9];
        String linkText = values[10];
        String thumbnail = values[11];
        String thumbnailText = values[12];
        String thumbnailTitle = values[13];
        String channelTitle = values[14];
        String channelUrl = values[15];
        String createdBy = values[16];
        String published = values[17];
        String promote = values[18];
        String newsletter = values[19];

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
        setLink(link);
        setLinkText(linkText);
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

        setDescription(obj.optString(Fields.DESCRIPTION));
        setVideoId(obj.optString(Fields.VIDEO_ID));
        setVideoType(obj.optString(Fields.VIDEO_TYPE));
        setProvider(VideoProvider.fromCode(obj.optString(Fields.PROVIDER)));
        setDuration(obj.optLong(Fields.DURATION));
        setLink(obj.optString(Fields.LINK));
        setLinkText(obj.optString(Fields.LINK_TEXT));
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
            ret.putOpt(Fields.DESCRIPTION, getDescription());
        ret.putOpt(Fields.VIDEO_ID, getVideoId());
        ret.putOpt(Fields.VIDEO_TYPE, getVideoType());
        ret.putOpt(Fields.PROVIDER, getProvider().code());
        if(getDuration() > 0L)
            ret.putOpt(Fields.DURATION, getDuration());
        ret.putOpt(Fields.LINK, getLink());
        ret.putOpt(Fields.LINK_TEXT, getLinkText());
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
        ret.put(Fields.DESCRIPTION, getDescription());
        ret.put(Fields.CHANNEL_ID, getChannelId());
        ret.put(Fields.CHANNEL_URL, getChannelUrl());
        ret.put(Fields.CHANNEL_TITLE, getChannelTitle());
        ret.put(Fields.VIDEO_TYPE, getVideoType());
        ret.put(Fields.LINK, getLink());
        ret.put(Fields.LINK_TEXT, getLinkText());

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
    public static VideoArticle getDefault(VideoConfiguration config) throws DateTimeParseException
    {
        VideoArticle article = new VideoArticle();

        article.init();
        article.setTitle("New Video");
        article.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));

        return article;
    }

    /**
     * Use the given organisation to set defaults for the content.
     */
    public void init(Organisation organisation)
    {
        setLink(organisation.getWebsite());
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    public void init(VideoConfiguration config)
    {
        super.init(config);

        setVideoType(config.getField(Fields.VIDEO_TYPE, ""));
        setTags(config.getField(Fields.TAGS, ""));
        setLinkText(config.getField(Fields.LINK_TEXT, ""));

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);

        String newsletter = config.getField(Fields.NEWSLETTER);
        setNewsletter(newsletter == null || newsletter.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the content using the given configuration.
     */
    public void prepare(VideoConfiguration config) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));
        setDescription(FormatUtils.getFormattedDescription(getDescription()));
        setSummary(FormatUtils.getFormattedSummary(getDescription(), config.getSummary()));
        String text = String.format("%s %s", getTitle(), getDescription());
        setVideoType(VideoType.fromText(text, VideoType.fromValue(getVideoType())));
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
        setContentSummary(obj);
        setDuration(obj.getDuration());
        setDescription(new String(obj.getDescription() != null ? obj.getDescription() : ""));
        setChannelId(new String(obj.getChannelId()));
        setChannelTitle(new String(obj.getChannelTitle()));
        setContentDetails(true);
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
    public void setVideoType(VideoType videoType)
    {
        setVideoType(videoType != null ? videoType.value() : "");
    }

    /**
     * Sets the video type.
     */
    public void setVideoType(String videoType)
    {
        this.videoType = videoType;
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
     * Returns the link.
     */
    public String getLink()
    {
        return link;
    }

    /**
     * Sets the link.
     */
    public void setLink(String link)
    {
        this.link = link;
    }

    /**
     * Returns <CODE>true</CODE> if the link has been set.
     */
    public boolean hasLink()
    {
        return link != null && link.length() > 0;
    }

    /**
     * Returns the link text.
     */
    public String getLinkText()
    {
        return linkText;
    }

    /**
     * Sets the link text.
     */
    public void setLinkText(String linkText)
    {
        this.linkText = linkText;
    }
}