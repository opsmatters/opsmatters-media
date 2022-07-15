/*
 * Copyright 2019 Gerald Curley
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
import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.config.content.ContentConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.organisation.OrganisationContentType;
import com.opsmatters.media.file.CommonFiles;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing a content item.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentItem implements java.io.Serializable
{
    public static final String EMPTY = "-";

    private ContentSummary details;
    private boolean detailsSet = false;

    private String uuid = "";
    private String siteId = "";
    private int id = -1;
    private String code = "";
    private boolean published = false;
    private String tracking = "";
    private String createdBy = "";
    private boolean social = false;
    private String canonicalUrl = "";
    private ContentStatus status = ContentStatus.NEW;
    private String otherSites = "";

    /**
     * Default constructor.
     */
    public ContentItem()
    {
    }

    /**
     * Initialise the content item.
     */
    public void init()
    {
        if(!hasUniqueId())
            setUuid();
    }

    /**
     * Resets the content UUID and ID.
     */
    public void reinit()
    {
        setUuid(null);
        setUuid();
        clearId();
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentItem obj)
    {
        setUuid(new String(obj.getUuid() != null ? obj.getUuid() : ""));
        setSiteId(obj.getSiteId());
        setCode(new String(obj.getCode() != null ? obj.getCode() : ""));
        setId(obj.getId());
        setPublished(obj.isPublished());
        setTracking(new String(obj.getTracking() != null ? obj.getTracking() : ""));
        setCreatedBy(new String(obj.getCreatedBy() != null ? obj.getCreatedBy() : ""));
        setSocial(obj.hasSocial());
        setCanonicalUrl(new String(obj.getCanonicalUrl() != null ? obj.getCanonicalUrl() : ""));
        setStatus(obj.getStatus());
        setOtherSites(obj.getOtherSites());
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        setCode(obj.optString(Fields.CODE));
        setId(obj.optInt(Fields.ID));
        setUuid(obj.optString(Fields.UUID));
        setPublishedDateMillis(obj.optLong(Fields.PUBLISHED_DATE));
        setTitle(obj.optString(Fields.TITLE));
        setSummary(EmojiParser.parseToUnicode(obj.optString(Fields.SUMMARY)));
        setPublished(obj.optBoolean(Fields.PUBLISHED, false));
        setTracking(obj.optString(Fields.TRACKING));
        setCreatedBy(obj.optString(Fields.CREATED_BY));
        setSocial(obj.optBoolean(Fields.SOCIAL, false));
        setStatus(obj.optString(Fields.STATUS));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(Fields.CODE, getCode());
        ret.put(Fields.ID, getId());
        ret.put(Fields.UUID, getUuid());
        ret.put(Fields.PUBLISHED_DATE, getPublishedDateMillis());
        ret.putOpt(Fields.TITLE, getTitle());
        if(getSummary() != null && getSummary().length() > 0)
            ret.putOpt(Fields.SUMMARY, EmojiParser.parseToAliases(getSummary()));
        ret.put(Fields.PUBLISHED, isPublished());
        ret.put(Fields.TRACKING, getTracking());
        ret.put(Fields.CREATED_BY, getCreatedBy());
        ret.put(Fields.SOCIAL, hasSocial());
        ret.put(Fields.STATUS, getStatus().name());

        return ret;
    }

    /**
     * Returns the set of output fields from the content item.
     */
    public Fields toFields()
    {
        Fields ret = new Fields();

        ret.put(Fields.ID, Integer.toString(getId()));
        ret.put(Fields.UUID, getUuid());
        ret.put(Fields.PUBLISHED_DATE, getPublishedDateAsString());
        ret.put(Fields.TITLE, getTitle());
        ret.put(Fields.SUMMARY, EmojiParser.parseToHtmlDecimal(getSummary()));
        ret.put(Fields.PUBLISHED, isPublished() ? "1" : "0");
        ret.put(Fields.TRACKING, getTracking());
        ret.put(Fields.CREATED_BY, getCreatedBy());

        return ret;
    }

    /**
     * Copies any external attributes of the given object.
     * <p>
     * Implemented by super-class.
     */
    public void copyExternalAttributes(ContentItem obj)
    {
    }

    /**
     * Use the given configuration to set defaults for the content item.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, ContentConfiguration config)
    {
        if(organisationSite != null)
        {
            OrganisationContentType type = organisationSite.getContentType(getType());
            if(type != null)
                setTracking(type.getTracking());
        }

        if(config.hasField(Fields.CODE))
            setCode(config.getField(Fields.CODE));

        String published = config.getField(Fields.PUBLISHED);
        setPublished(published == null || published.equals("0") ? false : true);
    }

    /**
     * Returns the content UUID.
     */
    public String toString()
    {
        return getUuid();
    }

    /**
     * Returns the content type.
     */
    public abstract ContentType getType();

    /**
     * Sets the details from a summary.
     */
    public void setContentSummary(ContentSummary obj)
    {
        if(obj != null)
        {
            setPublishedDate(obj.getPublishedDate());
            setTitle(new String(obj.getTitle()));
            setSummary(new String(obj.getSummary() != null ? obj.getSummary() : ""));
        }
    }

    /**
     * Returns <CODE>true</CODE> if the details have been set.
     */
    public boolean hasContentDetails()
    {
        return detailsSet;
    }

    /**
     * Set to <CODE>true</CODE> if the details have been set.
     */
    public void setContentDetails(boolean detailsSet)
    {
        this.detailsSet = detailsSet;
    }

    /**
     * Returns the content details.
     */
    public ContentSummary getContentDetails()
    {
        return details;
    }

    /**
     * Set the content details.
     */
    protected void setContentDetails(ContentSummary details)
    {
        this.details = details;
    }

    /**
     * Returns the content UUID.
     */
    public String getUniqueId()
    {
        return getUuid();
    }

    /**
     * Returns <CODE>true</CODE> if the unique ID has been set.
     */
    public boolean hasUniqueId()
    {
        return getUniqueId() != null && getUniqueId().length() > 0;
    }

    /**
     * Returns the content UUID.
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * Sets the content UUID.
     */
    public void setUuid()
    {
        setUuid(StringUtils.getUUID(null));
    }

    /**
     * Sets the content UUID.
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Returns the content ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the content ID.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Clears the content ID.
     */
    public void clearId()
    {
        setId(-1);
    }

    /**
     * Returns the content GUID.
     */
    public String getGuid()
    {
        return String.format("%s-%s-%05d", getType().code(), code, id);
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
     * Returns the content title.
     */
    public String getTitle()
    {
        return details.getTitle();
    }

    /**
     * Sets the content title.
     */
    public void setTitle(String title)
    {
        details.setTitle(title != null ? title : "");
    }

    /**
     * Returns <CODE>true</CODE> if the title has been set.
     */
    public boolean hasTitle()
    {
        return getTitle() != null && getTitle().length() > 0;
    }

    /**
     * Returns the date the content was published.
     */
    public Instant getPublishedDate()
    {
        return details.getPublishedDate();
    }

    /**
     * Returns the date the content was published.
     */
    public long getPublishedDateMillis()
    {
        return getPublishedDate() != null ? getPublishedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the content was published.
     */
    public LocalDateTime getPublishedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getPublishedDate());
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(getPublishedDate(), pattern);
    }

    /**
     * Returns the date the content was published.
     */
    public String getPublishedDateAsString()
    {
        return getPublishedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDate(Instant publishedDate)
    {
        details.setPublishedDate(publishedDate);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateMillis(long millis)
    {
        if(millis > 0L)
            setPublishedDate(Instant.ofEpochMilli(millis));
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setPublishedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateAsString(String str) throws DateTimeParseException
    {
        setPublishedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the content was published.
     */
    public void setPublishedDateUTC(LocalDateTime publishedDate)
    {
        if(publishedDate != null)
            setPublishedDate(TimeUtils.toInstantUTC(publishedDate));
    }

    /**
     * Returns the content summary.
     */
    public String getSummary()
    {
        return details.getSummary();
    }

    /**
     * Sets the content summary.
     */
    public void setSummary(String summary)
    {
        details.setSummary(summary != null ? summary : "");
    }

    /**
     * Returns the content image name.
     * <P>
     * Always returns <CODE>null</CODE>.
     */
    public String getImage()
    {
        return null;
    }

    /**
     * Sets the content image name.
     * <P>
     * Empty implementation.
     */
    public void setImage(String image)
    {
    }

    /**
     * Returns <CODE>true</CODE> if the content image has been set.
     * <P>
     * Always returns <CODE>false</CODE>.
     */
    public boolean hasImage()
    {
        return false;
    }

    /**
     * Returns <CODE>true</CODE> if the content image name has a JPG extension.
     */
    public boolean isJpgImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.JPG_EXT) || image.endsWith("."+CommonFiles.JPEG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the content image name has a PNG extension.
     */
    public boolean isPngImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.PNG_EXT);
    }

    /**
     * Returns <CODE>true</CODE> if the content image name has a GIF extension.
     */
    public boolean isGifImage()
    {
        String image = getImage() != null ? getImage().toLowerCase() : "";
        return image.endsWith("."+CommonFiles.GIF_EXT);
    }

    /**
     * Returns the content image source.
     * <P>
     * Always returns <CODE>null</CODE>.
     */
    public String getImageSource()
    {
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if the content image source has been set.
     * <P>
     * Always returns <CODE>false</CODE>.
     */
    public boolean hasImageSource()
    {
        return false;
    }

    /**
     * Sets the image name.
     */
    public void setImageFromPath(String prefix, String path)
    {
    }

    /**
     * Returns <CODE>true</CODE> if this content should be published.
     */
    public boolean isPublished()
    {
        return published;
    }

    /**
     * Returns <CODE>true</CODE> if this content should be published.
     */
    public Boolean getPublishedObject()
    {
        return Boolean.valueOf(isPublished());
    }

    /**
     * Set to <CODE>true</CODE> if this content should be published.
     */
    public void setPublished(boolean published)
    {
        this.published = published;
    }

    /**
     * Set to <CODE>true</CODE> if this content should be published.
     */
    public void setPublishedObject(Boolean published)
    {
        setPublished(published != null && published.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if this content should be promoted.
     */
    public boolean isPromoted()
    {
        return true;
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
        return getTracking() != null && getTracking().length() > 0;
    }

    /**
     * Returns the user that created the content.
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the user that created the content.
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * Returns <CODE>true</CODE> if this content has metatags to set.
     */
    public boolean hasMetatags()
    {
        return false;
    }

    /**
     * Add the metatags for this content to the given JSON object.
     */
    protected void addMetatags(JSONObject obj)
    {
    }

    /**
     * Returns the metatags for this content in JSON format.
     */
    public String getMetatags()
    {
        String ret = "";

        if(hasMetatags())
        {
            JSONObject obj = new JSONObject();
            addMetatags(obj);
            ret = obj.toString();
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if this content has social media.
     */
    public boolean hasSocial()
    {
        return social;
    }

    /**
     * Returns <CODE>true</CODE> if this content has social media.
     */
    public Boolean getSocialObject()
    {
        return Boolean.valueOf(hasSocial());
    }

    /**
     * Set to <CODE>true</CODE> if this content has social media.
     */
    public void setSocial(boolean social)
    {
        this.social = social;
    }

    /**
     * Set to <CODE>true</CODE> if this content has social media.
     */
    public void setSocialObject(Boolean social)
    {
        setSocial(social != null && social.booleanValue());
    }

    /**
     * Returns the canonical URL for the post.
     */
    public String getCanonicalUrl()
    {
        return canonicalUrl;
    }

    /**
     * Sets the canonical URL for the post.
     */
    public void setCanonicalUrl(String canonicalUrl)
    {
        this.canonicalUrl = canonicalUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the canonical URL for the post has been set.
     */
    public boolean hasCanonicalUrl()
    {
        return canonicalUrl != null && canonicalUrl.length() > 0;
    }

    /**
     * Returns the content status.
     */
    public ContentStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the content status.
     */
    public void setStatus(String status)
    {
        setStatus(ContentStatus.valueOf(status));
    }

    /**
     * Sets the content status.
     */
    public void setStatus(ContentStatus status)
    {
        this.status = status;
    }

    /**
     * Returns <CODE>true</CODE> if this content has been archived.
     */
    public boolean isArchived()
    {
        return getStatus() == ContentStatus.ARCHIVED;
    }

    /**
     * Returns <CODE>true</CODE> if this content has been skipped.
     */
    public boolean isSkipped()
    {
        return getStatus() == ContentStatus.SKIPPED;
    }

    /**
     * Returns <CODE>true</CODE> if this content has been deployed.
     */
    public boolean isDeployed()
    {
        return getStatus() == ContentStatus.DEPLOYED;
    }

    /**
     * Returns the other site ids.
     */
    public String getOtherSites()
    {
        return otherSites;
    }

    /**
     * Sets the other site ids.
     */
    public void setOtherSites(String otherSites)
    {
        this.otherSites = otherSites;
    }

    /**
     * Sets the other site ids.
     */
    public void setOtherSites(List<? extends ContentItem> items)
    {
        StringBuilder str = new StringBuilder();

        if(items != null)
        {
            for(ContentItem item : items)
            {
                if(str.indexOf(item.getSiteId()) == -1)
                {
                    if(str.length() > 0)
                        str.append(",");
                    str.append(item.getSiteId());
                }
            }
        }

        setOtherSites(str.toString());
    }

    /**
     * Returns <CODE>true</CODE> if the other site ids have been set.
     */
    public boolean hasOtherSites()
    {
        return getOtherSites() != null && getOtherSites().length() > 0;
    }
}