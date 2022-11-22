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
package com.opsmatters.media.model.content.crawler.field;

import java.util.Map;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Class that represents a set of field selectors for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Fields implements ConfigElement
{
    private String root = "";
    private Field validator;
    private Field title;
    private Field author;
    private Field authorLink;
    private Field publishedDate;
    private Field startDate;
    private Field startTime;
    private Field endDate;
    private Field endTime;
    private Field timezone;
    private Field body;
    private Field image;
    private Field backgroundImage;
    private Field url;

    /**
     * Default constructor.
     */
    public Fields()
    {
    }

    /**
     * Copy constructor.
     */
    public Fields(Fields obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Fields obj)
    {
        if(obj != null)
        {
            setRoot(obj.getRoot());
            if(obj.getValidator() != null)
                setValidator(new Field(obj.getValidator()));
            if(obj.getTitle() != null)
                setTitle(new Field(obj.getTitle()));
            if(obj.getAuthor() != null)
                setAuthor(new Field(obj.getAuthor()));
            if(obj.getAuthorLink() != null)
                setAuthorLink(new Field(obj.getAuthorLink()));
            if(obj.getPublishedDate() != null)
                setPublishedDate(new Field(obj.getPublishedDate()));
            if(obj.getStartDate() != null)
                setStartDate(new Field(obj.getStartDate()));
            if(obj.getStartTime() != null)
                setStartTime(new Field(obj.getStartTime()));
            if(obj.getEndDate() != null)
                setEndDate(new Field(obj.getEndDate()));
            if(obj.getEndTime() != null)
                setEndTime(new Field(obj.getEndTime()));
            if(obj.getTimeZone() != null)
                setTimeZone(new Field(obj.getTimeZone()));
            if(obj.getBody() != null)
                setBody(new Field(obj.getBody()));
            if(obj.getUrl() != null)
                setUrl(new Field(obj.getUrl()));
            if(obj.getImage() != null)
                setImage(new Field(obj.getImage()));
            if(obj.getBackgroundImage() != null)
                setBackgroundImage(new Field(obj.getBackgroundImage()));
        }
    }

    /**
     * Returns the root selector for this configuration.
     */
    public String getRoot()
    {
        return root;
    }

    /**
     * Sets the root selector for this configuration.
     */
    public void setRoot(String root)
    {
        this.root = root;
    }

    /**
     * Returns <CODE>true</CODE> if the root selector has been set.
     */
    public boolean hasRoot()
    {
        return root != null && root.length() > 0;
    }

    /**
     * Returns the validator for this configuration.
     */
    public Field getValidator()
    {
        return validator;
    }

    /**
     * Sets the validator for this configuration.
     */
    public void setValidator(Field validator)
    {
        this.validator = validator;
    }

    /**
     * Returns <CODE>true</CODE> if the validator selector has been set.
     */
    public boolean hasValidator()
    {
        return validator != null && validator.hasSelectors();
    }

    /**
     * Returns the title for this configuration.
     */
    public Field getTitle()
    {
        return title;
    }

    /**
     * Sets the title for this configuration.
     */
    public void setTitle(Field title)
    {
        this.title = title;
    }

    /**
     * Returns <CODE>true</CODE> if the title has been set.
     */
    public boolean hasTitle()
    {
        return title != null && title.hasSelectors();
    }

    /**
     * Returns the author for this configuration.
     */
    public Field getAuthor()
    {
        return author;
    }

    /**
     * Sets the author for this configuration.
     */
    public void setAuthor(Field author)
    {
        this.author = author;
    }

    /**
     * Returns <CODE>true</CODE> if the author has been set.
     */
    public boolean hasAuthor()
    {
        return author != null && author.hasSelectors();
    }

    /**
     * Returns the link to the author for this configuration.
     */
    public Field getAuthorLink()
    {
        return authorLink;
    }

    /**
     * Sets the link to the author for this configuration.
     */
    public void setAuthorLink(Field authorLink)
    {
        this.authorLink = authorLink;
    }

    /**
     * Returns <CODE>true</CODE> if the link to the author has been set.
     */
    public boolean hasAuthorLink()
    {
        return authorLink != null && authorLink.hasSelectors();
    }

    /**
     * Returns the published date for this configuration.
     */
    public Field getPublishedDate()
    {
        return publishedDate;
    }

    /**
     * Sets the published date for this configuration.
     */
    public void setPublishedDate(Field publishedDate)
    {
        this.publishedDate = publishedDate;
    }

    /**
     * Returns <CODE>true</CODE> if the published date has been set.
     */
    public boolean hasPublishedDate()
    {
        return publishedDate != null && publishedDate.hasSelectors();
    }

    /**
     * Returns the start date for this configuration.
     */
    public Field getStartDate()
    {
        return startDate;
    }

    /**
     * Sets the start date for this configuration.
     */
    public void setStartDate(Field startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Returns <CODE>true</CODE> if the start date has been set.
     */
    public boolean hasStartDate()
    {
        return startDate != null && startDate.hasSelectors();
    }

    /**
     * Returns the start time for this configuration.
     */
    public Field getStartTime()
    {
        return startTime;
    }

    /**
     * Sets the start time for this configuration.
     */
    public void setStartTime(Field startTime)
    {
        this.startTime = startTime;
    }

    /**
     * Returns <CODE>true</CODE> if the start time has been set.
     */
    public boolean hasStartTime()
    {
        return startTime != null && startTime.hasSelectors();
    }

    /**
     * Returns the end date for this configuration.
     */
    public Field getEndDate()
    {
        return endDate;
    }

    /**
     * Sets the end date for this configuration.
     */
    public void setEndDate(Field endDate)
    {
        this.endDate = endDate;
    }

    /**
     * Returns <CODE>true</CODE> if the end date has been set.
     */
    public boolean hasEndDate()
    {
        return endDate != null && endDate.hasSelectors();
    }

    /**
     * Returns the end time for this configuration.
     */
    public Field getEndTime()
    {
        return endTime;
    }

    /**
     * Sets the end time for this configuration.
     */
    public void setEndTime(Field endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Returns <CODE>true</CODE> if the end time has been set.
     */
    public boolean hasEndTime()
    {
        return endTime != null && endTime.hasSelectors();
    }

    /**
     * Returns the timezone for this configuration.
     */
    public Field getTimeZone()
    {
        return timezone;
    }

    /**
     * Sets the timezone for this configuration.
     */
    public void setTimeZone(Field timezone)
    {
        this.timezone = timezone;
    }

    /**
     * Returns <CODE>true</CODE> if the timezone has been set.
     */
    public boolean hasTimeZone()
    {
        return timezone != null && timezone.hasSelectors();
    }

    /**
     * Returns the body text for this configuration.
     */
    public Field getBody()
    {
        return body;
    }

    /**
     * Sets the body text for this configuration.
     */
    public void setBody(Field body)
    {
        this.body = body;
    }

    /**
     * Returns <CODE>true</CODE> if the body text has been set.
     */
    public boolean hasBody()
    {
        return body != null && body.hasSelectors();
    }

    /**
     * Returns the image for this configuration.
     */
    public Field getImage()
    {
        return image;
    }

    /**
     * Sets the image for this configuration.
     */
    public void setImage(Field image)
    {
        this.image = image;
    }

    /**
     * Returns <CODE>true</CODE> if the image has been set.
     */
    public boolean hasImage()
    {
        return image != null && image.hasSelectors();
    }

    /**
     * Returns the background image for this configuration.
     */
    public Field getBackgroundImage()
    {
        return backgroundImage;
    }

    /**
     * Sets the background image for this configuration.
     */
    public void setBackgroundImage(Field backgroundImage)
    {
        this.backgroundImage = backgroundImage;
    }

    /**
     * Returns <CODE>true</CODE> if the background image has been set.
     */
    public boolean hasBackgroundImage()
    {
        return backgroundImage != null && backgroundImage.hasSelectors();
    }

    /**
     * Returns the url for this configuration.
     */
    public Field getUrl()
    {
        return url;
    }

    /**
     * Sets the url for this configuration.
     */
    public void setUrl(Field url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the url date has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.hasSelectors();
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<Fields>
    {
        // The config attribute names
        private static final String ROOT = "root";
        private static final String VALIDATOR = "validator";
        private static final String TITLE = "title";
        private static final String AUTHOR = "author";
        private static final String AUTHOR_LINK = "author-link";
        private static final String PUBLISHED_DATE = "published-date";
        private static final String START_DATE = "start-date";
        private static final String START_TIME = "start-time";
        private static final String END_DATE = "end-date";
        private static final String END_TIME = "end-time";
        private static final String TIMEZONE = "timezone";
        public static final String BODY = "body";
        private static final String IMAGE = "image";
        private static final String BACKGROUND_IMAGE = "background-image";
        private static final String URL = "url";

        private Fields ret = new Fields();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(ROOT))
                ret.setRoot((String)map.get(ROOT));
            if(map.containsKey(VALIDATOR))
                ret.setValidator(createField(VALIDATOR, map.get(VALIDATOR)));
            if(map.containsKey(TITLE))
                ret.setTitle(createField(TITLE, map.get(TITLE)));
            if(map.containsKey(AUTHOR))
                ret.setAuthor(createField(AUTHOR, map.get(AUTHOR)));
            if(map.containsKey(AUTHOR_LINK))
                ret.setAuthorLink(createField(AUTHOR_LINK, map.get(AUTHOR_LINK)));
            if(map.containsKey(PUBLISHED_DATE))
                ret.setPublishedDate(createField(PUBLISHED_DATE, map.get(PUBLISHED_DATE)));
            if(map.containsKey(START_DATE))
                ret.setStartDate(createField(START_DATE, map.get(START_DATE)));
            if(map.containsKey(START_TIME))
                ret.setStartTime(createField(START_TIME, map.get(START_TIME)));
            if(map.containsKey(END_DATE))
                ret.setEndDate(createField(END_DATE, map.get(END_DATE)));
            if(map.containsKey(END_TIME))
                ret.setEndTime(createField(END_TIME, map.get(END_TIME)));
            if(map.containsKey(TIMEZONE))
                ret.setTimeZone(createField(TIMEZONE, map.get(TIMEZONE)));
            if(map.containsKey(BODY))
                ret.setBody(createField(BODY, map.get(BODY)));
            if(map.containsKey(URL))
                ret.setUrl(createField(URL, map.get(URL)));
            if(map.containsKey(IMAGE))
                ret.setImage(createField(IMAGE, map.get(IMAGE)));
            if(map.containsKey(BACKGROUND_IMAGE))
                ret.setBackgroundImage(createField(BACKGROUND_IMAGE, map.get(BACKGROUND_IMAGE)));

            return this;
        }

        /**
         * Create a field object for the given field.
         */
        private Field createField(String name, Object value)
        {
            Field.Builder builder = Field.builder(name);
            if(value instanceof String)
                builder = builder.expr((String)value);
            else if(value instanceof Map)
                builder = builder.parse((Map<String,Object>)value);
            return builder.build();
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public Fields build()
        {
            return ret;
        }
    }
}