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
import com.opsmatters.media.model.admin.TaskExecution;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the TASK_EXECUTIONS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaskExecutionDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(TaskExecutionDAO.class.getName());

    /**
     * The query to use to select an execution from the TASK_EXECUTIONS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TASK_ID, EXECUTION_TIME, UPDATED_COUNT, DELETED_COUNT, ERROR_MESSAGE "
      + "FROM TASK_EXECUTIONS WHERE ID=?";

    /**
     * The query to use to insert an execution into the TASK_EXECUTIONS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO TASK_EXECUTIONS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TASK_ID, EXECUTION_TIME, UPDATED_COUNT, DELETED_COUNT, ERROR_MESSAGE )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an execution in the TASK_EXECUTIONS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE TASK_EXECUTIONS SET UPDATED_DATE=?, EXECUTION_TIME=?, UPDATED_COUNT=?, DELETED_COUNT=?, ERROR_MESSAGE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the executions from the TASK_EXECUTIONS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TASK_ID, EXECUTION_TIME, UPDATED_COUNT, DELETED_COUNT, ERROR_MESSAGE "
      + "FROM TASK_EXECUTIONS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the executions from the TASK_EXECUTIONS table by task id.
     */
    private static final String LIST_BY_TASK_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TASK_ID, EXECUTION_TIME, UPDATED_COUNT, DELETED_COUNT, ERROR_MESSAGE "
      + "FROM TASK_EXECUTIONS WHERE TASK_ID=? AND CREATED_DATE >= (NOW() + INTERVAL -30 DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of executions from the TASK_EXECUTIONS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM TASK_EXECUTIONS";

    /**
     * The query to use to delete an execution from the TASK_EXECUTIONS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM TASK_EXECUTIONS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public TaskExecutionDAO(AdminDAOFactory factory)
    {
        super(factory, "TASK_EXECUTIONS");
    }

    /**
     * Defines the columns and indices for the TASK_EXECUTIONS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TASK_ID", Types.VARCHAR, 36, true);
        table.addColumn("EXECUTION_TIME", Types.BIGINT, true);
        table.addColumn("UPDATED_COUNT", Types.INTEGER, true);
        table.addColumn("DELETED_COUNT", Types.INTEGER, true);
        table.addColumn("ERROR_MESSAGE", Types.VARCHAR, 256, false);
        table.setPrimaryKey("TASK_EXECUTIONS_PK", new String[] {"ID"});
        table.addIndex("TASK_EXECUTIONS_TASK_ID_IDX", new String[] {"TASK_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an execution from the TASK_EXECUTIONS table by id.
     */
    public synchronized TaskExecution getById(String id) throws SQLException
    {
        TaskExecution ret = null;

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
                TaskExecution execution = new TaskExecution();
                execution.setId(rs.getString(1));
                execution.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                execution.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                execution.setTaskId(rs.getString(4));
                execution.setExecutionTime(rs.getLong(5));
                execution.setUpdatedCount(rs.getInt(6));
                execution.setDeletedCount(rs.getInt(7));
                execution.setErrorMessage(rs.getString(8));
                ret = execution;
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
     * Stores the given execution in the TASK_EXECUTIONS table.
     */
    public synchronized void add(TaskExecution execution) throws SQLException
    {
        if(!hasConnection() || execution == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, execution.getId());
            insertStmt.setTimestamp(2, new Timestamp(execution.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(execution.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, execution.getTaskId());
            insertStmt.setLong(5, execution.getExecutionTime());
            insertStmt.setInt(6, execution.getUpdatedCount());
            insertStmt.setInt(7, execution.getDeletedCount());
            insertStmt.setString(8, execution.getErrorMessage());
            insertStmt.executeUpdate();

            logger.info("Created execution '"+execution.getId()+"' in TASK_EXECUTIONS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the execution already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given execution in the TASK_EXECUTIONS table.
     */
    public synchronized void update(TaskExecution execution) throws SQLException
    {
        if(!hasConnection() || execution == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(execution.getUpdatedDateMillis()), UTC);
        updateStmt.setLong(2, execution.getExecutionTime());
        updateStmt.setInt(3, execution.getUpdatedCount());
        updateStmt.setInt(4, execution.getDeletedCount());
        updateStmt.setString(5, execution.getErrorMessage());
        updateStmt.setString(6, execution.getId());
        updateStmt.executeUpdate();

        logger.info("Updated execution '"+execution.getId()+"' in TASK_EXECUTIONS");
    }

    /**
     * Returns the executions from the TASK_EXECUTIONS table.
     */
    public synchronized List<TaskExecution> list() throws SQLException
    {
        List<TaskExecution> ret = null;

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
            ret = new ArrayList<TaskExecution>();
            while(rs.next())
            {
                TaskExecution execution = new TaskExecution();
                execution.setId(rs.getString(1));
                execution.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                execution.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                execution.setTaskId(rs.getString(4));
                execution.setExecutionTime(rs.getLong(5));
                execution.setUpdatedCount(rs.getInt(6));
                execution.setDeletedCount(rs.getInt(7));
                execution.setErrorMessage(rs.getString(8));
                ret.add(execution);
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
     * Returns the executions from the TASK_EXECUTIONS table by task id.
     */
    public synchronized List<TaskExecution> list(String taskId) throws SQLException
    {
        List<TaskExecution> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTaskStmt == null)
            listByTaskStmt = prepareStatement(getConnection(), LIST_BY_TASK_SQL);
        clearParameters(listByTaskStmt);

        ResultSet rs = null;

        try
        {
            listByTaskStmt.setString(1, taskId);
            listByTaskStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTaskStmt.executeQuery();
            ret = new ArrayList<TaskExecution>();
            while(rs.next())
            {
                TaskExecution execution = new TaskExecution();
                execution.setId(rs.getString(1));
                execution.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                execution.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                execution.setTaskId(rs.getString(4));
                execution.setExecutionTime(rs.getLong(5));
                execution.setUpdatedCount(rs.getInt(6));
                execution.setDeletedCount(rs.getInt(7));
                execution.setErrorMessage(rs.getString(8));
                ret.add(execution);
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
     * Returns the count of executions from the TASK_EXECUTIONS table.
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
     * Removes the given execution from the TASK_EXECUTIONS table.
     */
    public synchronized void delete(TaskExecution execution) throws SQLException
    {
        if(!hasConnection() || execution == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, execution.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted execution '"+execution.getId()+"' in TASK_EXECUTIONS");
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
        closeStatement(listByTaskStmt);
        listByTaskStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByTaskStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
