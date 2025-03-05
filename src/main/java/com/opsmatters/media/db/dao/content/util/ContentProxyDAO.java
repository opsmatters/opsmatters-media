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
package com.opsmatters.media.db.dao.content.util;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.util.ContentProxy;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the CONTENT_PROXIES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentProxyDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentProxyDAO.class.getName());

    /**
     * The query to use to select a proxy from the CONTENT_PROXIES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, HOST, PORT, USERNAME, PASSWORD, COUNTRY_CODE, CITY_NAME, STATUS "
      + "FROM CONTENT_PROXIES WHERE ID=?";

    /**
     * The query to use to insert a proxy into the CONTENT_PROXIES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_PROXIES"
      + "( ID, CREATED_DATE, UPDATED_DATE, HOST, PORT, USERNAME, PASSWORD, COUNTRY_CODE, CITY_NAME, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a proxy in the CONTENT_PROXIES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_PROXIES SET UPDATED_DATE=?, HOST=?, PORT=?, USERNAME=?, PASSWORD=?, COUNTRY_CODE=?, CITY_NAME=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the proxies from the CONTENT_PROXIES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, HOST, PORT, USERNAME, PASSWORD, COUNTRY_CODE, CITY_NAME, STATUS "
      + "FROM CONTENT_PROXIES";

    /**
     * The query to use to get the count of proxies from the CONTENT_PROXIES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_PROXIES";

    /**
     * The query to use to delete a proxy from the CONTENT_PROXIES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_PROXIES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentProxyDAO(ContentUtilDAOFactory factory)
    {
        super(factory, "CONTENT_PROXIES");
    }

    /**
     * Defines the columns and indices for the CONTENT_PROXIES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("HOST", Types.VARCHAR, 20, true);
        table.addColumn("PORT", Types.INTEGER, true);
        table.addColumn("USERNAME", Types.VARCHAR, 20, false);
        table.addColumn("PASSWORD", Types.VARCHAR, 20, false);
        table.addColumn("COUNTRY_CODE", Types.VARCHAR, 5, false);
        table.addColumn("CITY_NAME", Types.VARCHAR, 20, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("CONTENT_PROXIES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an proxy from the CONTENT_PROXIES table by id.
     */
    public synchronized ContentProxy getById(String id) throws SQLException
    {
        ContentProxy ret = null;

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
                ContentProxy proxy = new ContentProxy();
                proxy.setId(rs.getString(1));
                proxy.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                proxy.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                proxy.setHost(rs.getString(4));
                proxy.setPort(rs.getInt(5));
                proxy.setUsername(rs.getString(6));
                proxy.setPassword(rs.getString(7));
                proxy.setCountryCode(rs.getString(8));
                proxy.setCityName(rs.getString(9));
                proxy.setStatus(rs.getString(10));
                ret = proxy;
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
     * Stores the given proxy in the CONTENT_PROXIES table.
     */
    public synchronized void add(ContentProxy proxy) throws SQLException
    {
        if(!hasConnection() || proxy == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, proxy.getId());
            insertStmt.setTimestamp(2, new Timestamp(proxy.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(proxy.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, proxy.getHost());
            insertStmt.setInt(5, proxy.getPort());
            insertStmt.setString(6, proxy.getUsername());
            insertStmt.setString(7, proxy.getPassword());
            insertStmt.setString(8, proxy.getCountryCode());
            insertStmt.setString(9, proxy.getCityName());
            insertStmt.setString(10, proxy.getStatus().name());
            insertStmt.executeUpdate();

            logger.info(String.format("Created proxy %s in CONTENT_PROXIES", proxy.getId()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the proxy already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given proxy in the CONTENT_PROXIES table.
     */
    public synchronized void update(ContentProxy proxy) throws SQLException
    {
        if(!hasConnection() || proxy == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(proxy.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, proxy.getHost());
        updateStmt.setInt(3, proxy.getPort());
        updateStmt.setString(4, proxy.getUsername());
        updateStmt.setString(5, proxy.getPassword());
        updateStmt.setString(6, proxy.getCountryCode());
        updateStmt.setString(7, proxy.getCityName());
        updateStmt.setString(8, proxy.getStatus().name());
        updateStmt.setString(9, proxy.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated proxy %s in CONTENT_PROXIES", proxy.getId()));
    }

    /**
     * Adds or Updates the given proxy in the CONTENT_PROXIES table.
     */
    public boolean upsert(ContentProxy proxy) throws SQLException
    {
        boolean ret = false;

        ContentProxy existing = getById(proxy.getId());
        if(existing != null)
        {
            update(proxy);
        }
        else
        {
            add(proxy);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the proxies from the CONTENT_PROXIES table.
     */
    public synchronized List<ContentProxy> list() throws SQLException
    {
        List<ContentProxy> ret = null;

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
            ret = new ArrayList<ContentProxy>();
            while(rs.next())
            {
                ContentProxy proxy = new ContentProxy();
                proxy.setId(rs.getString(1));
                proxy.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                proxy.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                proxy.setHost(rs.getString(4));
                proxy.setPort(rs.getInt(5));
                proxy.setUsername(rs.getString(6));
                proxy.setPassword(rs.getString(7));
                proxy.setCountryCode(rs.getString(8));
                proxy.setCityName(rs.getString(9));
                proxy.setStatus(rs.getString(10));
                ret.add(proxy);
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
     * Returns the count of proxies from the table.
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
     * Removes the given proxy from the CONTENT_PROXIES table.
     */
    public synchronized void delete(ContentProxy proxy) throws SQLException
    {
        if(!hasConnection() || proxy == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, proxy.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted proxy %s in CONTENT_PROXIES", proxy.getId()));
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
