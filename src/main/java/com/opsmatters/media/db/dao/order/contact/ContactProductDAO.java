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
import com.opsmatters.media.model.order.contact.ContactProduct;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the CONTACT_PRODUCTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactProductDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContactProductDAO.class.getName());

    /**
     * The query to use to select a product from the CONTACT_PRODUCTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, PRODUCT_CODE, SITE_ID, PRICE, CURRENCY_CODE  "
      + "FROM CONTACT_PRODUCTS WHERE ID=?";

    /**
     * The query to use to insert a product into the CONTACT_PRODUCTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTACT_PRODUCTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, PRODUCT_CODE, SITE_ID, PRICE, CURRENCY_CODE )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a product in the CONTACT_PRODUCTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTACT_PRODUCTS SET UPDATED_DATE=?, PRODUCT_CODE=?, SITE_ID=?, PRICE=?, CURRENCY_CODE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the products from the CONTACT_PRODUCTS table by contact.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, PRODUCT_CODE, SITE_ID, PRICE, CURRENCY_CODE  "
      + "FROM CONTACT_PRODUCTS WHERE CONTACT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of products from the CONTACT_PRODUCTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTACT_PRODUCTS";

    /**
     * The query to use to delete a product from the CONTACT_PRODUCTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTACT_PRODUCTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContactProductDAO(OrderDAOFactory factory)
    {
        super(factory, "CONTACT_PRODUCTS");
    }

    /**
     * Defines the columns and indices for the CONTACT_PRODUCTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CONTACT_ID", Types.VARCHAR, 36, true);
        table.addColumn("PRODUCT_CODE", Types.VARCHAR, 5, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("PRICE", Types.INTEGER, true);
        table.addColumn("CURRENCY_CODE", Types.VARCHAR, 5, true);
        table.setPrimaryKey("CONTACT_PRODUCTS_PK", new String[] {"ID"});
        table.addIndex("CONTACT_PRODUCTS_CONTACT_IDX", new String[] {"CONTACT_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a product from the CONTACT_PRODUCTS table by id.
     */
    public synchronized ContactProduct getById(String id) throws SQLException
    {
        ContactProduct ret = null;

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
                ContactProduct product = new ContactProduct();
                product.setId(rs.getString(1));
                product.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                product.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                product.setContactId(rs.getString(4));
                product.setProductCode(rs.getString(5));
                product.setSiteId(rs.getString(6));
                product.setPrice(rs.getInt(7));
                product.setCurrency(rs.getString(8));
                ret = product;
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
     * Stores the given product in the CONTACT_PRODUCTS table.
     */
    public synchronized void add(ContactProduct product) throws SQLException
    {
        if(!hasConnection() || product == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, product.getId());
            insertStmt.setTimestamp(2, new Timestamp(product.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(product.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, product.getContactId());
            insertStmt.setString(5, product.getProductCode());
            insertStmt.setString(6, product.getSiteId());
            insertStmt.setInt(7, product.getPrice());
            insertStmt.setString(8, product.getCurrency().code());
            insertStmt.executeUpdate();

            logger.info("Created contact product '"+product.getId()+"' in CONTACT_PRODUCTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the contact product already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given product in the CONTACT_PRODUCTS table.
     */
    public synchronized void update(ContactProduct product) throws SQLException
    {
        if(!hasConnection() || product == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(product.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, product.getProductCode());
        updateStmt.setString(3, product.getSiteId());
        updateStmt.setInt(4, product.getPrice());
        updateStmt.setString(5, product.getCurrency().code());
        updateStmt.setString(6, product.getId());
        updateStmt.executeUpdate();

        logger.info("Updated contact product '"+product.getId()+"' in CONTACT_PRODUCTS");
    }

    /**
     * Adds or Updates the given product in the CONTACT_PRODUCTS table.
     */
    public boolean upsert(ContactProduct product) throws SQLException
    {
        boolean ret = false;

        ContactProduct existing = getById(product.getId());
        if(existing != null)
        {
            update(product);
        }
        else
        {
            add(product);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the products from the CONTACT_PRODUCTS table by contact.
     */
    public synchronized List<ContactProduct> list(String contactId) throws SQLException
    {
        List<ContactProduct> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setString(1, contactId);
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<ContactProduct>();
            while(rs.next())
            {
                ContactProduct product = new ContactProduct();
                product.setId(rs.getString(1));
                product.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                product.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                product.setContactId(rs.getString(4));
                product.setProductCode(rs.getString(5));
                product.setSiteId(rs.getString(6));
                product.setPrice(rs.getInt(7));
                product.setCurrency(rs.getString(8));
                ret.add(product);
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
     * Returns the count of products from the CONTACT_PRODUCTS table.
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
     * Removes the given product from the CONTACT_PRODUCTS table.
     */
    public synchronized void delete(ContactProduct product) throws SQLException
    {
        if(!hasConnection() || product == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, product.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted contact product '"+product.getId()+"' in CONTACT_PRODUCTS");
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
