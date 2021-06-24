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
package com.opsmatters.media.config.content.generator;

import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.content.Organisation;
import com.opsmatters.media.model.content.OrganisationListing;
import com.opsmatters.media.model.content.OrganisationTabs;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class that represents the set of field used to generate a config file.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class GeneratorFields implements java.io.Serializable
{
    private String code = "";
    private String name = "";
    private String tag = "";
    private String hashtags = "";
    private String tags = "";
    private String channelId = "";
    private String website = "";
    private String blogUrl = "";
    private String features = "";

    private Boolean videos = false;
    private Boolean roundups = false;
    private Boolean posts = false;
    private Boolean events = false;
    private Boolean whitePapers = false;
    private Boolean ebooks = false;
    private Boolean projects = false;
    private Boolean tools = false;
    private Boolean jobs = false;

    private List<EventPage> eventPages = new ArrayList<EventPage>();

    /**
     * Default constructor.
     */
    public GeneratorFields()
    {
    }

    /**
     * Sets the fields from an organisation and listing.
     */
    public void set(Organisation organisation, OrganisationListing listing)
    {
        setCode(organisation.getCode());
        setName(organisation.getName());
        setTag(getName().toLowerCase().replaceAll(" ","-").replaceAll("\\.|&",""));
        setWebsite(organisation.getWebsite());

        String youtube = listing.getYouTube();
        if(youtube != null && youtube.length() > 0)
            setChannelId(youtube.substring(youtube.lastIndexOf("/")+1));

        OrganisationTabs tabs = listing.getTabs();
        setVideos(tabs.contains(ContentType.VIDEO));
        setRoundups(tabs.contains(ContentType.ROUNDUP));
        setPosts(tabs.contains(ContentType.POST));
        setEvents(tabs.contains(ContentType.EVENT));
        setWhitePapers(tabs.contains(ContentType.WHITE_PAPER));
        setEBooks(tabs.contains(ContentType.EBOOK));

        setProjects(listing.hasProjects());
        setTools(listing.hasTools());
        setJobs(listing.hasJobs());
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
     * Returns the tag.
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Sets the tag.
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    /**
     * Returns the hashtags.
     */
    public String getHashtags()
    {
        return hashtags;
    }

    /**
     * Sets the hashtags.
     */
    public void setHashtags(String hashtags)
    {
        this.hashtags = hashtags;
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
     * Returns <CODE>true</CODE> if videos are enabled.
     */
    public Boolean getVideos()
    {
        return videos;
    }

    /**
     * Set to <CODE>true</CODE> if videos are enabled.
     */
    public void setVideos(Boolean videos)
    {
        this.videos = videos;
    }

    /**
     * Returns <CODE>true</CODE> if roundups are enabled.
     */
    public Boolean getRoundups()
    {
        return roundups;
    }

    /**
     * Set to <CODE>true</CODE> if roundups are enabled.
     */
    public void setRoundups(Boolean roundups)
    {
        this.roundups = roundups;
    }

    /**
     * Returns <CODE>true</CODE> if events are enabled.
     */
    public Boolean getEvents()
    {
        return events;
    }

    /**
     * Set to <CODE>true</CODE> if events are enabled.
     */
    public void setEvents(Boolean events)
    {
        this.events = events;
    }

    /**
     * Returns <CODE>true</CODE> if white papers are enabled.
     */
    public Boolean getWhitePapers()
    {
        return whitePapers;
    }

    /**
     * Set to <CODE>true</CODE> if white papers are enabled.
     */
    public void setWhitePapers(Boolean whitePapers)
    {
        this.whitePapers = whitePapers;
    }

    /**
     * Returns <CODE>true</CODE> if ebooks are enabled.
     */
    public Boolean getEBooks()
    {
        return ebooks;
    }

    /**
     * Set to <CODE>true</CODE> if ebooks are enabled.
     */
    public void setEBooks(Boolean ebooks)
    {
        this.ebooks = ebooks;
    }

    /**
     * Returns <CODE>true</CODE> if posts are enabled.
     */
    public Boolean getPosts()
    {
        return posts;
    }

    /**
     * Set to <CODE>true</CODE> if posts are enabled.
     */
    public void setPosts(Boolean posts)
    {
        this.posts = posts;
    }

    /**
     * Returns <CODE>true</CODE> if projects are enabled.
     */
    public Boolean getProjects()
    {
        return projects;
    }

    /**
     * Set to <CODE>true</CODE> if projects are enabled.
     */
    public void setProjects(Boolean projects)
    {
        this.projects = projects;
    }

    /**
     * Returns <CODE>true</CODE> if tools are enabled.
     */
    public Boolean getTools()
    {
        return tools;
    }

    /**
     * Set to <CODE>true</CODE> if tools are enabled.
     */
    public void setTools(Boolean tools)
    {
        this.tools = tools;
    }

    /**
     * Returns <CODE>true</CODE> if jobs are enabled.
     */
    public Boolean getJobs()
    {
        return jobs;
    }

    /**
     * Set to <CODE>true</CODE> if tools are enabled.
     */
    public void setJobs(Boolean jobs)
    {
        this.jobs = jobs;
    }
}