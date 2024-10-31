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
import com.opsmatters.media.cache.content.organisation.OrganisationConfigs;
import com.opsmatters.media.cache.content.util.ContentImages;
import com.opsmatters.media.cache.platform.Environments;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.Environment;
import com.opsmatters.media.model.platform.EnvironmentId;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.content.organisation.OrganisationListing;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.handler.ContentHandler;

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

    private static Map<ContentType,FieldMap> defaultMap = new LinkedHashMap<ContentType,FieldMap>();
    private static Map<ContentType,Map<String,String>> outputMap = new LinkedHashMap<ContentType,Map<String,String>>();

    private String name = "";
    private ContentSource source = getType().source();
    private FieldMap fields = new FieldMap();

    /**
     * Constructor that takes a name.
     */
    protected ContentConfig(String name)
    {
        setName(name);

        // Add the default fields
        addFields(getDefaults(getType()));
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
            setSource(obj.getSource());
            setFields(new FieldMap(obj.getFields()));
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
     * Returns the defaults for the given type.
     */
    private static FieldMap getDefaults(ContentType type)
    {
        return defaultMap.get(type);
    }

    /**
     * Sets the defaults.
     */
    public static void setDefaults(List<ContentDefault> defaults)
    {
        defaultMap.clear();
        for(ContentDefault _default : defaults)
        {
            if(_default.isEnabled())
                addDefault(_default);
        }

        int count = 0;
        for(ContentType type : ContentType.values())
        {
            if(getDefaults(type) != null)
            {
                int size = getDefaults(type).size();
                logger.info(String.format("Loaded %d defaults for %s",
                    size, type.tag()));
                count += size;
            }
        }

        logger.info(String.format("Loaded %d defaults", count));
    }

    /**
     * Adds the given default.
     */
    private static void addDefault(ContentDefault _default)
    {
        FieldMap defaults = defaultMap.get(_default.getType());
        if(defaults == null)
        {
            defaults = new FieldMap();
            defaults.put(TYPE, _default.getType().code()); // Add the type code
            defaultMap.put(_default.getType(), defaults);
        }
      
        defaults.put(_default.getName(), _default.getValue());
    }

    /**
     * Returns the output columns for the given type.
     */
    public static Map<String,String> getOutput(ContentType type)
    {
        return outputMap.get(type);
    }

    /**
     * Sets the output columns.
     */
    public static void setOutputColumns(List<OutputColumn> columns)
    {
        outputMap.clear();
        for(OutputColumn column : columns)
        {
            if(column.isEnabled())
                addOutputColumn(column);
        }

        int count = 0;
        for(ContentType type : ContentType.values())
        {
            if(getOutput(type) != null)
            {
                int size = getOutput(type).size();
                logger.info(String.format("Loaded %d output columns for %s",
                    size, type.tag()));
                count += size;
            }
        }

        logger.info(String.format("Loaded %d output columns", count));
    }

    /**
     * Adds the given output column.
     */
    private static void addOutputColumn(OutputColumn column)
    {
        Map<String,String> output = outputMap.get(column.getType());
        if(output == null)
        {
            output = new LinkedHashMap<String,String>();
            outputMap.put(column.getType(), output);
        }
      
        output.put(column.getName(), column.getValue());
    }

    /**
     * Returns the list of HTML fields that need to be escaped.
     */
    public String[] getHtmlFields()
    {
        return new String[] {"body"};
    }

    /**
     * Populate a content handler using content items from the database.
     */
    public ContentHandler getContentHandler(Site site, List<C> items, EnvironmentId env)
        throws IOException, SQLException
    {
        ContentHandler handler = ContentHandler.builder()
            .useConfig(this)
            .withOutput(getOutput(getType()))
            .withWorkingDirectory(System.getProperty("app.working"))
            .initFile()
            .build();

        Environment images = Environments.get(EnvironmentId.IMAGE);

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
                fields.add(OrganisationConfigs.get(organisation.getCode()));

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

            if(env != null)
            {
                if(content.getStatus() != ContentStatus.DEPLOYED)
                {
                    if(env == EnvironmentId.STAGE)
                        content.setStatus(ContentStatus.STAGED);
                    else if(content.getStatus() == ContentStatus.STAGED)
                        content.setStatus(ContentStatus.DEPLOYED);
                }

                // Only add the rows that have changed
                if(content.getStatus() != status)
                    handler.appendLine(handler.getValues(fields));
            }
            else // Otherwise add all rows
            {
                handler.appendLine(handler.getValues(fields));
            }
        }

        return handler;
    }

    /**
     * Populate a content handler using content items from the database.
     */
    public ContentHandler getContentHandler(Site site, List<C> items)
        throws IOException, SQLException
    {
        return getContentHandler(site, items, null);
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
        private static final String FIELDS = "fields";

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