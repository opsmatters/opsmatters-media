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
package com.opsmatters.media.model.content.project;

import java.util.Calendar;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.provider.RepositoryProviderId;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Resource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;
import com.opsmatters.media.util.SessionDate;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing an open source project.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Project extends Resource<ProjectDetails>
{
    /**
     * Default constructor.
     */
    public Project()
    {
        setDetails(new ProjectDetails());
    }

    /**
     * Constructor that takes a project.
     */
    public Project(Project obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Project(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Project obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Project(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

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
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(URL.value(), getUrl());
        ret.putOpt(BADGES.value(), getBadges());
        ret.putOpt(LINKS.value(), getLinks());
        ret.putOpt(FOUNDED.value(), getFounded());
        ret.putOpt(LICENSE.value(), getLicense());
        ret.putOpt(WEBSITE.value(), getWebsite());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setUrl(obj.optString(URL.value()));
        setBadges(obj.optString(BADGES.value()));
        setLinks(obj.optString(LINKS.value()));
        setFounded(obj.optString(FOUNDED.value()));
        setLicense(obj.optString(LICENSE.value()));
        setWebsite(obj.optString(WEBSITE.value()));
    }

    /**
     * Returns the set of output fields from the project.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(URL, getUrl());
        ret.put(BADGES, getBadges());
        ret.put(LINKS, getLinks());
        ret.put(FOUNDED, getFounded());
        ret.put(LICENSE, getLicense());
        ret.put(WEBSITE, getWebsite());

        return ret;
    }

    /**
     * Returns a new project with defaults.
     */
    public static Project getDefault(Organisation organisation, OrganisationSite organisationSite, ProjectConfig config)
        throws DateTimeParseException
    {
        Project project = new Project();

        project.init();
        project.setSiteId(organisationSite.getSiteId());
        project.setTitle("New Project");
        project.setDescription(StringUtils.EMPTY);
        project.setPublishedDateAsString(TimeUtils.toStringUTC(SessionDate.get(),
            config.getField(PUBLISHED_DATE)));
        project.setFounded(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));

        return project;
    }

    /**
     * Use the given configuration to set defaults for the project.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, ProjectConfig config)
    {
        super.init(organisation, organisationSite, config);

        if(!hasWebsite())
            setWebsite(organisation.getWebsite());
    }

    /**
     * Prepare the fields in the project using the given configuration.
     */
    public void prepare(ProjectConfig config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        formatSummary(config, false, debug);
    }

    /**
     * Format the project body and summary.
     */
    public void formatSummary(ProjectConfig config, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(getType()));
        }
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
     * Sets the project details from a teaser.
     */
    @Override
    public void setTeaserDetails(ProjectDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setUrl(new String(obj.getUrl()), false);
            setFounded(new String(obj.getFounded() != null ? obj.getFounded() : ""));
            setLicense(new String(obj.getLicense() != null ? obj.getLicense() : ""));
        }
    }

    /**
     * Sets the project details.
     */
    @Override
    public void setContentDetails(ProjectDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setBadges(new String(obj.getBadges() != null ? obj.getBadges() : ""));
            setLinks(new String(obj.getLinks() != null ? obj.getLinks() : ""));
            setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
            setConfigured(true);
        }
    }

    /**
     * Returns the provider of the project.
     */
    public RepositoryProviderId getProviderId()
    {
        return RepositoryProviderId.fromUrl(getUrl());
    }

    /**
     * Returns the project badges.
     */
    public String getBadges()
    {
        return getDetails().getBadges();
    }

    /**
     * Sets the project badges.
     */
    public void setBadges(String badges)
    {
        getDetails().setBadges(badges);
    }

    /**
     * Returns the project links.
     */
    public String getLinks()
    {
        return getDetails().getLinks();
    }

    /**
     * Sets the project links.
     */
    public void setLinks(String links)
    {
        getDetails().setLinks(links);
    }

    /**
     * Returns <CODE>true</CODE> if the project links have been set.
     */
    public boolean hasLinks()
    {
        return getDetails().hasLinks();
    }

    /**
     * Returns the project website.
     */
    public String getWebsite()
    {
        return getDetails().getWebsite();
    }

    /**
     * Sets the project website.
     */
    public void setWebsite(String website)
    {
        getDetails().setWebsite(website);
    }

    /**
     * Returns <CODE>true</CODE> if the project website has been set.
     */
    public boolean hasWebsite()
    {
        return getDetails().hasWebsite();
    }

    /**
     * Returns the project founded year.
     */
    public String getFounded()
    {
        return getDetails().getFounded();
    }

    /**
     * Sets the project founded year.
     */
    public void setFounded(String founded)
    {
        getDetails().setFounded(founded);
    }

    /**
     * Returns the project license.
     */
    public String getLicense()
    {
        return getDetails().getLicense();
    }

    /**
     * Sets the project license.
     */
    public void setLicense(OpenSourceLicense license)
    {
        setLicense(license != null ? license.value() : "");
    }

    /**
     * Sets the project license.
     */
    public void setLicense(String license)
    {
        getDetails().setLicense(license);
    }
}