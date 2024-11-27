/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.db.dao.order.contact;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.contact.ContactRate;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the CONTACT_RATES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactRateDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContactRateDAO.class.getName());

    /**
     * The query to use to select a rate from the CONTACT_RATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, SITE_ID, PRODUCT_CODE, AMOUNT, CURRENCY, NOTES  "
      + "FROM CONTACT_RATES WHERE ID=?";

    /**
     * The query to use to insert a rate into the CONTACT_RATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTACT_RATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, SITE_ID, PRODUCT_CODE, AMOUNT, CURRENCY, NOTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a rate in the CONTACT_RATES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTACT_RATES SET UPDATED_DATE=?, SITE_ID=?, PRODUCT_CODE=?, AMOUNT=?, CURRENCY=?, NOTES=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the rates from the CONTACT_RATES table by contact.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, SITE_ID, PRODUCT_CODE, AMOUNT, CURRENCY, NOTES  "
      + "FROM CONTACT_RATES WHERE CONTACT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of rates from the CONTACT_RATES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTACT_RATES";

    /**
     * The query to use to delete a rate from the CONTACT_RATES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTACT_RATES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContactRateDAO(OrderDAOFactory factory)
    {
        super(factory, "CONTACT_RATES");
    }

    /**
     * Defines the columns and indices for the CONTACT_RATES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CONTACT_ID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("PRODUCT_CODE", Types.VARCHAR, 5, true);
        table.addColumn("AMOUNT", Types.INTEGER, true);
        table.addColumn("CURRENCY", Types.VARCHAR, 5, true);
        table.addColumn("NOTES", Types.LONGVARCHAR, false);
        table.setPrimaryKey("CONTACT_RATES_PK", new String[] {"ID"});
        table.addIndex("CONTACT_RATES_CONTACT_IDX", new String[] {"CONTACT_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a rate from the CONTACT_RATES table by id.
     */
    public synchronized ContactRate getById(String id) throws SQLException
    {
        ContactRate ret = null;

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
                ContactRate rate = new ContactRate();
                rate.setId(rs.getString(1));
                rate.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                rate.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                rate.setContactId(rs.getString(4));
                rate.setSiteId(rs.getString(5));
                rate.setProductCode(rs.getString(6));
                rate.setAmount(rs.getInt(7));
                rate.setCurrency(rs.getString(8));
                rate.setNotes(rs.getString(9));
                ret = rate;
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
     * Stores the given rate in the CONTACT_RATES table.
     */
    public synchronized void add(ContactRate rate) throws SQLException
    {
        if(!hasConnection() || rate == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, rate.getId());
            insertStmt.setTimestamp(2, new Timestamp(rate.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(rate.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, rate.getContactId());
            insertStmt.setString(5, rate.getSiteId());
            insertStmt.setString(6, rate.getProductCode());
            insertStmt.setInt(7, rate.getAmount());
            insertStmt.setString(8, rate.getCurrency().code());
            insertStmt.setString(9, rate.getNotes());
            insertStmt.executeUpdate();

            logger.info("Created contact rate '"+rate.getId()+"' in CONTACT_RATES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the contact rate  exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given rate in the CONTACT_RATES table.
     */
    public synchronized void update(ContactRate rate) throws SQLException
    {
        if(!hasConnection() || rate == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(rate.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, rate.getSiteId());
        updateStmt.setString(3, rate.getProductCode());
        updateStmt.setInt(4, rate.getAmount());
        updateStmt.setString(5, rate.getCurrency().code());
        updateStmt.setString(6, rate.getNotes());
        updateStmt.setString(7, rate.getId());
        updateStmt.executeUpdate();

        logger.info("Updated contact rate '"+rate.getId()+"' in CONTACT_RATES");
    }

    /**
     * Returns the rates from the CONTACT_RATES table by contact.
     */
    public synchronized List<ContactRate> list(Contact contact) throws SQLException
    {
        List<ContactRate> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setString(1, contact.getId());
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<ContactRate>();
            while(rs.next())
            {
                ContactRate rate = new ContactRate();
                rate.setId(rs.getString(1));
                rate.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                rate.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                rate.setContactId(rs.getString(4));
                rate.setSiteId(rs.getString(5));
                rate.setProductCode(rs.getString(6));
                rate.setAmount(rs.getInt(7));
                rate.setCurrency(rs.getString(8));
                rate.setNotes(rs.getString(9));
                ret.add(rate);
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
     * Returns the count of rates from the CONTACT_RATES table.
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
     * Removes the given rate from the CONTACT_RATES table.
     */
    public synchronized void delete(ContactRate rate) throws SQLException
    {
        if(!hasConnection() || rate == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, rate.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted contact rate '"+rate.getId()+"' in CONTACT_RATES");
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
