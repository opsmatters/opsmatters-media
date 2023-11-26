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
package com.opsmatters.media.model.content.publication;

import com.opsmatters.media.model.content.ResourceItem;

/**
 * Class representing a publication list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PublicationItem extends ResourceItem<Publication>
{
    private Publication content = new Publication();

    /**
     * Default constructor.
     */
    public PublicationItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public PublicationItem(PublicationItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a publication.
     */
    public PublicationItem(Publication obj)
    {
        super.set(content);
        copyAttributes(obj);
        content.setConfigured(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(PublicationItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(Publication obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public Publication get()
    {
        return content;
    }

    /**
     * Returns the publication type.
     */
    public String getPublicationType()
    {
        return content.getPublicationType();
    }

    /**
     * Sets the publication type.
     */
    public void setPublicationType(String publicationType)
    {
        content.setPublicationType(publicationType);
    }

    /**
     * Sets the publication type.
     */
    public void setPublicationType(PublicationType publicationType)
    {
        setPublicationType(publicationType.value());
    }

    /**
     * Returns the URL of the publication.
     */
    public String getUrl()
    {
        return content.getUrl();
    }

    /**
     * Sets the URL of the publication.
     */
    public void setUrl(String url)
    {
        content.setUrl(url);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return content.hasUrl();
    }
}