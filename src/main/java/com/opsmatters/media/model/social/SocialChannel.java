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
package com.opsmatters.media.model.social;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.cache.provider.SocialProviders;
import com.opsmatters.media.model.BaseEntity;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.provider.SocialProviderId;
import com.opsmatters.media.model.provider.SocialProvider;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a social media channel.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannel extends BaseEntity
{
    private String code = "";
    private String name = "";
    private SocialProviderId providerId;
    private String handle = "";
    private String icon = "";
    private String sites = "";
    private List<String> siteList = new ArrayList<String>();
    private String contentTypes = "";
    private List<String> contentTypeList = new ArrayList<String>();
    private int delay = -1;
    private int maxPosts = -1;
    private SocialChannelStatus status = SocialChannelStatus.DISABLED;

    /**
     * Default constructor.
     */
    public SocialChannel()
    {
    }

    /**
     * Constructor that takes a code.
     */
    public SocialChannel(String code)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setCode(code);
    }

    /**
     * Copy constructor.
     */
    public SocialChannel(SocialChannel obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialChannel obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setCode(obj.getCode());
            setName(obj.getName());
            setHandle(obj.getHandle());
            setIcon(obj.getIcon());
            setProviderId(obj.getProviderId());
            setSites(obj.getSites());
            setContentTypes(obj.getContentTypes());
            setDelay(obj.getDelay());
            setMaxPosts(obj.getMaxPosts());
            setStatus(obj.getStatus());
        }
    }

    /**
     * Returns the channel code.
     */
    public String toString()
    {
        return getCode();
    }

    /**
     * Returns the channel code.
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Sets the channel code.
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * Returns the channel name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the channel name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the channel handle.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sets the channel handle.
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Returns the channel icon.
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Sets the channel icon.
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the provider for the channel.
     */
    public SocialProviderId getProviderId()
    {
        return providerId;
    }

    /**
     * Sets the provider for the channel.
     */
    public void setProviderId(String code)
    {
        setProviderId(SocialProviderId.fromCode(code));
    }

    /**
     * Sets the provider for the channel.
     */
    public void setProviderId(SocialProviderId providerId)
    {
        this.providerId = providerId;
    }

    /**
     * Returns the social provider for this configuration.
     */
    public SocialProvider getProvider()
    {
        return SocialProviders.get(providerId);
    }

    /**
     * Returns the channel sites.
     */
    public String getSites()
    {
        return sites;
    }

    /**
     * Returns the list of channel sites.
     */
    public List<String> getSiteList()
    {
        // Return a copy of the list to stop external modification
        return new ArrayList<String>(siteList);
    }

    /**
     * Sets the channel sites.
     */
    public void setSites(String sites)
    {
        this.sites = sites;

        siteList.clear();
        for(String site : StringUtils.toList(sites))
            siteList.add(site);
    }

    /**
     * Sets the list of channel sites.
     */
    public void setSiteList(List<String> siteList)
    {
        this.siteList.clear();
        for(String site : siteList)
            this.siteList.add(site);
        this.sites = StringUtils.fromList(siteList);
    }

    /**
     * Returns <CODE>true</CODE> if this channel is configured for the given site.
     */
    public boolean hasSite(String siteId)
    {
        return siteList.contains(siteId);
    }

    /**
     * Returns <CODE>true</CODE> if this channel is configured for the given site.
     */
    public boolean hasSite(Site site)
    {
        return hasSite(site.getId());
    }

    /**
     * Adds the given site to the list of configured sites.
     */
    public void addSite(String siteId)
    {
        if(!siteList.contains(siteId))
        {
            siteList.add(siteId);
            setSites(StringUtils.fromList(siteList));
        }
    }

    /**
     * Adds the given site to the list of configured sites.
     */
    public void addSite(Site site)
    {
        addSite(site.getId());
    }

    /**
     * Returns the channel content types.
     */
    public String getContentTypes()
    {
        return contentTypes;
    }

    /**
     * Returns the list of channel content types.
     */
    public List<String> getContentTypeList()
    {
        // Return a copy of the list to stop external modification
        return new ArrayList<String>(contentTypeList);
    }

    /**
     * Sets the channel content types.
     */
    public void setContentTypes(String contentTypes)
    {
        this.contentTypes = contentTypes;

        contentTypeList.clear();
        for(String contentType : StringUtils.toList(contentTypes))
            contentTypeList.add(contentType);
    }

    /**
     * Sets the list of channel content types.
     */
    public void setContentTypeList(List<String> contentTypeList)
    {
        this.contentTypeList.clear();
        for(String contentType : contentTypeList)
            this.contentTypeList.add(contentType);
        this.contentTypes = StringUtils.fromList(contentTypeList);
    }

    /**
     * Returns <CODE>true</CODE> if this channel has content types configured.
     */
    public boolean hasContentTypes()
    {
        return contentTypes != null && contentTypes.length() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if this channel is configured for the given content type.
     */
    public boolean hasContentType(String contentType)
    {
        return contentTypeList.contains(contentType);
    }

    /**
     * Returns <CODE>true</CODE> if this channel is configured for the given content type.
     */
    public boolean hasContentType(ContentType contentType)
    {
        return hasContentType(contentType.value());
    }

    /**
     * Returns the delay between social posts for this channel.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Sets the delay between social posts for this channel.
     */
    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    /**
     * Returns the maximum social posts per session for this channel.
     */
    public int getMaxPosts()
    {
        return maxPosts;
    }

    /**
     * Sets the maximum social posts per session for this channel.
     */
    public void setMaxPosts(int maxPosts)
    {
        this.maxPosts = maxPosts;
    }

    /**
     * Returns the channel status.
     */
    public SocialChannelStatus getStatus()
    {
        return status;
    }

    /**
     * Returns <CODE>true</CODE> if the channel status is ACTIVE.
     */
    public boolean isActive()
    {
        return status == SocialChannelStatus.ACTIVE;
    }

    /**
     * Returns <CODE>true</CODE> if the channel status is PAUSED.
     */
    public boolean isPaused()
    {
        return status == SocialChannelStatus.PAUSED;
    }

    /**
     * Sets the channel status.
     */
    public void setStatus(SocialChannelStatus status)
    {
        this.status = status;
    }

    /**
     * Sets the channel status.
     */
    public void setStatus(String status)
    {
        setStatus(SocialChannelStatus.valueOf(status));
    }

    /**
     * Returns the handle URL for the channel.
     */
    public String getHandleUrl(String handle)
    {
        SocialProvider provider = getProvider();
        String ret = null;
        if(provider != null)
            ret = provider.getUrl()+String.format(provider.getHandleUrl(), handle);
        return ret;
    }

    /**
     * Returns the hashtag URL for the channel.
     */
    public String getHashtagUrl(String hashtag)
    {
        SocialProvider provider = getProvider();
        String ret = null;
        if(provider != null)
            ret = provider.getUrl()+String.format(provider.getHashtagUrl(), hashtag);
        return ret;
    }
}