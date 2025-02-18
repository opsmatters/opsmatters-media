/*
 * Copyright 2024 Gerald Curley
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

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.samskivert.mustache.MustacheException;

/**
 * Generates text using a mustache template.
 *
 * @author Gerald Curley (opsmatters)
 */
public class MustacheTemplate
{
    private static final Logger logger = Logger.getLogger(MustacheTemplate.class.getName());

    private String name;
    private static Map<String,Template> templates = new HashMap<String,Template>();

    /**
     * Constructor that takes a template name.
     */
    public MustacheTemplate(String name) throws IOException
    {
        this.name = name;
        loadTemplate(name);
    }

    /**
     * Loads the mustache template with the given name.
     */
    private void loadTemplate(String name) throws IOException
    {
        if(!templates.containsKey(name))
        {
            String text = getContents(String.format("%s.mustache", name));
            templates.put(name, Mustache.compiler().compile(text));
        }
    }

    /**
     * Reads the contents of the template with the given filename.
     */
    private String getContents(String filename) throws IOException
    {
        InputStream in = null;
        ClassLoader cl = this.getClass().getClassLoader();
        String ret = null;

        try
        {
            in = cl.getResourceAsStream(String.format("template/%s", filename));
            ret = IOUtils.toString(in, StandardCharsets.UTF_8.name());
        }
        finally
        {
            try
            {
                if(in != null)
                    in.close();
            }
            catch(IOException e)
            {
            }
        }

        return ret;
    }

    /**
     * Generate the email from the current template and the properties.
     */
    public String generate(Map<String,Object> properties) throws MustacheException
    {
        return templates.get(name).execute(properties);
    }
}