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
import com.opsmatters.media.model.order.product.ProductText;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the PRODUCT_TEXTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProductTextDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ProductTextDAO.class.getName());

    /**
     * The query to use to select a person from the PRODUCT_TEXTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, PRODUCT_ID, CODE, VALUE, VARIATION "
      + "FROM PRODUCT_TEXTS WHERE ID=?";

    /**
     * The query to use to insert a person into the PRODUCT_TEXTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PRODUCT_TEXTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, PRODUCT_ID, CODE, VALUE, VARIATION )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a person in the PRODUCT_TEXTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PRODUCT_TEXTS SET UPDATED_DATE=?, CODE=?, VALUE=?, VARIATION=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the persons from the PRODUCT_TEXTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, PRODUCT_ID, CODE, VALUE, VARIATION "
      + "FROM PRODUCT_TEXTS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the persons from the PRODUCT_TEXTS table by product.
     */
    private static final String LIST_BY_PRODUCT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, PRODUCT_ID, CODE, VALUE, VARIATION "
      + "FROM PRODUCT_TEXTS WHERE PRODUCT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of persons from the PRODUCT_TEXTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM PRODUCT_TEXTS";

    /**
     * The query to use to delete a person from the PRODUCT_TEXTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM PRODUCT_TEXTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ProductTextDAO(OrderDAOFactory factory)
    {
        super(factory, "PRODUCT_TEXTS");
    }

    /**
     * Defines the columns and indices for the PRODUCT_TEXTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("PRODUCT_ID", Types.VARCHAR, 36, true);
        table.addColumn("CODE", Types.VARCHAR, 15, true);
        table.addColumn("VALUE", Types.VARCHAR, 128, false);
        table.addColumn("VARIATION", Types.VARCHAR, 128, false);
        table.setPrimaryKey("PRODUCT_TEXTS_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a text from the PRODUCT_TEXTS table by id.
     */
    public synchronized ProductText getById(String id) throws SQLException
    {
        ProductText ret = null;

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
                ProductText text = new ProductText();
                text.setId(rs.getString(1));
                text.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                text.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                text.setProductId(rs.getString(4));
                text.setCode(rs.getString(5));
                text.setValue(rs.getString(6));
                text.setVariation(rs.getString(7));
                ret = text;
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
     * Stores the given text in the PRODUCT_TEXTS table.
     */
    public synchronized void add(ProductText text) throws SQLException
    {
        if(!hasConnection() || text == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, text.getId());
            insertStmt.setTimestamp(2, new Timestamp(text.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(text.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, text.getProductId());
            insertStmt.setString(5, text.getCode());
            insertStmt.setString(6, text.getValue());
            insertStmt.setString(7, text.getVariation());
            insertStmt.executeUpdate();

            logger.info("Created product text '"+text.getId()+"' in PRODUCT_TEXTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the product text exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given text in the PRODUCT_TEXTS table.
     */
    public synchronized void update(ProductText text) throws SQLException
    {
        if(!hasConnection() || text == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(text.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, text.getCode());
        updateStmt.setString(3, text.getValue());
        updateStmt.setString(4, text.getVariation());
        updateStmt.setString(5, text.getId());
        updateStmt.executeUpdate();

        logger.info("Updated product text '"+text.getId()+"' in PRODUCT_TEXTS");
    }

    /**
     * Adds or Updates the given text in the PRODUCT_TEXTS table.
     */
    public boolean upsert(ProductText text) throws SQLException
    {
        boolean ret = false;

        ProductText existing = getById(text.getId());
        if(existing != null)
        {
            update(text);
        }
        else
        {
            add(text);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the texts from the PRODUCT_TEXTS table.
     */
    public synchronized List<ProductText> list() throws SQLException
    {
        List<ProductText> ret = null;

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
            ret = new ArrayList<ProductText>();
            while(rs.next())
            {
                ProductText text = new ProductText();
                text.setId(rs.getString(1));
                text.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                text.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                text.setProductId(rs.getString(4));
                text.setCode(rs.getString(5));
                text.setValue(rs.getString(6));
                text.setVariation(rs.getString(7));
                ret.add(text);
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
     * Returns the texts from the PRODUCT_TEXTS table by product.
     */
    public synchronized List<ProductText> list(String productId) throws SQLException
    {
        List<ProductText> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByProductStmt == null)
            listByProductStmt = prepareStatement(getConnection(), LIST_BY_PRODUCT_SQL);
        clearParameters(listByProductStmt);

        ResultSet rs = null;

        try
        {
            listByProductStmt.setString(1, productId);
            listByProductStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByProductStmt.executeQuery();
            ret = new ArrayList<ProductText>();
            while(rs.next())
            {
                ProductText text = new ProductText();
                text.setId(rs.getString(1));
                text.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                text.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                text.setProductId(rs.getString(4));
                text.setCode(rs.getString(5));
                text.setValue(rs.getString(6));
                text.setVariation(rs.getString(7));
                ret.add(text);
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
     * Returns the count of texts from the PRODUCT_TEXTS table.
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
     * Removes the given text from the PRODUCT_TEXTS table.
     */
    public synchronized void delete(ProductText text) throws SQLException
    {
        if(!hasConnection() || text == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, text.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted product text '"+text.getId()+"' in PRODUCT_TEXTS");
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
        closeStatement(listByProductStmt);
        listByProductStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByProductStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
