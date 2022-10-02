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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.social.DraftPost;
import com.opsmatters.media.model.social.DraftPostFactory;
import com.opsmatters.media.model.social.DraftStatus;
import com.opsmatters.media.model.social.PostType;
import com.opsmatters.media.model.social.DraftContentPost;
import com.opsmatters.media.util.AppSession;

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
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE ID=?";

    /**
     * The query to use to select pending content posts from the DRAFT_POSTS table.
     */
    private static final String GET_PENDING_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND STATUS='NEW'";

    /**
     * The query to use to insert a post into the DRAFT_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO DRAFT_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post in the DRAFT_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE DRAFT_POSTS SET UPDATED_DATE=?, SCHEDULED_DATE=?, SITE_ID=?, SOURCE_ID=?, PROPERTIES=?, ATTRIBUTES=?, MESSAGE=?, STATUS=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND (CREATED_DATE >= (NOW() + INTERVAL -? DAY) OR STATUS='NEW' OR STATUS='REPOSTED') ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE SITE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by site and type.
     */
    private static final String LIST_BY_SITE_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE SITE_ID=? AND TYPE=? AND (CREATED_DATE >= (NOW() + INTERVAL -? DAY) OR STATUS='NEW' OR STATUS='REPOSTED') ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND STATUS=? AND (CREATED_DATE >= (NOW() + INTERVAL -? DAY) OR STATUS='NEW' OR STATUS='REPOSTED') ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by source id.
     */
    private static final String LIST_BY_SOURCE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SCHEDULED_DATE, TYPE, SITE_ID, SOURCE_ID, PROPERTIES, ATTRIBUTES, MESSAGE, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND SOURCE_ID=? ORDER BY CREATED_DATE";

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
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("SOURCE_ID", Types.VARCHAR, 36, false);
        table.addColumn("PROPERTIES", Types.LONGVARCHAR, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("DRAFT_POSTS_PK", new String[] {"ID"});
        table.addIndex("DRAFT_POSTS_STATUS_IDX", new String[] {"TYPE", "STATUS"});
        table.addIndex("DRAFT_POSTS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a post from the DRAFT_POSTS table by id.
     */
    public synchronized DraftPost getById(String id) throws SQLException
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
            getByIdStmt.setString(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Returns the pending posts from the DRAFT_POSTS table for the given type.
     */
    public synchronized List<DraftPost> getPendingPosts(PostType type) throws SQLException
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
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Returns the pending posts for the given content item in the DRAFT_POSTS table.
     */
    public List<DraftPost> getPending(ContentItem content) throws SQLException
    {
        List<DraftPost> ret = new ArrayList<DraftPost>();

        List<DraftPost> posts = getPendingPosts(PostType.CONTENT);
        for(DraftPost draft : posts)
        {
            DraftContentPost post = (DraftContentPost)draft;
            if(post.getSiteId().equals(content.getSiteId())
                && post.getCode().equals(content.getCode())
                && post.getContentType() == content.getType())
            {
                // Roundups don't have a content id
                if(content.getType() == ContentType.ROUNDUP 
                    || post.getContentId() == content.getId())
                {
                    ret.add(post);
                }
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given content item has a pending post in the DRAFT_POSTS table.
     */
    public boolean hasPending(ContentItem content) throws SQLException
    {
        return getPending(content).size() > 0;
    }

    /**
     * Returns the pending posts for the given organisation in the DRAFT_POSTS table.
     */
    public List<DraftPost> getPending(OrganisationSite organisation) throws SQLException
    {
        List<DraftPost> ret = new ArrayList<DraftPost>();

        List<DraftPost> posts = getPendingPosts(PostType.CONTENT);
        for(DraftPost draft : posts)
        {
            DraftContentPost post = (DraftContentPost)draft;
            if(post.getSiteId().equals(organisation.getSiteId())
                && post.getCode().equals(organisation.getCode())
                && post.getContentType() == ContentType.ORGANISATION)
            {
                ret.add(post);
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given organisation has a pending post in the DRAFT_POSTS table.
     */
    public boolean hasPending(OrganisationSite organisation) throws SQLException
    {
        return getPending(organisation).size() > 0;
    }

    /**
     * Stores the given post in the DRAFT_POSTS table.
     */
    public synchronized void add(DraftPost post) throws SQLException
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
            insertStmt.setString(6, post.getSiteId());
            insertStmt.setString(7, post.getSourceId());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            insertStmt.setCharacterStream(8, reader, properties.length());
            String attributes = post.getAttributes().toString();
            reader2 = new StringReader(attributes);
            insertStmt.setCharacterStream(9, reader2, attributes.length());
            insertStmt.setString(10, post.getMessage());
            insertStmt.setString(11, post.getStatus().name());
            insertStmt.setString(12, post.getCreatedBy());
            insertStmt.setInt(13, AppSession.id());
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
            if(reader2 != null)
                reader2.close();
        }
    }

    /**
     * Updates the given post in the DRAFT_POSTS table.
     */
    public synchronized void update(DraftPost post) throws SQLException
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
            updateStmt.setString(3, post.getSiteId());
            updateStmt.setString(4, post.getSourceId());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            updateStmt.setCharacterStream(5, reader, properties.length());
            String attributes = post.getAttributes().toString();
            reader2 = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader2, attributes.length());
            updateStmt.setString(7, post.getMessage());
            updateStmt.setString(8, post.getStatus().name());
            updateStmt.setInt(9, AppSession.id());
            updateStmt.setString(10, post.getId());
            updateStmt.executeUpdate();

            logger.info("Updated post '"+post.getId()+"' in DRAFT_POSTS");
        }
        finally
        {
            if(reader != null)
                reader.close();
            if(reader2 != null)
                reader2.close();
        }
    }

    /**
     * Returns the posts from the DRAFT_POSTS table by type.
     */
    public synchronized List<DraftPost> list(PostType type, int interval) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, type.name());
            listByTypeStmt.setInt(2, interval);
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Returns the posts from the DRAFT_POSTS table by site.
     */
    public synchronized List<DraftPost> list(Site site) throws SQLException
    {
        List<DraftPost> ret = null;

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
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Returns the posts from the DRAFT_POSTS table by site and type.
     */
    public synchronized List<DraftPost> list(Site site, PostType type, int interval) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listBySiteTypeStmt == null)
            listBySiteTypeStmt = prepareStatement(getConnection(), LIST_BY_SITE_TYPE_SQL);
        clearParameters(listBySiteTypeStmt);

        ResultSet rs = null;

        try
        {
            listBySiteTypeStmt.setString(1, site.getId());
            listBySiteTypeStmt.setString(2, type.name());
            listBySiteTypeStmt.setInt(3, interval);
            listBySiteTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySiteTypeStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Returns the posts from the DRAFT_POSTS table by type and status.
     */
    public synchronized List<DraftPost> list(PostType type, DraftStatus status, int interval) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByStatusStmt == null)
            listByStatusStmt = prepareStatement(getConnection(), LIST_BY_STATUS_SQL);
        clearParameters(listByStatusStmt);

        ResultSet rs = null;

        try
        {
            listByStatusStmt.setString(1, type.name());
            listByStatusStmt.setString(2, status != null ? status.name() : "");
            listByStatusStmt.setInt(3, interval);
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Returns the posts from the DRAFT_POSTS table by type and source id.
     */
    public synchronized List<DraftPost> list(PostType type, String sourceId) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listBySourceStmt == null)
            listBySourceStmt = prepareStatement(getConnection(), LIST_BY_SOURCE_SQL);
        clearParameters(listBySourceStmt);

        ResultSet rs = null;

        try
        {
            listBySourceStmt.setString(1, type.name());
            listBySourceStmt.setString(2, sourceId);
            listBySourceStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySourceStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setScheduledDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setSourceId(rs.getString(7));
                post.setProperties(new JSONObject(getClob(rs, 8)));
                post.setAttributes(new JSONObject(getClob(rs, 9)));
                post.setMessage(rs.getString(10));
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
     * Removes the given post from the DRAFT_POSTS table.
     */
    public synchronized void delete(DraftPost post) throws SQLException
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
     * Removes the post for the given content from the DRAFT_POSTS table.
     */
    public void delete(ContentItem content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        List<DraftPost> posts = getPendingPosts(PostType.CONTENT);
        for(DraftPost draft : posts)
        {
            DraftContentPost post = (DraftContentPost)draft;
            if(post.getContentType() == content.getType()
                && post.getSiteId().equals(content.getSiteId())
                && post.getCode().equals(content.getCode()))
            {
                // Roundups don't have a content id
                if(content.getType() == ContentType.ROUNDUP 
                    || post.getContentId() == content.getId())
                {
                    logger.info("Found post for content '"+post.getId()+"' in DRAFT_POSTS");
                    delete(post);
                }
            }
        }
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
        closeStatement(listByTypeStmt);
        listByTypeStmt = null;
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listBySiteTypeStmt);
        listBySiteTypeStmt = null;
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(listBySourceStmt);
        listBySourceStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement getPendingStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByTypeStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listBySiteTypeStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement listBySourceStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
