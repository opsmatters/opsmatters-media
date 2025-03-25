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
package com.opsmatters.media.db.dao.content;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.OutputColumn;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the OUTPUT_COLUMNS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OutputColumnDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OutputColumnDAO.class.getName());

    /**
     * The query to use to select columns from the OUTPUT_COLUMNS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, POSITION, ENABLED, CREATED_BY "
      + "FROM OUTPUT_COLUMNS WHERE ID=?";

    /**
     * The query to use to insert columns into the OUTPUT_COLUMNS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO OUTPUT_COLUMNS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, POSITION, ENABLED, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update columns in the OUTPUT_COLUMNS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE OUTPUT_COLUMNS SET UPDATED_DATE=?, NAME=?, VALUE=?, POSITION=?, ENABLED=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the columns from the OUTPUT_COLUMNS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, POSITION, ENABLED, CREATED_BY "
      + "FROM OUTPUT_COLUMNS ORDER BY POSITION";

    /**
     * The query to use to select the columns from the OUTPUT_COLUMNS table by type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, POSITION, ENABLED, CREATED_BY "
      + "FROM OUTPUT_COLUMNS WHERE TYPE=? ORDER BY POSITION";

    /**
     * The query to use to get the count of columns from the OUTPUT_COLUMNS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM OUTPUT_COLUMNS";

    /**
     * The query to use to delete columns from the OUTPUT_COLUMNS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM OUTPUT_COLUMNS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OutputColumnDAO(ContentDAOFactory factory)
    {
        super(factory, "OUTPUT_COLUMNS");
    }

    /**
     * Defines the columns and indices for the OUTPUT_COLUMNS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("VALUE", Types.VARCHAR, 128, true);
        table.addColumn("POSITION", Types.INTEGER, true);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("OUTPUT_COLUMNS_PK", new String[] {"ID"});
        table.addIndex("OUTPUT_COLUMNS_TYPE_IDX", new String[] {"TYPE"});
        table.setInitialised(true);
    }

    /**
     * Returns columns from the OUTPUT_COLUMNS table by id.
     */
    public synchronized OutputColumn getById(String id) throws SQLException
    {
        OutputColumn ret = null;

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
                OutputColumn column = new OutputColumn();
                column.setId(rs.getString(1));
                column.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                column.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                column.setType(rs.getString(4));
                column.setName(rs.getString(5));
                column.setValue(rs.getString(6));
                column.setPosition(rs.getInt(7));
                column.setEnabled(rs.getBoolean(8));
                column.setCreatedBy(rs.getString(9));
                ret = column;
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
     * Stores the given column in the OUTPUT_COLUMNS table.
     */
    public synchronized void add(OutputColumn column) throws SQLException
    {
        if(!hasConnection() || column == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, column.getId());
            insertStmt.setTimestamp(2, new Timestamp(column.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(column.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, column.getType().name());
            insertStmt.setString(5, column.getName());
            insertStmt.setString(6, column.getValue());
            insertStmt.setInt(7, column.getPosition());
            insertStmt.setBoolean(8, column.isEnabled());
            insertStmt.setString(9, column.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created column '"+column.getId()+"' in OUTPUT_COLUMNS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the column already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given column in the OUTPUT_COLUMNS table.
     */
    public synchronized void update(OutputColumn column) throws SQLException
    {
        if(!hasConnection() || column == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(column.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, column.getName());
        updateStmt.setString(3, column.getValue());
        updateStmt.setInt(4, column.getPosition());
        updateStmt.setBoolean(5, column.isEnabled());
        updateStmt.setString(6, column.getCreatedBy());
        updateStmt.setString(7, column.getId());
        updateStmt.executeUpdate();

        logger.info("Updated column '"+column.getId()+"' in OUTPUT_COLUMNS");
    }

    /**
     * Returns the columns from the OUTPUT_COLUMNS table.
     */
    public synchronized List<OutputColumn> list() throws SQLException
    {
        List<OutputColumn> ret = null;

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
            ret = new ArrayList<OutputColumn>();
            while(rs.next())
            {
                OutputColumn column = new OutputColumn();
                column.setId(rs.getString(1));
                column.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                column.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                column.setType(rs.getString(4));
                column.setName(rs.getString(5));
                column.setValue(rs.getString(6));
                column.setPosition(rs.getInt(7));
                column.setEnabled(rs.getBoolean(8));
                column.setCreatedBy(rs.getString(9));
                ret.add(column);
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
     * Returns the columns from the OUTPUT_COLUMNS table by type.
     */
    public synchronized List<OutputColumn> list(ContentType type) throws SQLException
    {
        List<OutputColumn> ret = null;

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
            ret = new ArrayList<OutputColumn>();
            while(rs.next())
            {
                OutputColumn column = new OutputColumn();
                column.setId(rs.getString(1));
                column.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                column.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                column.setType(rs.getString(4));
                column.setName(rs.getString(5));
                column.setValue(rs.getString(6));
                column.setPosition(rs.getInt(7));
                column.setEnabled(rs.getBoolean(8));
                column.setCreatedBy(rs.getString(9));
                ret.add(column);
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
     * Returns the count of columns from the table.
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
     * Removes the given column from the OUTPUT_COLUMNS table.
     */
    public synchronized void delete(OutputColumn column) throws SQLException
    {
        if(!hasConnection() || column == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, column.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted column '"+column.getId()+"' in OUTPUT_COLUMNS");
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
    private PreparedStatement listByTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
