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

import java.util.List;
import java.time.format.DateTimeParseException;
import org.json.JSONObject;
import com.opsmatters.media.config.content.JobConfiguration;
import com.opsmatters.media.config.content.Fields;
import com.opsmatters.media.crawler.parser.BodyParser;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.organisation.OrganisationContentType;
import com.opsmatters.media.util.FormatUtils;
import com.opsmatters.media.util.TimeUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a job resource.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class JobResource extends Resource
{
    private JobDetails details = new JobDetails();
    private String technologies = "";

    /**
     * Default constructor.
     */
    public JobResource()
    {
        setContentDetails(details);
    }

    /**
     * Constructor that takes a job resource.
     */
    public JobResource(JobResource obj)
    {
        this();
        copyAttributes(obj);
    }

    /**
     * Constructor that takes a job.
     */
    public JobResource(Site site, String code, JobDetails obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setJobDetails(obj);
    }

    /**
     * Constructor that takes a job summary.
     */
    public JobResource(Site site, String code, JobSummary obj)
    {
        this();
        init();
        setSiteId(site.getId());
        setCode(code);
        setContentSummary(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(JobResource obj)
    {
        super.copyAttributes(obj);

        setJobDetails(obj.getJobDetails());
        setTechnologies(new String(obj.getTechnologies() != null ? obj.getTechnologies() : ""));
    }

    /**
     * Constructor that takes a spreadsheet row.
     */
    public JobResource(Site site, String code, String[] values) throws DateTimeParseException
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
     * Constructor that takes a JSON object.
     */
    public JobResource(JSONObject obj)
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

        setWebsite(obj.optString(Fields.WEBSITE));
        setLocation(obj.optString(Fields.LOCATION));
        setPackage(obj.optString(Fields.PACKAGE));
        setTechnologies(obj.optString(Fields.TECHNOLOGIES));
        setContact(obj.optString(Fields.CONTACT));
    }

    /**
     * Returns this object as a JSON object.
     */
    public JSONObject toJson()
    {
        JSONObject ret = super.toJson();

        ret.putOpt(Fields.WEBSITE, getWebsite());
        ret.putOpt(Fields.LOCATION, getLocation());
        ret.putOpt(Fields.PACKAGE, getPackage());
        ret.putOpt(Fields.TECHNOLOGIES, getTechnologies());
        ret.putOpt(Fields.CONTACT, getContact());

        return ret;
    }

    /**
     * Returns the set of output fields from the resource.
     */
    @Override
    public Fields toFields()
    {
        Fields ret = super.toFields();

        ret.put(Fields.WEBSITE, getWebsite());
        ret.put(Fields.LOCATION, getLocation());
        ret.put(Fields.PACKAGE, getPackage());
        ret.put(Fields.TECHNOLOGIES, getTechnologies());
        ret.put(Fields.CONTACT, getContact());

        return ret;
    }

    /**
     * Returns a new resource with defaults.
     */
    public static JobResource getDefault(Organisation organisation, OrganisationSite organisationSite, JobConfiguration config)
        throws DateTimeParseException
    {
        JobResource resource = new JobResource();

        resource.init();
        resource.setSiteId(organisationSite.getSiteId());
        resource.setTitle("New Job");
        resource.setDescription(StringUtils.EMPTY);
        resource.setPublishedDateAsString(TimeUtils.toStringUTC(config.getDefaultDatePattern()));
        resource.setSocial(organisationSite.hasSocial());

        return resource;
    }

    /**
     * Use the given configuration to set defaults for the resource.
     */
    public void init(Organisation organisation, OrganisationSite organisationSite, JobConfiguration config)
    {
        super.init(organisation, organisationSite, config);

        setWebsite(organisation.getWebsite());

        if(organisationSite != null)
        {
            OrganisationContentType type = organisationSite.getContentType(getType());
            if(type != null)
                setTechnologies(type.getTechnologies());
        }

        setLinkText(config.getField(Fields.LINK_TEXT, ""));

        String promote = config.getField(Fields.PROMOTE);
        setPromoted(promote == null || promote.equals("0") ? false : true);

        setSocial(organisationSite.hasSocial());
    }

    /**
     * Prepare the fields in the resource using the given configuration.
     */
    public void prepare(JobConfiguration config, boolean debug) throws DateTimeParseException
    {
        setPublishedDateAsString(getPublishedDateAsString(config.getDefaultDatePattern()));

        BodyParser parser = new BodyParser(getDescription(), debug);
        if(parser.converted())
            setDescription(parser.formatBody());
        setSummary(parser.formatSummary(config.getSummary()));

        // Use the default location if a resource location wasn't found
        if(config.hasField(Fields.LOCATION) && getLocation().length() == 0)
            setLocation(config.getField(Fields.LOCATION));
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
     * Returns the job details.
     */
    public JobDetails getJobDetails()
    {
        return details;
    }

    /**
     * Sets the job details.
     */
    public void setJobDetails(JobDetails obj)
    {
        setContentSummary(obj);
        setWebsite(new String(obj.getWebsite() != null ? obj.getWebsite() : ""));
        setContact(new String(obj.getContact() != null ? obj.getContact() : ""));
        setContentDetails(true);
    }

    /**
     * Sets the job details from a summary.
     */
    public void setContentSummary(JobSummary obj)
    {
        super.setContentSummary(obj);
        setUrl(new String(obj.getUrl()));
        setLocation(new String(obj.getLocation() != null ? obj.getLocation() : ""));
        setPackage(new String(obj.getPackage() != null ? obj.getPackage() : ""));
    }

    /**
     * Returns the job website.
     */
    public String getWebsite()
    {
        return details.getWebsite();
    }

    /**
     * Sets the job website.
     */
    public void setWebsite(String website)
    {
        details.setWebsite(website);
    }

    /**
     * Returns <CODE>true</CODE> if the job website has been set.
     */
    public boolean hasWebsite()
    {
        return details.hasWebsite();
    }

    /**
     * Sets the URL of the resource.
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
        return details.getLocation();
    }

    /**
     * Sets the job location.
     */
    public void setLocation(String location)
    {
        details.setLocation(location);
    }

    /**
     * Returns <CODE>true</CODE> if the job location has been set.
     */
    public boolean hasLocation()
    {
        return details.hasLocation();
    }

    /**
     * Returns the job package.
     */
    public String getPackage()
    {
        return details.getPackage();
    }

    /**
     * Sets the job package.
     */
    public void setPackage(String package_)
    {
        details.setPackage(package_);
    }

    /**
     * Returns <CODE>true</CODE> if the job package has been set.
     */
    public boolean hasPackage()
    {
        return details.hasPackage();
    }

    /**
     * Returns the job contact.
     */
    public String getContact()
    {
        return details.getContact();
    }

    /**
     * Sets the job contact.
     */
    public void setContact(String contact)
    {
        details.setContact(contact);
    }

    /**
     * Returns <CODE>true</CODE> if the job contact has been set.
     */
    public boolean hasContact()
    {
        return details.hasContact();
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