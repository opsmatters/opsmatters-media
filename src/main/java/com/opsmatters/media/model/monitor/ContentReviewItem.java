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
 * Class representing a content review item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentReviewItem extends ContentEventItem<ContentReview>
{
    private ContentReview content = new ContentReview();

    /**
     * Default constructor.
     */
    public ContentReviewItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public ContentReviewItem(ContentReviewItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a content review.
     */
    public ContentReviewItem(ContentReview obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentReviewItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(ContentReview obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public ContentReview get()
    {
        return content;
    }

    /**
     * Returns the date the entity was reviewed.
     */
    public Instant getReviewDate()
    {
        return content.getReviewDate();
    }

    /**
     * Returns the date the entity was reviewed.
     */
    public long getReviewDateMillis()
    {
        return content.getReviewDateMillis();
    }

    /**
     * Sets the date the entity was reviewed.
     */
    public void setReviewDate(Instant reviewDate)
    {
        content.setReviewDate(reviewDate);
    }

    /**
     * Sets the date the entity was reviewed.
     */
    public void setReviewDateMillis(long millis)
    {
        content.setReviewDateMillis(millis);
    }

    /**
     * Returns the review reason.
     */
    public ReviewReason getReason()
    {
        return content.getReason();
    }

    /**
     * Sets the review reason.
     */
    public void setReason(String reason)
    {
        content.setReason(reason);
    }

    /**
     * Sets the review reason.
     */
    public void setReason(ReviewReason reason)
    {
        content.setReason(reason);
    }

    /**
     * Returns the review status.
     */
    public ReviewStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the review status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the review status.
     */
    public void setStatus(ReviewStatus status)
    {
        content.setStatus(status);
    }
}