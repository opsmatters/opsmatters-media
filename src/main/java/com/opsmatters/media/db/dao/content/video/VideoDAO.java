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
package com.opsmatters.media.db.dao.content.video;

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
import com.opsmatters.media.model.content.ContentLookup;
import com.opsmatters.media.model.content.video.Video;
import com.opsmatters.media.model.content.video.VideoItem;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the VIDEOS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoDAO extends ContentDAO<Video>
{
    private static final Logger logger = Logger.getLogger(VideoDAO.class.getName());

    /**
     * The query to use to select a video from the VIDEOS table by videoId.
     */
    private static final String GET_BY_VIDEO_ID_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM VIDEOS WHERE SITE_ID=? AND CODE=? AND VIDEO_ID=?";

    /**
     * The query to use to insert a video into the VIDEOS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO VIDEOS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, VIDEO_ID, VIDEO_TYPE, DURATION, PROVIDER, PUBLISHED, PROMOTE, NEWSLETTER, "
      +   "STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a video in the VIDEOS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE VIDEOS SET UUID=?, PUBLISHED_DATE=?, TITLE=?, VIDEO_ID=?, VIDEO_TYPE=?, DURATION=?, PROVIDER=?, PUBLISHED=?, PROMOTE=?, NEWSLETTER=?, "
      +   "STATUS=?, CREATED_BY=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select a list of videos from the VIDEOS table by videoId.
     */
    private static final String LIST_BY_VIDEO_ID_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM VIDEOS WHERE CODE=? AND VIDEO_ID=?";

    /**
     * The query to use to select the video items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, VIDEO_ID, VIDEO_TYPE, DURATION, PUBLISHED, PROMOTE, NEWSLETTER, STATUS "
      + "FROM VIDEOS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * The query to use to select the video items from the table by published date.
     */
    private static final String LIST_ITEMS_BY_DATE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, VIDEO_ID, VIDEO_TYPE, DURATION, PUBLISHED, PROMOTE, NEWSLETTER, STATUS "
      + "FROM VIDEOS WHERE SITE_ID=? AND PUBLISHED=1 AND PUBLISHED_DATE>? AND STATUS != 'SKIPPED' ORDER BY ID";

    /**
     * Constructor that takes a DAO factory.
     */
    public VideoDAO(ContentDAOFactory factory)
    {
        super(factory, "VIDEOS");
    }

    /**
     * Defines the columns and indices for the VIDEOS table.
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
        table.addColumn("VIDEO_ID", Types.VARCHAR, 30, true);
        table.addColumn("VIDEO_TYPE", Types.VARCHAR, 30, true);
        table.addColumn("DURATION", Types.BIGINT, true);
        table.addColumn("PROVIDER", Types.VARCHAR, 15, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("NEWSLETTER", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("VIDEOS_PK", new String[] {"UUID"});
        table.addIndex("VIDEOS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("VIDEOS_TITLE_IDX", new String[] {"SITE_ID","CODE","TITLE"});
        table.addIndex("VIDEOS_VIDEO_ID_IDX", new String[] {"SITE_ID","CODE","VIDEO_ID"});
        table.addIndex("VIDEOS_CODE_VIDEO_ID_IDX", new String[] {"CODE","VIDEO_ID"});
        table.addIndex("VIDEOS_PUBLISHED_DATE_IDX", new String[] {"SITE_ID","PUBLISHED_DATE"});
        table.addIndex("VIDEOS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("VIDEOS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a video from the VIDEOS table by video id.
     */
    public synchronized Video getByVideoId(String siteId, String code, String videoId) throws SQLException
    {
        Video ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByVideoIdStmt == null)
            getByVideoIdStmt = prepareStatement(getConnection(), GET_BY_VIDEO_ID_SQL);
        clearParameters(getByVideoIdStmt);

        ResultSet rs = null;

        try
        {
            getByVideoIdStmt.setString(1, siteId);
            getByVideoIdStmt.setString(2, code);
            getByVideoIdStmt.setString(3, videoId);
            getByVideoIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByVideoIdStmt.executeQuery();
            while(rs.next())
            {
                Video content = new Video();
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
     * Returns a list of videos from the VIDEOS table by videoId.
     */
    public synchronized List<Video> listByVideoId(String code, String videoId) throws SQLException
    {
        List<Video> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByVideoIdStmt == null)
            listByVideoIdStmt = prepareStatement(getConnection(), LIST_BY_VIDEO_ID_SQL);
        clearParameters(listByVideoIdStmt);

        ResultSet rs = null;

        try
        {
            listByVideoIdStmt.setString(1, code);
            listByVideoIdStmt.setString(2, videoId);
            listByVideoIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByVideoIdStmt.executeQuery();
            ret = new ArrayList<Video>();
            while(rs.next())
            {
                Video content = new Video();
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
     * Stores the given video in the VIDEOS table.
     */
    public synchronized void add(Video content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("video uuid null");

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
            insertStmt.setString(7, content.getVideoId());
            insertStmt.setString(8, content.getVideoType());
            insertStmt.setLong(9, content.getDuration());
            insertStmt.setString(10, content.getProviderId().code());
            insertStmt.setBoolean(11, content.isPublished());
            insertStmt.setBoolean(12, content.isPromoted());
            insertStmt.setBoolean(13, content.isNewsletter());
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

            // Unique constraint violated means that the video already exists
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
     * Updates the given video in the VIDEOS table.
     */
    public synchronized void update(Video content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("video uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setString(1, content.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(3, content.getTitle());
            updateStmt.setString(4, content.getVideoId());
            updateStmt.setString(5, content.getVideoType());
            updateStmt.setLong(6, content.getDuration());
            updateStmt.setString(7, content.getProviderId().code());
            updateStmt.setBoolean(8, content.isPublished());
            updateStmt.setBoolean(9, content.isPromoted());
            updateStmt.setBoolean(10, content.isNewsletter());
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
    public ContentLookup<Video> newLookup()
    {
        return new ContentLookup<Video>()
        {
            @Override
            protected Video getByTitle(String siteId, String code, String title)
                throws SQLException
            {
                return VideoDAO.this.getByTitle(siteId, code, title);
            }

            @Override
            protected Video getById(String siteId, String code, String id)
                throws SQLException
            {
                return VideoDAO.this.getByVideoId(siteId, code, id);
            }
        };
    }

    /**
     * Returns the video items from the table by organisation code.
     */
    public synchronized List<VideoItem> listItems(Site site, String code) throws SQLException
    {
        List<VideoItem> ret = null;

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
            ret = new ArrayList<VideoItem>();
            while(rs.next())
            {
                VideoItem video = new VideoItem();
                video.setUuid(rs.getString(1));
                video.setSiteId(rs.getString(2));
                video.setCode(rs.getString(3));
                video.setId(rs.getInt(4));
                video.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                video.setTitle(rs.getString(6));
                video.setVideoId(rs.getString(7));
                video.setVideoType(rs.getString(8));
                video.setDuration(rs.getLong(9));
                video.setPublished(rs.getBoolean(10));
                video.setPromoted(rs.getBoolean(11));
                video.setNewsletter(rs.getBoolean(12));
                video.setStatus(rs.getString(13));
                ret.add(video);
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
     * Returns the video items from the table by published date.
     */
    public synchronized List<VideoItem> listItems(Site site, Instant date) throws SQLException
    {
        List<VideoItem> ret = null;

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
            ret = new ArrayList<VideoItem>();
            while(rs.next())
            {
                VideoItem video = new VideoItem();
                video.setUuid(rs.getString(1));
                video.setSiteId(rs.getString(2));
                video.setCode(rs.getString(3));
                video.setId(rs.getInt(4));
                video.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                video.setTitle(rs.getString(6));
                video.setVideoId(rs.getString(7));
                video.setVideoType(rs.getString(8));
                video.setDuration(rs.getLong(9));
                video.setPublished(rs.getBoolean(10));
                video.setPromoted(rs.getBoolean(11));
                video.setNewsletter(rs.getBoolean(12));
                video.setStatus(rs.getString(13));
                ret.add(video);
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
        closeStatement(getByVideoIdStmt);
        getByVideoIdStmt = null;
        closeStatement(listByVideoIdStmt);
        listByVideoIdStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByDateStmt);
        listByDateStmt = null;
    }

    private PreparedStatement getByVideoIdStmt;
    private PreparedStatement listByVideoIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByDateStmt;
}
