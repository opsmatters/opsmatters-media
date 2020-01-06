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
package com.opsmatters.media.model.social;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import twitter4j.Status;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPost implements java.io.Serializable
{
    private String id = "";
    private String title = "";
    private Instant createdDate;
    private String text = "";

    /**
     * Default constructor.
     */
    public SocialPost()
    {
    }

    /**
     * Constructor that takes a Twitter status.
     */
    public SocialPost(Status status)
    {
        setId(Long.toString(status.getId()));
        setCreatedDateMillis(status.getCreatedAt().getTime());
        setText(status.getText());
    }

    /**
     * Copy constructor.
     */
    public SocialPost(SocialPost obj)
    {
        if(obj != null)
        {
            setId(obj.getId());
            setCreatedDate(obj.getCreatedDate());
            setText(obj.getText());
        }
    }

    /**
     * Returns the id.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the post id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the post id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the date the post was created.
     */
    public Instant getCreatedDate()
    {
        return createdDate;
    }

    /**
     * Returns the date the post was created.
     */
    public String getCreatedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(createdDate, pattern);
    }

    /**
     * Returns the date the post was created.
     */
    public String getCreatedDateAsString()
    {
        return getCreatedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDateMillis(long millis)
    {
        if(millis > 0L)
            this.createdDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setCreatedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the post was created.
     */
    public void setCreatedDateAsString(String str) throws DateTimeParseException
    {
        setCreatedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the post text.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the post text.
     */
    public void setText(String text)
    {
        this.text = text;
    }
}