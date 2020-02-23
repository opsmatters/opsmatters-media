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
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.DAOFactory;
import com.opsmatters.media.model.content.ContentItem;
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
      "SELECT ATTRIBUTES FROM %s WHERE CODE=? AND UUID=?";

    /**
     * The query to use to select a content item from the table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ATTRIBUTES FROM %s WHERE CODE=? AND ID=?";

    /**
     * The query to use to select the content from the table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ATTRIBUTES FROM %s WHERE CODE=? ORDER BY ID";

    /**
     * The query to use to select the content from the table by published date.
     */
    private static final String LIST_BY_DATE_SQL =  
      "SELECT ATTRIBUTES FROM %s WHERE PUBLISHED_DATE>? ORDER BY ID";

    /**
     * The query to use to get the count of content items from the table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM %s WHERE CODE=?";

    /**
     * The query to use to get the last ID from the table.
     */
    private static final String GET_MAX_ID_SQL =  
      "SELECT MAX(ID) FROM %s WHERE CODE=?";

    /**
     * The query to use to delete a content item from the table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM %s WHERE CODE=? AND ID=?";

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
    public T getByUuid(String code, String uuid) throws SQLException
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
            getByUuidStmt.setString(1, code);
            getByUuidStmt.setString(2, uuid);
            getByUuidStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUuidStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
            }
        }
        catch(IllegalAccessException | NoSuchMethodException e)
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
    public T getById(String code, int id) throws SQLException
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
            getByIdStmt.setString(1, code);
            getByIdStmt.setInt(2, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes });
            }
        }
        catch(IllegalAccessException | NoSuchMethodException e)
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
    public List<T> list(String code) throws SQLException
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
            listByCodeStmt.setString(1, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret.add(newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes }));
            }
        }
        catch(IllegalAccessException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        catch(NoSuchMethodException e)
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
    public List<T> list(Instant date) throws SQLException
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
            listByDateStmt.setTimestamp(1, new Timestamp(date.toEpochMilli()), UTC);
            listByDateStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByDateStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret.add(newContentInstance(new Class[] { JSONObject.class }, new Object[] { attributes }));
            }
        }
        catch(IllegalAccessException e)
        {
            logger.severe(StringUtils.serialize(e));
        }
        catch(NoSuchMethodException e)
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
    public int count(String code) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), String.format(COUNT_SQL, getTableName()));
        clearParameters(countStmt);

        countStmt.setString(1, code);
        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Removes the given content item from the table.
     */
    public void delete(T content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), String.format(DELETE_SQL, getTableName()));
        clearParameters(deleteStmt);

        deleteStmt.setString(1, content.getCode());
        deleteStmt.setInt(2, content.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted content '%s' in %s (GUID=%s)", 
            content.getTitle(), getTableName(), content.getGuid()));
    }

    /**
     * Returns the maximum ID from the table.
     */
    protected int getMaxId(String code) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(maxIdStmt == null)
            maxIdStmt = prepareStatement(getConnection(), String.format(GET_MAX_ID_SQL, getTableName()));
        clearParameters(maxIdStmt);

        maxIdStmt.setString(1, code);
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

        T existing = getByUuid(content.getCode(), content.getUuid());
        if(existing != null)
        {
            if(keepExternal)
                content.copyExternalAttributes(existing);
            update(content);
        }
        else if(content.getId() > 0) // Get by ID as the URL may have changed
        {
            existing = getById(content.getCode(), content.getId());
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
                content.setId(getMaxId(content.getCode())+1);
                add(content);
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if all the content items for the given organisation code have been deployed.
     */
    public boolean isDeployed(String code) throws SQLException
    {
        boolean ret = true;

        List<T> items = list(code);
        if(items.size() > 0)
        {
            for(T content : items)
            {
                if(!content.isDeployed())
                {
                    logger.info(String.format("Organisation %s %s content not deployed: %s (GUID=%s)", 
                        code, getTableName(), content.getTitle(), content.getGuid()));
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns an instance of the template class.
     */
    private T newContentInstance(Class[] parameterTypes, Object[] parameters)
        throws IllegalAccessException, NoSuchMethodException
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
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(maxIdStmt);
        maxIdStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByUuidStmt;
    private PreparedStatement getByIdStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByDateStmt;
    private PreparedStatement countStmt;
    private PreparedStatement maxIdStmt;
    private PreparedStatement deleteStmt;
}
