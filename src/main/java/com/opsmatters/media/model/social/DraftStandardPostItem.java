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
package com.opsmatters.media.model.social;

/**
 * Class representing a draft standard post item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DraftStandardPostItem extends DraftPostItem<DraftStandardPost>
{
    private DraftStandardPost content = new DraftStandardPost();

    /**
     * Default constructor.
     */
    public DraftStandardPostItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public DraftStandardPostItem(DraftStandardPostItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a draft standard post.
     */
    public DraftStandardPostItem(DraftStandardPost obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DraftStandardPostItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(DraftStandardPost obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public DraftStandardPost get()
    {
        return content;
    }

    /**
     * Returns the post title.
     */
    public String getTitle()
    {
        return content.getTitle();
    }

    /**
     * Sets the post title.
     */
    public void setTitle(String title)
    {
        content.setTitle(title);
    }
}