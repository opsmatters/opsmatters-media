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
package com.opsmatters.media.model.social;

import java.time.Instant;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a saved standard social media post.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SavedStandardPost extends SavedPost
{
    /**
     * Default constructor.
     */
    public SavedStandardPost()
    {
    }

    /**
     * Constructor that takes a site and title.
     */
    public SavedStandardPost(Site site, String title)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(site.getId());
        setTitle(title);
        setStatus(SavedPostStatus.NEW);
    }

    /**
     * Copy constructor.
     */
    public SavedStandardPost(SavedStandardPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(SavedStandardPost obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
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
}