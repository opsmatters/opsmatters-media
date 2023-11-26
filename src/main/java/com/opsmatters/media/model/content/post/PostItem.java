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
 * Class representing a post list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostItem extends ArticleItem<Post>
{
    private Post content = new Post();

    /**
     * Default constructor.
     */
    public PostItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public PostItem(PostItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a post.
     */
    public PostItem(Post obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PostItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Post obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Post get()
    {
        return content;
    }

    /**
     * Returns the post type.
     */
    public String getPostType()
    {
        return content.getPostType();
    }

    /**
     * Sets the post type.
     */
    public void setPostType(String postType)
    {
        content.setPostType(postType);
    }

    /**
     * Sets the post type.
     */
    public void setPostType(PostType postType)
    {
        setPostType(postType.value());
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