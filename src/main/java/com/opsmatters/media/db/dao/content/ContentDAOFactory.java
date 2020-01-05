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
package com.opsmatters.media.db.dao.content;

import com.opsmatters.media.db.JDBCDatabaseDriver;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.db.dao.DAOFactory;

/**
 * The class for all content data access object factories.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentDAOFactory extends DAOFactory
{
    /**
     * Constructor that takes a database driver and connection.
     */
    public ContentDAOFactory(JDBCDatabaseDriver driver, JDBCDatabaseConnection conn)
    {
        super(driver, conn);

        getOrganisationDAO();
        getVideoDAO();
        getRoundupDAO();
        getPostDAO();
        getEventDAO();
        getWhitePaperDAO();
        getEBookDAO();
        getToolDAO();
        getProjectDAO();
    }

    /**
     * Returns the organisation DAO.
     */
    public OrganisationDAO getOrganisationDAO()
    {
        if(organisationDAO == null)
            organisationDAO = new OrganisationDAO(this);
        return organisationDAO;
    }

    /**
     * Returns the video DAO.
     */
    public VideoDAO getVideoDAO()
    {
        if(videoDAO == null)
            videoDAO = new VideoDAO(this);
        return videoDAO;
    }

    /**
     * Returns the roundup DAO.
     */
    public RoundupDAO getRoundupDAO()
    {
        if(roundupDAO == null)
            roundupDAO = new RoundupDAO(this);
        return roundupDAO;
    }

    /**
     * Returns the post DAO.
     */
    public PostDAO getPostDAO()
    {
        if(postDAO == null)
            postDAO = new PostDAO(this);
        return postDAO;
    }

    /**
     * Returns the event DAO.
     */
    public EventDAO getEventDAO()
    {
        if(eventDAO == null)
            eventDAO = new EventDAO(this);
        return eventDAO;
    }

    /**
     * Returns the white paper DAO.
     */
    public WhitePaperDAO getWhitePaperDAO()
    {
        if(whitePaperDAO == null)
            whitePaperDAO = new WhitePaperDAO(this);
        return whitePaperDAO;
    }

    /**
     * Returns the ebook DAO.
     */
    public EBookDAO getEBookDAO()
    {
        if(ebookDAO == null)
            ebookDAO = new EBookDAO(this);
        return ebookDAO;
    }

    /**
     * Returns the tool DAO.
     */
    public ToolDAO getToolDAO()
    {
        if(toolDAO == null)
            toolDAO = new ToolDAO(this);
        return toolDAO;
    }

    /**
     * Returns the project DAO.
     */
    public ProjectDAO getProjectDAO()
    {
        if(projectDAO == null)
            projectDAO = new ProjectDAO(this);
        return projectDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        organisationDAO = null;
        videoDAO = null;
        roundupDAO = null;
        postDAO = null;
        eventDAO = null;
        whitePaperDAO = null;
        ebookDAO = null;
        toolDAO = null;
        projectDAO = null;
    }

    private OrganisationDAO organisationDAO;
    private VideoDAO videoDAO;
    private RoundupDAO roundupDAO;
    private PostDAO postDAO;
    private EventDAO eventDAO;
    private WhitePaperDAO whitePaperDAO;
    private EBookDAO ebookDAO;
    private ToolDAO toolDAO;
    private ProjectDAO projectDAO;
}
