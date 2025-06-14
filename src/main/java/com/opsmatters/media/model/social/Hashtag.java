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

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import com.opsmatters.media.model.ManagedEntity;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a note.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Hashtag extends ManagedEntity
{
    private String name = "";
    private String sites = "";
    private HashtagStatus status = HashtagStatus.DISABLED;
    private List<String> siteList = new ArrayList<String>();

    /**
     * Default constructor.
     */
    public Hashtag()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public Hashtag(String name)
    {
        setId(StringUtils.getUUID(null));
        setName(name);
        setCreatedDate(Instant.now());
        setStatus(HashtagStatus.ACTIVE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Hashtag obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setName(obj.getName());
            setSites(obj.getSites());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the name.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the value.
     */
    public String getValue()
    {
        return hasName() ? String.format("#%s", getName()) : "";
    }

    /**
     * Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the name has been set.
     */
    public boolean hasName()
    {
        return name != null && name.length() > 0;
    }

    /**
     * Returns the hashtag sites.
     */
    public String getSites()
    {
        return sites;
    }

    /**
     * Returns the list of hashtag sites.
     */
    public List<String> getSiteList()
    {
        // Return a copy of the list to stop external modification
        return new ArrayList<String>(siteList);
    }

    /**
     * Sets the hashtag sites.
     */
    public void setSites(String sites)
    {
        this.sites = sites;

        siteList.clear();
        for(String site : StringUtils.toList(sites))
            siteList.add(site);
    }

    /**
     * Sets the list of hashtag sites.
     */
    public void setSiteList(List<String> siteList)
    {
        this.siteList.clear();
        for(String site : siteList)
            this.siteList.add(site);
        this.sites = StringUtils.fromList(siteList);
    }

    /**
     * Returns <CODE>true</CODE> if this hashtag is configured for the given site.
     */
    public boolean hasSite(String siteId)
    {
        return siteList.contains(siteId);
    }

    /**
     * Returns <CODE>true</CODE> if this hashtag is configured for the given site.
     */
    public boolean hasSite(Site site)
    {
        return hasSite(site.getId());
    }

    /**
     * Adds the given site to the list of configured sites.
     */
    public void addSite(String siteId)
    {
        if(!siteList.contains(siteId))
        {
            siteList.add(siteId);
            setSites(StringUtils.fromList(siteList));
        }
    }

    /**
     * Adds the given site to the list of configured sites.
     */
    public void addSite(Site site)
    {
        addSite(site.getId());
    }

    /**
     * Returns the hashtag's status.
     */
    public HashtagStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the hashtag is active.
     */
    public boolean isActive()
    {
        return status == HashtagStatus.ACTIVE;
    }

    /**
     * Sets the hashtag's status.
     */
    public void setStatus(HashtagStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the hashtag's status.
     */
    public void setStatus(String status)
    {
        setStatus(HashtagStatus.valueOf(status));
    }
}