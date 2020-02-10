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
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.social.DraftPost;
import com.opsmatters.media.model.social.DraftPostFactory;
import com.opsmatters.media.model.social.PostType;
import com.opsmatters.media.model.social.ContentPost;

/**
 * DAO that provides operations on the DRAFT_POSTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DraftPostDAO extends SocialDAO<DraftPost>
{
    private static final Logger logger = Logger.getLogger(DraftPostDAO.class.getName());

    /**
     * The query to use to select a post from the DRAFT_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, TEMPLATE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE ID=?";

    /**
     * The query to use to select pending content posts from the DRAFT_POSTS table.
     */
    private static final String GET_PENDING_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, TEMPLATE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND STATUS='NEW'";

    /**
     * The query to use to insert a post into the DRAFT_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO DRAFT_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, TEMPLATE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post in the DRAFT_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE DRAFT_POSTS SET UPDATED_DATE=?, SCHEDULED_DATE=?, TEMPLATE_ID=?, PROPERTIES=?, ATTRIBUTES=?, MESSAGE=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, TEMPLATE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE TYPE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of posts from the DRAFT_POSTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM DRAFT_POSTS";

    /**
     * The query to use to delete a post from the DRAFT_POSTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM DRAFT_POSTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public DraftPostDAO(SocialDAOFactory factory)
    {
        super(factory, "DRAFT_POSTS");
    }

    /**
     * Defines the columns and indices for the DRAFT_POSTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SCHEDULED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("TEMPLATE_ID", Types.VARCHAR, 36, false);
        table.addColumn("PROPERTIES", Types.LONGVARCHAR, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("DRAFT_POSTS_PK", new String[] {"ID"});
        table.addIndex("DRAFT_POSTS_STATUS_IDX", new String[] {"TYPE", "STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a post from the DRAFT_POSTS table by id.
     */
    public DraftPost getById(int id) throws SQLException
    {
        DraftPost ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), GET_BY_ID_SQL);
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setInt(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setTemplateId(rs.getString(6));
                post.setProperties(new JSONObject(getClob(rs, 7)));
                post.setAttributes(new JSONObject(getClob(rs, 8)));
                post.setMessage(rs.getString(9));
                post.setStatus(rs.getString(10));
                post.setCreatedBy(rs.getString(11));
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
     * Returns <CODE>true</CODE> if the given content item has a pending post in the DRAFT_POSTS table.
     */
    public boolean hasPending(ContentItem content) throws SQLException
    {
        boolean ret = false;

        List<DraftPost> posts = getPendingPosts(PostType.CONTENT);
        for(DraftPost draft : posts)
        {
            ContentPost post = (ContentPost)draft;
            if(post.getOrganisation().equals(content.getCode())
                && post.getContentType() == content.getType())
            {
                // Roundups don't have a content id
                if(content.getType() == ContentType.ROUNDUP 
                    || post.getContentId() == content.getId())
                {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns the pending posts from the DRAFT_POSTS table for the given type.
     */
    public List<DraftPost> getPendingPosts(PostType type) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getPendingStmt == null)
            getPendingStmt = prepareStatement(getConnection(), GET_PENDING_SQL);
        clearParameters(getPendingStmt);

        ResultSet rs = null;

        try
        {
            getPendingStmt.setString(1, type.name());
            getPendingStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getPendingStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setTemplateId(rs.getString(6));
                post.setProperties(new JSONObject(getClob(rs, 7)));
                post.setAttributes(new JSONObject(getClob(rs, 8)));
                post.setMessage(rs.getString(9));
                post.setStatus(rs.getString(10));
                post.setCreatedBy(rs.getString(11));
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
     * Stores the given post in the DRAFT_POSTS table.
     */
    public void add(DraftPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null, reader2 = null;

        try
        {
            insertStmt.setString(1, post.getId());
            insertStmt.setTimestamp(2, new Timestamp(post.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(post.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(post.getScheduledDateMillis()), UTC);
            insertStmt.setString(5, post.getType().name());
            insertStmt.setString(6, post.getTemplateId());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            insertStmt.setCharacterStream(7, reader, properties.length());
            String attributes = post.getAttributesAsJson().toString();
            reader2 = new StringReader(attributes);
            insertStmt.setCharacterStream(8, reader2, attributes.length());
            insertStmt.setString(9, post.getMessage());
            insertStmt.setString(10, post.getStatus().name());
            insertStmt.setString(11, post.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created post '"+post.getId()+"' in DRAFT_POSTS");
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
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Updates the given post in the DRAFT_POSTS table.
     */
    public void update(DraftPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null, reader2 = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(post.getUpdatedDateMillis()), UTC);
            updateStmt.setTimestamp(2, new Timestamp(post.getScheduledDateMillis()), UTC);
            updateStmt.setString(3, post.getTemplateId());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            updateStmt.setCharacterStream(4, reader, properties.length());
            String attributes = post.getAttributesAsJson().toString();
            reader2 = new StringReader(attributes);
            updateStmt.setCharacterStream(5, reader2, attributes.length());
            updateStmt.setString(6, post.getMessage());
            updateStmt.setString(7, post.getStatus().name());
            updateStmt.setString(8, post.getId());
            updateStmt.executeUpdate();

            logger.info("Updated post '"+post.getId()+"' in DRAFT_POSTS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the posts from the DRAFT_POSTS table.
     */
    public List<DraftPost> list(PostType type) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setString(1, type.name());
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setTemplateId(rs.getString(6));
                post.setProperties(new JSONObject(getClob(rs, 7)));
                post.setAttributes(new JSONObject(getClob(rs, 8)));
                post.setMessage(rs.getString(9));
                post.setStatus(rs.getString(10));
                post.setCreatedBy(rs.getString(11));
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
     * Removes the given post from the DRAFT_POSTS table.
     */
    public void delete(DraftPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, post.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted post '"+post.getId()+"' in DRAFT_POSTS");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getPendingStmt);
        getPendingStmt = null;
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
    private PreparedStatement getPendingStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
