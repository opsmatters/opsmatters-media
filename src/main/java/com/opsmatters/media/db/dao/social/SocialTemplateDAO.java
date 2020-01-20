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
import com.opsmatters.media.model.social.SocialTemplate;

/**
 * DAO that provides operations on the SOCIAL_TEMPLATES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialTemplateDAO extends SocialDAO<SocialTemplate>
{
    private static final Logger logger = Logger.getLogger(SocialTemplateDAO.class.getName());

    /**
     * The query to use to select an item from the SOCIAL_TEMPLATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY "
      + "FROM SOCIAL_TEMPLATES WHERE ID=?";

    /**
     * The query to use to insert a social template into the SOCIAL_TEMPLATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SOCIAL_TEMPLATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a social template in the SOCIAL_TEMPLATES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE SOCIAL_TEMPLATES SET UPDATED_DATE=?, NAME=?, MESSAGE=?, CONTENT_TYPE=?, IS_DEFAULT=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the social templates from the SOCIAL_TEMPLATES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, MESSAGE, CONTENT_TYPE, IS_DEFAULT, CREATED_BY "
      + "FROM SOCIAL_TEMPLATES";

    /**
     * The query to use to get the count of social templates from the SOCIAL_TEMPLATES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SOCIAL_TEMPLATES";

    /**
     * The query to use to delete a social template from the SOCIAL_TEMPLATES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SOCIAL_TEMPLATES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SocialTemplateDAO(SocialDAOFactory factory)
    {
        super(factory, "SOCIAL_TEMPLATES");
    }

    /**
     * Defines the columns and indices for the SOCIAL_TEMPLATES table.
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
        table.setPrimaryKey("SOCIAL_TEMPLATES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a social template from the SOCIAL_TEMPLATES table by id.
     */
    public SocialTemplate getById(int id) throws SQLException
    {
        SocialTemplate ret = null;

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
                SocialTemplate template = new SocialTemplate();
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
     * Stores the given social template in the SOCIAL_TEMPLATES table.
     */
    public void add(SocialTemplate template) throws SQLException
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

            logger.info("Created social template '"+template.getId()+"' in SOCIAL_TEMPLATES");
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
     * Updates the given social template in the SOCIAL_TEMPLATES table.
     */
    public void update(SocialTemplate template) throws SQLException
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

        logger.info("Updated social template '"+template.getId()+"' in SOCIAL_TEMPLATES");
    }

    /**
     * Returns the social templates from the SOCIAL_TEMPLATES table.
     */
    public List<SocialTemplate> list() throws SQLException
    {
        List<SocialTemplate> ret = null;

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
            ret = new ArrayList<SocialTemplate>();
            while(rs.next())
            {
                SocialTemplate template = new SocialTemplate();
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
     * Returns the count of social templates from the table.
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
     * Removes the given social template from the SOCIAL_TEMPLATES table.
     */
    public void delete(SocialTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, template.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted social template '"+template.getId()+"' in SOCIAL_TEMPLATES");
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
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
