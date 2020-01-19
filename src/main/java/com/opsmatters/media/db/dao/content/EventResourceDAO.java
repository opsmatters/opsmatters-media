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
package com.opsmatters.media.db.dao.content;

import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.content.EventResource;

/**
 * DAO that provides operations on the EVENTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventResourceDAO extends ContentDAO<EventResource>
{
    private static final Logger logger = Logger.getLogger(EventResourceDAO.class.getName());

    /**
     * The query to use to select a event from the EVENTS table by URL.
     */
    private static final String GET_BY_URL_SQL =  
      "SELECT ATTRIBUTES FROM EVENTS WHERE CODE=? AND URL=? AND (?=0 OR ABS(TIMESTAMPDIFF(DAY, ?, START_DATE)) < 2)";

    /**
     * The query to use to insert an event into the EVENTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO EVENTS"
      + "( CODE, ID, PUBLISHED_DATE, START_DATE, UUID, URL, ACTIVITY_TYPE, "
      + "PUBLISHED, CREATED_BY, ATTRIBUTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a event in the EVENTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE EVENTS SET PUBLISHED_DATE=?, START_DATE=?, UUID=?, URL=?, ACTIVITY_TYPE=?, "
      + "PUBLISHED=?, ATTRIBUTES=? "
      + "WHERE CODE=? AND ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public EventResourceDAO(ContentDAOFactory factory)
    {
        super(factory, "EVENTS");
    }

    /**
     * Defines the columns and indices for the EVENTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("START_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("URL", Types.VARCHAR, 512, true);
        table.addColumn("ACTIVITY_TYPE", Types.VARCHAR, 30, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("EVENTS_PK", new String[] {"CODE","ID"});
        table.addIndex("EVENTS_UUID_IDX", new String[] {"CODE","UUID"});
        table.addIndex("EVENTS_URL_IDX", new String[] {"CODE","URL"});
        table.setInitialised(true);
    }

    /**
     * Returns a event from the EVENTS table by URL.
     */
    public EventResource getByUrl(String code, String url, long startDate) throws SQLException
    {
        EventResource ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUrlStmt == null)
            getByUrlStmt = prepareStatement(getConnection(), GET_BY_URL_SQL);
        clearParameters(getByUrlStmt);

        ResultSet rs = null;

        try
        {
            getByUrlStmt.setString(1, code);
            getByUrlStmt.setString(2, url);
            getByUrlStmt.setLong(3, startDate);
            getByUrlStmt.setTimestamp(4, new Timestamp(startDate), UTC);
            getByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUrlStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = new EventResource(attributes);
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
     * Stores the given event in the EVENTS table.
     */
    public void add(EventResource content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("event uuid null");

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, content.getCode());
            insertStmt.setInt(2, content.getId());
            insertStmt.setTimestamp(3, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(content.getStartDateMillis()), UTC);
            insertStmt.setString(5, content.getUuid());
            insertStmt.setString(6, content.getUrl());
            insertStmt.setString(7, content.getActivityType());
            insertStmt.setBoolean(8, content.isPublished());
            insertStmt.setString(9, content.getCreatedBy());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(10, reader, attributes.length());
            insertStmt.executeUpdate();

            logger.info(String.format("Created %s '%s' in %s (GUID=%s)", 
                content.getType().value(), content.getTitle(), getTableName(), content.getGuid()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the event already exists
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
     * Updates the given event in the EVENTS table.
     */
    public void update(EventResource content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("event uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setTimestamp(2, new Timestamp(content.getStartDateMillis()), UTC);
            updateStmt.setString(3, content.getUuid());
            updateStmt.setString(4, content.getUrl());
            updateStmt.setString(5, content.getActivityType());
            updateStmt.setBoolean(6, content.isPublished());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(7, reader, attributes.length());
            updateStmt.setString(8, content.getCode());
            updateStmt.setInt(9, content.getId());
            updateStmt.executeUpdate();

            logger.info(String.format("Updated %s '%s' in %s (GUID=%s)", 
                content.getType().value(), content.getTitle(), getTableName(), content.getGuid()));
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByUrlStmt);
        getByUrlStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
    }

    private PreparedStatement getByUrlStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
}
