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

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.monitor.ContentAlert;
import com.opsmatters.media.model.monitor.AlertStatus;
import com.opsmatters.media.util.AppSession;

/**
 * DAO that provides operations on the CONTENT_ALERTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentAlertDAO extends MonitorDAO<ContentAlert>
{
    private static final Logger logger = Logger.getLogger(ContentAlertDAO.class.getName());

    /**
     * The query to use to select a alert from the CONTENT_ALERTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EFFECTIVE_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, \"CHANGE\", CREATED_BY "
      + "FROM CONTENT_ALERTS WHERE ID=?";

    /**
     * The query to use to insert a alert into the CONTENT_ALERTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_ALERTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, EFFECTIVE_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, \"CHANGE\", CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a alert in the CONTENT_ALERTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_ALERTS SET UPDATED_DATE=?, STATUS=?, NOTES=?, \"CHANGE\"=?, CREATED_BY=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the alerts from the CONTENT_ALERTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EFFECTIVE_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, \"CHANGE\", CREATED_BY "
      + "FROM CONTENT_ALERTS "
      + "WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the alerts from the CONTENT_ALERTS table by organisation.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EFFECTIVE_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, \"CHANGE\", CREATED_BY "
      + "FROM CONTENT_ALERTS "
      + "WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the alerts from the CONTENT_ALERTS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EFFECTIVE_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, \"CHANGE\", CREATED_BY "
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
        table.addColumn("EFFECTIVE_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("REASON", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("MONITOR_ID", Types.VARCHAR, 36, true);
        table.addColumn("NOTES", Types.VARCHAR, 256, false);
        table.addColumn("CHANGE", Types.BOOLEAN, true);
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
                alert.setEffectiveDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setStatus(rs.getString(7));
                alert.setMonitorId(rs.getString(8));
                alert.setNotes(rs.getString(9));
                alert.setChange(rs.getBoolean(10));
                alert.setCreatedBy(rs.getString(11));
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

        try
        {
            insertStmt.setString(1, alert.getId());
            insertStmt.setTimestamp(2, new Timestamp(alert.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(alert.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(alert.getEffectiveDateMillis()), UTC);
            insertStmt.setString(5, alert.getCode());
            insertStmt.setString(6, alert.getReason().name());
            insertStmt.setString(7, alert.getStatus().name());
            insertStmt.setString(8, alert.getMonitorId());
            insertStmt.setString(9, alert.getNotes());
            insertStmt.setBoolean(10, alert.hasChange());
            insertStmt.setString(11, alert.getCreatedBy());
            insertStmt.setInt(12, AppSession.id());
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

        updateStmt.setTimestamp(1, new Timestamp(alert.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, alert.getStatus().name());
        updateStmt.setString(3, alert.getNotes());
        updateStmt.setBoolean(4, alert.hasChange());
        updateStmt.setString(5, alert.getCreatedBy());
        updateStmt.setInt(6, AppSession.id());
        updateStmt.setString(7, alert.getId());
        updateStmt.executeUpdate();

        logger.info("Updated alert '"+alert.getId()+"' in CONTENT_ALERTS");
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
                alert.setEffectiveDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setStatus(rs.getString(7));
                alert.setMonitorId(rs.getString(8));
                alert.setNotes(rs.getString(9));
                alert.setChange(rs.getBoolean(10));
                alert.setCreatedBy(rs.getString(11));
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
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<ContentAlert>();
            while(rs.next())
            {
                ContentAlert alert = new ContentAlert();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setEffectiveDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setStatus(rs.getString(7));
                alert.setMonitorId(rs.getString(8));
                alert.setNotes(rs.getString(9));
                alert.setChange(rs.getBoolean(10));
                alert.setCreatedBy(rs.getString(11));
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
     * Returns the alerts from the CONTENT_ALERTS table by status.
     */
    public synchronized List<ContentAlert> list(AlertStatus status) throws SQLException
    {
        List<ContentAlert> ret = null;

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
            ret = new ArrayList<ContentAlert>();
            while(rs.next())
            {
                ContentAlert alert = new ContentAlert();
                alert.setId(rs.getString(1));
                alert.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                alert.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                alert.setEffectiveDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                alert.setCode(rs.getString(5));
                alert.setReason(rs.getString(6));
                alert.setStatus(rs.getString(7));
                alert.setMonitorId(rs.getString(8));
                alert.setNotes(rs.getString(9));
                alert.setChange(rs.getBoolean(10));
                alert.setCreatedBy(rs.getString(11));
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
