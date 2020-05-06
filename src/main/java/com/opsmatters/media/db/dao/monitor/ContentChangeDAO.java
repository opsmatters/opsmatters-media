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
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, CREATED_BY "
      + "FROM CONTENT_CHANGES WHERE ID=?";

    /**
     * The query to use to insert a change into the CONTENT_CHANGES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_CHANGES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a change in the CONTENT_CHANGES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_CHANGES SET UPDATED_DATE=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the changes from the CONTENT_CHANGES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SNAPSHOT_BEFORE, SNAPSHOT_AFTER, STATUS, MONITOR_ID, EXECUTION_TIME, DIFFERENCE, CREATED_BY "
      + "FROM CONTENT_CHANGES WHERE CREATED_DATE >= (NOW() + INTERVAL -7 DAY) ORDER BY CREATED_DATE";

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
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("MONITOR_ID", Types.VARCHAR, 36, true);
        table.addColumn("EXECUTION_TIME", Types.INTEGER, true);
        table.addColumn("DIFFERENCE", Types.INTEGER, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("CONTENT_CHANGES_PK", new String[] {"ID"});
        table.addIndex("CONTENT_CHANGES_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a change from the CONTENT_CHANGES table by id.
     */
    public ContentChange getById(String id) throws SQLException
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
                change.setStatus(rs.getString(7));
                change.setMonitorId(rs.getString(8));
                change.setExecutionTime(rs.getLong(9));
                change.setDifference(rs.getInt(10));
                change.setCreatedBy(rs.getString(11));
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
    public void add(ContentChange change) throws SQLException
    {
        if(!hasConnection() || change == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null, reader2 = null;

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
            insertStmt.setString(7, change.getStatus().name());
            insertStmt.setString(8, change.getMonitorId());
            insertStmt.setLong(9, change.getExecutionTime());
            insertStmt.setInt(10, change.getDifference());
            insertStmt.setString(11, change.getCreatedBy());
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
        }
    }

    /**
     * Updates the given change in the CONTENT_CHANGES table.
     */
    public void update(ContentChange change) throws SQLException
    {
        if(!hasConnection() || change == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(change.getUpdatedDateMillis()), UTC);
            updateStmt.setString(2, change.getStatus().name());
            updateStmt.setString(3, change.getCreatedBy());
            updateStmt.setString(4, change.getId());
            updateStmt.executeUpdate();

            logger.info("Updated change '"+change.getId()+"' in CONTENT_CHANGES");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the changes from the CONTENT_CHANGES table.
     */
    public List<ContentChange> list() throws SQLException
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
                change.setStatus(rs.getString(7));
                change.setMonitorId(rs.getString(8));
                change.setExecutionTime(rs.getLong(9));
                change.setDifference(rs.getInt(10));
                change.setCreatedBy(rs.getString(11));
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
    public void delete(ContentChange change) throws SQLException
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
