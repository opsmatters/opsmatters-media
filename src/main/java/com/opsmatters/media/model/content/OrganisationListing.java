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
import java.util.Calendar;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.OrganisationListingConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.TimeUtils;

/**
 * Class representing an organisation's listing.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationListing extends ContentItem
{
    private Instant publishedDate;
    private String title = "";
    private OrganisationTabs tabs = OrganisationTabs.ALL;
    private int content = 0;
    private String summary = "";
    private String description = "";
    private String advert = "";
    private String founded = "";
    private String location = "";
    private String stockSymbol = "";
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
    private String tags = "";
    private String image = "";
    private String imageText = "";

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
        setAdvert(new String(obj.getAdvert() != null ? obj.getAdvert() : ""));
        setFounded(new String(obj.getFounded() != null ? obj.getFounded() : ""));
        setLocation(new String(obj.getLocation() != null ? obj.getLocation() : ""));
        setStockSymbol(new String(obj.getStockSymbol() != null ? obj.getStockSymbol() : ""));
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
        setTags(new String(obj.getTags() != null ? obj.getTags() : ""));
        setImage(new String(obj.getImage() != null ? obj.getImage() : ""));
        setImageText(new String(obj.getImageText() != null ? obj.getImageText() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public OrganisationListing(String[] values) throws DateTimeParseException
    {
        init();

        String id = values[0];
        String pubdate = values[1];
        String code = values[2];
        String title = values[3];
        String sponsor = values[4];
        String tabs = values[5];
        String content = values[6]; // not used
        String summary = values[7];
        String description = values[8];
        String advert = values[9];
        String founded = values[10];
        String location = values[11];
        String stockSymbol = values[12];
        String website = values[13];
        String email = values[15];
        String facebook = values[16];
        String facebookUsername = values[17];
        String twitter = values[18];
        String twitterUsername = values[19];
        String linkedin = values[20];
        String instagram = values[21];
        String youtube = values[22];
        String vimeo = values[23];
        String projects = values[24];
        String github = values[25];
        String tools = values[26];
        String alternatives = values[27];
        String features = values[28];
        String tags = values[29];
        String hashtag = values[30];
        String image = values[31];
        String imageText = values[32];
        String imageTitle = values[33]; // not used
        String thumbnail = values[34];
        String thumbnailText = values[35];
        String thumbnailTitle = values[36]; // not used
        String createdBy = values[37];
        String published = values[38];

        // Remove feeds path from images
        if(image.indexOf("/") != -1)
            image = image.substring(image.lastIndexOf("/")+1);
        if(thumbnail.indexOf("/") != -1)
            thumbnail = thumbnail.substring(thumbnail.lastIndexOf("/")+1);

        setId(Integer.parseInt(id.substring(id.indexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setCode(code);
        setTitle(title);
        //setSponsor(sponsor != null && sponsor.equals("1"));
        setTabs(OrganisationTabs.valueOf(tabs));
        setSummary(summary);
        setDescription(description);
        setAdvert(advert);
        setFounded(founded);
        setLocation(location);
        setStockSymbol(stockSymbol);
        //setWebsite(website);
        //setEmail(email);
        setFacebook(facebook);
        //setFacebookUsername(facebookUsername);
        setTwitter(twitter);
        //setTwitterUsername(twitterUsername);
        setLinkedIn(linkedin);
        setInstagram(instagram);
        setYouTube(youtube);
        setVimeo(vimeo);
        setProjects(projects != null && projects.equals("1"));
        setGitHub(github);
        setTools(tools != null && tools.equals("1"));
        setAlternatives(alternatives);
        setFeatures(features);
        setTags(tags);
        //setHashtag(hashtag);
        setImage(image);
        setImageText(imageText);
        //setThumbnail(thumbnail);
        //setThumbnailText(thumbnailText);
        setCreatedBy(createdBy);
        //setPublished(published != null && published.equals("1"));
    }

    /**
     * Constructor that takes a JSON object.
     */
    public OrganisationListing(JSONObject obj)
    {
        fromJson(obj);
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setTabs(OrganisationTabs.valueOf(obj.optString(Fields.TABS)));
        setDescription(obj.optString(Fields.DESCRIPTION));
        setAdvert(obj.optString(Fields.ADVERT));
        setFounded(obj.optString(Fields.FOUNDED));
        setLocation(obj.optString(Fields.LOCATION));
        setStockSymbol(obj.optString(Fields.STOCK_SYMBOL));
        setFacebook(obj.optString(Fields.FACEBOOK));
        setTwitter(obj.optString(Fields.TWITTER));
        setLinkedIn(obj.optString(Fields.LINKEDIN));
        setInstagram(obj.optString(Fields.INSTAGRAM));
        setYouTube(obj.optString(Fields.YOUTUBE));
        setVimeo(obj.optString(Fields.VIMEO));
        setProjects(obj.optBoolean(Fields.PROJECTS, false));
        setGitHub(obj.optString(Fields.GITHUB));
        setTools(obj.optBoolean(Fields.TOOLS, false));
        setAlternatives(obj.optString(Fields.ALTERNATIVES));
        setFeatures(obj.optString(Fields.FEATURES));
        setTags(obj.optString(Fields.TAGS));
        setImage(obj.optString(Fields.IMAGE));
        setImageText(obj.optString(Fields.IMAGE_TEXT));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.put(Fields.TABS, getTabs().name());
        ret.put(Fields.CONTENT, getContent());
        ret.putOpt(Fields.DESCRIPTION, getDescription());
        ret.putOpt(Fields.ADVERT, getAdvert());
        ret.putOpt(Fields.FOUNDED, getFounded());
        ret.putOpt(Fields.LOCATION, getLocation());
        ret.putOpt(Fields.STOCK_SYMBOL, getStockSymbol());
        ret.putOpt(Fields.FACEBOOK, getFacebook());
        ret.putOpt(Fields.TWITTER, getTwitter());
        ret.putOpt(Fields.LINKEDIN, getLinkedIn());
        ret.putOpt(Fields.INSTAGRAM, getInstagram());
        ret.putOpt(Fields.YOUTUBE, getYouTube());
        ret.putOpt(Fields.VIMEO, getVimeo());
        ret.put(Fields.PROJECTS, hasProjects());
        ret.putOpt(Fields.GITHUB, getGitHub());
        ret.put(Fields.TOOLS, hasTools());
        ret.putOpt(Fields.ALTERNATIVES, getAlternatives());
        ret.putOpt(Fields.FEATURES, getFeatures());
        ret.putOpt(Fields.TAGS, getTags());
        ret.putOpt(Fields.IMAGE, getImage());
        ret.putOpt(Fields.IMAGE_TEXT, getImageText());

        return ret;
    }

    /**
     * Returns the set of output fields from the organisation listing.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.CODE, getCode());
        ret.put(Fields.TABS, getTabs().name());
        ret.put(Fields.CONTENT, Integer.toString(getTabs().value()));
        ret.put(Fields.DESCRIPTION, getDescription());
        ret.put(Fields.ADVERT, getAdvert());
        ret.put(Fields.FOUNDED, getFounded());
        ret.put(Fields.LOCATION, getLocation());
        ret.put(Fields.STOCK_SYMBOL, getStockSymbol());
        ret.put(Fields.FACEBOOK, getFacebook());
        ret.put(Fields.TWITTER, getTwitter());
        ret.put(Fields.LINKEDIN, getLinkedIn());
        ret.put(Fields.INSTAGRAM, getInstagram());
        ret.put(Fields.YOUTUBE, getYouTube());
        ret.put(Fields.VIMEO, getVimeo());
        ret.put(Fields.PROJECTS, hasProjects() ? "1" : "0");
        ret.put(Fields.GITHUB, getGitHub());
        ret.put(Fields.TOOLS, hasTools() ? "1" : "0");
        ret.put(Fields.ALTERNATIVES, getAlternatives());
        ret.put(Fields.FEATURES, getFeatures());
        ret.put(Fields.TAGS, getTags());
        ret.put(Fields.IMAGE, getImage());
        ret.put(Fields.IMAGE_TEXT, getImageText());

        return ret;
    }

    /**
     * Returns a new organisation listing with defaults.
     */
    public static OrganisationListing getDefault(Organisation organisation, OrganisationListingConfiguration config)
        throws DateTimeParseException
    {
        OrganisationListing listing = new OrganisationListing();

        listing.init();
        listing.setCode(organisation.getCode());
        listing.setTitle(organisation.getName());
        listing.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        listing.setFounded(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
        listing.setImage(String.format("%s-thumb.png", listing.getTitle()));
        listing.setImageText(String.format("%s logo", listing.getTitle().replaceAll("-", " ")));

        return listing;
    }

    /**
     * Use the given configuration to set defaults for the organisation listing.
     */
    public void init(OrganisationListingConfiguration config)
    {
        super.init(config);

        setTabs(OrganisationTabs.valueOf(config.getField(Fields.TABS, "ALL")));

        String projects = config.getField(Fields.PROJECTS);
        setProjects(projects == null || projects.equals("0") ? false : true);

        String tools = config.getField(Fields.TOOLS);
        setTools(tools == null || tools.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the organisation listing using the given configuration.
     */
    public void prepare(OrganisationListingConfiguration config) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));
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
        setContent(getTabs() != null ? getTabs().value() : 0);
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
     * Returns the organisation's advert.
     */
    public String getAdvert()
    {
        return advert;
    }

    /**
     * Sets the organisation's advert.
     */
    public void setAdvert(String advert)
    {
        this.advert = advert;
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
        return new Boolean(hasProjects());
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
        return new Boolean(hasTools());
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

    /**
     * Returns the organisation's tags.
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
     * Sets the organisation's tags.
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
     * Returns the organisation's logo image.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Sets the organisation's logo image.
     */
    public void setImage(String image)
    {
        this.image = image;
    }

    /**
     * Set to <CODE>true</CODE> if this organisation has a logo image.
     */
    public boolean hasImage()
    {
        return image != null && image.length() > 0;
    }

    /**
     * Returns the organisation's logo image text.
     */
    public String getImageText()
    {
        return imageText;
    }

    /**
     * Sets the organisation's logo image text.
     */
    public void setImageText(String imageText)
    {
        this.imageText = imageText;
    }
}