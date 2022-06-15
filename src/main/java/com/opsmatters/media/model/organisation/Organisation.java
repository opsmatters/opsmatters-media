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
package com.opsmatters.media.model.organisation;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import org.json.JSONObject;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.config.content.FieldSource;
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing an organisation.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Organisation extends OwnedItem implements FieldSource
{
    private String siteId = "";
    private String code = "";
    private String name = "";
    private String website = "";
    private String email = "";
    private String handle = "";
    private String hashtag = "";
    private String hashtags = "";
    private String tracking = "";
    private boolean sponsor = false;
    private boolean listing = false;
    private boolean social = false;
    private String imagePrefix = "";
    private String imageText = "";
    private OrganisationStatus status = OrganisationStatus.NEW;
    private ArchiveReason reason = ArchiveReason.NONE;
    private Map<ContentType, OrganisationContentType> contentTypes = new HashMap<ContentType, OrganisationContentType>();


    /**
     * Default constructor.
     */
    public Organisation()
    {
    }

    /**
     * Copy constructor.
     */
    public Organisation(Organisation obj)
    {
        copyAttributes(obj);
    }

    /**
     * Returns the name of the organisation.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Organisation obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSiteId(obj.getSiteId());
            setCode(obj.getCode());
            setName(obj.getName());
            setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
            setEmail(new String(obj.getEmail() != null ? obj.getEmail() : ""));
            setHandle(new String(obj.getHandle() != null ? obj.getHandle() : ""));
            setHashtag(new String(obj.getHashtag() != null ? obj.getHashtag() : ""));
            setHashtags(new String(obj.getHashtags() != null ? obj.getHashtags() : ""));
            setTracking(new String(obj.getTracking() != null ? obj.getTracking() : ""));
            setSponsor(obj.isSponsor());
            setListing(obj.hasListing());
            setSocial(obj.hasSocial());
            setImagePrefix(new String(obj.getImagePrefix() != null ? obj.getImagePrefix() : ""));
            setImageText(new String(obj.getImageText() != null ? obj.getImageText() : ""));
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

        ret.put(Fields.SPONSOR, isSponsor());
        ret.put(Fields.LISTING, hasListing());
        ret.put(Fields.SOCIAL, hasSocial());
        ret.putOpt(Fields.WEBSITE, getWebsite());
        ret.putOpt(Fields.EMAIL, getEmail());
        ret.putOpt(Fields.HASHTAG, getHashtag());
        ret.putOpt(Fields.HASHTAGS, getHashtags());
        ret.putOpt(Fields.TRACKING, getTracking());
        ret.putOpt(Fields.HANDLE, getHandle());
        ret.putOpt(Fields.IMAGE_PREFIX, getImagePrefix());
        ret.putOpt(Fields.IMAGE_TEXT, getImageText());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setSponsor(obj.optBoolean(Fields.SPONSOR, false));
        setListing(obj.optBoolean(Fields.LISTING, false));
        setSocial(obj.optBoolean(Fields.SOCIAL, false));
        setWebsite(obj.optString(Fields.WEBSITE));
        setEmail(obj.optString(Fields.EMAIL));
        setHashtag(obj.optString(Fields.HASHTAG));
        setHashtags(obj.optString(Fields.HASHTAGS));
        setTracking(obj.optString(Fields.TRACKING));
        setHandle(obj.optString(Fields.HANDLE));
        setImagePrefix(obj.optString(Fields.IMAGE_PREFIX));
        setImageText(obj.optString(Fields.IMAGE_TEXT));
    }

    /**
     * Returns the fields required by other objects.
     */
    public Fields getFields()
    {
        Fields ret = new Fields();

        ret.put(Fields.SPONSOR, isSponsor() ? "1" : "0");
        ret.put(Fields.LISTING, hasListing() ? "1" : "0");
        ret.put(Fields.SOCIAL, hasSocial() ? "1" : "0");
        ret.put(Fields.WEBSITE, getWebsite());
        ret.put(Fields.EMAIL, getEmail());
        ret.put(Fields.HANDLE, getHandle());
        ret.put(Fields.HASHTAG, getHashtag());
        ret.put(Fields.HASHTAGS, getHashtags());
        ret.put(Fields.TRACKING, getTracking());
        ret.put(Fields.IMAGE_TEXT, getImageText());

        return ret;
    }

    /**
     * Returns a new organisation with defaults.
     */
    public static Organisation getDefault(Site site)
    {
        Organisation organisation = new Organisation();

        organisation.setId(StringUtils.getUUID(null));
        organisation.setSiteId(site.getId());
        organisation.setCode("TBD");
        organisation.setName("New Organisation");
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
    public String getName()
    {
        return name;
    }

    /**
     * Sets the organisation name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns <CODE>true</CODE> if the organisation name has been set.
     */
    public boolean hasName()
    {
        return name != null && name.length() > 0;
    }

    /**
     * Returns the URL for the organisation.
     */
    public String getUrl(String basePath)
    {
        return String.format("%s/organisations/%s", basePath, StringUtils.getNormalisedName(getName()));
    }

    /**
     * Returns the organisation's website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the organisation's website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a website address.
     */
    public boolean hasWebsite()
    {
        return website != null && website.length() > 0;
    }

    /**
     * Returns the organisation's email.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the organisation's email.
     */
    public void setEmail(String email)
    {
        this.email = email;
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
     * Returns the organisation's social handle.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the organisation's social handle.
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Returns the organisation's social hashtag.
     */
    public String getHashtag()
    {
        return hashtag;
    }

    /**
     * Sets the organisation's social hashtag.
     */
    public void setHashtag(String hashtag)
    {
        this.hashtag = hashtag;
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
     * Returns the organisation's tracking string.
     */
    public String getTracking()
    {
        return tracking;
    }

    /**
     * Sets the organisation's tracking string.
     */
    public void setTracking(String tracking)
    {
        this.tracking = tracking;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a tracking string.
     */
    public boolean hasTracking()
    {
        return tracking != null && tracking.length() > 0;
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
     * Returns the image prefix.
     */
    public String getImagePrefix()
    {
        return imagePrefix;
    }

    /**
     * Sets the image prefix.
     */
    public void setImagePrefix(String imagePrefix)
    {
        this.imagePrefix = imagePrefix;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has an image prefix.
     */
    public boolean hasImagePrefix()
    {
        return imagePrefix != null && imagePrefix.length() > 0;
    }

    /**
     * Returns the organisation's image text.
     */
    public String getImageText()
    {
        return imageText;
    }

    /**
     * Sets the organisation's image text.
     */
    public void setImageText(String imageText)
    {
        this.imageText = imageText;
    }

    /**
     * Returns the content types.
     */
    public Map<ContentType,OrganisationContentType> getContentTypes()
    {
        return contentTypes;
    }

    /**
     * Returns <CODE>true</CODE>if the organisation has content types.
     */
    public boolean hasContentTypes()
    {
        return contentTypes != null && contentTypes.size() > 0;
    }

    /**
     * Returns the number of content types.
     */
    public int numContentTypes()
    {
        return contentTypes.size();
    }

    /**
     * Returns the data for the given content type.
     */
    public OrganisationContentType getContentType(ContentType type)
    {
        return contentTypes.get(type);
    }

    /**
     * Adds the given content type.
     */
    public void setContentType(OrganisationContentType type)
    {
        contentTypes.put(type.getType(), type);
    }

    /**
     * Removes the given content type.
     */
    public void removeContentType(OrganisationContentType type)
    {
        contentTypes.remove(type.getType());
    }

    /**
     * Sets the content types.
     */
    public void setContentTypes(Map<ContentType,OrganisationContentType> types)
    {
        contentTypes.clear();
        for(OrganisationContentType type : types.values())
            setContentType(type);
    }

    /**
     * Clears the content types.
     */
    public void clearContentTypes()
    {
        contentTypes.clear();
    }

    /**
     * Returns <CODE>true</CODE> if all of the content types have been deployed.
     */
    public boolean isDeployed()
    {
        boolean ret = false;

        if(contentTypes.size() > 0)
        {
            ret = true;
            for(OrganisationContentType type : contentTypes.values())
            {
                if(type.getItemCount() > 0 && !type.isDeployed())
                {
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }
}