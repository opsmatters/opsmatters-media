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
import com.opsmatters.media.model.admin.ImagePlatform;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the IMAGE_PLATFORMS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImagePlatformDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ImagePlatformDAO.class.getName());

    /**
     * The query to use to select a platform from the IMAGE_PLATFORMS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TAG, FREE, STATUS, CREATED_BY "
      + "FROM IMAGE_PLATFORMS WHERE ID=?";

    /**
     * The query to use to insert a platform into the IMAGE_PLATFORMS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO IMAGE_PLATFORMS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TAG, FREE, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a platform in the IMAGE_PLATFORMS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE IMAGE_PLATFORMS SET UPDATED_DATE=?, CODE=?, NAME=?, TAG=?, FREE=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the platforms from the IMAGE_PLATFORMS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, TAG, FREE, STATUS, CREATED_BY "
      + "FROM IMAGE_PLATFORMS";

    /**
     * The query to use to get the count of platforms from the IMAGE_PLATFORMS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM IMAGE_PLATFORMS";

    /**
     * The query to use to delete a platform from the IMAGE_PLATFORMS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM IMAGE_PLATFORMS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ImagePlatformDAO(AdminDAOFactory factory)
    {
        super(factory, "IMAGE_PLATFORMS");
    }

    /**
     * Defines the columns and indices for the IMAGE_PLATFORMS table.
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
        table.addColumn("FREE", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("IMAGE_PLATFORMS_PK", new String[] {"ID"});
        table.addIndex("IMAGE_PLATFORMS_CODE_IDX", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns a platform from the IMAGE_PLATFORMS table by id.
     */
    public synchronized ImagePlatform getById(String id) throws SQLException
    {
        ImagePlatform ret = null;

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
                ImagePlatform platform = new ImagePlatform();
                platform.setId(rs.getString(1));
                platform.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                platform.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                platform.setCode(rs.getString(4));
                platform.setName(rs.getString(5));
                platform.setTag(rs.getString(6));
                platform.setFree(rs.getBoolean(7));
                platform.setStatus(rs.getString(8));
                platform.setCreatedBy(rs.getString(9));
                ret = platform;
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
     * Stores the given platform in the IMAGE_PLATFORMS table.
     */
    public synchronized void add(ImagePlatform platform) throws SQLException
    {
        if(!hasConnection() || platform == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, platform.getId());
            insertStmt.setTimestamp(2, new Timestamp(platform.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(platform.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, platform.getCode());
            insertStmt.setString(5, platform.getName());
            insertStmt.setString(6, platform.getTag());
            insertStmt.setBoolean(7, platform.isFree());
            insertStmt.setString(8, platform.getStatus().name());
            insertStmt.setString(9, platform.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info(String.format("Created platform %s in IMAGE_PLATFORMS", platform.getId()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the platform already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given platform in the IMAGE_PLATFORMS table.
     */
    public synchronized void update(ImagePlatform platform) throws SQLException
    {
        if(!hasConnection() || platform == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(platform.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, platform.getCode());
        updateStmt.setString(3, platform.getName());
        updateStmt.setString(4, platform.getTag());
        updateStmt.setBoolean(5, platform.isFree());
        updateStmt.setString(6, platform.getStatus().name());
        updateStmt.setString(7, platform.getCreatedBy());
        updateStmt.setString(8, platform.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated platform %s in IMAGE_PLATFORMS", platform.getId()));
    }

    /**
     * Returns the platforms from the IMAGE_PLATFORMS table.
     */
    public synchronized List<ImagePlatform> list() throws SQLException
    {
        List<ImagePlatform> ret = null;

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
            ret = new ArrayList<ImagePlatform>();
            while(rs.next())
            {
                ImagePlatform platform = new ImagePlatform();
                platform.setId(rs.getString(1));
                platform.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                platform.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                platform.setCode(rs.getString(4));
                platform.setName(rs.getString(5));
                platform.setTag(rs.getString(6));
                platform.setFree(rs.getBoolean(7));
                platform.setStatus(rs.getString(8));
                platform.setCreatedBy(rs.getString(9));
                ret.add(platform);
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
     * Returns the count of platforms from the table.
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
     * Removes the given platform from the IMAGE_PLATFORMS table.
     */
    public synchronized void delete(ImagePlatform platform) throws SQLException
    {
        if(!hasConnection() || platform == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, platform.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted platform %s in IMAGE_PLATFORMS", platform.getId()));
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
