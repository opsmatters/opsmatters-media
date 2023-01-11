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

/**
 * Class representing a post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostDetails extends PostTeaser
{
    private String description = "";

    /**
     * Default constructor.
     */
    public PostDetails()
    {
    }

    /**
     * Copy constructor.
     */
    public PostDetails(PostDetails obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PostDetails obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setDescription(obj.getDescription());
        }
    }

    /**
     * Constructor that takes a teaser.
     */
    public PostDetails(PostTeaser obj)
    {
        super(obj);
    }

    /**
     * Returns the post description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the post description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
}