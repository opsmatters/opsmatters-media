/*
 * Copyright 2025 Gerald Curley
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

/**
 * Class representing a managed list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ManagedEntityItem<T extends ManagedEntity> extends BaseEntityItem<T>
{
    private T content;

    /**
     * Default constructor.
     */
    public ManagedEntityItem()
    {
        super.set(content);
    }

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
        super.set(content);
        this.content = content;
    }

    /**
     * Returns the id.
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
}