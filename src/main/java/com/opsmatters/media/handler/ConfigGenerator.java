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

package com.opsmatters.media.handler;

import java.io.StringReader;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import com.opsmatters.media.cache.admin.VideoProviders;
import com.opsmatters.media.model.admin.VideoProviderId;
import com.opsmatters.media.model.admin.VideoProvider;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.project.RepositoryBranch;
import com.opsmatters.media.model.content.util.ConfigGeneratorFields;

/**
 * Generates and writes an organisation content configuration YAML file.
 *
 * @author Gerald Curley (opsmatters)
 */
public class ConfigGenerator
{
    private static final Logger logger = Logger.getLogger(ConfigGenerator.class.getName());

    private static boolean initialised = false;
    private static Map<ContentType,String> contentTypeTemplates = new HashMap<ContentType,String>();
    private static Map<VideoProviderId,String> videoTemplates = new HashMap<VideoProviderId,String>();

    private ConfigGeneratorFields fields;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private StringSubstitutor substitutor = null;
    private Map<String,String> videos = new LinkedHashMap<String,String>();
    private Map<ContentType,String> configMap = new HashMap<ContentType,String>();

    /**
     * Constructor that takes a set of config fields.
     */
    public ConfigGenerator(ConfigGeneratorFields fields) throws IOException
    {
        this.fields = fields;

        // Cache the templates
        if(!initialised)
            init();

        setProperties(fields);
    }

    /**
     * Returns the map of generated configurations.
     */
    public Map<ContentType,String> getConfigMap()
    {
        return configMap;
    }

    /**
     * Initialise the templates.
     */
    private void init() throws IOException
    {
        for(ContentType type : ContentType.toList())
        {
            if(type != ContentType.ORGANISATION)
                contentTypeTemplates.put(type, getContents(String.format("%s.yml", type.tag())));
        }

        for(VideoProviderId providerId : VideoProviderId.toList())
        {
            VideoProvider provider = VideoProviders.get(providerId);
            if(provider != null)
                videoTemplates.put(providerId, getContents(String.format("videos-%s.yml", provider.getTag())));
        }

        initialised = true;
    }

    /**
     * Read the contents of the given filename.
     */
    private String getContents(String filename) throws IOException
    {
        InputStream in = null;
        ClassLoader cl = ConfigGenerator.class.getClassLoader();
        String ret = null;

        try
        {
            in = cl.getResourceAsStream(String.format("template/content/%s", filename));
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
     * Initialise the configuration properties.
     */
    private void setProperties(ConfigGeneratorFields fields) throws IOException
    {
        // Set the substitution properties
        properties.clear();
        properties.put("code", fields.getCode());
        properties.put("name", fields.getName());
        properties.put("tags", fields.getTags());
        properties.put("features", fields.getFeatures());
        properties.put("channel-id", fields.getChannelId());
        properties.put("user-id", fields.getUserId());
        properties.put("website", fields.getWebsite());
        properties.put("blog-url", fields.getBlogUrl());
        properties.put("branch", RepositoryBranch.MASTER.value());

        substitutor = new StringSubstitutor(properties);
    }

    /**
     * Generate the configuration from the parsed content and templates.
     */
    public void generate() throws IOException
    {
        for(ContentType type : ContentType.toList())
        {
            if(type == ContentType.ORGANISATION)
                continue;

            if(fields.getContentTypes().contains(type))
            {
                StringBuffer buff = new StringBuffer();

                // Go through the existing content types
                if(configMap.get(type) != null)
                    buff.append(configMap.get(type));
                else
                    buff.append(contentTypeTemplates.get(type));

                if(type == ContentType.VIDEO)
                {
                    // Go through the existing sections and map to providers
                    Map<VideoProviderId,String> providers = new HashMap<VideoProviderId,String>();
                    if(videos.size() > 0)
                    {
                        for(Map.Entry<String,String> entry : videos.entrySet())
                        {
                            VideoProvider provider = VideoProviders.getByName(entry.getKey());
                            if(provider != null)
                                providers.put(provider.getProviderId(), entry.getKey());
                        }
                    }

                    // Output any other providers that were requested but don't exist yet
                    for(VideoProviderId providerId : VideoProviderId.toList())
                    {
                        if(fields.getVideoProviders().contains(providerId)
                            && !providers.containsKey(providerId))
                        {
                            buff.append("\r\n");
                            buff.append(videoTemplates.get(providerId));
                        }
                    }
                }

                configMap.put(type, substitutor.replace(buff.toString()));
            }
        }
    }

    /**
     * Parse the configuration of the given type.
     */
    public void set(ContentType type, String config) throws IOException
    {
        StringReader reader = null;
        String currentVideo = null;

        try
        {
            StringBuffer buff = null; 
            reader = new StringReader(config);
            List<String> lines = IOUtils.readLines(reader);
            for(String line : lines)
            {
                if(line.length() > 0)
                {
                    // Extract the tag if this is the start of a content type
                    if(buff == null)
                    {
                        buff = new StringBuffer(line);
                    }
                    else
                    {
                        // Extract the name if this is the start of a page or channel
                        Matcher matcher = Pattern.compile("^- (.+):$").matcher(line);
                        String name = matcher.find() ? matcher.group(1) : null;
                        if(name != null)
                        {
                            if(type == ContentType.VIDEO)
                            {
                                if(currentVideo != null && buff.length() > 0)
                                {
                                    videos.put(currentVideo, buff.toString());
                                    buff.setLength(0);
                                }

                                currentVideo = null;
                                buff = new StringBuffer(line);
                                currentVideo = name;
                            }
                            else // Other types don't have provider
                            {
                                buff.append("\r\n");
                                buff.append(line);
                            }
                        }
                        else // Not the start or end of a section
                        {
                            buff.append("\r\n");
                            buff.append(line);
                        }
                    }
                }
            }

            if(currentVideo != null && buff.length() > 0)
            {
                videos.put(currentVideo, buff.toString());
                buff.setLength(0);
            }

            configMap.put(type, config);
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }
}