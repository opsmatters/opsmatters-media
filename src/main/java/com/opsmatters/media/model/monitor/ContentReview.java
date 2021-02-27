/*
 * Copyright 2021 Gerald Curley
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
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content monitor review.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentReview extends OwnedItem
{
    private String code = "";
    private String organisation = "";
    private ReviewReason reason;
    private ReviewStatus status;
    private String monitorId = "";
    private String notes = "";

    /**
     * Default constructor.
     */
    public ContentReview()
    {
    }

    /**
     * Constructor that takes a monitor.
     */
    public ContentReview(ContentMonitor monitor, ReviewReason reason)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(monitor.getCode());
        setReason(reason);
        setStatus(ReviewStatus.NEW);
        setMonitorId(monitor.getId());
    }

    /**
     * Copy constructor.
     */
    public ContentReview(ContentReview obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentReview obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation());
            setReason(obj.getReason());
            setStatus(obj.getStatus());
            setMonitorId(obj.getMonitorId());
            setNotes(obj.getNotes());
        }
    }

    /**
     * Returns the monitor organisation.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the monitor organisation.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the monitor organisation name.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the monitor organisation name.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
    }

    /**
     * Returns the review reason.
     */
    public ReviewReason getReason()
    {
        return reason;
    }

    /**
     * Sets the review reason.
     */
    public void setReason(String reason)
    {
        setReason(ReviewReason.valueOf(reason));
    }

    /**
     * Sets the review reason.
     */
    public void setReason(ReviewReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the review status.
     */
    public ReviewStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the review status.
     */
    public void setStatus(String status)
    {
        setStatus(ReviewStatus.valueOf(status));
    }

    /**
     * Sets the review status.
     */
    public void setStatus(ReviewStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the review status by the given user.
     */
    public void setStatus(ReviewStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns the monitor id.
     */
    public String getMonitorId()
    {
        return monitorId;
    }

    /**
     * Sets the monitor id.
     */
    public void setMonitorId(String monitorId)
    {
        this.monitorId = monitorId;
    }

    /**
     * Returns <CODE>true</CODE> if the monitor id has been set.
     */
    public boolean hasMonitorId()
    {
        return monitorId != null && monitorId.length() > 0;
    }

    /**
     * Returns the review notes.
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * Sets the review notes.
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    /**
     * Returns <CODE>true</CODE> if the review notes has been set.
     */
    public boolean hasNotes()
    {
        return notes != null && notes.length() > 0;
    }
}