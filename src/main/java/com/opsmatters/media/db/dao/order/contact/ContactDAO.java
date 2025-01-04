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
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the CONTACTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContactDAO.class.getName());

    /**
     * The query to use to select a contact from the CONTACTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, TYPE, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, WEBSITE, SALUTATION, NOTES, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, CREATED_BY "
      + "FROM CONTACTS WHERE ID=?";

    /**
     * The query to use to insert a contact into the CONTACTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTACTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, TYPE, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, WEBSITE, SALUTATION, NOTES, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a contact in the CONTACTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTACTS SET UPDATED_DATE=?, NAME=?, TYPE=?, CONTACT_EMAIL=?, BILLING_EMAIL=?, COMPANY_ID=?, WEBSITE=?, SALUTATION=?, NOTES=?, PAYMENT_METHOD=?, PAYMENT_MODE=?, PAYMENT_TERM=?, CURRENCY_CODE=?, STATUS=?, REASON=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the contacts from the CONTACTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, TYPE, CONTACT_EMAIL, BILLING_EMAIL, COMPANY_ID, WEBSITE, SALUTATION, NOTES, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, CREATED_BY "
      + "FROM CONTACTS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of contacts from the CONTACTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTACTS";

    /**
     * The query to use to delete a contact from the CONTACTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTACTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContactDAO(OrderDAOFactory factory)
    {
        super(factory, "CONTACTS");
    }

    /**
     * Defines the columns and indices for the CONTACTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("CONTACT_EMAIL", Types.VARCHAR, 50, false);
        table.addColumn("BILLING_EMAIL", Types.VARCHAR, 50, false);
        table.addColumn("COMPANY_ID", Types.VARCHAR, 36, false);
        table.addColumn("WEBSITE", Types.VARCHAR, 128, false);
        table.addColumn("SALUTATION", Types.VARCHAR, 30, false);
        table.addColumn("NOTES", Types.LONGVARCHAR, false);
        table.addColumn("PAYMENT_METHOD", Types.VARCHAR, 15, true);
        table.addColumn("PAYMENT_MODE", Types.VARCHAR, 15, true);
        table.addColumn("PAYMENT_TERM", Types.VARCHAR, 15, true);
        table.addColumn("CURRENCY_CODE", Types.VARCHAR, 5, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("REASON", Types.VARCHAR, 15, false);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("CONTACTS_PK", new String[] {"ID"});
        table.addIndex("CONTACTS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a contact from the CONTACTS table by id.
     */
    public synchronized Contact getById(String id) throws SQLException
    {
        Contact ret = null;

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
                Contact contact = new Contact();
                contact.setId(rs.getString(1));
                contact.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                contact.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                contact.setName(rs.getString(4));
                contact.setType(rs.getString(5));
                contact.setContactEmail(rs.getString(6));
                contact.setBillingEmail(rs.getString(7));
                contact.setCompanyId(rs.getString(8));
                contact.setWebsite(rs.getString(9));
                contact.setSalutation(rs.getString(10));
                contact.setNotes(rs.getString(11));
                contact.setPaymentMethod(rs.getString(12));
                contact.setPaymentMode(rs.getString(13));
                contact.setPaymentTerm(rs.getString(14));
                contact.setCurrency(rs.getString(15));
                contact.setStatus(rs.getString(16));
                contact.setReason(rs.getString(17));
                contact.setCreatedBy(rs.getString(18));
                ret = contact;
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
     * Stores the given contact in the CONTACTS table.
     */
    public synchronized void add(Contact contact) throws SQLException
    {
        if(!hasConnection() || contact == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, contact.getId());
            insertStmt.setTimestamp(2, new Timestamp(contact.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(contact.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, contact.getName());
            insertStmt.setString(5, contact.getType().name());
            insertStmt.setString(6, contact.getContactEmail());
            insertStmt.setString(7, contact.getBillingEmail());
            insertStmt.setString(8, contact.getCompanyId());
            insertStmt.setString(9, contact.getWebsite());
            insertStmt.setString(10, contact.getSalutation());
            insertStmt.setString(11, contact.getNotes());
            insertStmt.setString(12, contact.getPaymentMethod().name());
            insertStmt.setString(13, contact.getPaymentMode().name());
            insertStmt.setString(14, contact.getPaymentTerm().name());
            insertStmt.setString(15, contact.getCurrency().code());
            insertStmt.setString(16, contact.getStatus().name());
            insertStmt.setString(17, contact.getReason().name());
            insertStmt.setString(18, contact.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created contact '"+contact.getId()+"' in CONTACTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the contact already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given contact in the CONTACTS table.
     */
    public synchronized void update(Contact contact) throws SQLException
    {
        if(!hasConnection() || contact == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(contact.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, contact.getName());
        updateStmt.setString(3, contact.getType().name());
        updateStmt.setString(4, contact.getContactEmail());
        updateStmt.setString(5, contact.getBillingEmail());
        updateStmt.setString(6, contact.getCompanyId());
        updateStmt.setString(7, contact.getWebsite());
        updateStmt.setString(8, contact.getSalutation());
        updateStmt.setString(9, contact.getNotes());
        updateStmt.setString(10, contact.getPaymentMethod().name());
        updateStmt.setString(11, contact.getPaymentMode().name());
        updateStmt.setString(12, contact.getPaymentTerm().name());
        updateStmt.setString(13, contact.getCurrency().code());
        updateStmt.setString(14, contact.getStatus().name());
        updateStmt.setString(15, contact.getReason().name());
        updateStmt.setString(16, contact.getCreatedBy());
        updateStmt.setString(17, contact.getId());
        updateStmt.executeUpdate();

        logger.info("Updated contact '"+contact.getId()+"' in CONTACTS");
    }

    /**
     * Returns the contacts from the CONTACTS table.
     */
    public synchronized List<Contact> list() throws SQLException
    {
        List<Contact> ret = null;

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
            ret = new ArrayList<Contact>();
            while(rs.next())
            {
                Contact contact = new Contact();
                contact.setId(rs.getString(1));
                contact.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                contact.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                contact.setName(rs.getString(4));
                contact.setType(rs.getString(5));
                contact.setContactEmail(rs.getString(6));
                contact.setBillingEmail(rs.getString(7));
                contact.setCompanyId(rs.getString(8));
                contact.setWebsite(rs.getString(9));
                contact.setSalutation(rs.getString(10));
                contact.setNotes(rs.getString(11));
                contact.setPaymentMethod(rs.getString(12));
                contact.setPaymentMode(rs.getString(13));
                contact.setPaymentTerm(rs.getString(14));
                contact.setCurrency(rs.getString(15));
                contact.setStatus(rs.getString(16));
                contact.setReason(rs.getString(17));
                contact.setCreatedBy(rs.getString(18));
                ret.add(contact);
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
     * Returns the count of contacts from the CONTACTS table.
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
     * Removes the given contact from the CONTACTS table.
     */
    public synchronized void delete(Contact contact) throws SQLException
    {
        if(!hasConnection() || contact == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, contact.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted contact '"+contact.getId()+"' in CONTACTS");
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
