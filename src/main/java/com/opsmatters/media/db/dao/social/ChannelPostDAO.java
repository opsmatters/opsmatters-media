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
package com.opsmatters.media.db.dao.social;

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
import com.opsmatters.media.cache.social.SocialChannels;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.DeliveryStatus;
import com.opsmatters.media.model.social.ChannelPost;
import com.opsmatters.media.model.social.DraftPost;
import com.opsmatters.media.model.social.MessageFormat;
import com.opsmatters.media.model.social.SocialChannel;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the CHANNEL_POSTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChannelPostDAO extends SocialDAO<ChannelPost>
{
    private static final Logger logger = Logger.getLogger(ChannelPostDAO.class.getName());

    private static final int MAX_ERROR_MESSAGE = 256;

    /**
     * The query to use to select a post from the CHANNEL_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE ID=?";

    /**
     * The query to use to insert a post into the CHANNEL_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CHANNEL_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post in the CHANNEL_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CHANNEL_POSTS SET UPDATED_DATE=?, ATTRIBUTES=?, STATUS=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE (CREATED_DATE >= (NOW() + INTERVAL -? DAY) OR STATUS IN ('NEW','WAITING','ERROR','PAUSED')) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table by status and interval.
     */
    private static final String LIST_BY_STATUS_INTERVAL_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE STATUS=? AND CREATED_DATE >= (NOW() + INTERVAL -? DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE SITE_ID=? AND CREATED_DATE >= (NOW() + INTERVAL -? DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table by organisation.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE SITE_ID=? AND CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table by draft_id.
     */
    private static final String LIST_BY_DRAFT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE DRAFT_ID=?";

    /**
     * The query to use to select the posts from the CHANNEL_POSTS table by channel and status for the current session.
     */
    private static final String LIST_BY_CHANNEL_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, DRAFT_ID, CHANNEL, "
      + "CODE, CONTENT_TYPE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM CHANNEL_POSTS WHERE CHANNEL=? AND STATUS=? AND SESSION_ID=?";

    /**
     * The query to use to get the count of posts from the CHANNEL_POSTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CHANNEL_POSTS";

    /**
     * The query to use to delete a post from the CHANNEL_POSTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CHANNEL_POSTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ChannelPostDAO(SocialDAOFactory factory)
    {
        super(factory, "CHANNEL_POSTS");
    }

    /**
     * Defines the columns and indices for the CHANNEL_POSTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("DRAFT_ID", Types.VARCHAR, 36, true);
        table.addColumn("CHANNEL", Types.VARCHAR, 15, true);
        table.addColumn("CODE", Types.VARCHAR, 5, false);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("CHANNEL_POSTS_PK", new String[] {"ID"});
        table.addIndex("CHANNEL_POSTS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("CHANNEL_POSTS_DRAFT_IDX", new String[] {"DRAFT_ID"});
        table.addIndex("CHANNEL_POSTS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a post from the CHANNEL_POSTS table by id.
     */
    public synchronized ChannelPost getById(String id) throws SQLException
    {
        ChannelPost ret = null;

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
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret = post;
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
     * Stores the given post in the CHANNEL_POSTS table.
     */
    public synchronized void add(ChannelPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, post.getId());
            insertStmt.setTimestamp(2, new Timestamp(post.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(post.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, post.getType().name());
            insertStmt.setString(5, post.getSiteId());
            insertStmt.setString(6, post.getDraftId());
            insertStmt.setString(7, post.getChannel().getId());
            insertStmt.setString(8, post.getCode());
            insertStmt.setString(9, post.getContentType() != null ? post.getContentType().name() : "");
            String attributes = post.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(10, reader, attributes.length());
            insertStmt.setString(11, post.getStatus().name());
            insertStmt.setString(12, post.getCreatedBy());
            insertStmt.setInt(13, SessionId.get());
            insertStmt.executeUpdate();

            logger.info("Created post '"+post.getId()+"' in CHANNEL_POSTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the post already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given post in the CHANNEL_POSTS table.
     */
    public synchronized void update(ChannelPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(post.getUpdatedDateMillis()), UTC);
            String attributes = post.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(2, reader, attributes.length());
            updateStmt.setString(3, post.getStatus().name());
            updateStmt.setInt(4, SessionId.get());
            updateStmt.setString(5, post.getId());
            updateStmt.executeUpdate();

            logger.info("Updated post '"+post.getId()+"' in CHANNEL_POSTS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the posts from the CHANNEL_POSTS table.
     */
    public synchronized List<ChannelPost> list(int interval) throws SQLException
    {
        List<ChannelPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setInt(1, interval);
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the posts from the CHANNEL_POSTS table by status.
     */
    public synchronized List<ChannelPost> list(DeliveryStatus status) throws SQLException
    {
        List<ChannelPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByStatusStmt == null)
            listByStatusStmt = prepareStatement(getConnection(), LIST_BY_STATUS_SQL);
        clearParameters(listByStatusStmt);

        ResultSet rs = null;

        try
        {
            listByStatusStmt.setString(1, status != null ? status.name() : "");
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the posts from the CHANNEL_POSTS table by status and interval.
     */
    public synchronized List<ChannelPost> list(DeliveryStatus status, int interval) throws SQLException
    {
        List<ChannelPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByStatusIntervalStmt == null)
            listByStatusIntervalStmt = prepareStatement(getConnection(), LIST_BY_STATUS_INTERVAL_SQL);
        clearParameters(listByStatusIntervalStmt);

        ResultSet rs = null;

        try
        {
            listByStatusIntervalStmt.setString(1, status != null ? status.name() : "");
            listByStatusIntervalStmt.setInt(2, interval);
            listByStatusIntervalStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusIntervalStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the posts from the CHANNEL_POSTS table by site.
     */
    public synchronized List<ChannelPost> list(Site site, int interval) throws SQLException
    {
        List<ChannelPost> ret = null;

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
            listBySiteStmt.setInt(2, interval);
            listBySiteStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySiteStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the posts from the CHANNEL_POSTS table by organisation.
     */
    public synchronized List<ChannelPost> list(Site site, String code) throws SQLException
    {
        List<ChannelPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, site.getId());
            listByCodeStmt.setString(2, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the posts from the CHANNEL_POSTS table by draft post id.
     */
    public synchronized List<ChannelPost> list(DraftPost draft) throws SQLException
    {
        List<ChannelPost> ret = null;

        if(!hasConnection() || draft == null || !draft.hasId())
            return ret;

        preQuery();
        if(listByDraftStmt == null)
            listByDraftStmt = prepareStatement(getConnection(), LIST_BY_DRAFT_SQL);
        clearParameters(listByDraftStmt);

        ResultSet rs = null;

        try
        {
            listByDraftStmt.setString(1, draft.getId());
            listByDraftStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByDraftStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the posts from the CHANNEL_POSTS table by channel and status for the current session.
     */
    public synchronized List<ChannelPost> list(SocialChannel channel, DeliveryStatus status) throws SQLException
    {
        List<ChannelPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByChannelStmt == null)
            listByChannelStmt = prepareStatement(getConnection(), LIST_BY_CHANNEL_SQL);
        clearParameters(listByChannelStmt);

        ResultSet rs = null;

        try
        {
            listByChannelStmt.setString(1, channel.getId());
            listByChannelStmt.setString(2, status != null ? status.name() : "");
            listByChannelStmt.setInt(3, SessionId.get());
            listByChannelStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByChannelStmt.executeQuery();
            ret = new ArrayList<ChannelPost>();
            while(rs.next())
            {
                ChannelPost post = new ChannelPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setSiteId(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setChannel(SocialChannels.getChannel(rs.getString(7)));
                post.setCode(rs.getString(8));
                post.setContentType(rs.getString(9));
                post.setAttributes(new JSONObject(getClob(rs, 10)));
                post.setStatus(rs.getString(11));
                post.setCreatedBy(rs.getString(12));
                ret.add(post);
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
     * Returns the count of posts from the table.
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
     * Removes the given post from the CHANNEL_POSTS table.
     */
    public synchronized void delete(ChannelPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, post.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted post '"+post.getId()+"' in CHANNEL_POSTS");
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
        closeStatement(listByStatusIntervalStmt);
        listByStatusIntervalStmt = null;
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByDraftStmt);
        listByDraftStmt = null;
        closeStatement(listByChannelStmt);
        listByChannelStmt = null;
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
    private PreparedStatement listByStatusIntervalStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByDraftStmt;
    private PreparedStatement listByChannelStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}