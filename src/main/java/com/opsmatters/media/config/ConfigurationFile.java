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
package com.opsmatters.media.config;

import java.io.File;
import com.opsmatters.media.util.FileUtils;

/**
 * Class representing a configuration file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ConfigurationFile implements java.io.Serializable
{
    private String name = "";
    private String directory = "";
    private String filename = "";

    /**
     * Constructor that takes a filename.
     */
    public ConfigurationFile(File file)
    {
        setDirectory(file.getParent());
        setFilename(file.getName());
        init();
    }

    /**
     * Initialise the config file.
     */
    protected void init()
    {
        if(filename == null)
            throw new IllegalArgumentException("filename null");
        setName(FileUtils.getName(filename));
    }

    /**
     * Returns the type of the config file.
     */
    public abstract String getType();

    /**
     * Read the config file.
     */
    public abstract void read();

    /**
     * Returns the name of the config file.
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Returns the name of the config file.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the config file.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the directory of the config file.
     */
    public String getDirectory()
    {
        return directory;
    }

    /**
     * Sets the directory of the config file.
     */
    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    /**
     * Returns the filename of the config file.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the filename of the config file.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }
}