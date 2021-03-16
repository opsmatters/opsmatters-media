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
import com.opsmatters.media.model.site.Site;
import com.opsmatters.media.util.StringUtils;

/**
 * Class representing a draft social media post with a message and a link.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AdhocPost extends DraftPost
{
    public static final String TITLE = "title";

    private String title = "";

    /**
     * Default constructor.
     */
    public AdhocPost()
    {
    }

    /**
     * Constructor that takes a title.
     */
    public AdhocPost(Site site, String title)
    {
        setId(StringUtils.getUUID(null));
        setSiteId(site.getId());
        setTitle(title);
        setCreatedDate(Instant.now());
        setStatus(DraftStatus.NEW);
    }

    /**
     * Constructor that takes a library post template.
     */
    public AdhocPost(PostTemplate template)
    {
        setId(StringUtils.getUUID(null));
        setCreatedDate(Instant.now());
        setSiteId(template.getSiteId());
        setTemplateId(template.getId());
        setStatus(DraftStatus.NEW);
        setTitle(template.getName());
        setHashtags(template.getHashtags());
        setUrl(template.getUrl());
    }

    /**
     * Copy constructor.
     */
    public AdhocPost(AdhocPost obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(AdhocPost obj)
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
    public PostType getType()
    {
        return PostType.ADHOC;
    }

    /**
     * Returns the attributes as a JSON object.
     */
    @Override
    public JSONObject getAttributes()
    {
        JSONObject ret = new JSONObject();

        ret.putOpt(TITLE, getTitle());

        return ret;
    }

    /**
     * Initialise the attributes using a JSON object.
     */
    @Override
    public void setAttributes(JSONObject obj)
    {
        setTitle(obj.optString(TITLE));
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