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
 * Class representing an article teaser.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ArticleTeaser extends ContentTeaser
{
    private String author = "";
    private String authorLink = "";

    /**
     * Default constructor.
     */
    public ArticleTeaser()
    {
    }

    /**
     * Copy constructor.
     */
    public ArticleTeaser(ArticleTeaser obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ArticleTeaser obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setAuthor(obj.getAuthor());
            setAuthorLink(obj.getAuthorLink());
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
     * Returns the author link of the content.
     */
    public String getAuthorLink()
    {
        return authorLink;
    }

    /**
     * Sets the author link of the content.
     */
    public void setAuthorLink(String authorLink)
    {
        setAuthorLink("", authorLink, false);
    }

    /**
     * Sets the author link of the content.
     */
    public void setAuthorLink(String basePath, String authorLink, boolean removeParameters)
    {
        this.authorLink = FormatUtils.getFormattedUrl(basePath, authorLink, removeParameters);

        // Excape spaces in the URL
        if(this.authorLink != null)
            this.authorLink = this.authorLink.replaceAll(" ", "%20");
    }
}