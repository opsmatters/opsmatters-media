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
package com.opsmatters.media.model.content;

/**
 * Class representing an article list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ArticleItem<T extends Article> extends ContentItem<T>
{
    private T content;

    /**
     * Returns the content object.
     */
    public T get()
    {
        return content;
    }

    /**
     * Returns the content object.
     */
    protected void set(T content)
    {
        super.set(content);
        this.content = content;
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return content.getOrganisation();
    }

    /**
     * Returns <CODE>true</CODE> if this content should be promoted.
     */
    public boolean isPromoted()
    {
        return content.isPromoted();
    }

    /**
     * Set to <CODE>true</CODE> if this content should be promoted.
     */
    public void setPromoted(boolean promote)
    {
        content.setPromoted(promote);
    }

    /**
     * Returns <CODE>true</CODE> if this content should appear in the newsletter.
     */
    public boolean isNewsletter()
    {
        return content.isNewsletter();
    }

    /**
     * Set to <CODE>true</CODE> if this content should appear in the newsletter.
     */
    public void setNewsletter(boolean newsletter)
    {
        content.setNewsletter(newsletter);
    }

    /**
     * Returns the newsletter status of the article.
     */
    public NewsletterStatus getNewsletterStatus()
    {
        return content.getNewsletterStatus();
    }

    /**
     * Returns <CODE>true</CODE> if this content should appear in the featured section.
     */
    public boolean isFeatured()
    {
        return content.isFeatured();
    }

    /**
     * Set to <CODE>true</CODE> if this content should appear in the featured section.
     */
    public void setFeatured(boolean featured)
    {
        content.setFeatured(featured);
    }

    /**
     * Returns <CODE>true</CODE> if this content is sponsored.
     */
    public boolean isSponsored()
    {
        return content.isSponsored();
    }

    /**
     * Set to <CODE>true</CODE> if this content is sponsored.
     */
    public void setSponsored(boolean sponsored)
    {
        content.setSponsored(sponsored);
    }
}