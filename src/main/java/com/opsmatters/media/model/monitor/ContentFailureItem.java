/*
 * Copyright 2024 Gerald Curley
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

import java.time.Instant;

/**
 * Class representing a content failure item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFailureItem extends ContentEventItem<ContentFailure>
{
    private ContentFailure content = new ContentFailure();

    /**
     * Default constructor.
     */
    public ContentFailureItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ContentFailureItem(ContentFailureItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a content failure.
     */
    public ContentFailureItem(ContentFailure obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentFailureItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(ContentFailure obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public ContentFailure get()
    {
        return content;
    }

    /**
     * Returns the date the failure was reviewed.
     */
    public Instant getReviewDate()
    {
        return content.getReviewDate();
    }

    /**
     * Returns the date the failure was reviewed.
     */
    public long getReviewDateMillis()
    {
        return content.getReviewDateMillis();
    }

    /**
     * Sets the date the failure was reviewed.
     */
    public void setReviewDate(Instant reviewDate)
    {
        content.setReviewDate(reviewDate);
    }

    /**
     * Sets the date the failure was reviewed.
     */
    public void setReviewDateMillis(long millis)
    {
        content.setReviewDateMillis(millis);
    }

    /**
     * Returns the failure reason.
     */
    public FailureReason getReason()
    {
        return content.getReason();
    }

    /**
     * Sets the failure reason.
     */
    public void setReason(String reason)
    {
        content.setReason(reason);
    }

    /**
     * Sets the failure reason.
     */
    public void setReason(FailureReason reason)
    {
        content.setReason(reason);
    }

    /**
     * Returns the failure status.
     */
    public FailureStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the failure status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the failure status.
     */
    public void setStatus(FailureStatus status)
    {
        content.setStatus(status);
    }
}