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
package com.opsmatters.media.model.monitor;

/**
 * Class representing a content change item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChangeItem extends ContentEventItem<ContentChange>
{
    private ContentChange content = new ContentChange();

    /**
     * Default constructor.
     */
    public ContentChangeItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ContentChangeItem(ContentChangeItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a content change.
     */
    public ContentChangeItem(ContentChange obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentChangeItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(ContentChange obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public ContentChange get()
    {
        return content;
    }

    /**
     * Returns the site ids.
     */
    public String getSites()
    {
        return content.getSites();
    }

    /**
     * Sets the site ids.
     */
    public void setSites(String sites)
    {
        content.setSites(sites);
    }

    /**
     * Returns the change status.
     */
    public ChangeStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the change status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the change status.
     */
    public void setStatus(ChangeStatus status)
    {
        content.setStatus(status);
    }
}