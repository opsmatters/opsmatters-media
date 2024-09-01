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
package com.opsmatters.media.db.dao.monitor;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all monitor data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class MonitorDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public MonitorDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getContentMonitorDAO();
        getContentChangeDAO();
        getContentAlertDAO();
        getContentReviewDAO();
    }

    /**
     * Returns the content monitor DAO.
     */
    public ContentMonitorDAO getContentMonitorDAO()
    {
        if(contentMonitorDAO == null)
            contentMonitorDAO = new ContentMonitorDAO(this);
        return contentMonitorDAO;
    }

    /**
     * Returns the content change DAO.
     */
    public ContentChangeDAO getContentChangeDAO()
    {
        if(contentChangeDAO == null)
            contentChangeDAO = new ContentChangeDAO(this);
        return contentChangeDAO;
    }

    /**
     * Returns the content alert DAO.
     */
    public ContentAlertDAO getContentAlertDAO()
    {
        if(contentAlertDAO == null)
            contentAlertDAO = new ContentAlertDAO(this);
        return contentAlertDAO;
    }

    /**
     * Returns the content review DAO.
     */
    public ContentReviewDAO getContentReviewDAO()
    {
        if(contentReviewDAO == null)
            contentReviewDAO = new ContentReviewDAO(this);
        return contentReviewDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        contentMonitorDAO = null;
        contentChangeDAO = null;
        contentAlertDAO = null;
        contentReviewDAO = null;
    }

    private ContentMonitorDAO contentMonitorDAO;
    private ContentChangeDAO contentChangeDAO;
    private ContentAlertDAO contentAlertDAO;
    private ContentReviewDAO contentReviewDAO;
}
