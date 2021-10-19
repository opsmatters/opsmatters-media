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
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.config.content.ContentConfiguration;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a content resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class Resource extends ContentItem
{
    private ResourceSummary details;

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

        setDescription(EmojiParser.parseToUnicode(obj.optString(Fields.DESCRIPTION)));
        setUrl(obj.optString(Fields.URL));
        setFeatures(obj.optString(Fields.FEATURES));
        setLinkText(obj.optString(Fields.LINK_TEXT));
        setPromoted(obj.optBoolean(Fields.PROMOTE, false));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.DESCRIPTION, EmojiParser.parseToAliases(getDescription()));
        ret.putOpt(Fields.URL, getUrl());
        ret.putOpt(Fields.FEATURES, getFeatures());
        ret.putOpt(Fields.LINK_TEXT, getLinkText());
        ret.put(Fields.PROMOTE, isPromoted());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.DESCRIPTION, EmojiParser.parseToHtmlDecimal(getDescription()));
        ret.put(Fields.URL, getUrl());
        ret.put(Fields.FEATURES, getFeatures());
        ret.put(Fields.LINK_TEXT, getLinkText());
        ret.put(Fields.PROMOTE, isPromoted() ? "1" : "0");

        return ret;
    }

    /**
     * Use the given configuration to set defaults for the content.
     */
    @Override
    public void init(Organisation organisation, ContentConfiguration config)
    {
        super.init(organisation, config);

        if(organisation != null)
        {
            OrganisationContentType type = organisation.getContentType(getType());
            if(type != null)
                setFeatures(type.getFeatures());
        }
    }

    /**
     * Sets the details from a summary.
     */
    public void setContentSummary(ResourceSummary obj)
    {
        super.setContentSummary(obj);
        setDescription(new String(obj.getDescription()));
    }

    /**
     * Set the content details.
     */
    protected void setContentDetails(ResourceSummary details)
    {
        super.setContentDetails(details);
        this.details = details;
    }

    /**
     * Returns the resource description.
     */
    public String getDescription()
    {
        return details.getDescription();
    }

    /**
     * Sets the resource description.
     */
    public void setDescription(String description)
    {
        details.setDescription(description != null ? description : "");
    }

    /**
     * Returns the URL of the resource.
     */
    public String getUrl()
    {
        return details.getUrl();
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
        details.setUrl(url, removeParameters);
    }

    /**
     * Returns <CODE>true</CODE> if the URL has been set.
     */
    public boolean hasUrl()
    {
        return details.hasUrl();
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