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
 * Class representing a content post template item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentPostTemplateItem extends PostSourceItem<ContentPostTemplate>
{
    private ContentPostTemplate content = new ContentPostTemplate();

    /**
     * Default constructor.
     */
    public ContentPostTemplateItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ContentPostTemplateItem(ContentPostTemplateItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a post template.
     */
    public ContentPostTemplateItem(ContentPostTemplate obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentPostTemplateItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(ContentPostTemplate obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public ContentPostTemplate get()
    {
        return content;
    }

    /**
     * Returns the template name.
     */
    public String getName()
    {
        return content.getName();
    }

    /**
     * Sets the template name.
     */
    public void setName(String name)
    {
        content.setName(name);
    }

    /**
     * Returns the template content type.
     */
    public ContentType getContentType()
    {
        return content.getContentType();
    }

    /**
     * Sets the template content type.
     */
    public void setContentType(ContentType contentType)
    {
        content.setContentType(contentType);
    }

    /**
     * Sets the template content type from a value.
     */
    public void setContentType(String contentType)
    {
        content.setContentType(contentType);
    }

    /**
     * Returns <CODE>true</CODE> if this template is the default for the content type.
     */
    public boolean isDefault()
    {
        return content.isDefault();
    }

    /**
     * Set to <CODE>true</CODE> if this template is the default for the content type.
     */
    public void setDefault(boolean isDefault)
    {
        content.setDefault(isDefault);
    }
}