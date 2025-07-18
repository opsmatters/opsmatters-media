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
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.organisation.Organisation;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.social.DraftPost;
import com.opsmatters.media.model.social.DraftPostItem;
import com.opsmatters.media.model.social.DraftPostFactory;
import com.opsmatters.media.model.social.DraftPostItemFactory;
import com.opsmatters.media.model.social.DraftPostStatus;
import com.opsmatters.media.model.social.SocialPostType;
import com.opsmatters.media.model.social.DraftContentPost;
import com.opsmatters.media.model.social.DraftContentPostItem;
import com.opsmatters.media.model.social.DraftStandardPostItem;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.util.SessionId;

import static com.opsmatters.media.model.social.SocialPostType.*;

/**
 * DAO that provides operations on the DRAFT_POSTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DraftPostDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(DraftPostDAO.class.getName());

    /**
     * The query to use to select a post from the DRAFT_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, SOURCE_ID, CODE, CONTENT_TYPE, TITLE, MESSAGE, PROPERTIES, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE ID=?";


    /**
     * The query to use to insert a post into the DRAFT_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO DRAFT_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, SOURCE_ID, CODE, CONTENT_TYPE, TITLE, MESSAGE, PROPERTIES, ATTRIBUTES, STATUS, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post in the DRAFT_POSTS table.
     */
    private static final String UPDATE_SQL =
      "UPDATE DRAFT_POSTS SET UPDATED_DATE=?, SITE_ID=?, SOURCE_ID=?, TITLE=?, MESSAGE=?, PROPERTIES=?, ATTRIBUTES=?, STATUS=?, CREATED_BY=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the post items from the DRAFT_POSTS table by type and interval.
     */
    private static final String LIST_ITEMS_BY_TYPE_INTERVAL_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, TITLE, STATUS "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND (CREATED_DATE >= (NOW() + INTERVAL -? DAY) OR STATUS != 'COMPLETED') ORDER BY CREATED_DATE";

    /**
     * The query to use to select the post items from the DRAFT_POSTS table by type, status and interval.
     */
    private static final String LIST_ITEMS_BY_TYPE_STATUS_INTERVAL_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, TITLE, STATUS "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND STATUS=? AND CREATED_DATE >= (NOW() + INTERVAL -? DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the post items from the DRAFT_POSTS table by type and status.
     */
    private static final String LIST_ITEMS_BY_TYPE_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, TITLE, STATUS "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, SOURCE_ID, CODE, CONTENT_TYPE, TITLE, MESSAGE, PROPERTIES, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE SITE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by site and type.
     */
    private static final String LIST_BY_SITE_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, SOURCE_ID, CODE, CONTENT_TYPE, TITLE, MESSAGE, PROPERTIES, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE SITE_ID=? AND TYPE=? AND (CREATED_DATE >= (NOW() + INTERVAL -? DAY) OR STATUS IN ('NEW','REPOSTED')) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, SOURCE_ID, CODE, CONTENT_TYPE, TITLE, MESSAGE, PROPERTIES, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE STATUS=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the post items from the DRAFT_POSTS table by source id.
     */
    private static final String LIST_ITEMS_BY_SOURCE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, TITLE, STATUS "
      + "FROM DRAFT_POSTS WHERE TYPE=? AND SOURCE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the posts from the DRAFT_POSTS table by site, code and content type.
     */
    private static final String LIST_BY_CONTENT_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, SITE_ID, SOURCE_ID, CODE, CONTENT_TYPE, TITLE, MESSAGE, PROPERTIES, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM DRAFT_POSTS WHERE SITE_ID=? AND CODE=? AND CONTENT_TYPE=? AND (SESSION_ID=? OR STATUS IN ('NEW','REPOSTED')) ORDER BY CREATED_DATE";

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
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("SOURCE_ID", Types.VARCHAR, 36, false);
        table.addColumn("CODE", Types.VARCHAR, 5, false);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("TITLE", Types.VARCHAR, 256, false);
        table.addColumn("MESSAGE", Types.LONGVARCHAR, true);
        table.addColumn("PROPERTIES", Types.LONGVARCHAR, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
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
                DraftPost post = DraftPostFactory.newInstance(SocialPostType.valueOf(rs.getString(4)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(5));
                post.setSourceId(rs.getString(6));

                if(post.getType() == CONTENT)
                {
                    DraftContentPost contentPost = (DraftContentPost)post;
                    contentPost.setCode(rs.getString(7));
                    contentPost.setContentType(rs.getString(8));
                }

                post.setTitle(rs.getString(9));
                post.setMessage(rs.getString(10));
                post.setProperties(new JSONObject(getClob(rs, 11)));
                post.setAttributes(new JSONObject(getClob(rs, 12)));
                post.setStatus(rs.getString(13));
                post.setCreatedBy(rs.getString(14));
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
            insertStmt.setString(4, post.getType().name());
            insertStmt.setString(5, post.getSiteId());
            insertStmt.setString(6, post.getSourceId());

            String code = "";
            String contentType = "";
            if(post.getType() == CONTENT)
            {
                DraftContentPost contentPost = (DraftContentPost)post;
                code = contentPost.getCode();
                if(contentPost.getContentType() != null)
                    contentType = contentPost.getContentType().name();
            }

            insertStmt.setString(7, code);
            insertStmt.setString(8, contentType);
            insertStmt.setString(9, post.getTitle());
            insertStmt.setString(10, post.getMessage());

            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            insertStmt.setCharacterStream(11, reader, properties.length());
            String attributes = post.getAttributes().toString();
            reader2 = new StringReader(attributes);
            insertStmt.setCharacterStream(12, reader2, attributes.length());
            insertStmt.setString(13, post.getStatus().name());
            insertStmt.setString(14, post.getCreatedBy());
            insertStmt.setInt(15, SessionId.get());
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
            updateStmt.setString(2, post.getSiteId());
            updateStmt.setString(3, post.getSourceId());
            updateStmt.setString(4, post.getTitle());
            updateStmt.setString(5, post.getMessage());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            updateStmt.setCharacterStream(6, reader, properties.length());
            String attributes = post.getAttributes().toString();
            reader2 = new StringReader(attributes);
            updateStmt.setCharacterStream(7, reader2, attributes.length());
            updateStmt.setString(8, post.getStatus().name());
            updateStmt.setString(9, post.getCreatedBy());
            updateStmt.setInt(10, SessionId.get());
            updateStmt.setString(11, post.getId());
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
     * Adds or Updates the given post in the DRAFT_POSTS table.
     */
    public boolean upsert(DraftPost post) throws SQLException
    {
        boolean ret = false;

        DraftPost existing = getById(post.getId());
        if(existing != null)
        {
            update(post);
        }
        else
        {
            add(post);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the post items from the DRAFT_POSTS table by type and interval.
     */
    public synchronized List<DraftPostItem> listItems(SocialPostType type, int interval) throws SQLException
    {
        List<DraftPostItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByTypeIntervalStmt == null)
            listItemsByTypeIntervalStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_TYPE_INTERVAL_SQL);
        clearParameters(listItemsByTypeIntervalStmt);

        ResultSet rs = null;

        try
        {
            listItemsByTypeIntervalStmt.setString(1, type.name());
            listItemsByTypeIntervalStmt.setInt(2, interval);
            listItemsByTypeIntervalStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByTypeIntervalStmt.executeQuery();
            ret = new ArrayList<DraftPostItem>();
            while(rs.next())
            {
                DraftPostItem post = DraftPostItemFactory.newInstance(type);
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(4));

                if(post.getType() == CONTENT)
                {
                    DraftContentPostItem contentPost = (DraftContentPostItem)post;
                    contentPost.setCode(rs.getString(5));
                    contentPost.setContentType(rs.getString(6));
                }
                else if(post.getType() == STANDARD)
                {
                    DraftStandardPostItem standardPost = (DraftStandardPostItem)post;
                    standardPost.setTitle(rs.getString(7));
                }

                post.setStatus(rs.getString(8));
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
     * Returns the post items from the DRAFT_POSTS table by type, status and interval.
     */
    public synchronized List<DraftPostItem> listItems(SocialPostType type, DraftPostStatus status, int interval) throws SQLException
    {
        List<DraftPostItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByTypeStatusIntervalStmt == null)
            listItemsByTypeStatusIntervalStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_TYPE_STATUS_INTERVAL_SQL);
        clearParameters(listItemsByTypeStatusIntervalStmt);

        ResultSet rs = null;

        try
        {
            listItemsByTypeStatusIntervalStmt.setString(1, type.name());
            listItemsByTypeStatusIntervalStmt.setString(2, status != null ? status.name() : "");
            listItemsByTypeStatusIntervalStmt.setInt(3, interval);
            listItemsByTypeStatusIntervalStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByTypeStatusIntervalStmt.executeQuery();
            ret = new ArrayList<DraftPostItem>();
            while(rs.next())
            {
                DraftPostItem post = DraftPostItemFactory.newInstance(type);
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(4));

                if(post.getType() == CONTENT)
                {
                    DraftContentPostItem contentPost = (DraftContentPostItem)post;
                    contentPost.setCode(rs.getString(5));
                    contentPost.setContentType(rs.getString(6));
                }
                else if(post.getType() == STANDARD)
                {
                    DraftStandardPostItem standardPost = (DraftStandardPostItem)post;
                    standardPost.setTitle(rs.getString(7));
                }

                post.setStatus(rs.getString(8));
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
     * Returns the post items from the DRAFT_POSTS table by type and status.
     */
    public synchronized List<DraftPostItem> listItems(SocialPostType type, DraftPostStatus status) throws SQLException
    {
        List<DraftPostItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByTypeStatusStmt == null)
            listItemsByTypeStatusStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_TYPE_STATUS_SQL);
        clearParameters(listItemsByTypeStatusStmt);

        ResultSet rs = null;

        try
        {
            listItemsByTypeStatusStmt.setString(1, type.name());
            listItemsByTypeStatusStmt.setString(2, status != null ? status.name() : "");
            listItemsByTypeStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByTypeStatusStmt.executeQuery();
            ret = new ArrayList<DraftPostItem>();
            while(rs.next())
            {
                DraftPostItem post = DraftPostItemFactory.newInstance(type);
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(4));

                if(post.getType() == CONTENT)
                {
                    DraftContentPostItem contentPost = (DraftContentPostItem)post;
                    contentPost.setCode(rs.getString(5));
                    contentPost.setContentType(rs.getString(6));
                }
                else if(post.getType() == STANDARD)
                {
                    DraftStandardPostItem standardPost = (DraftStandardPostItem)post;
                    standardPost.setTitle(rs.getString(7));
                }

                post.setStatus(rs.getString(8));
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
                DraftPost post = DraftPostFactory.newInstance(SocialPostType.valueOf(rs.getString(4)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(5));
                post.setSourceId(rs.getString(6));

                if(post.getType() == CONTENT)
                {
                    DraftContentPost contentPost = (DraftContentPost)post;
                    contentPost.setCode(rs.getString(7));
                    contentPost.setContentType(rs.getString(8));
                }

                post.setTitle(rs.getString(9));
                post.setMessage(rs.getString(10));
                post.setProperties(new JSONObject(getClob(rs, 11)));
                post.setAttributes(new JSONObject(getClob(rs, 12)));
                post.setStatus(rs.getString(13));
                post.setCreatedBy(rs.getString(14));
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
    public synchronized List<DraftPost> list(Site site, SocialPostType type, int interval) throws SQLException
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
                DraftPost post = DraftPostFactory.newInstance(SocialPostType.valueOf(rs.getString(4)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(5));
                post.setSourceId(rs.getString(6));

                if(post.getType() == CONTENT)
                {
                    DraftContentPost contentPost = (DraftContentPost)post;
                    contentPost.setCode(rs.getString(7));
                    contentPost.setContentType(rs.getString(8));
                }

                post.setTitle(rs.getString(9));
                post.setMessage(rs.getString(10));
                post.setProperties(new JSONObject(getClob(rs, 11)));
                post.setAttributes(new JSONObject(getClob(rs, 12)));
                post.setStatus(rs.getString(13));
                post.setCreatedBy(rs.getString(14));
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
     * Returns the posts from the DRAFT_POSTS table by status.
     */
    public synchronized List<DraftPost> list(DraftPostStatus status) throws SQLException
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
            listByStatusStmt.setString(1, status != null ? status.name() : "");
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(SocialPostType.valueOf(rs.getString(4)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(5));
                post.setSourceId(rs.getString(6));

                if(post.getType() == CONTENT)
                {
                    DraftContentPost contentPost = (DraftContentPost)post;
                    contentPost.setCode(rs.getString(7));
                    contentPost.setContentType(rs.getString(8));
                }

                post.setTitle(rs.getString(9));
                post.setMessage(rs.getString(10));
                post.setProperties(new JSONObject(getClob(rs, 11)));
                post.setAttributes(new JSONObject(getClob(rs, 12)));
                post.setStatus(rs.getString(13));
                post.setCreatedBy(rs.getString(14));
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
     * Returns the post items from the DRAFT_POSTS table by type and source id.
     */
    public synchronized List<DraftPostItem> listItems(SocialPostType type, String sourceId) throws SQLException
    {
        List<DraftPostItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsBySourceStmt == null)
            listItemsBySourceStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_SOURCE_SQL);
        clearParameters(listItemsBySourceStmt);

        ResultSet rs = null;

        try
        {
            listItemsBySourceStmt.setString(1, type.name());
            listItemsBySourceStmt.setString(2, sourceId);
            listItemsBySourceStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsBySourceStmt.executeQuery();
            ret = new ArrayList<DraftPostItem>();
            while(rs.next())
            {
                DraftPostItem post = DraftPostItemFactory.newInstance(type);
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(4));

                if(post.getType() == CONTENT)
                {
                    DraftContentPostItem contentPost = (DraftContentPostItem)post;
                    contentPost.setCode(rs.getString(5));
                    contentPost.setContentType(rs.getString(6));
                }
                else if(post.getType() == STANDARD)
                {
                    DraftStandardPostItem standardPost = (DraftStandardPostItem)post;
                    standardPost.setTitle(rs.getString(7));
                }

                post.setStatus(rs.getString(8));
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
     * Returns the posts for the given site, code and content type in the DRAFT_POSTS table.
     */
    public List<DraftPost> list(String siteId, String code, ContentType type) throws SQLException
    {
        List<DraftPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContentTypeStmt == null)
            listByContentTypeStmt = prepareStatement(getConnection(), LIST_BY_CONTENT_TYPE_SQL);
        clearParameters(listByContentTypeStmt);

        ResultSet rs = null;

        try
        {
            listByContentTypeStmt.setString(1, siteId);
            listByContentTypeStmt.setString(2, code);
            listByContentTypeStmt.setString(3, type.name());
            listByContentTypeStmt.setInt(4, SessionId.get()); // Used for COMPLETED posts
            listByContentTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContentTypeStmt.executeQuery();
            ret = new ArrayList<DraftPost>();
            while(rs.next())
            {
                DraftPost post = DraftPostFactory.newInstance(SocialPostType.valueOf(rs.getString(4)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setSiteId(rs.getString(5));
                post.setSourceId(rs.getString(6));

                if(post.getType() == CONTENT)
                {
                    DraftContentPost contentPost = (DraftContentPost)post;
                    contentPost.setCode(rs.getString(7));
                    contentPost.setContentType(rs.getString(8));
                }

                post.setTitle(rs.getString(9));
                post.setMessage(rs.getString(10));
                post.setProperties(new JSONObject(getClob(rs, 11)));
                post.setAttributes(new JSONObject(getClob(rs, 12)));
                post.setStatus(rs.getString(13));
                post.setCreatedBy(rs.getString(14));
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
     * Returns the posts for the given organisation in the DRAFT_POSTS table by status.
     */
    public List<DraftPost> list(OrganisationSite organisation, DraftPostStatus status) throws SQLException
    {
        List<DraftPost> ret = new ArrayList<DraftPost>();
        List<DraftPost> posts = list(organisation.getSiteId(), organisation.getCode(), ContentType.ORGANISATION);

        if(posts != null)
        {
            for(DraftPost post : posts)
            {
                if(post.getStatus() == status)
                    ret.add(post);
            }
        }

        return ret;
    }

    /**
     * Returns the posts for the given content in the DRAFT_POSTS table by status.
     */
    public List<DraftPost> list(Content content, DraftPostStatus status) throws SQLException
    {
        List<DraftPost> ret = new ArrayList<DraftPost>();
        List<DraftPost> posts = list(content.getSiteId(), content.getCode(), content.getType());

        if(posts != null)
        {
            for(DraftPost post : posts)
            {
                if(post.getStatus() == status)
                {
                    DraftContentPost contentPost = (DraftContentPost)post;

                    // Roundups don't have a content id
                    if(content.getType() == ContentType.ROUNDUP 
                        || contentPost.getContentId() == content.getId())
                    {
                        ret.add(post);
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if the given organisation has a pending post in the DRAFT_POSTS table.
     */
    public boolean hasPending(OrganisationSite organisation) throws SQLException
    {
        return list(organisation, DraftPostStatus.NEW).size() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the given content has a pending post in the DRAFT_POSTS table.
     */
    public boolean hasPending(Content content) throws SQLException
    {
        return list(content, DraftPostStatus.NEW).size() > 0;
    }

    /**
     * Returns <CODE>true</CODE> if the given content has a completed post in the DRAFT_POSTS table.
     */
    public boolean hasCompleted(Content content) throws SQLException
    {
        return list(content, DraftPostStatus.COMPLETED).size() > 0;
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
    public void delete(Content content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        List<DraftPost> posts = list(content.getSiteId(), content.getCode(), content.getType());
        for(DraftPost post : posts)
        {
            DraftContentPost contentPost = (DraftContentPost)post;

            // Roundups don't have a content id
            if(content.getType() == ContentType.ROUNDUP 
                || contentPost.getContentId() == content.getId())
            {
                logger.info("Found post for content '"+post.getId()+"' in DRAFT_POSTS");
                delete(post);
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
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listItemsByTypeIntervalStmt);
        listItemsByTypeIntervalStmt = null;
        closeStatement(listItemsByTypeStatusIntervalStmt);
        listItemsByTypeStatusIntervalStmt = null;
        closeStatement(listItemsByTypeStatusStmt);
        listItemsByTypeStatusStmt = null;
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listBySiteTypeStmt);
        listBySiteTypeStmt = null;
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(listItemsBySourceStmt);
        listItemsBySourceStmt = null;
        closeStatement(listByContentTypeStmt);
        listByContentTypeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listItemsByTypeIntervalStmt;
    private PreparedStatement listItemsByTypeStatusIntervalStmt;
    private PreparedStatement listItemsByTypeStatusStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listBySiteTypeStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement listItemsBySourceStmt;
    private PreparedStatement listByContentTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
