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

package com.opsmatters.media.config.content;

import java.io.File;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.Fields;

/**
 * Class that represents the configuration for an organisation's content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationContentConfiguration extends ContentConfiguration
{
    private static final Logger logger = Logger.getLogger(OrganisationContentConfiguration.class.getName());

    public static final String TYPE = "content";
    public static final String TITLE = "Content";

    private static final String DEFAULTS = "content.yml";

    private static OrganisationContentConfiguration defaults;

    private String code = "";
    private String organisation = "";
    private VideoConfiguration videos;
    private RoundupConfiguration roundups;
    private PostConfiguration posts;
    private EventConfiguration events;
    private WhitePaperConfiguration whitePapers;
    private EBookConfiguration ebooks;
    private ToolConfiguration tools;
    private ProjectConfiguration projects;

    private Map<String,ContentConfiguration> configurations = new LinkedHashMap<String,ContentConfiguration>();

    /**
     * Default constructor.
     */
    public OrganisationContentConfiguration(String name)
    {
        super(name);
    }

    /**
     * Returns the type for this configuration.
     */
    @Override
    public String getType()
    {
        return TYPE;
    }

    /**
     * Returns the title for this configuration.
     */
    @Override
    public String getTitle()
    {
        return TITLE;
    }

    /**
     * Initialise the default content configuration.
     */
    private static void readGlobalDefaults(String directory)
    {
        // Read the defaults and output formats
        if(defaults == null)
        {
            defaults = new OrganisationContentConfiguration(DEFAULTS);
            File file = new File(directory, DEFAULTS);
            defaults.read(file.getAbsolutePath());
        }
    }

    /**
     * Returns the default content configuration.
     */
    public static OrganisationContentConfiguration getGlobalDefaults()
    {
        return defaults;
    }

    /**
     * Returns the organisation code for this configuration.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the organisation code for this configuration.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the organisation name for this configuration.
     */
    public String getOrganisation()
    {
        return organisation;
    }

    /**
     * Sets the organisation name for this configuration.
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }

    /**
     * Returns a list of the configurations.
     */
    public List<ContentConfiguration> getConfigurations()
    {
        return new ArrayList<ContentConfiguration>(configurations.values());
    }

    /**
     * Returns <CODE>true</CODE> if the given configuration type has been set.
     */
    public boolean hasConfiguration(String type)
    {
        return configurations.get(type) != null;
    }

    /**
     * Returns the video configuration.
     */
    public VideoConfiguration getVideos()
    {
        return videos;
    }

    /**
     * Sets the video configuration.
     */
    public void setVideos(VideoConfiguration videos)
    {
        this.videos = videos;
        configurations.put(VideoConfiguration.TYPE, videos);
    }

    /**
     * Returns the roundup configuration.
     */
    public RoundupConfiguration getRoundups()
    {
        return roundups;
    }

    /**
     * Sets the roundup configuration.
     */
    public void setRoundups(RoundupConfiguration roundups)
    {
        this.roundups = roundups;
        configurations.put(RoundupConfiguration.TYPE, roundups);
    }

    /**
     * Returns the post configuration.
     */
    public PostConfiguration getPosts()
    {
        return posts;
    }

    /**
     * Sets the post configuration.
     */
    public void setPosts(PostConfiguration posts)
    {
        this.posts = posts;
        configurations.put(PostConfiguration.TYPE, posts);
    }

    /**
     * Returns the event configuration.
     */
    public EventConfiguration getEvents()
    {
        return events;
    }

    /**
     * Sets the event configuration.
     */
    public void setEvents(EventConfiguration events)
    {
        this.events = events;
        configurations.put(EventConfiguration.TYPE, events);
    }

    /**
     * Returns the white paper configuration.
     */
    public WhitePaperConfiguration getWhitePapers()
    {
        return whitePapers;
    }

    /**
     * Sets the white paper configuration.
     */
    public void setWhitePapers(WhitePaperConfiguration whitePapers)
    {
        this.whitePapers = whitePapers;
        configurations.put(WhitePaperConfiguration.TYPE, whitePapers);
    }

    /**
     * Returns the ebook configuration.
     */
    public EBookConfiguration getEBooks()
    {
        return ebooks;
    }

    /**
     * Sets the ebook configuration.
     */
    public void setEBooks(EBookConfiguration ebooks)
    {
        this.ebooks = ebooks;
        configurations.put(EBookConfiguration.TYPE, ebooks);
    }

    /**
     * Returns the tool configuration.
     */
    public ToolConfiguration getTools()
    {
        return tools;
    }

    /**
     * Sets the tool configuration.
     */
    public void setTools(ToolConfiguration tools)
    {
        this.tools = tools;
        configurations.put(ToolConfiguration.TYPE, tools);
    }

    /**
     * Returns the project configuration.
     */
    public ProjectConfiguration getProjects()
    {
        return projects;
    }

    /**
     * Sets the project configuration.
     */
    public void setProjects(ProjectConfiguration projects)
    {
        this.projects = projects;
        configurations.put(ProjectConfiguration.TYPE, projects);
    }

    /**
     * Reads the configuration from the given YAML Document.
     */
    protected void parseDocument(Object doc)
    {
        if(doc instanceof Map)
        {
            Map map = (Map)doc;

            if(map.containsKey(Fields.CODE))
                setCode((String)map.get(Fields.CODE));

            if(map.containsKey(Fields.ORGANISATION))
                setOrganisation((String)map.get(Fields.ORGANISATION));

            if(map.containsKey(VideoConfiguration.TYPE))
            {
                VideoConfiguration config = new VideoConfiguration(defaults.getVideos());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(VideoConfiguration.TYPE));
                setVideos(config);
            }

            if(map.containsKey(RoundupConfiguration.TYPE))
            {
                RoundupConfiguration config = new RoundupConfiguration(defaults.getRoundups());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(RoundupConfiguration.TYPE));
                setRoundups(config);
            }

            if(map.containsKey(EventConfiguration.TYPE))
            {
                EventConfiguration config = new EventConfiguration(defaults.getEvents());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(EventConfiguration.TYPE));
                setEvents(config);
            }

            if(map.containsKey(WhitePaperConfiguration.TYPE))
            {
                WhitePaperConfiguration config = new WhitePaperConfiguration(defaults.getWhitePapers());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(WhitePaperConfiguration.TYPE));
                setWhitePapers(config);
            }

            if(map.containsKey(EBookConfiguration.TYPE))
            {
                EBookConfiguration config = new EBookConfiguration(defaults.getEBooks());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(EBookConfiguration.TYPE));
                setEBooks(config);
            }

            if(map.containsKey(PostConfiguration.TYPE))
            {
                PostConfiguration config = new PostConfiguration(defaults.getPosts());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(PostConfiguration.TYPE));
                setPosts(config);
            }

            if(map.containsKey(ProjectConfiguration.TYPE))
            {
                ProjectConfiguration config = new ProjectConfiguration(defaults.getProjects());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ProjectConfiguration.TYPE));
                setProjects(config);
            }

            if(map.containsKey(ToolConfiguration.TYPE))
            {
                ToolConfiguration config = new ToolConfiguration(defaults.getTools());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ToolConfiguration.TYPE));
                setTools(config);
            }
        }
    }

    /**
     * Sets the content defaults.
     */
    private void setContentDefaults(ContentConfiguration config, Map<Object,Object> map)
    {
        for(Map.Entry<Object, Object> entry : map.entrySet())
        {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if(value instanceof String)
                config.getFields().put(key, (String)value);
        }
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder
    {
        protected String name = "";
        protected String directory = "";
        protected String filename = "";

        /**
         * Default constructor.
         */
        public Builder()
        {
        }

        /**
         * Sets the name of the configuration.
         * <P>
         * @param name The name of the configuration
         * @return This object
         */
        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        /**
         * Sets the config directory.
         * @param key The config directory
         * @return This object
         */
        public Builder directory(String directory)
        {
            this.directory = directory;
            return this;
        }

        /**
         * Sets the config filename.
         * @param key The config filename
         * @return This object
         */
        public Builder filename(String filename)
        {
            this.filename = filename;
            return this;
        }

        /**
         * Returns the configuration
         * @return The configuration
         */
        public OrganisationContentConfiguration build(boolean read)
        {
            OrganisationContentConfiguration ret = new OrganisationContentConfiguration(name);

            if(read)
            {
                OrganisationContentConfiguration.readGlobalDefaults(directory);

                // Read the config file
                File config = new File(directory, filename);
                ret.read(config.getAbsolutePath());
            }

            return ret;
        }
    }
}