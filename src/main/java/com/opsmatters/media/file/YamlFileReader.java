/*
 * Copyright 2022 Gerald Curley
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

package com.opsmatters.media.file;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Class to read a YAML configuration file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class YamlFileReader
{
    private File file;
    private FileReader reader = null;

    /**
     * Constructor that takes a file.
     */
    public YamlFileReader(File file) throws IOException
    {
        setFile(file);
    }

    /**
     * Constructor that takes a filename.
     */
    public YamlFileReader(String filename) throws IOException
    {
        this(new File(filename));
    }

    /**
     * Initialise the file and reader.
     */
    private void setFile(File file) throws IOException
    {
        this.file = file;
        reader = new FileReader(file);
    }

    /**
     * Reads the configuration from the current file.
     */
    public Map<String,Object> read() throws IOException
    {
        return new Yaml().load(readFile("\n"));
    }

    /**
     * Read the contents of the file.
     */
    private String readFile(String terminator) throws IOException
    {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(reader);

        try
        {
            String line = null;
            while((line = in.readLine()) != null)
            {
                buff.append(line);
                if(terminator != null)
                    buff.append(terminator);
            }
        }
        finally
        {
            if(in != null)
                in.close();
        }

        return buff.toString();
    }

    /**
     * Release internal resources and associated streams.
     */
    public void close()
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