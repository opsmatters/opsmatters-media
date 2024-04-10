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
 * Class representing a social media template item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialTemplateItem extends SocialPostItem<SocialTemplate>
{
    private SocialTemplate content = new SocialTemplate();

    /**
     * Default constructor.
     */
    public SocialTemplateItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public SocialTemplateItem(SocialTemplateItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a template.
     */
    public SocialTemplateItem(SocialTemplate obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialTemplateItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(SocialTemplate obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public SocialTemplate get()
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
     * Returns the weight of the template.
     */
    public int getWeight()
    {
        return content.getWeight();
    }

    /**
     * Sets the weight of the template.
     */
    public void setWeight(int weight)
    {
        content.setWeight(weight);
    }

    /**
     * Returns the template status.
     */
    public TemplateStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the template status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the template status.
     */
    public void setStatus(TemplateStatus status)
    {
        content.setStatus(status);
    }
}