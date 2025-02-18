/*
 * Copyright 2020 Gerald Curley
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
import com.opsmatters.media.cache.platform.Sites;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.content.ContentSiteSettings;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;

/**
 * DAO that provides operations on the CONTENT_SITE_SETTINGS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentSiteSettingsDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentSiteSettingsDAO.class.getName());

    /**
     * The query to use to select settings from the CONTENT_SITE_SETTINGS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_SITE_SETTINGS WHERE ID=?";

    /**
     * The query to use to select the settings from the CONTENT_SITE_SETTINGS table.
     */
    private static final String GET_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_SITE_SETTINGS WHERE SITE_ID=? AND CODE=? AND CONTENT_TYPE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to insert settings into the CONTENT_SITE_SETTINGS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_SITE_SETTINGS"
      + "( ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update settings in the CONTENT_SITE_SETTINGS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_SITE_SETTINGS SET UPDATED_DATE=?, ATTRIBUTES=?, ITEM_COUNT=?, DEPLOYED=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the settings from the CONTENT_SITE_SETTINGS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_SITE_SETTINGS WHERE SITE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the settings from the CONTENT_SITE_SETTINGS table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, CODE, CONTENT_TYPE, ATTRIBUTES, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_SITE_SETTINGS WHERE SITE_ID=? AND CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of settings from the CONTENT_SITE_SETTINGS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_SITE_SETTINGS";

    /**
     * The query to use to delete settings from the CONTENT_SITE_SETTINGS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_SITE_SETTINGS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentSiteSettingsDAO(ContentDAOFactory factory)
    {
        super(factory, "CONTENT_SITE_SETTINGS");
    }

    /**
     * Defines the columns and indices for the CONTENT_SITE_SETTINGS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("ITEM_COUNT", Types.INTEGER, true);
        table.addColumn("DEPLOYED", Types.BOOLEAN, true);
        table.setPrimaryKey("CONTENT_SITE_SETTINGS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_SITE_SETTINGS_CODE_IDX", new String[] {"SITE_ID","CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns settings from the CONTENT_SITE_SETTINGS table by id.
     */
    public synchronized ContentSiteSettings getById(String id) throws SQLException
    {
        ContentSiteSettings ret = null;

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
                ContentSiteSettings settings = new ContentSiteSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setSiteId(rs.getString(4));
                settings.setCode(rs.getString(5));
                settings.setType(rs.getString(6));
                settings.setAttributes(new JSONObject(getClob(rs, 7)));
                settings.setItemCount(rs.getInt(8));
                settings.setDeployed(rs.getBoolean(9));
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
     * Returns settings from the CONTENT_SITE_SETTINGS table by site, code and content type.
     */
    public synchronized ContentSiteSettings get(Site site, String code, ContentType type) throws SQLException
    {
        ContentSiteSettings ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByTypeStmt == null)
            getByTypeStmt = prepareStatement(getConnection(), GET_BY_TYPE_SQL);
        clearParameters(getByTypeStmt);

        ResultSet rs = null;

        try
        {
            getByTypeStmt.setString(1, site.getId());
            getByTypeStmt.setString(2, code);
            getByTypeStmt.setString(3, type.name());
            rs = getByTypeStmt.executeQuery();
            while(rs.next())
            {
                ContentSiteSettings settings = new ContentSiteSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setSiteId(rs.getString(4));
                settings.setCode(rs.getString(5));
                settings.setType(rs.getString(6));
                settings.setAttributes(new JSONObject(getClob(rs, 7)));
                settings.setItemCount(rs.getInt(8));
                settings.setDeployed(rs.getBoolean(9));
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
     * Stores the given settings in the CONTENT_SITE_SETTINGS table.
     */
    public synchronized void add(ContentSiteSettings settings) throws SQLException
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
            insertStmt.setString(4, settings.getSiteId());
            insertStmt.setString(5, settings.getCode());
            insertStmt.setString(6, settings.getType().name());
            String attributes = settings.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(7, reader, attributes.length());
            insertStmt.setLong(8, settings.getItemCount());
            insertStmt.setBoolean(9, settings.isDeployed());
            insertStmt.executeUpdate();

            logger.info("Created settings '"+settings.getId()+"' in CONTENT_SITE_SETTINGS");
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
     * Updates the given settings in the CONTENT_SITE_SETTINGS table.
     */
    public synchronized void update(ContentSiteSettings settings) throws SQLException
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
            updateStmt.setLong(3, settings.getItemCount());
            updateStmt.setBoolean(4, settings.isDeployed());
            updateStmt.setString(5, settings.getId());
            updateStmt.executeUpdate();

            logger.info("Updated settings '"+settings.getId()+"' in CONTENT_SITE_SETTINGS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Adds or updates the given settings in the CONTENT_SITE_SETTINGS table.
     */
    public boolean upsert(ContentSiteSettings settings) throws SQLException
    {
        boolean ret = false;

        ContentSiteSettings existing = getById(settings.getId());
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
     * Returns the settings from the CONTENT_SITE_SETTINGS table.
     */
    public synchronized List<ContentSiteSettings> list(Site site) throws SQLException
    {
        List<ContentSiteSettings> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setString(1, site.getId());
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<ContentSiteSettings>();
            while(rs.next())
            {
                ContentSiteSettings settings = new ContentSiteSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setSiteId(rs.getString(4));
                settings.setCode(rs.getString(5));
                settings.setType(rs.getString(6));
                settings.setAttributes(new JSONObject(getClob(rs, 7)));
                settings.setItemCount(rs.getInt(8));
                settings.setDeployed(rs.getBoolean(9));
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
     * Returns the settings from the CONTENT_SITE_SETTINGS table by organisation code.
     */
    public synchronized List<ContentSiteSettings> list(Site site, String code) throws SQLException
    {
        List<ContentSiteSettings> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, site.getId());
            listByCodeStmt.setString(2, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<ContentSiteSettings>();
            while(rs.next())
            {
                ContentSiteSettings settings = new ContentSiteSettings();
                settings.setId(rs.getString(1));
                settings.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                settings.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                settings.setSiteId(rs.getString(4));
                settings.setCode(rs.getString(5));
                settings.setType(rs.getString(6));
                settings.setAttributes(new JSONObject(getClob(rs, 7)));
                settings.setItemCount(rs.getInt(8));
                settings.setDeployed(rs.getBoolean(9));
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
     * Removes the given settings from the CONTENT_SITE_SETTINGS table.
     */
    public synchronized void delete(ContentSiteSettings settings) throws SQLException
    {
        if(!hasConnection() || settings == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, settings.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted settings '"+settings.getId()+"' in CONTENT_SITE_SETTINGS");
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
