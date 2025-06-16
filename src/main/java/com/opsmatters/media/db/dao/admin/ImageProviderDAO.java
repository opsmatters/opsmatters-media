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
package com.opsmatters.media.db.dao.admin;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.ImageProvider;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the IMAGE_PROVIDERS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageProviderDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ImageProviderDAO.class.getName());

    /**
     * The query to use to select a provider from the IMAGE_PROVIDERS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TAG, TYPE, ATTRIBUTION, STATUS, CREATED_BY "
      + "FROM IMAGE_PROVIDERS WHERE ID=?";

    /**
     * The query to use to insert a provider into the IMAGE_PROVIDERS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO IMAGE_PROVIDERS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TAG, TYPE, ATTRIBUTION, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a provider in the IMAGE_PROVIDERS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE IMAGE_PROVIDERS SET UPDATED_DATE=?, CODE=?, NAME=?, TAG=?, TYPE=?, ATTRIBUTION=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the providers from the IMAGE_PROVIDERS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TAG, TYPE, ATTRIBUTION, STATUS, CREATED_BY "
      + "FROM IMAGE_PROVIDERS";

    /**
     * The query to use to get the count of providers from the IMAGE_PROVIDERS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM IMAGE_PROVIDERS";

    /**
     * The query to use to delete a provider from the IMAGE_PROVIDERS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM IMAGE_PROVIDERS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ImageProviderDAO(AdminDAOFactory factory)
    {
        super(factory, "IMAGE_PROVIDERS");
    }

    /**
     * Defines the columns and indices for the IMAGE_PROVIDERS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 15, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("TAG", Types.VARCHAR, 15, true);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTION", Types.VARCHAR, 128, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("IMAGE_PROVIDERS_PK", new String[] {"ID"});
        table.addIndex("IMAGE_PROVIDERS_CODE_IDX", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns a provider from the IMAGE_PROVIDERS table by id.
     */
    public synchronized ImageProvider getById(String id) throws SQLException
    {
        ImageProvider ret = null;

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
                ImageProvider provider = new ImageProvider();
                provider.setId(rs.getString(1));
                provider.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                provider.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                provider.setCode(rs.getString(4));
                provider.setName(rs.getString(5));
                provider.setTag(rs.getString(6));
                provider.setType(rs.getString(7));
                provider.setAttribution(rs.getString(8));
                provider.setStatus(rs.getString(9));
                provider.setCreatedBy(rs.getString(10));
                ret = provider;
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
     * Stores the given provider in the IMAGE_PROVIDERS table.
     */
    public synchronized void add(ImageProvider provider) throws SQLException
    {
        if(!hasConnection() || provider == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, provider.getId());
            insertStmt.setTimestamp(2, new Timestamp(provider.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(provider.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, provider.getCode());
            insertStmt.setString(5, provider.getName());
            insertStmt.setString(6, provider.getTag());
            insertStmt.setString(7, provider.getType().name());
            insertStmt.setString(8, provider.getAttribution());
            insertStmt.setString(9, provider.getStatus().name());
            insertStmt.setString(10, provider.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info(String.format("Created provider %s in IMAGE_PROVIDERS", provider.getId()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the provider already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given provider in the IMAGE_PROVIDERS table.
     */
    public synchronized void update(ImageProvider provider) throws SQLException
    {
        if(!hasConnection() || provider == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(provider.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, provider.getCode());
        updateStmt.setString(3, provider.getName());
        updateStmt.setString(4, provider.getTag());
        updateStmt.setString(5, provider.getType().name());
        updateStmt.setString(6, provider.getAttribution());
        updateStmt.setString(7, provider.getStatus().name());
        updateStmt.setString(8, provider.getCreatedBy());
        updateStmt.setString(9, provider.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated provider %s in IMAGE_PROVIDERS", provider.getId()));
    }

    /**
     * Returns the providers from the IMAGE_PROVIDERS table.
     */
    public synchronized List<ImageProvider> list() throws SQLException
    {
        List<ImageProvider> ret = null;

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
            ret = new ArrayList<ImageProvider>();
            while(rs.next())
            {
                ImageProvider provider = new ImageProvider();
                provider.setId(rs.getString(1));
                provider.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                provider.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                provider.setCode(rs.getString(4));
                provider.setName(rs.getString(5));
                provider.setTag(rs.getString(6));
                provider.setType(rs.getString(7));
                provider.setAttribution(rs.getString(8));
                provider.setStatus(rs.getString(9));
                provider.setCreatedBy(rs.getString(10));
                ret.add(provider);
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
     * Returns the count of providers from the table.
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
     * Removes the given provider from the IMAGE_PROVIDERS table.
     */
    public synchronized void delete(ImageProvider provider) throws SQLException
    {
        if(!hasConnection() || provider == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, provider.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted provider %s in IMAGE_PROVIDERS", provider.getId()));
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
