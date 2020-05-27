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
package com.opsmatters.media.model.content;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.model.content.Organisation;

/**
 * Class representing a the summary data for an organisation.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationSummary extends OwnedItem
{
    private String code = "";
    private String title = "";
    private OrganisationStatus status = OrganisationStatus.NEW;
    private Map<ContentType, ContentTypeSummary> content = new HashMap<ContentType, ContentTypeSummary>();

    /**
     * Default constructor.
     */
    public OrganisationSummary()
    {
    }

    /**
     * Constructor that takes an organisation.
     */
    public OrganisationSummary(Organisation organisation)
    {
        setId(organisation.getUniqueId());
        setCreatedDate(organisation.getPublishedDate());
        setCode(organisation.getCode());
        if(organisation.isPublished())
            setStatus(OrganisationStatus.ACTIVE);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OrganisationSummary obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setTitle(obj.getTitle());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation code has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the organisation title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the organisation title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation title has been set.
     */
    public boolean hasTitle()
    {
        return title != null && title.length() > 0;
    }

    /**
     * Returns the organisation status.
     */
    public OrganisationStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the organisation status.
     */
    public void setStatus(String status)
    {
        setStatus(OrganisationStatus.valueOf(status));
    }

    /**
     * Sets the organisation status.
     */
    public void setStatus(OrganisationStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the organisation status by the given user.
     */
    public void setStatus(Organisation organisation, String username)
    {
        if(organisation.isPublished())
            setStatus(OrganisationStatus.ACTIVE);
        setUpdatedDate(Instant.now());
        setCreatedBy(username);
    }

    /**
     * Returns the content type summaries.
     */
    public Map<ContentType,ContentTypeSummary> getContent()
    {
        return content;
    }

    /**
     * Returns the number of content type summaries.
     */
    public int getContentSize()
    {
        return content.size();
    }

    /**
     * Returns the summary for the given content type.
     */
    public ContentTypeSummary getContent(ContentType type)
    {
        return content.get(type);
    }

    /**
     * Adds the given content type summary.
     */
    public void addContent(ContentTypeSummary summary)
    {
        content.put(summary.getType(), summary);
    }

    /**
     * Removes the given content type summary.
     */
    public void removeContent(ContentTypeSummary summary)
    {
        content.remove(summary.getType());
    }

    /**
     * Sets the content type summaries.
     */
    public void setContent(Map<ContentType,ContentTypeSummary> summaries)
    {
        content.clear();
        for(ContentTypeSummary summary : summaries.values())
            addContent(summary);
    }

    /**
     * Returns <CODE>true</CODE> if all of the content types have been deployed.
     */
    public boolean isDeployed()
    {
        boolean ret = false;

        if(content.size() > 0)
        {
            ret = true;
            for(ContentTypeSummary type : content.values())
            {
                if(!type.isDeployed())
                {
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }
}