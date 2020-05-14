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

import java.util.List;
import org.json.JSONObject;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content article.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Article extends ContentItem
{
    private ArticleSummary details;

    private String revisedTitle = "";
    private String tags = "";
    private String creatorEmail = "";
    private boolean promote = false;
    private boolean newsletter = false;
    private boolean featured = false;
    private boolean sponsored = false;

    /**
     * Default constructor.
     */
    public Article()
    {
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Article obj)
    {
        super.copyAttributes(obj);

        setRevisedTitle(new String(obj.getRevisedTitle() != null ? obj.getRevisedTitle() : ""));
        setTags(new String(obj.getTags() != null ? obj.getTags() : ""));
        setCreatorEmail(new String(obj.getCreatorEmail() != null ? obj.getCreatorEmail() : ""));
        setPromoted(obj.isPromoted());
        setNewsletter(obj.isNewsletter());
        setFeatured(obj.isFeatured());
        setSponsored(obj.isSponsored());
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setTags(obj.optString(Fields.TAGS));
        setCreatorEmail(obj.optString(Fields.EMAIL));
        setPromoted(obj.optBoolean(Fields.PROMOTE, false));
        setNewsletter(obj.optBoolean(Fields.NEWSLETTER, false));
        setFeatured(obj.optBoolean(Fields.FEATURED, false));
        setSponsored(obj.optBoolean(Fields.SPONSORED, false));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.TAGS, getTags());
        ret.putOpt(Fields.EMAIL, getCreatorEmail());
        ret.put(Fields.PROMOTE, isPromoted());
        ret.put(Fields.NEWSLETTER, isNewsletter());
        ret.put(Fields.FEATURED, isFeatured());
        ret.put(Fields.SPONSORED, isSponsored());

        return ret;
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.TAGS, getTags());
        ret.put(Fields.EMAIL, getCreatorEmail());
        ret.put(Fields.PROMOTE, isPromoted() ? "1" : "0");
        ret.put(Fields.NEWSLETTER, isNewsletter() ? "1" : "0");
        ret.put(Fields.FEATURED, isFeatured() ? "1" : "0");
        ret.put(Fields.SPONSORED, isSponsored() ? "1" : "0");

        return ret;
    }

    /**
     * Sets the details from a summary.
     */
    public void setContentSummary(ArticleSummary obj)
    {
        super.setContentSummary(obj);
    }

    /**
     * Set the content details.
     */
    protected void setContentDetails(ArticleSummary details)
    {
        super.setContentDetails(details);
        this.details = details;
    }

    /**
     * Returns the content revised title.
     */
    public String getRevisedTitle()
    {
        return revisedTitle;
    }

    /**
     * Sets the content revised title.
     */
    public void setRevisedTitle(String revisedTitle)
    {
        this.revisedTitle = revisedTitle;
    }

    /**
     * Returns <CODE>true</CODE> if the revised title has been set.
     */
    public boolean hasRevisedTitle()
    {
        return getRevisedTitle() != null && getRevisedTitle().length() > 0;
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
     * Returns the creator email.
     */
    public String getCreatorEmail()
    {
        return creatorEmail;
    }

    /**
     * Sets the creator email.
     */
    public void setCreatorEmail(String creatorEmail)
    {
        this.creatorEmail = creatorEmail;
    }

    /**
     * Returns <CODE>true</CODE> if this content should be promoted.
     */
    @Override
    public boolean isPromoted()
    {
        return promote;
    }

    /**
     * Returns <CODE>true</CODE> if this content should be promoted.
     */
    public Boolean getPromoteObject()
    {
        return new Boolean(isPromoted());
    }

    /**
     * Set to <CODE>true</CODE> if this content should be promoted.
     */
    public void setPromoted(boolean promote)
    {
        this.promote = promote;
    }

    /**
     * Set to <CODE>true</CODE> if this content should be promoted.
     */
    public void setPromoteObject(Boolean promote)
    {
        setPromoted(promote != null && promote.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if this content should appear in the newsletter.
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
        return new Boolean(isNewsletter());
    }

    /**
     * Returns the newsletter status of the article.
     */
    public NewsletterStatus getNewsletterStatus()
    {
        return newsletter ? NewsletterStatus.INCLUDED : NewsletterStatus.NOT_INCLUDED;
    }

    /**
     * Set to <CODE>true</CODE> if this content should appear in the newsletter.
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
     * Returns <CODE>true</CODE> if this content should appear in the featured section.
     */
    public boolean isFeatured()
    {
        return featured;
    }

    /**
     * Returns <CODE>true</CODE> if this content should appear in the featured section.
     */
    public Boolean getFeaturedObject()
    {
        return new Boolean(isFeatured());
    }

    /**
     * Set to <CODE>true</CODE> if this content should appear in the featured section.
     */
    public void setFeatured(boolean featured)
    {
        this.featured = featured;
    }

    /**
     * Set to <CODE>true</CODE> if this content should appear in the featured section.
     */
    public void setFeaturedObject(Boolean featured)
    {
        setFeatured(featured != null && featured.booleanValue());
    }

    /**
     * Returns <CODE>true</CODE> if this content is sponsored.
     */
    public boolean isSponsored()
    {
        return sponsored;
    }

    /**
     * Returns <CODE>true</CODE> if this content is sponsored.
     */
    public Boolean getSponsoredObject()
    {
        return new Boolean(isSponsored());
    }

    /**
     * Set to <CODE>true</CODE> if this content is sponsored.
     */
    public void setSponsored(boolean sponsored)
    {
        this.sponsored = sponsored;
    }

    /**
     * Set to <CODE>true</CODE> if this content is sponsored.
     */
    public void setSponsoredObject(Boolean sponsored)
    {
        setSponsored(sponsored != null && sponsored.booleanValue());
    }
}