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
package com.opsmatters.media.db.dao.content.post;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.ContentLookup;
import com.opsmatters.media.model.content.post.RoundupPost;
import com.opsmatters.media.model.content.post.RoundupPostItem;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the ROUNDUPS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupPostDAO extends ContentDAO<RoundupPost>
{
    private static final Logger logger = Logger.getLogger(RoundupPostDAO.class.getName());

    /**
     * The query to use to select a roundup from the ROUNDUPS table by URL.
     */
    private static final String GET_BY_URL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM ROUNDUPS WHERE SITE_ID=? AND CODE=? AND URL=? ";

    /**
     * The query to use to insert a roundup into the ROUNDUPS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ROUNDUPS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, PUBLISHED, PROMOTE, NEWSLETTER, FEATURED, SPONSORED, "
      +   "AUTHOR, STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a roundup in the ROUNDUPS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ROUNDUPS SET UUID=?, PUBLISHED_DATE=?, TITLE=?, URL=?, PUBLISHED=?, PROMOTE=?, NEWSLETTER=?, FEATURED=?, SPONSORED=?, "
      + "AUTHOR=?, STATUS=?, CREATED_BY=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select a list of roundup from the ROUNDUPS table by URL.
     */
    private static final String LIST_BY_URL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM ROUNDUPS WHERE CODE=? AND URL=? AND (?=0 OR ABS(TIMESTAMPDIFF(DAY, ?, PUBLISHED_DATE)) <= 7)";

    /**
     * The query to use to select the roundup items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, PUBLISHED, PROMOTE, NEWSLETTER, FEATURED, SPONSORED, AUTHOR, STATUS "
      + "FROM ROUNDUPS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * The query to use to select the roundup items from the table by published date.
     */
    private static final String LIST_ITEMS_BY_DATE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, PUBLISHED, PROMOTE, NEWSLETTER, FEATURED, SPONSORED, AUTHOR, STATUS "
      + "FROM ROUNDUPS WHERE SITE_ID=? AND PUBLISHED=1 AND PUBLISHED_DATE>? AND STATUS != 'SKIPPED' ORDER BY ID";

    /**
     * Constructor that takes a DAO factory.
     */
    public RoundupPostDAO(ContentDAOFactory factory)
    {
        super(factory, "ROUNDUPS");
    }

    /**
     * Defines the columns and indices for the ROUNDUPS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("TITLE", Types.VARCHAR, 256, true);
        table.addColumn("URL", Types.VARCHAR, 256, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("NEWSLETTER", Types.BOOLEAN, true);
        table.addColumn("FEATURED", Types.BOOLEAN, true);
        table.addColumn("SPONSORED", Types.BOOLEAN, true);
        table.addColumn("AUTHOR", Types.VARCHAR, 30, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("ROUNDUPS_PK", new String[] {"UUID"});
        table.addIndex("ROUNDUPS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("ROUNDUPS_TITLE_IDX", new String[] {"SITE_ID","CODE","TITLE"});
        table.addIndex("ROUNDUPS_URL_IDX", new String[] {"SITE_ID","CODE","URL"});
        table.addIndex("ROUNDUPS_CODE_URL_IDX", new String[] {"CODE","URL"});
        table.addIndex("ROUNDUPS_PUBLISHED_DATE_IDX", new String[] {"SITE_ID","PUBLISHED_DATE"});
        table.addIndex("ROUNDUPS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("ROUNDUPS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a roundup from the ROUNDUPS table by URL.
     */
    public synchronized RoundupPost getByUrl(String siteId, String code, String url) throws SQLException
    {
        RoundupPost ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUrlStmt == null)
            getByUrlStmt = prepareStatement(getConnection(), GET_BY_URL_SQL);
        clearParameters(getByUrlStmt);

        ResultSet rs = null;

        try
        {
            getByUrlStmt.setString(1, siteId);
            getByUrlStmt.setString(2, code);
            getByUrlStmt.setString(3, url);
            getByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUrlStmt.executeQuery();
            while(rs.next())
            {
                RoundupPost content = new RoundupPost();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setAttributes(new JSONObject(getClob(rs, 6)));
                content.setStatus(rs.getString(7));
                content.setCreatedBy(rs.getString(8));
                ret = content;
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
     * Stores the given roundup in the ROUNDUPS table.
     */
    public synchronized void add(RoundupPost content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("roundup uuid null");

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
            insertStmt.setString(6, content.getTitle());
            insertStmt.setString(7, content.getUrl());
            insertStmt.setBoolean(8, content.isPublished());
            insertStmt.setBoolean(9, content.isPromoted());
            insertStmt.setBoolean(10, content.isNewsletter());
            insertStmt.setBoolean(11, content.isFeatured());
            insertStmt.setBoolean(12, content.isSponsored());
            insertStmt.setString(13, content.getAuthor());
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

            // Unique constraint violated means that the roundup already exists
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
     * Updates the given roundup in the ROUNDUPS table.
     */
    public synchronized void update(RoundupPost content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("roundup uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setString(1, content.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(3, content.getTitle());
            updateStmt.setString(4, content.getUrl());
            updateStmt.setBoolean(5, content.isPublished());
            updateStmt.setBoolean(6, content.isPromoted());
            updateStmt.setBoolean(7, content.isNewsletter());
            updateStmt.setBoolean(8, content.isFeatured());
            updateStmt.setBoolean(9, content.isSponsored());
            updateStmt.setString(10, content.getAuthor());
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
     * Returns a class to look up an organisation's content by title or id.
     */
    public ContentLookup<RoundupPost> newLookup()
    {
        return new ContentLookup<RoundupPost>()
        {
            @Override
            protected RoundupPost getByTitle(String siteId, String code, String title)
                throws SQLException
            {
                return RoundupPostDAO.this.getByTitle(siteId, code, title);
            }

            @Override
            protected RoundupPost getById(String siteId, String code, String id)
                throws SQLException
            {
                return RoundupPostDAO.this.getByUrl(siteId, code, id);
            }
        };
    }

    /**
     * Returns a list of roundups from the ROUNDUPS table by URL.
     */
    public synchronized List<RoundupPost> listByUrl(String code, String url, long publishedDate) throws SQLException
    {
        List<RoundupPost> ret = null;

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
            listByUrlStmt.setLong(3, publishedDate);
            listByUrlStmt.setTimestamp(4, new Timestamp(publishedDate), UTC);
            listByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByUrlStmt.executeQuery();
            ret = new ArrayList<RoundupPost>();
            while(rs.next())
            {
                RoundupPost content = new RoundupPost();
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
     * Returns the roundup items from the table by organisation code.
     */
    public synchronized List<RoundupPostItem> listItems(Site site, String code) throws SQLException
    {
        List<RoundupPostItem> ret = null;

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
            ret = new ArrayList<RoundupPostItem>();
            while(rs.next())
            {
                RoundupPostItem post = new RoundupPostItem();
                post.setUuid(rs.getString(1));
                post.setSiteId(rs.getString(2));
                post.setCode(rs.getString(3));
                post.setId(rs.getInt(4));
                post.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                post.setTitle(rs.getString(6));
                post.setUrl(rs.getString(7));
                post.setPublished(rs.getBoolean(8));
                post.setPromoted(rs.getBoolean(9));
                post.setNewsletter(rs.getBoolean(10));
                post.setFeatured(rs.getBoolean(11));
                post.setSponsored(rs.getBoolean(12));
                post.setAuthor(rs.getString(13));
                post.setStatus(rs.getString(14));
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
     * Returns the roundup items from the table by published date.
     */
    public synchronized List<RoundupPostItem> listItems(Site site, Instant date) throws SQLException
    {
        List<RoundupPostItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByDateStmt == null)
            listByDateStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_DATE_SQL);
        clearParameters(listByDateStmt);

        ResultSet rs = null;

        try
        {
            listByDateStmt.setString(1, site.getId());
            listByDateStmt.setTimestamp(2, new Timestamp(date.toEpochMilli()), UTC);
            listByDateStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByDateStmt.executeQuery();
            ret = new ArrayList<RoundupPostItem>();
            while(rs.next())
            {
                RoundupPostItem post = new RoundupPostItem();
                post.setUuid(rs.getString(1));
                post.setSiteId(rs.getString(2));
                post.setCode(rs.getString(3));
                post.setId(rs.getInt(4));
                post.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                post.setTitle(rs.getString(6));
                post.setUrl(rs.getString(7));
                post.setPublished(rs.getBoolean(8));
                post.setPromoted(rs.getBoolean(9));
                post.setNewsletter(rs.getBoolean(10));
                post.setFeatured(rs.getBoolean(11));
                post.setSponsored(rs.getBoolean(12));
                post.setAuthor(rs.getString(13));
                post.setStatus(rs.getString(14));
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
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByUrlStmt);
        getByUrlStmt = null;
        closeStatement(listByUrlStmt);
        listByUrlStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByDateStmt);
        listByDateStmt = null;
    }

    private PreparedStatement getByUrlStmt;
    private PreparedStatement listByUrlStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByDateStmt;
}
