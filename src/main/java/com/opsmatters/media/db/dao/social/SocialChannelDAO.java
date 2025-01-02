/*
 * Copyright 2023 Gerald Curley
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
package com.opsmatters.media.db.dao.social;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the SOCIAL_CHANNELS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialChannelDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(SocialChannelDAO.class.getName());

    /**
     * The query to use to select a channel from the SOCIAL_CHANNELS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, PROVIDER, HANDLE, ICON, SITES, CONTENT_TYPES, DELAY, MAX_POSTS, STATUS, CREATED_BY "
      + "FROM SOCIAL_CHANNELS WHERE ID=?";

    /**
     * The query to use to select a channel from the SOCIAL_CHANNELS table by code.
     */
    private static final String GET_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, PROVIDER, HANDLE, ICON, SITES, CONTENT_TYPES, DELAY, MAX_POSTS, STATUS, CREATED_BY "
      + "FROM SOCIAL_CHANNELS WHERE CODE=?";

    /**
     * The query to use to insert a channel into the SOCIAL_CHANNELS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SOCIAL_CHANNELS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, PROVIDER, HANDLE, ICON, SITES, CONTENT_TYPES, DELAY, MAX_POSTS, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a channel in the SOCIAL_CHANNELS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE SOCIAL_CHANNELS SET UPDATED_DATE=?, CODE=?, NAME=?, PROVIDER=?, HANDLE=?, ICON=?, SITES=?, CONTENT_TYPES=?, DELAY=?, MAX_POSTS=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the channels from the SOCIAL_CHANNELS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, NAME, PROVIDER, HANDLE, ICON, SITES, CONTENT_TYPES, DELAY, MAX_POSTS, STATUS, CREATED_BY "
      + "FROM SOCIAL_CHANNELS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of channels from the SOCIAL_CHANNELS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SOCIAL_CHANNELS";

    /**
     * The query to use to delete a channel from the SOCIAL_CHANNELS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SOCIAL_CHANNELS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SocialChannelDAO(SocialDAOFactory factory)
    {
        super(factory, "SOCIAL_CHANNELS");
    }

    /**
     * Defines the columns and indices for the SOCIAL_CHANNELS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 15, true);
        table.addColumn("NAME", Types.VARCHAR, 25, true);
        table.addColumn("PROVIDER", Types.VARCHAR, 15, true);
        table.addColumn("HANDLE", Types.VARCHAR, 25, true);
        table.addColumn("ICON", Types.VARCHAR, 15, true);
        table.addColumn("SITES", Types.VARCHAR, 15, true);
        table.addColumn("CONTENT_TYPES", Types.VARCHAR, 50, false);
        table.addColumn("DELAY", Types.INTEGER, true);
        table.addColumn("MAX_POSTS", Types.INTEGER, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SOCIAL_CHANNELS_PK", new String[] {"ID"});
        table.addIndex("SOCIAL_CHANNELS_CODE_IDX", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns a channel from the SOCIAL_CHANNELS table by id.
     */
    public synchronized SocialChannel getById(String id) throws SQLException
    {
        SocialChannel ret = null;

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
                SocialChannel channel = new SocialChannel();
                channel.setId(rs.getString(1));
                channel.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                channel.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                channel.setCode(rs.getString(4));
                channel.setName(rs.getString(5));
                channel.setProvider(rs.getString(6));
                channel.setHandle(rs.getString(7));
                channel.setIcon(rs.getString(8));
                channel.setSites(rs.getString(9));
                channel.setContentTypes(rs.getString(10));
                channel.setDelay(rs.getInt(11));
                channel.setMaxPosts(rs.getInt(12));
                channel.setStatus(rs.getString(13));
                channel.setCreatedBy(rs.getString(14));
                ret = channel;
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
     * Returns a channel from the SOCIAL_CHANNELS table by code.
     */
    public synchronized SocialChannel getByCode(String code) throws SQLException
    {
        SocialChannel ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByCodeStmt == null)
            getByCodeStmt = prepareStatement(getConnection(), GET_BY_CODE_SQL);
        clearParameters(getByCodeStmt);

        ResultSet rs = null;

        try
        {
            getByCodeStmt.setString(1, code);
            getByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByCodeStmt.executeQuery();
            while(rs.next())
            {
                SocialChannel channel = new SocialChannel();
                channel.setId(rs.getString(1));
                channel.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                channel.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                channel.setCode(rs.getString(4));
                channel.setName(rs.getString(5));
                channel.setProvider(rs.getString(6));
                channel.setHandle(rs.getString(7));
                channel.setIcon(rs.getString(8));
                channel.setSites(rs.getString(9));
                channel.setContentTypes(rs.getString(10));
                channel.setDelay(rs.getInt(11));
                channel.setMaxPosts(rs.getInt(12));
                channel.setStatus(rs.getString(13));
                channel.setCreatedBy(rs.getString(14));
                ret = channel;
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
     * Stores the given channel in the SOCIAL_CHANNELS table.
     */
    public synchronized void add(SocialChannel channel) throws SQLException
    {
        if(!hasConnection() || channel == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, channel.getId());
            insertStmt.setTimestamp(2, new Timestamp(channel.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(channel.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, channel.getCode());
            insertStmt.setString(5, channel.getName());
            insertStmt.setString(6, channel.getProvider() != null ? channel.getProvider().code() : "");
            insertStmt.setString(7, channel.getHandle());
            insertStmt.setString(8, channel.getIcon());
            insertStmt.setString(9, channel.getSites());
            insertStmt.setString(10, channel.getContentTypes());
            insertStmt.setInt(11, channel.getDelay());
            insertStmt.setInt(12, channel.getMaxPosts());
            insertStmt.setString(13, channel.getStatus().name());
            insertStmt.setString(14, channel.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created channel '"+channel.getId()+"' in SOCIAL_CHANNELS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the channel already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given channel in the SOCIAL_CHANNELS table.
     */
    public synchronized void update(SocialChannel channel) throws SQLException
    {
        if(!hasConnection() || channel == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(channel.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, channel.getCode());
        updateStmt.setString(3, channel.getName());
        updateStmt.setString(4, channel.getProvider() != null ? channel.getProvider().code() : "");
        updateStmt.setString(5, channel.getHandle());
        updateStmt.setString(6, channel.getIcon());
        updateStmt.setString(7, channel.getSites());
        updateStmt.setString(8, channel.getContentTypes());
        updateStmt.setInt(9, channel.getDelay());
        updateStmt.setInt(10, channel.getMaxPosts());
        updateStmt.setString(11, channel.getStatus().name());
        updateStmt.setString(12, channel.getCreatedBy());
        updateStmt.setString(13, channel.getId());
        updateStmt.executeUpdate();

        logger.info("Updated channel '"+channel.getId()+"' in SOCIAL_CHANNELS");
    }

    /**
     * Returns the channels from the SOCIAL_CHANNELS table.
     */
    public synchronized List<SocialChannel> list() throws SQLException
    {
        List<SocialChannel> ret = null;

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
            ret = new ArrayList<SocialChannel>();
            while(rs.next())
            {
                SocialChannel channel = new SocialChannel();
                channel.setId(rs.getString(1));
                channel.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                channel.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                channel.setCode(rs.getString(4));
                channel.setName(rs.getString(5));
                channel.setProvider(rs.getString(6));
                channel.setHandle(rs.getString(7));
                channel.setIcon(rs.getString(8));
                channel.setSites(rs.getString(9));
                channel.setContentTypes(rs.getString(10));
                channel.setDelay(rs.getInt(11));
                channel.setMaxPosts(rs.getInt(12));
                channel.setStatus(rs.getString(13));
                channel.setCreatedBy(rs.getString(14));
                ret.add(channel);
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
     * Returns the count of channels from the SOCIAL_CHANNELS table.
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
     * Removes the given channel from the SOCIAL_CHANNELS table.
     */
    public synchronized void delete(SocialChannel channel) throws SQLException
    {
        if(!hasConnection() || channel == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, channel.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted channel '"+channel.getId()+"' in SOCIAL_CHANNELS");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByCodeStmt);
        getByCodeStmt = null;
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
    private PreparedStatement getByCodeStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
