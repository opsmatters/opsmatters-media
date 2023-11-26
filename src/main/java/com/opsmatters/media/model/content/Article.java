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
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.organisation.OrganisationContentType;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a piece of article content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Article<D extends ArticleDetails> extends Content<D>
{
    private String organisation = "";
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

        setOrganisation(new String(obj.getOrganisation() != null ? obj.getOrganisation() : ""));
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

        setTags(obj.optString(TAGS.value()));
        setCreatorEmail(obj.optString(EMAIL.value()));
        setPromoted(obj.optBoolean(PROMOTE.value(), false));
        setNewsletter(obj.optBoolean(NEWSLETTER.value(), false));
        setFeatured(obj.optBoolean(FEATURED.value(), false));
        setSponsored(obj.optBoolean(SPONSORED.value(), false));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(TAGS.value(), getTags());
        ret.putOpt(EMAIL.value(), getCreatorEmail());
        ret.put(PROMOTE.value(), isPromoted());
        ret.put(NEWSLETTER.value(), isNewsletter());
        ret.put(FEATURED.value(), isFeatured());
        ret.put(SPONSORED.value(), isSponsored());

        return ret;
    }

    /**
     * Returns the set of output fields for the content.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(TAGS, getTags());
        ret.put(EMAIL, getCreatorEmail());
        ret.put(PROMOTE, isPromoted() ? "1" : "0");
        ret.put(NEWSLETTER, isNewsletter() ? "1" : "0");
        ret.put(FEATURED, isFeatured() ? "1" : "0");
        ret.put(SPONSORED, isSponsored() ? "1" : "0");

        return ret;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    @Override
    public void init(Organisation organisation, OrganisationSite organisationSite, ContentConfig config)
    {
        super.init(organisation, organisationSite, config);

        if(organisationSite != null)
        {
            OrganisationContentType type = organisationSite.getContentType(getType());
            if(type != null)
            {
                setTags(type.getTags());
                setNewsletter(type.isNewsletter());
            }
        }
    }

    /**
     * Sets the details from a teaser.
     */
    @Override
    public void setTeaserDetails(D obj)
    {
        super.setTeaserDetails(obj);
    }

    /**
     * Sets the article details.
     */
    @Override
    public void setContentDetails(D obj)
    {
        super.setContentDetails(obj);
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
     * Sets the monitor organisation.
     */
    @Override
    public void setCode(String code)
    {
        super.setCode(code);

        Organisation organisation = Organisations.get(code);
        setOrganisation(organisation != null ? organisation.getName() : "");
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
     * Clears the content revised title.
     */
    public void clearRevisedTitle()
    {
        setRevisedTitle("");
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
        return Boolean.valueOf(isPromoted());
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
        return Boolean.valueOf(isNewsletter());
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
        return Boolean.valueOf(isFeatured());
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
        return Boolean.valueOf(isSponsored());
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