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
package com.opsmatters.media.db.dao.provider;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all provider data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProviderDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public ProviderDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getEventProviderDAO();
        getImageProviderDAO();
        getVideoProviderDAO();
        getSocialProviderDAO();
    }

    /**
     * Returns the event provider DAO.
     */
    public EventProviderDAO getEventProviderDAO()
    {
        if(eventProviderDAO == null)
            eventProviderDAO = new EventProviderDAO(this);
        return eventProviderDAO;
    }

    /**
     * Returns the image provider DAO.
     */
    public ImageProviderDAO getImageProviderDAO()
    {
        if(imageProviderDAO == null)
            imageProviderDAO = new ImageProviderDAO(this);
        return imageProviderDAO;
    }

    /**
     * Returns the video provider DAO.
     */
    public VideoProviderDAO getVideoProviderDAO()
    {
        if(videoProviderDAO == null)
            videoProviderDAO = new VideoProviderDAO(this);
        return videoProviderDAO;
    }

    /**
     * Returns the social provider DAO.
     */
    public SocialProviderDAO getSocialProviderDAO()
    {
        if(socialProviderDAO == null)
            socialProviderDAO = new SocialProviderDAO(this);
        return socialProviderDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        eventProviderDAO = null;
        imageProviderDAO = null;
        videoProviderDAO = null;
        socialProviderDAO = null;
    }

    private EventProviderDAO eventProviderDAO;
    private ImageProviderDAO imageProviderDAO;
    private VideoProviderDAO videoProviderDAO;
    private SocialProviderDAO socialProviderDAO;
}