/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.cache.admin;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import com.samskivert.mustache.Template;
import com.opsmatters.media.model.admin.EmailTemplate;
import com.opsmatters.media.model.admin.EmailTemplateId;

/**
 * Class representing the list of email templates.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EmailTemplates implements java.io.Serializable
{
    private static final Logger logger = Logger.getLogger(EmailTemplates.class.getName());

    private static Map<String,EmailTemplate> templateMap = new LinkedHashMap<String,EmailTemplate>();

    private static boolean initialised = false;

    /**
     * Private constructor.
     */
    private EmailTemplates()
    {
    }

    /**
     * Returns <CODE>true</CODE> if templates have been initialised.
     */
    public static boolean isInitialised()
    {
        return initialised;
    }

    /**
     * Loads the set of templates.
     */
    public static void load(List<EmailTemplate> templates)
    {
        initialised = false;

        clear();
        for(EmailTemplate template : templates)
        {
            add(template);
        }

        logger.info("Loaded "+size()+" email templates");

        initialised = true;
    }

    /**
     * Clears the email templates.
     */
    public static void clear()
    {
        templateMap.clear();
    }

    /**
     * Returns the email template with the given code.
     */
    public static EmailTemplate get(String code)
    {
        return templateMap.get(code);
    }

    /**
     * Returns the email template with the given id.
     */
    public static EmailTemplate get(EmailTemplateId id)
    {
        return templateMap.get(id.code());
    }

    /**
     * Adds the email template with the given code.
     */
    public static void add(EmailTemplate template)
    {
        templateMap.put(template.getCode(), template);
    }

    /**
     * Removes the email template with the given code.
     */
    public static void remove(EmailTemplate template)
    {
        templateMap.remove(template.getCode());
    }

    /**
     * Returns the count of email templates.
     */
    public static int size()
    {
        return templateMap.size();
    }

    /**
     * Generates an email using the given template code and properties.
     */
    public static String generate(String code, Map<String,Object> properties)
        throws RuntimeException
    {
        String ret = null;
        EmailTemplate template = get(code);
        if(template != null && template.isActive())
        {
            Template mustache = template.getMustacheTemplate();
            if(mustache != null)
                ret = mustache.execute(properties);
        }

        return ret;
    }

    /**
     * Generates an email using the given template id and properties.
     */
    public static String generate(EmailTemplateId id, Map<String,Object> properties)
        throws RuntimeException
    {
        return generate(id.code(), properties);
    }
}