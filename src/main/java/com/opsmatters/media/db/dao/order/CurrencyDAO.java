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
import com.opsmatters.media.model.order.Currency;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the CURRENCIES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CurrencyDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(CurrencyDAO.class.getName());

    /**
     * The query to use to select a currency from the CURRENCIES table by id.
     */
    private static final String GET_BY_ID_SQL =
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ENABLED, CREATED_BY "
      + "FROM CURRENCIES WHERE ID=?";

    /**
     * The query to use to insert a currency into the CURRENCIES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CURRENCIES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ENABLED, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a currency in the CURRENCIES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CURRENCIES SET UPDATED_DATE=?, CODE=?, NAME=?, ENABLED=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the currencies from the CURRENCIES table.
     */
    private static final String LIST_SQL =
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, ENABLED, CREATED_BY "
      + "FROM CURRENCIES ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of currencies from the CURRENCIES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CURRENCIES";

    /**
     * The query to use to delete a currency from the CURRENCIES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CURRENCIES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public CurrencyDAO(OrderDAOFactory factory)
    {
        super(factory, "CURRENCIES");
    }

    /**
     * Defines the columns and indices for the CURRENCIES table.
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
        table.setPrimaryKey("CURRENCIES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a currency from the CURRENCIES table by id.
     */
    public synchronized Currency getById(String id) throws SQLException
    {
        Currency ret = null;

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
                Currency currency = new Currency();
                currency.setId(rs.getString(1));
                currency.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                currency.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                currency.setCode(rs.getString(4));
                currency.setName(rs.getString(5));
                currency.setEnabled(rs.getBoolean(6));
                currency.setCreatedBy(rs.getString(7));
                ret = currency;
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
     * Stores the given currency in the CURRENCIES table.
     */
    public synchronized void add(Currency currency) throws SQLException
    {
        if(!hasConnection() || currency == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, currency.getId());
            insertStmt.setTimestamp(2, new Timestamp(currency.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(currency.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, currency.getCode());
            insertStmt.setString(5, currency.getName());
            insertStmt.setBoolean(6, currency.isEnabled());
            insertStmt.setString(7, currency.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created currency '"+currency.getId()+"' in CURRENCIES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the currency already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given currency in the CURRENCIES table.
     */
    public synchronized void update(Currency currency) throws SQLException
    {
        if(!hasConnection() || currency == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(currency.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, currency.getCode());
        updateStmt.setString(3, currency.getName());
        updateStmt.setBoolean(4, currency.isEnabled());
        updateStmt.setString(5, currency.getCreatedBy());
        updateStmt.setString(6, currency.getId());
        updateStmt.executeUpdate();

        logger.info("Updated currency '"+currency.getId()+"' in CURRENCIES");
    }

    /**
     * Adds or Updates the given currency in the CURRENCIES table.
     */
    public boolean upsert(Currency currency) throws SQLException
    {
        boolean ret = false;

        Currency existing = getById(currency.getId());
        if(existing != null)
        {
            update(currency);
        }
        else
        {
            add(currency);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the currencies from the CURRENCIES table.
     */
    public synchronized List<Currency> list() throws SQLException
    {
        List<Currency> ret = null;

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
            ret = new ArrayList<Currency>();
            while(rs.next())
            {
                Currency currency = new Currency();
                currency.setId(rs.getString(1));
                currency.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                currency.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                currency.setCode(rs.getString(4));
                currency.setName(rs.getString(5));
                currency.setEnabled(rs.getBoolean(6));
                currency.setCreatedBy(rs.getString(7));
                ret.add(currency);
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
     * Returns the count of currencies from the CURRENCIES table.
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
     * Removes the given currency from the CURRENCIES table.
     */
    public synchronized void delete(Currency currency) throws SQLException
    {
        if(!hasConnection() || currency == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, currency.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted currency '"+currency.getId()+"' in CURRENCIES");
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
