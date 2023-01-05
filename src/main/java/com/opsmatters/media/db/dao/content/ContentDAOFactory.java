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
import com.opsmatters.media.db.dao.content.organisation.OrganisationListingDAO;
import com.opsmatters.media.db.dao.content.organisation.OrganisationContentTypeDAO;
import com.opsmatters.media.db.dao.content.video.VideoDAO;
import com.opsmatters.media.db.dao.content.event.EventDAO;
import com.opsmatters.media.db.dao.content.publication.PublicationDAO;
import com.opsmatters.media.db.dao.content.post.PostDAO;
import com.opsmatters.media.db.dao.content.post.RoundupPostDAO;
import com.opsmatters.media.db.dao.content.project.ProjectDAO;
import com.opsmatters.media.db.dao.content.tool.ToolDAO;
import com.opsmatters.media.db.dao.content.job.JobDAO;


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

        getOrganisationListingDAO();
        getOrganisationContentTypeDAO();
        getVideoDAO();
        getPostDAO();
        getRoundupPostDAO();
        getEventDAO();
        getPublicationDAO();
        getToolDAO();
        getProjectDAO();
        getJobDAO();
    }

    /**
     * Returns the OrganisationListing DAO.
     */
    public OrganisationListingDAO getOrganisationListingDAO()
    {
        if(organisationListingDAO == null)
            organisationListingDAO = new OrganisationListingDAO(this);
        return organisationListingDAO;
    }

    /**
     * Returns the OrganisationContentType DAO.
     */
    public OrganisationContentTypeDAO getOrganisationContentTypeDAO()
    {
        if(organisationContentTypeDAO == null)
            organisationContentTypeDAO = new OrganisationContentTypeDAO(this);
        return organisationContentTypeDAO;
    }

    /**
     * Returns the Video DAO.
     */
    public VideoDAO getVideoDAO()
    {
        if(videoDAO == null)
            videoDAO = new VideoDAO(this);
        return videoDAO;
    }

    /**
     * Returns the Post DAO.
     */
    public PostDAO getPostDAO()
    {
        if(postDAO == null)
            postDAO = new PostDAO(this);
        return postDAO;
    }

    /**
     * Returns the RoundupPost DAO.
     */
    public RoundupPostDAO getRoundupPostDAO()
    {
        if(roundupPostDAO == null)
            roundupPostDAO = new RoundupPostDAO(this);
        return roundupPostDAO;
    }

    /**
     * Returns the Event DAO.
     */
    public EventDAO getEventDAO()
    {
        if(eventDAO == null)
            eventDAO = new EventDAO(this);
        return eventDAO;
    }

    /**
     * Returns the Publication DAO.
     */
    public PublicationDAO getPublicationDAO()
    {
        if(publicationDAO == null)
            publicationDAO = new PublicationDAO(this);
        return publicationDAO;
    }

    /**
     * Returns the Tool DAO.
     */
    public ToolDAO getToolDAO()
    {
        if(toolDAO == null)
            toolDAO = new ToolDAO(this);
        return toolDAO;
    }

    /**
     * Returns the Project DAO.
     */
    public ProjectDAO getProjectDAO()
    {
        if(projectDAO == null)
            projectDAO = new ProjectDAO(this);
        return projectDAO;
    }

    /**
     * Returns the Job DAO.
     */
    public JobDAO getJobDAO()
    {
        if(jobDAO == null)
            jobDAO = new JobDAO(this);
        return jobDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        organisationListingDAO = null;
        organisationContentTypeDAO = null;
        videoDAO = null;
        roundupPostDAO = null;
        postDAO = null;
        eventDAO = null;
        publicationDAO = null;
        toolDAO = null;
        projectDAO = null;
        jobDAO = null;
    }

    private OrganisationListingDAO organisationListingDAO;
    private OrganisationContentTypeDAO organisationContentTypeDAO;
    private VideoDAO videoDAO;
    private RoundupPostDAO roundupPostDAO;
    private PostDAO postDAO;
    private EventDAO eventDAO;
    private PublicationDAO publicationDAO;
    private ToolDAO toolDAO;
    private ProjectDAO projectDAO;
    private JobDAO jobDAO;
}
