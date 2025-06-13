/*
 * Copyright 2020 Gerald Curley
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

import java.util.List;
import java.time.Instant;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.SnapshotDiff;

/**
 * Class representing a content monitor change.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChange extends ContentEvent
{
    private ChangeStatus status;
    private String snapshotBefore = "";
    private String snapshotAfter = "";
    private String snapshotDiff = "";
    private long executionTime = -1L;
    private int difference = 0;
    private String sites = "";

    /**
     * Default constructor.
     */
    public ContentChange()
    {
    }

    /**
     * Constructor that takes a monitor, a snapshot and the difference.
     */
    public ContentChange(ContentMonitor monitor, ContentSnapshot snapshot, ContentSnapshot diff)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(monitor.getCode());
        setStatus(ChangeStatus.NEW);
        setSnapshotBefore(monitor.getSnapshot());
        setSnapshotAfter(snapshot);
        if(diff != null)
            setSnapshotDiff(diff);
        setMonitorId(monitor.getId());
        setExecutionTime(monitor.getExecutionTime());
        setDifference(SnapshotDiff.getDifferencePercent(getSnapshotBefore(), getSnapshotAfter()));
    }

    /**
     * Constructor that takes a monitor and a snapshot.
     */
    public ContentChange(ContentMonitor monitor, ContentSnapshot snapshot)
    {
        this(monitor, snapshot, null);
    }

    /**
     * Copy constructor.
     */
    public ContentChange(ContentChange obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentChange obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setStatus(obj.getStatus());
            setSnapshotBefore(obj.getSnapshotBefore());
            setSnapshotAfter(obj.getSnapshotAfter());
            setSnapshotDiff(obj.getSnapshotDiff());
            setExecutionTime(obj.getExecutionTime());
            setDifference(obj.getDifference());
            setSites(obj.getSites());
        }
    }

    /**
     * Returns the type of the event.
     */
    @Override
    public ContentEventType getType()
    {
        return ContentEventType.CHANGE;
    }

    /**
     * Returns the change status.
     */
    public ChangeStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the change status.
     */
    public void setStatus(String status)
    {
        setStatus(ChangeStatus.valueOf(status));
    }

    /**
     * Sets the change status.
     */
    public void setStatus(ChangeStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the change status by the given user.
     */
    public void setStatus(ChangeStatus status, String username)
    {
        setStatus(status);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns <CODE>true</CODE> if this change is NEW.
     */
    public boolean isNew()
    {
        return getStatus() == ChangeStatus.NEW;
    }

    /**
     * Returns <CODE>true</CODE> if this change has been SKIPPED.
     */
    public boolean isSkipped()
    {
        return getStatus() == ChangeStatus.SKIPPED;
    }

    /**
     * Returns <CODE>true</CODE> if this change has been RESOLVED.
     */
    public boolean isResolved()
    {
        return getStatus() == ChangeStatus.RESOLVED;
    }

    /**
     * Returns the monitor snapshot before the change.
     */
    public String getSnapshotBefore()
    {
        return snapshotBefore;
    }

    /**
     * Sets the monitor snapshot before the change.
     */
    public void setSnapshotBefore(String snapshotBefore)
    {
        this.snapshotBefore = snapshotBefore;
    }

    /**
     * Returns the monitor snapshot after the change.
     */
    public String getSnapshotAfter()
    {
        return snapshotAfter;
    }

    /**
     * Sets the monitor snapshot after the change.
     */
    public void setSnapshotAfter(String snapshotAfter)
    {
        this.snapshotAfter = snapshotAfter;
    }

    /**
     * Sets the monitor snapshot after the change.
     */
    public void setSnapshotAfter(ContentSnapshot snapshotAfter)
    {
        setSnapshotAfter(snapshotAfter.toString());
    }

    /**
     * Returns the snapshot difference.
     */
    public String getSnapshotDiff()
    {
        return snapshotDiff;
    }

    /**
     * Sets the snapshot difference.
     */
    public void setSnapshotDiff(String snapshotDiff)
    {
        this.snapshotDiff = snapshotDiff;
    }

    /**
     * Sets the snapshot difference.
     */
    public void setSnapshotDiff(ContentSnapshot snapshotDiff)
    {
        setSnapshotDiff(snapshotDiff.toString());
    }

    /**
     * Returns the time taken for the monitor execution.
     */
    public long getExecutionTime()
    {
        return executionTime;
    }

    /**
     * Sets the time taken for the monitor execution.
     */
    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    /**
     * Returns the % difference between the snapshots.
     */
    public int getDifference()
    {
        return difference;
    }

    /**
     * Sets the % difference between the snapshots.
     */
    public void setDifference(int difference)
    {
        this.difference = difference;
    }

    /**
     * Returns the site ids.
     */
    public String getSites()
    {
        return sites;
    }

    /**
     * Sets the site ids.
     */
    public void setSites(String sites)
    {
        this.sites = sites;
    }

    /**
     * Clears the site ids.
     */
    public void clearSites()
    {
        setSites("");
    }

    /**
     * Returns <CODE>true</CODE> if the site ids have been set.
     */
    public boolean hasSites()
    {
        return sites != null && sites.length() > 0;
    }

    /**
     * Returns the number of site ids.
     */
    public int numSites()
    {
        return StringUtils.toList(getSites()).size();
    }

    /**
     * Adds a site id.
     */
    public void addSite(String siteId)
    {
        List<String> list = StringUtils.toList(getSites());
        if(!list.contains(siteId))
            list.add(siteId);
        setSites(StringUtils.fromList(list));
    }

    /**
     * Sets the site ids.
     */
    public void setSites(List<OrganisationSite> organisations)
    {
        clearSites();
        for(OrganisationSite organisation : organisations)
            addSite(organisation.getSiteId());
    }
}