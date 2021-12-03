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

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.DAOFactory;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentItem;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.util.ClassUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * DAO that provides operations on a content table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentDAO<T extends ContentItem> extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentDAO.class.getName());

    /**
     * The query to use to select a content item from the table by UUID.
     */
    private static final String GET_BY_UUID_SQL =  
      "SELECT ATTRIBUTES, SITE_ID FROM %s WHERE SITE_ID=? AND CODE=? AND UUID=?";

    /**
     * The query to use to select a content item from the table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ATTRIBUTES, SITE_ID FROM %s WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select the content from the table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ATTRIBUTES, SITE_ID FROM %s WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * The query to use to select the content from the table by published date.
     */
    private static final String LIST_BY_DATE_SQL =  
      "SELECT ATTRIBUTES, SITE_ID FROM %s WHERE SITE_ID=? AND PUBLISHED=1 AND PUBLISHED_DATE>? AND STATUS != 'SKIPPED' ORDER BY ID";

    /**
     * The query to use to select the content from the table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ATTRIBUTES, SITE_ID FROM %s WHERE SITE_ID=? AND STATUS=? ORDER BY CODE";

    /**
     * The query to use to get the count of content items from the table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM %s";

    /**
     * The query to use to get the count of content items from the table by organisation.
     */
    private static final String COUNT_BY_CODE_SQL =
      "SELECT COUNT(*) FROM %s WHERE SITE_ID=? AND CODE=?";

    /**
     * The query to use to get the last ID from the table.
     */
    private static final String GET_MAX_ID_SQL =  
      "SELECT MAX(ID) FROM %s WHERE SITE_ID=? AND CODE=?";

    /**
     * The query to use to delete a content item from the table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM %s WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * Constructor that takes a DAO factory and a table name.
     */
    public ContentDAO(DAOFactory factory, String tableName)
    {
        super(factory, tableName);
    }

    /**
     * Stores the given content item in the table.
     */
    public abstract void add(T a) throws SQLException;

    /**
     * Updates the given content item in the table.
     */
    public abstract void update(T content) throws SQLException;

    /**
     * Returns a content item from the table by UUID.
     */
    public synchronized T getByUuid(String siteId, String code, String uuid) throws SQLException
    {
        T ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUuidStmt == null)
            getByUuidStmt = prepareStatement(getConnection(), String.format(GET_BY_UUID_SQL, getTableName()));
        clearParameters(getByUuidStmt);

        ResultSet rs = null;

        try
        {
            getByUuidStmt.setString(1, siteId);
            getByUuidStmt.setString(2, code);
            getByUuidStmt.setString(3, uuid);
            getByUuidStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUuidStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
                ret.setSiteId(rs.getString(2));
            }
        }
        catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            logger.severe(StringUtils.serialize(e));
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
     * Returns a content item from the table by id.
     */
    public synchronized T getById(String siteId, String code, int id) throws SQLException
    {
        T ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), String.format(GET_BY_ID_SQL, getTableName()));
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setString(1, siteId);
            getByIdStmt.setString(2, code);
            getByIdStmt.setInt(3, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
                ret.setSiteId(rs.getString(2));
            }
        }
        catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            logger.severe(StringUtils.serialize(e));
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
     * Returns the content items from the table by organisation code.
     */
    public synchronized List<T> list(Site site, String code) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), String.format(LIST_BY_CODE_SQL, getTableName()));
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, site.getId());
            listByCodeStmt.setString(2, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                T item = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
                item.setSiteId(rs.getString(2));
                ret.add(item);
            }
        }
        catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            logger.severe(StringUtils.serialize(e));
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
     * Returns the content items from the table by published date.
     */
    public synchronized List<T> list(Site site, Instant date) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByDateStmt == null)
            listByDateStmt = prepareStatement(getConnection(), String.format(LIST_BY_DATE_SQL, getTableName()));
        clearParameters(listByDateStmt);

        ResultSet rs = null;

        try
        {
            listByDateStmt.setString(1, site.getId());
            listByDateStmt.setTimestamp(2, new Timestamp(date.toEpochMilli()), UTC);
            listByDateStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByDateStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                T item = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
                item.setSiteId(rs.getString(2));
                ret.add(item);
            }
        }
        catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            logger.severe(StringUtils.serialize(e));
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
     * Returns the content items from the table by status.
     */
    public synchronized List<T> list(Site site, ContentStatus status) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByStatusStmt == null)
            listByStatusStmt = prepareStatement(getConnection(), String.format(LIST_BY_STATUS_SQL, getTableName()));
        clearParameters(listByStatusStmt);

        ResultSet rs = null;

        try
        {
            listByStatusStmt.setString(1, site.getId());
            listByStatusStmt.setString(2, status.name());
            listByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByStatusStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                T item = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
                item.setSiteId(rs.getString(2));
                ret.add(item);
            }
        }
        catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            logger.severe(StringUtils.serialize(e));
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
     * Returns the count of content items from the table.
     */
    public int count() throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), String.format(COUNT_SQL, getTableName()));
        clearParameters(countStmt);

        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Returns the count of content items from the table by organisation.
     */
    public int count(Site site, String code) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countByCodeStmt == null)
            countByCodeStmt = prepareStatement(getConnection(), String.format(COUNT_BY_CODE_SQL, getTableName()));
        clearParameters(countByCodeStmt);

        countByCodeStmt.setString(1, site.getId());
        countByCodeStmt.setString(2, code);
        countByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countByCodeStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Removes the given content item from the table.
     */
    public synchronized void delete(T content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), String.format(DELETE_SQL, getTableName()));
        clearParameters(deleteStmt);

        deleteStmt.setString(1, content.getSiteId());
        deleteStmt.setString(2, content.getCode());
        deleteStmt.setInt(3, content.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted content '%s' in %s (GUID=%s)", 
            content.getTitle(), getTableName(), content.getGuid()));
    }

    /**
     * Returns the maximum ID from the table.
     */
    protected synchronized int getMaxId(String siteId, String code) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(maxIdStmt == null)
            maxIdStmt = prepareStatement(getConnection(), String.format(GET_MAX_ID_SQL, getTableName()));
        clearParameters(maxIdStmt);

        maxIdStmt.setString(1, siteId);
        maxIdStmt.setString(2, code);
        maxIdStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = maxIdStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Adds or Updates the given content item in the table.
     */
    public boolean upsert(T content) throws SQLException
    {
        return upsert(content, false);
    }

    /**
     * Adds or Updates the given content in the table.
     */
    public boolean upsert(T content, boolean keepExternal) throws SQLException
    {
        boolean ret = false;

        T existing = getByUuid(content.getSiteId(), content.getCode(), content.getUuid());
        if(existing != null)
        {
            if(keepExternal)
                content.copyExternalAttributes(existing);
            update(content);
        }
        else if(content.getId() > 0) // Get by ID as the URL may have changed
        {
            existing = getById(content.getSiteId(), content.getCode(), content.getId());
            if(existing != null)
            {
                if(keepExternal)
                    content.copyExternalAttributes(existing);
                update(content);
            }
            else
            {
                add(content);
                ret = true;
            }
        }
        else
        {
            synchronized(table)
            {
                content.setId(getMaxId(content.getSiteId(), content.getCode())+1);
                add(content);
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Returns an instance of the template class.
     */
    private T newContentInstance(Class[] parameterTypes, Object[] parameters)
        throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        ParameterizedType superClass = (ParameterizedType)getClass().getGenericSuperclass();
        Class<T> contentClass = (Class<T>)superClass.getActualTypeArguments()[0];
        return (T)ClassUtils.newInstance(contentClass, parameterTypes, parameters);
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByUuidStmt);
        getByUuidStmt = null;
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByDateStmt);
        listByDateStmt = null;
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(countByCodeStmt);
        countByCodeStmt = null;
        closeStatement(maxIdStmt);
        maxIdStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByUuidStmt;
    private PreparedStatement getByIdStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByDateStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement countByCodeStmt;
    private PreparedStatement maxIdStmt;
    private PreparedStatement deleteStmt;
}
