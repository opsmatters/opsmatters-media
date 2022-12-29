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
import com.opsmatters.media.db.dao.content.roundup.RoundupArticleDAO;
import com.opsmatters.media.db.dao.content.event.EventDAO;
import com.opsmatters.media.db.dao.content.publication.WhitePaperResourceDAO;
import com.opsmatters.media.db.dao.content.publication.EBookResourceDAO;
import com.opsmatters.media.db.dao.content.post.PostDAO;
import com.opsmatters.media.db.dao.content.project.ProjectDAO;
import com.opsmatters.media.db.dao.content.tool.ToolDAO;
import com.opsmatters.media.db.dao.content.job.JobResourceDAO;


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
        getRoundupArticleDAO();
        getPostDAO();
        getEventDAO();
        getWhitePaperResourceDAO();
        getEBookResourceDAO();
        getToolDAO();
        getProjectDAO();
        getJobResourceDAO();
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
     * Returns the RoundupArticle DAO.
     */
    public RoundupArticleDAO getRoundupArticleDAO()
    {
        if(roundupArticleDAO == null)
            roundupArticleDAO = new RoundupArticleDAO(this);
        return roundupArticleDAO;
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
     * Returns the Event DAO.
     */
    public EventDAO getEventDAO()
    {
        if(eventDAO == null)
            eventDAO = new EventDAO(this);
        return eventDAO;
    }

    /**
     * Returns the WhitePaperResource DAO.
     */
    public WhitePaperResourceDAO getWhitePaperResourceDAO()
    {
        if(whitePaperResourceDAO == null)
            whitePaperResourceDAO = new WhitePaperResourceDAO(this);
        return whitePaperResourceDAO;
    }

    /**
     * Returns the EBookResource DAO.
     */
    public EBookResourceDAO getEBookResourceDAO()
    {
        if(ebookResourceDAO == null)
            ebookResourceDAO = new EBookResourceDAO(this);
        return ebookResourceDAO;
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
     * Returns the JobResource DAO.
     */
    public JobResourceDAO getJobResourceDAO()
    {
        if(jobResourceDAO == null)
            jobResourceDAO = new JobResourceDAO(this);
        return jobResourceDAO;
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
        roundupArticleDAO = null;
        postDAO = null;
        eventDAO = null;
        whitePaperResourceDAO = null;
        ebookResourceDAO = null;
        toolDAO = null;
        projectDAO = null;
        jobResourceDAO = null;
    }

    private OrganisationListingDAO organisationListingDAO;
    private OrganisationContentTypeDAO organisationContentTypeDAO;
    private VideoDAO videoDAO;
    private RoundupArticleDAO roundupArticleDAO;
    private PostDAO postDAO;
    private EventDAO eventDAO;
    private WhitePaperResourceDAO whitePaperResourceDAO;
    private EBookResourceDAO ebookResourceDAO;
    private ToolDAO toolDAO;
    private ProjectDAO projectDAO;
    private JobResourceDAO jobResourceDAO;
}
