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

        getOrganisationListingDAO();
        getVideoArticleDAO();
        getRoundupArticleDAO();
        getPostArticleDAO();
        getEventResourceDAO();
        getWhitePaperResourceDAO();
        getEBookResourceDAO();
        getToolResourceDAO();
        getProjectResourceDAO();
        getOrganisationSummaryDAO();
        getContentTypeSummaryDAO();
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
     * Returns the OrganisationSummary DAO.
     */
    public OrganisationSummaryDAO getOrganisationSummaryDAO()
    {
        if(organisationSummaryDAO == null)
            organisationSummaryDAO = new OrganisationSummaryDAO(this);
        return organisationSummaryDAO;
    }

    /**
     * Returns the ContentTypeSummary DAO.
     */
    public ContentTypeSummaryDAO getContentTypeSummaryDAO()
    {
        if(contentTypeSummaryDAO == null)
            contentTypeSummaryDAO = new ContentTypeSummaryDAO(this);
        return contentTypeSummaryDAO;
    }

    /**
     * Close any resources associated with this DAO factory.
     */
    @Override
    public void close()
    {
        super.close();
        organisationListingDAO = null;
        videoArticleDAO = null;
        roundupArticleDAO = null;
        postArticleDAO = null;
        eventResourceDAO = null;
        whitePaperResourceDAO = null;
        ebookResourceDAO = null;
        toolResourceDAO = null;
        projectResourceDAO = null;
        organisationSummaryDAO = null;
        contentTypeSummaryDAO = null;
    }

    private OrganisationListingDAO organisationListingDAO;
    private VideoArticleDAO videoArticleDAO;
    private RoundupArticleDAO roundupArticleDAO;
    private PostArticleDAO postArticleDAO;
    private EventResourceDAO eventResourceDAO;
    private WhitePaperResourceDAO whitePaperResourceDAO;
    private EBookResourceDAO ebookResourceDAO;
    private ToolResourceDAO toolResourceDAO;
    private ProjectResourceDAO projectResourceDAO;
    private OrganisationSummaryDAO organisationSummaryDAO;
    private ContentTypeSummaryDAO contentTypeSummaryDAO;
}
