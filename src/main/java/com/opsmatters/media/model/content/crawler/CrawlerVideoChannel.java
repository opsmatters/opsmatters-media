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
package com.opsmatters.media.model.content.crawler;

import java.util.Map;
import com.opsmatters.media.cache.provider.VideoProviders;
import com.opsmatters.media.model.provider.VideoProvider;
import com.opsmatters.media.model.content.crawler.field.Fields;
import com.opsmatters.media.model.content.crawler.field.Field;
import com.opsmatters.media.util.Formats;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class that represents a YAML configuration for a video channel crawler.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CrawlerVideoChannel extends CrawlerTarget
{
    private String channelId = "";
    private String userId = "";

    /**
     * Constructor that takes a name.
     */
    public CrawlerVideoChannel(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public CrawlerVideoChannel(CrawlerVideoChannel obj)
    {
        super(obj.getName());
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(CrawlerVideoChannel obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setChannelId(obj.getChannelId());
            setUserId(obj.getUserId());
        }
    }

    /**
     * Returns the channel id for this configuration.
     */
    public String getChannelId()
    {
        return channelId;
    }

    /**
     * Sets the channel id for this configuration.
     */
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    /**
     * Returns <CODE>true</CODE> if the channel id for this configuration has been set.
     */
    public boolean hasChannelId()
    {
        return channelId != null && channelId.length() > 0;
    }

    /**
     * Returns the user id for this configuration.
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Sets the user id for this configuration.
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * Returns <CODE>true</CODE> if the user id for this configuration has been set.
     */
    public boolean hasUserId()
    {
        return userId != null && userId.length() > 0;
    }

    /**
     * Returns the video provider for this configuration.
     */
    public VideoProvider getVideoProvider()
    {
        return VideoProviders.getByTag(getProvider());
    }

    /**
     * Returns the channel url for this configuration.
     */
    public String getChannelUrl()
    {
        VideoProvider provider = getVideoProvider();
        String ret = null;
        if(provider != null)
            ret = provider.getUrl()+String.format(provider.getChannelUrl(), channelId);
        return ret;
    }

    /**
     * Returns a builder for the configuration.
     * @param name The name of the configuration
     * @return The builder instance.
     */
    public static Builder builder(String name)
    {
        return new Builder(name);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder extends CrawlerTarget.Builder<CrawlerVideoChannel, Builder>
    {
        // The config attribute names
        private static final String CHANNEL_ID = "channelId";
        private static final String USER_ID = "userId";

        private CrawlerVideoChannel ret = null;

        /**
         * Constructor that takes a name.
         * @param name The nam for the configuration
         */
        public Builder(String name)
        {
            ret = new CrawlerVideoChannel(name);
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);

            if(map.containsKey(CHANNEL_ID))
                ret.setChannelId((String)map.get(CHANNEL_ID));
            if(map.containsKey(USER_ID))
                ret.setUserId((String)map.get(USER_ID));

            setTeaserFields(ret.getTeasers());
            setArticleFields(ret.getArticles());

            return this;
        }

        /**
         * Set the default video teaser fields if they are not present.
         */
        private void setTeaserFields(CrawlerContent teasers)
        {
            if(!teasers.hasFields())
                teasers.addFields(new Fields());
            Fields fields = teasers.getFields().get(0);
            if(!fields.hasTitle())
                fields.setTitle(createTitleField());
            if(!fields.hasPublishedDate())
                fields.setPublishedDate(createPublishedDateField());
        }

        /**
         * Set the default video article fields if they are not present.
         */
        private void setArticleFields(CrawlerContent articles)
        {
            if(!articles.hasFields())
                articles.addFields(new Fields());
            Fields fields = articles.getFields().get(0);
            if(!fields.hasTitle())
                fields.setTitle(createTitleField());
            if(!fields.hasPublishedDate())
                fields.setPublishedDate(createPublishedDateField());
            if(!fields.hasBody())
                fields.setBody(createBodyField());
        }

        /**
         * Create a title field object.
         */
        private Field createTitleField()
        {
            return Field.builder(Fields.Builder.TITLE)
                .expr(TITLE.value())
                .build();
        }

        /**
         * Create a published date field object.
         */
        private Field createPublishedDateField()
        {
            return Field.builder(Fields.Builder.PUBLISHED_DATE)
                .expr(PUBLISHED_DATE.value())
                .datePattern(Formats.LONG_ISO8601_FORMAT)
                .build();
        }

        /**
         * Create a body field object.
         */
        private Field createBodyField()
        {
            return Field.builder(Fields.Builder.BODY)
                .expr(DESCRIPTION.value())
                .build();
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public CrawlerVideoChannel build()
        {
            return ret;
        }
    }
}