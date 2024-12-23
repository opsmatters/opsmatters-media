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

package com.opsmatters.media.handler;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;

/**
 * Generates and writes an email body.
 *
 * @author Gerald Curley (opsmatters)
 */
public class EmailGenerator
{
    private static final Logger logger = Logger.getLogger(EmailGenerator.class.getName());

    private String template;
    private static Map<String,String> templates = new HashMap<String,String>();

    /**
     * Default constructor.
     */
    public EmailGenerator(String template) throws IOException
    {
        this.template = template;
        if(!templates.containsKey(template))
            templates.put(template, getContents(template));
    }

    /**
     * Reads the contents of the template with the given name.
     */
    private String getContents(String name) throws IOException
    {
        InputStream in = null;
        ClassLoader cl = ConfigGenerator.class.getClassLoader();
        String ret = null;

        try
        {
            in = cl.getResourceAsStream(String.format("email/%s.txt", name));
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
     * Generate the email from the template and the properties.
     */
    public String generate(Map<String,Object> properties) throws IOException
    {
        return new StringSubstitutor(properties).replace(templates.get(template));
    }
}