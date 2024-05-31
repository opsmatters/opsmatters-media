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
import com.vdurmont.emoji.EmojiParser;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.ContentSettings;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a piece of resource content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Resource<D extends ResourceDetails> extends Content<D> implements LinkedContent
{
    private String features = "";
    private String linkText = "";
    private boolean promote = false;

    /**
     * Default constructor.
     */
    public Resource()
    {
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Resource obj)
    {
        super.copyAttributes(obj);

        setFeatures(new String(obj.getFeatures() != null ? obj.getFeatures() : ""));
        setLinkText(new String(obj.getLinkText() != null ? obj.getLinkText() : ""));
        setPromoted(obj.isPromoted());
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setDescription(EmojiParser.parseToUnicode(obj.optString(DESCRIPTION.value())));
        setUrl(obj.optString(URL.value()));
        setFeatures(obj.optString(FEATURES.value()));
        setLinkText(obj.optString(LINK_TEXT.value()));
        setPromoted(obj.optBoolean(PROMOTE.value(), false));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(DESCRIPTION.value(), EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(URL.value(), getUrl());
        ret.putOpt(FEATURES.value(), getFeatures());
        ret.putOpt(LINK_TEXT.value(), getLinkText());
        ret.put(PROMOTE.value(), isPromoted());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(DESCRIPTION, EmojiParser.parseToHtmlDecimal(getDescription()));
        ret.put(URL, getUrl());
        ret.put(FEATURES, getFeatures());
        ret.put(LINK_TEXT, getLinkText());
        ret.put(PROMOTE, isPromoted() ? "1" : "0");

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
            ContentSettings settings = organisationSite.getContentSettings(getType());
            if(settings != null)
                setFeatures(settings.getFeatures());
        }
    }

    /**
     * Sets the details from a teaser.
     */
    @Override
    public void setTeaserDetails(D obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setDescription(new String(obj.getDescription()));
        }
    }

    /**
     * Sets the resource details.
     */
    @Override
    public void setContentDetails(D obj)
    {
        super.setContentDetails(obj);
    }

    /**
     * Returns the resource description.
     */
    public String getDescription()
    {
        return getDetails().getDescription();
    }

    /**
     * Sets the resource description.
     */
    public void setDescription(String description)
    {
        getDetails().setDescription(description != null ? description : "");
    }

    /**
     * Returns the URL of the resource.
     */
    public String getUrl()
    {
        return getDetails().getUrl();
    }

    /**
     * Sets the URL of the resource.
     */
    public void setUrl(String url)
    {
        setUrl(url, false);
    }

    /**
     * Sets the URL of the resource.
     */
    public void setUrl(String url, boolean removeParameters)
    {
        getDetails().setUrl(url, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return getDetails().hasUrl();
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
     * Returns the link text.
     */
    public String getLinkText()
    {
        return linkText;
    }

    /**
     * Sets the link text.
     */
    public void setLinkText(String linkText)
    {
        this.linkText = linkText;
    }

    /**
     * Sets the link text.
     */
    public void setLinkText(LinkText linkText)
    {
        setLinkText(linkText.value());
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
}