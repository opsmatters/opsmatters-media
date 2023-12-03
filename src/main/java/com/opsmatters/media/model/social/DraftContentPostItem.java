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

import com.opsmatters.media.model.content.ContentType;

/**
 * Class representing a draft content post item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DraftContentPostItem extends DraftPostItem<DraftContentPost>
{
    private DraftContentPost content = new DraftContentPost();

    /**
     * Default constructor.
     */
    public DraftContentPostItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public DraftContentPostItem(DraftContentPostItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a draft content post.
     */
    public DraftContentPostItem(DraftContentPost obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DraftContentPostItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(DraftContentPost obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public DraftContentPost get()
    {
        return content;
    }

    /**
     * Returns the post organisation.
     */
    public String getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the post organisation.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return content.getOrganisation();
    }

    /**
     * Sets the organisation name.
     */
    public void setOrganisation(String organisation)
    {
        content.setOrganisation(organisation);
    }

    /**
     * Returns the post content type.
     */
    public ContentType getContentType()
    {
        return content.getContentType();
    }

    /**
     * Sets the post content type.
     */
    public void setContentType(ContentType contentType)
    {
        content.setContentType(contentType);
    }

    /**
     * Sets the post content type from a value.
     */
    public void setContentType(String contentType)
    {
        content.setContentType(contentType);
    }
}