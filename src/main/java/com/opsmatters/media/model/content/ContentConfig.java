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
import com.opsmatters.media.cache.content.ContentConfigs;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.FeedsConfig;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.EnvironmentName;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.organisation.OrganisationStatus;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.content.organisation.OrganisationListing;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.handler.ContentHandler;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.util.FileUtils;

import static com.opsmatters.media.model.content.FieldName.*;

/**
 * Class that represents a YAML configuration for content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentConfig<C extends ContentItem> implements FieldSource, ConfigElement
{
    private static final Logger logger = Logger.getLogger(ContentConfig.class.getName());

    public static final String FILENAME = "content.yml";

    private String name = "";
    private String filename = "";
    private String sheet = "";
    private ContentSource source = ContentSource.STORE;
    private String defaultDatePattern = "";
    private boolean trailingSlash = false;
    private SummaryConfig summary; 
    private FieldMap fields;
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
            setFilename(obj.getFilename());
            setSheet(obj.getSheet());
            setSource(obj.getSource());
            setDefaultDatePattern(obj.getDefaultDatePattern());
            setTrailingSlash(obj.hasTrailingSlash());
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
        if(this.fields == null)
            this.fields = new FieldMap();
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
                    --idx;
                    continue;
                }

                // Set the published flag based on the environment and status
                boolean published = false;
                if(env == EnvironmentName.PROD)
                    published = organisationSite.isActive();
                else
                    published = organisationSite.isReview() || organisationSite.isActive();
                fields.put(PUBLISHED, published ? "1" : "0");
                fields.add(ContentConfigs.get(organisation.getCode()));

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

                // Check if the URL needs a trailing slash (to avoid redirects)
                if(hasTrailingSlash() && fields.containsKey(URL))
                {
                    String url = fields.get(URL);
                    if(url != null && url.length() > 0 && !url.endsWith("/"))
                        fields.put(URL, url+"/");
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

    /**
     * Builder to make configuration construction easier.
     */
    protected abstract static class Builder<T extends ContentConfig, B extends Builder<T,B>>
        implements ConfigParser<ContentConfig>
    {
        // The config attribute names
        private static final String FILENAME = "filename";
        private static final String SHEET = "sheet";
        private static final String SOURCE = "source";
        private static final String DEFAULT_DATE_PATTERN = "default-date-pattern";
        private static final String TRAILING_SLASH = "trailing-slash";
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
            if(map.containsKey(FILENAME))
                ret.setFilename((String)map.get(FILENAME));
            if(map.containsKey(SHEET))
                ret.setSheet((String)map.get(SHEET));
            if(map.containsKey(DEFAULT_DATE_PATTERN))
                ret.setDefaultDatePattern((String)map.get(DEFAULT_DATE_PATTERN));
            if(map.containsKey(TRAILING_SLASH))
                ret.setTrailingSlash((Boolean)map.get(TRAILING_SLASH));
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
            if(ret.getFields() != null)
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