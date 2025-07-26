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
import com.opsmatters.media.model.MessageFormat;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.ContentType;
import com.opsmatters.media.model.social.SocialTemplate;
import com.opsmatters.media.model.social.SocialTemplateItem;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the SOCIAL_TEMPLATES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialTemplateDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(SocialTemplateDAO.class.getName());

    /**
     * The query to use to select a template from the SOCIAL_TEMPLATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, CONTENT_TYPE, SITE_ID, MESSAGE, ATTRIBUTES, WEIGHT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM SOCIAL_TEMPLATES WHERE ID=?";

    /**
     * The query to use to insert a template into the SOCIAL_TEMPLATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SOCIAL_TEMPLATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, CONTENT_TYPE, SITE_ID, MESSAGE, ATTRIBUTES, WEIGHT, SHORTEN_URL, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a template in the SOCIAL_TEMPLATES table.
     */
    private static final String UPDATE_SQL =
      "UPDATE SOCIAL_TEMPLATES SET UPDATED_DATE=?, NAME=?, CONTENT_TYPE=?, SITE_ID=?, MESSAGE=?, ATTRIBUTES=?, WEIGHT=?, SHORTEN_URL=?, STATUS=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the templates from the SOCIAL_TEMPLATES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, CONTENT_TYPE, SITE_ID, MESSAGE, ATTRIBUTES, WEIGHT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM SOCIAL_TEMPLATES";

    /**
     * The query to use to select the template items from the SOCIAL_TEMPLATES table.
     */
    private static final String LIST_ITEMS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, CONTENT_TYPE, SITE_ID, WEIGHT, STATUS "
      + "FROM SOCIAL_TEMPLATES";

    /**
     * The query to use to select the templates from the SOCIAL_TEMPLATES table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, CONTENT_TYPE, SITE_ID, MESSAGE, ATTRIBUTES, WEIGHT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM SOCIAL_TEMPLATES WHERE SITE_ID=?";

    /**
     * The query to use to select the templates from the SOCIAL_TEMPLATES table by content type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, CONTENT_TYPE, SITE_ID, MESSAGE, ATTRIBUTES, WEIGHT, SHORTEN_URL, STATUS, CREATED_BY "
      + "FROM SOCIAL_TEMPLATES WHERE CONTENT_TYPE=?";

    /**
     * The query to use to get the count of templates from the SOCIAL_TEMPLATES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SOCIAL_TEMPLATES";

    /**
     * The query to use to delete a template from the SOCIAL_TEMPLATES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SOCIAL_TEMPLATES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SocialTemplateDAO(SocialDAOFactory factory)
    {
        super(factory, "SOCIAL_TEMPLATES");
    }

    /**
     * Defines the columns and indices for the SOCIAL_TEMPLATES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 128, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, false);
        table.addColumn("MESSAGE", Types.LONGVARCHAR, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("WEIGHT", Types.INTEGER, true);
        table.addColumn("SHORTEN_URL", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SOCIAL_TEMPLATES_PK", new String[] {"ID"});
        table.addIndex("SOCIAL_TEMPLATES_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a template from the SOCIAL_TEMPLATES table by id.
     */
    public synchronized SocialTemplate getById(String id) throws SQLException
    {
        SocialTemplate ret = null;

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
                SocialTemplate template = new SocialTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setContentType(rs.getString(5));
                template.setSiteId(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setAttributes(new JSONObject(getClob(rs, 8)));
                template.setWeight(rs.getInt(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret = template;
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
     * Stores the given template in the SOCIAL_TEMPLATES table.
     */
    public synchronized void add(SocialTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, template.getId());
            insertStmt.setTimestamp(2, new Timestamp(template.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(template.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, template.getName());
            insertStmt.setString(5, template.getContentType() != null ? template.getContentType().name(): "");
            insertStmt.setString(6, template.getSiteId());
            insertStmt.setString(7, template.getMessage(MessageFormat.ENCODED));
            String attributes = template.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(8, reader, attributes.length());
            insertStmt.setInt(9, template.getWeight());
            insertStmt.setBoolean(10, template.isShortenUrl());
            insertStmt.setString(11, template.getStatus().name());
            insertStmt.setString(12, template.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created template '"+template.getId()+"' in SOCIAL_TEMPLATES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the template already exists
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
     * Updates the given template in the SOCIAL_TEMPLATES table.
     */
    public synchronized void update(SocialTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(template.getUpdatedDateMillis()), UTC);
            updateStmt.setString(2, template.getName());
            updateStmt.setString(3, template.getContentType() != null ? template.getContentType().name(): "");
            updateStmt.setString(4, template.getSiteId());
            updateStmt.setString(5, template.getMessage(MessageFormat.ENCODED));
            String attributes = template.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader, attributes.length());
            updateStmt.setInt(7, template.getWeight());
            updateStmt.setBoolean(8, template.isShortenUrl());
            updateStmt.setString(9, template.getStatus().name());
            updateStmt.setString(10, template.getCreatedBy());
            updateStmt.setString(11, template.getId());
            updateStmt.executeUpdate();

            logger.info("Updated template '"+template.getId()+"' in SOCIAL_TEMPLATES");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Adds or Updates the given template in the SOCIAL_TEMPLATES table.
     */
    public boolean upsert(SocialTemplate template) throws SQLException
    {
        boolean ret = false;

        SocialTemplate existing = getById(template.getId());
        if(existing != null)
        {
            update(template);
        }
        else
        {
            add(template);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the templates from the SOCIAL_TEMPLATES table.
     */
    public synchronized List<SocialTemplate> list() throws SQLException
    {
        List<SocialTemplate> ret = null;

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
            ret = new ArrayList<SocialTemplate>();
            while(rs.next())
            {
                SocialTemplate template = new SocialTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setContentType(rs.getString(5));
                template.setSiteId(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setAttributes(new JSONObject(getClob(rs, 8)));
                template.setWeight(rs.getInt(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret.add(template);
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
     * Returns the template items from the SOCIAL_TEMPLATES table.
     */
    public synchronized List<SocialTemplateItem> listItems() throws SQLException
    {
        List<SocialTemplateItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsStmt == null)
            listItemsStmt = prepareStatement(getConnection(), LIST_ITEMS_SQL);
        clearParameters(listItemsStmt);

        ResultSet rs = null;

        try
        {
            listItemsStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsStmt.executeQuery();
            ret = new ArrayList<SocialTemplateItem>();
            while(rs.next())
            {
                SocialTemplateItem template = new SocialTemplateItem();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setContentType(rs.getString(5));
                template.setSiteId(rs.getString(6));
                template.setWeight(rs.getInt(7));
                template.setStatus(rs.getString(8));
                ret.add(template);
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
     * Returns the templates from the SOCIAL_TEMPLATES table by site.
     */
    public synchronized List<SocialTemplate> list(Site site) throws SQLException
    {
        List<SocialTemplate> ret = null;

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
            ret = new ArrayList<SocialTemplate>();
            while(rs.next())
            {
                SocialTemplate template = new SocialTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setContentType(rs.getString(5));
                template.setSiteId(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setAttributes(new JSONObject(getClob(rs, 8)));
                template.setWeight(rs.getInt(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));
                ret.add(template);
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
     * Returns the templates from the SOCIAL_TEMPLATES table by content type.
     */
    public synchronized List<SocialTemplate> list(ContentType contentType, Site site) throws SQLException
    {
        List<SocialTemplate> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, contentType.name());
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<SocialTemplate>();
            while(rs.next())
            {
                SocialTemplate template = new SocialTemplate();
                template.setId(rs.getString(1));
                template.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                template.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                template.setName(rs.getString(4));
                template.setContentType(rs.getString(5));
                template.setSiteId(rs.getString(6));
                template.setMessage(rs.getString(7));
                template.setAttributes(new JSONObject(getClob(rs, 8)));
                template.setWeight(rs.getInt(9));
                template.setShortenUrl(rs.getBoolean(10));
                template.setStatus(rs.getString(11));
                template.setCreatedBy(rs.getString(12));

                if(!template.hasSiteId()
                    || template.getSiteId().equals(site.getId()))
                {
                    ret.add(template);
                }
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
     * Returns the count of templates from the table.
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
     * Removes the given template from the SOCIAL_TEMPLATES table.
     */
    public synchronized void delete(SocialTemplate template) throws SQLException
    {
        if(!hasConnection() || template == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, template.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted template '"+template.getId()+"' in SOCIAL_TEMPLATES");
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
        closeStatement(listItemsStmt);
        listItemsStmt = null;
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listByTypeStmt);
        listByTypeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listItemsStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listByTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
