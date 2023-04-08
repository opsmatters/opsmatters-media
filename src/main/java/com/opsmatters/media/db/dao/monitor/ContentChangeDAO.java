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
package com.opsmatters.media.db.dao.monitor;

import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.monitor.ContentChange;
import com.opsmatters.media.model.monitor.ChangeStatus;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the CONTENT_CHANGES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentChangeDAO extends MonitorDAO<ContentChange>
{
    private static final Logger logger = Logger.getLogger(ContentChangeDAO.class.getName());

    /**
     * The query to use to select a change from the CONTENT_CHANGES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, SNAPSHOT_DIFF, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, SITES, CREATED_BY "
      + "FROM CONTENT_CHANGES WHERE ID=?";

    /**
     * The query to use to insert a change into the CONTENT_CHANGES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_CHANGES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, SNAPSHOT_DIFF, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, SITES, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a change in the CONTENT_CHANGES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_CHANGES SET UPDATED_DATE=?, SNAPSHOT_AFTER=?, SNAPSHOT_DIFF=?, STATUS=?, EXECUTION_TIME=?, DIFFERENCE=?, SITES=?, CREATED_BY=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the changes from the CONTENT_CHANGES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, SNAPSHOT_DIFF, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, SITES, CREATED_BY "
      + "FROM CONTENT_CHANGES "
      + "WHERE CREATED_DATE >= (NOW() + INTERVAL -7 DAY) OR STATUS='NEW' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the changes from the CONTENT_CHANGES table by organisation.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, SNAPSHOT_DIFF, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, SITES, CREATED_BY "
      + "FROM CONTENT_CHANGES "
      + "WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the changes from the CONTENT_CHANGES table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, SNAPSHOT_DIFF, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, SITES, CREATED_BY "
      + "FROM CONTENT_CHANGES "
      + "WHERE STATUS=? AND (CREATED_DATE >= (NOW() + INTERVAL -7 DAY) OR STATUS='NEW') ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of changes from the CONTENT_CHANGES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_CHANGES";

    /**
     * The query to use to delete a change from the CONTENT_CHANGES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_CHANGES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentChangeDAO(MonitorDAOFactory factory)
    {
        super(factory, "CONTENT_CHANGES");
    }

    /**
     * Defines the columns and indices for the CONTENT_CHANGES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("SNAPSHOT_BEFORE", Types.LONGVARCHAR, true);
        table.addColumn("SNAPSHOT_AFTER", Types.LONGVARCHAR, true);
        table.addColumn("SNAPSHOT_DIFF", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("MONITOR_ID", Types.VARCHAR, 36, true);
        table.addColumn("EXECUTION_TIME", Types.INTEGER, true);
        table.addColumn("DIFFERENCE", Types.INTEGER, true);
        table.addColumn("SITES", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("CONTENT_CHANGES_PK", new String[] {"ID"});
        table.addIndex("CONTENT_CHANGES_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("CONTENT_CHANGES_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a change from the CONTENT_CHANGES table by id.
     */
    public synchronized ContentChange getById(String id) throws SQLException
    {
        ContentChange ret = null;

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
                ContentChange change = new ContentChange();
                change.setId(rs.getString(1));
                change.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                change.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                change.setCode(rs.getString(4));
                change.setSnapshotBefore(getClob(rs, 5));
                change.setSnapshotAfter(getClob(rs, 6));
                change.setSnapshotDiff(getClob(rs, 7));
                change.setStatus(rs.getString(8));
                change.setMonitorId(rs.getString(9));
                change.setExecutionTime(rs.getLong(10));
                change.setDifference(rs.getInt(11));
                change.setSites(rs.getString(12));
                change.setCreatedBy(rs.getString(13));
                ret = change;
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
     * Stores the given change in the CONTENT_CHANGES table.
     */
    public synchronized void add(ContentChange change) throws SQLException
    {
        if(!hasConnection() || change == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null, reader2 = null, reader3 = null;

        try
        {
            insertStmt.setString(1, change.getId());
            insertStmt.setTimestamp(2, new Timestamp(change.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(change.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, change.getCode());
            String snapshotBefore = change.getSnapshotBefore();
            reader = new StringReader(snapshotBefore);
            insertStmt.setCharacterStream(5, reader, snapshotBefore.length());
            String snapshotAfter = change.getSnapshotAfter();
            reader2 = new StringReader(snapshotAfter);
            insertStmt.setCharacterStream(6, reader2, snapshotAfter.length());
            String snapshotDiff = change.getSnapshotDiff();
            reader3 = new StringReader(snapshotDiff);
            insertStmt.setCharacterStream(7, reader3, snapshotDiff.length());

            insertStmt.setString(8, change.getStatus().name());
            insertStmt.setString(9, change.getMonitorId());
            insertStmt.setLong(10, change.getExecutionTime());
            insertStmt.setInt(11, change.getDifference());
            insertStmt.setString(12, change.getSites());
            insertStmt.setString(13, change.getCreatedBy());
            insertStmt.setInt(14, SessionId.get());
            insertStmt.executeUpdate();

            logger.info("Created change '"+change.getId()+"' in CONTENT_CHANGES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the change already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
        finally
        {
            if(reader != null)
                reader.close();
            if(reader2 != null)
                reader2.close();
            if(reader3 != null)
                reader3.close();
        }
    }

    /**
     * Updates the given change in the CONTENT_CHANGES table.
     */
    public synchronized void update(ContentChange change) throws SQLException
    {
        if(!hasConnection() || change == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null, reader2 = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(change.getUpdatedDateMillis()), UTC);
            String snapshotAfter = change.getSnapshotAfter();
            reader = new StringReader(snapshotAfter);
            updateStmt.setCharacterStream(2, reader, snapshotAfter.length());
            String snapshotDiff = change.getSnapshotDiff();
            reader2 = new StringReader(snapshotDiff);
            updateStmt.setCharacterStream(3, reader2, snapshotDiff.length());
            updateStmt.setString(4, change.getStatus().name());
            updateStmt.setLong(5, change.getExecutionTime());
            updateStmt.setInt(6, change.getDifference());
            updateStmt.setString(7, change.getSites());
            updateStmt.setString(8, change.getCreatedBy());
            updateStmt.setInt(9, SessionId.get());
            updateStmt.setString(10, change.getId());
            updateStmt.executeUpdate();

            logger.info("Updated change '"+change.getId()+"' in CONTENT_CHANGES");
        }
        finally
        {
            if(reader != null)
                reader.close();
            if(reader2 != null)
                reader2.close();
        }
    }

    /**
     * Returns the changes from the CONTENT_CHANGES table.
     */
    public synchronized List<ContentChange> list() throws SQLException
    {
        List<ContentChange> ret = null;

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
            ret = new ArrayList<ContentChange>();
            while(rs.next())
            {
                ContentChange change = new ContentChange();
                change.setId(rs.getString(1));
                change.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                change.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                change.setCode(rs.getString(4));
                change.setSnapshotBefore(getClob(rs, 5));
                change.setSnapshotAfter(getClob(rs, 6));
                change.setSnapshotDiff(getClob(rs, 7));
                change.setStatus(rs.getString(8));
                change.setMonitorId(rs.getString(9));
                change.setExecutionTime(rs.getLong(10));
                change.setDifference(rs.getInt(11));
                change.setSites(rs.getString(12));
                change.setCreatedBy(rs.getString(13));
                ret.add(change);
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
     * Returns the changes from the CONTENT_CHANGES table by organisation.
     */
    public synchronized List<ContentChange> list(String code) throws SQLException
    {
        List<ContentChange> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<ContentChange>();
            while(rs.next())
            {
                ContentChange change = new ContentChange();
                change.setId(rs.getString(1));
                change.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                change.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                change.setCode(rs.getString(4));
                change.setSnapshotBefore(getClob(rs, 5));
                change.setSnapshotAfter(getClob(rs, 6));
                change.setSnapshotDiff(getClob(rs, 7));
                change.setStatus(rs.getString(8));
                change.setMonitorId(rs.getString(9));
                change.setExecutionTime(rs.getLong(10));
                change.setDifference(rs.getInt(11));
                change.setSites(rs.getString(12));
                change.setCreatedBy(rs.getString(13));
                ret.add(change);
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
     * Returns the changes from the CONTENT_CHANGES table by status.
     */
    public synchronized List<ContentChange> list(ChangeStatus status) throws SQLException
    {
        List<ContentChange> ret = null;

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
            ret = new ArrayList<ContentChange>();
            while(rs.next())
            {
                ContentChange change = new ContentChange();
                change.setId(rs.getString(1));
                change.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                change.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                change.setCode(rs.getString(4));
                change.setSnapshotBefore(getClob(rs, 5));
                change.setSnapshotAfter(getClob(rs, 6));
                change.setSnapshotDiff(getClob(rs, 7));
                change.setStatus(rs.getString(8));
                change.setMonitorId(rs.getString(9));
                change.setExecutionTime(rs.getLong(10));
                change.setDifference(rs.getInt(11));
                change.setSites(rs.getString(12));
                change.setCreatedBy(rs.getString(13));
                ret.add(change);
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
     * Returns the count of changes from the table.
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
     * Removes the given change from the CONTENT_CHANGES table.
     */
    public synchronized void delete(ContentChange change) throws SQLException
    {
        if(!hasConnection() || change == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, change.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted change '"+change.getId()+"' in CONTENT_CHANGES");
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
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
