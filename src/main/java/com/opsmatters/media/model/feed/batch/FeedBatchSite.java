/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.model.feed.batch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.platform.Site;

/**
 * Class representing a list of feeds to be executed for a given site.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedBatchSite
{
    private Site site;
    private List<FeedBatchType> types = new ArrayList<FeedBatchType>();
    private Map<ContentType,FeedBatchType> typeMap = new HashMap<ContentType,FeedBatchType>();

    /**
     * Default constructor.
     */
    public FeedBatchSite(Site site)
    {
        setSite(site);
    }

    /**
     * Returns the site.
     */
    public Site getSite()
    {
        return site;
    }

    /**
     * Sets the site.
     */
    public void setSite(Site site)
    {
        this.site = site;
    }

    /**
     * Returns the batch types.
     */
    public List<FeedBatchType> getTypes()
    {
        return types;
    }

    /**
     * Returns the batch type for the given content type.
     */
    public FeedBatchType getType(ContentType type)
    {
        return typeMap.get(type);
    }

    /**
     * Adds a batch type.
     */
    public void addType(FeedBatchType type)
    {
        types.add(type);
        typeMap.put(type.getType(), type);
    }

    /**
     * Returns the number of batch types.
     */
    public int numTypes()
    {
        return types.size();
    }
}