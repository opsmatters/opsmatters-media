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

package com.opsmatters.media.util;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Generic text file reader.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TextFile
{
    private static final Logger logger = Logger.getLogger(TextFile.class.getName());

    private String filename;
    private String contents;
    private boolean valid = true;

    /**
     * Constructor that takes a filename.
     * @param filename The name of the file
     */
    public TextFile(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the name of the file.
     * @return The name of the file
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the contents of the file as a string.
     * @param contents The contents of the file
     */
    public void setContents(String contents)
    {
        this.contents = contents;
    }

    /**
     * Returns the contents of the file as a string.
     * @return The contents of the file
     */
    public String getContents()
    {
        return contents;
    }

    /**
     * Returns <CODE>true</CODE> if the file is valid.
     * @return <CODE>true</CODE> if the file is valid
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Reads the contents of the file.
     * @return <CODE>true</CODE> if the file was read successfully
     * @throws IOException The file could not be opened
     */
    public boolean read() throws IOException
    {
        valid = false;
        File file = new File(filename);
        FileReader reader = new FileReader(file);
        if(file.exists())
        {
            // Load the file contents
            contents = getContents(reader, "\n");
            if(contents != null)
                valid = true;
            else
                logger.severe("Unable to read file contents: "+file.getAbsolutePath());
        }
        else
        {
            logger.severe("File does not exist: "+file.getAbsolutePath());
        }

        try
        {
            if(reader != null)
                reader.close();
        }
        catch(IOException e)
        {
        }

        return valid;
    }

    /**
     * Reads the contents of the given stream.
     * @param stream The input stream of the file
     * @return <CODE>true</CODE> if the file was read successfully
     * @throws IOException The file could not be opened
     */
    public boolean read(InputStream stream) throws IOException
    {
        valid = false;
        InputStreamReader reader = new InputStreamReader(stream);

        // Load the file contents
        contents = getContents(reader, "\n");
        if(contents != null)
            valid = true;
        else
            logger.severe("Unable to read file contents: "+filename);

        try
        {
            if(reader != null)
                reader.close();
        }
        catch(IOException e)
        {
        }

        return valid;
    }

    /**
     * Read the contents of the file using the given reader.
     * @param reader The reader used to read the file stream
     * @param terminator The line terminator of the YAML file
     * @return The contents of the file as a string
     */
    private String getContents(Reader reader, String terminator) throws IOException
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
        reader.close();
        return buff.toString();
    }
}