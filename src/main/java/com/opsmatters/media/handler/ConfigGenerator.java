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

import java.io.File;
import java.io.FileReader;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.video.VideoProvider;
import com.opsmatters.media.model.content.project.RepositoryBranch;
import com.opsmatters.media.model.content.util.ConfigGeneratorFields;
import com.opsmatters.media.util.StringUtils;

/**
 * Generates and writes an organisation content configuration YAML file.
 *
 * @author Gerald Curley (opsmatters)
 */
public class ConfigGenerator
{
    private static final Logger logger = Logger.getLogger(ConfigGenerator.class.getName());

    private static boolean initialised = false;
    private static String header = null;
    private static Map<ContentType,String> contentTypeTemplates = new HashMap<ContentType,String>();
    private static Map<VideoProvider,String> videoTemplates = new HashMap<VideoProvider,String>();

    private String config = null;
    private ConfigGeneratorFields fields;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private StringSubstitutor substitutor = null;
    private ContentType currentType = null;
    private String currentVideo = null;
    private String currentEvent = null;
    private Map<ContentType,String> contentTypes = new HashMap<ContentType,String>();
    private Map<String,String> videos = new LinkedHashMap<String,String>();
    private Map<String,String> events = new LinkedHashMap<String,String>();

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
     * Returns the generated configuration.
     */
    public String getConfig()
    {
        return config;
    }

    /**
     * Initialise the templates.
     */
    private void init() throws IOException
    {
        header = getContents("header.yml");

        for(ContentType type : ContentType.toList())
            contentTypeTemplates.put(type, getContents(String.format("%s.yml", type.tag())));
        for(VideoProvider provider : VideoProvider.toList())
            videoTemplates.put(provider, getContents(String.format("videos-%s.yml", provider.code())));

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
            in = cl.getResourceAsStream(String.format("generator/%s", filename));
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
        StringBuffer buff = new StringBuffer(header); 
        for(ContentType type : ContentType.toList())
        {
            if(fields.getContentTypes().contains(type))
            {
                buff.append("\r\n\r\n");

                // Go through the existing content types
                if(contentTypes.get(type) != null)
                    buff.append(contentTypes.get(type));
                else
                    buff.append(contentTypeTemplates.get(type));

                if(type == ContentType.VIDEO)
                {
                    // Go through the existing sections and map to providers
                    Map<VideoProvider,String> providers = new HashMap<VideoProvider,String>();
                    if(videos.size() > 0)
                    {
                        for(Map.Entry<String,String> entry : videos.entrySet())
                        {
                            VideoProvider provider = VideoProvider.fromValue(entry.getKey());
                            if(provider != null)
                                providers.put(provider, entry.getKey());

                            // Exclude the section if the field has been deselected
                            if(provider == null || fields.getVideoProviders().contains(provider))
                            {
                                buff.append("\r\n");
                                buff.append(entry.getValue());
                            }
                        }
                    }

                    // Output any other providers that were requested but don't exist yet
                    for(VideoProvider provider : VideoProvider.toList())
                    {
                        if(fields.getVideoProviders().contains(provider)
                            && !providers.containsKey(provider))
                        {
                            buff.append("\r\n");
                            buff.append(videoTemplates.get(provider));
                        }
                    }
                }
            }
        }

        config = substitutor.replace(buff.toString());
    }

    /**
     * Parse the configuration from the given file.
     */
    public boolean parse(File file) throws IOException
    {
        FileReader reader = null;
        boolean ret = false;

        if(!file.exists())
            return ret;

        try
        {
            StringBuffer buff = null; 
            reader = new FileReader(file);
            List<String> lines = IOUtils.readLines(reader);
            for(String line : lines)
            {
                if(line.length() > 0)
                {
                    // Extract the tag if this is the start of a content type
                    String tag = line.replaceAll("(.+):", "$1");
                    ContentType type = ContentType.fromTag(tag);
                    if(type != null)
                    {
                        closeBuffer(buff);
                        buff = startBuffer(line);
                        currentType = type;
                    }
                    else if(currentType != null)
                    {
                        // Extract the name if this is the start of a page or channel
                        Matcher matcher = Pattern.compile("^  - (.+):$").matcher(line);
                        String name = matcher.find() ? matcher.group(1) : null;
                        if(name != null)
                        {
                            if(currentType == ContentType.VIDEO)
                            {
                                closeBuffer(buff);
                                buff = startBuffer(line);
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

            closeBuffer(buff);

            ret = true;
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

        return ret;
    }

    private StringBuffer startBuffer(String line)
    {
        currentVideo = currentEvent = null;
        return new StringBuffer(line);
    }

    private void closeBuffer(StringBuffer buff)
    {
        if(currentVideo != null && buff.length() > 0)
        {
            videos.put(currentVideo, buff.toString());
            buff.setLength(0);
        }

        if(currentEvent != null && buff.length() > 0)
        {
            events.put(currentEvent, buff.toString());
            buff.setLength(0);
        }

        if(currentType != null && buff.length() > 0)
        {
            contentTypes.put(currentType, buff.toString());
            buff.setLength(0);
        }
    }

    public void write(File file) throws IOException
    {
        if(file == null)
            throw new IllegalStateException("file null");

        if(config == null)
            generate();

        FileUtils.copyInputStreamToFile(IOUtils.toInputStream(config, StandardCharsets.UTF_8), file);
    }
}