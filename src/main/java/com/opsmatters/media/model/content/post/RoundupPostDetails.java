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
package com.opsmatters.media.model.content.post;

import com.opsmatters.media.model.content.ArticleDetails;
import com.opsmatters.media.model.content.LinkedContent;
import com.opsmatters.media.util.FormatUtils;

/**
 * Class representing a roundup post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupPostDetails extends ArticleDetails implements LinkedContent
{
    private String url = "";

    /**
     * Default constructor.
     */
    public RoundupPostDetails()
    {
    }

    public RoundupPostDetails(String url, boolean removeParameters)
    {
        setUrl(url, removeParameters);
    }

    /**
     * Copy constructor.
     */
    public RoundupPostDetails(RoundupPostDetails obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RoundupPostDetails obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
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

    /**
     * Returns <CODE>true</CODE> if the roundup url has been set.
     */
    public boolean hasUrl()
    {
        return url != null && url.length() > 0;
    }
}