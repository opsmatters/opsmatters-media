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
import com.opsmatters.media.model.admin.TableTask;

/**
 * DAO that provides operations on the TABLE_TASKS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TableTaskDAO extends AdminDAO<TableTask>
{
    private static final Logger logger = Logger.getLogger(TableTaskDAO.class.getName());

    /**
     * The query to use to select a task from the TABLE_TASKS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, TYPE, COUNT_QUERY, UPDATE_QUERY, \"INTERVAL\", INTERVAL_UNIT, ENABLED, STATUS, ITEM_COUNT, CREATED_BY "
      + "FROM TABLE_TASKS WHERE ID=?";

    /**
     * The query to use to insert a task into the TABLE_TASKS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO TABLE_TASKS"
      + "( ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, TYPE, COUNT_QUERY, UPDATE_QUERY, \"INTERVAL\", INTERVAL_UNIT, ENABLED, STATUS, ITEM_COUNT, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a task in the TABLE_TASKS table.
     */
    private static final String UPDATE_SQL =
      "UPDATE TABLE_TASKS SET UPDATED_DATE=?, EXECUTED_DATE=?, NAME=?, TYPE=?, COUNT_QUERY=?, UPDATE_QUERY=?, \"INTERVAL\"=?, INTERVAL_UNIT=?, ENABLED=?, STATUS=?, ITEM_COUNT=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the tasks from the TABLE_TASKS table.
     */
    private static final String LIST_SQL =
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, TYPE, COUNT_QUERY, UPDATE_QUERY, \"INTERVAL\", INTERVAL_UNIT, ENABLED, STATUS, ITEM_COUNT, CREATED_BY "
      + "FROM TABLE_TASKS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of tasks from the TABLE_TASKS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM TABLE_TASKS";

    /**
     * The query to use to delete a task from the TABLE_TASKS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM TABLE_TASKS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public TableTaskDAO(AdminDAOFactory factory)
    {
        super(factory, "TABLE_TASKS");
    }

    /**
     * Defines the columns and indices for the TABLE_TASKS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("EXECUTED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 25, true);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("COUNT_QUERY", Types.VARCHAR, 256, false);
        table.addColumn("UPDATE_QUERY", Types.VARCHAR, 256, true);
        table.addColumn("\"INTERVAL\"", Types.INTEGER, true);
        table.addColumn("INTERVAL_UNIT", Types.VARCHAR, 15, true);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("ITEM_COUNT", Types.INTEGER, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("TABLE_TASKS_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a task from the TABLE_TASKS table by id.
     */
    public synchronized TableTask getById(String id) throws SQLException
    {
        TableTask ret = null;

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
                TableTask task = new TableTask();
                task.setId(rs.getString(1));
                task.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                task.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                task.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                task.setName(rs.getString(5));
                task.setType(rs.getString(6));
                task.setCountQuery(rs.getString(7));
                task.setUpdateQuery(rs.getString(8));
                task.setInterval(rs.getInt(9));
                task.setIntervalUnit(rs.getString(10));
                task.setEnabled(rs.getBoolean(11));
                task.setStatus(rs.getString(12));
                task.setItemCount(rs.getInt(13));
                task.setCreatedBy(rs.getString(14));
                ret = task;
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
     * Stores the given task in the TABLE_TASKS table.
     */
    public synchronized void add(TableTask task) throws SQLException
    {
        if(!hasConnection() || task == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, task.getId());
            insertStmt.setTimestamp(2, new Timestamp(task.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(task.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(task.getExecutedDateMillis()), UTC);
            insertStmt.setString(5, task.getName());
            insertStmt.setString(6, task.getType().name());
            insertStmt.setString(7, task.getCountQuery());
            insertStmt.setString(8, task.getUpdateQuery());
            insertStmt.setInt(9, task.getInterval());
            insertStmt.setString(10, task.getIntervalUnit().name());
            insertStmt.setBoolean(11, task.isEnabled());
            insertStmt.setString(12, task.getStatus().name());
            insertStmt.setInt(13, task.getItemCount());
            insertStmt.setString(14, task.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created task '"+task.getId()+"' in TABLE_TASKS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the task already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given task in the TABLE_TASKS table.
     */
    public synchronized void update(TableTask task) throws SQLException
    {
        if(!hasConnection() || task == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(task.getUpdatedDateMillis()), UTC);
        updateStmt.setTimestamp(2, new Timestamp(task.getExecutedDateMillis()), UTC);
        updateStmt.setString(3, task.getName());
        updateStmt.setString(4, task.getType().name());
        updateStmt.setString(5, task.getCountQuery());
        updateStmt.setString(6, task.getUpdateQuery());
        updateStmt.setInt(7, task.getInterval());
        updateStmt.setString(8, task.getIntervalUnit().name());
        updateStmt.setBoolean(9, task.isEnabled());
        updateStmt.setString(10, task.getStatus().name());
        updateStmt.setInt(11, task.getItemCount());
        updateStmt.setString(12, task.getId());
        updateStmt.executeUpdate();

        logger.info("Updated task '"+task.getId()+"' in TABLE_TASKS");
    }

    /**
     * Returns the tasks from the TABLE_TASKS table.
     */
    public synchronized List<TableTask> list() throws SQLException
    {
        List<TableTask> ret = null;

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
            ret = new ArrayList<TableTask>();
            while(rs.next())
            {
                TableTask task = new TableTask();
                task.setId(rs.getString(1));
                task.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                task.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                task.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                task.setName(rs.getString(5));
                task.setType(rs.getString(6));
                task.setCountQuery(rs.getString(7));
                task.setUpdateQuery(rs.getString(8));
                task.setInterval(rs.getInt(9));
                task.setIntervalUnit(rs.getString(10));
                task.setEnabled(rs.getBoolean(11));
                task.setStatus(rs.getString(12));
                task.setItemCount(rs.getInt(13));
                task.setCreatedBy(rs.getString(14));
                ret.add(task);
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
     * Returns the count of tasks from the TABLE_TASKS table.
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
     * Removes the given task from the TABLE_TASKS table.
     */
    public synchronized void delete(TableTask task) throws SQLException
    {
        if(!hasConnection() || task == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, task.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted task '"+task.getId()+"' in TABLE_TASKS");
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
