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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.config.content.FieldSource;
import com.opsmatters.media.model.OwnedItem;
import com.opsmatters.media.util.Formats;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing an organisation.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Organisation extends OwnedItem implements FieldSource
{
    public static final String MISCELLANEOUS = "Miscellaneous";

    private String code = "";
    private String name = "";
    private String website = "";
    private String email = "";
    private String facebookUsername = "";
    private String twitterUsername = "";
    private String hashtag = "";
    private boolean sponsor = false;
    private String thumbnail = "";
    private String thumbnailText = "";
    private OrganisationStatus status = OrganisationStatus.NEW;
    private Map<ContentType, ContentTypeSummary> content = new HashMap<ContentType, ContentTypeSummary>();
    private Instant reviewedDate;
    private int listingId = -1;

    /**
     * Default constructor.
     */
    public Organisation()
    {
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Organisation obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
            setEmail(new String(obj.getEmail() != null ? obj.getEmail() : ""));
            setFacebookUsername(new String(obj.getFacebookUsername() != null ? obj.getFacebookUsername() : ""));
            setTwitterUsername(new String(obj.getTwitterUsername() != null ? obj.getTwitterUsername() : ""));
            setHashtag(new String(obj.getHashtag() != null ? obj.getHashtag() : ""));
            setSponsor(obj.isSponsor());
            setThumbnail(new String(obj.getThumbnail() != null ? obj.getThumbnail() : ""));
            setThumbnailText(new String(obj.getThumbnailText() != null ? obj.getThumbnailText() : ""));
            setStatus(obj.getStatus());
            setReviewedDate(obj.getReviewedDate());
            setListingId(obj.getListingId());
        }
    }

    /**
     * Returns the attributes as a JSON object.
     */
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.put(Fields.SPONSOR, isSponsor());
        ret.putOpt(Fields.WEBSITE, getWebsite());
        ret.putOpt(Fields.EMAIL, getEmail());
        ret.putOpt(Fields.HASHTAG, getHashtag());
        ret.putOpt(Fields.TWITTER_USERNAME, getTwitterUsername());
        ret.putOpt(Fields.FACEBOOK_USERNAME, getFacebookUsername());
        ret.putOpt(Fields.THUMBNAIL, getThumbnail());
        ret.putOpt(Fields.THUMBNAIL_TEXT, getThumbnailText());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    public void setAttributes(JSONObject obj)
    {
        setSponsor(obj.optBoolean(Fields.SPONSOR, false));
        setWebsite(obj.optString(Fields.WEBSITE));
        setEmail(obj.optString(Fields.EMAIL));
        setHashtag(obj.optString(Fields.HASHTAG));
        setTwitterUsername(obj.optString(Fields.TWITTER_USERNAME));
        setFacebookUsername(obj.optString(Fields.FACEBOOK_USERNAME));
        setThumbnail(obj.optString(Fields.THUMBNAIL));
        setThumbnailText(obj.optString(Fields.THUMBNAIL_TEXT));
    }

    /**
     * Returns the fields required by other objects.
     */
    public Fields getFields()
    {
        Fields ret = new Fields();

        ret.put(Fields.SPONSOR, isSponsor() ? "1" : "0");
        ret.put(Fields.WEBSITE, getWebsite());
        ret.put(Fields.EMAIL, getEmail());
        ret.put(Fields.FACEBOOK_USERNAME, getFacebookUsername());
        ret.put(Fields.TWITTER_USERNAME, getTwitterUsername());
        ret.put(Fields.HASHTAG, getHashtag());
        ret.put(Fields.THUMBNAIL, getThumbnail());
        ret.put(Fields.THUMBNAIL_TEXT, getThumbnailText());
        ret.put(Fields.PUBLISHED, isActive() ? "1" : "0");

        return ret;
    }

    /**
     * Returns a new organisation with defaults.
     */
    public static Organisation getDefault()
    {
        Organisation organisation = new Organisation();

        organisation.setId(StringUtils.getUUID(null));
        organisation.setCode("TBD");
        organisation.setName("New Organisation");
        organisation.setCreatedDate(Instant.now());
        organisation.setThumbnail("tbd-thumb.png");
        organisation.setThumbnailText("tbd logo");

        return organisation;
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
        return new Boolean(isSponsor());
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
     * Returns the organisation's facebook username.
     */
    public String getFacebookUsername()
    {
        return facebookUsername;
    }

    /**
     * Sets the organisation's facebook username.
     */
    public void setFacebookUsername(String facebookUsername)
    {
        this.facebookUsername = facebookUsername;
    }

    /**
     * Returns the organisation's twitter username.
     */
    public String getTwitterUsername()
    {
        return twitterUsername;
    }

    /**
     * Sets the organisation's twitter username.
     */
    public void setTwitterUsername(String twitterUsername)
    {
        this.twitterUsername = twitterUsername;
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
     * Returns the organisation's logo thumbnail.
     */
    public String getThumbnail()
    {
        return thumbnail;
    }

    /**
     * Sets the organisation's logo thumbnail.
     */
    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a thumbnail image.
     */
    public boolean hasThumbnail()
    {
        return thumbnail != null && thumbnail.length() > 0;
    }

    /**
     * Returns the organisation's logo thumbnail text.
     */
    public String getThumbnailText()
    {
        return thumbnailText;
    }

    /**
     * Sets the organisation's logo thumbnail text.
     */
    public void setThumbnailText(String thumbnailText)
    {
        this.thumbnailText = thumbnailText;
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
     * Returns the date the item was last reviewed.
     */
    public Instant getReviewedDate()
    {
        return reviewedDate;
    }

    /**
     * Returns the date the item was last reviewed.
     */
    public long getReviewedDateMillis()
    {
        return getReviewedDate() != null ? getReviewedDate().toEpochMilli() : 0L;
    }

    /**
     * Returns the date the item was last reviewed.
     */
    public LocalDateTime getReviewedDateUTC()
    {
        return TimeUtils.toDateTimeUTC(getReviewedDate());
    }

    /**
     * Returns the date the item was last reviewed.
     */
    public String getReviewedDateAsString(String pattern)
    {
        return TimeUtils.toStringUTC(reviewedDate, pattern);
    }

    /**
     * Returns the date the item was last reviewed.
     */
    public String getReviewedDateAsString(String pattern, String timezone)
    {
        return TimeUtils.toString(reviewedDate, pattern, timezone);
    }

    /**
     * Returns the date the item was last reviewed.
     */
    public String getReviewedDateAsString()
    {
        return getReviewedDateAsString(Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was last reviewed.
     */
    public void setReviewedDate(Instant reviewedDate)
    {
        this.reviewedDate = reviewedDate;
    }

    /**
     * Sets the date the item was last reviewed.
     */
    public void setReviewedDateMillis(long millis)
    {
        if(millis > 0L)
            this.reviewedDate = Instant.ofEpochMilli(millis);
    }

    /**
     * Sets the date the item was last reviewed.
     */
    public void setReviewedDateAsString(String str, String pattern) throws DateTimeParseException
    {
        setReviewedDate(TimeUtils.toInstantUTC(str, pattern));
    }

    /**
     * Sets the date the item was last reviewed.
     */
    public void setReviewedDateAsString(String str) throws DateTimeParseException
    {
        setReviewedDateAsString(str, Formats.CONTENT_DATE_FORMAT);
    }

    /**
     * Sets the date the item was last reviewed.
     */
    public void setReviewedDateUTC(LocalDateTime reviewedDate)
    {
        if(reviewedDate != null)
            setReviewedDate(TimeUtils.toInstantUTC(reviewedDate));
    }

    /**
     * Returns the id of the organisation listing.
     */
    public int getListingId()
    {
        return listingId;
    }

    /**
     * Sets the id of the organisation listing.
     */
    public void setListingId(int listingId)
    {
        this.listingId = listingId;
    }

    /**
     * Returns the content type summaries.
     */
    public Map<ContentType,ContentTypeSummary> getContent()
    {
        return content;
    }

    /**
     * Returns <CODE>true</CODE>if the organisation has content type summaries.
     */
    public boolean hasContent()
    {
        return content != null && content.size() > 0;
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