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
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents a YAML configuration file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class YamlConfiguration implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(YamlConfiguration.class.getName());

    private String name = "";
    private boolean valid = true;

    /**
     * Default constructor.
     */
    public YamlConfiguration(String name)
    {
        setName(name);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(YamlConfiguration obj)
    {
        if(obj != null)
        {
            setName(obj.getName());
            setValid(obj.isValid());
        }
    }

    /**
     * Returns the name of this configuration.
     */
    public String toString()
    {
        return getName();
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
     * Returns <CODE>true</CODE> if this configuration file is valid.
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Set to <CODE>true</CODE> if this configuration file is valid.
     */
    public void setValid(boolean b)
    {
        valid = b;
    }

    /**
     * Reads the configuration from the given filename.
     * @param filename the YAML configuration filename to parse
     */
    public void read(String filename)
    {
        read(new File(filename));
    }

    /**
     * Reads the configuration from the given file.
     * @param file the YAML configuration file to parse
     */
    public void read(File file)
    {
        FileReader reader = null;

        try
        {
            if(file.exists())
            {
                // Create the parser
                reader = new FileReader(file);
                Map<String,Object> data = new Yaml().load(getContents(reader, "\n"));
                if(data != null)
                {
                    parseDocument(data);
                }
                else
                {
                    logger.severe("Unable to find configuration in "+name+" file: "+file.getAbsolutePath());
                    valid = false;
                }
            }
            else
            {
                logger.severe("Unable to find "+name+" file: "+file.getAbsolutePath());
                valid = false;
            }
        }
        catch(IOException e)
        {
            logger.severe("loading "+name+" file '"+file.getAbsolutePath());
            logger.severe(StringUtils.serialize(e));
            valid = false;
        }
        finally
        {
            try
            {
                if(reader != null)
                    reader.close();
            }
            catch(IOException e)
            {
            }
        }
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    protected abstract void parseDocument(Map<String,Object> map);

    /**
     * Read the contents of the configuration file.
     */
    private String getContents(FileReader reader, String terminator) throws IOException
    {
        String line = null;
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(reader);
        while((line = in.readLine()) != null)
        {
            buff.append(line);
            if(terminator != null)
                buff.append(terminator);
        }
        return buff.toString();
    }

    /**
     * Write the YAML configuration to the given file.
     */
    public void write(File file, String name) throws IOException
    {
        if(file == null)
        {
            logger.severe("Error saving "+name+" configuration - no filename");
            return;
        }
        else if(!valid)
        {
            logger.severe("Error saving "+name+" configuration - invalid");
            return;
        }

        FileWriter writer = null;
        try
        {
            // Create the YAML document and write it
            Object doc = createDocument();
            writer = new FileWriter(file);
            new Yaml().dump(doc, writer);
        }
        catch(IOException e)
        {
            logger.severe(StringUtils.serialize(e));
            logger.severe("Error writing "+name+" file, unable to save");
        }
        finally
        {
            if(writer != null)
                writer.close();
        }
    }

    /**
     * Creates a YAML Document that represents the configuration (optional).
     */
    public Object createDocument()
    {
        return null;
    }
}