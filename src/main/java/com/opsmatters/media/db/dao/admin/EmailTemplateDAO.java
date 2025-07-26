/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.db.dao.admin;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.EmailTemplate;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the EMAIL_TEMPLATES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EmailTemplateDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(EmailTemplateDAO.class.getName());

    /**
     * The query to use to select a template from the EMAIL_TEMPLATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TYPE, MESSAGE, STATUS, CREATED_BY "
      + "FROM EMAIL_TEMPLATES WHERE ID=?";

    /**
     * The query to use to insert a template into the EMAIL_TEMPLATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO EMAIL_TEMPLATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TYPE, MESSAGE, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a template in the EMAIL_TEMPLATES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE EMAIL_TEMPLATES SET UPDATED_DATE=?, CODE=?, NAME=?, TYPE=?, MESSAGE=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the templates from the EMAIL_TEMPLATES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TYPE, MESSAGE, STATUS, CREATED_BY "
      + "FROM EMAIL_TEMPLATES";

    /**
     * The query to use to get the count of templates from the EMAIL_TEMPLATES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM EMAIL_TEMPLATES";

    /**
     * The query to use to delete a template from the EMAIL_TEMPLATES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM EMAIL_TEMPLATES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public EmailTemplateDAO(AdminDAOFactory factory)
    {
        super(factory, "EMAIL_TEMPLATES");
    }

    /**
     * Defines the columns and indices for the EMAIL_TEMPLATES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 15, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("MESSAGE", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("EMAIL_TEMPLATES_PK", new String[] {"ID"});
        table.addIndex("EMAIL_TEMPLATES_CODE_IDX", new String[] {"CODE"});
        table.addIndex("EMAIL_TEMPLATES_TYPE_IDX", new String[] {"TYPE"});
        table.setInitialised(true);
    }

    /**
     * Returns a template from the EMAIL_TEMPLATES table by id.
     */
    public synchronized EmailTemplate getById(String id) throws SQLException
    {
        EmailTemplate ret = null;

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
                EmailTemplate template = new EmailTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setCode(rs.getString(4));
                template.setName(rs.getString(5));
                template.setType(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setStatus(rs.getString(8));
                template.setCreatedBy(rs.getString(9));
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
     * Stores the given template in the EMAIL_TEMPLATES table.
     */
    public synchronized void add(EmailTemplate template) throws SQLException
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
            insertStmt.setString(4, template.getCode());
            insertStmt.setString(5, template.getName());
            insertStmt.setString(6, template.getType().name());
            insertStmt.setString(7, template.getMessage());
            insertStmt.setString(8, template.getStatus().name());
            insertStmt.setString(9, template.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info(String.format("Created template %s in EMAIL_TEMPLATES", template.getId()));
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
     * Updates the given template in the EMAIL_TEMPLATES table.
     */
    public synchronized void update(EmailTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(template.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, template.getCode());
        updateStmt.setString(3, template.getName());
        updateStmt.setString(4, template.getType().name());
        updateStmt.setString(5, template.getMessage());
        updateStmt.setString(6, template.getStatus().name());
        updateStmt.setString(7, template.getCreatedBy());
        updateStmt.setString(8, template.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated template %s in EMAIL_TEMPLATES", template.getId()));
    }

    /**
     * Adds or Updates the given template in the EMAIL_TEMPLATES table.
     */
    public boolean upsert(EmailTemplate template) throws SQLException
    {
        boolean ret = false;

        EmailTemplate existing = getById(template.getId());
        if(existing != null)
        {
            update(template);
        }
        else
        {
            add(template);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the templates from the EMAIL_TEMPLATES table.
     */
    public synchronized List<EmailTemplate> list() throws SQLException
    {
        List<EmailTemplate> ret = null;

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
            ret = new ArrayList<EmailTemplate>();
            while(rs.next())
            {
                EmailTemplate template = new EmailTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setCode(rs.getString(4));
                template.setName(rs.getString(5));
                template.setType(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setStatus(rs.getString(8));
                template.setCreatedBy(rs.getString(9));
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
     * Returns the count of templates from the table.
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
     * Removes the given template from the EMAIL_TEMPLATES table.
     */
    public synchronized void delete(EmailTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, template.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted template %s in EMAIL_TEMPLATES", template.getId()));
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
