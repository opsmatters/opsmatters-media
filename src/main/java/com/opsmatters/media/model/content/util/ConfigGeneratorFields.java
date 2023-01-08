/*
 * Copyright 2021 Gerald Curley
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
package com.opsmatters.media.model.content.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.opsmatters.media.cache.organisation.OrganisationSites;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.video.VideoConfig;
import com.opsmatters.media.model.content.video.VideoProvider;
import com.opsmatters.media.model.content.event.EventConfig;
import com.opsmatters.media.model.content.event.EventProvider;
import com.opsmatters.media.model.content.post.PostConfig;
import com.opsmatters.media.model.content.post.RoundupPostConfig;
import com.opsmatters.media.model.content.project.ProjectConfig;
import com.opsmatters.media.model.content.tool.ToolConfig;
import com.opsmatters.media.model.content.organisation.OrganisationListing;
import com.opsmatters.media.model.content.organisation.OrganisationTabs;
import com.opsmatters.media.model.content.organisation.OrganisationContentConfig;
import com.opsmatters.media.model.content.organisation.OrganisationContentType;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.content.video.VideoProvider.*;
import static com.opsmatters.media.model.content.event.EventProvider.*;

/**
 * Class that represents the set of field used to generate a config file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ConfigGeneratorFields implements java.io.Serializable
{
    private String code = "";
    private String name = "";
    private String tags = "";
    private String channelId = "";
    private String userId = "";
    private String website = "";
    private String blogUrl = "";
    private String features = "";

    Map<ContentType,Boolean> types = new HashMap<ContentType,Boolean>();
    Map<VideoProvider,Boolean> videos = new HashMap<VideoProvider,Boolean>();
    Map<EventProvider,Boolean> events = new HashMap<EventProvider,Boolean>();

    /**
     * Default constructor.
     */
    public ConfigGeneratorFields()
    {
    }

    /**
     * Sets the fields from an organisation, site and listing.
     */
    public void set(Organisation organisation, OrganisationSite organisationSite, OrganisationListing listing)
    {
        setCode(organisation.getCode());
        setName(organisation.getName());
        setWebsite(organisation.getWebsite());

        if(organisationSite.hasListing())
        {
            OrganisationTabs tabs = listing.getTabs();
            setVideos(tabs.contains(ContentType.VIDEO));
            setRoundups(tabs.contains(ContentType.ROUNDUP));
            setPosts(tabs.contains(ContentType.POST));
            setEvents(tabs.contains(ContentType.EVENT));
            setPublications(tabs.contains(ContentType.PUBLICATION));

            setProjects(listing.hasProjects());
            setTools(listing.hasTools());
            setJobs(listing.hasJobs());

            String youtube = listing.getYouTube();
            if(youtube != null && youtube.length() > 0)
            {
                setChannelId(youtube.substring(youtube.lastIndexOf("/")+1));
                setYoutube(true);
            }

            String vimeo = listing.getVimeo();
            if(vimeo != null && vimeo.length() > 0)
            {
                setChannelId(vimeo.substring(vimeo.lastIndexOf("/")+1));
                setVimeo(true);
            }

            setWebinars(getEvents());
        }
        else
        {
            setPosts(true);
        }
    }

    /**
     * Sets the fields from an organisation config.
     */
    public void set(Organisation organisation, OrganisationSite organisationSite, OrganisationContentConfig config)
    {
        String siteId = organisationSite.getSiteId();

        setCode(organisation.getCode());
        setName(organisation.getName());
        setWebsite(organisation.getWebsite());

        setVideos(config.hasVideos());
        setRoundups(config.hasRoundups());
        setPosts(config.hasPosts());
        setEvents(config.hasEvents());
        setPublications(config.hasPublications());
        setProjects(config.hasProjects());
        setTools(config.hasTools());
        setJobs(config.hasJobs());

        String channelId = null;
        String userId = null;
        String blogUrl = null;
        String features = null;
        String tags = null;

        if(config.hasVideos())
        {
            VideoConfig videos = config.getVideos();

            OrganisationContentType type = OrganisationSites.getContentType(siteId, videos);
            if(type != null)
            {
                if(tags == null)
                    tags = type.getTags();
            }

            if(videos.numChannels() > 0)
            {
                CrawlerVideoChannel channel = videos.getChannel(0);
                channelId = channel.getChannelId();
                userId = channel.getUserId();

                setYoutube(videos.hasChannel(YOUTUBE.value()));
                setVimeo(videos.hasChannel(VIMEO.value()));
                setWistia(videos.hasChannel(WISTIA.value()));
            }
        }

        if(config.hasRoundups())
        {
            RoundupPostConfig roundups = config.getRoundups();

            OrganisationContentType type = OrganisationSites.getContentType(siteId, roundups);
            if(type != null)
            {
                if(tags == null)
                    tags = type.getTags();
            }

            if(roundups.numPages() > 0)
            {
                CrawlerWebPage page = roundups.getPage(0);
                blogUrl = page.getUrl(0);
            }
        }

        if(config.hasEvents())
        {
            EventConfig events = config.getEvents();

            if(events.numPages() > 0)
            {
                setWebinars(events.hasPage(WEBINARS.value()));
                setZoom(events.hasPage(ZOOM.value()));
                setBrighttalk(events.hasPage(BRIGHTTALK.value()));
                setGotowebinar(events.hasPage(GOTOWEBINAR.value()));
                setOn24(events.hasPage(ON24.value()));
                setLivestorm(events.hasPage(LIVESTORM.value()));
            }
        }

        if(config.hasPosts())
        {
            PostConfig posts = config.getPosts();

            OrganisationContentType type = OrganisationSites.getContentType(siteId, posts);
            if(type != null)
            {
                if(tags == null)
                    tags = type.getTags();
            }
        }

        if(config.hasProjects())
        {
            ProjectConfig projects = config.getProjects();

            OrganisationContentType type = OrganisationSites.getContentType(siteId, projects);
            if(type != null)
            {
                if(features == null)
                    features = type.getFeatures();
            }
        }

        if(config.hasTools())
        {
            ToolConfig tools = config.getTools();

            OrganisationContentType type = OrganisationSites.getContentType(siteId, tools);
            if(type != null)
            {
                if(features == null)
                    features = type.getFeatures();
            }
        }

        if(channelId != null)
            setChannelId(channelId);
        if(userId != null)
            setUserId(userId);
        if(blogUrl != null)
            setBlogUrl(blogUrl);
        if(features != null)
            setFeatures(features);
        if(tags != null)
            setTags(tags);
    }

    /**
     * Returns the code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the tags.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Returns the list of tags.
     */
    public List<String> getTagsList()
    {
        return StringUtils.toList(getTags());
    }

    /**
     * Sets the tags.
     */
    public void setTags(String tags)
    {
        this.tags = tags;
    }

    /**
     * Sets the list of tags.
     */
    public void setTagsList(List<String> tags)
    {
        setTags(StringUtils.fromList(tags));
    }

    /**
     * Returns the channel id.
     */
    public String getChannelId()
    {
        return channelId;
    }

    /**
     * Sets the channel id.
     */
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    /**
     * Returns the user id.
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Sets the user id.
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * Returns the website.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the website.
     */
    public void setWebsite(String website)
    {
        this.website = website;
    }

    /**
     * Returns the blog url.
     */
    public String getBlogUrl()
    {
        return blogUrl;
    }

    /**
     * Sets the blog url.
     */
    public void setBlogUrl(String blogUrl)
    {
        this.blogUrl = blogUrl;
    }

    /**
     * Returns the features.
     */
    public String getFeatures()
    {
        return features;
    }

    /**
     * Returns the list of features.
     */
    public List<String> getFeaturesList()
    {
        return StringUtils.toList(getFeatures());
    }

    /**
     * Sets the features.
     */
    public void setFeatures(String features)
    {
        this.features = features;
    }

    /**
     * Sets the list of features.
     */
    public void setFeaturesList(List<String> features)
    {
        setFeatures(StringUtils.fromList(features));
    }

    /**
     * Returns the list of content types.
     */
    public List<ContentType> getContentTypes()
    {
        return new ArrayList<ContentType>(types.keySet());
    }

    /**
     * Returns <CODE>true</CODE> if the given content type is enabled.
     */
    private Boolean hasType(ContentType type)
    {
        return types.containsKey(type);
    }

    /**
     * Set to <CODE>true</CODE> if the given content type is enabled.
     */
    private void setType(boolean enabled, ContentType type)
    {
        if(enabled)
            types.put(type, Boolean.TRUE);
        else
            types.remove(type);
    }

    /**
     * Returns <CODE>true</CODE> if videos are enabled.
     */
    public Boolean getVideos()
    {
        return hasType(ContentType.VIDEO);
    }

    /**
     * Set to <CODE>true</CODE> if videos are enabled.
     */
    public void setVideos(Boolean videos)
    {
        setType(videos, ContentType.VIDEO);
    }

    /**
     * Returns <CODE>true</CODE> if roundups are enabled.
     */
    public Boolean getRoundups()
    {
        return hasType(ContentType.ROUNDUP);
    }

    /**
     * Set to <CODE>true</CODE> if roundups are enabled.
     */
    public void setRoundups(Boolean roundups)
    {
        setType(roundups, ContentType.ROUNDUP);
    }

    /**
     * Returns <CODE>true</CODE> if events are enabled.
     */
    public Boolean getEvents()
    {
        return hasType(ContentType.EVENT);
    }

    /**
     * Set to <CODE>true</CODE> if events are enabled.
     */
    public void setEvents(Boolean events)
    {
        setType(events, ContentType.EVENT);
    }

    /**
     * Returns <CODE>true</CODE> if publications are enabled.
     */
    public Boolean getPublications()
    {
        return hasType(ContentType.PUBLICATION);
    }

    /**
     * Set to <CODE>true</CODE> if publications are enabled.
     */
    public void setPublications(Boolean publications)
    {
        setType(publications, ContentType.PUBLICATION);
    }

    /**
     * Returns <CODE>true</CODE> if posts are enabled.
     */
    public Boolean getPosts()
    {
        return hasType(ContentType.POST);
    }

    /**
     * Set to <CODE>true</CODE> if posts are enabled.
     */
    public void setPosts(Boolean posts)
    {
        setType(posts, ContentType.POST);
    }

    /**
     * Returns <CODE>true</CODE> if projects are enabled.
     */
    public Boolean getProjects()
    {
        return hasType(ContentType.PROJECT);
    }

    /**
     * Set to <CODE>true</CODE> if projects are enabled.
     */
    public void setProjects(Boolean projects)
    {
        setType(projects, ContentType.PROJECT);
    }

    /**
     * Returns <CODE>true</CODE> if tools are enabled.
     */
    public Boolean getTools()
    {
        return hasType(ContentType.TOOL);
    }

    /**
     * Set to <CODE>true</CODE> if tools are enabled.
     */
    public void setTools(Boolean tools)
    {
        setType(tools, ContentType.TOOL);
    }

    /**
     * Returns <CODE>true</CODE> if jobs are enabled.
     */
    public Boolean getJobs()
    {
        return hasType(ContentType.JOB);
    }

    /**
     * Set to <CODE>true</CODE> if tools are enabled.
     */
    public void setJobs(Boolean jobs)
    {
        setType(jobs, ContentType.JOB);
    }

    /**
     * Returns the list of event providers.
     */
    public List<EventProvider> getEventProviders()
    {
        return new ArrayList<EventProvider>(events.keySet());
    }

    /**
     * Returns <CODE>true</CODE> if the given event provider is enabled.
     */
    private Boolean hasProvider(EventProvider provider)
    {
        return events.containsKey(provider);
    }

    /**
     * Set to <CODE>true</CODE> if the given event provider is enabled.
     */
    private void setProvider(boolean enabled, EventProvider provider)
    {
        if(enabled)
            events.put(provider, Boolean.TRUE);
        else
            events.remove(provider);
    }

    /**
     * Returns <CODE>true</CODE> if webinar events are enabled.
     */
    public Boolean getWebinars()
    {
        return hasProvider(WEBINARS);
    }

    /**
     * Set to <CODE>true</CODE> if webinar events are enabled.
     */
    public void setWebinars(Boolean webinars)
    {
        setProvider(webinars, WEBINARS);
    }

    /**
     * Returns <CODE>true</CODE> if Zoom events are enabled.
     */
    public Boolean getZoom()
    {
        return hasProvider(ZOOM);
    }

    /**
     * Set to <CODE>true</CODE> if Zoom events are enabled.
     */
    public void setZoom(Boolean zoom)
    {
        setProvider(zoom, ZOOM);
    }

    /**
     * Returns <CODE>true</CODE> if BrightTalk events are enabled.
     */
    public Boolean getBrighttalk()
    {
        return hasProvider(BRIGHTTALK);
    }

    /**
     * Set to <CODE>true</CODE> if BrightTalk events are enabled.
     */
    public void setBrighttalk(Boolean brightTalk)
    {
        setProvider(brightTalk, BRIGHTTALK);
    }

    /**
     * Returns <CODE>true</CODE> if GoToWebinar events are enabled.
     */
    public Boolean getGotowebinar()
    {
        return hasProvider(GOTOWEBINAR);
    }

    /**
     * Set to <CODE>true</CODE> if GoToWebinar events are enabled.
     */
    public void setGotowebinar(Boolean goToWebinar)
    {
        setProvider(goToWebinar, GOTOWEBINAR);
    }

    /**
     * Returns <CODE>true</CODE> if on24 events are enabled.
     */
    public Boolean getOn24()
    {
        return hasProvider(ON24);
    }

    /**
     * Set to <CODE>true</CODE> if on24 events are enabled.
     */
    public void setOn24(Boolean on24)
    {
        setProvider(on24, ON24);
    }

    /**
     * Returns <CODE>true</CODE> if Livestorm events are enabled.
     */
    public Boolean getLivestorm()
    {
        return hasProvider(LIVESTORM);
    }

    /**
     * Set to <CODE>true</CODE> if Livestorm events are enabled.
     */
    public void setLivestorm(Boolean livestorm)
    {
        setProvider(livestorm, LIVESTORM);
    }

    /**
     * Returns the list of video providers.
     */
    public List<VideoProvider> getVideoProviders()
    {
        return new ArrayList<VideoProvider>(videos.keySet());
    }

    /**
     * Returns <CODE>true</CODE> if the given video provider is enabled.
     */
    private Boolean hasProvider(VideoProvider provider)
    {
        return videos.containsKey(provider);
    }

    /**
     * Set to <CODE>true</CODE> if the given video provider is enabled.
     */
    private void setProvider(boolean enabled, VideoProvider provider)
    {
        if(enabled)
            videos.put(provider, Boolean.TRUE);
        else
            videos.remove(provider);
    }

    /**
     * Returns <CODE>true</CODE> if YouTube videos are enabled.
     */
    public Boolean getYoutube()
    {
        return hasProvider(YOUTUBE);
    }

    /**
     * Set to <CODE>true</CODE> if YouTube videos are enabled.
     */
    public void setYoutube(Boolean youtube)
    {
        setProvider(youtube, YOUTUBE);
    }

    /**
     * Returns <CODE>true</CODE> if Vimeo videos are enabled.
     */
    public Boolean getVimeo()
    {
        return hasProvider(VIMEO);
    }

    /**
     * Set to <CODE>true</CODE> if Vimeo videos are enabled.
     */
    public void setVimeo(Boolean vimeo)
    {
        setProvider(vimeo, VIMEO);
    }

    /**
     * Returns <CODE>true</CODE> if Wistia videos are enabled.
     */
    public Boolean getWistia()
    {
        return hasProvider(WISTIA);
    }

    /**
     * Set to <CODE>true</CODE> if Wistia videos are enabled.
     */
    public void setWistia(Boolean wistia)
    {
        setProvider(wistia, WISTIA);
    }
}