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

import java.time.Instant;
import java.util.List;
import java.time.format.DateTimeParseException;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a content teaser.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentTeaser implements java.io.Serializable
{
    private String uuid = "";
    private String title = "";
    private Instant publishedDate;
    private String summary = "";
    private String image = "";
    private String imageSource = "";
    private boolean valid = true;

    /**
     * Default constructor.
     */
    public ContentTeaser()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentTeaser(ContentTeaser obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentTeaser obj)
    {
        if(obj != null)
        {
            setUuid(obj.getUuid());
            setTitle(obj.getTitle());
            setPublishedDate(obj.getPublishedDate());
            setSummary(obj.getSummary());
            setImage(obj.getImage());
            setImageSource(obj.getImageSource());
        }
    }

    /**
     * Returns the title.
     */
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the content uuid.
     */
    public String getUniqueId()
    {
        return getUuid();
    }

    /**
     * Returns <CODE>true</CODE> if this teaser has the same unique id as the given teaser.
     */
    public boolean hasSameUniqueId(ContentTeaser teaser)
    {
        return getUniqueId().equals(teaser.getUniqueId());
    }

    /**
     * Returns the content uuid.
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * Sets the content uuid.
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Returns <CODE>true</CODE> if the content item is valid.
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Set to <CODE>true</CODE> if the content item is valid.
     */
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    /**
     * Returns <CODE>true</CODE> if the content item matches the given keywords.
     */
    public boolean matches(List<String> keywords)
    {
        boolean ret = false;
        String search = getTitle().toLowerCase();
        for(String keyword : keywords)
        {
            if(search.indexOf(keyword) != -1)
            {
                ret = true;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the content title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the content title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns <CODE>true</CODE> if the title has been set.
     */
    public boolean hasTitle()
    {
        return getTitle() != null && getTitle().length() > 0;
    }

    /**
     * Returns the date the content was published.
     */
    public Instant getPublishedDate()
    {
        return publishedDate;
    }

    /**
     * Returns the date the content was published.
     */
    public long getPublishedDateMillis()
    {
        return getPublishedDate() != null ? getPublishedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(publishedDate, pattern);
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString()
    {
        return getPublishedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDate(Instant publishedDate)
    {
        this.publishedDate = publishedDate;
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateMillis(long millis)
    {
        if(millis > 0L)
            this.publishedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setPublishedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateAsString(String str) throws DateTimeParseException
    {
        setPublishedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Returns the content summary.
     */
    public String getSummary()
    {
        return summary;
    }

    /**
     * Sets the content summary.
     */
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    /**
     * Returns the content image filename.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Sets the content image filename.
     */
    public void setImage(String image)
    {
        this.image = image;
    }

    /**
     * Returns <CODE>true</CODE> if the content image has been set.
     */
    public boolean hasImage()
    {
        return image != null && image.length() > 0;
    }

    /**
     * Returns the content image source.
     */
    public String getImageSource()
    {
        return imageSource;
    }

    /**
     * Sets the content image source.
     */
    public void setImageSource(String imageSource)
    {
        setImageSource("", imageSource, false);
    }

    /**
     * Sets the content image source.
     */
    public void setImageSource(String basePath, String imageSource, boolean removeParameters)
    {
        this.imageSource = FormatUtils.getFormattedUrl(basePath, imageSource, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the content image source has been set.
     */
    public boolean hasImageSource()
    {
        return imageSource != null && imageSource.length() > 0;
    }

    /**
     * Sets the content image name.
     */
    public void setImageFromPath(String prefix, String path)
    {
        if(path != null && path.length() > 0)
        {
            // Look for the url in the parameters
            boolean fromParameters = false;
            if(path.indexOf("image_url=") != -1)
            {
                path = path.replaceAll(".+image_url=(.+?)&.+", "$1");
                fromParameters = true;
            }
            else if(path.indexOf("url=") != -1)
            {
                path = path.replaceAll(".+url=(.+?)&.+", "$1");
                fromParameters = true;
            }

            if(fromParameters)
                path = path.replaceAll("%2F", "/");

            // Remove path and convert to lower case
            path = path.substring(path.lastIndexOf("/")+1);
            path = path.toLowerCase();

            // Remove domain
            path = path.replaceAll("^www\\..+?\\.com-(.+)", "$1");

            if(!path.startsWith(prefix+"-"))
                path = String.format("%s-%s", prefix, path);
        }

        setImage(path);
        setImage(FormatUtils.getFormattedImageFilename(getImage()));
    }

    /**
     * Returns <CODE>true</CODE> if the given content has a published date field.
     */
    public static boolean hasPublishedDate(List<? extends ContentTeaser> content)
    {
        boolean ret = false;
        if(content.size() > 0)
        {
            for(ContentTeaser item : content)
            {
                if(item.getPublishedDate() != null)
                {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }
}