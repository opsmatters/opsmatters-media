/*
 * Copyright 2022 Gerald Curley
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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.FieldName;
import com.opsmatters.media.model.content.FieldSource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSiteSettings;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing an organisation site.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationSite extends BaseEntity implements FieldSource
{
    private String siteId = "";
    private String code = "";
    private String organisation = ""; // Only for search
    private String hashtags = "";
    private boolean sponsor = false;
    private boolean listing = false;
    private boolean social = false;
    private OrganisationStatus status = OrganisationStatus.NEW;
    private ArchiveReason reason = ArchiveReason.NONE;
    private Map<ContentType,ContentSiteSettings> settings = new HashMap<ContentType,ContentSiteSettings>();


    /**
     * Default constructor.
     */
    public OrganisationSite()
    {
    }

    /**
     * Copy constructor.
     */
    public OrganisationSite(OrganisationSite obj)
    {
        copyAttributes(obj);
    }

    /**
     * Returns the code of the organisation.
     */
    public String toString()
    {
        return getCode();
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OrganisationSite obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSiteId(obj.getSiteId());
            setCode(obj.getCode());
            setOrganisation(obj.getOrganisation());
            setHashtags(new String(obj.getHashtags() != null ? obj.getHashtags() : ""));
            setSponsor(obj.isSponsor());
            setListing(obj.hasListing());
            setSocial(obj.hasSocial());
            setStatus(obj.getStatus());
            setReason(obj.getReason());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.put(SOCIAL.value(), hasSocial());
        ret.putOpt(HASHTAGS.value(), getHashtags());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setSocial(obj.optBoolean(SOCIAL.value(), false));
        setHashtags(obj.optString(HASHTAGS.value()));
    }

    /**
     * Returns the fields required by other objects.
     */
    public FieldMap getFields()
    {
        FieldMap ret = new FieldMap();

        ret.put(SPONSOR, isSponsor() ? "1" : "0");
        ret.put(LISTING, hasListing() ? "1" : "0");
        ret.put(SOCIAL, hasSocial() ? "1" : "0");
        ret.put(HASHTAGS, getHashtags());

        return ret;
    }

    /**
     * Returns a new organisation site with defaults.
     */
    public static OrganisationSite getDefault(Site site)
    {
        OrganisationSite organisation = new OrganisationSite();

        organisation.setId(StringUtils.getUUID(null));
        organisation.setSiteId(site.getId());
        organisation.setCode(Organisation.DEFAULT_CODE);
        organisation.setCreatedDate(Instant.now());
        organisation.setSocial(true);

        return organisation;
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

        Organisation organisation = Organisations.get(code);
        setOrganisation(organisation != null ? organisation.getName() : "");
    }

    /**
     * Returns <CODE>true</CODE> if the organisation code has been set.
     */
    public boolean hasCode()
    {
        return code != null && code.length() > 0;
    }

    /**
     * Returns the organisation name.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the organisation name.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation name has been set.
     */
    public boolean hasOrganisation()
    {
        return organisation != null && organisation.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation is a sponsor.
     */
    public boolean isSponsor()
    {
        return sponsor;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation is a sponsor.
     */
    public Boolean getSponsorObject()
    {
        return Boolean.valueOf(isSponsor());
    }

    /**
     * Set to <CODE>true</CODE> if this organisation is a sponsor.
     */
    public void setSponsor(boolean sponsor)
    {
        this.sponsor = sponsor;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation is a sponsor.
     */
    public void setSponsorObject(Boolean sponsor)
    {
        setSponsor(sponsor != null && sponsor.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has a listing.
     */
    public boolean hasListing()
    {
        return listing;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has a listing.
     */
    public Boolean getListingObject()
    {
        return Boolean.valueOf(hasListing());
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a listing.
     */
    public void setListing(boolean listing)
    {
        this.listing = listing;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a listing.
     */
    public void setListingObject(Boolean listing)
    {
        setListing(listing != null && listing.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has social posts.
     */
    public boolean hasSocial()
    {
        return social;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has social posts.
     */
    public Boolean getSocialObject()
    {
        return Boolean.valueOf(hasSocial());
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has social posts.
     */
    public void setSocial(boolean social)
    {
        this.social = social;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has social posts.
     */
    public void setSocialObject(Boolean social)
    {
        setSocial(social != null && social.booleanValue());
    }

    /**
     * Returns the organisation's social hashtags.
     */
    public String getHashtags()
    {
        return hashtags;
    }

    /**
     * Sets the organisation's social hashtags.
     */
    public void setHashtags(String hashtags)
    {
        this.hashtags = hashtags;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has social hashtags.
     */
    public boolean hasHashtags()
    {
        return hashtags != null && hashtags.length() > 0;
    }

    /**
     * Returns the organisation status.
     */
    public OrganisationStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation is ACTIVE.
     */
    public boolean isActive()
    {
        return status == OrganisationStatus.ACTIVE;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation is REVIEW.
     */
    public boolean isReview()
    {
        return status == OrganisationStatus.REVIEW;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation is ARCHIVED.
     */
    public boolean isArchived()
    {
        return status == OrganisationStatus.ARCHIVED;
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
     * Returns the archive reason.
     */
    public ArchiveReason getReason()
    {
        return reason;
    }

    /**
     * Sets the archive reason.
     */
    public void setReason(String reason)
    {
        setReason(ArchiveReason.valueOf(reason));
    }

    /**
     * Sets the archive reason.
     */
    public void setReason(ArchiveReason reason)
    {
        this.reason = reason;
    }

    /**
     * Returns the content settings.
     */
    public Map<ContentType,ContentSiteSettings> getSettings()
    {
        return settings;
    }

    /**
     * Returns a list of the content settings.
     */
    public List<ContentSiteSettings> getSettingsList()
    {
        return new ArrayList<ContentSiteSettings>(settings.values());
    }

    /**
     * Returns <CODE>true</CODE>if the organisation has content settings.
     */
    public boolean hasSettings()
    {
        return settings != null && settings.size() > 0;
    }

    /**
     * Returns the number of content settings.
     */
    public int numSettings()
    {
        return settings.size();
    }

    /**
     * Returns the settings for the given content type.
     */
    public ContentSiteSettings getSettings(ContentType type)
    {
        return settings.get(type);
    }

    /**
     * Adds the given content settings.
     */
    public void setSettings(ContentSiteSettings settings)
    {
        this.settings.put(settings.getType(), settings);
    }

    /**
     * Removes the given content settings.
     */
    public void removeSettings(ContentSiteSettings settings)
    {
        this.settings.remove(settings.getType());
    }

    /**
     * Sets the content settings.
     */
    public void setSettings(Map<ContentType,ContentSiteSettings> map)
    {
        this.settings.clear();
        for(ContentSiteSettings settings : map.values())
            setSettings(settings);
    }

    /**
     * Clears the content settings.
     */
    public void clearSettings()
    {
        settings.clear();
    }

    /**
     * Returns <CODE>true</CODE> if all of the content settings have been deployed.
     */
    public boolean isDeployed()
    {
        boolean ret = false;

        if(getSettings().size() > 0)
        {
            ret = true;
            for(ContentSiteSettings settings : getSettings().values())
            {
                if(settings.getItemCount() > 0 && !settings.isDeployed())
                {
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }
}