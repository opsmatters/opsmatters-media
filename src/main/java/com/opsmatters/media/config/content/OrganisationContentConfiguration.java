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
import com.opsmatters.media.model.content.ContentType;

/**
 * Class that represents the configuration for an organisation's content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationContentConfiguration extends ContentConfiguration
{
    private static final Logger logger = Logger.getLogger(OrganisationContentConfiguration.class.getName());

    public static final String SUFFIX = "-content.yml";
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

    private Map<ContentType,ContentConfiguration> configurations = new LinkedHashMap<ContentType,ContentConfiguration>();

    /**
     * Default constructor.
     */
    public OrganisationContentConfiguration(String name)
    {
        super(name);
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
     * Returns <CODE>true</CODE> if the configuration for the given content type has been set.
     */
    public boolean hasConfiguration(ContentType type)
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
        configurations.put(videos.getType(), videos);
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
        configurations.put(roundups.getType(), roundups);
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
        configurations.put(posts.getType(), posts);
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
        configurations.put(events.getType(), events);
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
        configurations.put(whitePapers.getType(), whitePapers);
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
        configurations.put(ebooks.getType(), ebooks);
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
        configurations.put(tools.getType(), tools);
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
        configurations.put(projects.getType(), projects);
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

            if(map.containsKey(ContentType.VIDEO.tag()))
            {
                VideoConfiguration config = new VideoConfiguration(defaults.getVideos());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.VIDEO.tag()));
                setVideos(config);
            }

            if(map.containsKey(ContentType.ROUNDUP.tag()))
            {
                RoundupConfiguration config = new RoundupConfiguration(defaults.getRoundups());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.ROUNDUP.tag()));
                setRoundups(config);
            }

            if(map.containsKey(ContentType.EVENT.tag()))
            {
                EventConfiguration config = new EventConfiguration(defaults.getEvents());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.EVENT.tag()));
                setEvents(config);
            }

            if(map.containsKey(ContentType.WHITE_PAPER.tag()))
            {
                WhitePaperConfiguration config = new WhitePaperConfiguration(defaults.getWhitePapers());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.WHITE_PAPER.tag()));
                setWhitePapers(config);
            }

            if(map.containsKey(ContentType.EBOOK.tag()))
            {
                EBookConfiguration config = new EBookConfiguration(defaults.getEBooks());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.EBOOK.tag()));
                setEBooks(config);
            }

            if(map.containsKey(ContentType.POST.tag()))
            {
                PostConfiguration config = new PostConfiguration(defaults.getPosts());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.POST.tag()));
                setPosts(config);
            }

            if(map.containsKey(ContentType.PROJECT.tag()))
            {
                ProjectConfiguration config = new ProjectConfiguration(defaults.getProjects());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.PROJECT.tag()));
                setProjects(config);
            }

            if(map.containsKey(ContentType.TOOL.tag()))
            {
                ToolConfiguration config = new ToolConfiguration(defaults.getTools());
                setContentDefaults(config, map);
                config.parseDocument((Map<String,Object>)map.get(ContentType.TOOL.tag()));
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