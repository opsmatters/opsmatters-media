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

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.OrganisationListing;

/**
 * Class that represents the configuration for an organisation's listing.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationListingConfiguration extends ContentConfiguration<OrganisationListing>
{
    private static final Logger logger = Logger.getLogger(OrganisationListingConfiguration.class.getName());

    public static final String SUFFIX = "-organisations.yml";

    /**
     * Default constructor.
     */
    public OrganisationListingConfiguration(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public OrganisationListingConfiguration(OrganisationListingConfiguration obj)
    {
        super(obj != null ? obj.getName() : null);
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(OrganisationListingConfiguration obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
        }
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public ContentType getType()
    {
        return ContentType.ORGANISATION;
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    @Override
    protected void parseDocument(Map<String,Object> map)
    {
        super.parseDocument(map);
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder
    {
        protected String name = "";
        protected String directory = "";
        protected String filename = "";

        /**
         * Default constructor.
         */
        public Builder()
        {
        }

        /**
         * Sets the name of the configuration.
         * <P>
         * @param name The name of the configuration
         * @return This object
         */
        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        /**
         * Sets the config directory.
         * @param key The config directory
         * @return This object
         */
        public Builder directory(String directory)
        {
            this.directory = directory;
            return this;
        }

        /**
         * Sets the config filename.
         * @param key The config filename
         * @return This object
         */
        public Builder filename(String filename)
        {
            this.filename = filename;
            return this;
        }

        /**
         * Returns the configuration
         * @return The configuration
         */
        public OrganisationListingConfiguration build(boolean read)
        {
            OrganisationListingConfiguration ret = new OrganisationListingConfiguration(name);

            if(read)
            {
                // Read the config file
                File config = new File(directory, filename);
                ret.read(config.getAbsolutePath());
            }

            return ret;
        }
    }
}