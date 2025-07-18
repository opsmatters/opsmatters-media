/*
 * Copyright 2019 Gerald Curley
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
import com.opsmatters.media.model.admin.User;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the USERS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class UserDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * The query to use to select a user from the USERS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, USERNAME, EMAIL, FIRSTNAME, LASTNAME, ROLE, TIMEZONE, ADMIN, STATUS "
      + "FROM USERS WHERE ID=?";

    /**
     * The query to use to select a user from the USERS table by username.
     */
    private static final String GET_BY_USERNAME_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, USERNAME, EMAIL, FIRSTNAME, LASTNAME, ROLE, TIMEZONE, ADMIN, STATUS "
      + "FROM USERS WHERE USERNAME=?";

    /**
     * The query to use to select a user from the USERS table by email.
     */
    private static final String GET_BY_EMAIL_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, USERNAME, EMAIL, FIRSTNAME, LASTNAME, ROLE, TIMEZONE, ADMIN, STATUS "
      + "FROM USERS WHERE EMAIL=?";

    /**
     * The query to use to insert a user into the USERS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO USERS"
      + "( ID, CREATED_DATE, UPDATED_DATE, USERNAME, EMAIL, FIRSTNAME, LASTNAME, ROLE, TIMEZONE, ADMIN, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a user in the USERS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE USERS SET UPDATED_DATE=?, USERNAME=?, EMAIL=?, FIRSTNAME=?, LASTNAME=?, ROLE=?, TIMEZONE=?, ADMIN=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the users from the USERS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, USERNAME, EMAIL, FIRSTNAME, LASTNAME, ROLE, TIMEZONE, ADMIN, STATUS "
      + "FROM USERS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of users from the USERS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM USERS";

    /**
     * The query to use to delete a user from the USERS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM USERS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public UserDAO(AdminDAOFactory factory)
    {
        super(factory, "USERS");
    }

    /**
     * Defines the columns and indices for the USERS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("USERNAME", Types.VARCHAR, 20, true);
        table.addColumn("EMAIL", Types.VARCHAR, 30, true);
        table.addColumn("FIRSTNAME", Types.VARCHAR, 20, false);
        table.addColumn("LASTNAME", Types.VARCHAR, 20, false);
        table.addColumn("ROLE", Types.VARCHAR, 50, false);
        table.addColumn("TIMEZONE", Types.VARCHAR, 30, true);
        table.addColumn("ADMIN", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("USERS_PK", new String[] {"ID"});
        table.addIndex("USERS_USERNAME_IDX", new String[] {"USERNAME"});
        table.setInitialised(true);
    }

    /**
     * Returns a user from the USERS table by id.
     */
    public synchronized User getById(String id) throws SQLException
    {
        User ret = null;

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
                User user = new User();
                user.setId(rs.getString(1));
                user.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                user.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                user.setUsername(rs.getString(4));
                user.setEmail(rs.getString(5));
                user.setFirstName(rs.getString(6));
                user.setLastName(rs.getString(7));
                user.setRole(rs.getString(8));
                user.setTimezone(rs.getString(9));
                user.setAdmin(rs.getBoolean(10));
                user.setStatus(rs.getString(11));
                ret = user;
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
     * Returns a user from the USERS table by username.
     */
    public synchronized User getByUsername(String username) throws SQLException
    {
        User ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUsernameStmt == null)
            getByUsernameStmt = prepareStatement(getConnection(), GET_BY_USERNAME_SQL);
        clearParameters(getByUsernameStmt);

        ResultSet rs = null;

        try
        {
            getByUsernameStmt.setString(1, username);
            getByUsernameStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUsernameStmt.executeQuery();
            while(rs.next())
            {
                User user = new User();
                user.setId(rs.getString(1));
                user.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                user.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                user.setUsername(rs.getString(4));
                user.setEmail(rs.getString(5));
                user.setFirstName(rs.getString(6));
                user.setLastName(rs.getString(7));
                user.setRole(rs.getString(8));
                user.setTimezone(rs.getString(9));
                user.setAdmin(rs.getBoolean(10));
                user.setStatus(rs.getString(11));
                ret = user;
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
     * Returns a user from the USERS table by email.
     */
    public synchronized User getByEmail(String email) throws SQLException
    {
        User ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByEmailStmt == null)
            getByEmailStmt = prepareStatement(getConnection(), GET_BY_EMAIL_SQL);
        clearParameters(getByEmailStmt);

        ResultSet rs = null;

        try
        {
            getByEmailStmt.setString(1, email);
            getByEmailStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByEmailStmt.executeQuery();
            while(rs.next())
            {
                User user = new User();
                user.setId(rs.getString(1));
                user.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                user.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                user.setUsername(rs.getString(4));
                user.setEmail(rs.getString(5));
                user.setFirstName(rs.getString(6));
                user.setLastName(rs.getString(7));
                user.setRole(rs.getString(8));
                user.setTimezone(rs.getString(9));
                user.setAdmin(rs.getBoolean(10));
                user.setStatus(rs.getString(11));
                ret = user;
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
     * Stores the given user in the USERS table.
     */
    public synchronized void add(User user) throws SQLException
    {
        if(!hasConnection() || user == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, user.getId());
            insertStmt.setTimestamp(2, new Timestamp(user.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(user.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, user.getUsername());
            insertStmt.setString(5, user.getEmail());
            insertStmt.setString(6, user.getFirstName());
            insertStmt.setString(7, user.getLastName());
            insertStmt.setString(8, user.getRole());
            insertStmt.setString(9, user.getTimezone());
            insertStmt.setBoolean(10, user.isAdmin());
            insertStmt.setString(11, user.getStatus().name());
            insertStmt.executeUpdate();

            logger.info("Created user '"+user.getId()+"' in USERS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the user already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given user in the USERS table.
     */
    public synchronized void update(User user) throws SQLException
    {
        if(!hasConnection() || user == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(user.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, user.getUsername());
        updateStmt.setString(3, user.getEmail());
        updateStmt.setString(4, user.getFirstName());
        updateStmt.setString(5, user.getLastName());
        updateStmt.setString(6, user.getRole());
        updateStmt.setString(7, user.getTimezone());
        updateStmt.setBoolean(8, user.isAdmin());
        updateStmt.setString(9, user.getStatus().name());
        updateStmt.setString(10, user.getId());
        updateStmt.executeUpdate();

        logger.info("Updated user '"+user.getId()+"' in USERS");
    }

    /**
     * Adds or Updates the given user in the USERS table.
     */
    public boolean upsert(User user) throws SQLException
    {
        boolean ret = false;

        User existing = getById(user.getId());
        if(existing != null)
        {
            update(user);
        }
        else
        {
            add(user);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the users from the USERS table.
     */
    public synchronized List<User> list() throws SQLException
    {
        List<User> ret = null;

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
            ret = new ArrayList<User>();
            while(rs.next())
            {
                User user = new User();
                user.setId(rs.getString(1));
                user.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                user.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                user.setUsername(rs.getString(4));
                user.setEmail(rs.getString(5));
                user.setFirstName(rs.getString(6));
                user.setLastName(rs.getString(7));
                user.setRole(rs.getString(8));
                user.setTimezone(rs.getString(9));
                user.setAdmin(rs.getBoolean(10));
                user.setStatus(rs.getString(11));
                ret.add(user);
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
     * Returns the count of users from the table.
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
     * Removes the given user from the USERS table.
     */
    public synchronized void delete(User user) throws SQLException
    {
        if(!hasConnection() || user == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, user.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted user '"+user.getId()+"' in USERS");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByUsernameStmt);
        getByUsernameStmt = null;
        closeStatement(getByEmailStmt);
        getByEmailStmt = null;
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
    private PreparedStatement getByUsernameStmt;
    private PreparedStatement getByEmailStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
