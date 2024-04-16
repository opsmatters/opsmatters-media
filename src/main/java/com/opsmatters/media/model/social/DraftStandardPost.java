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
import org.json.JSONObject;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a draft standard social media post with a message and a link.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DraftStandardPost extends DraftPost
{
    private String title = "";

    /**
     * Default constructor.
     */
    public DraftStandardPost()
    {
    }

    /**
     * Constructor that takes a title.
     */
    public DraftStandardPost(Site site, String title)
    {
        setId(StringUtils.getUUID(null));
        setSiteId(site.getId());
        setTitle(title);
        setCreatedDate(Instant.now());
        setStatus(DraftPostStatus.NEW);
    }

    /**
     * Constructor that takes a saved post.
     */
    public DraftStandardPost(SavedStandardPost post)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(post.getSiteId());
        setSourceId(post.getId());
        setStatus(DraftPostStatus.NEW);
        setTitle(post.getTitle());
        setHashtags(post.getHashtags());
        setUrl(post.getUrl());
    }

    /**
     * Copy constructor.
     */
    public DraftStandardPost(DraftStandardPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(DraftStandardPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setTitle(obj.getTitle());
        }
    }

    /**
     * Returns the post type.
     */
    @Override
    public SocialPostType getType()
    {
        return SocialPostType.STANDARD;
    }

    /**
     * Returns the post title.
     */
    @Override
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the post title.
     */
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns <CODE>true</CODE> if the post title has been set.
     */
    public boolean hasTitle()
    {
        return title != null && title.length() > 0;
    }
}