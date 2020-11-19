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

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.DeliveryStatus;
import com.opsmatters.media.model.social.PreparedPost;
import com.opsmatters.media.model.social.DraftPost;
import com.opsmatters.media.model.social.SocialChannels;
import com.opsmatters.media.model.social.MessageFormat;

/**
 * DAO that provides operations on the PREPARED_POSTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PreparedPostDAO extends SocialDAO<PreparedPost>
{
    private static final Logger logger = Logger.getLogger(PreparedPostDAO.class.getName());

    /**
     * The query to use to select a post from the PREPARED_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, DRAFT_ID, CODE, "
      + "TITLE, MESSAGE, CHANNEL, STATUS, EXTERNAL_ID, ERROR_CODE, ERROR_MESSAGE, CREATED_BY "
      + "FROM PREPARED_POSTS WHERE ID=?";

    /**
     * The query to use to insert a post into the PREPARED_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PREPARED_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, DRAFT_ID, CODE, "
      + "TITLE, MESSAGE, CHANNEL, STATUS, EXTERNAL_ID, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post in the PREPARED_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PREPARED_POSTS SET UPDATED_DATE=?, SCHEDULED_DATE=?, CODE=?, "
      + "TITLE=?, MESSAGE=?, STATUS=?, EXTERNAL_ID=?, ERROR_CODE=?, ERROR_MESSAGE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the posts from the PREPARED_POSTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, DRAFT_ID, CODE, "
      + "TITLE, MESSAGE, CHANNEL, STATUS, EXTERNAL_ID, ERROR_CODE, ERROR_MESSAGE, CREATED_BY "
      + "FROM PREPARED_POSTS WHERE CREATED_DATE >= (NOW() + INTERVAL -? DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the PREPARED_POSTS table.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, DRAFT_ID, CODE, "
      + "TITLE, MESSAGE, CHANNEL, STATUS, EXTERNAL_ID, ERROR_CODE, ERROR_MESSAGE, CREATED_BY "
      + "FROM PREPARED_POSTS WHERE STATUS=? AND CREATED_DATE >= (NOW() + INTERVAL -? DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the PREPARED_POSTS table.
     */
    private static final String LIST_BY_DRAFT_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, DRAFT_ID, CODE, "
      + "TITLE, MESSAGE, CHANNEL, STATUS, EXTERNAL_ID, ERROR_CODE, ERROR_MESSAGE, CREATED_BY "
      + "FROM PREPARED_POSTS WHERE DRAFT_ID=?";

    /**
     * The query to use to get the count of posts from the PREPARED_POSTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM PREPARED_POSTS";

    /**
     * The query to use to delete a post from the PREPARED_POSTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM PREPARED_POSTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public PreparedPostDAO(SocialDAOFactory factory)
    {
        super(factory, "PREPARED_POSTS");
    }

    /**
     * Defines the columns and indices for the PREPARED_POSTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SCHEDULED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("DRAFT_ID", Types.VARCHAR, 36, true);
        table.addColumn("CODE", Types.VARCHAR, 5, false);
        table.addColumn("TITLE", Types.VARCHAR, 192, false);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("CHANNEL", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("EXTERNAL_ID", Types.VARCHAR, 36, false);
        table.addColumn("ERROR_CODE", Types.INTEGER, false);
        table.addColumn("ERROR_MESSAGE", Types.VARCHAR, 256, false);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("PREPARED_POSTS_PK", new String[] {"ID"});
        table.addIndex("PREPARED_POSTS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("PREPARED_POSTS_DRAFT_IDX", new String[] {"DRAFT_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a post from the PREPARED_POSTS table by id.
     */
    public PreparedPost getById(String id) throws SQLException
    {
        PreparedPost ret = null;

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
                PreparedPost post = new PreparedPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setType(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setCode(rs.getString(7));
                post.setTitle(rs.getString(8));
                post.setMessage(rs.getString(9));
                post.setChannel(SocialChannels.get(rs.getString(10)));
                post.setStatus(rs.getString(11));
                post.setExternalId(rs.getString(12));
                post.setErrorCode(rs.getInt(13));
                post.setErrorMessage(rs.getString(14));
                post.setCreatedBy(rs.getString(15));
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
     * Stores the given post in the PREPARED_POSTS table.
     */
    public void add(PreparedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, post.getId());
            insertStmt.setTimestamp(2, new Timestamp(post.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(post.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(post.getScheduledDateMillis()), UTC);
            insertStmt.setString(5, post.getType().name());
            insertStmt.setString(6, post.getDraftId());
            insertStmt.setString(7, post.getCode());
            insertStmt.setString(8, post.getTitle());
            insertStmt.setString(9, post.getMessage(MessageFormat.ENCODED));
            insertStmt.setString(10, post.getChannel().getName());
            insertStmt.setString(11, post.getStatus().name());
            insertStmt.setString(12, post.getExternalId());
            insertStmt.setString(13, post.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created post '"+post.getId()+"' in PREPARED_POSTS");
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
     * Updates the given post in the PREPARED_POSTS table.
     */
    public void update(PreparedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(post.getUpdatedDateMillis()), UTC);
        updateStmt.setTimestamp(2, new Timestamp(post.getScheduledDateMillis()), UTC);
        updateStmt.setString(3, post.getCode());
        updateStmt.setString(4, post.getTitle());
        updateStmt.setString(5, post.getMessage(MessageFormat.ENCODED));
        updateStmt.setString(6, post.getStatus().name());
        updateStmt.setString(7, post.getExternalId());
        updateStmt.setInt(8, post.getErrorCode());
        updateStmt.setString(9, post.getErrorMessage());
        updateStmt.setString(10, post.getId());
        updateStmt.executeUpdate();

        logger.info("Updated post '"+post.getId()+"' in PREPARED_POSTS");
    }

    /**
     * Returns the posts from the PREPARED_POSTS table.
     */
    public List<PreparedPost> list(int interval) throws SQLException
    {
        List<PreparedPost> ret = null;

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
            ret = new ArrayList<PreparedPost>();
            while(rs.next())
            {
                PreparedPost post = new PreparedPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setType(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setCode(rs.getString(7));
                post.setTitle(rs.getString(8));
                post.setMessage(rs.getString(9));
                post.setChannel(SocialChannels.get(rs.getString(10)));
                post.setStatus(rs.getString(11));
                post.setExternalId(rs.getString(12));
                post.setErrorCode(rs.getInt(13));
                post.setErrorMessage(rs.getString(14));
                post.setCreatedBy(rs.getString(15));
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
     * Returns the posts from the PREPARED_POSTS table by status.
     */
    public List<PreparedPost> list(DeliveryStatus status, int interval) throws SQLException
    {
        List<PreparedPost> ret = null;

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
            listByStatusStmt.setInt(2, interval);
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<PreparedPost>();
            while(rs.next())
            {
                PreparedPost post = new PreparedPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setType(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setCode(rs.getString(7));
                post.setTitle(rs.getString(8));
                post.setMessage(rs.getString(9));
                post.setChannel(SocialChannels.get(rs.getString(10)));
                post.setStatus(rs.getString(11));
                post.setExternalId(rs.getString(12));
                post.setErrorCode(rs.getInt(13));
                post.setErrorMessage(rs.getString(14));
                post.setCreatedBy(rs.getString(15));
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
     * Returns the posts from the PREPARED_POSTS table by draft post id.
     */
    public List<PreparedPost> list(DraftPost draft) throws SQLException
    {
        List<PreparedPost> ret = null;

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
            ret = new ArrayList<PreparedPost>();
            while(rs.next())
            {
                PreparedPost post = new PreparedPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setType(rs.getString(5));
                post.setDraftId(rs.getString(6));
                post.setCode(rs.getString(7));
                post.setTitle(rs.getString(8));
                post.setMessage(rs.getString(9));
                post.setChannel(SocialChannels.get(rs.getString(10)));
                post.setStatus(rs.getString(11));
                post.setExternalId(rs.getString(12));
                post.setErrorCode(rs.getInt(13));
                post.setErrorMessage(rs.getString(14));
                post.setCreatedBy(rs.getString(15));
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
     * Removes the given post from the PREPARED_POSTS table.
     */
    public void delete(PreparedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, post.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted post '"+post.getId()+"' in PREPARED_POSTS");
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
        closeStatement(listByDraftStmt);
        listByDraftStmt = null;
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
    private PreparedStatement listByDraftStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}