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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.sql.SQLException;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.cache.organisation.OrganisationSites;
import com.opsmatters.media.cache.content.organisation.OrganisationContentConfigs;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.cache.platform.Environments;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.FeedsConfig;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.EnvironmentId;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.organisation.OrganisationStatus;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.content.organisation.OrganisationListing;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.model.feed.batch.FeedBatchOrganisation;
import com.opsmatters.media.handler.ContentHandler;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.util.FileUtils;

import static com.opsmatters.media.file.FileFormat.*;
import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class that represents a YAML configuration for content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentConfig<C extends Content> implements FieldSource, ConfigElement
{
    private static final Logger logger = Logger.getLogger(ContentConfig.class.getName());

    private String name = "";
    private String sheet = "";
    private ContentSource source = ContentSource.STORE;
    private SummaryConfig summary; 
    private FieldMap fields = new FieldMap();
    private Map<String,String> output;

    /**
     * Constructor that takes a name.
     */
    protected ContentConfig(String name)
    {
        setName(name);
    }

    /**
     * Copy constructor.
     */
    protected ContentConfig(ContentConfig obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ContentConfig obj)
    {
        if(obj != null)
        {
            setSheet(obj.getSheet());
            setSource(obj.getSource());
            if(obj.getSummary() != null)
                setSummary(new SummaryConfig(obj.getSummary()));
            setFields(new FieldMap(obj.getFields()));
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
     * Returns the name of this configuration.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this configuration.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the organisation code for this configuration.
     */
    public String getCode()
    {
        return fields.get(CODE);
    }

    /**
     * Returns the import filename for this configuration.
     */
    public String getFilename()
    {
        String ret = "";
        Organisation organisation = Organisations.get(getCode());
        if(organisation != null)
            ret = String.format("%s-%s.%s",
                organisation.getFilePrefix(), getType().tag(), XLSX.value());
        return ret;
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
    public SummaryConfig getSummary()
    {
        return summary;
    }

    /**
     * Sets the summary configuration.
     */
    public void setSummary(SummaryConfig summary)
    {
        this.summary = summary;
    }

    /**
     * Returns the fields for this configuration.
     */
    @Override
    public FieldMap getFields()
    {
        return fields;
    }

    /**
     * Sets the fields for this configuration.
     */
    public void setFields(FieldMap fields)
    {
        this.fields = fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Map<String,String> fields)
    {
        this.fields.putAll(fields);
    }

    /**
     * Returns <CODE>true</CODE> if given field has been set.
     */
    public boolean hasField(FieldName name)
    {
        return fields != null ? fields.containsKey(name) : false;
    }

    /**
     * Returns the value of the given field.
     */
    public String getField(FieldName name)
    {
        return fields != null ? fields.get(name) : null;
    }

    /**
     * Returns the value of the given field.
     * <p>
     * Returns the fallback if the field is not found.
     */
    public String getField(FieldName name, String fallback)
    {
        return fields != null ? fields.get(name, fallback) : null;
    }

    /**
     * Returns the name of the field to use for the content published date.
     */
    public String getPublishedDateField(boolean sponsor)
    {
        return getField(sponsor ? SPONSOR_PUBLISHED_DATE : PUBLISHED_DATE);
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
     * Extract the list of content items from the database and deploy using the given handler.
     */
    public void deployContent(FeedBatchOrganisation batchOrganisation, List<C> items, EnvironmentId env)
        throws IOException, SQLException
    {
        Site site = batchOrganisation.getSite();
        ContentDAO contentDAO = batchOrganisation.getDAO();
        Environment images = Environments.get(EnvironmentId.IMAGE);

        ContentHandler s3Handler = ContentHandler.builder()
            .useConfig(this)
            .withWorkingDirectory(System.getProperty("app.working"))
            .initFile()
            .build();

        ContentHandler csvHandler = ContentHandler.builder()
            .useConfig(this)
            .withWorkingDirectory(System.getProperty("app.working"))
            .initFile()
            .build();

        for(C content : items)
        {
            if(content.isSkipped())
                continue;

            ContentStatus status = content.getStatus();
            FieldMap fields = content.toFields().add(this);

            Organisation organisation = Organisations.get(content.getCode());
            OrganisationSite organisationSite = OrganisationSites.get(site, content.getCode());
            if(organisation != null && organisation.hasTracking())
                fields.put(TRACKING, organisation.getTracking());

            // Add the Organisation fields
            if(content instanceof OrganisationListing)
            {
                // Ignore missing and archived organisations
                if(organisation == null || organisationSite.isArchived())
                {
                    continue;
                }

                // Set the published flag based on the environment and status
                boolean published = false;
                if(env == EnvironmentId.PROD)
                    published = organisationSite.isActive();
                else
                    published = organisationSite.isReview() || organisationSite.isActive();
                fields.put(PUBLISHED, published ? "1" : "0");
                fields.add(OrganisationContentConfigs.get(organisation.getCode()));

                // Add the path to the organisation thumbnail and logo
                boolean missing = false;
                ContentImage thumbnail = ContentImages.get(ImageType.THUMBNAIL, content.getCode());
                if(thumbnail.isActive())
                {
                    fields.put(THUMBNAIL, String.format("%s%s/%s",
                        images.getUrl(), thumbnail.getType().path(), thumbnail.getFilename()));
                    fields.put(THUMBNAIL_TEXT, thumbnail.getText());
                }
                else
                {
                    thumbnail = null;
                    missing = true;
                }

                ContentImage logo = ContentImages.get(ImageType.LOGO, content.getCode());
                if(logo.isActive())
                {
                    fields.put(IMAGE, String.format("%s%s/%s",
                        images.getUrl(), logo.getType().path(), logo.getFilename()));
                    fields.put(IMAGE_TEXT, logo.getText());
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
                    fields.put(THUMBNAIL, String.format("%s%s/%s",
                        images.getUrl(), thumbnail.getType().path(), thumbnail.getFilename()));
                    fields.put(THUMBNAIL_TEXT, thumbnail.getText());
                }

                // Add the path to the image if present
                String image = fields.get(IMAGE);
                if(image != null && image.length() > 0)
                {
                    fields.put(IMAGE, String.format("%s%s/%s",
                        images.getUrl(), System.getProperty("app.path.images"), image));
                }

                // Allow for posts with no listing
                if(!organisationSite.hasListing())
                {
                    fields.put(ORGANISATION, "");
                    fields.put(IMAGE_TEXT, "");
                }

                // Further processing of the fields list from super-class
                processContentFields(fields);
            }

            fields.add(organisation, organisationSite);
            s3Handler.appendLine(s3Handler.getValues(fields));

            if(content.getStatus() != ContentStatus.DEPLOYED)
            {
                if(env == EnvironmentId.STAGE)
                    content.setStatus(ContentStatus.STAGED);
                else if(content.getStatus() == ContentStatus.STAGED)
                    content.setStatus(ContentStatus.DEPLOYED);
            }

            if(content.getStatus() != status)
            {
                contentDAO.update(content);
                csvHandler.appendLine(csvHandler.getValues(fields));
            }
        }

        // Only copy the content to the S3 bucket once per organisation
        if(batchOrganisation.getBucket() == null)
        {
            String bucket = site.getS3Config().getContentBucket();
            s3Handler.writeFile();
            s3Handler.copyFileToBucket(bucket);
            s3Handler.deleteFile();
            batchOrganisation.setBucket(bucket);
        }

        // Process the CSV file
        String type = getType().tag();
        csvHandler.setFilename(csvHandler.getCsvFilename());
        csvHandler.convertLinesToAscii(getHtmlFields());
        csvHandler.writeFile();

        // Upload the csv file to the environment
        Environment environment = site.getEnvironment(env);
        String path = environment.getBase()+System.getProperty("app.path.files.feeds."+type);
        csvHandler.copyFileToHost(path, environment);

        csvHandler.deleteFile();
    }

    /**
     * Process the fields for the content to be deployed.
     * <p>
     * Implemented by super-class.
     */
    protected void processContentFields(FieldMap fields)
    {
    }

    /**
     * Builder to make configuration construction easier.
     */
    protected abstract static class Builder<T extends ContentConfig, B extends Builder<T,B>>
        implements ConfigParser<ContentConfig>
    {
        // The config attribute names
        private static final String SOURCE = "source";
        private static final String SUMMARY = "summary";
        private static final String FIELDS = "fields";
        private static final String OUTPUT = "output";

        private ContentConfig ret = null;

        /**
         * Sets the configuration.
         * @param config The configuration
         */
        public void set(ContentConfig config)
        {
            ret = config;
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public B parse(Map<String, Object> map)
        {
            if(map.containsKey(SOURCE))
                ret.setSource(ContentSource.fromCode((String)map.get(SOURCE)));
            if(map.containsKey(FIELDS))
                ret.addFields((Map<String,String>)map.get(FIELDS));
            if(map.containsKey(OUTPUT))
                ret.addOutput((Map<String,String>)map.get(OUTPUT));

            if(map.containsKey(SUMMARY))
            {
                ret.setSummary(SummaryConfig.builder()
                    .parse((Map<String,Object>)map.get(SUMMARY)).build());
            }

            return self();
        }

        /**
         * Sets the content defaults.
         */
        public B fields(Map<String,Object> map)
        {
            ret.getFields().put(ORGANISATION, ret.getName());

            for(Map.Entry<String, Object> entry : map.entrySet())
            {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                if(value instanceof String)
                    ret.getFields().put(key, (String)value);
            }

            return self();
        }

        /**
         * Copy constructor.
         * @param obj The object to copy attributes from
         * @return This object
         */
        public B copy(T obj)
        {
            ret.copyAttributes(obj);
            return self();
        }

        /**
         * Returns this object.
         * @return This object
         */
        protected abstract B self();

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public abstract T build();
    }
}