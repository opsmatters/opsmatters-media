/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.db.dao.content.util;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.util.ContentImage;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.util.StringUtils;

/**
 * DAO that provides operations on the CONTENT_IMAGES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentImageDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentImageDAO.class.getName());

    /**
     * The query to use to select an image from the CONTENT_IMAGES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS "
      + "FROM CONTENT_IMAGES WHERE ID=?";

    /**
     * The query to use to select an image from the CONTENT_IMAGES table by type.
     */
    private static final String GET_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS "
      + "FROM CONTENT_IMAGES WHERE CODE=? AND TYPE=?";

    /**
     * The query to use to select an image from the CONTENT_IMAGES table by filename.
     */
    private static final String GET_BY_FILENAME_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS "
      + "FROM CONTENT_IMAGES WHERE CODE=? AND TYPE=? AND FILENAME=?";

    /**
     * The query to use to insert an image into the CONTENT_IMAGES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_IMAGES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an image in the CONTENT_IMAGES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_IMAGES SET UPDATED_DATE=?, FILENAME=?, TEXT=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the images from the CONTENT_IMAGES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS "
      + "FROM CONTENT_IMAGES";

    /**
     * The query to use to select the images from the CONTENT_IMAGES table by code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS "
      + "FROM CONTENT_IMAGES WHERE CODE=?";

    /**
     * The query to use to select the images from the CONTENT_IMAGES table by type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, TEXT, STATUS "
      + "FROM CONTENT_IMAGES WHERE CODE=? AND TYPE=?";

    /**
     * The query to use to get the count of images from the CONTENT_IMAGES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_IMAGES";

    /**
     * The query to use to delete an image from the CONTENT_IMAGES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_IMAGES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentImageDAO(ContentUtilDAOFactory factory)
    {
        super(factory, "CONTENT_IMAGES");
    }

    /**
     * Defines the columns and indices for the CONTENT_IMAGES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("TYPE", Types.VARCHAR, 20, true);
        table.addColumn("FILENAME", Types.VARCHAR, 128, true);
        table.addColumn("\"TEXT\"", Types.VARCHAR, 30, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("CONTENT_IMAGES_PK", new String[] {"ID"});
        table.addIndex("CONTENT_IMAGES_CODE_IDX", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns an image from the CONTENT_IMAGES table by id.
     */
    public synchronized ContentImage getById(String id) throws SQLException
    {
        ContentImage ret = null;

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
                ContentImage image = new ContentImage();
                image.setId(rs.getString(1));
                image.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                image.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                image.setCode(rs.getString(4));
                image.setType(ImageType.valueOf(rs.getString(5)));
                image.setFilename(rs.getString(6));
                image.setText(rs.getString(7));
                image.setStatus(rs.getString(8));
                ret = image;
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
     * Returns an image from the CONTENT_IMAGES table by type.
     */
    public synchronized ContentImage getByType(String code, ImageType type) throws SQLException
    {
        ContentImage ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByTypeStmt == null)
            getByTypeStmt = prepareStatement(getConnection(), GET_BY_TYPE_SQL);
        clearParameters(getByTypeStmt);

        ResultSet rs = null;

        try
        {
            getByTypeStmt.setString(1, code);
            getByTypeStmt.setString(2, type.name());
            getByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByTypeStmt.executeQuery();
            while(rs.next())
            {
                ContentImage image = new ContentImage();
                image.setId(rs.getString(1));
                image.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                image.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                image.setCode(rs.getString(4));
                image.setType(ImageType.valueOf(rs.getString(5)));
                image.setFilename(rs.getString(6));
                image.setText(rs.getString(7));
                image.setStatus(rs.getString(8));
                ret = image;
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
     * Returns an image from the CONTENT_IMAGES table by filename.
     */
    public synchronized ContentImage getByFilename(String code, ImageType type, String filename) throws SQLException
    {
        ContentImage ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByFilenameStmt == null)
            getByFilenameStmt = prepareStatement(getConnection(), GET_BY_FILENAME_SQL);
        clearParameters(getByFilenameStmt);

        ResultSet rs = null;

        try
        {
            getByFilenameStmt.setString(1, code);
            getByFilenameStmt.setString(2, type.name());
            getByFilenameStmt.setString(3, filename);
            getByFilenameStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByFilenameStmt.executeQuery();
            while(rs.next())
            {
                ContentImage image = new ContentImage();
                image.setId(rs.getString(1));
                image.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                image.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                image.setCode(rs.getString(4));
                image.setType(ImageType.valueOf(rs.getString(5)));
                image.setFilename(rs.getString(6));
                image.setText(rs.getString(7));
                image.setStatus(rs.getString(8));
                ret = image;
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
     * Stores the given image in the CONTENT_IMAGES table.
     */
    public synchronized void add(ContentImage image) throws SQLException
    {
        if(!hasConnection() || image == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, image.getId());
            insertStmt.setTimestamp(2, new Timestamp(image.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(image.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, image.getCode());
            insertStmt.setString(5, image.getType().name());
            insertStmt.setString(6, image.getFilename());
            insertStmt.setString(7, image.getText());
            insertStmt.setString(8, image.getStatus().name());
            insertStmt.executeUpdate();

            logger.info(String.format("Created image %s in CONTENT_IMAGES", image.getId()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the image already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given image in the CONTENT_IMAGES table.
     */
    public synchronized void update(ContentImage image) throws SQLException
    {
        if(!hasConnection() || image == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(image.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, image.getFilename());
        updateStmt.setString(3, image.getText());
        updateStmt.setString(4, image.getStatus().name());
        updateStmt.setString(5, image.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated image %s in CONTENT_IMAGES", image.getId()));
    }

    /**
     * Adds or Updates the given image in the CONTENT_IMAGES table.
     */
    public boolean upsert(ContentImage image) throws SQLException
    {
        boolean ret = false;

        ContentImage existing = getById(image.getId());
        if(existing != null)
        {
            update(image);
        }
        else
        {
            add(image);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the images from the CONTENT_IMAGES table.
     */
    public synchronized List<ContentImage> list() throws SQLException
    {
        List<ContentImage> ret = null;

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
            ret = new ArrayList<ContentImage>();
            while(rs.next())
            {
                ContentImage image = new ContentImage();
                image.setId(rs.getString(1));
                image.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                image.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                image.setCode(rs.getString(4));
                image.setType(ImageType.valueOf(rs.getString(5)));
                image.setFilename(rs.getString(6));
                image.setText(rs.getString(7));
                image.setStatus(rs.getString(8));
                ret.add(image);
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
     * Returns the images from the CONTENT_IMAGES table by code.
     */
    public synchronized List<ContentImage> list(String code) throws SQLException
    {
        List<ContentImage> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<ContentImage>();
            while(rs.next())
            {
                ContentImage image = new ContentImage();
                image.setId(rs.getString(1));
                image.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                image.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                image.setCode(rs.getString(4));
                image.setType(ImageType.valueOf(rs.getString(5)));
                image.setFilename(rs.getString(6));
                image.setText(rs.getString(7));
                image.setStatus(rs.getString(8));
                ret.add(image);
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
     * Returns the images from the CONTENT_IMAGES table by code and type.
     */
    public synchronized List<ContentImage> list(String code, ImageType type) throws SQLException
    {
        List<ContentImage> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, code);
            listByTypeStmt.setString(2, type.name());
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<ContentImage>();
            while(rs.next())
            {
                ContentImage image = new ContentImage();
                image.setId(rs.getString(1));
                image.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                image.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                image.setCode(rs.getString(4));
                image.setType(ImageType.valueOf(rs.getString(5)));
                image.setFilename(rs.getString(6));
                image.setText(rs.getString(7));
                image.setStatus(rs.getString(8));
                ret.add(image);
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
     * Returns the count of images from the table.
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
     * Removes the given image from the CONTENT_IMAGES table.
     */
    public synchronized void delete(ContentImage image) throws SQLException
    {
        if(!hasConnection() || image == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, image.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted image %s in CONTENT_IMAGES", image.getId()));
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByTypeStmt);
        getByTypeStmt = null;
        closeStatement(getByFilenameStmt);
        getByFilenameStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByTypeStmt);
        listByTypeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement getByTypeStmt;
    private PreparedStatement getByFilenameStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
