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
package com.opsmatters.media.model.organisation;

import java.util.Map;
import com.opsmatters.media.model.BaseEntityItem;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSettings;

/**
 * Class representing an organisation site item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationSiteItem extends BaseEntityItem<OrganisationSite>
{
    private OrganisationSite content = new OrganisationSite();

    /**
     * Default constructor.
     */
    public OrganisationSiteItem()
    {
        super.set(content);
    }

    /**
     * Copy constructor.
     */
    public OrganisationSiteItem(OrganisationSiteItem obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an organisation.
     */
    public OrganisationSiteItem(OrganisationSite obj)
    {
        super.set(content);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OrganisationSiteItem obj)
    {
        copyAttributes(obj.get());
    }

    /**
     * Copies the attributes of the given object.
     */
    @Override
    public void copyAttributes(OrganisationSite obj)
    {
        content.copyAttributes(obj);
    }

    /**
     * Returns the content object.
     */
    public OrganisationSite get()
    {
        return content;
    }

    /**
     * Returns the site id.
     */
    public String getSiteId()
    {
        return content.getSiteId();
    }

    /**
     * Sets the site id.
     */
    public void setSiteId(String siteId)
    {
        content.setSiteId(siteId);
    }

    /**
     * Returns the organisation code.
     */
    public String getCode()
    {
        return content.getCode();
    }

    /**
     * Sets the organisation code.
     */
    public void setCode(String code)
    {
        content.setCode(code);
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return content.getOrganisation();
    }

    /**
     * Sets the organisation name.
     */
    public void setOrganisation(String organisation)
    {
        content.setOrganisation(organisation);
    }

    /**
     * Returns <CODE>true</CODE> if the organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return content.hasOrganisation();
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has a listing.
     */
    public boolean hasListing()
    {
        return content.hasListing();
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a listing.
     */
    public void setListing(boolean listing)
    {
        content.setListing(listing);
    }

    /**
     * Returns <CODE>true</CODE> if this organisation is a sponsor.
     */
    public boolean isSponsor()
    {
        return content.isSponsor();
    }

    /**
     * Set to <CODE>true</CODE> if this organisation is a sponsor.
     */
    public void setSponsor(boolean sponsor)
    {
        content.setSponsor(sponsor);
    }

    /**
     * Returns the content settings.
     */
    public Map<ContentType,ContentSettings> getContentSettings()
    {
        return content.getContentSettings();
    }

    /**
     * Sets the content settings.
     */
    public void setContentSettings(Map<ContentType,ContentSettings> settings)
    {
        content.setContentSettings(settings);
    }

    /**
     * Adds the given content settings.
     */
    public void setContentSettings(ContentSettings settings)
    {
        content.setContentSettings(settings);
    }

    /**
     * Returns <CODE>true</CODE> if all of the content types have been deployed.
     */
    public boolean isDeployed()
    {
        return content.isDeployed();
    }

    /**
     * Returns the organisation status.
     */
    public OrganisationStatus getStatus()
    {
        return content.getStatus();
    }

    /**
     * Sets the organisation status.
     */
    public void setStatus(String status)
    {
        content.setStatus(status);
    }

    /**
     * Sets the organisation status.
     */
    public void setStatus(OrganisationStatus status)
    {
        content.setStatus(status);
    }

    /**
     * Returns the archive reason.
     */
    public ArchiveReason getReason()
    {
        return content.getReason();
    }

    /**
     * Sets the archive reason.
     */
    public void setReason(String reason)
    {
        content.setReason(reason);
    }

    /**
     * Sets the archive reason.
     */
    public void setReason(ArchiveReason reason)
    {
        content.setReason(reason);
    }
}