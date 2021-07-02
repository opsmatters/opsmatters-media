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
package com.opsmatters.media.db.dao.social;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.social.PostTemplate;
import com.opsmatters.media.model.social.ContentPostTemplate;
import com.opsmatters.media.model.social.MessageFormat;
import com.opsmatters.media.model.content.ContentType;

/**
 * DAO that provides operations on the POST_TEMPLATES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PostTemplateDAO extends SocialDAO<PostTemplate>
{
    private static final Logger logger = Logger.getLogger(PostTemplateDAO.class.getName());

    /**
     * The query to use to select a post template from the POST_TEMPLATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, SITE_ID, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM POST_TEMPLATES WHERE ID=?";

    /**
     * The query to use to insert a post template into the POST_TEMPLATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO POST_TEMPLATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, SITE_ID, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, SHORTEN_URL, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post template in the POST_TEMPLATES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE POST_TEMPLATES SET UPDATED_DATE=?, POSTED_DATE=?, SITE_ID=?, NAME=?, MESSAGE=?, CONTENT_TYPE=?, IS_DEFAULT=?, SHORTEN_URL=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the post templates from the POST_TEMPLATES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, SITE_ID, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM POST_TEMPLATES";

    /**
     * The query to use to select the post templates from the POST_TEMPLATES table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, SITE_ID, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM POST_TEMPLATES WHERE SITE_ID=?";

    /**
     * The query to use to select the post templates from the POST_TEMPLATES table by content type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, SITE_ID, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM POST_TEMPLATES WHERE SITE_ID=? AND CONTENT_TYPE=?";

    /**
     * The query to use to get the count of post templates from the POST_TEMPLATES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM POST_TEMPLATES";

    /**
     * The query to use to delete a post template from the POST_TEMPLATES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM POST_TEMPLATES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public PostTemplateDAO(SocialDAOFactory factory)
    {
        super(factory, "POST_TEMPLATES");
    }

    /**
     * Defines the columns and indices for the POST_TEMPLATES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("POSTED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("NAME", Types.VARCHAR, 128, true);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("IS_DEFAULT", Types.BOOLEAN, true);
        table.addColumn("SHORTEN_URL", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("POST_TEMPLATES_PK", new String[] {"ID"});
        table.addIndex("POST_TEMPLATES_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a post template from the POST_TEMPLATES table by id.
     */
    public synchronized ContentPostTemplate getById(String id) throws SQLException
    {
        ContentPostTemplate ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), GET_BY_ID_SQL);
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setString(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                ContentPostTemplate template = new ContentPostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                template.setSiteId(rs.getString(5));
                template.setName(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setContentType(rs.getString(8));
                template.setDefault(rs.getBoolean(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret = template;
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Stores the given post template in the POST_TEMPLATES table.
     */
    public synchronized void add(ContentPostTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, template.getId());
            insertStmt.setTimestamp(2, new Timestamp(template.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(template.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(template.getPostedDateMillis()), UTC);
            insertStmt.setString(5, template.getSiteId());
            insertStmt.setString(6, template.getName());
            insertStmt.setString(7, template.getMessage(MessageFormat.ENCODED));
            insertStmt.setString(8, template.getContentType() != null ? template.getContentType().name(): "");
            insertStmt.setBoolean(9, template.isDefault());
            insertStmt.setBoolean(10, template.isShortenUrl());
            insertStmt.setString(11, template.getStatus().name());
            insertStmt.setString(12, template.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created post template '"+template.getId()+"' in POST_TEMPLATES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the template already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given post template in the POST_TEMPLATES table.
     */
    public synchronized void update(ContentPostTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(template.getUpdatedDateMillis()), UTC);
        updateStmt.setTimestamp(2, new Timestamp(template.getPostedDateMillis()), UTC);
        updateStmt.setString(3, template.getSiteId());
        updateStmt.setString(4, template.getName());
        updateStmt.setString(5, template.getMessage(MessageFormat.ENCODED));
        updateStmt.setString(6, template.getContentType() != null ? template.getContentType().name(): "");
        updateStmt.setBoolean(7, template.isDefault());
        updateStmt.setBoolean(8, template.isShortenUrl());
        updateStmt.setString(9, template.getStatus().name());
        updateStmt.setString(10, template.getId());
        updateStmt.executeUpdate();

        logger.info("Updated post template '"+template.getId()+"' in POST_TEMPLATES");
    }

    /**
     * Returns the post templates from the POST_TEMPLATES table.
     */
    public synchronized List<ContentPostTemplate> list() throws SQLException
    {
        List<ContentPostTemplate> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<ContentPostTemplate>();
            while(rs.next())
            {
                ContentPostTemplate template = new ContentPostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                template.setSiteId(rs.getString(5));
                template.setName(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setContentType(rs.getString(8));
                template.setDefault(rs.getBoolean(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret.add(template);
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Returns the post templates from the POST_TEMPLATES table by site.
     */
    public synchronized List<ContentPostTemplate> list(Site site) throws SQLException
    {
        List<ContentPostTemplate> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listBySiteStmt == null)
            listBySiteStmt = prepareStatement(getConnection(), LIST_BY_SITE_SQL);
        clearParameters(listBySiteStmt);

        ResultSet rs = null;

        try
        {
            listBySiteStmt.setString(1, site.getId());
            listBySiteStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySiteStmt.executeQuery();
            ret = new ArrayList<ContentPostTemplate>();
            while(rs.next())
            {
                ContentPostTemplate template = new ContentPostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                template.setSiteId(rs.getString(5));
                template.setName(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setContentType(rs.getString(8));
                template.setDefault(rs.getBoolean(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret.add(template);
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Returns the post templates from the POST_TEMPLATES table by content type.
     */
    public synchronized List<ContentPostTemplate> list(Site site, ContentType contentType) throws SQLException
    {
        List<ContentPostTemplate> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, site.getId());
            listByTypeStmt.setString(2, contentType.name());
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<ContentPostTemplate>();
            while(rs.next())
            {
                ContentPostTemplate template = new ContentPostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                template.setSiteId(rs.getString(5));
                template.setName(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setContentType(rs.getString(8));
                template.setDefault(rs.getBoolean(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret.add(template);
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Returns the count of post templates from the table.
     */
    public int count() throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), COUNT_SQL);
        clearParameters(countStmt);

        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Removes the given post template from the POST_TEMPLATES table.
     */
    public synchronized void delete(ContentPostTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, template.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted post template '"+template.getId()+"' in POST_TEMPLATES");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listByTypeStmt);
        listByTypeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listByTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
