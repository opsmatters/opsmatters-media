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
import java.util.logging.Logger;
import com.opsmatters.media.model.order.Order;
import com.opsmatters.media.model.order.OrderItem;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the ORDER_ITEMS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrderItemDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OrderItemDAO.class.getName());

    /**
     * The query to use to select a item from the ORDER_ITEMS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORDER_ID, PRODUCT_CODE, SITE_ID, CONTENT_ID, CONTENT_TYPE, QUANTITY, PRICE, CURRENCY_CODE, NAME, DESCRIPTION, ENABLED "
      + "FROM ORDER_ITEMS WHERE ID=?";

    /**
     * The query to use to insert a item into the ORDER_ITEMS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORDER_ITEMS"
      + "( ID, CREATED_DATE, UPDATED_DATE, ORDER_ID, PRODUCT_CODE, SITE_ID, CONTENT_ID, CONTENT_TYPE, QUANTITY, PRICE, CURRENCY_CODE, NAME, DESCRIPTION, ENABLED )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a item in the ORDER_ITEMS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORDER_ITEMS SET UPDATED_DATE=?, PRODUCT_CODE=?, SITE_ID=?, CONTENT_ID=?, CONTENT_TYPE=?, QUANTITY=?, PRICE=?, CURRENCY_CODE=?, NAME=?, DESCRIPTION=?, ENABLED=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the items from the ORDER_ITEMS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORDER_ID, PRODUCT_CODE, SITE_ID, CONTENT_ID, CONTENT_TYPE, QUANTITY, PRICE, CURRENCY_CODE, NAME, DESCRIPTION, ENABLED "
      + "FROM ORDER_ITEMS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the items from the ORDER_ITEMS table by order.
     */
    private static final String LIST_BY_ORDER_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORDER_ID, PRODUCT_CODE, SITE_ID, CONTENT_ID, CONTENT_TYPE, QUANTITY, PRICE, CURRENCY_CODE, NAME, DESCRIPTION, ENABLED "
      + "FROM ORDER_ITEMS WHERE ORDER_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the items from the ORDER_ITEMS table by content.
     */
    private static final String LIST_BY_CONTENT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORDER_ID, PRODUCT_CODE, SITE_ID, CONTENT_ID, CONTENT_TYPE, QUANTITY, PRICE, CURRENCY_CODE, NAME, DESCRIPTION, ENABLED "
      + "FROM ORDER_ITEMS WHERE CONTENT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of items from the ORDER_ITEMS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORDER_ITEMS";

    /**
     * The query to use to delete a item from the ORDER_ITEMS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORDER_ITEMS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrderItemDAO(OrderDAOFactory factory)
    {
        super(factory, "ORDER_ITEMS");
    }

    /**
     * Defines the columns and indices for the ORDER_ITEMS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("ORDER_ID", Types.VARCHAR, 36, true);
        table.addColumn("PRODUCT_CODE", Types.VARCHAR, 5, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CONTENT_ID", Types.VARCHAR, 36, false);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("QUANTITY", Types.INTEGER, true);
        table.addColumn("PRICE", Types.INTEGER, true);
        table.addColumn("CURRENCY_CODE", Types.VARCHAR, 5, true);
        table.addColumn("NAME", Types.VARCHAR, 128, true);
        table.addColumn("DESCRIPTION", Types.VARCHAR, 128, false);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.setPrimaryKey("ORDER_ITEMS_PK", new String[] {"ID"});
        table.addIndex("ORDER_ITEMS_ORDER_IDX", new String[] {"ORDER_ID"});
        table.addIndex("ORDER_ITEMS_CONTENT_IDX", new String[] {"CONTENT_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a item from the ORDER_ITEMS table by id.
     */
    public synchronized OrderItem getById(String id) throws SQLException
    {
        OrderItem ret = null;

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
                OrderItem item = new OrderItem();
                item.setId(rs.getString(1));
                item.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                item.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                item.setOrderId(rs.getString(4));
                item.setProductCode(rs.getString(5));
                item.setSiteId(rs.getString(6));
                item.setContentId(rs.getString(7));
                item.setContentType(rs.getString(8));
                item.setQuantity(rs.getInt(9));
                item.setPrice(rs.getInt(10));
                item.setCurrency(rs.getString(11));
                item.setName(rs.getString(12));
                item.setDescription(rs.getString(13));
                item.setEnabled(rs.getBoolean(14));
                ret = item;
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
     * Stores the given item in the ORDER_ITEMS table.
     */
    public synchronized void add(OrderItem item) throws SQLException
    {
        if(!hasConnection() || item == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, item.getId());
            insertStmt.setTimestamp(2, new Timestamp(item.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(item.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, item.getOrderId());
            insertStmt.setString(5, item.getProductCode());
            insertStmt.setString(6, item.getSiteId());
            insertStmt.setString(7, item.getContentId());
            insertStmt.setString(8, item.getContentType() != null ? item.getContentType().name() : null);
            insertStmt.setInt(9, item.getQuantity());
            insertStmt.setInt(10, item.getPrice());
            insertStmt.setString(11, item.getCurrency().code());
            insertStmt.setString(12, item.getName());
            insertStmt.setString(13, item.getDescription());
            insertStmt.setBoolean(14, item.isEnabled());
            insertStmt.executeUpdate();

            logger.info("Created order item '"+item.getId()+"' in ORDER_ITEMS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the order item already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given item in the ORDER_ITEMS table.
     */
    public synchronized void update(OrderItem item) throws SQLException
    {
        if(!hasConnection() || item == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(item.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, item.getProductCode());
        updateStmt.setString(3, item.getSiteId());
        updateStmt.setString(4, item.getContentId());
        updateStmt.setString(5, item.getContentType() != null ? item.getContentType().name() : null);
        updateStmt.setInt(6, item.getQuantity());
        updateStmt.setInt(7, item.getPrice());
        updateStmt.setString(8, item.getCurrency().code());
        updateStmt.setString(9, item.getName());
        updateStmt.setString(10, item.getDescription());
        updateStmt.setBoolean(11, item.isEnabled());
        updateStmt.setString(12, item.getId());
        updateStmt.executeUpdate();

        logger.info("Updated order item '"+item.getId()+"' in ORDER_ITEMS");
    }

    /**
     * Adds or Updates the given item in the ORDER_ITEMS table.
     */
    public boolean upsert(OrderItem item) throws SQLException
    {
        boolean ret = false;

        OrderItem existing = getById(item.getId());
        if(existing != null)
        {
            update(item);
        }
        else
        {
            add(item);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the items from the ORDER_ITEMS table.
     */
    public synchronized List<OrderItem> list() throws SQLException
    {
        List<OrderItem> ret = null;

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
            ret = new ArrayList<OrderItem>();
            while(rs.next())
            {
                OrderItem item = new OrderItem();
                item.setId(rs.getString(1));
                item.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                item.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                item.setOrderId(rs.getString(4));
                item.setProductCode(rs.getString(5));
                item.setSiteId(rs.getString(6));
                item.setContentId(rs.getString(7));
                item.setContentType(rs.getString(8));
                item.setQuantity(rs.getInt(9));
                item.setPrice(rs.getInt(10));
                item.setCurrency(rs.getString(11));
                item.setName(rs.getString(12));
                item.setDescription(rs.getString(13));
                item.setEnabled(rs.getBoolean(14));
                ret.add(item);
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
     * Returns the items from the ORDER_ITEMS table by order.
     */
    public synchronized List<OrderItem> list(String orderId) throws SQLException
    {
        List<OrderItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByOrderStmt == null)
            listByOrderStmt = prepareStatement(getConnection(), LIST_BY_ORDER_SQL);
        clearParameters(listByOrderStmt);

        ResultSet rs = null;

        try
        {
            listByOrderStmt.setString(1, orderId);
            listByOrderStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByOrderStmt.executeQuery();
            ret = new ArrayList<OrderItem>();
            while(rs.next())
            {
                OrderItem item = new OrderItem();
                item.setId(rs.getString(1));
                item.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                item.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                item.setOrderId(rs.getString(4));
                item.setProductCode(rs.getString(5));
                item.setSiteId(rs.getString(6));
                item.setContentId(rs.getString(7));
                item.setContentType(rs.getString(8));
                item.setQuantity(rs.getInt(9));
                item.setPrice(rs.getInt(10));
                item.setCurrency(rs.getString(11));
                item.setName(rs.getString(12));
                item.setDescription(rs.getString(13));
                item.setEnabled(rs.getBoolean(14));
                ret.add(item);
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
     * Returns the items from the ORDER_ITEMS table by content.
     */
    public synchronized List<OrderItem> list(Content content) throws SQLException
    {
        List<OrderItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContentStmt == null)
            listByContentStmt = prepareStatement(getConnection(), LIST_BY_CONTENT_SQL);
        clearParameters(listByContentStmt);

        ResultSet rs = null;

        try
        {
            listByContentStmt.setString(1, content.getUuid());
            listByContentStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContentStmt.executeQuery();
            ret = new ArrayList<OrderItem>();
            while(rs.next())
            {
                OrderItem item = new OrderItem();
                item.setId(rs.getString(1));
                item.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                item.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                item.setOrderId(rs.getString(4));
                item.setProductCode(rs.getString(5));
                item.setSiteId(rs.getString(6));
                item.setContentId(rs.getString(7));
                item.setContentType(rs.getString(8));
                item.setQuantity(rs.getInt(9));
                item.setPrice(rs.getInt(10));
                item.setCurrency(rs.getString(11));
                item.setName(rs.getString(12));
                item.setDescription(rs.getString(13));
                item.setEnabled(rs.getBoolean(14));
                ret.add(item);
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
     * Returns the count of items from the ORDER_ITEMS table.
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
     * Removes the given item from the ORDER_ITEMS table.
     */
    public synchronized void delete(OrderItem item) throws SQLException
    {
        if(!hasConnection() || item == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, item.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted order item '"+item.getId()+"' in ORDER_ITEMS");
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
        closeStatement(listByOrderStmt);
        listByOrderStmt = null;
        closeStatement(listByContentStmt);
        listByContentStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByOrderStmt;
    private PreparedStatement listByContentStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
