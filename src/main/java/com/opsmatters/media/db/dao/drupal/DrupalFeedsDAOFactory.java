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
package com.opsmatters.media.db.dao.drupal;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all drupal feeds data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DrupalFeedsDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public DrupalFeedsDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getFeedsFeedDAO();
    }

    /**
     * Returns the feeds DAO.
     */
    public FeedsFeedDAO getFeedsFeedDAO()
    {
        if(feedsFeedDAO == null)
            feedsFeedDAO = new FeedsFeedDAO(this);
        return feedsFeedDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        feedsFeedDAO = null;
    }

    private FeedsFeedDAO feedsFeedDAO;
}
