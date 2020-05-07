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

import java.time.Instant;
import java.util.List;
import com.opsmatters.media.model.BaseItem;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a the summary data for an organisation's content type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentTypeSummary extends BaseItem
{
    private String code = "";
    private ContentType type;
    private String templateId = "";
    private int count = -1;
    private boolean deployed = false;

    /**
     * Default constructor.
     */
    public ContentTypeSummary()
    {
    }

    /**
     * Constructor that takes an organisation summary and content type.
     */
    public ContentTypeSummary(OrganisationSummary summary, List<? extends ContentItem> content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(summary.getCreatedDate());
        setCode(summary.getCode());

        // Go through the items to populate the summary
        int count = 0;
        boolean deployed = true;
        for(ContentItem item : content)
        {
            setType(item.getType());
            if(getUpdatedDate() == null || item.getPublishedDate().isAfter(getUpdatedDate()))
                setUpdatedDate(item.getPublishedDate());
            ++count;
            if(!item.isDeployed())
                deployed = false;
        }

        setItemCount(count);
        setDeployed(deployed);
    }

    /**
     * Constructor that takes a new content item.
     */
    public ContentTypeSummary(ContentItem item)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(item.getCode());
        setType(item.getType());
        setItemCount(1);
        setDeployed(false);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentTypeSummary obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setType(obj.getType());
            setTemplateId(obj.getTemplateId());
            setItemCount(obj.getItemCount());
            setDeployed(obj.isDeployed());
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
     * Returns the content type.
     */
    public ContentType getType()
    {
        return type;
    }

    /**
     * Sets the content type.
     */
    public void setType(String type)
    {
        setType(ContentType.valueOf(type));
    }

    /**
     * Sets the content type.
     */
    public void setType(ContentType type)
    {
        this.type = type;
    }

    /**
     * Returns the social post template id.
     */
    public String getTemplateId()
    {
        return templateId;
    }

    /**
     * Sets the social post template id.
     */
    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    /**
     * Returns <CODE>true</CODE> if the social post template id has been set.
     */
    public boolean hasTemplateId()
    {
        return templateId != null && templateId.length() > 0;
    }

    /**
     * Returns the count of items for the content type.
     */
    public int getItemCount()
    {
        return count;
    }

    /**
     * Sets the count of items for the content type.
     */
    public void setItemCount(int count)
    {
        this.count = count;
    }

    /**
     * Adds an item for the content type.
     */
    public void addItem()
    {
        ++this.count;
        setDeployed(false);
        setUpdatedDate(Instant.now());
    }

    /**
     * Removes an item for the content type.
     */
    public void removeItem(boolean deployed)
    {
        --this.count;
        if(deployed)
            setDeployed(false);
        setUpdatedDate(Instant.now());
    }

    /**
     * Returns <CODE>true</CODE> if the content type has been deployed.
     */
    public boolean isDeployed()
    {
        return deployed;
    }

    /**
     * Sets to <CODE>true</CODE> if the content type has been deployed.
     */
    public void setDeployed(boolean deployed)
    {
        this.deployed = deployed;
    }
}