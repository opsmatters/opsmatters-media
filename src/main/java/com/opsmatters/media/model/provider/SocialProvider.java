/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model.provider;

/**
 * Class that represents a social media provider.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialProvider extends ExternalProvider
{
    private SocialProviderId providerId;
    private String url = "";
    private String handleUrl = "";
    private String hashtagUrl = "";
    private String thumbnail = "";
    private int maxPostLength = -1;
    private int urlLength = -1;

    /**
     * Default constructor.
     */
    public SocialProvider()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public SocialProvider(String name)
    {
        super(name);
    }

    /**
     * Copy constructor.
     */
    public SocialProvider(SocialProvider obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SocialProvider obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setUrl(obj.getUrl());
            setHandleUrl(obj.getHandleUrl());
            setHashtagUrl(obj.getHashtagUrl());
            setThumbnail(obj.getThumbnail());
            setMaxPostLength(obj.getMaxPostLength());
            setUrlLength(obj.getUrlLength());
        }
    }

    /**
     * Sets the code for the provider.
     */
    @Override
    public void setCode(String code)
    {
        super.setCode(code);
        setProviderId(code);
    }

    /**
     * Returns the provider id.
     */
    public SocialProviderId getProviderId()
    {
        return providerId;
    }

    /**
     * Sets the provider id.
     */
    private void setProviderId(SocialProviderId providerId)
    {
        this.providerId = providerId;
    }

    /**
     * Sets the provider id.
     */
    private void setProviderId(String code)
    {
        setProviderId(SocialProviderId.fromCode(code));
    }

    /**
     * Returns the url for the provider.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url for the provider.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns <CODE>true</CODE> if the url for the provider has been set.
     */
    public boolean hasUrl()
    {
        return getUrl() != null && getUrl().length() > 0;
    }

    /**
     * Returns the handle url for the provider.
     */
    public String getHandleUrl()
    {
        return handleUrl;
    }

    /**
     * Sets the handle url for the provider.
     */
    public void setHandleUrl(String handleUrl)
    {
        this.handleUrl = handleUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the handle url for the provider has been set.
     */
    public boolean hasHandleUrl()
    {
        return getHandleUrl() != null && getHandleUrl().length() > 0;
    }

    /**
     * Returns the hashtag url for the provider.
     */
    public String getHashtagUrl()
    {
        return hashtagUrl;
    }

    /**
     * Sets the hashtag url for the provider.
     */
    public void setHashtagUrl(String hashtagUrl)
    {
        this.hashtagUrl = hashtagUrl;
    }

    /**
     * Returns <CODE>true</CODE> if the hashtag url for the provider has been set.
     */
    public boolean hasHashtagUrl()
    {
        return getHashtagUrl() != null && getHashtagUrl().length() > 0;
    }

    /**
     * Returns the thumbnail for the provider.
     */
    public String getThumbnail()
    {
        return thumbnail;
    }

    /**
     * Returns the thumbnail path for the provider.
     */
    public String getThumbnailPath()
    {
        return String.format("/images/%s", thumbnail);
    }

    /**
     * Sets the thumbnail for the provider.
     */
    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    /**
     * Returns <CODE>true</CODE> if the thumbnail for the provider has been set.
     */
    public boolean hasThumbnail()
    {
        return getThumbnail() != null && getThumbnail().length() > 0;
    }

    /**
     * Returns the max post length for the provider.
     */
    public int getMaxPostLength()
    {
        return maxPostLength;
    }

    /**
     * Sets the max post length for the provider.
     */
    public void setMaxPostLength(int maxPostLength)
    {
        this.maxPostLength = maxPostLength;
    }

    /**
     * Returns the length of a URL in a post for the provider.
     */
    public int getUrlLength()
    {
        return urlLength;
    }

    /**
     * Sets the length of a URL in a post for the provider.
     */
    public void setUrlLength(int urlLength)
    {
        this.urlLength = urlLength;
    }
}