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
package com.opsmatters.media.db.dao.content.event;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.event.Event;
import com.opsmatters.media.model.content.event.EventItem;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the EVENTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EventDAO extends ContentDAO<Event>
{
    private static final Logger logger = Logger.getLogger(EventDAO.class.getName());

    /**
     * The query to use to insert an event into the EVENTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO EVENTS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, START_DATE, TITLE, URL, EVENT_TYPE, TIMEZONE, PROVIDER, PUBLISHED, PROMOTE, "
      +   "STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a event in the EVENTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE EVENTS SET UUID=?, PUBLISHED_DATE=?, START_DATE=?, TITLE=?, URL=?, EVENT_TYPE=?, TIMEZONE=?, PROVIDER=?, PUBLISHED=?, PROMOTE=?, STATUS=?, CREATED_BY=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select a list of events from the EVENTS table by URL.
     */
    private static final String LIST_BY_URL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM EVENTS WHERE CODE=? AND URL=? AND (?=0 OR ABS(TIMESTAMPDIFF(DAY, ?, START_DATE)) < 2)";

    /**
     * The query to use to select the event items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, START_DATE, TITLE, URL, EVENT_TYPE, TIMEZONE, PUBLISHED, PROMOTE, STATUS "
      + "FROM EVENTS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * Constructor that takes a DAO factory.
     */
    public EventDAO(ContentDAOFactory factory)
    {
        super(factory, "EVENTS");
    }

    /**
     * Defines the columns and indices for the EVENTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("START_DATE", Types.TIMESTAMP, true);
        table.addColumn("TITLE", Types.VARCHAR, 256, true);
        table.addColumn("URL", Types.VARCHAR, 512, true);
        table.addColumn("EVENT_TYPE", Types.VARCHAR, 30, true);
        table.addColumn("TIMEZONE", Types.VARCHAR, 8, true);
        table.addColumn("PROVIDER", Types.VARCHAR, 15, false);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("EVENTS_PK", new String[] {"UUID"});
        table.addIndex("EVENTS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("EVENTS_TITLE_IDX", new String[] {"SITE_ID","CODE","TITLE"});
        table.addIndex("EVENTS_URL_IDX", new String[] {"SITE_ID","CODE","URL"});
        table.addIndex("EVENTS_CODE_URL_IDX", new String[] {"CODE","URL"});
        table.addIndex("EVENTS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("EVENTS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a list of events from the EVENTS table by URL.
     */
    public synchronized List<Event> listByUrl(String code, String url, long startDate) throws SQLException
    {
        List<Event> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByUrlStmt == null)
            listByUrlStmt = prepareStatement(getConnection(), LIST_BY_URL_SQL);
        clearParameters(listByUrlStmt);

        ResultSet rs = null;

        try
        {
            listByUrlStmt.setString(1, code);
            listByUrlStmt.setString(2, url);
            listByUrlStmt.setLong(3, startDate);
            listByUrlStmt.setTimestamp(4, new Timestamp(startDate), UTC);
            listByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByUrlStmt.executeQuery();
            ret = new ArrayList<Event>();
            while(rs.next())
            {
                Event content = new Event();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setAttributes(new JSONObject(getClob(rs, 6)));
                content.setStatus(rs.getString(7));
                content.setCreatedBy(rs.getString(8));
                ret.add(content);
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
    public synchronized void add(Event content) throws SQLException
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
            insertStmt.setString(1, content.getUuid());
            insertStmt.setString(2, content.getSiteId());
            insertStmt.setString(3, content.getCode());
            insertStmt.setInt(4, content.getId());
            insertStmt.setTimestamp(5, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setTimestamp(6, new Timestamp(content.getStartDateMillis()), UTC);
            insertStmt.setString(7, content.getTitle());
            insertStmt.setString(8, content.getUrl());
            insertStmt.setString(9, content.getEventType());
            insertStmt.setString(10, content.getTimeZone());
            insertStmt.setString(11, content.getProvider());
            insertStmt.setBoolean(12, content.isPublished());
            insertStmt.setBoolean(13, content.isPromoted());
            insertStmt.setString(14, content.getStatus().name());
            insertStmt.setString(15, content.getCreatedBy());
            String attributes = content.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(16, reader, attributes.length());
            insertStmt.setInt(17, SessionId.get());
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
    public synchronized void update(Event content) throws SQLException
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
            updateStmt.setString(1, content.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setTimestamp(3, new Timestamp(content.getStartDateMillis()), UTC);
            updateStmt.setString(4, content.getTitle());
            updateStmt.setString(5, content.getUrl());
            updateStmt.setString(6, content.getEventType());
            updateStmt.setString(7, content.getTimeZone());
            updateStmt.setString(8, content.getProvider());
            updateStmt.setBoolean(9, content.isPublished());
            updateStmt.setBoolean(10, content.isPromoted());
            updateStmt.setString(11, content.getStatus().name());
            updateStmt.setString(12, content.getCreatedBy());
            String attributes = content.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(13, reader, attributes.length());
            updateStmt.setString(14, content.getSiteId());
            updateStmt.setString(15, content.getCode());
            updateStmt.setInt(16, content.getId());
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
     * Returns the event items from the table by organisation code.
     */
    public synchronized List<EventItem> listItems(Site site, String code) throws SQLException
    {
        List<EventItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, site.getId());
            listByCodeStmt.setString(2, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<EventItem>();
            while(rs.next())
            {
                EventItem event = new EventItem();
                event.setUuid(rs.getString(1));
                event.setSiteId(rs.getString(2));
                event.setCode(rs.getString(3));
                event.setId(rs.getInt(4));
                event.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                event.setStartDateMillis(rs.getTimestamp(6, UTC).getTime());
                event.setTitle(rs.getString(7));
                event.setUrl(rs.getString(8));
                event.setEventType(rs.getString(9));
                event.setTimeZone(rs.getString(10));
                event.setPublished(rs.getBoolean(11));
                event.setPromoted(rs.getBoolean(12));
                event.setStatus(rs.getString(13));
                ret.add(event);
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
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(listByUrlStmt);
        listByUrlStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
    }

    private PreparedStatement listByUrlStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByCodeStmt;
}
