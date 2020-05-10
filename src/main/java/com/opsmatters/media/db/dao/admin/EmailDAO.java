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
import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.DeliveryStatus;
import com.opsmatters.media.model.admin.Email;

/**
 * DAO that provides operations on the EMAILS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EmailDAO extends AdminDAO<Email>
{
    private static final Logger logger = Logger.getLogger(EmailDAO.class.getName());

    /**
     * The query to use to select an email from the EMAILS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SUBJECT, ATTRIBUTES, PROVIDER, MESSAGE_ID, STATUS "
      + "FROM EMAILS WHERE ID=?";

    /**
     * The query to use to insert a email into the EMAILS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO EMAILS"
      + "( ID, CREATED_DATE, UPDATED_DATE, SUBJECT, ATTRIBUTES, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a email in the EMAILS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE EMAILS SET UPDATED_DATE=?, SUBJECT=?, ATTRIBUTES=?, PROVIDER=?, MESSAGE_ID=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the EMAILS from the EMAILS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SUBJECT, ATTRIBUTES, PROVIDER, MESSAGE_ID, STATUS "
      + "FROM EMAILS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the EMAILS from the EMAILS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SUBJECT, ATTRIBUTES, PROVIDER, MESSAGE_ID, STATUS "
      + "FROM EMAILS WHERE STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of emails from the EMAILS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM EMAILS";

    /**
     * The query to use to delete an email from the EMAILS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM EMAILS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public EmailDAO(AdminDAOFactory factory)
    {
        super(factory, "EMAILS");
    }

    /**
     * Defines the columns and indices for the EMAILS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SUBJECT", Types.VARCHAR, 128, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("PROVIDER", Types.VARCHAR, 15, false);
        table.addColumn("MESSAGE_ID", Types.VARCHAR, 60, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("EMAILS_PK", new String[] {"ID"});
        table.addIndex("EMAILS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a email from the EMAILS table by id.
     */
    public Email getById(String id) throws SQLException
    {
        Email ret = null;

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
                Email email = new Email();
                email.setId(rs.getString(1));
                email.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                email.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                email.setSubject(rs.getString(4));
                email.setAttributes(new JSONObject(getClob(rs, 5)));
                email.setProvider(rs.getString(6));
                email.setMessageId(rs.getString(7));
                email.setStatus(rs.getString(8));
                ret = email;
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
     * Stores the given email in the EMAILS table.
     */
    public void add(Email email) throws SQLException
    {
        if(!hasConnection() || email == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, email.getId());
            insertStmt.setTimestamp(2, new Timestamp(email.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(email.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, email.getSubject());
            String attributes = email.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(5, reader, attributes.length());
            insertStmt.setString(6, email.getStatus().name());
            insertStmt.executeUpdate();

            logger.info("Created email '"+email.getId()+"' in EMAILS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the email already exists
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
     * Updates the given email in the EMAILS table.
     */
    public void update(Email email) throws SQLException
    {
        if(!hasConnection() || email == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(email.getUpdatedDateMillis()), UTC);
            updateStmt.setString(2, email.getSubject());
            String attributes = email.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(3, reader, attributes.length());
            updateStmt.setString(4, email.getProvider() != null ? email.getProvider().name() : null);
            updateStmt.setString(5, email.getMessageId());
            updateStmt.setString(6, email.getStatus().name());
            updateStmt.setString(7, email.getId());
            updateStmt.executeUpdate();

            logger.info("Updated email '"+email.getId()+"' in EMAILS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the emails from the EMAILS table.
     */
    public List<Email> list() throws SQLException
    {
        List<Email> ret = null;

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
            ret = new ArrayList<Email>();
            while(rs.next())
            {
                Email email = new Email();
                email.setId(rs.getString(1));
                email.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                email.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                email.setSubject(rs.getString(4));
                email.setAttributes(new JSONObject(getClob(rs, 5)));
                email.setProvider(rs.getString(6));
                email.setMessageId(rs.getString(7));
                email.setStatus(rs.getString(8));
                ret.add(email);
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
     * Returns the emails from the EMAILS table by status.
     */
    public List<Email> list(DeliveryStatus status) throws SQLException
    {
        List<Email> ret = null;

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
            ret = new ArrayList<Email>();
            while(rs.next())
            {
                Email email = new Email();
                email.setId(rs.getString(1));
                email.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                email.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                email.setSubject(rs.getString(4));
                email.setAttributes(new JSONObject(getClob(rs, 5)));
                email.setProvider(rs.getString(6));
                email.setMessageId(rs.getString(7));
                email.setStatus(rs.getString(8));
                ret.add(email);
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
     * Returns the count of emails from the table.
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
     * Removes the given email from the EMAILS table.
     */
    public void delete(Email email) throws SQLException
    {
        if(!hasConnection() || email == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, email.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted email '"+email.getId()+"' in EMAILS");
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
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
