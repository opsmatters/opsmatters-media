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

package com.opsmatters.media.model.content.organisation;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;

import static com.opsmatters.media.file.FileFormat.*;

/**
 * Class that represents the configuration for an organisation's listing.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationListingConfig extends ContentConfig<OrganisationListing>
{
    private static final Logger logger = Logger.getLogger(OrganisationListingConfig.class.getName());

    /**
     * Default constructor.
     */
    public OrganisationListingConfig(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public OrganisationListingConfig(OrganisationListingConfig obj)
    {
        super(obj);
        copyAttributes(obj);
    }

    /**
     * Returns the import filename for this configuration.
     */
    @Override
    public String getFilename()
    {
        return String.format("%s.%s",
            getType().tag(), XLSX.value());
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
     * Returns the list of HTML fields that need to be escaped.
     */
    @Override
    public String[] getHtmlFields()
    {
        return new String[] {"summary", "body"};
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
    public static class Builder extends ContentConfig.Builder<OrganisationListingConfig, Builder>
    {
        private OrganisationListingConfig ret = null;

        /**
         * Default constructor.
         */
        public Builder()
        {
            ret = new OrganisationListingConfig("");
            super.set(ret);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            super.parse(map);
            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public OrganisationListingConfig build()
        {
            return ret;
        }
    }
}