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

import java.time.Instant;

/**
 * Class representing a social media post list item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class PostSourceItem<T extends PostSource> extends SocialPostItem<T>
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
     * Returns the posted date.
     */
    public Instant getPostedDate()
    {
        return content.getPostedDate();
    }

    /**
     * Returns the posted date.
     */
    public long getPostedDateMillis()
    {
        return content.getPostedDateMillis();
    }

    /**
     * Sets the posted date.
     */
    public void setPostedDate(Instant postedDate)
    {
        content.setPostedDate(postedDate);
    }

    /**
     * Sets the posted date.
     */
    public void setPostedDateMillis(long millis)
    {
        content.setPostedDateMillis(millis);
    }

    /**
     * Returns the source status.
     */
    public SourceStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the source status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the source status.
     */
    public void setStatus(SourceStatus status)
    {
        content.setStatus(status);
    }
}