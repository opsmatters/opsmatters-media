/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.config.platform;

import java.io.File;
import java.util.logging.Logger;
import com.opsmatters.media.config.ConfigurationFile;

/**
 * Class representing the platform configuration file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PlatformConfigurationFile extends ConfigurationFile
{
    private static final Logger logger = Logger.getLogger(PlatformConfigurationFile.class.getName());

    private PlatformConfiguration config;

    /**
     * Constructor that takes a filename.
     */
    public PlatformConfigurationFile(File file)
    {
        super(file);
    }

    /**
     * Read the config file.
     */
    @Override
    public void read()
    {
        // Get the config from the config file
        config = PlatformConfiguration.builder()
            .name(getType())
            .directory(getDirectory())
            .filename(getFilename())
            .build(true);
        logger.fine("Found platform config file: "+getFilename());
    }

    /**
     * Returns the name of the config file.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the type of the config file.
     */
    @Override
    public String getType()
    {
        return "platform";
    }

    /**
     * Returns the configuration.
     */
    public PlatformConfiguration getConfiguration()
    {
        // Load the YAML file
        if(config == null)
        {
            read();
        }

        return config;
    }
}