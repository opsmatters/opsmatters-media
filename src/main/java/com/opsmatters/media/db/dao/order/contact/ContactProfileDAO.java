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
package com.opsmatters.media.db.dao.order.contact;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.ContactProfile;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the CONTACT_PROFILES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactProfileDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContactProfileDAO.class.getName());

    /**
     * The query to use to select a profile from the CONTACT_PROFILES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, PRE_PAYMENT, INCLUDE_URL, ENABLED "
      + "FROM CONTACT_PROFILES WHERE ID=?";

    /**
     * The query to use to insert a profile into the CONTACT_PROFILES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTACT_PROFILES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, PRE_PAYMENT, INCLUDE_URL, ENABLED )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a profile in the CONTACT_PROFILES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTACT_PROFILES SET UPDATED_DATE=?, NAME=?, CONTACT_EMAIL=?, BILLING_EMAIL=?, COMPANY_ID=?, PAYMENT_METHOD=?, PAYMENT_MODE=?, PAYMENT_TERM=?, CURRENCY_CODE=?, PRE_PAYMENT=?, INCLUDE_URL=?, ENABLED=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the profiles from the CONTACT_PROFILES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, PRE_PAYMENT, INCLUDE_URL, ENABLED "
      + "FROM CONTACT_PROFILES ORDER BY CREATED_DATE";

    /**
     * The query to use to select the profiles from the CONTACT_PROFILES table by contact.
     */
    private static final String LIST_BY_CONTACT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, PRE_PAYMENT, INCLUDE_URL, ENABLED "
      + "FROM CONTACT_PROFILES WHERE CONTACT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of profiles from the CONTACT_PROFILES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTACT_PROFILES";

    /**
     * The query to use to delete a profile from the CONTACT_PROFILES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTACT_PROFILES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContactProfileDAO(OrderDAOFactory factory)
    {
        super(factory, "CONTACT_PROFILES");
    }

    /**
     * Defines the columns and indices for the CONTACT_PROFILES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CONTACT_ID", Types.VARCHAR, 36, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("CONTACT_EMAIL", Types.VARCHAR, 50, false);
        table.addColumn("BILLING_EMAIL", Types.VARCHAR, 50, false);
        table.addColumn("COMPANY_ID", Types.VARCHAR, 36, false);
        table.addColumn("PAYMENT_METHOD", Types.VARCHAR, 15, true);
        table.addColumn("PAYMENT_MODE", Types.VARCHAR, 15, true);
        table.addColumn("PAYMENT_TERM", Types.VARCHAR, 15, true);
        table.addColumn("CURRENCY_CODE", Types.VARCHAR, 5, true);
        table.addColumn("PRE_PAYMENT", Types.BOOLEAN, true);
        table.addColumn("INCLUDE_URL", Types.BOOLEAN, true);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.setPrimaryKey("CONTACT_PROFILES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a profile from the CONTACT_PROFILES table by id.
     */
    public synchronized ContactProfile getById(String id) throws SQLException
    {
        ContactProfile ret = null;

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
                ContactProfile profile = new ContactProfile();
                profile.setId(rs.getString(1));
                profile.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                profile.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                profile.setContactId(rs.getString(4));
                profile.setName(rs.getString(5));
                profile.setContactEmail(rs.getString(6));
                profile.setBillingEmail(rs.getString(7));
                profile.setCompanyId(rs.getString(8));
                profile.setPaymentMethod(rs.getString(9));
                profile.setPaymentMode(rs.getString(10));
                profile.setPaymentTerm(rs.getString(11));
                profile.setCurrency(rs.getString(12));
                profile.setPrePayment(rs.getBoolean(13));
                profile.setIncludeUrl(rs.getBoolean(14));
                profile.setEnabled(rs.getBoolean(15));
                ret = profile;
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
     * Stores the given profile in the CONTACT_PROFILES table.
     */
    public synchronized void add(ContactProfile profile) throws SQLException
    {
        if(!hasConnection() || profile == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, profile.getId());
            insertStmt.setTimestamp(2, new Timestamp(profile.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(profile.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, profile.getContactId());
            insertStmt.setString(5, profile.getName());
            insertStmt.setString(6, profile.getContactEmail());
            insertStmt.setString(7, profile.getBillingEmail());
            insertStmt.setString(8, profile.getCompanyId());
            insertStmt.setString(9, profile.getPaymentMethod().name());
            insertStmt.setString(10, profile.getPaymentMode().name());
            insertStmt.setString(11, profile.getPaymentTerm().name());
            insertStmt.setString(12, profile.getCurrency() != null ? profile.getCurrency().getCode() : null);
            insertStmt.setBoolean(13, profile.hasPrePayment());
            insertStmt.setBoolean(14, profile.includeUrl());
            insertStmt.setBoolean(15, profile.isEnabled());
            insertStmt.executeUpdate();

            logger.info("Created profile '"+profile.getId()+"' in CONTACT_PROFILES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the profile already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given profile in the CONTACT_PROFILES table.
     */
    public synchronized void update(ContactProfile profile) throws SQLException
    {
        if(!hasConnection() || profile == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(profile.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, profile.getName());
        updateStmt.setString(3, profile.getContactEmail());
        updateStmt.setString(4, profile.getBillingEmail());
        updateStmt.setString(5, profile.getCompanyId());
        updateStmt.setString(6, profile.getPaymentMethod().name());
        updateStmt.setString(7, profile.getPaymentMode().name());
        updateStmt.setString(8, profile.getPaymentTerm().name());
        updateStmt.setString(9, profile.getCurrency() != null ? profile.getCurrency().getCode() : null);
        updateStmt.setBoolean(10, profile.hasPrePayment());
        updateStmt.setBoolean(11, profile.includeUrl());
        updateStmt.setBoolean(12, profile.isEnabled());
        updateStmt.setString(13, profile.getId());
        updateStmt.executeUpdate();

        logger.info("Updated profile '"+profile.getId()+"' in CONTACT_PROFILES");
    }

    /**
     * Adds or Updates the given profile in the CONTACT_PROFILES table.
     */
    public boolean upsert(ContactProfile profile) throws SQLException
    {
        boolean ret = false;

        ContactProfile existing = getById(profile.getId());
        if(existing != null)
        {
            update(profile);
        }
        else
        {
            add(profile);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the profiles from the CONTACT_PROFILES table.
     */
    public synchronized List<ContactProfile> list() throws SQLException
    {
        List<ContactProfile> ret = null;

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
            ret = new ArrayList<ContactProfile>();
            while(rs.next())
            {
                ContactProfile profile = new ContactProfile();
                profile.setId(rs.getString(1));
                profile.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                profile.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                profile.setContactId(rs.getString(4));
                profile.setName(rs.getString(5));
                profile.setContactEmail(rs.getString(6));
                profile.setBillingEmail(rs.getString(7));
                profile.setCompanyId(rs.getString(8));
                profile.setPaymentMethod(rs.getString(9));
                profile.setPaymentMode(rs.getString(10));
                profile.setPaymentTerm(rs.getString(11));
                profile.setCurrency(rs.getString(12));
                profile.setPrePayment(rs.getBoolean(13));
                profile.setIncludeUrl(rs.getBoolean(14));
                profile.setEnabled(rs.getBoolean(15));
                ret.add(profile);
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
     * Returns the profiles from the CONTACT_PROFILES table by contact.
     */
    public synchronized List<ContactProfile> list(String contactId) throws SQLException
    {
        List<ContactProfile> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContactStmt == null)
            listByContactStmt = prepareStatement(getConnection(), LIST_BY_CONTACT_SQL);
        clearParameters(listByContactStmt);

        ResultSet rs = null;

        try
        {
            listByContactStmt.setString(1, contactId);
            listByContactStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContactStmt.executeQuery();
            ret = new ArrayList<ContactProfile>();
            while(rs.next())
            {
                ContactProfile profile = new ContactProfile();
                profile.setId(rs.getString(1));
                profile.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                profile.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                profile.setContactId(rs.getString(4));
                profile.setName(rs.getString(5));
                profile.setContactEmail(rs.getString(6));
                profile.setBillingEmail(rs.getString(7));
                profile.setCompanyId(rs.getString(8));
                profile.setPaymentMethod(rs.getString(9));
                profile.setPaymentMode(rs.getString(10));
                profile.setPaymentTerm(rs.getString(11));
                profile.setCurrency(rs.getString(12));
                profile.setPrePayment(rs.getBoolean(13));
                profile.setIncludeUrl(rs.getBoolean(14));
                profile.setEnabled(rs.getBoolean(15));
                ret.add(profile);
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
     * Returns the count of profiles from the CONTACT_PROFILES table.
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
     * Removes the given profile from the CONTACT_PROFILES table.
     */
    public synchronized void delete(ContactProfile profile) throws SQLException
    {
        if(!hasConnection() || profile == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, profile.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted profile '"+profile.getId()+"' in CONTACT_PROFILES");
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
        closeStatement(listByContactStmt);
        listByContactStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByContactStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
