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
package com.opsmatters.media.config.content;

import java.util.Map;

/**
 * Class that represents a set of field selectors for a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFields implements java.io.Serializable
{
    public static final String ROOT = "root";
    public static final String VALIDATOR = "validator";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String AUTHOR_LINK = "author-link";
    public static final String PUBLISHED_DATE = "published-date";
    public static final String START_DATE = "start-date";
    public static final String START_TIME = "start-time";
    public static final String END_DATE = "end-date";
    public static final String END_TIME = "end-time";
    public static final String TIMEZONE = "timezone";
    public static final String BODY = "body";
    public static final String IMAGE = "image";
    public static final String BACKGROUND_IMAGE = "background-image";
    public static final String URL = "url";

    private String root = "";
    private String validator = "";
    private ContentField title;
    private ContentField author;
    private ContentField authorLink;
    private ContentField publishedDate;
    private ContentField startDate;
    private ContentField startTime;
    private ContentField endDate;
    private ContentField endTime;
    private ContentField timezone;
    private ContentField body;
    private ContentField image;
    private ContentField backgroundImage;
    private ContentField url;

    /**
     * Default constructor.
     */
    public ContentFields()
    {
    }

    /**
     * Constructor that takes a map of attributes.
     */
    public ContentFields(Map<String, Object> map)
    {
        parse(map);
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
     * Returns the validator selector for this configuration.
     */
    public String getValidator()
    {
        return validator;
    }

    /**
     * Sets the validator selector for this configuration.
     */
    public void setValidator(String validator)
    {
        this.validator = validator;
    }

    /**
     * Returns <CODE>true</CODE> if the validator selector has been set.
     */
    public boolean hasValidator()
    {
        return validator != null && validator.length() > 0;
    }

    /**
     * Returns the title for this configuration.
     */
    public ContentField getTitle()
    {
        return title;
    }

    /**
     * Sets the title for this configuration.
     */
    public void setTitle(ContentField title)
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
    public ContentField getAuthor()
    {
        return author;
    }

    /**
     * Sets the author for this configuration.
     */
    public void setAuthor(ContentField author)
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
    public ContentField getAuthorLink()
    {
        return authorLink;
    }

    /**
     * Sets the link to the author for this configuration.
     */
    public void setAuthorLink(ContentField authorLink)
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
    public ContentField getPublishedDate()
    {
        return publishedDate;
    }

    /**
     * Sets the published date for this configuration.
     */
    public void setPublishedDate(ContentField publishedDate)
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
    public ContentField getStartDate()
    {
        return startDate;
    }

    /**
     * Sets the start date for this configuration.
     */
    public void setStartDate(ContentField startDate)
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
    public ContentField getStartTime()
    {
        return startTime;
    }

    /**
     * Sets the start time for this configuration.
     */
    public void setStartTime(ContentField startTime)
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
    public ContentField getEndDate()
    {
        return endDate;
    }

    /**
     * Sets the end date for this configuration.
     */
    public void setEndDate(ContentField endDate)
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
    public ContentField getEndTime()
    {
        return endTime;
    }

    /**
     * Sets the end time for this configuration.
     */
    public void setEndTime(ContentField endTime)
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
    public ContentField getTimeZone()
    {
        return timezone;
    }

    /**
     * Sets the timezone for this configuration.
     */
    public void setTimeZone(ContentField timezone)
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
    public ContentField getBody()
    {
        return body;
    }

    /**
     * Sets the body text for this configuration.
     */
    public void setBody(ContentField body)
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
    public ContentField getImage()
    {
        return image;
    }

    /**
     * Sets the image for this configuration.
     */
    public void setImage(ContentField image)
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
    public ContentField getBackgroundImage()
    {
        return backgroundImage;
    }

    /**
     * Sets the background image for this configuration.
     */
    public void setBackgroundImage(ContentField backgroundImage)
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
    public ContentField getUrl()
    {
        return url;
    }

    /**
     * Sets the url for this configuration.
     */
    public void setUrl(ContentField url)
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
     * Reads the configuration from the given YAML Document.
     */
    public void parse(Map<String, Object> map)
    {
        if(map.containsKey(ROOT))
            setRoot((String)map.get(ROOT));
        if(map.containsKey(VALIDATOR))
            setValidator((String)map.get(VALIDATOR));
        if(map.containsKey(TITLE))
            setTitle(createField(TITLE, map.get(TITLE)));
        if(map.containsKey(AUTHOR))
            setAuthor(createField(AUTHOR, map.get(AUTHOR)));
        if(map.containsKey(AUTHOR_LINK))
            setAuthorLink(createField(AUTHOR_LINK, map.get(AUTHOR_LINK)));
        if(map.containsKey(PUBLISHED_DATE))
            setPublishedDate(createField(PUBLISHED_DATE, map.get(PUBLISHED_DATE)));
        if(map.containsKey(START_DATE))
            setStartDate(createField(START_DATE, map.get(START_DATE)));
        if(map.containsKey(START_TIME))
            setStartTime(createField(START_TIME, map.get(START_TIME)));
        if(map.containsKey(END_DATE))
            setEndDate(createField(END_DATE, map.get(END_DATE)));
        if(map.containsKey(END_TIME))
            setEndTime(createField(END_TIME, map.get(END_TIME)));
        if(map.containsKey(TIMEZONE))
            setTimeZone(createField(TIMEZONE, map.get(TIMEZONE)));
        if(map.containsKey(BODY))
            setBody(createField(BODY, map.get(BODY)));
        if(map.containsKey(URL))
            setUrl(createField(URL, map.get(URL)));
        if(map.containsKey(IMAGE))
            setImage(createField(IMAGE, map.get(IMAGE)));
        if(map.containsKey(BACKGROUND_IMAGE))
            setBackgroundImage(createField(BACKGROUND_IMAGE, map.get(BACKGROUND_IMAGE)));
    }

    /**
     * Create a field object for the given field.
     */
    public ContentField createField(String name, Object value)
    {
        ContentField ret = null;
        if(value instanceof String)
            ret = new ContentField(name, (String)value);
        else if(value instanceof Map)
            ret = new ContentField(name, (Map<String,Object>)value);
        return ret;
    }
}