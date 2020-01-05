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
package com.opsmatters.media.db.dao.content;

import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.content.VideoArticle;

/**
 * DAO that provides operations on the VIDEOS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class VideoDAO extends ContentDAO<VideoArticle>
{
    private static final Logger logger = Logger.getLogger(VideoDAO.class.getName());

    /**
     * The query to use to select a video from the VIDEOS table by videoId.
     */
    private static final String GET_BY_VIDEO_ID_SQL =  
      "SELECT ATTRIBUTES FROM VIDEOS WHERE CODE=? AND VIDEO_ID=?";

    /**
     * The query to use to insert a video into the VIDEOS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO VIDEOS"
      + "( CODE, ID, PUBLISHED_DATE, UUID, VIDEO_ID, VIDEO_TYPE, PROVIDER, PUBLISHED, CREATED_BY, ATTRIBUTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a video in the VIDEOS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE VIDEOS SET PUBLISHED_DATE=?, UUID=?, VIDEO_ID=?, VIDEO_TYPE=?, PROVIDER=?, PUBLISHED=?, ATTRIBUTES=? "
      + "WHERE CODE=? AND ID=?";

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
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("VIDEO_ID", Types.VARCHAR, 30, true);
        table.addColumn("VIDEO_TYPE", Types.VARCHAR, 30, true);
        table.addColumn("PROVIDER", Types.VARCHAR, 15, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("VIDEOS_PK", new String[] {"CODE","ID"});
        table.addIndex("VIDEOS_UUID_IDX", new String[] {"CODE","UUID"});
        table.addIndex("VIDEOS_VIDEO_ID_IDX", new String[] {"CODE","VIDEO_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a video from the VIDEOS table by videoId.
     */
    public VideoArticle getByVideoId(String code, String videoId) throws SQLException
    {
        VideoArticle ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByVideoIdStmt == null)
            getByVideoIdStmt = prepareStatement(getConnection(), GET_BY_VIDEO_ID_SQL);
        clearParameters(getByVideoIdStmt);

        ResultSet rs = null;

        try
        {
            getByVideoIdStmt.setString(1, code);
            getByVideoIdStmt.setString(2, videoId);
            getByVideoIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByVideoIdStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = new VideoArticle(attributes);
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
    public void add(VideoArticle content) throws SQLException
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
            insertStmt.setString(1, content.getCode());
            insertStmt.setInt(2, content.getId());
            insertStmt.setTimestamp(3, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setString(4, content.getUuid());
            insertStmt.setString(5, content.getVideoId());
            insertStmt.setString(6, content.getVideoType());
            insertStmt.setString(7, content.getProvider().code());
            insertStmt.setBoolean(8, content.isPublished());
            insertStmt.setString(9, content.getCreatedBy());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(10, reader, attributes.length());
            insertStmt.executeUpdate();

            logger.info("Created video '"+content.getTitle()+"' in VIDEOS"
                +" (id="+content.getId()+" uuid="+content.getUuid()+")");
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
    public void update(VideoArticle content) throws SQLException
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
            updateStmt.setTimestamp(1, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(2, content.getUuid());
            updateStmt.setString(3, content.getVideoId());
            updateStmt.setString(4, content.getVideoType());
            updateStmt.setString(5, content.getProvider().code());
            updateStmt.setBoolean(6, content.isPublished());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(7, reader, attributes.length());
            updateStmt.setString(8, content.getCode());
            updateStmt.setInt(9, content.getId());
            updateStmt.executeUpdate();

            logger.info("Updated video '"+content.getTitle()+"' in VIDEOS"
                +" (id="+content.getId()+" uuid="+content.getUuid()+")");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByVideoIdStmt);
        getByVideoIdStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
    }

    private PreparedStatement getByVideoIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
}
