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
import com.opsmatters.media.db.dao.content.video.VideoArticleDAO;
import com.opsmatters.media.db.dao.content.roundup.RoundupArticleDAO;
import com.opsmatters.media.db.dao.content.event.EventResourceDAO;
import com.opsmatters.media.db.dao.content.publication.WhitePaperResourceDAO;
import com.opsmatters.media.db.dao.content.publication.EBookResourceDAO;
import com.opsmatters.media.db.dao.content.post.PostArticleDAO;
import com.opsmatters.media.db.dao.content.project.ProjectResourceDAO;
import com.opsmatters.media.db.dao.content.tool.ToolResourceDAO;
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
        getVideoArticleDAO();
        getRoundupArticleDAO();
        getPostArticleDAO();
        getEventResourceDAO();
        getWhitePaperResourceDAO();
        getEBookResourceDAO();
        getToolResourceDAO();
        getProjectResourceDAO();
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
     * Returns the VideoArticle DAO.
     */
    public VideoArticleDAO getVideoArticleDAO()
    {
        if(videoArticleDAO == null)
            videoArticleDAO = new VideoArticleDAO(this);
        return videoArticleDAO;
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
     * Returns the PostArticle DAO.
     */
    public PostArticleDAO getPostArticleDAO()
    {
        if(postArticleDAO == null)
            postArticleDAO = new PostArticleDAO(this);
        return postArticleDAO;
    }

    /**
     * Returns the EventResource DAO.
     */
    public EventResourceDAO getEventResourceDAO()
    {
        if(eventResourceDAO == null)
            eventResourceDAO = new EventResourceDAO(this);
        return eventResourceDAO;
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
     * Returns the ToolResource DAO.
     */
    public ToolResourceDAO getToolResourceDAO()
    {
        if(toolResourceDAO == null)
            toolResourceDAO = new ToolResourceDAO(this);
        return toolResourceDAO;
    }

    /**
     * Returns the ProjectResource DAO.
     */
    public ProjectResourceDAO getProjectResourceDAO()
    {
        if(projectResourceDAO == null)
            projectResourceDAO = new ProjectResourceDAO(this);
        return projectResourceDAO;
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
        videoArticleDAO = null;
        roundupArticleDAO = null;
        postArticleDAO = null;
        eventResourceDAO = null;
        whitePaperResourceDAO = null;
        ebookResourceDAO = null;
        toolResourceDAO = null;
        projectResourceDAO = null;
        jobResourceDAO = null;
    }

    private OrganisationListingDAO organisationListingDAO;
    private OrganisationContentTypeDAO organisationContentTypeDAO;
    private VideoArticleDAO videoArticleDAO;
    private RoundupArticleDAO roundupArticleDAO;
    private PostArticleDAO postArticleDAO;
    private EventResourceDAO eventResourceDAO;
    private WhitePaperResourceDAO whitePaperResourceDAO;
    private EBookResourceDAO ebookResourceDAO;
    private ToolResourceDAO toolResourceDAO;
    private ProjectResourceDAO projectResourceDAO;
    private JobResourceDAO jobResourceDAO;
}
