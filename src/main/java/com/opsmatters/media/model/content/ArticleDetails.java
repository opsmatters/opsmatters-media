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
 * Class representing an article details.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ArticleDetails extends ContentDetails
{
    private String author = "";
    private String authorUrl = "";

    /**
     * Default constructor.
     */
    public ArticleDetails()
    {
    }

    /**
     * Copy constructor.
     */
    public ArticleDetails(ArticleDetails obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ArticleDetails obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setAuthor(obj.getAuthor());
            setAuthorUrl(obj.getAuthorUrl());
        }
    }

    /**
     * Returns the content author.
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Sets the content author.
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**
     * Returns the author URL of the content.
     */
    public String getAuthorUrl()
    {
        return authorUrl;
    }

    /**
     * Sets the author URL of the content.
     */
    public void setAuthorUrl(String authorUrl)
    {
        setAuthorUrl("", authorUrl, false);
    }

    /**
     * Sets the author URL of the content.
     */
    public void setAuthorUrl(String basePath, String authorUrl, boolean removeParameters)
    {
        this.authorUrl = FormatUtils.getFormattedUrl(basePath, authorUrl, removeParameters);

        // Excape spaces in the URL
        if(this.authorUrl != null)
            this.authorUrl = this.authorUrl.replaceAll(" ", "%20");
    }
}