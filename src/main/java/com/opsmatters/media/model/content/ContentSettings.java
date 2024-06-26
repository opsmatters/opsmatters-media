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
 * Class representing the settings of a content type for an organisation.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSettings extends BaseEntity
{
    private String siteId = "";
    private String code = "";
    private ContentType type;
    private String templateId = "";
    private String tracking = "";
    private String features = "";
    private String tags = "";
    private String technologies = "";
    private boolean newsletter = false;
    private String imageRejects = "";
    private String summaryRejects = "";
    private int count = -1;
    private boolean deployed = false;

    /**
     * Default constructor.
     */
    public ContentSettings()
    {
    }

    /**
     * Copy constructor.
     */
    public ContentSettings(ContentSettings obj)
    {
        copyAttributes(obj);
    }

    /**
     * Constructor that takes an organisation and content type.
     */
    public ContentSettings(OrganisationSite organisation, List<? extends Content> content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(organisation.getCreatedDate());
        setSiteId(organisation.getSiteId());
        setCode(organisation.getCode());
        setContent(content);
    }

    /**
     * Constructor that takes new content.
     */
    public ContentSettings(Content content)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(content.getSiteId());
        setCode(content.getCode());
        setType(content.getType());
        setItemCount(1);
        setDeployed(false);
    }

    /**
     * Constructor that takes an organisation and content type.
     */
    public ContentSettings(OrganisationSite organisation, ContentType type)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(organisation.getCreatedDate());
        setSiteId(organisation.getSiteId());
        setCode(organisation.getCode());
        setType(type);
        setItemCount(0);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentSettings obj)
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
            setTechnologies(obj.getTechnologies());
            setNewsletter(obj.isNewsletter());
            setImageRejects(obj.getImageRejects());
            setSummaryRejects(obj.getSummaryRejects());
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
        ret.putOpt(TECHNOLOGIES.value(), getTechnologies());
        ret.put(NEWSLETTER.value(), isNewsletter());
        ret.putOpt(IMAGE_REJECTS.value(), getImageRejects());
        ret.putOpt(SUMMARY_REJECTS.value(), getSummaryRejects());

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
        setTechnologies(obj.optString(TECHNOLOGIES.value()));
        setNewsletter(obj.optBoolean(NEWSLETTER.value(), false));
        setImageRejects(obj.optString(IMAGE_REJECTS.value()));
        setSummaryRejects(obj.optString(SUMMARY_REJECTS.value()));
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
     * Returns the technologies.
     */
    public String getTechnologies()
    {
        return technologies;
    }

    /**
     * Returns the list of technologies.
     */
    public List<String> getTechnologiesList()
    {
        return StringUtils.toList(getTechnologies());
    }

    /**
     * Sets the technologies.
     */
    public void setTechnologies(String technologies)
    {
        this.technologies = technologies;
    }

    /**
     * Sets the list of technologies.
     */
    public void setTechnologiesList(List<String> technologies)
    {
        setTechnologies(StringUtils.fromList(technologies));
    }

    /**
     * Returns <CODE>true</CODE> if the technologies has been set.
     */
    public boolean hasTechnologies()
    {
        return technologies != null && technologies.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the newsletter is enabled.
     */
    public boolean isNewsletter()
    {
        return newsletter;
    }

    /**
     * Returns <CODE>true</CODE> if this content should appear in the newsletter.
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
     * Set to <CODE>true</CODE> if this content should appear in the newsletter.
     */
    public void setNewsletterObject(Boolean newsletter)
    {
        setNewsletter(newsletter != null && newsletter.booleanValue());
    }

    /**
     * Returns the list of image filenames to reject.
     */
    public String getImageRejects()
    {
        return imageRejects;
    }

    /**
     * Sets the list of image filenames to reject.
     */
    public void setImageRejects(String imageRejects)
    {
        this.imageRejects = imageRejects;
    }

    /**
     * Returns <CODE>true</CODE> if the reject image filenames have been set.
     */
    public boolean hasImageRejects()
    {
        return imageRejects != null && imageRejects.length() > 0;
    }

    /**
     * Returns the list of image filenames to reject.
     */
    public List<String> getImageRejectsList()
    {
        return StringUtils.toList(imageRejects, "\\n");
    }

    /**
     * Returns the list of summary phrases to reject.
     */
    public String getSummaryRejects()
    {
        return summaryRejects;
    }

    /**
     * Sets the list of summary phrases to reject.
     */
    public void setSummaryRejects(String summaryRejects)
    {
        this.summaryRejects = summaryRejects;
    }

    /**
     * Returns <CODE>true</CODE> if the reject summary phrases have been set.
     */
    public boolean hasSummaryRejects()
    {
        return summaryRejects != null && summaryRejects.length() > 0;
    }

    /**
     * Returns the list of summary phrases to reject.
     */
    public List<String> getSummaryRejectsList()
    {
        return StringUtils.toList(summaryRejects, "\\n");
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