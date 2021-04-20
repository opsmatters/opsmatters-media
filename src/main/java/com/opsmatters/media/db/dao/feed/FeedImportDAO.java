/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.db.dao.feed;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.feed.FeedImport;

/**
 * DAO that provides operations on the FEED_IMPORTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FeedImportDAO extends FeedDAO<FeedImport>
{
    private static final Logger logger = Logger.getLogger(FeedImportDAO.class.getName());

    /**
     * The query to use to select an import from the FEED_IMPORTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, FEED_ID, EXECUTION_TIME, CREATED_COUNT, UPDATED_COUNT, FAILED_COUNT, ERROR_MESSAGE "
      + "FROM FEED_IMPORTS WHERE ID=?";

    /**
     * The query to use to insert an import into the FEED_IMPORTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO FEED_IMPORTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, FEED_ID, EXECUTION_TIME, CREATED_COUNT, UPDATED_COUNT, FAILED_COUNT, ERROR_MESSAGE )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an import in the FEED_IMPORTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE FEED_IMPORTS SET UPDATED_DATE=?, EXECUTION_TIME=?, CREATED_COUNT=?, UPDATED_COUNT=?, FAILED_COUNT=?, ERROR_MESSAGE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the imports from the FEED_IMPORTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, FEED_ID, EXECUTION_TIME, CREATED_COUNT, UPDATED_COUNT, FAILED_COUNT, ERROR_MESSAGE "
      + "FROM FEED_IMPORTS WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the imports from the FEED_IMPORTS table by feed.
     */
    private static final String LIST_BY_FEED_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, FEED_ID, EXECUTION_TIME, CREATED_COUNT, UPDATED_COUNT, FAILED_COUNT, ERROR_MESSAGE "
      + "FROM FEED_IMPORTS WHERE FEED_ID=? AND CREATED_DATE >= (NOW() + INTERVAL -30 DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of imports from the FEED_IMPORTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM FEED_IMPORTS";

    /**
     * The query to use to delete an import from the FEED_IMPORTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM FEED_IMPORTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public FeedImportDAO(FeedDAOFactory factory)
    {
        super(factory, "FEED_IMPORTS");
    }

    /**
     * Defines the columns and indices for the FEED_IMPORTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("FEED_ID", Types.VARCHAR, 36, true);
        table.addColumn("EXECUTION_TIME", Types.BIGINT, true);
        table.addColumn("CREATED_COUNT", Types.SMALLINT, true);
        table.addColumn("UPDATED_COUNT", Types.SMALLINT, true);
        table.addColumn("FAILED_COUNT", Types.SMALLINT, true);
        table.addColumn("ERROR_MESSAGE", Types.VARCHAR, 256, false);
        table.setPrimaryKey("FEED_IMPORTS_PK", new String[] {"ID"});
        table.addIndex("FEED_IMPORTS_CREATED_IDX", new String[] {"CREATED_DATE"});
        table.addIndex("FEED_IMPORTS_FEED_ID_IDX", new String[] {"FEED_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an import from the FEED_IMPORTS table by id.
     */
    public synchronized FeedImport getById(String id) throws SQLException
    {
        FeedImport ret = null;

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
                FeedImport imprt = new FeedImport();
                imprt.setId(rs.getString(1));
                imprt.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                imprt.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                imprt.setFeedId(rs.getString(4));
                imprt.setExecutionTime(rs.getLong(5));
                imprt.setCreatedCount(rs.getInt(6));
                imprt.setUpdatedCount(rs.getInt(7));
                imprt.setFailedCount(rs.getInt(8));
                imprt.setErrorMessage(rs.getString(9));
                ret = imprt;
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
     * Stores the given import in the FEED_IMPORTS table.
     */
    public synchronized void add(FeedImport imprt) throws SQLException
    {
        if(!hasConnection() || imprt == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, imprt.getId());
            insertStmt.setTimestamp(2, new Timestamp(imprt.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(imprt.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, imprt.getFeedId());
            insertStmt.setLong(5, imprt.getExecutionTime());
            insertStmt.setInt(6, imprt.getCreatedCount());
            insertStmt.setInt(7, imprt.getUpdatedCount());
            insertStmt.setInt(8, imprt.getFailedCount());
            insertStmt.setString(9, imprt.getErrorMessage());
            insertStmt.executeUpdate();

            logger.info("Created import '"+imprt.getId()+"' in FEED_IMPORTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the import already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given import in the FEED_IMPORTS table.
     */
    public synchronized void update(FeedImport imprt) throws SQLException
    {
        if(!hasConnection() || imprt == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(imprt.getUpdatedDateMillis()), UTC);
        updateStmt.setLong(2, imprt.getExecutionTime());
        updateStmt.setInt(3, imprt.getCreatedCount());
        updateStmt.setInt(4, imprt.getUpdatedCount());
        updateStmt.setInt(5, imprt.getFailedCount());
        updateStmt.setString(6, imprt.getErrorMessage());
        updateStmt.setString(7, imprt.getId());
        updateStmt.executeUpdate();

        logger.info("Updated import '"+imprt.getId()+"' in FEED_IMPORTS");
    }

    /**
     * Returns the imports from the FEED_IMPORTS table.
     */
    public synchronized List<FeedImport> list() throws SQLException
    {
        List<FeedImport> ret = null;

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
            ret = new ArrayList<FeedImport>();
            while(rs.next())
            {
                FeedImport imprt = new FeedImport();
                imprt.setId(rs.getString(1));
                imprt.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                imprt.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                imprt.setFeedId(rs.getString(4));
                imprt.setExecutionTime(rs.getLong(5));
                imprt.setCreatedCount(rs.getInt(6));
                imprt.setUpdatedCount(rs.getInt(7));
                imprt.setFailedCount(rs.getInt(8));
                imprt.setErrorMessage(rs.getString(9));
                ret.add(imprt);
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
     * Returns the imports from the FEED_IMPORTS table by feed.
     */
    public synchronized List<FeedImport> list(String feedId) throws SQLException
    {
        List<FeedImport> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByFeedStmt == null)
            listByFeedStmt = prepareStatement(getConnection(), LIST_BY_FEED_SQL);
        clearParameters(listByFeedStmt);

        ResultSet rs = null;

        try
        {
            listByFeedStmt.setString(1, feedId);
            listByFeedStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByFeedStmt.executeQuery();
            ret = new ArrayList<FeedImport>();
            while(rs.next())
            {
                FeedImport imprt = new FeedImport();
                imprt.setId(rs.getString(1));
                imprt.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                imprt.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                imprt.setFeedId(rs.getString(4));
                imprt.setExecutionTime(rs.getLong(5));
                imprt.setCreatedCount(rs.getInt(6));
                imprt.setUpdatedCount(rs.getInt(7));
                imprt.setFailedCount(rs.getInt(8));
                imprt.setErrorMessage(rs.getString(9));
                ret.add(imprt);
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
     * Returns the count of imports from the FEED_IMPORTS table.
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
     * Removes the given import from the FEED_IMPORTS table.
     */
    public synchronized void delete(FeedImport imprt) throws SQLException
    {
        if(!hasConnection() || imprt == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, imprt.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted import '"+imprt.getId()+"' in FEED_IMPORTS");
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
        closeStatement(listByFeedStmt);
        listByFeedStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByFeedStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
