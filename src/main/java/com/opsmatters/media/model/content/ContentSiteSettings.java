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
import org.json.JSONObject;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing the settings of a content type for an organisation site.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSiteSettings extends BaseEntity
{
    private String siteId = "";
    private String code = "";
    private ContentType type;
    private String templateId = "";
    private String tracking = "";
    private String features = "";
    private String tags = "";
    private boolean promote = false;
    private boolean newsletter = false;
    private boolean featured = false;
    private boolean sponsored = false;
    private int count = -1;
    private boolean deployed = false;

    /**
     * Default constructor.
     */
    public ContentSiteSettings()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentSiteSettings(ContentSiteSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an organisation and content type.
     */
    public ContentSiteSettings(OrganisationSite organisation, List<? extends Content> content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(organisation.getSiteId());
        setCode(organisation.getCode());
        setContent(content);
        setPromoted(true);
    }

    /**
     * Constructor that takes new content.
     */
    public ContentSiteSettings(Content content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(content.getSiteId());
        setCode(content.getCode());
        setType(content.getType());
        setPromoted(true);
        setItemCount(1);
        setDeployed(false);
    }

    /**
     * Constructor that takes an organisation and content type.
     */
    public ContentSiteSettings(OrganisationSite organisation, ContentType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(organisation.getCreatedDate());
        setSiteId(organisation.getSiteId());
        setCode(organisation.getCode());
        setType(type);
        setPromoted(true);
        setItemCount(0);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentSiteSettings obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setSiteId(obj.getSiteId());
            setCode(obj.getCode());
            setType(obj.getType());
            setTemplateId(obj.getTemplateId());
            setTracking(obj.getTracking());
            setFeatures(obj.getFeatures());
            setTags(obj.getTags());
            setPromoted(obj.isPromoted());
            setNewsletter(obj.isNewsletter());
            setFeatured(obj.isFeatured());
            setSponsored(obj.isSponsored());
            setItemCount(obj.getItemCount());
            setDeployed(obj.isDeployed());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(TEMPLATE.value(), getTemplateId());
        ret.putOpt(TRACKING.value(), getTracking());
        ret.putOpt(FEATURES.value(), getFeatures());
        ret.putOpt(TAGS.value(), getTags());
        ret.put(PROMOTE.value(), isPromoted());
        ret.put(NEWSLETTER.value(), isNewsletter());
        ret.put(FEATURED.value(), isFeatured());
        ret.put(SPONSORED.value(), isSponsored());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setTemplateId(obj.optString(TEMPLATE.value()));
        setTracking(obj.optString(TRACKING.value()));
        setFeatures(obj.optString(FEATURES.value()));
        setTags(obj.optString(TAGS.value()));
        setPromoted(obj.optBoolean(PROMOTE.value(), false));
        setNewsletter(obj.optBoolean(NEWSLETTER.value(), false));
        setFeatured(obj.optBoolean(FEATURED.value(), false));
        setSponsored(obj.optBoolean(SPONSORED.value(), false));
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
     * Returns the tracking string.
     */
    public String getTracking()
    {
        return tracking;
    }

    /**
     * Sets the tracking string.
     */
    public void setTracking(String tracking)
    {
        this.tracking = tracking;
    }

    /**
     * Returns <CODE>true</CODE> if the tracking string has been set.
     */
    public boolean hasTracking()
    {
        return tracking != null && tracking.length() > 0;
    }

    /**
     * Returns the features.
     */
    public String getFeatures()
    {
        return features;
    }

    /**
     * Returns the list of features.
     */
    public List<String> getFeaturesList()
    {
        return StringUtils.toList(getFeatures());
    }

    /**
     * Sets the features.
     */
    public void setFeatures(String features)
    {
        this.features = features;
    }

    /**
     * Sets the list of features.
     */
    public void setFeaturesList(List<String> features)
    {
        setFeatures(StringUtils.fromList(features));
    }

    /**
     * Returns <CODE>true</CODE> if the features has been set.
     */
    public boolean hasFeatures()
    {
        return features != null && features.length() > 0;
    }

    /**
     * Returns the tags.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Returns the list of tags.
     */
    public List<String> getTagsList()
    {
        return StringUtils.toList(getTags());
    }

    /**
     * Sets the tags.
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

    /**
     * Sets the list of tags.
     */
    public void setTagsList(List<String> tags)
    {
        setTags(StringUtils.fromList(tags));
    }

    /**
     * Returns <CODE>true</CODE> if the features has been set.
     */
    public boolean hasTags()
    {
        return tags != null && tags.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the content is promoted.
     */
    public boolean isPromoted()
    {
        return promote;
    }

    /**
     * Returns <CODE>true</CODE> if the content is promoted.
     */
    public Boolean getPromoteObject()
    {
        return Boolean.valueOf(isPromoted());
    }

    /**
     * Sets to <CODE>true</CODE> if the content is promoted.
     */
    public void setPromoted(boolean promote)
    {
        this.promote = promote;
    }

    /**
     * Set to <CODE>true</CODE> if the content is promoted.
     */
    public void setPromoteObject(Boolean promote)
    {
        setPromoted(promote != null && promote.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if the newsletter is enabled.
     */
    public boolean isNewsletter()
    {
        return newsletter;
    }

    /**
     * Returns <CODE>true</CODE> if the newsletter is enabled.
     */
    public Boolean getNewsletterObject()
    {
        return Boolean.valueOf(isNewsletter());
    }

    /**
     * Sets to <CODE>true</CODE> if the newsletter is enabled.
     */
    public void setNewsletter(boolean newsletter)
    {
        this.newsletter = newsletter;
    }

    /**
     * Set to <CODE>true</CODE> if the newsletter is enabled.
     */
    public void setNewsletterObject(Boolean newsletter)
    {
        setNewsletter(newsletter != null && newsletter.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if the content is featured.
     */
    public boolean isFeatured()
    {
        return featured;
    }

    /**
     * Returns <CODE>true</CODE> if the content is featured.
     */
    public Boolean getFeaturedObject()
    {
        return Boolean.valueOf(isFeatured());
    }

    /**
     * Sets to <CODE>true</CODE> if the content is featured.
     */
    public void setFeatured(boolean featured)
    {
        this.featured = featured;
    }

    /**
     * Set to <CODE>true</CODE> if the content is featured.
     */
    public void setFeaturedObject(Boolean featured)
    {
        setFeatured(featured != null && featured.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if the content is sponsored.
     */
    public boolean isSponsored()
    {
        return sponsored;
    }

    /**
     * Returns <CODE>true</CODE> if the content is sponsored.
     */
    public Boolean getSponsoredObject()
    {
        return Boolean.valueOf(isSponsored());
    }

    /**
     * Sets to <CODE>true</CODE> if the content is sponsored.
     */
    public void setSponsored(boolean sponsored)
    {
        this.sponsored = sponsored;
    }

    /**
     * Set to <CODE>true</CODE> if the content is sponsored.
     */
    public void setSponsoredObject(Boolean sponsored)
    {
        setSponsored(sponsored != null && sponsored.booleanValue());
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

    /**
     * Set the deployed flag to false.
     */
    public void clearDeployed()
    {
        setDeployed(false);
        setUpdatedDate(Instant.now());
    }

    /**
     * Go through the items to populate the content type.
     */
    public void setContent(List<? extends Content> items)
    {
        int count = 0;
        boolean deployed = true;
        for(Content content : items)
        {
            if(content.isSkipped())
                continue;

            setType(content.getType());
            if(content.getPublishedDate() != null)
            {
                if(getUpdatedDate() == null || content.getPublishedDate().isAfter(getUpdatedDate()))
                    setUpdatedDate(content.getPublishedDate());
            }

            ++count;
            if(!content.isDeployed())
                deployed = false;
        }

        setItemCount(count);
        setDeployed(deployed);
    }
}