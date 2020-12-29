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
package com.opsmatters.media.config.chart;

import java.io.File;
import java.util.logging.Logger;
import com.opsmatters.media.config.ConfigurationFile;

/**
 * Class representing the chart configuration file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartConfigurationFile extends ConfigurationFile
{
    private static final Logger logger = Logger.getLogger(ChartConfigurationFile.class.getName());

    private ChartConfiguration config;

    /**
     * Constructor that takes a filename.
     */
    public ChartConfigurationFile(File file)
    {
        super(file);
    }

    /**
     * Read the config file.
     */
    @Override
    public void read()
    {
        // Get the charts config from the config file
        config = ChartConfiguration.builder()
            .name(getType())
            .directory(getDirectory())
            .filename(getFilename())
            .build(true);
        logger.fine("Found charts config file: "+getFilename());
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
        return "charts";
    }

    /**
     * Returns the configuration.
     */
    public ChartConfiguration getConfiguration()
    {
        // Load the YAML file
        if(config == null)
        {
            read();
        }

        return config;
    }
}