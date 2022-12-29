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

import com.opsmatters.media.model.content.video.Video;
import com.opsmatters.media.model.content.roundup.RoundupArticle;
import com.opsmatters.media.model.content.post.PostArticle;

/**
 * Class representing a wrapped content article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ArticleWrapper extends Article
{
    private Article article;
    private String title = "";
    private String summary = "";

    /**
     * Default constructor.
     */
    public ArticleWrapper()
    {
    }

    /**
     * Constructor that takes an article.
     */
    public ArticleWrapper(Article article)
    {
        setArticle(article);
    }

    /**
     * Returns the article wrapped by this object.
     */
    public Article getArticle()
    {
        return article;
    }

    /**
     * Sets the article wrapped by this object.
     */
    public void setArticle(Article article)
    {
        super.copyAttributes(article);
        this.article = article;
    }

    /**
     * Returns <CODE>true</CODE> if this object contains an article.
     */
    public boolean hasArticle()
    {
        return article != null;
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return article != null ? article.getType() : null;
    }

    /**
     * Returns the content title.
     */
    @Override
    public String getTitle()
    {
        return article != null ? article.getTitle() : title;
    }

    /**
     * Sets the content title.
     */
    @Override
    public void setTitle(String title)
    {
        if(article != null)
            article.setTitle(title);
        else if(title != null)
            this.title = title;
    }

    /**
     * Returns the content summary.
     */
    @Override
    public String getSummary()
    {
        return article != null ? article.getSummary() : summary;
    }

    /**
     * Sets the content summary.
     */
    @Override
    public void setSummary(String summary)
    {
        if(article != null)
            article.setSummary(summary);
        else if(summary != null)
            this.summary = summary;
    }

    /**
     * Returns the content published date.
     */
    @Override
    public Instant getPublishedDate()
    {
        return article != null ? article.getPublishedDate() : null;
    }

    /**
     * Sets the content published date.
     */
    @Override
    public void setPublishedDate(Instant publishedDate)
    {
        if(article != null)
            article.setPublishedDate(publishedDate);
    }

    /**
     * Returns the content url.
     */
    public String getUrl()
    {
        String ret = null;

        if(article instanceof RoundupArticle)
        {
            RoundupArticle roundup = (RoundupArticle)article;
            ret = roundup.getUrl();
        }
        else if(article instanceof Video)
        {
            Video video = (Video)article;
            ret = video.getVideoUrl();
        }
        else if(article instanceof PostArticle)
        {
            PostArticle post = (PostArticle)article;
            ret = post.getUrl();
        }

        return ret;
    }

    /**
     * Sets the content url.
     */
    public void setUrl(String url)
    {
        if(article instanceof RoundupArticle)
        {
            RoundupArticle roundup = (RoundupArticle)article;
            roundup.setUrl(url);
        }
        else if(article instanceof Video)
        {
            Video video = (Video)article;
            video.setVideoId(video.getProvider().getVideoId(url));
        }
        else if(article instanceof PostArticle)
        {
            PostArticle post = (PostArticle)article;
            post.setUrl(url);
        }
    }

    /**
     * Returns <CODE>true</CODE> if the content has a url.
     */
    public boolean hasUrl()
    {
        String url = getUrl();
        return url != null && url.length() > 0;
    }

    /**
     * Returns the content image.
     */
    public String getImage()
    {
        String ret = null;

        if(article instanceof RoundupArticle)
        {
            RoundupArticle roundup = (RoundupArticle)article;
            ret = roundup.getImage();
        }
        else if(article instanceof PostArticle)
        {
            PostArticle post = (PostArticle)article;
            ret = post.getImage();
        }

        return ret;
    }

    /**
     * Sets the content image.
     */
    public void setImage(String image)
    {
        if(article instanceof RoundupArticle)
        {
            RoundupArticle roundup = (RoundupArticle)article;
            roundup.setImage(image);
        }
        else if(article instanceof PostArticle)
        {
            PostArticle post = (PostArticle)article;
            post.setImage(image);
        }
    }

    /**
     * Returns <CODE>true</CODE> if the content has an image.
     */
    public boolean hasImage()
    {
        String image = getImage();
        return image != null && image.length() > 0;
    }

    /**
     * Returns the video ID.
     */
    public String getVideoId()
    {
        String ret = null;
        if(article instanceof Video)
            ret = ((Video)article).getVideoId();
        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the content has a video ID.
     */
    public boolean hasVideoId()
    {
        boolean ret = false;
        if(article instanceof Video)
            ret = ((Video)article).hasVideoId();
        return ret;
    }

    /**
     * Returns the video duration in hh:MM:ss format.
     */
    public String getFormattedDuration(boolean replaceZero)
    {
        String ret = null;
        if(article instanceof Video)
            ret = ((Video)article).getFormattedDuration(replaceZero);
        return ret;
    }
}