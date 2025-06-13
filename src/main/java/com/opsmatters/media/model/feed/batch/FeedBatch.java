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
import com.opsmatters.media.cache.system.Sites;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.feed.ContentFeed;
import com.opsmatters.media.model.system.Site;

/**
 * Class representing a list of feeds to be executed.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedBatch
{
    private List<FeedBatchSite> sites = new ArrayList<FeedBatchSite>();
    private Map<String,FeedBatchSite> siteMap = new HashMap<String,FeedBatchSite>();
    private boolean organisationsLoaded = false;

    /**
     * Prepares a new batch using the given set of submitted feeds.
     */
    public FeedBatch(List<ContentFeed> feeds)
    {
        for(ContentFeed feed : feeds)
        {
            Site site = Sites.get(feed.getSiteId());
            ContentType type = feed.getContentType();

            FeedBatchSite batchSite = getSite(site);
            if(batchSite == null)
            {
                batchSite = new FeedBatchSite(site);
                addSite(batchSite);
            }

            FeedBatchType batchType = batchSite.getType(type);
            if(batchType == null)
            {
                batchType = new FeedBatchType(type);
                batchSite.addType(batchType);
            }

            batchType.addFeed(feed);
        }
    }

    /**
     * Returns the batch sites.
     */
    public List<FeedBatchSite> getSites()
    {
        return sites;
    }

    /**
     * Returns the batch for the given site.
     */
    public FeedBatchSite getSite(Site site)
    {
        return siteMap.get(site.getId());
    }

    /**
     * Adds a site.
     */
    public void addSite(FeedBatchSite site)
    {
        sites.add(site);
        siteMap.put(site.getSite().getId(), site);
    }

    /**
     * Returns the number of sites.
     */
    public int numSites()
    {
        return sites.size();
    }

    /**
     * Returns <CODE>true</CODE> if the organisations have been loaded by the batch.
     */
    public boolean isOrganisationsLoaded()
    {
        return organisationsLoaded;
    }

    /**
     * Set to <CODE>true</CODE> if the organisations have been loaded by the batch.
     */
    public void setOrganisationsLoaded(boolean organisationsLoaded)
    {
        this.organisationsLoaded = organisationsLoaded;
    }
}