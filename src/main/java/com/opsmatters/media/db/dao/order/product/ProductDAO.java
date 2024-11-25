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
package com.opsmatters.media.db.dao.order.product;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.product.Product;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the PRODUCTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProductDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ProductDAO.class.getName());

    /**
     * The query to use to select a product from the PRODUCTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, DESCRIPTION, STATUS, CREATED_BY "
      + "FROM PRODUCTS WHERE ID=?";

    /**
     * The query to use to insert a product into the PRODUCTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PRODUCTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, DESCRIPTION, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a product in the PRODUCTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PRODUCTS SET UPDATED_DATE=?, CODE=?, NAME=?, DESCRIPTION=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the products from the PRODUCTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, DESCRIPTION, STATUS, CREATED_BY "
      + "FROM PRODUCTS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of products from the PRODUCTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM PRODUCTS";

    /**
     * The query to use to delete a product from the PRODUCTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM PRODUCTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ProductDAO(OrderDAOFactory factory)
    {
        super(factory, "PRODUCTS");
    }

    /**
     * Defines the columns and indices for the PRODUCTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("DESCRIPTION", Types.VARCHAR, 128, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("PRODUCTS_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a product from the PRODUCTS table by id.
     */
    public synchronized Product getById(String id) throws SQLException
    {
        Product ret = null;

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
                Product product = new Product();
                product.setId(rs.getString(1));
                product.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                product.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                product.setCode(rs.getString(4));
                product.setName(rs.getString(5));
                product.setDescription(rs.getString(6));
                product.setStatus(rs.getString(7));
                product.setCreatedBy(rs.getString(8));
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
     * Stores the given product in the PRODUCTS table.
     */
    public synchronized void add(Product product) throws SQLException
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
            insertStmt.setString(4, product.getCode());
            insertStmt.setString(5, product.getName());
            insertStmt.setString(6, product.getDescription());
            insertStmt.setString(7, product.getStatus().name());
            insertStmt.setString(8, product.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created product '"+product.getId()+"' in PRODUCTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the product already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given product in the PRODUCTS table.
     */
    public synchronized void update(Product product) throws SQLException
    {
        if(!hasConnection() || product == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(product.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, product.getCode());
        updateStmt.setString(3, product.getName());
        updateStmt.setString(4, product.getDescription());
        updateStmt.setString(5, product.getStatus().name());
        updateStmt.setString(6, product.getId());
        updateStmt.executeUpdate();

        logger.info("Updated product '"+product.getId()+"' in PRODUCTS");
    }

    /**
     * Returns the products from the PRODUCTS table.
     */
    public synchronized List<Product> list() throws SQLException
    {
        List<Product> ret = null;

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
            ret = new ArrayList<Product>();
            while(rs.next())
            {
                Product product = new Product();
                product.setId(rs.getString(1));
                product.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                product.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                product.setCode(rs.getString(4));
                product.setName(rs.getString(5));
                product.setDescription(rs.getString(6));
                product.setStatus(rs.getString(7));
                product.setCreatedBy(rs.getString(8));
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
     * Returns the count of products from the PRODUCTS table.
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
     * Removes the given product from the PRODUCTS table.
     */
    public synchronized void delete(Product product) throws SQLException
    {
        if(!hasConnection() || product == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, product.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted product '"+product.getId()+"' in PRODUCTS");
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
