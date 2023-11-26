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
import com.opsmatters.media.model.content.post.Post;
import com.opsmatters.media.model.content.post.RoundupPost;

/**
 * Class representing the result of an article search.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ArticleResult extends Article
{
    private Article article;
    private String title = "";
    private String summary = "";

    /**
     * Default constructor.
     */
    public ArticleResult()
    {
        set(new Post()); // default object
    }

    /**
     * Constructor that takes an article.
     */
    public ArticleResult(Article article)
    {
        set(article);
    }

    /**
     * Returns the article.
     */
    public Article get()
    {
        return article;
    }

    /**
     * Sets the article wrapped by this object.
     */
    public void set(Article article)
    {
        super.copyAttributes(article);
        this.article = article;
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

        if(article instanceof RoundupPost)
        {
            RoundupPost post = (RoundupPost)article;
            ret = post.getUrl();
        }
        else if(article instanceof Video)
        {
            Video video = (Video)article;
            ret = video.getVideoUrl();
        }
        else if(article instanceof Post)
        {
            Post post = (Post)article;
            ret = post.getUrl();
        }

        return ret;
    }

    /**
     * Sets the content url.
     */
    public void setUrl(String url)
    {
        if(article instanceof RoundupPost)
        {
            RoundupPost post = (RoundupPost)article;
            post.setUrl(url);
        }
        else if(article instanceof Video)
        {
            Video video = (Video)article;
            video.setVideoId(video.getProvider().getVideoId(url));
        }
        else if(article instanceof Post)
        {
            Post post = (Post)article;
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

        if(article instanceof RoundupPost)
        {
            RoundupPost post = (RoundupPost)article;
            ret = post.getImage();
        }
        else if(article instanceof Post)
        {
            Post post = (Post)article;
            ret = post.getImage();
        }

        return ret;
    }

    /**
     * Sets the content image.
     */
    public void setImage(String image)
    {
        if(article instanceof RoundupPost)
        {
            RoundupPost post = (RoundupPost)article;
            post.setImage(image);
        }
        else if(article instanceof Post)
        {
            Post post = (Post)article;
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