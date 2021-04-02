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
package com.opsmatters.media.db.dao.feed;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.platform.EnvironmentName;
import com.opsmatters.media.model.feed.ContentFeed;
import com.opsmatters.media.model.feed.FeedStatus;
import com.opsmatters.media.model.content.ContentType;

/**
 * DAO that provides operations on the CONTENT_FEEDS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentFeedDAO extends FeedDAO<ContentFeed>
{
    private static final Logger logger = Logger.getLogger(ContentFeedDAO.class.getName());

    /**
     * The query to use to select a feed from the CONTENT_FEEDS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, EXTERNAL_ID, CONTENT_TYPE, SITE_ID, ENV, STATUS, ITEM_COUNT "
      + "FROM CONTENT_FEEDS WHERE ID=?";

    /**
     * The query to use to insert a feed into the CONTENT_FEEDS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_FEEDS"
      + "( ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, EXTERNAL_ID, CONTENT_TYPE, SITE_ID, ENV, STATUS, ITEM_COUNT )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a feed in the CONTENT_FEEDS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_FEEDS SET UPDATED_DATE=?, EXECUTED_DATE=?, NAME=?, EXTERNAL_ID=?, STATUS=?, ITEM_COUNT=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the feeds from the CONTENT_FEEDS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, EXTERNAL_ID, CONTENT_TYPE, SITE_ID, ENV, STATUS, ITEM_COUNT "
      + "FROM CONTENT_FEEDS ORDER BY NAME";

    /**
     * The query to use to select the feeds from the CONTENT_FEEDS table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, EXTERNAL_ID, CONTENT_TYPE, SITE_ID, ENV, STATUS, ITEM_COUNT "
      + "FROM CONTENT_FEEDS WHERE SITE_ID=? ORDER BY NAME";

    /**
     * The query to use to select the feeds from the CONTENT_FEEDS table by content type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, EXTERNAL_ID, CONTENT_TYPE, SITE_ID, ENV, STATUS, ITEM_COUNT "
      + "FROM CONTENT_FEEDS WHERE SITE_ID=? AND CONTENT_TYPE=? ORDER BY NAME";

    /**
     * The query to use to select the feeds from the CONTENT_FEEDS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, EXECUTED_DATE, NAME, EXTERNAL_ID, CONTENT_TYPE, SITE_ID, ENV, STATUS, ITEM_COUNT "
      + "FROM CONTENT_FEEDS WHERE STATUS=? ORDER BY NAME";

    /**
     * The query to use to get the count of feeds from the CONTENT_FEEDS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_FEEDS";

    /**
     * The query to use to delete a feed from the CONTENT_FEEDS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_FEEDS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentFeedDAO(FeedDAOFactory factory)
    {
        super(factory, "CONTENT_FEEDS");
    }

    /**
     * Defines the columns and indices for the CONTENT_FEEDS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("EXECUTED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 25, true);
        table.addColumn("EXTERNAL_ID", Types.VARCHAR, 36, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("ENV", Types.VARCHAR, 10, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("ITEM_COUNT", Types.BIGINT, true);
        table.setPrimaryKey("CONTENT_FEEDS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_FEEDS_TYPE_IDX", new String[] {"SITE_ID","CONTENT_TYPE"});
        table.addIndex("CONTENT_FEEDS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a feed from the CONTENT_FEEDS table by id.
     */
    public ContentFeed getById(String id) throws SQLException
    {
        ContentFeed ret = null;

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
                ContentFeed feed = new ContentFeed();
                feed.setId(rs.getString(1));
                feed.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                feed.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                feed.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                feed.setName(rs.getString(5));
                feed.setExternalId(rs.getString(6));
                feed.setContentType(rs.getString(7));
                feed.setSiteId(rs.getString(8));
                feed.setEnvironment(rs.getString(9));
                feed.setStatus(rs.getString(10));
                feed.setItemCount(rs.getLong(11));
                ret = feed;
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
     * Stores the given feed in the CONTENT_FEEDS table.
     */
    public void add(ContentFeed feed) throws SQLException
    {
        if(!hasConnection() || feed == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, feed.getId());
            insertStmt.setTimestamp(2, new Timestamp(feed.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(feed.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(feed.getExecutedDateMillis()), UTC);
            insertStmt.setString(5, feed.getName());
            insertStmt.setString(6, feed.getExternalId());
            insertStmt.setString(7, feed.getContentType().name());
            insertStmt.setString(8, feed.getSiteId());
            insertStmt.setString(9, feed.getEnvironment().name());
            insertStmt.setString(10, feed.getStatus().name());
            insertStmt.setLong(11, feed.getItemCount());
            insertStmt.executeUpdate();

            logger.info("Created feed '"+feed.getId()+"' in CONTENT_FEEDS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the feed already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given feed in the CONTENT_FEEDS table.
     */
    public void update(ContentFeed feed) throws SQLException
    {
        if(!hasConnection() || feed == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(feed.getUpdatedDateMillis()), UTC);
        updateStmt.setTimestamp(2, new Timestamp(feed.getExecutedDateMillis()), UTC);
        updateStmt.setString(3, feed.getName());
        updateStmt.setString(4, feed.getExternalId());
        updateStmt.setString(5, feed.getStatus().name());
        updateStmt.setLong(6, feed.getItemCount());
        updateStmt.setString(7, feed.getId());
        updateStmt.executeUpdate();

        logger.info("Updated feed '"+feed.getId()+"' in CONTENT_FEEDS");
    }

    /**
     * Adds or Updates the given feed in the CONTENT_FEEDS table.
     */
    public boolean upsert(ContentFeed feed) throws SQLException
    {
        boolean ret = false;

        ContentFeed existing = getById(feed.getId());
        if(existing != null)
        {
            update(feed);
        }
        else
        {
            add(feed);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the feeds from the CONTENT_FEEDS table.
     */
    public List<ContentFeed> list() throws SQLException
    {
        List<ContentFeed> ret = null;

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
            ret = new ArrayList<ContentFeed>();
            while(rs.next())
            {
                ContentFeed feed = new ContentFeed();
                feed.setId(rs.getString(1));
                feed.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                feed.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                feed.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                feed.setName(rs.getString(5));
                feed.setExternalId(rs.getString(6));
                feed.setContentType(rs.getString(7));
                feed.setSiteId(rs.getString(8));
                feed.setEnvironment(rs.getString(9));
                feed.setStatus(rs.getString(10));
                feed.setItemCount(rs.getLong(11));
                ret.add(feed);
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
     * Returns the feeds from the CONTENT_FEEDS table by site.
     */
    public List<ContentFeed> list(Site site) throws SQLException
    {
        List<ContentFeed> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listBySiteStmt == null)
            listBySiteStmt = prepareStatement(getConnection(), LIST_BY_SITE_SQL);
        clearParameters(listBySiteStmt);

        ResultSet rs = null;

        try
        {
            listBySiteStmt.setString(1, site.getId());
            listBySiteStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySiteStmt.executeQuery();
            ret = new ArrayList<ContentFeed>();
            while(rs.next())
            {
                ContentFeed feed = new ContentFeed();
                feed.setId(rs.getString(1));
                feed.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                feed.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                feed.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                feed.setName(rs.getString(5));
                feed.setExternalId(rs.getString(6));
                feed.setContentType(rs.getString(7));
                feed.setSiteId(rs.getString(8));
                feed.setEnvironment(rs.getString(9));
                feed.setStatus(rs.getString(10));
                feed.setItemCount(rs.getLong(11));
                ret.add(feed);
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
     * Returns the feeds from the CONTENT_FEEDS table by content type.
     */
    public List<ContentFeed> list(Site site, ContentType type) throws SQLException
    {
        List<ContentFeed> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, site.getId());
            listByTypeStmt.setString(2, type.name());
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<ContentFeed>();
            while(rs.next())
            {
                ContentFeed feed = new ContentFeed();
                feed.setId(rs.getString(1));
                feed.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                feed.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                feed.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                feed.setName(rs.getString(5));
                feed.setExternalId(rs.getString(6));
                feed.setContentType(rs.getString(7));
                feed.setSiteId(rs.getString(8));
                feed.setEnvironment(rs.getString(9));
                feed.setStatus(rs.getString(10));
                feed.setItemCount(rs.getLong(11));
                ret.add(feed);
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
     * Returns the feeds from the CONTENT_FEEDS table by status.
     */
    public List<ContentFeed> list(FeedStatus status) throws SQLException
    {
        List<ContentFeed> ret = null;

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
            ret = new ArrayList<ContentFeed>();
            while(rs.next())
            {
                ContentFeed feed = new ContentFeed();
                feed.setId(rs.getString(1));
                feed.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                feed.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                feed.setExecutedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                feed.setName(rs.getString(5));
                feed.setExternalId(rs.getString(6));
                feed.setContentType(rs.getString(7));
                feed.setSiteId(rs.getString(8));
                feed.setEnvironment(rs.getString(9));
                feed.setStatus(rs.getString(10));
                feed.setItemCount(rs.getLong(11));
                ret.add(feed);
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
     * Returns the feeds from the CONTENT_FEEDS table by external id and environment.
     */
    public List<ContentFeed> list(String id, Site site, EnvironmentName environment) throws SQLException
    {
        List<ContentFeed> ret = new ArrayList<ContentFeed>();
        for(ContentFeed feed : list(site))
        {
              if(feed.getEnvironment() == environment
                && feed.getExternalId().equals(id))
            {
                ret.add(feed);
            }
        }

        return ret;
    }

    /**
     * Returns the feed from the CONTENT_FEEDS table in the given environment.
     */
    public ContentFeed getByEnvironment(Site site, ContentType type, EnvironmentName environment) throws SQLException
    {
        ContentFeed ret = null;
        for(ContentFeed feed : list(site, type))
        {
            if(feed.getEnvironment() == environment)
                ret = feed;
        }

        return ret;
    }

    /**
     * Returns the count of feeds from the CONTENT_FEEDS table.
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
     * Removes the given feed from the CONTENT_FEEDS table.
     */
    public void delete(ContentFeed feed) throws SQLException
    {
        if(!hasConnection() || feed == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, feed.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted feed '"+feed.getId()+"' in CONTENT_FEEDS");
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
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listByTypeStmt);
        listByTypeStmt = null;
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
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listByTypeStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
