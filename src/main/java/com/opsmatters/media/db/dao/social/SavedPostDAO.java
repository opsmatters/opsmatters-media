/*
 * Copyright 2021 Gerald Curley
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
import com.opsmatters.media.model.social.SavedPost;
import com.opsmatters.media.model.social.SavedPostItem;
import com.opsmatters.media.model.social.SavedPostFactory;
import com.opsmatters.media.model.social.SavedPostItemFactory;
import com.opsmatters.media.model.social.PostType;
import com.opsmatters.media.model.social.MessageFormat;
import com.opsmatters.media.model.social.SavedContentPost;
import com.opsmatters.media.model.social.SavedContentPostItem;
import com.opsmatters.media.model.content.ContentType;

/**
 * DAO that provides operations on the SAVED_POST table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SavedPostDAO extends SocialDAO<SavedPost>
{
    private static final Logger logger = Logger.getLogger(SavedPostDAO.class.getName());

    /**
     * The query to use to select a saved post from the SAVED_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, TYPE, SITE_ID, TITLE, CODE, CONTENT_TYPE, MESSAGE, SHORTEN_URL, PROPERTIES, STATUS, CREATED_BY "
      + "FROM SAVED_POSTS WHERE ID=?";

    /**
     * The query to use to insert a saved post into the SAVED_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SAVED_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, TYPE, SITE_ID, TITLE, CODE, CONTENT_TYPE, MESSAGE, SHORTEN_URL, PROPERTIES, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a saved post in the SAVED_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE SAVED_POSTS SET UPDATED_DATE=?, POSTED_DATE=?, SITE_ID=?, TITLE=?, CODE=?, CONTENT_TYPE=?, MESSAGE=?, SHORTEN_URL=?, PROPERTIES=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the saved posts from the SAVED_POSTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, TYPE, SITE_ID, TITLE, CODE, CONTENT_TYPE, MESSAGE, SHORTEN_URL, PROPERTIES, STATUS, CREATED_BY "
      + "FROM SAVED_POSTS";

    /**
     * The query to use to select the saved posts from the SAVED_POSTS table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, TYPE, SITE_ID, TITLE, CODE, CONTENT_TYPE, MESSAGE, SHORTEN_URL, PROPERTIES, STATUS, CREATED_BY "
      + "FROM SAVED_POSTS WHERE SITE_ID=?";

    /**
     * The query to use to select the saved post items from the SAVED_POSTS table by type.
     */
    private static final String LIST_ITEMS_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, SITE_ID, CODE, CONTENT_TYPE, TITLE, STATUS "
      + "FROM SAVED_POSTS WHERE TYPE=?";

    /**
     * The query to use to select the saved posts from the SAVED_POSTS table by content type.
     */
    private static final String LIST_BY_CONTENT_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, POSTED_DATE, TYPE, SITE_ID, TITLE, CODE, CONTENT_TYPE, MESSAGE, SHORTEN_URL, PROPERTIES, STATUS, CREATED_BY "
      + "FROM SAVED_POSTS WHERE SITE_ID=? AND TYPE=?";

    /**
     * The query to use to get the count of saved posts from the SAVED_POSTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SAVED_POSTS";

    /**
     * The query to use to delete a saved post from the SAVED_POSTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SAVED_POSTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SavedPostDAO(SocialDAOFactory factory)
    {
        super(factory, "SAVED_POSTS");
    }

    /**
     * Defines the columns and indices for the SAVED_POSTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("POSTED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("TITLE", Types.VARCHAR, 128, true);
        table.addColumn("CODE", Types.VARCHAR, 5, false);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("MESSAGE", Types.LONGVARCHAR, true);
        table.addColumn("SHORTEN_URL", Types.BOOLEAN, true);
        table.addColumn("PROPERTIES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SAVED_POSTS_PK", new String[] {"ID"});
        table.addIndex("SAVED_POSTS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a saved post from the SAVED_POSTS table by id.
     */
    public synchronized SavedPost getById(String id) throws SQLException
    {
        SavedPost ret = null;

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
                SavedPost post = SavedPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setTitle(rs.getString(7));

                if(post.getType() == PostType.CONTENT)
                {
                    SavedContentPost contentPost = (SavedContentPost)post;
                    contentPost.setCode(rs.getString(8));
                    contentPost.setContentType(rs.getString(9));
                }

                post.setMessage(rs.getString(10));
                post.setShortenUrl(rs.getBoolean(11));
                post.setProperties(new JSONObject(getClob(rs, 12)));
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
     * Stores the given saved post in the SAVED_POSTS table.
     */
    public synchronized void add(SavedPost post) throws SQLException
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
            insertStmt.setTimestamp(4, new Timestamp(post.getPostedDateMillis()), UTC);
            insertStmt.setString(5, post.getType().name());
            insertStmt.setString(6, post.getSiteId());
            insertStmt.setString(7, post.getTitle());

            String code = "";
            String contentType = "";
            if(post.getType() == PostType.CONTENT)
            {
                SavedContentPost contentPost = (SavedContentPost)post;
                code = contentPost.getCode();
                if(contentPost.getContentType() != null)
                    contentType = contentPost.getContentType().name();
            }

            insertStmt.setString(8, code);
            insertStmt.setString(9, contentType);

            insertStmt.setString(10, post.getMessage(MessageFormat.ENCODED));
            insertStmt.setBoolean(11, post.isShortenUrl());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            insertStmt.setCharacterStream(12, reader, properties.length());
            insertStmt.setString(13, post.getStatus().name());
            insertStmt.setString(14, post.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created saved post '"+post.getId()+"' in SAVED_POSTS");
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
     * Updates the given saved post in the SAVED_POSTS table.
     */
    public synchronized void update(SavedPost post) throws SQLException
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
            updateStmt.setTimestamp(2, new Timestamp(post.getPostedDateMillis()), UTC);
            updateStmt.setString(3, post.getSiteId());
            updateStmt.setString(4, post.getTitle());

            String code = "";
            String contentType = "";
            if(post.getType() == PostType.CONTENT)
            {
                SavedContentPost contentPost = (SavedContentPost)post;
                code = contentPost.getCode();
                if(contentPost.getContentType() != null)
                    contentType = contentPost.getContentType().name();
            }
            updateStmt.setString(5, code);
            updateStmt.setString(6, contentType);

            updateStmt.setString(7, post.getMessage(MessageFormat.ENCODED));
            updateStmt.setBoolean(8, post.isShortenUrl());
            String properties = post.getPropertiesAsJson().toString();
            reader = new StringReader(properties);
            updateStmt.setCharacterStream(9, reader, properties.length());
            updateStmt.setString(10, post.getStatus().name());
            updateStmt.setString(11, post.getId());
            updateStmt.executeUpdate();

            logger.info("Updated saved post '"+post.getId()+"' in SAVED_POSTS");
        }
        finally
        {
            if(reader != null)
                reader.close();
          }
    }

    /**
     * Returns the saved posts from the SAVED_POSTS table.
     */
    public synchronized List<SavedPost> list() throws SQLException
    {
        List<SavedPost> ret = null;

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
            ret = new ArrayList<SavedPost>();
            while(rs.next())
            {
                SavedPost post = SavedPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setTitle(rs.getString(7));

                if(post.getType() == PostType.CONTENT)
                {
                    SavedContentPost contentPost = (SavedContentPost)post;
                    contentPost.setCode(rs.getString(8));
                    contentPost.setContentType(rs.getString(9));
                }

                post.setMessage(rs.getString(10));
                post.setShortenUrl(rs.getBoolean(11));
                post.setProperties(new JSONObject(getClob(rs, 12)));
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
     * Returns the saved posts from the SAVED_POSTS table by site.
     */
    public synchronized List<SavedPost> list(Site site) throws SQLException
    {
        List<SavedPost> ret = null;

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
            ret = new ArrayList<SavedPost>();
            while(rs.next())
            {
                SavedPost post = SavedPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setTitle(rs.getString(7));

                if(post.getType() == PostType.CONTENT)
                {
                    SavedContentPost contentPost = (SavedContentPost)post;
                    contentPost.setCode(rs.getString(8));
                    contentPost.setContentType(rs.getString(9));
                }

                post.setMessage(rs.getString(10));
                post.setShortenUrl(rs.getBoolean(11));
                post.setProperties(new JSONObject(getClob(rs, 12)));
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
     * Returns the saved post items from the SAVED_POSTS table by post type.
     */
    public synchronized List<SavedPostItem> listItems(PostType type) throws SQLException
    {
        List<SavedPostItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByTypeStmt == null)
            listItemsByTypeStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_TYPE_SQL);
        clearParameters(listItemsByTypeStmt);

        ResultSet rs = null;

        try
        {
            listItemsByTypeStmt.setString(1, type.name());
            listItemsByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByTypeStmt.executeQuery();
            ret = new ArrayList<SavedPostItem>();
            while(rs.next())
            {
                SavedPostItem post = SavedPostItemFactory.newInstance(type);
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(5));

                if(post.getType() == PostType.CONTENT)
                {
                    SavedContentPostItem contentPost = (SavedContentPostItem)post;
                    contentPost.setCode(rs.getString(6));
                    contentPost.setContentType(rs.getString(7));
                }

                post.setTitle(rs.getString(8));
                post.setStatus(rs.getString(9));
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
     * Returns the saved posts from the SAVED_POSTS table by post type and content type.
     */
    public synchronized List<SavedPost> list(Site site, PostType type, ContentType contentType) throws SQLException
    {
        List<SavedPost> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByContentTypeStmt == null)
            listByContentTypeStmt = prepareStatement(getConnection(), LIST_BY_CONTENT_TYPE_SQL);
        clearParameters(listByContentTypeStmt);

        ResultSet rs = null;

        try
        {
            listByContentTypeStmt.setString(1, site.getId());
            listByContentTypeStmt.setString(2, type.name());
            listByContentTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByContentTypeStmt.executeQuery();
            ret = new ArrayList<SavedPost>();
            while(rs.next())
            {
                SavedPost post = SavedPostFactory.newInstance(PostType.valueOf(rs.getString(5)));
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setPostedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                post.setSiteId(rs.getString(6));
                post.setTitle(rs.getString(7));

                if(post.getType() == PostType.CONTENT)
                {
                    SavedContentPost contentPost = (SavedContentPost)post;
                    contentPost.setCode(rs.getString(8));
                    contentPost.setContentType(rs.getString(9));
                }

                post.setMessage(rs.getString(10));
                post.setShortenUrl(rs.getBoolean(11));
                post.setProperties(new JSONObject(getClob(rs, 12)));
                post.setStatus(rs.getString(13));
                post.setCreatedBy(rs.getString(14));

                if(contentType == null || post.getContentType() == contentType)
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
     * Returns the count of saved posts from the table.
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
     * Removes the given saved post from the SAVED_POSTS table.
     */
    public synchronized void delete(SavedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, post.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted saved post '"+post.getId()+"' in SAVED_POSTS");
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
        closeStatement(listItemsByTypeStmt);
        listItemsByTypeStmt = null;
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
    private PreparedStatement listStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listItemsByTypeStmt;
    private PreparedStatement listByContentTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
