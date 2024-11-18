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
package com.opsmatters.media.model.content.organisation;

import java.util.List;
import java.util.Calendar;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.social.SocialProvider;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.SessionId;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing an organisation's listing.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationListing extends Content
{
    private Instant publishedDate;
    private String title = "";
    private OrganisationTabs tabs = OrganisationTabs.ALL;
    private int content = 0;
    private String summary = "";
    private String description = "";
    private String footer = "";
    private String founded = "";
    private String location = "";
    private String stockSymbol = "";
    private SocialProvider feedProvider;
    private String facebook = "";
    private String twitter = "";
    private String linkedin = "";
    private String instagram = "";
    private String youtube = "";
    private String vimeo = "";
    private boolean projects = false;
    private String github = "";
    private boolean tools = false;
    private String alternatives = "";
    private String features = "";

    /**
     * Default constructor.
     */
    public OrganisationListing()
    {
    }

    /**
     * Constructor that takes an organisation listing.
     */
    public OrganisationListing(OrganisationListing obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OrganisationListing obj)
    {
        super.copyAttributes(obj);

        setPublishedDate(obj.getPublishedDate());
        setTitle(new String(obj.getTitle() != null ? obj.getTitle() : ""));
        setTabs(obj.getTabs());
        setSummary(new String(obj.getSummary() != null ? obj.getSummary() : ""));
        setDescription(new String(obj.getDescription() != null ? obj.getDescription() : ""));
        setFooter(new String(obj.getFooter() != null ? obj.getFooter() : ""));
        setFounded(new String(obj.getFounded() != null ? obj.getFounded() : ""));
        setLocation(new String(obj.getLocation() != null ? obj.getLocation() : ""));
        setStockSymbol(new String(obj.getStockSymbol() != null ? obj.getStockSymbol() : ""));
        setFeedProvider(obj.getFeedProvider());
        setFacebook(new String(obj.getFacebook() != null ? obj.getFacebook() : ""));
        setTwitter(new String(obj.getTwitter() != null ? obj.getTwitter() : ""));
        setLinkedIn(new String(obj.getLinkedIn() != null ? obj.getLinkedIn() : ""));
        setInstagram(new String(obj.getInstagram() != null ? obj.getInstagram() : ""));
        setYouTube(new String(obj.getYouTube() != null ? obj.getYouTube() : ""));
        setVimeo(new String(obj.getVimeo() != null ? obj.getVimeo() : ""));
        setProjects(obj.hasProjects());
        setGitHub(new String(obj.getGitHub() != null ? obj.getGitHub() : ""));
        setTools(obj.hasTools());
        setAlternatives(new String(obj.getAlternatives() != null ? obj.getAlternatives() : ""));
        setFeatures(new String(obj.getFeatures() != null ? obj.getFeatures() : ""));
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.put(TABS.value(), getTabs().name());
        ret.put(CONTENT.value(), getContent());
        ret.putOpt(DESCRIPTION.value(), getDescription());
        ret.putOpt(FOOTER.value(), getFooter());
        ret.putOpt(FOUNDED.value(), getFounded());
        ret.putOpt(LOCATION.value(), getLocation());
        ret.putOpt(STOCK_SYMBOL.value(), getStockSymbol());
        ret.putOpt(FEED_PROVIDER.value(), getFeedProvider().name());
        ret.putOpt(FACEBOOK.value(), getFacebook());
        ret.putOpt(TWITTER.value(), getTwitter());
        ret.putOpt(LINKEDIN.value(), getLinkedIn());
        ret.putOpt(INSTAGRAM.value(), getInstagram());
        ret.putOpt(YOUTUBE.value(), getYouTube());
        ret.putOpt(VIMEO.value(), getVimeo());
        ret.put(PROJECTS.value(), hasProjects());
        ret.putOpt(GITHUB.value(), getGitHub());
        ret.put(TOOLS.value(), hasTools());
        ret.putOpt(ALTERNATIVES.value(), getAlternatives());
        ret.putOpt(FEATURES.value(), getFeatures());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setTabs(OrganisationTabs.valueOf(obj.optString(TABS.value())));
        setDescription(obj.optString(DESCRIPTION.value()));
        setFooter(obj.optString(FOOTER.value()));
        setFounded(obj.optString(FOUNDED.value()));
        setLocation(obj.optString(LOCATION.value()));
        setStockSymbol(obj.optString(STOCK_SYMBOL.value()));
        setFeedProvider(SocialProvider.valueOf(obj.optString(FEED_PROVIDER.value())));
        setFacebook(obj.optString(FACEBOOK.value()));
        setTwitter(obj.optString(TWITTER.value()));
        setLinkedIn(obj.optString(LINKEDIN.value()));
        setInstagram(obj.optString(INSTAGRAM.value()));
        setYouTube(obj.optString(YOUTUBE.value()));
        setVimeo(obj.optString(VIMEO.value()));
        setProjects(obj.optBoolean(PROJECTS.value(), false));
        setGitHub(obj.optString(GITHUB.value()));
        setTools(obj.optBoolean(TOOLS.value(), false));
        setAlternatives(obj.optString(ALTERNATIVES.value()));
        setFeatures(obj.optString(FEATURES.value()));
    }

    /**
     * Returns the set of output fields from the organisation listing.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(CODE, getCode());
        ret.put(TABS, getTabs().name());
        ret.put(CONTENT, Integer.toString(getTabs().content()));
        ret.put(DESCRIPTION, getDescription());
        ret.put(FOOTER, getFooter());
        ret.put(FOUNDED, getFounded());
        ret.put(LOCATION, getLocation());
        ret.put(STOCK_SYMBOL, getStockSymbol());
        ret.put(FEED_PROVIDER, getFeedProvider().name());
        ret.put(FACEBOOK, getFacebook());
        ret.put(TWITTER, getTwitter());
        ret.put(LINKEDIN, getLinkedIn());
        ret.put(INSTAGRAM, getInstagram());
        ret.put(YOUTUBE, getYouTube());
        ret.put(VIMEO, getVimeo());
        ret.put(PROJECTS, hasProjects() ? "1" : "0");
        ret.put(GITHUB, getGitHub());
        ret.put(TOOLS, hasTools() ? "1" : "0");
        ret.put(ALTERNATIVES, getAlternatives());
        ret.put(FEATURES, getFeatures());

        return ret;
    }

    /**
     * Returns a new organisation listing with defaults.
     */
    public static OrganisationListing getDefault(Organisation organisation,
        OrganisationSite organisationSite, OrganisationListingConfig config)
        throws DateTimeParseException
    {
        OrganisationListing listing = new OrganisationListing();

        listing.init();
        listing.setSiteId(organisationSite.getSiteId());
        listing.setCode(organisation.getCode());
        listing.setTitle(organisation.getName());
        listing.setPublishedDateAsString(TimeUtils.toStringUTC(SessionId.now(), config.getField(PUBLISHED_DATE)));
        listing.setFounded(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
        listing.setFeedProvider(SocialProvider.TWITTER);

        return listing;
    }

    /**
     * Use the given configuration to set defaults for the organisation listing.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, OrganisationListingConfig config)
    {
        super.init(organisation, organisationSite, config);

        setTabs(OrganisationTabs.valueOf(config.getField(TABS, "ALL")));

        String projects = config.getField(PROJECTS);
        setProjects(projects == null || projects.equals("0") ? false : true);

        String tools = config.getField(TOOLS);
        setTools(tools == null || tools.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the organisation listing using the given configuration.
     */
    public void prepare(OrganisationListingConfig config, boolean debug)
        throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.ORGANISATION;
    }

    /**
     * Returns the organisation name.
     */
    @Override
    public String toString()
    {
        return getTitle();
    }

    /**
     * Returns the date the organisation was published.
     */
    @Override
    public Instant getPublishedDate()
    {
        return publishedDate;
    }

    /**
     * Sets the date the organisation was published.
     */
    @Override
    public void setPublishedDate(Instant publishedDate)
    {
        this.publishedDate = publishedDate;
    }

    /**
     * Returns the organisation's title.
     */
    @Override
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the organisation's title.
     */
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the organisation's tabs.
     */
    public OrganisationTabs getTabs()
    {
        return tabs;
    }

    /**
     * Sets the organisation's tabs.
     */
    public void setTabs(OrganisationTabs tabs)
    {
        this.tabs = tabs;
        setContent(getTabs() != null ? getTabs().content() : 0);
    }

    /**
     * Returns the code indicating which tabs are enabled for this organisation.
     */
    public int getContent()
    {
        return content;
    }

    /**
     * Sets the code indicating which tabs are enabled for this organisation.
     */
    public void setContent(int content)
    {
        this.content = content;
    }

    /**
     * Returns the organisation's summary.
     */
    @Override
    public String getSummary()
    {
        return summary;
    }

    /**
     * Sets the organisation's summary.
     */
    @Override
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    /**
     * Returns the organisation's description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the organisation's description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the organisation's footer.
     */
    public String getFooter()
    {
        return footer;
    }

    /**
     * Sets the organisation's footer.
     */
    public void setFooter(String footer)
    {
        this.footer = footer;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a footer.
     */
    public boolean hasFooter()
    {
        return footer != null && footer.length() > 0;
    }

    /**
     * Returns the founding year for the organisation.
     */
    public String getFounded()
    {
        return founded;
    }

    /**
     * Sets the founding year for the organisation.
     */
    public void setFounded(String founded)
    {
        this.founded = founded;
    }

    /**
     * Returns the location of the organisation.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets the location of the organisation.
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Returns the stock symbol of the organisation.
     */
    public String getStockSymbol()
    {
        return stockSymbol;
    }

    /**
     * Sets the stock symbol of the organisation.
     */
    public void setStockSymbol(String stockSymbol)
    {
        this.stockSymbol = stockSymbol;
    }

    /**
     * Returns the organisation's feed provider.
     */
    public SocialProvider getFeedProvider()
    {
        return feedProvider;
    }

    /**
     * Sets the organisation's feed provider.
     */
    public void setFeedProvider(String feedProvider)
    {
        setFeedProvider(SocialProvider.valueOf(feedProvider));
    }

    /**
     * Sets the organisation's feed provider.
     */
    public void setFeedProvider(SocialProvider feedProvider)
    {
        this.feedProvider = feedProvider;
    }

    /**
     * Returns the organisation's facebook address.
     */
    public String getFacebook()
    {
        return facebook;
    }

    /**
     * Sets the organisation's facebook address.
     */
    public void setFacebook(String facebook)
    {
        this.facebook = facebook;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a facebook address.
     */
    public boolean hasFacebook()
    {
        return facebook != null && facebook.length() > 0;
    }

    /**
     * Returns the organisation's twitter address.
     */
    public String getTwitter()
    {
        return twitter;
    }

    /**
     * Sets the organisation's twitter address.
     */
    public void setTwitter(String twitter)
    {
        this.twitter = twitter;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a twitter address.
     */
    public boolean hasTwitter()
    {
        return twitter != null && twitter.length() > 0;
    }

    /**
     * Returns the organisation's linkedin address.
     */
    public String getLinkedIn()
    {
        return linkedin;
    }

    /**
     * Sets the organisation's linkedin address.
     */
    public void setLinkedIn(String linkedin)
    {
        this.linkedin = linkedin;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a linkedin address.
     */
    public boolean hasLinkedIn()
    {
        return linkedin != null && linkedin.length() > 0;
    }

    /**
     * Returns the organisation's instagram address.
     */
    public String getInstagram()
    {
        return instagram;
    }

    /**
     * Sets the organisation's instagram address.
     */
    public void setInstagram(String instagram)
    {
        this.instagram = instagram;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has an instagram address.
     */
    public boolean hasInstagram()
    {
        return instagram != null && instagram.length() > 0;
    }

    /**
     * Returns the organisation's youtube address.
     */
    public String getYouTube()
    {
        return youtube;
    }

    /**
     * Sets the organisation's youtube address.
     */
    public void setYouTube(String youtube)
    {
        this.youtube = youtube;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a youtube address.
     */
    public boolean hasYouTube()
    {
        return youtube != null && youtube.length() > 0;
    }

    /**
     * Returns the organisation's vimeo address.
     */
    public String getVimeo()
    {
        return vimeo;
    }

    /**
     * Sets the organisation's vimeo address.
     */
    public void setVimeo(String vimeo)
    {
        this.vimeo = vimeo;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a vimeo address.
     */
    public boolean hasVimeo()
    {
        return vimeo != null && vimeo.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has projects.
     */
    public boolean hasProjects()
    {
        return projects;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has projects.
     */
    public Boolean getProjectsObject()
    {
        return Boolean.valueOf(hasProjects());
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has projects.
     */
    public void setProjects(boolean projects)
    {
        this.projects = projects;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has projects.
     */
    public void setProjectsObject(Boolean projects)
    {
        setProjects(projects != null && projects.booleanValue());
    }

    /**
     * Returns the organisation's github address.
     */
    public String getGitHub()
    {
        return github;
    }

    /**
     * Sets the organisation's github address.
     */
    public void setGitHub(String github)
    {
        this.github = github;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a github address.
     */
    public boolean hasGitHub()
    {
        return github != null && github.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has tools.
     */
    public boolean hasTools()
    {
        return tools;
    }

    /**
     * Returns <CODE>true</CODE> if this organisation has tools.
     */
    public Boolean getToolsObject()
    {
        return Boolean.valueOf(hasTools());
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has tools.
     */
    public void setTools(boolean tools)
    {
        this.tools = tools;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has tools.
     */
    public void setToolsObject(Boolean tools)
    {
        setTools(tools != null && tools.booleanValue());
    }

    /**
     * Returns the organisation's alternatives address.
     */
    public String getAlternatives()
    {
        return alternatives;
    }

    /**
     * Sets the organisation's alternatives address.
     */
    public void setAlternatives(String alternatives)
    {
        this.alternatives = alternatives;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has an alternatives address.
     */
    public boolean hasAlternatives()
    {
        return alternatives != null && alternatives.length() > 0;
    }

    /**
     * Returns the organisation's features.
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
     * Sets the organisation's features.
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
}