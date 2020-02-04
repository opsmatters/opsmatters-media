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
import com.opsmatters.media.model.social.PostTemplate;
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
     * The query to use to select an item from the POST_TEMPLATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY "
      + "FROM POST_TEMPLATES WHERE ID=?";

    /**
     * The query to use to insert a post template into the POST_TEMPLATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO POST_TEMPLATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post template in the POST_TEMPLATES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE POST_TEMPLATES SET UPDATED_DATE=?, NAME=?, MESSAGE=?, CONTENT_TYPE=?, IS_DEFAULT=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the post templates from the POST_TEMPLATES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY "
      + "FROM POST_TEMPLATES";

    /**
     * The query to use to select the post templates from the POST_TEMPLATES table.
     */
    private static final String LIST_BY_CONTENT_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY "
      + "FROM POST_TEMPLATES WHERE CONTENT_TYPE=?";

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
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("IS_DEFAULT", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("POST_TEMPLATES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a post template from the POST_TEMPLATES table by id.
     */
    public PostTemplate getById(int id) throws SQLException
    {
        PostTemplate ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), GET_BY_ID_SQL);
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setInt(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                PostTemplate template = new PostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setMessage(rs.getString(5));
                template.setContentType(rs.getString(6));
                template.setDefault(rs.getBoolean(7));
                template.setCreatedBy(rs.getString(8));
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
    public void add(PostTemplate template) throws SQLException
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
            insertStmt.setString(4, template.getName());
            insertStmt.setString(5, template.getMessage());
            insertStmt.setString(6, template.getContentType() != null ? template.getContentType().name(): "");
            insertStmt.setBoolean(7, template.isDefault());
            insertStmt.setString(8, template.getCreatedBy());
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
    public void update(PostTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(template.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, template.getName());
        updateStmt.setString(3, template.getMessage());
        updateStmt.setString(4, template.getContentType() != null ? template.getContentType().name(): "");
        updateStmt.setBoolean(5, template.isDefault());
        updateStmt.setString(6, template.getId());
        updateStmt.executeUpdate();

        logger.info("Updated post template '"+template.getId()+"' in POST_TEMPLATES");
    }

    /**
     * Returns the post templates from the POST_TEMPLATES table.
     */
    public List<PostTemplate> list() throws SQLException
    {
        List<PostTemplate> ret = null;

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
            ret = new ArrayList<PostTemplate>();
            while(rs.next())
            {
                PostTemplate template = new PostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setMessage(rs.getString(5));
                template.setContentType(rs.getString(6));
                template.setDefault(rs.getBoolean(7));
                template.setCreatedBy(rs.getString(8));
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
     * Returns the post templates from the POST_TEMPLATES table.
     */
    public List<PostTemplate> list(ContentType type) throws SQLException
    {
        List<PostTemplate> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContentTypeStmt == null)
            listByContentTypeStmt = prepareStatement(getConnection(), LIST_BY_CONTENT_TYPE_SQL);
        clearParameters(listByContentTypeStmt);

        ResultSet rs = null;

        try
        {
            listByContentTypeStmt.setString(1, type != null ? type.name() : "");
            listByContentTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContentTypeStmt.executeQuery();
            ret = new ArrayList<PostTemplate>();
            while(rs.next())
            {
                PostTemplate template = new PostTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setMessage(rs.getString(5));
                template.setContentType(rs.getString(6));
                template.setDefault(rs.getBoolean(7));
                template.setCreatedBy(rs.getString(8));
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
    public void delete(PostTemplate template) throws SQLException
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
        closeStatement(listByContentTypeStmt);
        listByContentTypeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByContentTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
