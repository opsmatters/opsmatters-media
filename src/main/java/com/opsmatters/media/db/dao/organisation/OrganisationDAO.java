/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.db.dao.organisation;

import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.model.organisation.Organisation;

/**
 * DAO that provides operations on the ORGANISATIONS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OrganisationDAO.class.getName());

    /**
     * The query to use to select an organisation from the ORGANISATIONS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ATTRIBUTES, CREATED_BY "
      + "FROM ORGANISATIONS WHERE ID=?";

    /**
     * The query to use to select an organisation from the ORGANISATIONS table by code.
     */
    private static final String GET_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ATTRIBUTES, CREATED_BY "
      + "FROM ORGANISATIONS WHERE CODE=?";

    /**
     * The query to use to insert an organisation into the ORGANISATIONS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORGANISATIONS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ATTRIBUTES, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an organisation in the ORGANISATIONS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATIONS SET CODE=?, NAME=?, UPDATED_DATE=?, ATTRIBUTES=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the organisations from the ORGANISATIONS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ATTRIBUTES, CREATED_BY "
      + "FROM ORGANISATIONS ORDER BY CODE";

    /**
     * The query to use to get the count of organisations from the ORGANISATIONS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORGANISATIONS";

    /**
     * The query to use to delete an organisation from the ORGANISATIONS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATIONS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrganisationDAO(OrganisationDAOFactory factory)
    {
        super(factory, "ORGANISATIONS");
    }

    /**
     * Defines the columns and indices for the ORGANISATIONS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("NAME", Types.VARCHAR, 60, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("ORGANISATIONS_PK", new String[] {"ID"});
        table.addIndex("ORGANISATIONS_CODE_IDX", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns an organisation from the ORGANISATIONS table by id.
     */
    public synchronized Organisation getById(String id) throws SQLException
    {
        Organisation ret = null;

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
                Organisation organisation = new Organisation();
                organisation.setId(rs.getString(1));
                organisation.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(4));
                organisation.setName(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setCreatedBy(rs.getString(7));
                ret = organisation;
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
     * Returns an organisation from the ORGANISATIONS table by code.
     */
    public synchronized Organisation getByCode(String code) throws SQLException
    {
        Organisation ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByCodeStmt == null)
            getByCodeStmt = prepareStatement(getConnection(), GET_BY_CODE_SQL);
        clearParameters(getByCodeStmt);

        ResultSet rs = null;

        try
        {
            getByCodeStmt.setString(1, code);
            getByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByCodeStmt.executeQuery();
            while(rs.next())
            {
                Organisation organisation = new Organisation();
                organisation.setId(rs.getString(1));
                organisation.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(4));
                organisation.setName(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setCreatedBy(rs.getString(7));
                ret = organisation;
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
     * Stores the given organisation in the ORGANISATIONS table.
     */
    public synchronized void add(Organisation organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, organisation.getId());
            insertStmt.setTimestamp(2, new Timestamp(organisation.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(organisation.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, organisation.getCode());
            insertStmt.setString(5, organisation.getName());
            String attributes = organisation.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(6, reader, attributes.length());
            insertStmt.setString(7, organisation.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created organisation '"+organisation.getId()+"' in ORGANISATIONS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the organisation already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Updates the given organisation in the ORGANISATIONS table.
     */
    public synchronized void update(Organisation organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setString(1, organisation.getCode());
            updateStmt.setString(2, organisation.getName());
            updateStmt.setTimestamp(3, new Timestamp(organisation.getUpdatedDateMillis()), UTC);
            String attributes = organisation.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(4, reader, attributes.length());
            updateStmt.setString(5, organisation.getCreatedBy());
            updateStmt.setString(6, organisation.getId());
            updateStmt.executeUpdate();

            logger.info("Updated organisation '"+organisation.getId()+"' in ORGANISATIONS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Adds or Updates the given monitor in the ORGANISATIONS table.
     */
    public boolean upsert(Organisation organisation) throws SQLException
    {
        boolean ret = false;

        Organisation existing = getById(organisation.getId());
        if(existing != null)
        {
            update(organisation);
        }
        else
        {
            add(organisation);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the organisations from the ORGANISATIONS table.
     */
    public synchronized List<Organisation> list() throws SQLException
    {
        List<Organisation> ret = null;

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
            ret = new ArrayList<Organisation>();
            while(rs.next())
            {
                Organisation organisation = new Organisation();
                organisation.setId(rs.getString(1));
                organisation.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(4));
                organisation.setName(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setCreatedBy(rs.getString(7));
                ret.add(organisation);
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
     * Returns the count of organisations from the table.
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
     * Removes the given organisation from the ORGANISATIONS table.
     */
    public synchronized void delete(Organisation organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, organisation.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted organisation '"+organisation.getId()+"' in ORGANISATIONS");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByCodeStmt);
        getByCodeStmt = null;
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
    private PreparedStatement getByCodeStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
