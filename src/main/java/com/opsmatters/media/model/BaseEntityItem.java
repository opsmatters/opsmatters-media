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
package com.opsmatters.media.model;

import java.time.Instant;

/**
 * Class representing a list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class BaseEntityItem<T extends BaseEntity> implements java.io.Serializable
{
    private T content;

    /**
     * Copies the attributes of the given object.
     */
    public abstract void copyAttributes(T obj);

    /**
     * Returns the content object.
     */
    public T get()
    {
        return content;
    }

    /**
     * Sets the object.
     */
    protected void set(T content)
    {
        this.content = content;
    }

    /**
     * Returns the title.
     */
    public String toString()
    {
        return getId();
    }

    /**
     * Returns the entity id.
     */
    public String getId()
    {
        return content.getId();
    }

    /**
     * Sets the entity id.
     */
    public void setId(String id)
    {
        content.setId(id);
    }

    /**
     * Returns the date the entity was created.
     */
    public Instant getCreatedDate()
    {
        return content.getCreatedDate();
    }

    /**
     * Returns the date the entity was created.
     */
    public long getCreatedDateMillis()
    {
        return content.getCreatedDateMillis();
    }

    /**
     * Sets the date the entity was created.
     */
    public void setCreatedDate(Instant createdDate)
    {
        content.setCreatedDate(createdDate);
    }

    /**
     * Sets the date the entity was created.
     */
    public void setCreatedDateMillis(long millis)
    {
        content.setCreatedDateMillis(millis);
    }

    /**
     * Returns the date the entity was last updated.
     */
    public Instant getUpdatedDate()
    {
        return content.getUpdatedDate();
    }

    /**
     * Returns the date the entity was last updated.
     */
    public long getUpdatedDateMillis()
    {
        return content.getUpdatedDateMillis();
    }

    /**
     * Sets the date the entity was last updated.
     */
    public void setUpdatedDate(Instant updatedDate)
    {
        content.setUpdatedDate(updatedDate);
    }

    /**
     * Sets the date the entity was last updated.
     */
    public void setUpdatedDateMillis(long millis)
    {
        content.setUpdatedDateMillis(millis);
    }

    /**
     * Returns the date the entity was created or last updated.
     */
    public Instant getDate()
    {
        return content.getDate();
    }

    /**
     * Returns the date the entity was created or last updated.
     */
    public long getDateMillis()
    {
        return content.getDateMillis();
    }
}