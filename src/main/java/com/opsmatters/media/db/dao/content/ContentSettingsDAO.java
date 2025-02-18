/*
 * Copyright 2024 Gerald Curley
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
import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSettings;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;

/**
 * DAO that provides operations on the CONTENT_SETTINGS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSettingsDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentSettingsDAO.class.getName());

    /**
     * The query to use to select settings from the CONTENT_SETTINGS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ATTRIBUTES, CONFIG "
      + "FROM CONTENT_SETTINGS WHERE ID=?";

    /**
     * The query to use to select the settings from the CONTENT_SETTINGS table.
     */
    private static final String GET_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ATTRIBUTES, CONFIG "
      + "FROM CONTENT_SETTINGS WHERE CODE=? AND CONTENT_TYPE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to insert settings into the CONTENT_SETTINGS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_SETTINGS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ATTRIBUTES, CONFIG )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update settings in the CONTENT_SETTINGS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_SETTINGS SET UPDATED_DATE=?, ATTRIBUTES=?, CONFIG=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the settings from the CONTENT_SETTINGS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ATTRIBUTES, CONFIG "
      + "FROM CONTENT_SETTINGS ORDER BY CREATED_DATE";

    /**
     * The query to use to select the settings from the CONTENT_SETTINGS table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ATTRIBUTES, CONFIG "
      + "FROM CONTENT_SETTINGS WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of settings from the CONTENT_SETTINGS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_SETTINGS";

    /**
     * The query to use to delete settings from the CONTENT_SETTINGS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_SETTINGS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentSettingsDAO(ContentDAOFactory factory)
    {
        super(factory, "CONTENT_SETTINGS");
    }

    /**
     * Defines the columns and indices for the CONTENT_SETTINGS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("CONFIG", Types.LONGVARCHAR, false);
        table.setPrimaryKey("CONTENT_SETTINGS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_SETTINGS_CODE_IDX", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns settings from the CONTENT_SETTINGS table by id.
     */
    public synchronized ContentSettings getById(String id) throws SQLException
    {
        ContentSettings ret = null;

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
                ContentSettings settings = new ContentSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setCode(rs.getString(4));
                settings.setType(rs.getString(5));
                settings.setAttributes(new JSONObject(getClob(rs, 6)));
                settings.setConfig(rs.getString(7));
                ret = settings;
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
     * Returns settings from the CONTENT_SETTINGS table by code and content type.
     */
    public synchronized ContentSettings get(String code, ContentType type) throws SQLException
    {
        ContentSettings ret = null;

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
            rs = getByTypeStmt.executeQuery();
            while(rs.next())
            {
                ContentSettings settings = new ContentSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setCode(rs.getString(4));
                settings.setType(rs.getString(5));
                settings.setAttributes(new JSONObject(getClob(rs, 6)));
                settings.setConfig(rs.getString(7));
                ret = settings;
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
     * Returns settings from the CONTENT_SETTINGS table for the given content item.
     */
    public ContentSettings get(Content content) throws SQLException
    {
        return get(content.getCode(), content.getType());
    }

    /**
     * Stores the given settings in the CONTENT_SETTINGS table.
     */
    public synchronized void add(ContentSettings settings) throws SQLException
    {
        if(!hasConnection() || settings == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, settings.getId());
            insertStmt.setTimestamp(2, new Timestamp(settings.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(settings.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, settings.getCode());
            insertStmt.setString(5, settings.getType().name());
            String attributes = settings.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(6, reader, attributes.length());
            insertStmt.setString(7, settings.getConfig());
            insertStmt.executeUpdate();

            logger.info("Created settings '"+settings.getId()+"' in CONTENT_SETTINGS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the settings already exists
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
     * Updates the given settings in the CONTENT_SETTINGS table.
     */
    public synchronized void update(ContentSettings settings) throws SQLException
    {
        if(!hasConnection() || settings == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(settings.getUpdatedDateMillis()), UTC);
            String attributes = settings.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(2, reader, attributes.length());
            updateStmt.setString(3, settings.getConfig());
            updateStmt.setString(4, settings.getId());
            updateStmt.executeUpdate();

            logger.info("Updated settings '"+settings.getId()+"' in CONTENT_SETTINGS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Adds or updates the given settings in the CONTENT_SETTINGS table.
     */
    public boolean upsert(ContentSettings settings) throws SQLException
    {
        boolean ret = false;

        ContentSettings existing = getById(settings.getId());
        if(existing != null)
        {
            update(settings);
        }
        else
        {
            add(settings);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the settings from the CONTENT_SETTINGS table.
     */
    public synchronized List<ContentSettings> list() throws SQLException
    {
        List<ContentSettings> ret = null;

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
            ret = new ArrayList<ContentSettings>();
            while(rs.next())
            {
                ContentSettings settings = new ContentSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setCode(rs.getString(4));
                settings.setType(rs.getString(5));
                settings.setAttributes(new JSONObject(getClob(rs, 6)));
                settings.setConfig(rs.getString(7));
                ret.add(settings);
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
     * Returns the settings from the CONTENT_SETTINGS table by organisation code.
     */
    public synchronized List<ContentSettings> list(String code) throws SQLException
    {
        List<ContentSettings> ret = null;

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
            ret = new ArrayList<ContentSettings>();
            while(rs.next())
            {
                ContentSettings settings = new ContentSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setCode(rs.getString(4));
                settings.setType(rs.getString(5));
                settings.setAttributes(new JSONObject(getClob(rs, 6)));
                settings.setConfig(rs.getString(7));
                ret.add(settings);
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
     * Returns the count of settings from the table.
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
     * Removes the given settings from the CONTENT_SETTINGS table.
     */
    public synchronized void delete(ContentSettings settings) throws SQLException
    {
        if(!hasConnection() || settings == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, settings.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted settings '"+settings.getId()+"' in CONTENT_SETTINGS");
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
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement getByTypeStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
