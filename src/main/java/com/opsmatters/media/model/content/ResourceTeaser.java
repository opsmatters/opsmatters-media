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
 * Class representing a resource teaser.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ResourceTeaser extends ContentTeaser implements LinkedContent
{
    private String description = "";
    private String url = "";

    /**
     * Default constructor.
     */
    public ResourceTeaser()
    {
    }

    /**
     * Constructor that takes a url.
     */
    public ResourceTeaser(String url, boolean removeParameters)
    {
        setUrl(url, removeParameters);
    }

    /**
     * Copy constructor.
     */
    public ResourceTeaser(ResourceTeaser obj)
    {
        super(obj);

        if(obj != null)
        {
            setDescription(obj.getDescription());
            setUrl(obj.getUrl(), false);
        }
    }

    /**
     * Returns the resource description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the resource description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the resource url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the resource url.
     */
    public void setUrl(String url, boolean removeParameters)
    {
        setUrl("", url, removeParameters);
    }

    /**
     * Sets the resource url.
     */
    public void setUrl(String basePath, String url, boolean removeParameters)
    {
        this.url = FormatUtils.getFormattedUrl(basePath, url, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the resource url has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.length() > 0;
    }
}