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
import com.opsmatters.media.cache.provider.VideoProviders;
import com.opsmatters.media.cache.organisation.Organisations;
import com.opsmatters.media.cache.organisation.OrganisationSites;
import com.opsmatters.media.model.provider.VideoProviderId;
import com.opsmatters.media.model.provider.VideoProvider;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.video.VideoConfig;
import com.opsmatters.media.model.content.post.PostConfig;
import com.opsmatters.media.model.content.post.RoundupPostConfig;
import com.opsmatters.media.model.content.project.ProjectConfig;
import com.opsmatters.media.model.content.tool.ToolConfig;
import com.opsmatters.media.model.content.organisation.OrganisationListing;
import com.opsmatters.media.model.content.organisation.OrganisationTabs;
import com.opsmatters.media.model.content.ContentSiteSettings;
import com.opsmatters.media.model.content.crawler.CrawlerWebPage;
import com.opsmatters.media.model.content.crawler.CrawlerVideoChannel;
import com.opsmatters.media.util.StringUtils;

import static com.opsmatters.media.model.provider.VideoProviderId.*;

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
    Map<VideoProviderId,Boolean> videos = new HashMap<VideoProviderId,Boolean>();

    /**
     * Default constructor.
     */
    public ConfigGeneratorFields()
    {
    }

    /**
     * Clears the fields.
     */
    public void clear()
    {
        code = "";
        name = "";
        tags = "";
        channelId = "";
        userId = "";
        website = "";
        blogUrl = "";
        features = "";
        types.clear();
        videos.clear();
    }

    /**
     * Sets the fields from an organisation, site and listing.
     */
    public void setFields(Organisation organisation, OrganisationSite organisationSite, OrganisationListing listing)
    {
        organisation = Organisations.get(organisation.getCode());
        organisationSite = OrganisationSites.get(organisationSite.getSiteId(), organisationSite.getCode());

        setCode(organisation.getCode());
        setName(organisation.getName());
        setWebsite(organisation.getWebsite());

        if(organisationSite.hasListing())
        {
            OrganisationTabs tabs = listing.getTabs();

            if(organisation.hasSettings())
            {
                setVideos(organisation.hasVideoConfig());
                setRoundups(organisation.hasRoundupPostConfig());
                setPosts(organisation.hasPostConfig());
                setEvents(organisation.hasEventConfig());
                setPublications(organisation.hasPublicationConfig());
                setProjects(organisation.hasProjectConfig());
                setTools(organisation.hasToolConfig());
                setFields(organisation, organisationSite.getSiteId(), tabs);
            }
            else
            {
                setVideos(tabs.contains(ContentType.VIDEO));
                setRoundups(tabs.contains(ContentType.ROUNDUP));
                setPosts(tabs.contains(ContentType.POST));
                setEvents(tabs.contains(ContentType.EVENT));
                setPublications(tabs.contains(ContentType.PUBLICATION));
                setProjects(listing.hasProjects());
                setTools(listing.hasTools());
            }

            // Set the video channel from the listing
            if(getVideos() && !hasChannelId())
            {
                String youtube = listing.getYouTube();
                if(youtube != null && youtube.length() > 0)
                {
                    VideoProvider provider = VideoProviders.get(YOUTUBE);
                    setChannelId(provider.getChannelId(youtube));
                    setYoutube(true);
                }

                String vimeo = listing.getVimeo();
                if(vimeo != null && vimeo.length() > 0)
                {
                    VideoProvider provider = VideoProviders.get(VIMEO);
                    setChannelId(provider.getChannelId(vimeo));
                    setVimeo(true);
                }
            }
        }
        else
        {
            if(organisation.hasSettings())
            {
                setPosts(organisation.hasPostConfig());
                setFields(organisation, organisationSite.getSiteId());
            }
            else
            {
                setPosts(true);
            }
        }
    }

    /**
     * Set the fields for the content.
     */
    private void setFields(Organisation organisation, String siteId, OrganisationTabs tabs)
    {
        String channelId = null;
        String userId = null;
        String blogUrl = null;
        String features = null;
        String tags = null;

        if(organisation.hasVideoConfig())
        {
            VideoConfig videos = organisation.getVideoConfig();

            ContentSiteSettings settings = OrganisationSites.getSettings(siteId, videos);
            if(settings != null)
            {
                if(tags == null)
                    tags = settings.getTags();
            }

            if(videos.numChannels() > 0)
            {
                CrawlerVideoChannel channel = videos.getChannel(0);
                channelId = channel.getChannelId();
                userId = channel.getUserId();

                setYoutube(videos.hasChannel(VideoProviders.get(YOUTUBE).getName()));
                setVimeo(videos.hasChannel(VideoProviders.get(VIMEO).getName()));
                setWistia(videos.hasChannel(VideoProviders.get(WISTIA).getName()));
            }
        }
        else // Adding videos to existing config
        {
            setVideos(tabs.contains(ContentType.VIDEO));
        }

        if(organisation.hasRoundupPostConfig())
        {
            RoundupPostConfig roundups = organisation.getRoundupPostConfig();

            ContentSiteSettings settings = OrganisationSites.getSettings(siteId, roundups);
            if(settings != null)
            {
                if(tags == null)
                    tags = settings.getTags();
            }

            if(roundups.numPages() > 0)
            {
                CrawlerWebPage page = roundups.getPage(0);
                blogUrl = page.getTeasers().getUrl();
            }
        }

        if(organisation.hasPostConfig())
        {
            PostConfig posts = organisation.getPostConfig();

            ContentSiteSettings settings = OrganisationSites.getSettings(siteId, posts);
            if(settings != null)
            {
                if(tags == null)
                    tags = settings.getTags();
            }
        }

        if(organisation.hasProjectConfig())
        {
            ProjectConfig projects = organisation.getProjectConfig();

            ContentSiteSettings settings = OrganisationSites.getSettings(siteId, projects);
            if(settings != null)
            {
                if(features == null)
                    features = settings.getFeatures();
            }
        }

        if(organisation.hasToolConfig())
        {
            ToolConfig tools = organisation.getToolConfig();

            ContentSiteSettings settings = OrganisationSites.getSettings(siteId, tools);
            if(settings != null)
            {
                if(features == null)
                    features = settings.getFeatures();
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
     * Set the fields for post content.
     */
    private void setFields(Organisation organisation, String siteId)
    {
        String tags = null;

        if(organisation.hasPostConfig())
        {
            PostConfig posts = organisation.getPostConfig();

            ContentSiteSettings settings = OrganisationSites.getSettings(siteId, posts);
            if(settings != null)
            {
                if(tags == null)
                    tags = settings.getTags();
            }
        }

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
     * Returns <CODE>true</CODE> if the channel id has been set.
     */
    public boolean hasChannelId()
    {
        return channelId != null && channelId.length() > 0;
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
     * Returns <CODE>true</CODE> if the user id has been set.
     */
    public boolean hasUserId()
    {
        return userId != null && userId.length() > 0;
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
    public Boolean hasType(ContentType type)
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
     * Returns the list of video providers.
     */
    public List<VideoProviderId> getVideoProviders()
    {
        return new ArrayList<VideoProviderId>(videos.keySet());
    }

    /**
     * Returns <CODE>true</CODE> if the given video provider is enabled.
     */
    public Boolean hasProvider(VideoProviderId providerId)
    {
        return videos.containsKey(providerId);
    }

    /**
     * Set to <CODE>true</CODE> if the given video provider is enabled.
     */
    private void setProvider(boolean enabled, VideoProviderId providerId)
    {
        if(enabled)
            videos.put(providerId, Boolean.TRUE);
        else
            videos.remove(providerId);
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