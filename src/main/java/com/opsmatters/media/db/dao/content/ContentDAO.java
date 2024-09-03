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
import java.time.temporal.ChronoUnit;
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
import com.opsmatters.media.model.content.Content;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.ContentLookup;
import com.opsmatters.media.util.ClassUtils;
import com.opsmatters.media.util.StringUtils;

/**
 * DAO that provides operations on a content table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class ContentDAO<T extends Content> extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentDAO.class.getName());

    /**
     * The query to use to update the status of a content item in the table.
     */
    private static final String UPDATE_STATUS_SQL =  
      "UPDATE %s SET STATUS=? WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select a content item from the table by UUID.
     */
    private static final String GET_BY_UUID_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE UUID=?";

    /**
     * The query to use to select a content item from the table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select a content item from the table by title.
     */
    private static final String GET_BY_TITLE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND CODE=? AND TITLE=?";

    /**
     * The query to use to select the content from the table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * The query to use to select the pending content from the table by organisation code.
     */
    private static final String LIST_PENDING_BY_CODE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND CODE=? AND STATUS IN ('PENDING','STAGED') ORDER BY ID";

    /**
     * The query to use to select the content from the table by organisation code and interval.
     */
    private static final String LIST_BY_CODE_INTERVAL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND CODE=? AND PUBLISHED_DATE >= (NOW() + INTERVAL -? DAY) ORDER BY ID";

    /**
     * The query to use to select the content from the table by published date.
     */
    private static final String LIST_BY_DATE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND PUBLISHED=1 AND PUBLISHED_DATE>? AND STATUS != 'SKIPPED' ORDER BY ID";

    /**
     * The query to use to select the content from the table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND STATUS=? ORDER BY CODE";

    /**
     * The query to use to select the content from the table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? ORDER BY CODE";

    /**
     * The query to use to select the content from the table by site, code and title.
     */
    private static final String LIST_BY_TITLE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM %s WHERE SITE_ID=? AND CODE=? AND TITLE=? ORDER BY PUBLISHED_DATE DESC";

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
     * The query to use to delete a content item from the table by organisation.
     */
    private static final String DELETE_BY_CODE_SQL =  
      "DELETE FROM %s WHERE SITE_ID=? AND CODE=?";

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
     * Updates the status of the given content item in the table.
     */
    public synchronized void updateStatus(T content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("content uuid null");

        if(updateStatusStmt == null)
            updateStatusStmt = prepareStatement(getConnection(), String.format(UPDATE_STATUS_SQL, getTableName()));
        clearParameters(updateStatusStmt);

        updateStatusStmt.setString(1, content.getStatus().name());
        updateStatusStmt.setString(2, content.getSiteId());
        updateStatusStmt.setString(3, content.getCode());
        updateStatusStmt.setInt(4, content.getId());
        updateStatusStmt.executeUpdate();

        logger.info(String.format("Updated status of %s '%s' in %s (GUID=%s)", 
            content.getType().value(), content.getTitle(), getTableName(), content.getGuid()));
    }

    /**
     * Returns a content item from the table by UUID.
     */
    public synchronized T getByUuid(String uuid) throws SQLException
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
            getByUuidStmt.setString(1, uuid);
            getByUuidStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUuidStmt.executeQuery();
            while(rs.next())
            {
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret = content;
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
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret = content;
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
    public synchronized T getById(T content) throws SQLException
    {
        return getById(content.getSiteId(), content.getCode(), content.getId());
    }

    /**
     * Returns a content item from the table by soce and title.
     */
    public synchronized T getByTitle(String siteId, String code, String title) throws SQLException
    {
        T ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByTitleStmt == null)
            getByTitleStmt = prepareStatement(getConnection(), String.format(GET_BY_TITLE_SQL, getTableName()));
        clearParameters(getByTitleStmt);

        ResultSet rs = null;

        try
        {
            getByTitleStmt.setString(1, siteId);
            getByTitleStmt.setString(2, code);
            getByTitleStmt.setString(3, title);
            getByTitleStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByTitleStmt.executeQuery();
            while(rs.next())
            {
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret = content;
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
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
     * Returns the pending content items from the table by organisation code.
     */
    public synchronized List<T> listPending(Site site, String code) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listPendingByCodeStmt == null)
            listPendingByCodeStmt = prepareStatement(getConnection(), String.format(LIST_PENDING_BY_CODE_SQL, getTableName()));
        clearParameters(listPendingByCodeStmt);

        ResultSet rs = null;

        try
        {
            listPendingByCodeStmt.setString(1, site.getId());
            listPendingByCodeStmt.setString(2, code);
            listPendingByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listPendingByCodeStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
     * Returns the content items from the table by organisation code and interval.
     */
    public synchronized List<T> list(Site site, String code, int interval) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeIntervalStmt == null)
            listByCodeIntervalStmt = prepareStatement(getConnection(), String.format(LIST_BY_CODE_INTERVAL_SQL, getTableName()));
        clearParameters(listByCodeIntervalStmt);

        ResultSet rs = null;

        try
        {
            listByCodeIntervalStmt.setString(1, site.getId());
            listByCodeIntervalStmt.setString(2, code);
            listByCodeIntervalStmt.setInt(3, interval);
            listByCodeIntervalStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeIntervalStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
     * Returns the content items from the table by site.
     */
    public synchronized List<T> list(Site site) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listBySiteStmt == null)
            listBySiteStmt = prepareStatement(getConnection(), String.format(LIST_BY_SITE_SQL, getTableName()));
        clearParameters(listBySiteStmt);

        ResultSet rs = null;

        try
        {
            listBySiteStmt.setString(1, site.getId());
            listBySiteStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySiteStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
     * Returns a list of content items from the table by site, code and title.
     */
    public synchronized List<T> listByTitle(String siteId, String code, String title) throws SQLException
    {
        List<T> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTitleStmt == null)
            listByTitleStmt = prepareStatement(getConnection(), String.format(LIST_BY_TITLE_SQL, getTableName()));
        clearParameters(listByTitleStmt);

        ResultSet rs = null;

        try
        {
            listByTitleStmt.setString(1, siteId);
            listByTitleStmt.setString(2, code);
            listByTitleStmt.setString(3, title);
            listByTitleStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTitleStmt.executeQuery();
            ret = new ArrayList<T>();
            while(rs.next())
            {
                T content = newContentInstance(new Class[0], new Object[0]);
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
     * Returns the count of duplicates of the given content item from the table.
     */
    public int getDuplicateCount(T content) throws SQLException
    {
        int ret = 0;
        List<T> duplicates = listByTitle(content.getSiteId(),
            content.getCode(), content.getTitle());
        for(T duplicate : duplicates)
        {
            if(duplicate.getId() != content.getId())
            {
                if(content.getPublishedDate() != null)
                {
                    long diff = ChronoUnit.DAYS.between(duplicate.getPublishedDate(),
                        content.getPublishedDate());
                    if(diff >= 90) // not a duplicate if more than 90 days older
                        continue;
                }

                ++ret;
            }
        }

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
     * Removes the content items from the table by organisation.
     */
    public synchronized void delete(Site site, String code) throws SQLException
    {
        if(!hasConnection())
            return;

        if(deleteByCodeStmt == null)
            deleteByCodeStmt = prepareStatement(getConnection(), String.format(DELETE_BY_CODE_SQL, getTableName()));
        clearParameters(deleteByCodeStmt);

        deleteByCodeStmt.setString(1, site.getId());
        deleteByCodeStmt.setString(2, code);
        deleteByCodeStmt.executeUpdate();

        logger.info(String.format("Deleted content for site %s and code %s in %s", 
            site.getId(), code, getTableName()));
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

        T existing = getByUuid(content.getUuid());
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
     * Returns a class to look up an organisation's content by title or id.
     */
    public ContentLookup<T> newLookup()
    {
        return null;
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(updateStatusStmt);
        updateStatusStmt = null;
        closeStatement(getByUuidStmt);
        getByUuidStmt = null;
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByTitleStmt);
        getByTitleStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listPendingByCodeStmt);
        listPendingByCodeStmt = null;
        closeStatement(listByCodeIntervalStmt);
        listByCodeIntervalStmt = null;
        closeStatement(listByDateStmt);
        listByDateStmt = null;
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listByTitleStmt);
        listByTitleStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(countByCodeStmt);
        countByCodeStmt = null;
        closeStatement(maxIdStmt);
        maxIdStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
        closeStatement(deleteByCodeStmt);
        deleteByCodeStmt = null;
    }

    private PreparedStatement updateStatusStmt;
    private PreparedStatement getByUuidStmt;
    private PreparedStatement getByIdStmt;
    private PreparedStatement getByTitleStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listPendingByCodeStmt;
    private PreparedStatement listByCodeIntervalStmt;
    private PreparedStatement listByDateStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listByTitleStmt;
    private PreparedStatement countStmt;
    private PreparedStatement countByCodeStmt;
    private PreparedStatement maxIdStmt;
    private PreparedStatement deleteStmt;
    private PreparedStatement deleteByCodeStmt;
}
