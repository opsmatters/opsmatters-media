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

    /**
     * Default constructor.
     */
    public RoundupSummary()
    {
    }

    public RoundupSummary(String url, boolean removeParameters)
    {
        setUrl(url, removeParameters);
    }

    /**
     * Copy constructor.
     */
    public RoundupSummary(RoundupSummary obj)
    {
        super(obj);

        if(obj != null)
        {
            setUrl(obj.getUrl(), false);
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
     * Sets the resource url.
     */
    public void setUrl(String url, boolean removeParameters)
    {
        setUrl("", url, removeParameters);
    }

    /**
     * Sets the roundup url.
     */
    public void setUrl(String basePath, String url, boolean removeParameters)
    {
        this.url = FormatUtils.getFormattedUrl(basePath, url, removeParameters);
    }
}