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
package com.opsmatters.media.config.social;

import java.io.File;
import java.util.logging.Logger;
import com.opsmatters.media.config.ConfigurationFile;
import com.opsmatters.media.util.FileUtils;

/**
 * Class representing the social media configuration file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialConfigurationFile extends ConfigurationFile
{
    private static final Logger logger = Logger.getLogger(SocialConfigurationFile.class.getName());

    private SocialConfiguration config;

    /**
     * Constructor that takes a filename.
     */
    public SocialConfigurationFile(File file)
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
        // Get the social config from the config file
        config = SocialConfiguration.builder()
            .name(getType())
            .directory(getDirectory())
            .filename(getFilename())
            .build(true);
        logger.fine("Found social config file: "+getFilename());
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
        return "social";
    }

    /**
     * Returns the configuration.
     */
    public SocialConfiguration getConfiguration()
    {
        // Load the YAML file
        if(config == null)
        {
            read();
        }

        return config;
    }
}