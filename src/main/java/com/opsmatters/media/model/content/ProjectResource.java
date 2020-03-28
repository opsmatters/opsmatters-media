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

import java.util.Calendar;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.ProjectConfiguration;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a project resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectResource extends Resource
{
    private ProjectDetails details = new ProjectDetails();

    /**
     * Default constructor.
     */
    public ProjectResource()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes a project resource.
     */
    public ProjectResource(ProjectResource obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a project.
     */
    public ProjectResource(String code, ProjectDetails obj)
    {
        this();
        init();
        setCode(code);
        setProjectDetails(obj);
    }

    /**
     * Constructor that takes a project summary.
     */
    public ProjectResource(String code, ProjectSummary obj)
    {
        this();
        init();
        setCode(code);
        setContentSummary(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ProjectResource obj)
    {
        super.copyAttributes(obj);

        setProjectDetails(obj.getProjectDetails());
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public ProjectResource(String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String description = values[4];
        String badges = values[5];
        String links = values[6];
        String organisation = values[7];
        String founded = values[8];
        String license = values[9];
        String website = values[10];
        String url = values[11];
        String features = values[12];
        String thumbnail = values[13];
        String thumbnailText = values[14];
        String thumbnailTitle = values[15];
        String createdBy = values[16];
        String published = values[17];

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setBadges(badges);
        setLinks(links);
        setFounded(founded);
        setLicense(license);
        setWebsite(website);
        setUrl(url);
        setFeatures(features);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
    }

    /**
     * Constructor that takes a JSON object.
     */
    public ProjectResource(JSONObject obj)
    {
        this();
        fromJson(obj);
    }

    /**
     * Initialise this object using a JSON object.
     */
    public void fromJson(JSONObject obj)
    {
        super.fromJson(obj);

        setUrl(obj.optString(Fields.URL));
        setBadges(obj.optString(Fields.BADGES));
        setLinks(obj.optString(Fields.LINKS));
        setFounded(obj.optString(Fields.FOUNDED));
        setLicense(obj.optString(Fields.LICENSE));
        setWebsite(obj.optString(Fields.WEBSITE));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.URL, getUrl());
        ret.putOpt(Fields.BADGES, getBadges());
        ret.putOpt(Fields.LINKS, getLinks());
        ret.putOpt(Fields.FOUNDED, getFounded());
        ret.putOpt(Fields.LICENSE, getLicense());
        ret.putOpt(Fields.WEBSITE, getWebsite());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.URL, getUrl());
        ret.put(Fields.BADGES, getBadges());
        ret.put(Fields.LINKS, getLinks());
        ret.put(Fields.FOUNDED, getFounded());
        ret.put(Fields.LICENSE, getLicense());
        ret.put(Fields.WEBSITE, getWebsite());

        return ret;
    }

    /**
     * Returns a new resource with defaults.
     */
    public static ProjectResource getDefault(ProjectConfiguration config) throws DateTimeParseException
    {
        ProjectResource resource = new ProjectResource();

        resource.init();
        resource.setTitle("New Project");
        resource.setDescription(StringUtils.EMPTY);
        resource.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        resource.setFounded(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));

        return resource;
    }

    /**
     * Use the given organisation to set defaults for the resource.
     */
    public void init(Organisation organisation)
    {
        if(getWebsite().length() == 0)
            setWebsite(organisation.getWebsite());
    }

    /**
     * Use the given configuration to set defaults for the resource.
     */
    public void init(ProjectConfiguration config)
    {
        super.init(config);

        setFeatures(config.getField(Fields.FEATURES, ""));
        if(getLicense().length() == 0)
            setLicense(config.getField(Fields.LICENSE, ""));
    }

    /**
     * Prepare the fields in the resource using the given configuration.
     */
    public void prepare(ProjectConfiguration config) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));
        setDescription(FormatUtils.getFormattedDescription(getDescription()));
        if(getSummary().length() == 0)
            setSummary(FormatUtils.getFormattedSummary(getDescription()));
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.PROJECT;
    }

    /**
     * Returns the project details.
     */
    public ProjectDetails getProjectDetails()
    {
        return details;
    }

    /**
     * Sets the project details.
     */
    public void setProjectDetails(ProjectDetails obj)
    {
        setContentSummary(obj);
        setBadges(new String(obj.getBadges() != null ? obj.getBadges() : ""));
        setLinks(new String(obj.getLinks() != null ? obj.getLinks() : ""));
        setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
        setContentDetails(true);
    }

    /**
     * Sets the project details from a summary.
     */
    public void setContentSummary(ProjectSummary obj)
    {
        super.setContentSummary(obj);
        setUrl(new String(obj.getUrl()), false);
        setFounded(new String(obj.getFounded() != null ? obj.getFounded() : ""));
        setLicense(new String(obj.getLicense() != null ? obj.getLicense() : ""));
    }

    /**
     * Returns the provider of the project.
     */
    public RepositoryProvider getProvider()
    {
        return RepositoryProvider.fromUrl(getUrl());
    }

    /**
     * Returns the project badges.
     */
    public String getBadges()
    {
        return details.getBadges();
    }

    /**
     * Sets the project badges.
     */
    public void setBadges(String badges)
    {
        details.setBadges(badges);
    }

    /**
     * Returns the project links.
     */
    public String getLinks()
    {
        return details.getLinks();
    }

    /**
     * Sets the project links.
     */
    public void setLinks(String links)
    {
        details.setLinks(links);
    }

    /**
     * Returns <CODE>true</CODE> if the project links have been set.
     */
    public boolean hasLinks()
    {
        return details.hasLinks();
    }

    /**
     * Returns the project website.
     */
    public String getWebsite()
    {
        return details.getWebsite();
    }

    /**
     * Sets the project website.
     */
    public void setWebsite(String website)
    {
        details.setWebsite(website);
    }

    /**
     * Returns <CODE>true</CODE> if the project website has been set.
     */
    public boolean hasWebsite()
    {
        return details.hasWebsite();
    }

    /**
     * Returns the project founded year.
     */
    public String getFounded()
    {
        return details.getFounded();
    }

    /**
     * Sets the project founded year.
     */
    public void setFounded(String founded)
    {
        details.setFounded(founded);
    }

    /**
     * Returns the project license.
     */
    public String getLicense()
    {
        return details.getLicense();
    }

    /**
     * Sets the project license.
     */
    public void setLicense(RepositoryLicense license)
    {
        setLicense(license != null ? license.value() : "");
    }

    /**
     * Sets the project license.
     */
    public void setLicense(String license)
    {
        details.setLicense(license);
    }
}