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
import java.util.Map;
import java.util.HashMap;
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
    private String tags = "";
    private String channelId = "";
    private String website = "";
    private String blogUrl = "";
    private String features = "";

    Map<ContentType,Boolean> types = new HashMap<ContentType,Boolean>();
    Map<EventPage,Boolean> pages = new HashMap<EventPage,Boolean>();

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
        setWhitepapers(tabs.contains(ContentType.WHITE_PAPER));
        setEbooks(tabs.contains(ContentType.EBOOK));

        setProjects(listing.hasProjects());
        setTools(listing.hasTools());
        setJobs(listing.hasJobs());

        setWebinars(true);
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
     * Returns <CODE>true</CODE> if white papers are enabled.
     */
    public Boolean getWhitepapers()
    {
        return hasType(ContentType.WHITE_PAPER);
    }

    /**
     * Set to <CODE>true</CODE> if white papers are enabled.
     */
    public void setWhitepapers(Boolean whitePapers)
    {
        setType(whitePapers, ContentType.WHITE_PAPER);
    }

    /**
     * Returns <CODE>true</CODE> if ebooks are enabled.
     */
    public Boolean getEbooks()
    {
        return hasType(ContentType.EBOOK);
    }

    /**
     * Set to <CODE>true</CODE> if ebooks are enabled.
     */
    public void setEbooks(Boolean ebooks)
    {
        setType(ebooks, ContentType.EBOOK);
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
     * Returns the list of event pages.
     */
    public List<EventPage> getEventPages()
    {
        return new ArrayList<EventPage>(pages.keySet());
    }

    /**
     * Returns <CODE>true</CODE> if the given page is enabled.
     */
    private Boolean hasPage(EventPage page)
    {
        return pages.containsKey(page);
    }

    /**
     * Set to <CODE>true</CODE> if the given page is enabled.
     */
    private void setPage(boolean enabled, EventPage page)
    {
        if(enabled)
            pages.put(page, Boolean.TRUE);
        else
            pages.remove(page);
    }

    /**
     * Returns <CODE>true</CODE> if webinar events are enabled.
     */
    public Boolean getWebinars()
    {
        return hasPage(EventPage.WEBINARS);
    }

    /**
     * Set to <CODE>true</CODE> if webinar events are enabled.
     */
    public void setWebinars(Boolean webinars)
    {
        setPage(webinars, EventPage.WEBINARS);
    }

    /**
     * Returns <CODE>true</CODE> if Zoom events are enabled.
     */
    public Boolean getZoom()
    {
        return hasPage(EventPage.ZOOM);
    }

    /**
     * Set to <CODE>true</CODE> if Zoom events are enabled.
     */
    public void setZoom(Boolean zoom)
    {
        setPage(zoom, EventPage.ZOOM);
    }

    /**
     * Returns <CODE>true</CODE> if BrightTalk events are enabled.
     */
    public Boolean getBrighttalk()
    {
        return hasPage(EventPage.BRIGHTTALK);
    }

    /**
     * Set to <CODE>true</CODE> if BrightTalk events are enabled.
     */
    public void setBrighttalk(Boolean brightTalk)
    {
        setPage(brightTalk, EventPage.BRIGHTTALK);
    }

    /**
     * Returns <CODE>true</CODE> if GoToWebinar events are enabled.
     */
    public Boolean getGotowebinar()
    {
        return hasPage(EventPage.GOTOWEBINAR);
    }

    /**
     * Set to <CODE>true</CODE> if GoToWebinar events are enabled.
     */
    public void setGotowebinar(Boolean goToWebinar)
    {
        setPage(goToWebinar, EventPage.GOTOWEBINAR);
    }

    /**
     * Returns <CODE>true</CODE> if on24 events are enabled.
     */
    public Boolean getOn24()
    {
        return hasPage(EventPage.ON24);
    }

    /**
     * Set to <CODE>true</CODE> if on24 events are enabled.
     */
    public void setOn24(Boolean on24)
    {
        setPage(on24, EventPage.ON24);
    }

    /**
     * Returns <CODE>true</CODE> if Livestorm events are enabled.
     */
    public Boolean getLivestorm()
    {
        return hasPage(EventPage.LIVESTORM);
    }

    /**
     * Set to <CODE>true</CODE> if Livestorm events are enabled.
     */
    public void setLivestorm(Boolean livestorm)
    {
        setPage(livestorm, EventPage.LIVESTORM);
    }
}