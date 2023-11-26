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
package com.opsmatters.media.model.content;

/**
 * Class representing an resource list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ResourceItem<T extends Resource> extends ContentItem<T>
{
    private T content;

    /**
     * Returns the content object.
     */
    public T get()
    {
        return content;
    }

    /**
     * Returns the content object.
     */
    protected void set(T content)
    {
        super.set(content);
        this.content = content;
    }

    /**
     * Returns <CODE>true</CODE> if this content should be promoted.
     */
    public boolean isPromoted()
    {
        return content.isPromoted();
    }

    /**
     * Set to <CODE>true</CODE> if this content should be promoted.
     */
    public void setPromoted(boolean promote)
    {
        content.setPromoted(promote);
    }
}