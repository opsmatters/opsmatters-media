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
import com.opsmatters.media.model.content.FieldDefault;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the FIELD_DEFAULTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class FieldDefaultDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(FieldDefaultDAO.class.getName());

    /**
     * The query to use to select defaults from the FIELD_DEFAULTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, ENABLED, CREATED_BY "
      + "FROM FIELD_DEFAULTS WHERE ID=?";

    /**
     * The query to use to insert defaults into the FIELD_DEFAULTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO FIELD_DEFAULTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, ENABLED, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update defaults in the FIELD_DEFAULTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE FIELD_DEFAULTS SET UPDATED_DATE=?, NAME=?, VALUE=?, ENABLED=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the defaults from the FIELD_DEFAULTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, ENABLED, CREATED_BY "
      + "FROM FIELD_DEFAULTS";

    /**
     * The query to use to select the defaults from the FIELD_DEFAULTS table by type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE, ENABLED, CREATED_BY "
      + "FROM FIELD_DEFAULTS WHERE TYPE=?";

    /**
     * The query to use to get the count of defaults from the FIELD_DEFAULTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM FIELD_DEFAULTS";

    /**
     * The query to use to delete defaults from the FIELD_DEFAULTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM FIELD_DEFAULTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public FieldDefaultDAO(ContentDAOFactory factory)
    {
        super(factory, "FIELD_DEFAULTS");
    }

    /**
     * Defines the defaults and indices for the FIELD_DEFAULTS table.
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
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("FIELD_DEFAULTS_PK", new String[] {"ID"});
        table.addIndex("FIELD_DEFAULTS_TYPE_IDX", new String[] {"TYPE"});
        table.setInitialised(true);
    }

    /**
     * Returns defaults from the FIELD_DEFAULTS table by id.
     */
    public synchronized FieldDefault getById(String id) throws SQLException
    {
        FieldDefault ret = null;

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
                FieldDefault _default = new FieldDefault();
                _default.setId(rs.getString(1));
                _default.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                _default.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                _default.setType(rs.getString(4));
                _default.setName(rs.getString(5));
                _default.setValue(rs.getString(6));
                _default.setEnabled(rs.getBoolean(7));
                _default.setCreatedBy(rs.getString(8));
                ret = _default;
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
     * Stores the given default in the FIELD_DEFAULTS table.
     */
    public synchronized void add(FieldDefault _default) throws SQLException
    {
        if(!hasConnection() || _default == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, _default.getId());
            insertStmt.setTimestamp(2, new Timestamp(_default.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(_default.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, _default.getType().name());
            insertStmt.setString(5, _default.getName());
            insertStmt.setString(6, _default.getValue());
            insertStmt.setBoolean(7, _default.isEnabled());
            insertStmt.setString(8, _default.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created default '"+_default.getId()+"' in FIELD_DEFAULTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the default already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given default in the FIELD_DEFAULTS table.
     */
    public synchronized void update(FieldDefault _default) throws SQLException
    {
        if(!hasConnection() || _default == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(_default.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, _default.getName());
        updateStmt.setString(3, _default.getValue());
        updateStmt.setBoolean(4, _default.isEnabled());
        updateStmt.setString(5, _default.getCreatedBy());
        updateStmt.setString(6, _default.getId());
        updateStmt.executeUpdate();

        logger.info("Updated default '"+_default.getId()+"' in FIELD_DEFAULTS");
    }

    /**
     * Adds or Updates the given default in the FIELD_DEFAULTS table.
     */
    public boolean upsert(FieldDefault _default) throws SQLException
    {
        boolean ret = false;

        FieldDefault existing = getById(_default.getId());
        if(existing != null)
        {
            update(_default);
        }
        else
        {
            add(_default);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the defaults from the FIELD_DEFAULTS table.
     */
    public synchronized List<FieldDefault> list() throws SQLException
    {
        List<FieldDefault> ret = null;

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
            ret = new ArrayList<FieldDefault>();
            while(rs.next())
            {
                FieldDefault _default = new FieldDefault();
                _default.setId(rs.getString(1));
                _default.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                _default.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                _default.setType(rs.getString(4));
                _default.setName(rs.getString(5));
                _default.setValue(rs.getString(6));
                _default.setEnabled(rs.getBoolean(7));
                _default.setCreatedBy(rs.getString(8));
                ret.add(_default);
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
     * Returns the defaults from the FIELD_DEFAULTS table by type.
     */
    public synchronized List<FieldDefault> list(ContentType type) throws SQLException
    {
        List<FieldDefault> ret = null;

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
            ret = new ArrayList<FieldDefault>();
            while(rs.next())
            {
                FieldDefault _default = new FieldDefault();
                _default.setId(rs.getString(1));
                _default.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                _default.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                _default.setType(rs.getString(4));
                _default.setName(rs.getString(5));
                _default.setValue(rs.getString(6));
                _default.setEnabled(rs.getBoolean(7));
                _default.setCreatedBy(rs.getString(8));
                ret.add(_default);
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
     * Returns the count of defaults from the table.
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
     * Removes the given default from the FIELD_DEFAULTS table.
     */
    public synchronized void delete(FieldDefault _default) throws SQLException
    {
        if(!hasConnection() || _default == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, _default.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted default '"+_default.getId()+"' in FIELD_DEFAULTS");
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
