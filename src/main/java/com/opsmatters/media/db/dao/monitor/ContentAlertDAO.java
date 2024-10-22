/*
 * Copyright 2021 Gerald Curley
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
import com.opsmatters.media.model.monitor.ContentAlert;
import com.opsmatters.media.model.monitor.ContentAlertItem;
import com.opsmatters.media.model.monitor.AlertStatus;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the CONTENT_ALERTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentAlertDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentAlertDAO.class.getName());

    /**
     * The query to use to select a alert from the CONTENT_ALERTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, START_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY "
      + "FROM CONTENT_ALERTS WHERE ID=?";

    /**
     * The query to use to insert a alert into the CONTENT_ALERTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_ALERTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, START_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a alert in the CONTENT_ALERTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_ALERTS SET UPDATED_DATE=?, ATTRIBUTES=?, STATUS=?, CREATED_BY=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the alerts from the CONTENT_ALERTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, START_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY "
      + "FROM CONTENT_ALERTS "
      + "WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the alert items from the CONTENT_ALERTS table.
     */
    private static final String LIST_ITEMS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, REASON, STATUS, MONITOR_ID "
      + "FROM CONTENT_ALERTS "
      + "WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the alerts from the CONTENT_ALERTS table by organisation.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, START_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY "
      + "FROM CONTENT_ALERTS "
      + "WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the alert items from the CONTENT_ALERTS table by status.
     */
    private static final String LIST_ITEMS_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, REASON, STATUS, MONITOR_ID "
      + "FROM CONTENT_ALERTS "
      + "WHERE STATUS=? AND (CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW') ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of alerts from the CONTENT_ALERTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_ALERTS";

    /**
     * The query to use to delete a alert from the CONTENT_ALERTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_ALERTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentAlertDAO(MonitorDAOFactory factory)
    {
        super(factory, "CONTENT_ALERTS");
    }

    /**
     * Defines the columns and indices for the CONTENT_ALERTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("START_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("REASON", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("MONITOR_ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("CONTENT_ALERTS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_ALERTS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("CONTENT_ALERTS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an alert from the CONTENT_ALERTS table by id.
     */
    public synchronized ContentAlert getById(String id) throws SQLException
    {
        ContentAlert ret = null;

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
                ContentAlert alert = new ContentAlert();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setStartDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setAttributes(new JSONObject(getClob(rs, 7)));
                alert.setStatus(rs.getString(8));
                alert.setMonitorId(rs.getString(9));
                alert.setCreatedBy(rs.getString(10));
                ret = alert;
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
     * Stores the given alert in the CONTENT_ALERTS table.
     */
    public synchronized void add(ContentAlert alert) throws SQLException
    {
        if(!hasConnection() || alert == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, alert.getId());
            insertStmt.setTimestamp(2, new Timestamp(alert.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(alert.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(alert.getStartDateMillis()), UTC);
            insertStmt.setString(5, alert.getCode());
            insertStmt.setString(6, alert.getReason().name());
            String attributes = alert.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(7, reader, attributes.length());
            insertStmt.setString(8, alert.getStatus().name());
            insertStmt.setString(9, alert.getMonitorId());
            insertStmt.setString(10, alert.getCreatedBy());
            insertStmt.setInt(11, SessionId.get());
            insertStmt.executeUpdate();

            logger.info("Created alert '"+alert.getId()+"' in CONTENT_ALERTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the alert already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Updates the given alert in the CONTENT_ALERTS table.
     */
    public synchronized void update(ContentAlert alert) throws SQLException
    {
        if(!hasConnection() || alert == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(alert.getUpdatedDateMillis()), UTC);
            String attributes = alert.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(2, reader, attributes.length());
            updateStmt.setString(3, alert.getStatus().name());
            updateStmt.setString(4, alert.getCreatedBy());
            updateStmt.setInt(5, SessionId.get());
            updateStmt.setString(6, alert.getId());
            updateStmt.executeUpdate();

            logger.info("Updated alert '"+alert.getId()+"' in CONTENT_ALERTS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the alerts from the CONTENT_ALERTS table.
     */
    public synchronized List<ContentAlert> list() throws SQLException
    {
        List<ContentAlert> ret = null;

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
            ret = new ArrayList<ContentAlert>();
            while(rs.next())
            {
                ContentAlert alert = new ContentAlert();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setStartDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setAttributes(new JSONObject(getClob(rs, 7)));
                alert.setStatus(rs.getString(8));
                alert.setMonitorId(rs.getString(9));
                alert.setCreatedBy(rs.getString(10));
                ret.add(alert);
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
     * Returns the alert items from the CONTENT_ALERTS table.
     */
    public synchronized List<ContentAlertItem> listItems() throws SQLException
    {
        List<ContentAlertItem> ret = null;

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
            ret = new ArrayList<ContentAlertItem>();
            while(rs.next())
            {
                ContentAlertItem alert = new ContentAlertItem();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setCode(rs.getString(4));
                alert.setReason(rs.getString(5));
                alert.setStatus(rs.getString(6));
                alert.setMonitorId(rs.getString(7));
                ret.add(alert);
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
     * Returns the alerts from the CONTENT_ALERTS table by organisation.
     */
    public synchronized List<ContentAlert> list(String code) throws SQLException
    {
        List<ContentAlert> ret = null;

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
            ret = new ArrayList<ContentAlert>();
            while(rs.next())
            {
                ContentAlert alert = new ContentAlert();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setStartDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setAttributes(new JSONObject(getClob(rs, 7)));
                alert.setStatus(rs.getString(8));
                alert.setMonitorId(rs.getString(9));
                alert.setCreatedBy(rs.getString(10));
                ret.add(alert);
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
     * Returns <CODE>true</CODE> if the given organisation has a NEW alert in the CONTENT_ALERTS table.
     */
    public boolean hasAlert(String code) throws SQLException
    {
        boolean ret = false;

        List<ContentAlert> alerts = list(code);
        if(alerts != null)
        {
            for(ContentAlert alert : alerts)
            {
                if(alert.getStatus() == AlertStatus.NEW)
                {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns the alert items from the CONTENT_ALERTS table by status.
     */
    public synchronized List<ContentAlertItem> listItems(AlertStatus status) throws SQLException
    {
        List<ContentAlertItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByStatusStmt == null)
            listItemsByStatusStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_STATUS_SQL);
        clearParameters(listItemsByStatusStmt);

        ResultSet rs = null;

        try
        {
            listItemsByStatusStmt.setString(1, status.name());
            listItemsByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByStatusStmt.executeQuery();
            ret = new ArrayList<ContentAlertItem>();
            while(rs.next())
            {
                ContentAlertItem alert = new ContentAlertItem();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setCode(rs.getString(4));
                alert.setReason(rs.getString(5));
                alert.setStatus(rs.getString(6));
                alert.setMonitorId(rs.getString(7));
                ret.add(alert);
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
     * Returns the count of alerts from the table.
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
     * Removes the given alert from the CONTENT_ALERTS table.
     */
    public synchronized void delete(ContentAlert alert) throws SQLException
    {
        if(!hasConnection() || alert == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, alert.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted alert '"+alert.getId()+"' in CONTENT_ALERTS");
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
        closeStatement(listItemsByStatusStmt);
        listItemsByStatusStmt = null;
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
    private PreparedStatement listItemsByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
