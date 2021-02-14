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
import com.opsmatters.media.model.monitor.ContentMonitor;
import com.opsmatters.media.model.monitor.MonitorStatus;
import com.opsmatters.media.model.content.ContentType;

/**
 * DAO that provides operations on the CONTENT_MONITORS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitorDAO extends MonitorDAO<ContentMonitor>
{
    private static final Logger logger = Logger.getLogger(ContentMonitorDAO.class.getName());

    /**
     * The query to use to select a monitor from the CONTENT_MONITORS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ATTRIBUTES, STATUS, CHANGE_ID, ACTIVE "
      + "FROM CONTENT_MONITORS WHERE ID=?";

    /**
     * The query to use to insert a monitor into the CONTENT_MONITORS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_MONITORS"
      + "( ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ATTRIBUTES, STATUS, CHANGE_ID, ACTIVE )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a monitor in the CONTENT_MONITORS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_MONITORS SET UPDATED_DATE=?, EXECUTED_DATE=?, NAME=?, SNAPSHOT=?, ATTRIBUTES=?, STATUS=?, CHANGE_ID=?, ACTIVE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the monitors from the CONTENT_MONITORS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ATTRIBUTES, STATUS, CHANGE_ID, ACTIVE "
      + "FROM CONTENT_MONITORS ORDER BY EXECUTED_DATE";

    /**
     * The query to use to select the monitors from the CONTENT_MONITORS table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ATTRIBUTES, STATUS, CHANGE_ID, ACTIVE "
      + "FROM CONTENT_MONITORS WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of monitors from the CONTENT_MONITORS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_MONITORS";

    /**
     * The query to use to delete a monitor from the CONTENT_MONITORS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_MONITORS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentMonitorDAO(MonitorDAOFactory factory)
    {
        super(factory, "CONTENT_MONITORS");
    }

    /**
     * Defines the columns and indices for the CONTENT_MONITORS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("EXECUTED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("NAME", Types.VARCHAR, 25, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("SNAPSHOT", Types.LONGVARCHAR, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CHANGE_ID", Types.VARCHAR, 36, false);
        table.addColumn("ACTIVE", Types.BOOLEAN, true);
        table.setPrimaryKey("CONTENT_MONITORS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_MONITORS_CODE_IDX", new String[] {"CODE"});
        table.addIndex("CONTENT_MONITORS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a monitor from the CONTENT_MONITORS table by id.
     */
    public ContentMonitor getById(String id) throws SQLException
    {
        ContentMonitor ret = null;

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
                ContentMonitor monitor = new ContentMonitor();
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setSnapshot(getClob(rs, 8));
                monitor.setAttributes(new JSONObject(getClob(rs, 9)));
                monitor.setStatus(rs.getString(10));
                monitor.setChangeId(rs.getString(11));
                monitor.setActive(rs.getBoolean(12));
                ret = monitor;
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
     * Stores the given monitor in the CONTENT_MONITORS table.
     */
    public void add(ContentMonitor monitor) throws SQLException
    {
        if(!hasConnection() || monitor == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null, reader2 = null;

        try
        {
            insertStmt.setString(1, monitor.getId());
            insertStmt.setTimestamp(2, new Timestamp(monitor.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(monitor.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(monitor.getExecutedDateMillis()), UTC);
            insertStmt.setString(5, monitor.getCode());
            insertStmt.setString(6, monitor.getName());
            insertStmt.setString(7, monitor.getContentType().name());
            String snapshot = monitor.getSnapshot();
            reader = new StringReader(snapshot);
            insertStmt.setCharacterStream(8, reader, snapshot.length());
            String attributes = monitor.getAttributes().toString();
            reader2 = new StringReader(attributes);
            insertStmt.setCharacterStream(9, reader2, attributes.length());
            insertStmt.setString(10, monitor.getStatus().name());
            insertStmt.setString(11, monitor.getChangeId());
            insertStmt.setBoolean(12, monitor.isActive());
            insertStmt.executeUpdate();

            logger.info("Created monitor '"+monitor.getId()+"' in CONTENT_MONITORS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the monitor already exists
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
     * Updates the given monitor in the CONTENT_MONITORS table.
     */
    public void update(ContentMonitor monitor) throws SQLException
    {
        if(!hasConnection() || monitor == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null, reader2 = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(monitor.getUpdatedDateMillis()), UTC);
            updateStmt.setTimestamp(2, new Timestamp(monitor.getExecutedDateMillis()), UTC);
            updateStmt.setString(3, monitor.getName());
            String snapshot = monitor.getSnapshot();
            reader = new StringReader(snapshot);
            updateStmt.setCharacterStream(4, reader, snapshot.length());
            String attributes = monitor.getAttributes().toString();
            reader2 = new StringReader(attributes);
            updateStmt.setCharacterStream(5, reader2, attributes.length());
            updateStmt.setString(6, monitor.getStatus().name());
            updateStmt.setString(7, monitor.getChangeId());
            updateStmt.setBoolean(8, monitor.isActive());
            updateStmt.setString(9, monitor.getId());
            updateStmt.executeUpdate();

            logger.info("Updated monitor '"+monitor.getId()+"' in CONTENT_MONITORS");
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
     * Adds or Updates the given monitor in the CONTENT_MONITORS table.
     */
    public boolean upsert(ContentMonitor monitor) throws SQLException
    {
        boolean ret = false;

        ContentMonitor existing = getById(monitor.getId());
        if(existing != null)
        {
            update(monitor);
        }
        else
        {
            add(monitor);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the monitors from the CONTENT_MONITORS table.
     */
    public List<ContentMonitor> list() throws SQLException
    {
        List<ContentMonitor> ret = null;

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
            ret = new ArrayList<ContentMonitor>();
            while(rs.next())
            {
                ContentMonitor monitor = new ContentMonitor();
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setSnapshot(getClob(rs, 8));
                monitor.setAttributes(new JSONObject(getClob(rs, 9)));
                monitor.setStatus(rs.getString(10));
                monitor.setChangeId(rs.getString(11));
                monitor.setActive(rs.getBoolean(12));
                ret.add(monitor);
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
     * Returns the monitors from the CONTENT_MONITORS table by organisation code.
     */
    public List<ContentMonitor> list(String code) throws SQLException
    {
        List<ContentMonitor> ret = null;

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
            ret = new ArrayList<ContentMonitor>();
            while(rs.next())
            {
                ContentMonitor monitor = new ContentMonitor();
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setSnapshot(getClob(rs, 8));
                monitor.setAttributes(new JSONObject(getClob(rs, 9)));
                monitor.setStatus(rs.getString(10));
                monitor.setChangeId(rs.getString(11));
                monitor.setActive(rs.getBoolean(12));
                ret.add(monitor);
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
     * Returns the monitors from the CONTENT_MONITORS table by organisation code and content type.
     */
    public List<ContentMonitor> list(String code, String name, ContentType type) throws SQLException
    {
        List<ContentMonitor> ret = new ArrayList<ContentMonitor>();
        for(ContentMonitor monitor : list(code))
        {
            if(monitor.getContentType() == type
                && monitor.getName().equals(name))
            {
                ret.add(monitor);
            }
        }

        return ret;
    }

    /**
     * Returns the count of monitors from the CONTENT_MONITORS table by organisation code and content type.
     */
    public int getPendingCount(String code, String name, ContentType type) throws SQLException
    {
        int ret = 0;
        for(ContentMonitor monitor : list(code, name, type))
        {
            if(monitor.getStatus() == MonitorStatus.PENDING)
                ++ret;
        }

        return ret;
    }

    /**
     * Returns the count of monitors from the CONTENT_MONITORS table.
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
     * Removes the given monitor from the CONTENT_MONITORS table.
     */
    public void delete(ContentMonitor monitor) throws SQLException
    {
        if(!hasConnection() || monitor == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, monitor.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted monitor '"+monitor.getId()+"' in CONTENT_MONITORS");
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
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
