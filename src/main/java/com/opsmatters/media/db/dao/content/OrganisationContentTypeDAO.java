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
package com.opsmatters.media.db.dao.content;

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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.OrganisationContentType;

/**
 * DAO that provides operations on the ORGANISATION_CONTENT_TYPES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationContentTypeDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OrganisationContentTypeDAO.class.getName());

    /**
     * The query to use to select a type from the ORGANISATION_CONTENT_TYPES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM ORGANISATION_CONTENT_TYPES WHERE ID=?";

    /**
     * The query to use to insert a type into the ORGANISATION_CONTENT_TYPES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORGANISATION_CONTENT_TYPES"
      + "( ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a type in the ORGANISATION_CONTENT_TYPES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATION_CONTENT_TYPES SET UPDATED_DATE=?, ATTRIBUTES=?, ITEM_COUNT=?, DEPLOYED=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the types from the ORGANISATION_CONTENT_TYPES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM ORGANISATION_CONTENT_TYPES WHERE SITE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the types from the ORGANISATION_CONTENT_TYPES table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM ORGANISATION_CONTENT_TYPES WHERE SITE_ID=? AND CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of types from the ORGANISATION_CONTENT_TYPES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORGANISATION_CONTENT_TYPES";

    /**
     * The query to use to delete a type from the ORGANISATION_CONTENT_TYPES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATION_CONTENT_TYPES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrganisationContentTypeDAO(ContentDAOFactory factory)
    {
        super(factory, "ORGANISATION_CONTENT_TYPES");
    }

    /**
     * Defines the columns and indices for the ORGANISATION_CONTENT_TYPES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("ITEM_COUNT", Types.INTEGER, true);
        table.addColumn("DEPLOYED", Types.BOOLEAN, true);
        table.setPrimaryKey("ORG_CONTENT_TYPES_PK", new String[] {"ID"});
        table.addIndex("ORG_CONTENT_TYPES_CODE_IDX", new String[] {"SITE_ID","CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns a type from the ORGANISATION_CONTENT_TYPES table by id.
     */
    public synchronized OrganisationContentType getById(String id) throws SQLException
    {
        OrganisationContentType ret = null;

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
                OrganisationContentType type = new OrganisationContentType();
                type.setId(rs.getString(1));
                type.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                type.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                type.setSiteId(rs.getString(4));
                type.setCode(rs.getString(5));
                type.setType(rs.getString(6));
                type.setAttributes(new JSONObject(getClob(rs, 7)));
                type.setItemCount(rs.getInt(8));
                type.setDeployed(rs.getBoolean(9));
                ret = type;
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
     * Stores the given type in the ORGANISATION_CONTENT_TYPES table.
     */
    public synchronized void add(OrganisationContentType type) throws SQLException
    {
        if(!hasConnection() || type == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, type.getId());
            insertStmt.setTimestamp(2, new Timestamp(type.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(type.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, type.getSiteId());
            insertStmt.setString(5, type.getCode());
            insertStmt.setString(6, type.getType().name());
            String attributes = type.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(7, reader, attributes.length());
            insertStmt.setLong(8, type.getItemCount());
            insertStmt.setBoolean(9, type.isDeployed());
            insertStmt.executeUpdate();

            logger.info("Created type '"+type.getId()+"' in ORGANISATION_CONTENT_TYPES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the type already exists
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
     * Updates the given type in the ORGANISATION_CONTENT_TYPES table.
     */
    public synchronized void update(OrganisationContentType type) throws SQLException
    {
        if(!hasConnection() || type == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(type.getUpdatedDateMillis()), UTC);
            String attributes = type.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(2, reader, attributes.length());
            updateStmt.setLong(3, type.getItemCount());
            updateStmt.setBoolean(4, type.isDeployed());
            updateStmt.setString(5, type.getId());
            updateStmt.executeUpdate();

            logger.info("Updated type '"+type.getId()+"' in ORGANISATION_CONTENT_TYPES");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the types from the ORGANISATION_CONTENT_TYPES table.
     */
    public synchronized List<OrganisationContentType> list(Site site) throws SQLException
    {
        List<OrganisationContentType> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setString(1, site.getId());
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<OrganisationContentType>();
            while(rs.next())
            {
                OrganisationContentType type = new OrganisationContentType();
                type.setId(rs.getString(1));
                type.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                type.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                type.setSiteId(rs.getString(4));
                type.setCode(rs.getString(5));
                type.setType(rs.getString(6));
                type.setAttributes(new JSONObject(getClob(rs, 7)));
                type.setItemCount(rs.getInt(8));
                type.setDeployed(rs.getBoolean(9));
                ret.add(type);
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
     * Returns the types from the ORGANISATION_CONTENT_TYPES table by organisation code.
     */
    public synchronized List<OrganisationContentType> list(Site site, String code) throws SQLException
    {
        List<OrganisationContentType> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, site.getId());
            listByCodeStmt.setString(2, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<OrganisationContentType>();
            while(rs.next())
            {
                OrganisationContentType type = new OrganisationContentType();
                type.setId(rs.getString(1));
                type.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                type.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                type.setSiteId(rs.getString(4));
                type.setCode(rs.getString(5));
                type.setType(rs.getString(6));
                type.setAttributes(new JSONObject(getClob(rs, 7)));
                type.setItemCount(rs.getInt(8));
                type.setDeployed(rs.getBoolean(9));
                ret.add(type);
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
     * Returns the count of types from the table.
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
     * Removes the given type from the ORGANISATION_CONTENT_TYPES table.
     */
    public synchronized void delete(OrganisationContentType type) throws SQLException
    {
        if(!hasConnection() || type == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, type.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted type '"+type.getId()+"' in ORGANISATION_CONTENT_TYPES");
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
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
