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
package com.opsmatters.media.db.dao.order.contact;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.order.contact.Contact;
import com.opsmatters.media.model.order.contact.ContactPerson;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.order.OrderDAOFactory;

/**
 * DAO that provides operations on the CONTACT_PERSONS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContactPersonDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContactPersonDAO.class.getName());

    /**
     * The query to use to select a person from the CONTACT_PERSONS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, EMAIL, ENABLED  "
      + "FROM CONTACT_PERSONS WHERE ID=?";

    /**
     * The query to use to insert a person into the CONTACT_PERSONS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTACT_PERSONS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, EMAIL, ENABLED )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a person in the CONTACT_PERSONS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTACT_PERSONS SET UPDATED_DATE=?, NAME=?, EMAIL=?, ENABLED=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the persons from the CONTACT_PERSONS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, EMAIL, ENABLED  "
      + "FROM CONTACT_PERSONS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the persons from the CONTACT_PERSONS table by contact.
     */
    private static final String LIST_BY_CONTACT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CONTACT_ID, NAME, EMAIL, ENABLED  "
      + "FROM CONTACT_PERSONS WHERE CONTACT_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of persons from the CONTACT_PERSONS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTACT_PERSONS";

    /**
     * The query to use to delete a person from the CONTACT_PERSONS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTACT_PERSONS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContactPersonDAO(OrderDAOFactory factory)
    {
        super(factory, "CONTACT_PERSONS");
    }

    /**
     * Defines the columns and indices for the CONTACT_PERSONS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CONTACT_ID", Types.VARCHAR, 36, true);
        table.addColumn("NAME", Types.VARCHAR, 50, true);
        table.addColumn("EMAIL", Types.VARCHAR, 50, true);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.setPrimaryKey("CONTACT_PERSONS_PK", new String[] {"ID"});
        table.addIndex("CONTACT_PERSONS_CONTACT_IDX", new String[] {"CONTACT_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a person from the CONTACT_PERSONS table by id.
     */
    public synchronized ContactPerson getById(String id) throws SQLException
    {
        ContactPerson ret = null;

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
                ContactPerson person = new ContactPerson();
                person.setId(rs.getString(1));
                person.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                person.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                person.setContactId(rs.getString(4));
                person.setName(rs.getString(5));
                person.setEmail(rs.getString(6));
                person.setEnabled(rs.getBoolean(7));
                ret = person;
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
     * Stores the given person in the CONTACT_PERSONS table.
     */
    public synchronized void add(ContactPerson person) throws SQLException
    {
        if(!hasConnection() || person == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, person.getId());
            insertStmt.setTimestamp(2, new Timestamp(person.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(person.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, person.getContactId());
            insertStmt.setString(5, person.getName());
            insertStmt.setString(6, person.getEmail());
            insertStmt.setBoolean(7, person.isEnabled());
            insertStmt.executeUpdate();

            logger.info("Created contact person '"+person.getId()+"' in CONTACT_PERSONS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the contact rate  exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given person in the CONTACT_PERSONS table.
     */
    public synchronized void update(ContactPerson person) throws SQLException
    {
        if(!hasConnection() || person == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(person.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, person.getName());
        updateStmt.setString(3, person.getEmail());
        updateStmt.setBoolean(4, person.isEnabled());
        updateStmt.setString(5, person.getId());
        updateStmt.executeUpdate();

        logger.info("Updated contact person '"+person.getId()+"' in CONTACT_PERSONS");
    }

    /**
     * Returns the persons from the CONTACT_PERSONS table.
     */
    public synchronized List<ContactPerson> list() throws SQLException
    {
        List<ContactPerson> ret = null;

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
            ret = new ArrayList<ContactPerson>();
            while(rs.next())
            {
                ContactPerson person = new ContactPerson();
                person.setId(rs.getString(1));
                person.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                person.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                person.setContactId(rs.getString(4));
                person.setName(rs.getString(5));
                person.setEmail(rs.getString(6));
                person.setEnabled(rs.getBoolean(7));
                ret.add(person);
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
     * Returns the persons from the CONTACT_PERSONS table by contact.
     */
    public synchronized List<ContactPerson> list(String contactId) throws SQLException
    {
        List<ContactPerson> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContactStmt == null)
            listByContactStmt = prepareStatement(getConnection(), LIST_BY_CONTACT_SQL);
        clearParameters(listByContactStmt);

        ResultSet rs = null;

        try
        {
            listByContactStmt.setString(1, contactId);
            listByContactStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContactStmt.executeQuery();
            ret = new ArrayList<ContactPerson>();
            while(rs.next())
            {
                ContactPerson person = new ContactPerson();
                person.setId(rs.getString(1));
                person.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                person.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                person.setContactId(rs.getString(4));
                person.setName(rs.getString(5));
                person.setEmail(rs.getString(6));
                person.setEnabled(rs.getBoolean(7));
                ret.add(person);
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
     * Returns the count of persons from the CONTACT_PERSONS table.
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
     * Removes the given person from the CONTACT_PERSONS table.
     */
    public synchronized void delete(ContactPerson person) throws SQLException
    {
        if(!hasConnection() || person == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, person.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted contact person '"+person.getId()+"' in CONTACT_PERSONS");
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
        closeStatement(listByContactStmt);
        listByContactStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByContactStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
