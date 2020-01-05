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

import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a roundup summary.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupSummary extends ArticleSummary
{
    private String url = "";
    private String imageSource = "";
    private String imagePrefix = "";
    private boolean imageRefresh = false;

    /**
     * Default constructor.
     */
    public RoundupSummary()
    {
    }

    /**
     * Constructor that takes a url.
     */
    public RoundupSummary(String url)
    {
        setUrl(url);
    }

    /**
     * Copy constructor.
     */
    public RoundupSummary(RoundupSummary obj)
    {
        super(obj);

        if(obj != null)
        {
            setUrl(obj.getUrl());
            setImageSource(obj.getImageSource());
            setImagePrefix(obj.getImagePrefix());
        }
    }

    /**
     * Returns the roundup url.
     */
    @Override
    public String getUniqueId()
    {
        return getUrl();
    }

    /**
     * Returns the roundup url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the roundup url.
     */
    public void setUrl(String url)
    {
        setUrl("", url, true);
    }

    /**
     * Sets the roundup url.
     */
    public void setUrl(String basePath, String url, boolean removeParameters)
    {
        this.url = FormatUtils.getFormattedUrl(basePath, url, removeParameters);
    }

    /**
     * Returns the roundup image source.
     */
    public String getImageSource()
    {
        return imageSource;
    }

    /**
     * Sets the roundup image source.
     */
    public void setImageSource(String imageSource)
    {
        setImageSource("", imageSource, false);
    }

    /**
     * Sets the roundup image source.
     */
    public void setImageSource(String basePath, String imageSource, boolean removeParameters)
    {
        this.imageSource = FormatUtils.getFormattedUrl(basePath, imageSource, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the roundup image source has been set.
     */
    public boolean hasImageSource()
    {
        return imageSource != null && imageSource.length() > 0;
    }

    /**
     * Returns the image prefix.
     */
    public String getImagePrefix()
    {
        return imagePrefix;
    }

    /**
     * Sets the image prefix.
     */
    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }

    /**
     * Sets the roundup image name.
     */
    public void setImageFromPath(String path)
    {
        if(path != null && path.length() > 0)
        {
            path = path.substring(path.lastIndexOf("/")+1);

            String prefix = getImagePrefix()+"-";
            if(!path.startsWith(prefix))
                path = prefix+path;
        }

        setImage(path);
        setImage(FormatUtils.getFormattedImageFilename(getImage()));
    }

    /**
     * Returns <CODE>true</CODE> if the image needs to be refreshed after being rendered.
     */
    public boolean isImageRefresh()
    {
        return imageRefresh;
    }

    /**
     * Set to <CODE>true</CODE> if the image needs to be refreshed after being rendered.
     */
    public void setImageRefresh(boolean imageRefresh)
    {
        this.imageRefresh = imageRefresh;
    }
}