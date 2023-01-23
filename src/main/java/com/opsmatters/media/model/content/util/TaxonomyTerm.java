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
package com.opsmatters.media.model.content.util;

import java.time.Instant;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.drupal.DrupalTaxonomyTerm;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.util.TaxonomyTermStatus.*;

/**
 * Class representing a taxonomy term.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyTerm extends BaseEntity
{
    private String siteId = "";
    private TaxonomyType type;
    private String name = "";
    private TaxonomyTermStatus status = NEW;

    /**
     * Default constructor.
     */
    public TaxonomyTerm()
    {
    }

    /**
     * Constructor that takes a site and drupal term.
     */
    public TaxonomyTerm(Site site, DrupalTaxonomyTerm term)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(site.getId());
        setType(TaxonomyType.fromVocabulary(term.getType()));
        setName(term.getName());
        setStatus(term.isPublished() ? ACTIVE : DISABLED);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(TaxonomyTerm obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSiteId(obj.getSiteId());
            setType(obj.getType());
            setName(obj.getName());
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
     * Returns the site id.
     */
    public String getSiteId()
    {
        return siteId;
    }

    /**
     * Sets the site id.
     */
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }

    /**
     * Returns <CODE>true</CODE> if the site id has been set.
     */
    public boolean hasSiteId()
    {
        return siteId != null && siteId.length() > 0;
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
     * Returns the term's type.
     */
    public TaxonomyType getType()
    {
        return type;
    }

    /**
     * Sets the term's type.
     */
    public void setType(TaxonomyType type)
    {
        this.type = type;
    }

    /**
     * Sets the term's type.
     */
    public void setType(String type)
    {
        setType(TaxonomyType.valueOf(type));
    }

    /**
     * Returns the term's status.
     */
    public TaxonomyTermStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the term is active.
     */
    public boolean isActive()
    {
        return status == ACTIVE;
    }

    /**
     * Sets the term's status.
     */
    public void setStatus(TaxonomyTermStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the term's status.
     */
    public void setStatus(String status)
    {
        setStatus(TaxonomyTermStatus.valueOf(status));
    }
}