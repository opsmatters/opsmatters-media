/*
 * Copyright 2023 Gerald Curley
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

import com.opsmatters.media.model.content.ArticleItem;

/**
 * Class representing a roundup post list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupPostItem extends ArticleItem<RoundupPost>
{
    private RoundupPost content = new RoundupPost();

    /**
     * Default constructor.
     */
    public RoundupPostItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public RoundupPostItem(RoundupPostItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a post.
     */
    public RoundupPostItem(RoundupPost obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(RoundupPostItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(RoundupPost obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public RoundupPost get()
    {
        return content;
    }

    /**
     * Returns the URL of the roundup.
     */
    public String getUrl()
    {
        return content.getUrl();
    }

    /**
     * Sets the URL of the roundup.
     */
    public void setUrl(String url)
    {
        content.setUrl(url);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return content.hasUrl();
    }

    /**
     * Returns the content author.
     */
    public String getAuthor()
    {
        return content.getAuthor();
    }

    /**
     * Sets the content author.
     */
    public void setAuthor(String author)
    {
        content.setAuthor(author);
    }
}