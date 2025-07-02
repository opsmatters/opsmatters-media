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
package com.opsmatters.media.db.dao.order;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.Order;
import com.opsmatters.media.model.order.OrderStatus;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the ORDERS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrderDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OrderDAO.class.getName());

    /**
     * The query to use to select an order from the ORDERS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, CONTACT_PERSON_ID, COMPANY_ID, WEEK, MONTH, YEAR, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, INVOICE_ID, INVOICE_NUMBER, INVOICE_EMAIL, INVOICE_URL, INVOICE_NOTE, INVOICE_STATUS, CREATED_BY "
      + "FROM ORDERS WHERE ID=?";

    /**
     * The query to use to insert an order into the ORDERS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORDERS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, CONTACT_PERSON_ID, COMPANY_ID, WEEK, MONTH, YEAR, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, INVOICE_ID, INVOICE_NUMBER, INVOICE_EMAIL, INVOICE_URL, INVOICE_NOTE, INVOICE_STATUS, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an order in the ORDERS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORDERS SET UPDATED_DATE=?, COMPANY_ID=?, WEEK=?, MONTH=?, YEAR=?, PAYMENT_METHOD=?, PAYMENT_MODE=?, PAYMENT_TERM=?, CURRENCY_CODE=?, STATUS=?, REASON=?, INVOICE_ID=?, INVOICE_NUMBER=?, INVOICE_EMAIL=?, INVOICE_URL=?, INVOICE_NOTE=?, INVOICE_STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the orders from the ORDERS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, CONTACT_PERSON_ID, COMPANY_ID, WEEK, MONTH, YEAR, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, INVOICE_ID, INVOICE_NUMBER, INVOICE_EMAIL, INVOICE_URL, INVOICE_NOTE, INVOICE_STATUS, CREATED_BY "
      + "FROM ORDERS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the orders from the ORDERS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, CONTACT_PERSON_ID, COMPANY_ID, WEEK, MONTH, YEAR, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, INVOICE_ID, INVOICE_NUMBER, INVOICE_EMAIL, INVOICE_URL, INVOICE_NOTE, INVOICE_STATUS, CREATED_BY "
      + "FROM ORDERS WHERE STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the orders from the ORDERS table by contact.
     */
    private static final String LIST_BY_CONTACT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, CONTACT_PERSON_ID, COMPANY_ID, WEEK, MONTH, YEAR, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, INVOICE_ID, INVOICE_NUMBER, INVOICE_EMAIL, INVOICE_URL, INVOICE_NOTE, INVOICE_STATUS, CREATED_BY "
      + "FROM ORDERS WHERE CONTACT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the orders from the ORDERS table by contact and status.
     */
    private static final String LIST_BY_CONTACT_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, CONTACT_PERSON_ID, COMPANY_ID, WEEK, MONTH, YEAR, PAYMENT_METHOD, PAYMENT_MODE, PAYMENT_TERM, CURRENCY_CODE, STATUS, REASON, INVOICE_ID, INVOICE_NUMBER, INVOICE_EMAIL, INVOICE_URL, INVOICE_NOTE, INVOICE_STATUS, CREATED_BY "
      + "FROM ORDERS WHERE CONTACT_ID=? AND STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of orders from the ORDERS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORDERS";

    /**
     * The query to use to delete an order from the ORDERS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORDERS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrderDAO(OrderDAOFactory factory)
    {
        super(factory, "ORDERS");
    }

    /**
     * Defines the columns and indices for the ORDERS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CONTACT_ID", Types.VARCHAR, 36, true);
        table.addColumn("CONTACT_PERSON_ID", Types.VARCHAR, 36, false);
        table.addColumn("COMPANY_ID", Types.VARCHAR, 36, false);
        table.addColumn("WEEK", Types.INTEGER, true);
        table.addColumn("MONTH", Types.INTEGER, true);
        table.addColumn("YEAR", Types.INTEGER, true);
        table.addColumn("PAYMENT_METHOD", Types.VARCHAR, 15, true);
        table.addColumn("PAYMENT_MODE", Types.VARCHAR, 15, true);
        table.addColumn("PAYMENT_TERM", Types.VARCHAR, 15, true);
        table.addColumn("CURRENCY_CODE", Types.VARCHAR, 5, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("REASON", Types.VARCHAR, 15, false);
        table.addColumn("INVOICE_ID", Types.VARCHAR, 30, false);
        table.addColumn("INVOICE_NUMBER", Types.VARCHAR, 30, false);
        table.addColumn("INVOICE_EMAIL", Types.VARCHAR, 50, false);
        table.addColumn("INVOICE_URL", Types.VARCHAR, 128, false);
        table.addColumn("INVOICE_NOTE", Types.LONGVARCHAR, false);
        table.addColumn("INVOICE_STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("ORDERS_PK", new String[] {"ID"});
        table.addIndex("ORDERS_CONTACT_IDX", new String[] {"CONTACT_ID"});
        table.addIndex("ORDERS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("ORDERS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an order from the ORDERS table by id.
     */
    public synchronized Order getById(String id) throws SQLException
    {
        Order ret = null;

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
                Order order = new Order();
                order.setId(rs.getString(1));
                order.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                order.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                order.setContactId(rs.getString(4));
                order.setContactPersonId(rs.getString(5));
                order.setCompanyId(rs.getString(6));
                order.setWeek(rs.getInt(7));
                order.setMonth(rs.getInt(8));
                order.setYear(rs.getInt(9));
                order.setPaymentMethod(rs.getString(10));
                order.setPaymentMode(rs.getString(11));
                order.setPaymentTerm(rs.getString(12));
                order.setCurrency(rs.getString(13));
                order.setStatus(rs.getString(14));
                order.setReason(rs.getString(15));
                order.getInvoice().setId(rs.getString(16));
                order.getInvoice().setNumber(rs.getString(17));
                order.getInvoice().setEmail(rs.getString(18));
                order.getInvoice().setUrl(rs.getString(19));
                order.getInvoice().setNote(rs.getString(20));
                order.getInvoice().setStatus(rs.getString(21));
                order.setCreatedBy(rs.getString(22));
                ret = order;
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
     * Stores the given order in the ORDERS table.
     */
    public synchronized void add(Order order) throws SQLException
    {
        if(!hasConnection() || order == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, order.getId());
            insertStmt.setTimestamp(2, new Timestamp(order.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(order.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, order.getContactId());
            insertStmt.setString(5, order.getContactPersonId());
            insertStmt.setString(6, order.getCompanyId());
            insertStmt.setInt(7, order.getWeek());
            insertStmt.setInt(8, order.getMonth());
            insertStmt.setInt(9, order.getYear());
            insertStmt.setString(10, order.getPaymentMethod().name());
            insertStmt.setString(11, order.getPaymentMode().name());
            insertStmt.setString(12, order.getPaymentTerm().name());
            insertStmt.setString(13, order.getCurrency().code());
            insertStmt.setString(14, order.getStatus().name());
            insertStmt.setString(15, order.getReason().name());
            insertStmt.setString(16, order.getInvoice().getId());
            insertStmt.setString(17, order.getInvoice().getNumber());
            insertStmt.setString(18, order.getInvoice().getEmail());
            insertStmt.setString(19, order.getInvoice().getUrl());
            insertStmt.setString(20, order.getInvoice().getNote());
            insertStmt.setString(21, order.getInvoice().getStatus().name());
            insertStmt.setString(22, order.getCreatedBy());
            insertStmt.setInt(23, SessionId.get());
            insertStmt.executeUpdate();

            logger.info("Created order '"+order.getId()+"' in ORDERS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the order already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given order in the ORDERS table.
     */
    public synchronized void update(Order order) throws SQLException
    {
        if(!hasConnection() || order == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(order.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, order.getCompanyId());
        updateStmt.setInt(3, order.getWeek());
        updateStmt.setInt(4, order.getMonth());
        updateStmt.setInt(5, order.getYear());
        updateStmt.setString(6, order.getPaymentMethod().name());
        updateStmt.setString(7, order.getPaymentMode().name());
        updateStmt.setString(8, order.getPaymentTerm().name());
        updateStmt.setString(9, order.getCurrency().code());
        updateStmt.setString(10, order.getStatus().name());
        updateStmt.setString(11, order.getReason().name());
        updateStmt.setString(12, order.getInvoice().getId());
        updateStmt.setString(13, order.getInvoice().getNumber());
        updateStmt.setString(14, order.getInvoice().getEmail());
        updateStmt.setString(15, order.getInvoice().getUrl());
        updateStmt.setString(16, order.getInvoice().getNote());
        updateStmt.setString(17, order.getInvoice().getStatus().name());
        updateStmt.setString(18, order.getCreatedBy());
        updateStmt.setString(19, order.getId());
        updateStmt.executeUpdate();

        logger.info("Updated order '"+order.getId()+"' in ORDERS");
    }

    /**
     * Adds or Updates the given order in the ORDERS table.
     */
    public boolean upsert(Order order) throws SQLException
    {
        boolean ret = false;

        Order existing = getById(order.getId());
        if(existing != null)
        {
            update(order);
        }
        else
        {
            add(order);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the orders from the ORDERS table.
     */
    public synchronized List<Order> list() throws SQLException
    {
        List<Order> ret = null;

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
            ret = new ArrayList<Order>();
            while(rs.next())
            {
                Order order = new Order();
                order.setId(rs.getString(1));
                order.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                order.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                order.setContactId(rs.getString(4));
                order.setContactPersonId(rs.getString(5));
                order.setCompanyId(rs.getString(6));
                order.setWeek(rs.getInt(7));
                order.setMonth(rs.getInt(8));
                order.setYear(rs.getInt(9));
                order.setPaymentMethod(rs.getString(10));
                order.setPaymentMode(rs.getString(11));
                order.setPaymentTerm(rs.getString(12));
                order.setCurrency(rs.getString(13));
                order.setStatus(rs.getString(14));
                order.setReason(rs.getString(15));
                order.getInvoice().setId(rs.getString(16));
                order.getInvoice().setNumber(rs.getString(17));
                order.getInvoice().setEmail(rs.getString(18));
                order.getInvoice().setUrl(rs.getString(19));
                order.getInvoice().setNote(rs.getString(20));
                order.getInvoice().setStatus(rs.getString(21));
                order.setCreatedBy(rs.getString(22));
                ret.add(order);
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
     * Returns the orders from the ORDERS table by status.
     */
    public synchronized List<Order> list(OrderStatus status) throws SQLException
    {
        List<Order> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByStatusStmt == null)
            listByStatusStmt = prepareStatement(getConnection(), LIST_BY_STATUS_SQL);
        clearParameters(listByStatusStmt);

        ResultSet rs = null;

        try
        {
            listByStatusStmt.setString(1, status.name());
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<Order>();
            while(rs.next())
            {
                Order order = new Order();
                order.setId(rs.getString(1));
                order.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                order.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                order.setContactId(rs.getString(4));
                order.setContactPersonId(rs.getString(5));
                order.setCompanyId(rs.getString(6));
                order.setWeek(rs.getInt(7));
                order.setMonth(rs.getInt(8));
                order.setYear(rs.getInt(9));
                order.setPaymentMethod(rs.getString(10));
                order.setPaymentMode(rs.getString(11));
                order.setPaymentTerm(rs.getString(12));
                order.setCurrency(rs.getString(13));
                order.setStatus(rs.getString(14));
                order.setReason(rs.getString(15));
                order.getInvoice().setId(rs.getString(16));
                order.getInvoice().setNumber(rs.getString(17));
                order.getInvoice().setEmail(rs.getString(18));
                order.getInvoice().setUrl(rs.getString(19));
                order.getInvoice().setNote(rs.getString(20));
                order.getInvoice().setStatus(rs.getString(21));
                order.setCreatedBy(rs.getString(22));
                ret.add(order);
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
     * Returns the orders from the ORDERS table by contact.
     */
    public synchronized List<Order> list(String contactId) throws SQLException
    {
        List<Order> ret = null;

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
            ret = new ArrayList<Order>();
            while(rs.next())
            {
                Order order = new Order();
                order.setId(rs.getString(1));
                order.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                order.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                order.setContactId(rs.getString(4));
                order.setContactPersonId(rs.getString(5));
                order.setCompanyId(rs.getString(6));
                order.setWeek(rs.getInt(7));
                order.setMonth(rs.getInt(8));
                order.setYear(rs.getInt(9));
                order.setPaymentMethod(rs.getString(10));
                order.setPaymentMode(rs.getString(11));
                order.setPaymentTerm(rs.getString(12));
                order.setCurrency(rs.getString(13));
                order.setStatus(rs.getString(14));
                order.setReason(rs.getString(15));
                order.getInvoice().setId(rs.getString(16));
                order.getInvoice().setNumber(rs.getString(17));
                order.getInvoice().setEmail(rs.getString(18));
                order.getInvoice().setUrl(rs.getString(19));
                order.getInvoice().setNote(rs.getString(20));
                order.getInvoice().setStatus(rs.getString(21));
                order.setCreatedBy(rs.getString(22));
                ret.add(order);
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
     * Returns the orders from the ORDERS table by contact.
     */
    public synchronized List<Order> list(String contactId, OrderStatus status) throws SQLException
    {
        List<Order> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContactStatusStmt == null)
            listByContactStatusStmt = prepareStatement(getConnection(), LIST_BY_CONTACT_STATUS_SQL);
        clearParameters(listByContactStatusStmt);

        ResultSet rs = null;

        try
        {
            listByContactStatusStmt.setString(1, contactId);
            listByContactStatusStmt.setString(2, status.name());
            listByContactStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContactStatusStmt.executeQuery();
            ret = new ArrayList<Order>();
            while(rs.next())
            {
                Order order = new Order();
                order.setId(rs.getString(1));
                order.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                order.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                order.setContactId(rs.getString(4));
                order.setContactPersonId(rs.getString(5));
                order.setCompanyId(rs.getString(6));
                order.setWeek(rs.getInt(7));
                order.setMonth(rs.getInt(8));
                order.setYear(rs.getInt(9));
                order.setPaymentMethod(rs.getString(10));
                order.setPaymentMode(rs.getString(11));
                order.setPaymentTerm(rs.getString(12));
                order.setCurrency(rs.getString(13));
                order.setStatus(rs.getString(14));
                order.setReason(rs.getString(15));
                order.getInvoice().setId(rs.getString(16));
                order.getInvoice().setNumber(rs.getString(17));
                order.getInvoice().setEmail(rs.getString(18));
                order.getInvoice().setUrl(rs.getString(19));
                order.getInvoice().setNote(rs.getString(20));
                order.getInvoice().setStatus(rs.getString(21));
                order.setCreatedBy(rs.getString(22));
                ret.add(order);
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
     * Returns the aging orders from the ORDERS table by contact.
     */
    public List<Order> listAging(Contact contact, int days) throws SQLException
    {
        List<Order> ret = new ArrayList<Order>();

        Instant now = Instant.now();
        List<Order> orders = list(contact.getId(), OrderStatus.DELIVERED);
        for(Order order : orders)
        {
            // Add the DELIVERED invoices more than "days" old
            if(ChronoUnit.DAYS.between(order.getCreatedDate(), now) > days)
            {
                ret.add(order);
            }
        }

        return ret;
    }

    /**
     * Returns the count of orders from the ORDERS table.
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
     * Removes the given order from the ORDERS table.
     */
    public synchronized void delete(Order order) throws SQLException
    {
        if(!hasConnection() || order == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, order.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted order '"+order.getId()+"' in ORDERS");
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
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(listByContactStmt);
        listByContactStmt = null;
        closeStatement(listByContactStatusStmt);
        listByContactStatusStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement listByContactStmt;
    private PreparedStatement listByContactStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
