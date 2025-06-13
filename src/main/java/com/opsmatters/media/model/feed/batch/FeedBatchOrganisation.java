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
import java.sql.SQLException;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentConfig;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.db.dao.content.ContentDAO;

/**
 * Class representing the content for an organisation to be updated as part of a feed batch.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedBatchOrganisation
{
    private Site site;
    private ContentConfig config;
    private List<Content> items;
    private String bucket;
    private ContentDAO dao;

    /**
     * Default constructor.
     */
    public FeedBatchOrganisation(Site site, ContentConfig config, ContentDAO dao)
    {
        setSite(site);
        setConfig(config);
        setDAO(dao);
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
     * Returns the config.
     */
    public ContentConfig getConfig()
    {
        return config;
    }

    /**
     * Sets the config.
     */
    public void setConfig(ContentConfig config)
    {
        this.config = config;
    }

    /**
     * Returns the items.
     */
    public List<Content> getItems()
    {
        return items;
    }

    /**
     * Sets the items.
     */
    public void setItems(List<Content> items)
    {
        this.items = items;
    }

    /**
     * Sets the items.
     */
    public void setItems(boolean pendingOnly) throws SQLException
    {
        if(items == null)
        {
            if(pendingOnly)
                items = getDAO().listPending(site, config.getCode());
            else
                items = getDAO().list(site, config.getCode());
        }
    }

    /**
     * Returns the items.
     */
    public List<Content> getItems(boolean pendingOnly) throws SQLException
    {
        setItems(pendingOnly);
        return getItems();
    }

    /**
     * Returns <CODE>true</CODE> if the items have been set.
     */
    public boolean hasItems()
    {
        return items != null && items.size() > 0;
    }

    /**
     * Clears the items.
     */
    public void clearItems()
    {
        if(hasItems())
            getItems().clear();
        setItems(null);
    }

    /**
     * Returns the number of items.
     */
    public int numItems()
    {
        return items != null ? items.size() : -1;
    }

    /**
     * Returns the bucket used for the last file copy.
     */
    public String getBucket()
    {
        return bucket;
    }

    /**
     * Sets the bucket used for the last file copy.
     */
    public void setBucket(String bucket)
    {
        this.bucket = bucket;
    }

    /**
     * Returns the DAO used for database operations.
     */
    public ContentDAO getDAO()
    {
        return dao;
    }

    /**
     * Sets the DAO used for database operations.
     */
    public void setDAO(ContentDAO dao)
    {
        this.dao = dao;
    }
}