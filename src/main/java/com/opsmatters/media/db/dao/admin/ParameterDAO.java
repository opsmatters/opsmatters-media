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
package com.opsmatters.media.db.dao.admin;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.Parameter;
import com.opsmatters.media.model.admin.ParameterType;
import com.opsmatters.media.model.admin.ParameterName;
import com.opsmatters.media.util.StringUtils;

/**
 * DAO that provides operations on the PARAMETERS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ParameterDAO extends AdminDAO<Parameter>
{
    private static final Logger logger = Logger.getLogger(ParameterDAO.class.getName());

    /**
     * The query to use to select a parameter from the PARAMETERS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE "
      + "FROM PARAMETERS WHERE ID=?";

    /**
     * The query to use to select a parameter from the PARAMETERS table by name.
     */
    private static final String GET_BY_NAME_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE "
      + "FROM PARAMETERS WHERE TYPE=? AND NAME=?";

    /**
     * The query to use to insert a parameter into the PARAMETERS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PARAMETERS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a parameter in the PARAMETERS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PARAMETERS SET UPDATED_DATE=?, TYPE=?, NAME=?, VALUE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the parameters from the PARAMETERS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, NAME, VALUE "
      + "FROM PARAMETERS";

    /**
     * The query to use to get the count of parameters from the PARAMETERS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM PARAMETERS";

    /**
     * The query to use to delete a parameter from the PARAMETERS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM PARAMETERS WHERE TYPE=? AND NAME=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ParameterDAO(AdminDAOFactory factory)
    {
        super(factory, "PARAMETERS");
    }

    /**
     * Defines the columns and indices for the PARAMETERS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 20, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("VALUE", Types.VARCHAR, 10, true);
        table.setPrimaryKey("PARAMETERS_PK", new String[] {"ID"});
        table.addIndex("PARAMETERS_USERNAME_IDX", new String[] {"TYPE", "NAME"});
        table.setInitialised(true);
    }

    /**
     * Sets the default for the given parameter.
     */
    public void setDefault(ParameterType type, ParameterName name, String value) throws SQLException
    {
        Parameter parameter = getByName(type, name);
        if(parameter == null)
        {
            parameter = new Parameter();
            parameter.setId(StringUtils.getUUID(null));
            parameter.setCreatedDate(Instant.now());
            parameter.setType(type);
            parameter.setName(name);
            parameter.setValue(value);
            add(parameter);
        }
    }

    /**
     * Sets the default for the given parameter.
     */
    public void setDefault(ParameterType type, ParameterName name, int value) throws SQLException
    {
        setDefault(type, name, Integer.toString(value));
    }

    /**
     * Returns a parameter from the PARAMETERS table by id.
     */
    public Parameter getById(String id) throws SQLException
    {
        Parameter ret = null;

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
                Parameter parameter = new Parameter();
                parameter.setId(rs.getString(1));
                parameter.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                parameter.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                parameter.setType(ParameterType.fromValue(rs.getString(4)));
                parameter.setName(ParameterName.fromValue(rs.getString(5)));
                parameter.setValue(rs.getString(6));
                ret = parameter;
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
     * Returns a parameter from the PARAMETERS table by name.
     */
    public Parameter getByName(ParameterType type, ParameterName name) throws SQLException
    {
        Parameter ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByNameStmt == null)
            getByNameStmt = prepareStatement(getConnection(), GET_BY_NAME_SQL);
        clearParameters(getByNameStmt);

        ResultSet rs = null;

        try
        {
            getByNameStmt.setString(1, type.value());
            getByNameStmt.setString(2, name.value());
            getByNameStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByNameStmt.executeQuery();
            while(rs.next())
            {
                Parameter parameter = new Parameter();
                parameter.setId(rs.getString(1));
                parameter.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                parameter.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                parameter.setType(ParameterType.fromValue(rs.getString(4)));
                parameter.setName(ParameterName.fromValue(rs.getString(5)));
                parameter.setValue(rs.getString(6));
                ret = parameter;
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
     * Stores the given parameter in the PARAMETERS table.
     */
    public void add(Parameter parameter) throws SQLException
    {
        if(!hasConnection() || parameter == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, parameter.getId());
            insertStmt.setTimestamp(2, new Timestamp(parameter.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(parameter.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, parameter.getType().value());
            insertStmt.setString(5, parameter.getName().value());
            insertStmt.setString(6, parameter.getValue());
            insertStmt.executeUpdate();

            logger.info("Created parameter '"+parameter.getId()+"' in PARAMETERS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the parameter already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given parameters in the PARAMETERS table.
     */
    public void update(List<Parameter> parameters) throws SQLException
    {
        for(Parameter parameter : parameters)
            update(parameter);
    }

    /**
     * Updates the given parameter in the PARAMETERS table.
     */
    public void update(Parameter parameter) throws SQLException
    {
        if(!hasConnection() || parameter == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(parameter.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, parameter.getType().value());
        updateStmt.setString(3, parameter.getName().value());
        updateStmt.setString(4, parameter.getValue());
        updateStmt.setString(5, parameter.getId());
        updateStmt.executeUpdate();

        logger.info("Updated parameter '"+parameter.getId()+"' in PARAMETERS");
    }

    /**
     * Returns the parameters from the PARAMETERS table.
     */
    public List<Parameter> list() throws SQLException
    {
        List<Parameter> ret = null;

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
            ret = new ArrayList<Parameter>();
            while(rs.next())
            {
                Parameter parameter = new Parameter();
                parameter.setId(rs.getString(1));
                parameter.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                parameter.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                parameter.setType(ParameterType.fromValue(rs.getString(4)));
                parameter.setName(ParameterName.fromValue(rs.getString(5)));
                parameter.setValue(rs.getString(6));
                ret.add(parameter);
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
     * Returns the count of parameters from the table.
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
     * Removes the given parameter from the PARAMETERS table.
     */
    public void delete(Parameter parameter) throws SQLException
    {
        if(!hasConnection() || parameter == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, parameter.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted parameter '"+parameter.getId()+"' in PARAMETERS");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByNameStmt);
        getByNameStmt = null;
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
    private PreparedStatement getByNameStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
