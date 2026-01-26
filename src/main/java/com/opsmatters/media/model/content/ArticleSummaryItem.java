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

import com.opsmatters.media.model.content.video.Video;
import com.opsmatters.media.model.content.post.Post;
import com.opsmatters.media.model.content.post.RoundupPost;

/**
 * Class representing a content summary containing an article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ArticleSummaryItem extends ArticleItem<Article>
{
    private Article content;

    /**
     * Default constructor.
     */
    public ArticleSummaryItem()
    {
        content = new Post();
        super.set(content);
    }

    /**
     * Constructor that takes an article.
     */
    public ArticleSummaryItem(ArticleItem content)
    {
        Article article = content.get();
        this.content = article;
        super.set(article);
    }

    /**
     * Constructor that takes an article.
     */
    public ArticleSummaryItem(Article content)
    {
        this.content = content;
        super.set(content);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ArticleSummaryItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Article obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public void set(Article content)
    {
        super.set(content);
        this.content = content;
    }

    /**
     * Returns the content summary.
     */
    public String getSummary()
    {
        return content.getSummary();
    }

    /**
     * Returns the content url.
     */
    public String getUrl()
    {
        String ret = null;

        if(content instanceof RoundupPost)
        {
            RoundupPost post = (RoundupPost)content;
            ret = post.getUrl();
        }
        else if(content instanceof Video)
        {
            Video video = (Video)content;
            ret = video.getVideoUrl();
        }
        else if(content instanceof Post)
        {
            Post post = (Post)content;
            ret = post.getUrl();
        }

        return ret;
    }

    /**
     * Returns the content image.
     */
    public String getImage()
    {
        return content.getImage();
    }

    /**
     * Returns the video ID.
     */
    public String getVideoId()
    {
        String ret = null;
        if(content instanceof Video)
            ret = ((Video)content).getVideoId();
        return ret;
    }
}