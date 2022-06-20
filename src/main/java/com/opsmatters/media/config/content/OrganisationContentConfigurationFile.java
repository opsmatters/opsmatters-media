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
import java.util.logging.Logger;
import com.opsmatters.media.config.ConfigurationFile;
import com.opsmatters.media.util.FileUtils;

/**
 * Class representing the configuration file for an organisation's content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationContentConfigurationFile extends ConfigurationFile
{
    private static final Logger logger = Logger.getLogger(OrganisationContentConfigurationFile.class.getName());

    private OrganisationContentConfiguration config;

    /**
     * Constructor that takes a filename.
     */
    public OrganisationContentConfigurationFile(File file)
    {
        super(file);
    }

    /**
     * Initialise the config file.
     */
    @Override
    protected void init()
    {
        String filename = getFilename();
        if(filename == null)
            throw new IllegalArgumentException("filename null");
        int pos = filename.indexOf("-"+getType());
        if(pos != -1)
        {
            String configName = FileUtils.getName(filename);
            setName(configName.substring(0, pos));
        }
    }

    /**
     * Read the config file.
     */
    @Override
    public void read()
    {
        // Get the content config from the config file
        config = OrganisationContentConfiguration.builder()
            .name(getType())
            .directory(getDirectory())
            .filename(getFilename())
            .build(true);
        logger.fine("Found content config file: "+getFilename());
    }

    /**
     * Returns the name of the config file.
     */
    public String toString()
    {
        return config != null ? config.getName() : null;
    }

    /**
     * Returns the type of the config file.
     */
    @Override
    public String getType()
    {
        return "content";
    }

    /**
     * Returns the configuration.
     */
    public OrganisationContentConfiguration getConfiguration()
    {
        // Load the YAML file
        if(config == null)
        {
            read();
        }

        return config;
    }
}