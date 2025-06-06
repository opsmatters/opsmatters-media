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
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.monitor.ContentMonitor;
import com.opsmatters.media.model.monitor.ContentMonitorItem;
import com.opsmatters.media.model.monitor.MonitorStatus;
import com.opsmatters.media.model.monitor.ContentMonitorFactory;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the CONTENT_MONITORS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentMonitorDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentMonitorDAO.class.getName());

    /**
     * The query to use to select a monitor from the CONTENT_MONITORS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ALERTS, ATTRIBUTES, STATUS, EVENT_TYPE, EVENT_ID "
      + "FROM CONTENT_MONITORS WHERE ID=?";

    /**
     * The query to use to insert a monitor into the CONTENT_MONITORS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_MONITORS"
      + "( ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ALERTS, ATTRIBUTES, STATUS, EVENT_TYPE, EVENT_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a monitor in the CONTENT_MONITORS table.
     */
    private static final String UPDATE_SQL =
      "UPDATE CONTENT_MONITORS SET UPDATED_DATE=?, EXECUTED_DATE=?, NAME=?, SNAPSHOT=?, ALERTS=?, ATTRIBUTES=?, STATUS=?, EVENT_TYPE=?, EVENT_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the monitors from the CONTENT_MONITORS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ALERTS, ATTRIBUTES, STATUS, EVENT_TYPE, EVENT_ID "
      + "FROM CONTENT_MONITORS ORDER BY EXECUTED_DATE";

    /**
     * The query to use to select the monitor items from the table.
     */
    private static final String LIST_ITEMS_SQL =
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, ALERTS, STATUS "
      + "FROM CONTENT_MONITORS ORDER BY EXECUTED_DATE";

    /**
     * The query to use to select the monitors from the CONTENT_MONITORS table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, SNAPSHOT, ALERTS, ATTRIBUTES, STATUS, EVENT_TYPE, EVENT_ID "
      + "FROM CONTENT_MONITORS WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the monitor items from the CONTENT_MONITORS table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, CODE, NAME, CONTENT_TYPE, ALERTS, STATUS "
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
        table.addColumn("NAME", Types.VARCHAR, 40, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("SNAPSHOT", Types.LONGVARCHAR, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("EVENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("EVENT_ID", Types.VARCHAR, 36, false);
        table.addColumn("ALERTS", Types.BOOLEAN, true);
        table.setPrimaryKey("CONTENT_MONITORS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_MONITORS_CODE_IDX", new String[] {"CODE"});
        table.addIndex("CONTENT_MONITORS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a monitor from the CONTENT_MONITORS table by id.
     */
    public synchronized ContentMonitor getById(String id) throws SQLException
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
                ContentType type = ContentType.valueOf(rs.getString(7));
                ContentMonitor monitor = ContentMonitorFactory.newInstance(type);
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setSnapshot(getClob(rs, 8));
                monitor.setAlerts(rs.getBoolean(9));
                monitor.setAttributes(new JSONObject(getClob(rs, 10)));
                monitor.setStatus(rs.getString(11));
                monitor.setEventType(rs.getString(12));
                monitor.setEventId(rs.getString(13));
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
    public synchronized void add(ContentMonitor monitor) throws SQLException
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
            insertStmt.setBoolean(9, monitor.hasAlerts());
            String attributes = monitor.getAttributes().toString();
            reader2 = new StringReader(attributes);
            insertStmt.setCharacterStream(10, reader2, attributes.length());
            insertStmt.setString(11, monitor.getStatus().name());
            insertStmt.setString(12, monitor.getEventType() != null ? monitor.getEventType().name() : "");
            insertStmt.setString(13, monitor.getEventId());
            insertStmt.executeUpdate();

            logger.info(String.format("Created monitor %s/%s in CONTENT_MONITORS",
                monitor.getCode(), monitor.getName()));
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
    public synchronized void update(ContentMonitor monitor, boolean log) throws SQLException
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
            updateStmt.setBoolean(5, monitor.hasAlerts());
            String attributes = monitor.getAttributes().toString();
            reader2 = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader2, attributes.length());
            updateStmt.setString(7, monitor.getStatus().name());
            updateStmt.setString(8, monitor.getEventType() != null ? monitor.getEventType().name() : "");
            updateStmt.setString(9, monitor.getEventId());
            updateStmt.setString(10, monitor.getId());
            updateStmt.executeUpdate();

            if(log)
              logger.info(String.format("Updated monitor %s/%s in CONTENT_MONITORS",
                  monitor.getCode(), monitor.getName()));
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
        update(monitor, true);
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
    public synchronized List<ContentMonitor> list() throws SQLException
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
                ContentType type = ContentType.valueOf(rs.getString(7));
                ContentMonitor monitor = ContentMonitorFactory.newInstance(type);
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setSnapshot(getClob(rs, 8));
                monitor.setAlerts(rs.getBoolean(9));
                monitor.setAttributes(new JSONObject(getClob(rs, 10)));
                monitor.setStatus(rs.getString(11));
                monitor.setEventType(rs.getString(12));
                monitor.setEventId(rs.getString(13));
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
     * Returns the monitors from the CONTENT_MONITORS table.
     */
    public synchronized List<ContentMonitorItem> listItems() throws SQLException
    {
        List<ContentMonitorItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsStmt == null)
            listItemsStmt = prepareStatement(getConnection(), LIST_ITEMS_SQL);
        clearParameters(listItemsStmt);

        ResultSet rs = null;

        try
        {
            listItemsStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsStmt.executeQuery();
            ret = new ArrayList<ContentMonitorItem>();
            while(rs.next())
            {
                ContentType type = ContentType.valueOf(rs.getString(7));
                ContentMonitorItem monitor = new ContentMonitorItem(ContentMonitorFactory.newInstance(type));
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setAlerts(rs.getBoolean(8));
                monitor.setStatus(rs.getString(9));
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
    public synchronized List<ContentMonitor> list(String code) throws SQLException
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
                ContentType type = ContentType.valueOf(rs.getString(7));
                ContentMonitor monitor = ContentMonitorFactory.newInstance(type);
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setSnapshot(getClob(rs, 8));
                monitor.setAlerts(rs.getBoolean(9));
                monitor.setAttributes(new JSONObject(getClob(rs, 10)));
                monitor.setStatus(rs.getString(11));
                monitor.setEventType(rs.getString(12));
                monitor.setEventId(rs.getString(13));
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
     * Returns the monitor items from the CONTENT_MONITORS table by organisation code.
     */
    public synchronized List<ContentMonitorItem> listItems(String code) throws SQLException
    {
        List<ContentMonitorItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByCodeStmt == null)
            listItemsByCodeStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_CODE_SQL);
        clearParameters(listItemsByCodeStmt);

        ResultSet rs = null;

        try
        {
            listItemsByCodeStmt.setString(1, code);
            listItemsByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByCodeStmt.executeQuery();
            ret = new ArrayList<ContentMonitorItem>();
            while(rs.next())
            {
                ContentType type = ContentType.valueOf(rs.getString(7));
                ContentMonitorItem monitor = new ContentMonitorItem(ContentMonitorFactory.newInstance(type));
                monitor.setId(rs.getString(1));
                monitor.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                monitor.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                monitor.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                monitor.setCode(rs.getString(5));
                monitor.setName(rs.getString(6));
                monitor.setContentType(rs.getString(7));
                monitor.setAlerts(rs.getBoolean(8));
                monitor.setStatus(rs.getString(9));
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
    public synchronized List<ContentMonitor> list(String code, String name, ContentType type) throws SQLException
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
     * Returns the count of monitors from the CONTENT_MONITORS table with a status of CHANGE.
     */
    public int getChangeCount(String code, String name, ContentType type) throws SQLException
    {
        int ret = 0;
        for(ContentMonitor monitor : list(code, name, type))
        {
            if(monitor.getStatus() == MonitorStatus.CHANGE)
                ++ret;
        }

        return ret;
    }

    /**
     * Returns the count of monitors from the CONTENT_MONITORS table with a status of ALERT.
     */
    public int getAlertCount(String code, String name, ContentType type) throws SQLException
    {
        int ret = 0;
        for(ContentMonitor monitor : list(code, name, type))
        {
            if(monitor.getStatus() == MonitorStatus.ALERT)
                ++ret;
        }

        return ret;
    }

    /**
     * Returns the count of monitors from the CONTENT_MONITORS table with a status of FAILURE.
     */
    public List<ContentMonitor> getFailures(String code, String name, ContentType type) throws SQLException
    {
        List<ContentMonitor> ret = new ArrayList<ContentMonitor>();
        for(ContentMonitor monitor : list(code, name, type))
        {
            if(monitor.getStatus() == MonitorStatus.FAILURE)
                ret.add(monitor);
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
    public synchronized void delete(ContentMonitor monitor) throws SQLException
    {
        if(!hasConnection() || monitor == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, monitor.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted monitor %s/%s in CONTENT_MONITORS",
            monitor.getCode(), monitor.getName()));
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
        closeStatement(listItemsStmt);
        listItemsStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listItemsByCodeStmt);
        listItemsByCodeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listItemsStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listItemsByCodeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
