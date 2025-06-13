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
import java.util.Collection;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.feed.ContentFeed;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.system.EnvironmentId;
import com.opsmatters.media.db.dao.content.ContentDAO;

/**
 * Class representing a list of feeds to be executed for a given content type.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedBatchType
{
    private static final Logger logger = Logger.getLogger(FeedBatchType.class.getName());

    private ContentType type;
    private List<ContentFeed> feeds = new ArrayList<ContentFeed>();
    private List<FeedBatchOrganisation> organisations = new ArrayList<FeedBatchOrganisation>();

    /**
     * Default constructor.
     */
    public FeedBatchType(ContentType type)
    {
        setType(type);
    }

    /**
     * Returns the site.
     */
    public ContentType getType()
    {
        return type;
    }

    /**
     * Sets the site.
     */
    public void setType(ContentType type)
    {
        this.type = type;
    }

    /**
     * Returns the batch feeds.
     */
    public List<ContentFeed> getFeeds()
    {
        return feeds;
    }

    /**
     * Adds a batch feed.
     */
    public void addFeed(ContentFeed feed)
    {
        feeds.add(feed);
    }

    /**
     * Returns the number of feeds.
     */
    public int numFeeds()
    {
        return feeds.size();
    }

    /**
     * Returns <CODE>true</CODE> if there exists a feed for the given environment.
     */
    public boolean hasEnvironment(EnvironmentId env)
    {
        boolean ret = false;
        for(ContentFeed feed : feeds)
        {
            if(feed.getEnvironment() == env)
            {
                ret = true;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the batch organisations.
     */
    public List<FeedBatchOrganisation> getOrganisations()
    {
        return organisations;
    }

    /**
     * Adds a batch organisation.
     */
    public void addOrganisation(FeedBatchOrganisation organisation)
    {
        organisations.add(organisation);
    }

    /**
     * Returns the number of organisations.
     */
    public int numOrganisations()
    {
        return organisations.size();
    }

    /**
     * Set to <CODE>true</CODE> if the organisations have been loaded by the batch.
     */
    public void addOrganisations(Site site, Collection<ContentConfig> configs, ContentDAO dao)
    {
        if(configs.size() > 0)
        {
            // Add all the organisations with pending content
            for(ContentConfig config : configs)
                addOrganisation(new FeedBatchOrganisation(site, config, dao));
            logger.info(String.format("Found %d %s organisations for feed batch",
                numOrganisations(), type));
        }
        else
        {
            logger.info(String.format("No %s organisations found for feed batch", type));
        }
    }
}