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
package com.opsmatters.media.model.content.job;

import java.util.List;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.Resource;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSettings;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class representing a job.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class Job extends Resource<JobDetails>
{
    private String technologies = "";

    /**
     * Default constructor.
     */
    public Job()
    {
        setDetails(new JobDetails());
    }

    /**
     * Constructor that takes a job.
     */
    public Job(Job obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a site and code.
     */
    public Job(Site site, String code)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(Job obj)
    {
        super.copyAttributes(obj);
        setContentDetails(obj.getDetails());
        setTechnologies(new String(obj.getTechnologies() != null ? obj.getTechnologies() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public Job(Site site, String code, String[] values) throws DateTimeParseException
    {
        this();
        init();

        setSiteId(site.getId());

        String id = values[0];
        String pubdate = values[1];
        String title = values[2];
        String summary = values[3];
        String description = values[4];
        String url = values[5];
        String linkText = values[6];
        String organisation = values[7];
        String website = values[8];
        String location = values[9];
        String package_ = values[10];
        String technologies = values[11];
        String contact = values[12];
        String thumbnail = values[13];
        String thumbnailText = values[14];
        String thumbnailTitle = values[15];
        String createdBy = values[16];
        String published = values[17];
        String promote = values[18];

        setCode(code);
        setId(Integer.parseInt(id.substring(id.lastIndexOf("-")+1)));
        setPublishedDateAsString(pubdate);
        setTitle(title);
        setSummary(summary);
        setDescription(description);
        setUrl(url);
        setLinkText(linkText);
        setWebsite(website);
        setLocation(location);
        setPackage(package_);
        setTechnologies(technologies);
        setContact(contact);
        setCreatedBy(createdBy);
        setPublished(published != null && published.equals("1"));
        setPromoted(promote != null && promote.equals("1"));
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = super.getAttributes();

        ret.putOpt(WEBSITE.value(), getWebsite());
        ret.putOpt(LOCATION.value(), getLocation());
        ret.putOpt(PACKAGE.value(), getPackage());
        ret.putOpt(TECHNOLOGIES.value(), getTechnologies());
        ret.putOpt(CONTACT.value(), getContact());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        super.setAttributes(obj);

        setWebsite(obj.optString(WEBSITE.value()));
        setLocation(obj.optString(LOCATION.value()));
        setPackage(obj.optString(PACKAGE.value()));
        setTechnologies(obj.optString(TECHNOLOGIES.value()));
        setContact(obj.optString(CONTACT.value()));
    }

    /**
     * Returns the set of output fields from the job.
     */
    @Override
    public FieldMap toFields()
    {
        FieldMap ret = super.toFields();

        ret.put(WEBSITE, getWebsite());
        ret.put(LOCATION, getLocation());
        ret.put(PACKAGE, getPackage());
        ret.put(TECHNOLOGIES, getTechnologies());
        ret.put(CONTACT, getContact());

        return ret;
    }

    /**
     * Returns a new job with defaults.
     */
    public static Job getDefault(Organisation organisation, OrganisationSite organisationSite, JobConfig config)
        throws DateTimeParseException
    {
        Job job = new Job();

        job.init();
        job.setSiteId(organisationSite.getSiteId());
        job.setTitle("New Job");
        job.setDescription(StringUtils.EMPTY);
        job.setPublishedDateAsString(TimeUtils.toStringUTC(config.getField(PUBLISHED_DATE)));

        return job;
    }

    /**
     * Use the given configuration to set defaults for the job.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, JobConfig config)
    {
        super.init(organisation, organisationSite, config);

        setWebsite(organisation.getWebsite());

        if(organisationSite != null)
        {
            ContentSettings settings = organisationSite.getContentSettings(getType());
            if(settings != null)
                setTechnologies(settings.getTechnologies());
        }

        setLinkText(config.getField(LINK_TEXT, ""));

        String promote = config.getField(PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);
    }

    /**
     * Prepare the fields in the job using the given configuration.
     */
    public void prepare(JobConfig config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getField(PUBLISHED_DATE)));

        formatSummary(config, false, debug);

        // Use the default location if a job location wasn't found
        if(config.hasField(LOCATION) && getLocation().length() == 0)
            setLocation(config.getField(LOCATION));
    }

    /**
     * Format the job body and summary.
     */
    public void formatSummary(JobConfig config, boolean force, boolean debug)
    {
        if(hasDescription())
        {
            BodyParser parser = new BodyParser(getDescription(), debug);
            if(parser.converted())
                setDescription(parser.formatBody());
            if(getSummary().length() == 0 || force)
                setSummary(parser.formatSummary(config.getSummary()));
        }
    }

    /**
     * Returns the content type.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.JOB;
    }

    /**
     * Sets the job details from a teaser.
     */
    @Override
    public void setTeaserDetails(JobDetails obj)
    {
        super.setTeaserDetails(obj);

        if(obj != null)
        {
            setUrl(new String(obj.getUrl()));
            setLocation(new String(obj.getLocation() != null ? obj.getLocation() : ""));
            setPackage(new String(obj.getPackage() != null ? obj.getPackage() : ""));
        }
    }

    /**
     * Sets the job details.
     */
    @Override
    public void setContentDetails(JobDetails obj)
    {
        if(obj != null)
        {
            setTeaserDetails(obj);
            setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
            setContact(new String(obj.getContact() != null ? obj.getContact() : ""));
            setConfigured(true);
        }
    }

    /**
     * Returns the job website.
     */
    public String getWebsite()
    {
        return getDetails().getWebsite();
    }

    /**
     * Sets the job website.
     */
    public void setWebsite(String website)
    {
        getDetails().setWebsite(website);
    }

    /**
     * Returns <CODE>true</CODE> if the job website has been set.
     */
    public boolean hasWebsite()
    {
        return getDetails().hasWebsite();
    }

    /**
     * Sets the URL of the job.
     */
    @Override
    public void setUrl(String url)
    {
        setUrl(url, true);
    }

    /**
     * Returns the job location.
     */
    public String getLocation()
    {
        return getDetails().getLocation();
    }

    /**
     * Sets the job location.
     */
    public void setLocation(String location)
    {
        getDetails().setLocation(location);
    }

    /**
     * Returns <CODE>true</CODE> if the job location has been set.
     */
    public boolean hasLocation()
    {
        return getDetails().hasLocation();
    }

    /**
     * Returns the job package.
     */
    public String getPackage()
    {
        return getDetails().getPackage();
    }

    /**
     * Sets the job package.
     */
    public void setPackage(String package_)
    {
        getDetails().setPackage(package_);
    }

    /**
     * Returns <CODE>true</CODE> if the job package has been set.
     */
    public boolean hasPackage()
    {
        return getDetails().hasPackage();
    }

    /**
     * Returns the job contact.
     */
    public String getContact()
    {
        return getDetails().getContact();
    }

    /**
     * Sets the job contact.
     */
    public void setContact(String contact)
    {
        getDetails().setContact(contact);
    }

    /**
     * Returns <CODE>true</CODE> if the job contact has been set.
     */
    public boolean hasContact()
    {
        return getDetails().hasContact();
    }

    /**
     * Returns the technologies.
     */
    public String getTechnologies()
    {
        return technologies;
    }

    /**
     * Returns the list of technologies.
     */
    public List<String> getTechnologiesList()
    {
        return StringUtils.toList(getTechnologies());
    }

    /**
     * Sets the technologies.
     */
    public void setTechnologies(String technologies)
    {
        this.technologies = technologies;
    }

    /**
     * Sets the list of technologies.
     */
    public void setTechnologiesList(List<String> technologies)
    {
        setTechnologies(StringUtils.fromList(technologies));
    }
}