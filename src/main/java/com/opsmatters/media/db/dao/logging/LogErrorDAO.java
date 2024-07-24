/*
 * Copyright 2024 Gerald Curley
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
package com.opsmatters.media.db.dao.logging;

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
import com.opsmatters.media.model.logging.Log;
import com.opsmatters.media.model.logging.LogEvent;
import com.opsmatters.media.model.logging.LogError;
import com.opsmatters.media.model.logging.LogErrorItem;
import com.opsmatters.media.model.logging.ErrorCode;
import com.opsmatters.media.util.SessionId;

import static com.opsmatters.media.model.logging.LogEventType.*;
import static com.opsmatters.media.model.logging.ErrorStatus.*;

/**
 * DAO that provides operations on the LOG_ERRORS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class LogErrorDAO extends LogDAO<LogError>
{
    private static final Logger logger = Logger.getLogger(LogErrorDAO.class.getName());

    /**
     * The query to use to select an error from the LOG_ERRORS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, CATEGORY, LEVEL, ENTITY_CODE, ENTITY_TYPE, ENTITY_NAME, ATTRIBUTES, STATUS "
      + "FROM LOG_ERRORS WHERE ID=?";

    /**
     * The query to use to insert an error into the LOG_ERRORS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO LOG_ERRORS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, CATEGORY, LEVEL, ENTITY_CODE, ENTITY_TYPE, ENTITY_NAME, ATTRIBUTES, STATUS, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an error in the LOG_ERRORS table.
     */
    private static final String UPDATE_SQL =
      "UPDATE LOG_ERRORS SET UPDATED_DATE=?, TYPE=?, CATEGORY=?, LEVEL=?, STATUS=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the errors from the LOG_ERRORS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, CATEGORY, LEVEL, ENTITY_CODE, ENTITY_TYPE, ENTITY_NAME, ATTRIBUTES, STATUS "
      + "FROM LOG_ERRORS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the error items from the LOG_ERRORS table.
     */
    private static final String LIST_ITEMS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, CATEGORY, LEVEL, ENTITY_CODE, ENTITY_TYPE, ENTITY_NAME, STATUS "
      + "FROM LOG_ERRORS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the errors from the LOG_ERRORS table by code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, CATEGORY, LEVEL, ENTITY_CODE, ENTITY_TYPE, ENTITY_NAME, ATTRIBUTES, STATUS "
      + "FROM LOG_ERRORS WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of errors from the LOG_ERRORS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM LOG_ERRORS";

    /**
     * The query to use to delete a error from the LOG_ERRORS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM LOG_ERRORS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public LogErrorDAO(LogDAOFactory factory)
    {
        super(factory, "LOG_ERRORS");
    }

    /**
     * Defines the columns and indices for the LOG_ERRORS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 20, true);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("CATEGORY", Types.VARCHAR, 15, true);
        table.addColumn("LEVEL", Types.VARCHAR, 15, true);
        table.addColumn("ENTITY_CODE", Types.VARCHAR, 15, true);
        table.addColumn("ENTITY_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("ENTITY_NAME", Types.VARCHAR, 30, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("LOG_ERRORS_PK", new String[] {"ID"});
        table.addIndex("LOG_ERRORS_CODE_IDX", new String[] {"CODE"});
        table.addIndex("LOG_ERRORS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("LOG_ERRORS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an error from the LOG_ERRORS table by id.
     */
    public synchronized LogError getById(String id) throws SQLException
    {
        LogError ret = null;

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
                LogError error = new LogError();
                error.setId(rs.getString(1));
                error.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                error.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                error.setCode(rs.getString(4));
                error.setType(rs.getString(5));
                error.setCategory(rs.getString(6));
                error.setLevel(rs.getString(7));
                error.setEntityCode(rs.getString(8));
                error.setEntityType(rs.getString(9));
                error.setEntityName(rs.getString(10));
                error.setAttributes(new JSONObject(getClob(rs, 11)));
                error.setStatus(rs.getString(12));
                ret = error;
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
     * Stores the given error in the LOG_ERRORS table.
     */
    public synchronized void add(LogError error) throws SQLException
    {
        if(!hasConnection() || error == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, error.getId());
            insertStmt.setTimestamp(2, new Timestamp(error.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(error.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, error.getCode().name());
            insertStmt.setString(5, error.getType().name());
            insertStmt.setString(6, error.getCategory().name());
            insertStmt.setString(7, error.getLevel().name());
            insertStmt.setString(8, error.getEntityCode());
            insertStmt.setString(9, error.getEntityType());
            insertStmt.setString(10, error.getEntityName());
            String attributes = error.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(11, reader, attributes.length());
            insertStmt.setString(12, error.getStatus().name());
            insertStmt.setInt(13, SessionId.get());
            insertStmt.executeUpdate();

            logger.info(String.format("Created error %s in LOG_ERRORS",
                error.getId()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the error already exists
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
     * Stores the given error in the LOG_ERRORS table if an error with the same entity does not already exist.
     */
    public void add(LogError error, boolean checkDuplicate) throws SQLException
    {
        boolean found = false;

        if(checkDuplicate && error.hasEntityCode())
        {
            List<LogError> errors = list(error.getCode());
            for(LogError existing : errors)
            {
                if(existing.getStatus() == NEW
                    && existing.getEntityCode().equals(error.getEntityCode())
                    && existing.getEntityType().equals(error.getEntityType())
                    && existing.getEntityName().equals(error.getEntityName()))
                {
                    found = true;
                    break;
                }
            }
        }

        if(!found)
            add(error);
    }

    /**
     * Stores the errors from the given log in the LOG_ERRORS table.
     */
    public void addAll(List<LogEvent> events) throws SQLException
    {
        for(LogEvent event : events)
        {
            // Ignore user-generated events
            if(event.getType() == UI)
                continue;

            // Only add LogErrors to database
            if(event instanceof LogError)
                add((LogError)event, true);
        }
    }

    /**
     * Stores the errors from the given log in the LOG_ERRORS table.
     */
    public void addAll(Log log) throws SQLException
    {
        addAll(log.getEvents());
    }

    /**
     * Updates the given error in the LOG_ERRORS table.
     */
    public synchronized void update(LogError error) throws SQLException
    {
        if(!hasConnection() || error == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null, reader2 = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(error.getUpdatedDateMillis()), UTC);
            updateStmt.setString(2, error.getType().name());
            updateStmt.setString(3, error.getCategory().name());
            updateStmt.setString(4, error.getLevel().name());
            updateStmt.setString(5, error.getStatus().name());
            updateStmt.setInt(6, SessionId.get());
            updateStmt.setString(7, error.getId());
            updateStmt.executeUpdate();

            logger.info(String.format("Updated error %s in LOG_ERRORS",
                error.getId()));
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
     * Returns the errors from the LOG_ERRORS table.
     */
    public synchronized List<LogError> list() throws SQLException
    {
        List<LogError> ret = null;

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
            ret = new ArrayList<LogError>();
            while(rs.next())
            {
                LogError error = new LogError();
                error.setId(rs.getString(1));
                error.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                error.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                error.setCode(rs.getString(4));
                error.setType(rs.getString(5));
                error.setCategory(rs.getString(6));
                error.setLevel(rs.getString(7));
                error.setEntityCode(rs.getString(8));
                error.setEntityType(rs.getString(9));
                error.setEntityName(rs.getString(10));
                error.setAttributes(new JSONObject(getClob(rs, 11)));
                error.setStatus(rs.getString(12));
                ret.add(error);
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
     * Returns the error items from the LOG_ERRORS table.
     */
    public synchronized List<LogErrorItem> listItems() throws SQLException
    {
        List<LogErrorItem> ret = null;

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
            ret = new ArrayList<LogErrorItem>();
            while(rs.next())
            {
                LogErrorItem error = new LogErrorItem();
                error.setId(rs.getString(1));
                error.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                error.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                error.setCode(rs.getString(4));
                error.setType(rs.getString(5));
                error.setCategory(rs.getString(6));
                error.setLevel(rs.getString(7));
                error.setEntityCode(rs.getString(8));
                error.setEntityType(rs.getString(9));
                error.setEntityName(rs.getString(10));
                error.setStatus(rs.getString(11));
                ret.add(error);
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
     * Returns the errors from the LOG_ERRORS table by code.
     */
    public synchronized List<LogError> list(ErrorCode code) throws SQLException
    {
        List<LogError> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, code.name());
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<LogError>();
            while(rs.next())
            {
                LogError error = new LogError();
                error.setId(rs.getString(1));
                error.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                error.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                error.setCode(rs.getString(4));
                error.setType(rs.getString(5));
                error.setCategory(rs.getString(6));
                error.setLevel(rs.getString(7));
                error.setEntityCode(rs.getString(8));
                error.setEntityType(rs.getString(9));
                error.setEntityName(rs.getString(10));
                error.setAttributes(new JSONObject(getClob(rs, 11)));
                error.setStatus(rs.getString(12));
                ret.add(error);
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
     * Returns the count of errors from the LOG_ERRORS table.
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
     * Removes the given error from the LOG_ERRORS table.
     */
    public synchronized void delete(LogError error) throws SQLException
    {
        if(!hasConnection() || error == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, error.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted error %s in LOG_ERRORS",
            error.getId()));
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
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
