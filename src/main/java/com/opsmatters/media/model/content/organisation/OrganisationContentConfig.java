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

package com.opsmatters.media.model.content.organisation;

import java.io.File;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.model.ConfigSetup;
import com.opsmatters.media.model.ConfigParser;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.FieldMap;
import com.opsmatters.media.model.content.FieldSource;
import com.opsmatters.media.model.content.video.VideoConfig;
import com.opsmatters.media.model.content.roundup.RoundupConfig;
import com.opsmatters.media.model.content.event.EventConfig;
import com.opsmatters.media.model.content.publication.WhitePaperConfig;
import com.opsmatters.media.model.content.publication.EBookConfig;
import com.opsmatters.media.model.content.post.PostConfig;
import com.opsmatters.media.model.content.project.ProjectConfig;
import com.opsmatters.media.model.content.tool.ToolConfig;
import com.opsmatters.media.model.content.job.JobConfig;
import com.opsmatters.media.util.FileUtils;
import java.io.IOException;

import static com.opsmatters.media.model.content.ContentType.*;

/**
 * Class that represents the configuration for an organisation's content.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationContentConfig extends ConfigSetup implements FieldSource
{
    private static final Logger logger = Logger.getLogger(OrganisationContentConfig.class.getName());

    public static final String SUFFIX = "-content.yml";

    private static OrganisationContentConfig defaults;
    private static OrganisationListingConfig listing;

    private String code = "";
    private String name = "";
    private String filename = "";
    private VideoConfig videos;
    private RoundupConfig roundups;
    private PostConfig posts;
    private EventConfig events;
    private WhitePaperConfig whitePapers;
    private EBookConfig ebooks;
    private ToolConfig tools;
    private ProjectConfig projects;
    private JobConfig jobs;
    private FieldMap fields;

    private Map<ContentType,ContentConfig> configs = new LinkedHashMap<ContentType,ContentConfig>();

    /**
     * Constructor that takes a name.
     */
    public OrganisationContentConfig(String name)
    {
        setName(name);
    }

    /**
     * Returns the code for this configuration.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code for this configuration.
     */
    public void setCode(String code)
    {
        this.code = code;

        Organisation organisation = Organisations.get(code);
        if(organisation != null)
            setName(organisation.getName());
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
     * Returns the import filename for this configuration.
     */
    @Override
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the import filename for this configuration.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the fields for this configuration.
     */
    @Override
    public FieldMap getFields()
    {
        return fields;
    }

    /**
     * Adds the fields for this configuration.
     */
    public void addFields(Map<String,String> fields)
    {
        if(this.fields == null)
            this.fields = new FieldMap();
        this.fields.putAll(fields);
    }

    /**
     * Returns the default content configuration.
     */
    public static OrganisationContentConfig getGlobalDefaults()
    {
        return defaults;
    }

    /**
     * Sets the default content configuration.
     */
    public static void setGlobalDefaults(OrganisationContentConfig defaults)
    {
        OrganisationContentConfig.defaults = defaults;
    }

    /**
     * Clears the default content configuration.
     */
    public static void clearGlobalDefaults()
    {
        setGlobalDefaults(null);
    }

    /**
     * Returns a list of the configurations.
     */
    public List<ContentConfig> getConfigs()
    {
        return new ArrayList<ContentConfig>(configs.values());
    }

    /**
     * Returns <CODE>true</CODE> if the configuration for the given content type has been set.
     */
    public boolean hasConfig(ContentType type)
    {
        return configs.get(type) != null;
    }

    /**
     * Returns the organisation listing configuration.
     */
    public static OrganisationListingConfig getOrganisationListing()
    {
        return listing;
    }

    /**
     * Sets the organisation listing configuration.
     */
    public static void setOrganisationListing(OrganisationListingConfig listing)
    {
        OrganisationContentConfig.listing = listing;
    }

    /**
     * Returns the video configuration.
     */
    public VideoConfig getVideos()
    {
        return videos;
    }

    /**
     * Sets the video configuration.
     */
    public void setVideos(VideoConfig videos)
    {
        this.videos = videos;
        configs.put(videos.getType(), videos);
    }

    /**
     * Returns the roundup configuration.
     */
    public RoundupConfig getRoundups()
    {
        return roundups;
    }

    /**
     * Sets the roundup configuration.
     */
    public void setRoundups(RoundupConfig roundups)
    {
        this.roundups = roundups;
        configs.put(roundups.getType(), roundups);
    }

    /**
     * Returns the post configuration.
     */
    public PostConfig getPosts()
    {
        return posts;
    }

    /**
     * Sets the post configuration.
     */
    public void setPosts(PostConfig posts)
    {
        this.posts = posts;
        configs.put(posts.getType(), posts);
    }

    /**
     * Returns the event configuration.
     */
    public EventConfig getEvents()
    {
        return events;
    }

    /**
     * Sets the event configuration.
     */
    public void setEvents(EventConfig events)
    {
        this.events = events;
        configs.put(events.getType(), events);
    }

    /**
     * Returns the white paper configuration.
     */
    public WhitePaperConfig getWhitePapers()
    {
        return whitePapers;
    }

    /**
     * Sets the white paper configuration.
     */
    public void setWhitePapers(WhitePaperConfig whitePapers)
    {
        this.whitePapers = whitePapers;
        configs.put(whitePapers.getType(), whitePapers);
    }

    /**
     * Returns the ebook configuration.
     */
    public EBookConfig getEBooks()
    {
        return ebooks;
    }

    /**
     * Sets the ebook configuration.
     */
    public void setEBooks(EBookConfig ebooks)
    {
        this.ebooks = ebooks;
        configs.put(ebooks.getType(), ebooks);
    }

    /**
     * Returns the tool configuration.
     */
    public ToolConfig getTools()
    {
        return tools;
    }

    /**
     * Sets the tool configuration.
     */
    public void setTools(ToolConfig tools)
    {
        this.tools = tools;
        configs.put(tools.getType(), tools);
    }

    /**
     * Returns the project configuration.
     */
    public ProjectConfig getProjects()
    {
        return projects;
    }

    /**
     * Sets the project configuration.
     */
    public void setProjects(ProjectConfig projects)
    {
        this.projects = projects;
        configs.put(projects.getType(), projects);
    }

    /**
     * Returns the job configuration.
     */
    public JobConfig getJobs()
    {
        return jobs;
    }

    /**
     * Sets the job configuration.
     */
    public void setJobs(JobConfig jobs)
    {
        this.jobs = jobs;
        configs.put(jobs.getType(), jobs);
    }

    /**
     * Returns a builder for the configuration.
     * @param name The name for the configuration
     * @return The builder instance.
     */
    public static Builder builder(String name)
    {
        return new Builder(name);
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder
        extends ConfigSetup.Builder<OrganisationContentConfig,Builder>
        implements ConfigParser<OrganisationContentConfig>
    {
        // The config attribute names
        private static final String CODE = "code";
        private static final String FIELDS = "fields";

        private OrganisationContentConfig ret = null;

        /**
         * Constructor that takes a name.
         * @param name The name for the configuration
         */
        public Builder(String name)
        {
            name = FileUtils.getName(name);
            ret = new OrganisationContentConfig(name);
        }

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            OrganisationContentConfig defaults = OrganisationContentConfig.getGlobalDefaults();

            if(map.containsKey(CODE))
                ret.setCode((String)map.get(CODE));

            if(map.containsKey(FIELDS))
                ret.addFields((Map<String,String>)map.get(FIELDS));

            String name = ret.getName();

            if(map.containsKey(ORGANISATION.tag()))
            {
                OrganisationListingConfig.Builder builder = OrganisationListingConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getOrganisationListing());
                ret.setOrganisationListing(builder.fields(map)
                    .parse((Map<String,Object>)map.get(ORGANISATION.tag())).build());
            }

            if(map.containsKey(VIDEO.tag()))
            {
                VideoConfig.Builder builder = VideoConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getVideos());
                ret.setVideos(builder.fields(map)
                    .parse((Map<String,Object>)map.get(VIDEO.tag())).build());
            }

            if(map.containsKey(ROUNDUP.tag()))
            {
                RoundupConfig.Builder builder = RoundupConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getRoundups());
                ret.setRoundups(builder.fields(map)
                    .parse((Map<String,Object>)map.get(ROUNDUP.tag())).build());
            }

            if(map.containsKey(EVENT.tag()))
            {
                EventConfig.Builder builder = EventConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getEvents());
                ret.setEvents(builder.fields(map)
                    .parse((Map<String,Object>)map.get(EVENT.tag())).build());
            }

            if(map.containsKey(WHITE_PAPER.tag()))
            {
                WhitePaperConfig.Builder builder = WhitePaperConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getWhitePapers());
                ret.setWhitePapers(builder.fields(map)
                    .parse((Map<String,Object>)map.get(WHITE_PAPER.tag())).build());
            }

            if(map.containsKey(EBOOK.tag()))
            {
                EBookConfig.Builder builder = EBookConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getEBooks());
                ret.setEBooks(builder.fields(map)
                    .parse((Map<String,Object>)map.get(EBOOK.tag())).build());
            }

            if(map.containsKey(POST.tag()))
            {
                PostConfig.Builder builder = PostConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getPosts());
                ret.setPosts(builder.fields(map)
                    .parse((Map<String,Object>)map.get(POST.tag())).build());
            }

            if(map.containsKey(PROJECT.tag()))
            {
                ProjectConfig.Builder builder = ProjectConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getProjects());
                ret.setProjects(builder.fields(map)
                    .parse((Map<String,Object>)map.get(PROJECT.tag())).build());
            }

            if(map.containsKey(TOOL.tag()))
            {
                ToolConfig.Builder builder = ToolConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getTools());
                ret.setTools(builder.fields(map)
                    .parse((Map<String,Object>)map.get(TOOL.tag())).build());
            }

            if(map.containsKey(JOB.tag()))
            {
                JobConfig.Builder builder = JobConfig.builder(name);
                if(defaults != null)
                    builder = builder.copy(defaults.getJobs());
                ret.setJobs(builder.fields(map)
                    .parse((Map<String,Object>)map.get(JOB.tag())).build());
            }

            return this;
        }

        /**
         * Returns this object.
         * @return This object
         */
        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        @Override
        public OrganisationContentConfig build() throws IOException
        {
            read(this);
            ret.setFilename(filename);
            return ret;
        }
    }
}