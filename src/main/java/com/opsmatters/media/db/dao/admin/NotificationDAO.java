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
package com.opsmatters.media.db.dao.admin;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.Notification;
import com.opsmatters.media.model.admin.NotificationStatus;
import com.opsmatters.media.model.admin.NotificationType;

/**
 * DAO that provides operations on the NOTIFICATIONS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NotificationDAO extends AdminDAO<Notification>
{
    private static final Logger logger = Logger.getLogger(NotificationDAO.class.getName());

    /**
     * The query to use to select a notification from the NOTIFICATIONS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SUMMARY, LEVEL, TYPE, STATUS, EXPIRY "
      + "FROM NOTIFICATIONS WHERE ID=?";

    /**
     * The query to use to insert a notification into the NOTIFICATIONS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO NOTIFICATIONS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, SUMMARY, LEVEL, TYPE, STATUS, EXPIRY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a notification in the NOTIFICATIONS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE NOTIFICATIONS SET UPDATED_DATE=?, SUMMARY=?, LEVEL=?, TYPE=?, STATUS=?, EXPIRY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the notifications from the NOTIFICATIONS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SUMMARY, LEVEL, TYPE, STATUS, EXPIRY "
      + "FROM NOTIFICATIONS WHERE STATUS != 'COMPLETED' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the notifications from the NOTIFICATIONS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SUMMARY, LEVEL, TYPE, STATUS, EXPIRY "
      + "FROM NOTIFICATIONS WHERE STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the notifications from the NOTIFICATIONS table by type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, SUMMARY, LEVEL, TYPE, STATUS, EXPIRY "
      + "FROM NOTIFICATIONS WHERE TYPE=? AND STATUS != 'COMPLETED' ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of notifications from the NOTIFICATIONS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM NOTIFICATIONS";

    /**
     * The query to use to delete a notification from the NOTIFICATIONS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM NOTIFICATIONS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public NotificationDAO(AdminDAOFactory factory)
    {
        super(factory, "NOTIFICATIONS");
    }

    /**
     * Defines the columns and indices for the NOTIFICATIONS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 35, false);
        table.addColumn("SUMMARY", Types.VARCHAR, 128, true);
        table.addColumn("LEVEL", Types.VARCHAR, 10, true);
        table.addColumn("TYPE", Types.VARCHAR, 25, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("EXPIRY", Types.INTEGER, true);
        table.setPrimaryKey("NOTIFICATIONS_PK", new String[] {"ID"});
        table.addIndex("NOTIFICATIONS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("NOTIFICATIONS_TYPE_IDX", new String[] {"TYPE"});
        table.setInitialised(true);
    }

    /**
     * Returns a notification from the NOTIFICATIONS table by id.
     */
    public synchronized Notification getById(String id) throws SQLException
    {
        Notification ret = null;

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
                Notification notification = new Notification();
                notification.setId(rs.getString(1));
                notification.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                notification.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                notification.setCode(rs.getString(4));
                notification.setSummary(rs.getString(5));
                notification.setLevel(rs.getString(6));
                notification.setType(rs.getString(7));
                notification.setStatus(rs.getString(8));
                notification.setExpiry(rs.getInt(9));
                ret = notification;
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
     * Stores the given notification in the NOTIFICATIONS table.
     */
    public synchronized void add(Notification notification) throws SQLException
    {
        if(!hasConnection() || notification == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, notification.getId());
            insertStmt.setTimestamp(2, new Timestamp(notification.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(notification.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, notification.getCode());
            insertStmt.setString(5, notification.getSummary());
            insertStmt.setString(6, notification.getLevel().name());
            insertStmt.setString(7, notification.getType().name());
            insertStmt.setString(8, notification.getStatus().name());
            insertStmt.setInt(9, notification.getExpiry());
            insertStmt.executeUpdate();

            logger.info("Created notification '"+notification.getId()+"' in NOTIFICATIONS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the notification already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given notification in the NOTIFICATIONS table.
     */
    public synchronized void update(Notification notification) throws SQLException
    {
        if(!hasConnection() || notification == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(notification.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, notification.getSummary());
        updateStmt.setString(3, notification.getLevel().name());
        updateStmt.setString(4, notification.getType().name());
        updateStmt.setString(5, notification.getStatus().name());
        updateStmt.setInt(6, notification.getExpiry());
        updateStmt.setString(7, notification.getId());
        updateStmt.executeUpdate();

        logger.info("Updated notification '"+notification.getId()+"' in NOTIFICATIONS");
    }

    /**
     * Returns the notifications from the NOTIFICATIONS table.
     */
    public synchronized List<Notification> list() throws SQLException
    {
        List<Notification> ret = null;

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
            ret = new ArrayList<Notification>();
            while(rs.next())
            {
                Notification notification = new Notification();
                notification.setId(rs.getString(1));
                notification.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                notification.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                notification.setCode(rs.getString(4));
                notification.setSummary(rs.getString(5));
                notification.setLevel(rs.getString(6));
                notification.setType(rs.getString(7));
                notification.setStatus(rs.getString(8));
                notification.setExpiry(rs.getInt(9));
                ret.add(notification);
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
     * Returns the notifications from the NOTIFICATIONS table by status.
     */
    public synchronized List<Notification> list(NotificationStatus status) throws SQLException
    {
        List<Notification> ret = null;

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
            ret = new ArrayList<Notification>();
            while(rs.next())
            {
                Notification notification = new Notification();
                notification.setId(rs.getString(1));
                notification.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                notification.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                notification.setCode(rs.getString(4));
                notification.setSummary(rs.getString(5));
                notification.setLevel(rs.getString(6));
                notification.setType(rs.getString(7));
                notification.setStatus(rs.getString(8));
                notification.setExpiry(rs.getInt(9));
                ret.add(notification);
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
     * Returns the notifications from the NOTIFICATIONS table by type.
     */
    public synchronized List<Notification> list(NotificationType type) throws SQLException
    {
        List<Notification> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, type.name());
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<Notification>();
            while(rs.next())
            {
                Notification notification = new Notification();
                notification.setId(rs.getString(1));
                notification.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                notification.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                notification.setCode(rs.getString(4));
                notification.setSummary(rs.getString(5));
                notification.setLevel(rs.getString(6));
                notification.setType(rs.getString(7));
                notification.setStatus(rs.getString(8));
                notification.setExpiry(rs.getInt(9));
                ret.add(notification);
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
     * Returns the notifications from the NOTIFICATIONS table by type and code.
     */
    public synchronized List<Notification> listPending(NotificationType type, String code) throws SQLException
    {
        List<Notification> ret = new ArrayList<Notification>();
        List<Notification> notifications = list(type);
        for(Notification notification : notifications)
        {
            if(notification.getStatus() != NotificationStatus.PENDING)
                continue;

            if(code != null && !code.equals(notification.getCode()))
                continue;

            ret.add(notification);
        }

        return ret;
    }

    /**
     * Returns the count of notifications from the NOTIFICATIONS table.
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
     * Removes the given notification from the NOTIFICATIONS table.
     */
    public synchronized void delete(Notification notification) throws SQLException
    {
        if(!hasConnection() || notification == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, notification.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted notification '"+notification.getId()+"' in NOTIFICATIONS");
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
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(listByTypeStmt);
        listByTypeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement listByTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
