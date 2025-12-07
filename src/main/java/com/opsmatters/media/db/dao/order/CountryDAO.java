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
package com.opsmatters.media.db.dao.order;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.Country;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the COUNTRIES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CountryDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(CountryDAO.class.getName());

    /**
     * The query to use to select a country from the COUNTRIES table by id.
     */
    private static final String GET_BY_ID_SQL =
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ENABLED, CREATED_BY "
      + "FROM COUNTRIES WHERE ID=?";

    /**
     * The query to use to insert a country into the COUNTRIES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO COUNTRIES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ENABLED, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a country in the COUNTRIES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE COUNTRIES SET UPDATED_DATE=?, CODE=?, NAME=?, ENABLED=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the countries from the COUNTRIES table.
     */
    private static final String LIST_SQL =
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ENABLED, CREATED_BY "
      + "FROM COUNTRIES ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of countries from the COUNTRIES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM COUNTRIES";

    /**
     * The query to use to delete a country from the COUNTRIES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM COUNTRIES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public CountryDAO(OrderDAOFactory factory)
    {
        super(factory, "COUNTRIES");
    }

    /**
     * Defines the columns and indices for the COUNTRIES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("COUNTRIES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a country from the COUNTRIES table by id.
     */
    public synchronized Country getById(String id) throws SQLException
    {
        Country ret = null;

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
                Country country = new Country();
                country.setId(rs.getString(1));
                country.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                country.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                country.setCode(rs.getString(4));
                country.setName(rs.getString(5));
                country.setEnabled(rs.getBoolean(6));
                country.setCreatedBy(rs.getString(7));
                ret = country;
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
     * Stores the given country in the COUNTRIES table.
     */
    public synchronized void add(Country country) throws SQLException
    {
        if(!hasConnection() || country == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, country.getId());
            insertStmt.setTimestamp(2, new Timestamp(country.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(country.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, country.getCode());
            insertStmt.setString(5, country.getName());
            insertStmt.setBoolean(6, country.isEnabled());
            insertStmt.setString(7, country.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created country '"+country.getId()+"' in COUNTRIES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the country already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given country in the COUNTRIES table.
     */
    public synchronized void update(Country country) throws SQLException
    {
        if(!hasConnection() || country == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(country.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, country.getCode());
        updateStmt.setString(3, country.getName());
        updateStmt.setBoolean(4, country.isEnabled());
        updateStmt.setString(5, country.getCreatedBy());
        updateStmt.setString(6, country.getId());
        updateStmt.executeUpdate();

        logger.info("Updated country '"+country.getId()+"' in COUNTRIES");
    }

    /**
     * Adds or Updates the given country in the COUNTRIES table.
     */
    public boolean upsert(Country country) throws SQLException
    {
        boolean ret = false;

        Country existing = getById(country.getId());
        if(existing != null)
        {
            update(country);
        }
        else
        {
            add(country);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the countries from the COUNTRIES table.
     */
    public synchronized List<Country> list() throws SQLException
    {
        List<Country> ret = null;

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
            ret = new ArrayList<Country>();
            while(rs.next())
            {
                Country country = new Country();
                country.setId(rs.getString(1));
                country.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                country.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                country.setCode(rs.getString(4));
                country.setName(rs.getString(5));
                country.setEnabled(rs.getBoolean(6));
                country.setCreatedBy(rs.getString(7));
                ret.add(country);
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
     * Returns the count of countries from the COUNTRIES table.
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
     * Removes the given country from the COUNTRIES table.
     */
    public synchronized void delete(Country country) throws SQLException
    {
        if(!hasConnection() || country == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, country.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted country '"+country.getId()+"' in COUNTRIES");
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
