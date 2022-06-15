/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.model.monitor;

import java.util.List;
import java.util.logging.Logger;
import java.sql.SQLException;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.content.ContentItem;

/**
 * Class representing a lookup for content items.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentLookup<T extends ContentItem>
{
    private static final Logger logger = Logger.getLogger(ContentLookup.class.getName());

    private List<Organisation> organisations;

    /**
     * Constructor that takes a list of organisations.
     */
    public ContentLookup(List<Organisation> organisations)
    {
        this.organisations = organisations;
    }

    /**
     * Returns the lookup organisations.
     */
    public List<Organisation> getOrganisations()
    {
        return organisations;
    }

    /**
     * Returns the content item with the given title.
     */
    public T getByTitle(String title) throws SQLException
    {
        T ret = null;
        for(Organisation organisation : organisations)
        {
            ret = getByTitle(organisation.getSiteId(), organisation.getCode(), title);
            if(ret != null)
                break;
        }

        return ret;
    }

    /**
     * Returns the content item with the given organisation, code and title.
     */
    protected abstract T getByTitle(String siteId, String code, String title) throws SQLException;

    /**
     * Returns the content item with the given id (url or video id).
     */
    public T getById(String id) throws SQLException
    {
        T ret = null;
        for(Organisation organisation : organisations)
        {
            ret = getById(organisation.getSiteId(), organisation.getCode(), id);
            if(ret != null)
                break;
        }

        return ret;
    }

    /**
     * Returns the content item with the given organisation, code and id (url or video id).
     */
    protected abstract T getById(String siteId, String code, String id) throws SQLException;
}