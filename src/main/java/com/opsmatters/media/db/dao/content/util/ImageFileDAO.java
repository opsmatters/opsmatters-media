/*
 * Copyright 2026 Gerald Curley
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
import com.opsmatters.media.model.content.util.ImageFile;
import com.opsmatters.media.model.content.util.ImageType;
import com.opsmatters.media.model.content.util.FileStatus;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.util.StringUtils;

/**
 * DAO that provides operations on the IMAGE_FILES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImageFileDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ImageFileDAO.class.getName());

    /**
     * The query to use to select an image from the IMAGE_FILES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, OLD_FILENAME, STATUS, SESSION_ID "
      + "FROM IMAGE_FILES WHERE ID=?";

    /**
     * The query to use to select an image from the IMAGE_FILES table by filename.
     */
    private static final String GET_BY_FILENAME_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, OLD_FILENAME, STATUS, SESSION_ID "
      + "FROM IMAGE_FILES WHERE FILENAME=?";

    /**
     * The query to use to insert an image into the IMAGE_FILES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO IMAGE_FILES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, OLD_FILENAME, STATUS, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an image in the IMAGE_FILES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE IMAGE_FILES SET UPDATED_DATE=?, FILENAME=?, OLD_FILENAME=?, STATUS=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the images from the IMAGE_FILES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, OLD_FILENAME, STATUS, SESSION_ID "
      + "FROM IMAGE_FILES";

    /**
     * The query to use to select the images from the IMAGE_FILES table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, FILENAME, OLD_FILENAME, STATUS, SESSION_ID "
      + "FROM IMAGE_FILES WHERE STATUS=?";

    /**
     * The query to use to get the count of images from the IMAGE_FILES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM IMAGE_FILES";

    /**
     * The query to use to delete an image from the IMAGE_FILES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM IMAGE_FILES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ImageFileDAO(ContentUtilDAOFactory factory)
    {
        super(factory, "IMAGE_FILES");
    }

    /**
     * Defines the columns and indices for the IMAGE_FILES table.
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
        table.addColumn("OLD_FILENAME", Types.VARCHAR, 128, false);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("IMAGE_FILES_PK", new String[] {"ID"});
        table.addIndex("IMAGE_FILES_FILENAME_IDX", new String[] {"FILENAME"});
        table.setInitialised(true);
    }

    /**
     * Returns a file from the IMAGE_FILES table by id.
     */
    public synchronized ImageFile getById(String id) throws SQLException
    {
        ImageFile ret = null;

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
                ImageFile file = new ImageFile();
                file.setId(rs.getString(1));
                file.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                file.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                file.setCode(rs.getString(4));
                file.setType(ImageType.valueOf(rs.getString(5)));
                file.setFilename(rs.getString(6));
                file.setOldFilename(rs.getString(7));
                file.setStatus(rs.getString(8));
                file.setSessionId(rs.getInt(9));
                ret = file;
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
     * Returns a file from the IMAGE_FILES table by filename.
     */
    public synchronized ImageFile getByFilename(String filename) throws SQLException
    {
        ImageFile ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByFilenameStmt == null)
            getByFilenameStmt = prepareStatement(getConnection(), GET_BY_FILENAME_SQL);
        clearParameters(getByFilenameStmt);

        ResultSet rs = null;

        try
        {
            getByFilenameStmt.setString(1, filename);
            getByFilenameStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByFilenameStmt.executeQuery();
            while(rs.next())
            {
                ImageFile file = new ImageFile();
                file.setId(rs.getString(1));
                file.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                file.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                file.setCode(rs.getString(4));
                file.setType(ImageType.valueOf(rs.getString(5)));
                file.setFilename(rs.getString(6));
                file.setOldFilename(rs.getString(7));
                file.setStatus(rs.getString(8));
                file.setSessionId(rs.getInt(9));
                ret = file;
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
     * Stores the given file in the IMAGE_FILES table.
     */
    public synchronized void add(ImageFile file) throws SQLException
    {
        if(!hasConnection() || file == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, file.getId());
            insertStmt.setTimestamp(2, new Timestamp(file.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(file.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, file.getCode());
            insertStmt.setString(5, file.getType().name());
            insertStmt.setString(6, file.getFilename());
            insertStmt.setString(7, file.getOldFilename());
            insertStmt.setString(8, file.getStatus().name());
            insertStmt.setInt(9, file.getSessionId());
            insertStmt.executeUpdate();

            logger.info(String.format("Created file %s in IMAGE_FILES", file.getId()));
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
     * Updates the given file in the IMAGE_FILES table.
     */
    public synchronized void update(ImageFile file) throws SQLException
    {
        if(!hasConnection() || file == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(file.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, file.getFilename());
        updateStmt.setString(3, file.getOldFilename());
        updateStmt.setString(4, file.getStatus().name());
        updateStmt.setInt(5, file.getSessionId());
        updateStmt.setString(6, file.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated file %s in IMAGE_FILES", file.getId()));
    }

    /**
     * Adds or Updates the given file in the IMAGE_FILES table.
     */
    public boolean upsert(ImageFile image) throws SQLException
    {
        boolean ret = false;

        ImageFile existing = getById(image.getId());
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
     * Returns the files from the IMAGE_FILES table.
     */
    public synchronized List<ImageFile> list() throws SQLException
    {
        List<ImageFile> ret = null;

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
            ret = new ArrayList<ImageFile>();
            while(rs.next())
            {
                ImageFile file = new ImageFile();
                file.setId(rs.getString(1));
                file.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                file.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                file.setCode(rs.getString(4));
                file.setType(ImageType.valueOf(rs.getString(5)));
                file.setFilename(rs.getString(6));
                file.setOldFilename(rs.getString(7));
                file.setStatus(rs.getString(8));
                file.setSessionId(rs.getInt(9));
                ret.add(file);
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
     * Returns the files from the IMAGE_FILES table by status.
     */
    public synchronized List<ImageFile> list(FileStatus status) throws SQLException
    {
        List<ImageFile> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByStatusStmt == null)
            listByStatusStmt = prepareStatement(getConnection(), LIST_BY_STATUS_SQL);
        clearParameters(listByStatusStmt);

        ResultSet rs = null;

        try
        {
            listByStatusStmt.setString(1, status.name());
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<ImageFile>();
            while(rs.next())
            {
                ImageFile file = new ImageFile();
                file.setId(rs.getString(1));
                file.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                file.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                file.setCode(rs.getString(4));
                file.setType(ImageType.valueOf(rs.getString(5)));
                file.setFilename(rs.getString(6));
                file.setOldFilename(rs.getString(7));
                file.setStatus(rs.getString(8));
                file.setSessionId(rs.getInt(9));
                ret.add(file);
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
     * Returns the count of files from the table.
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
     * Removes the given file from the IMAGE_FILES table.
     */
    public synchronized void delete(ImageFile image) throws SQLException
    {
        if(!hasConnection() || image == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, image.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted image %s in IMAGE_FILES", image.getId()));
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByFilenameStmt);
        getByFilenameStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement getByFilenameStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
