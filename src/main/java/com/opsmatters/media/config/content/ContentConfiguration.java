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

package com.opsmatters.media.config.content;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.sql.SQLException;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.cache.organisation.OrganisationSites;
import com.opsmatters.media.cache.content.ContentConfigurations;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.config.YamlConfiguration;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.FeedsConfig;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.EnvironmentName;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.organisation.OrganisationStatus;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.content.OrganisationListing;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.handler.ContentHandler;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.util.FileUtils;

/**
 * Class that represents a YAML configuration for content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentConfiguration<C extends ContentItem> extends YamlConfiguration implements FieldSource
{
    private static final Logger logger = Logger.getLogger(ContentConfiguration.class.getName());

    public static final String FILENAME = "filename";
    public static final String SHEET = "sheet";
    public static final String SOURCE = "source";
    public static final String DEFAULT_DATE_PATTERN = "default-date-pattern";
    public static final String TRAILING_SLASH = "trailing-slash";
    public static final String SUMMARY = "summary";
    public static final String FIELDS = "fields";
    public static final String OUTPUT = "output";

    private String filename = "";
    private String sheet = "";
    private ContentSource source = ContentSource.STORE;
    private String defaultDatePattern = "";
    private boolean trailingSlash = false;
    private SummaryConfiguration summary; 
    private Fields fields;
    private Map<String,String> output;

    /**
     * Default constructor.
     */
    public ContentConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setFilename(obj.getFilename());
            setSheet(obj.getSheet());
            setSource(obj.getSource());
            setDefaultDatePattern(obj.getDefaultDatePattern());
            setTrailingSlash(obj.hasTrailingSlash());
            if(obj.getSummary() != null)
                setSummary(new SummaryConfiguration(obj.getSummary()));
            setFields(new Fields(obj.getFields()));
            setOutput(new LinkedHashMap<String,String>(obj.getOutput()));
        }
    }

    /**
     * Returns the type for this configuration.
     */
    public ContentType getType()
    {
        return null;
    }

    /**
     * Returns the organisation code for this configuration.
     */
    public String getCode()
    {
        return fields.get(Fields.CODE);
    }

    /**
     * Returns the import filename for this configuration.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the import filename for this configuration.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the worksheet for this configuration.
     */
    public String getSheet()
    {
        return sheet;
    }

    /**
     * Sets the worksheet for this configuration.
     */
    public void setSheet(String sheet)
    {
        this.sheet = sheet;
    }

    /**
     * Returns the default date pattern for this configuration.
     */
    public String getDefaultDatePattern()
    {
        return defaultDatePattern;
    }

    /**
     * Sets the default date pattern for this configuration.
     */
    public void setDefaultDatePattern(String defaultDatePattern)
    {
        this.defaultDatePattern = defaultDatePattern;
    }

    /*
     * Returns <CODE>true</CODE> if the content URL should have a trailing slash.
     */
    public boolean hasTrailingSlash()
    {
        return trailingSlash;
    }

    /**
     * Set to <CODE>true</CODE> if the content URL should have a trailing slash.
     */
    public void setTrailingSlash(boolean trailingSlash)
    {
        this.trailingSlash = trailingSlash;
    }

    /**
     * Returns the content source for this configuration.
     */
    public ContentSource getSource()
    {
        return source;
    }

    /**
     * Sets the content source for this configuration.
     */
    public void setSource(ContentSource source)
    {
        this.source = source;
    }

    /**
     * Returns the summary configuration.
     */
    public SummaryConfiguration getSummary()
    {
        return summary;
    }

    /**
     * Sets the summary configuration.
     */
    public void setSummary(SummaryConfiguration summary)
    {
        this.summary = summary;
    }

    /**
     * Returns the fields for this configuration.
     */
    @Override
    public Fields getFields()
    {
        return fields;
    }

    /**
     * Sets the fields for this configuration.
     */
    public void setFields(Fields fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Map<String,String> fields)
    {
        if(this.fields == null)
            this.fields = new Fields();
        this.fields.putAll(fields);
    }

    /**
     * Returns <CODE>true</CODE> if given field has been set.
     */
    public boolean hasField(String name)
    {
        return fields != null ? fields.containsKey(name) : false;
    }

    /**
     * Returns the value of the given field.
     */
    public String getField(String name)
    {
        return fields != null ? fields.get(name) : null;
    }

    /**
     * Returns the value of the given field.
     * <p>
     * Returns the fallback if the field is not found.
     */
    public String getField(String name, String fallback)
    {
        return fields != null ? fields.get(name, fallback) : null;
    }

    /**
     * Returns the output fields for this configuration.
     */
    public Map<String,String> getOutput()
    {
        return output;
    }

    /**
     * Sets the output fields for this configuration.
     */
    public void setOutput(Map<String,String> output)
    {
        this.output = output;
    }

    /**
     * Adds the output fields for this configuration.
     */
    public void addOutput(Map<String,String> output)
    {
        if(this.output == null)
            this.output = new LinkedHashMap<String,String>();
        this.output.putAll(output);
    }

    /**
     * Returns the list of HTML fields that need to be escaped.
     */
    public String[] getHtmlFields()
    {
        return new String[] {"body"};
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        if(map.containsKey(FILENAME))
            setFilename((String)map.get(FILENAME));
        if(map.containsKey(SHEET))
            setSheet((String)map.get(SHEET));
        if(map.containsKey(DEFAULT_DATE_PATTERN))
            setDefaultDatePattern((String)map.get(DEFAULT_DATE_PATTERN));
        if(map.containsKey(TRAILING_SLASH))
            setTrailingSlash((Boolean)map.get(TRAILING_SLASH));
        if(map.containsKey(SOURCE))
            setSource(ContentSource.fromCode((String)map.get(SOURCE)));
        if(map.containsKey(FIELDS))
            addFields((Map<String,String>)map.get(FIELDS));
        if(map.containsKey(OUTPUT))
            addOutput((Map<String,String>)map.get(OUTPUT));

        if(map.containsKey(SUMMARY))
        {
            summary = new SummaryConfiguration(getName());
            summary.parseDocument((Map<String,Object>)map.get(SUMMARY));
        }
    }

    /**
     * Extract the list of content items from the database and deploy using the given handler.
     */
    public List<C> deployContent(Site site, EnvironmentName env, Environment images, ContentDAO contentDAO, ContentHandler handler)
        throws IOException, SQLException
    {
        int idx = 0;
        int startIdx = -1;
        List<C> items = contentDAO.list(site, getCode());
        for(C content : items)
        {
            if(content.isSkipped())
                continue;

            ++idx;
            ContentStatus status = content.getStatus();
            Fields fields = content.toFields().add(this);

            Organisation organisation = Organisations.get(content.getCode());
            OrganisationSite organisationSite = OrganisationSites.get(site, content.getCode());
            if(organisation != null && organisation.hasTracking())
                fields.put(Fields.TRACKING, organisation.getTracking());

            // Add the Organisation fields
            if(content instanceof OrganisationListing)
            {
                // Ignore missing and archived organisations
                if(organisation == null || organisationSite.isArchived())
                {
                    --idx;
                    continue;
                }

                // Set the published flag based on the environment and status
                boolean published = false;
                if(env == EnvironmentName.PROD)
                    published = organisationSite.isActive();
                else
                    published = organisationSite.isReview() || organisationSite.isActive();
                fields.put(Fields.PUBLISHED, published ? "1" : "0");
                fields.add(ContentConfigurations.get(organisation.getCode()));

                // Add the path to the organisation thumbnail and logo
                boolean missing = false;
                ContentImage thumbnail = ContentImages.get(ImageType.THUMBNAIL, content.getCode());
                if(thumbnail.isActive())
                {
                    fields.put(Fields.THUMBNAIL, String.format("%s%s/%s",
                        images.getUrl(), thumbnail.getType().path(), thumbnail.getFilename()));
                    fields.put(Fields.THUMBNAIL_TEXT, thumbnail.getText());
                }
                else
                {
                    thumbnail = null;
                    missing = true;
                }

                ContentImage logo = ContentImages.get(ImageType.LOGO, content.getCode());
                if(logo.isActive())
                {
                    fields.put(Fields.IMAGE, String.format("%s%s/%s",
                        images.getUrl(), logo.getType().path(), logo.getFilename()));
                    fields.put(Fields.IMAGE_TEXT, logo.getText());
                }
                else
                {
                    logo = null;
                    missing = true;
                }

                if(missing)
                {
                    logger.severe(String.format("Organisation %s has missing images: thumbnail=%s, logo=%s",
                        content.getCode(), thumbnail, logo));
                }
            }
            else
            {
                // Add the path to the organisation thumbnail
                ContentImage thumbnail = ContentImages.get(ImageType.THUMBNAIL, content.getCode());
                if(thumbnail.isActive())
                {
                    fields.put(Fields.THUMBNAIL, String.format("%s%s/%s",
                        images.getUrl(), thumbnail.getType().path(), thumbnail.getFilename()));
                    fields.put(Fields.THUMBNAIL_TEXT, thumbnail.getText());
                }

                // Add the path to the image if present
                String image = fields.get(Fields.IMAGE);
                if(image != null && image.length() > 0)
                {
                    fields.put(Fields.IMAGE, String.format("%s%s/%s",
                        images.getUrl(), System.getProperty("app.path.images"), image));
                }

                // Allow for posts with no listing
                if(!organisationSite.hasListing())
                {
                    fields.put(Fields.ORGANISATION, "");
                    fields.put(Fields.IMAGE_TEXT, "");
                }

                // Check if the URL needs a trailing slash (to avoid redirects)
                if(hasTrailingSlash() && fields.containsKey(Fields.URL))
                {
                    String url = fields.get(Fields.URL);
                    if(url != null && url.length() > 0 && !url.endsWith("/"))
                        fields.put(Fields.URL, url+"/");
                }
            }

            fields.add(organisation, organisationSite);
            handler.appendLine(handler.getValues(fields));

            if(content.getStatus() != ContentStatus.DEPLOYED)
            {
                if(env == EnvironmentName.STAGE)
                    content.setStatus(ContentStatus.STAGED);
                else if(content.getStatus() == ContentStatus.STAGED)
                    content.setStatus(ContentStatus.DEPLOYED);
            }

            if(content.getStatus() != status)
            {
                contentDAO.update(content);
                if(startIdx < 0)
                    startIdx = idx;
            }
        }

        // Process the import file
        handler.writeFile();
        handler.copyFileToBucket(site.getS3Config().getContentBucket());
        handler.deleteFile();

        // Process the CSV file
        String type = getType().tag();
        handler.setFilename(handler.getCsvFilename());
        if(startIdx > 0)
            handler.trimLines(startIdx);
        handler.convertLinesToAscii(getHtmlFields());
        handler.writeFile();

        // Upload the csv file to the environment
        Environment environment = site.getEnvironment(env);
        String path = environment.getFeedsConfig().getPath()
            +System.getProperty("app.files.feeds."+type);
        handler.copyFileToHost(path, environment);

        handler.deleteFile();

        return items;
    }
}