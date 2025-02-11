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
package com.opsmatters.media.db.dao.monitor;

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
import com.opsmatters.media.model.monitor.ContentReview;
import com.opsmatters.media.model.monitor.ContentReviewItem;
import com.opsmatters.media.model.monitor.ReviewStatus;
import com.opsmatters.media.model.monitor.ContentMonitor;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the CONTENT_REVIEWS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentReviewDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentReviewDAO.class.getName());

    /**
     * The query to use to select a review from the CONTENT_REVIEWS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY, SESSION_ID "
      + "FROM CONTENT_REVIEWS WHERE ID=?";

    /**
     * The query to use to insert a review into the CONTENT_REVIEWS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_REVIEWS"
      + "( ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a review in the CONTENT_REVIEWS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_REVIEWS SET UPDATED_DATE=?, REVIEW_DATE=?, REASON=?, ATTRIBUTES=?, STATUS=?, CREATED_BY=?, SESSION_ID=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the reviews from the CONTENT_REVIEWS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY, SESSION_ID "
      + "FROM CONTENT_REVIEWS "
      + "WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the review items from the CONTENT_REVIEWS table.
     */
    private static final String LIST_ITEMS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, STATUS, MONITOR_ID "
      + "FROM CONTENT_REVIEWS "
      + "WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW' ORDER BY CREATED_DATE";

    /**
     * The query to use to select the reviews from the CONTENT_REVIEWS table by organisation.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY, SESSION_ID "
      + "FROM CONTENT_REVIEWS "
      + "WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the reviews from the CONTENT_REVIEWS table by monitor.
     */
    private static final String LIST_BY_MONITOR_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, ATTRIBUTES, STATUS, MONITOR_ID, CREATED_BY, SESSION_ID "
      + "FROM CONTENT_REVIEWS "
      + "WHERE MONITOR_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the review items from the CONTENT_REVIEWS table by status.
     */
    private static final String LIST_ITEMS_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, REVIEW_DATE, CODE, REASON, STATUS, MONITOR_ID "
      + "FROM CONTENT_REVIEWS "
      + "WHERE STATUS=? AND (CREATED_DATE >= (NOW() + INTERVAL -30 DAY) OR STATUS='NEW') ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of reviews from the CONTENT_REVIEWS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_REVIEWS";

    /**
     * The query to use to delete a review from the CONTENT_REVIEWS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_REVIEWS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentReviewDAO(MonitorDAOFactory factory)
    {
        super(factory, "CONTENT_REVIEWS");
    }

    /**
     * Defines the columns and indices for the CONTENT_REVIEWS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("REVIEW_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("REASON", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("MONITOR_ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("CONTENT_REVIEWS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_REVIEWS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("CONTENT_REVIEWS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an review from the CONTENT_REVIEWS table by id.
     */
    public synchronized ContentReview getById(String id) throws SQLException
    {
        ContentReview ret = null;

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
                ContentReview review = new ContentReview();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setReviewDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                review.setCode(rs.getString(5));
                review.setReason(rs.getString(6));
                review.setAttributes(new JSONObject(getClob(rs, 7)));
                review.setStatus(rs.getString(8));
                review.setMonitorId(rs.getString(9));
                review.setCreatedBy(rs.getString(10));
                review.setSessionId(rs.getInt(11));
                ret = review;
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
     * Stores the given review in the CONTENT_REVIEWS table.
     */
    public synchronized void add(ContentReview review) throws SQLException
    {
        if(!hasConnection() || review == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, review.getId());
            insertStmt.setTimestamp(2, new Timestamp(review.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(review.getUpdatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(review.getReviewDateMillis()), UTC);
            insertStmt.setString(5, review.getCode());
            insertStmt.setString(6, review.getReason().name());
            String attributes = review.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(7, reader, attributes.length());
            insertStmt.setString(8, review.getStatus().name());
            insertStmt.setString(9, review.getMonitorId());
            insertStmt.setString(10, review.getCreatedBy());
            insertStmt.setInt(11, review.getSessionId());
            insertStmt.executeUpdate();

            logger.info("Created review '"+review.getId()+"' in CONTENT_REVIEWS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the review already exists
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
     * Updates the given review in the CONTENT_REVIEWS table.
     */
    public synchronized void update(ContentReview review) throws SQLException
    {
        if(!hasConnection() || review == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(review.getUpdatedDateMillis()), UTC);
            updateStmt.setTimestamp(2, new Timestamp(review.getReviewDateMillis()), UTC);
            updateStmt.setString(3, review.getReason().name());
            String attributes = review.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(4, reader, attributes.length());
            updateStmt.setString(5, review.getStatus().name());
            updateStmt.setString(6, review.getCreatedBy());
            updateStmt.setInt(7, review.getSessionId());
            updateStmt.setString(8, review.getId());
            updateStmt.executeUpdate();

            logger.info("Updated review '"+review.getId()+"' in CONTENT_REVIEWS");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the reviews from the CONTENT_REVIEWS table.
     */
    public synchronized List<ContentReview> list() throws SQLException
    {
        List<ContentReview> ret = null;

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
            ret = new ArrayList<ContentReview>();
            while(rs.next())
            {
                ContentReview review = new ContentReview();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setReviewDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                review.setCode(rs.getString(5));
                review.setReason(rs.getString(6));
                review.setAttributes(new JSONObject(getClob(rs, 7)));
                review.setStatus(rs.getString(8));
                review.setMonitorId(rs.getString(9));
                review.setCreatedBy(rs.getString(10));
                review.setSessionId(rs.getInt(11));
                ret.add(review);
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
     * Returns the review items from the CONTENT_REVIEWS table.
     */
    public synchronized List<ContentReviewItem> listItems() throws SQLException
    {
        List<ContentReviewItem> ret = null;

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
            ret = new ArrayList<ContentReviewItem>();
            while(rs.next())
            {
                ContentReviewItem review = new ContentReviewItem();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setReviewDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                review.setCode(rs.getString(5));
                review.setReason(rs.getString(6));
                review.setStatus(rs.getString(7));
                review.setMonitorId(rs.getString(8));
                ret.add(review);
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
     * Returns the reviews from the CONTENT_REVIEWS table by organisation.
     */
    public synchronized List<ContentReview> list(String code) throws SQLException
    {
        List<ContentReview> ret = null;

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
            ret = new ArrayList<ContentReview>();
            while(rs.next())
            {
                ContentReview review = new ContentReview();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setReviewDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                review.setCode(rs.getString(5));
                review.setReason(rs.getString(6));
                review.setAttributes(new JSONObject(getClob(rs, 7)));
                review.setStatus(rs.getString(8));
                review.setMonitorId(rs.getString(9));
                review.setCreatedBy(rs.getString(10));
                review.setSessionId(rs.getInt(11));
                ret.add(review);
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
     * Returns the reviews from the CONTENT_REVIEWS table by monitor.
     */
    public synchronized List<ContentReview> list(ContentMonitor monitor) throws SQLException
    {
        List<ContentReview> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByMonitorStmt == null)
            listByMonitorStmt = prepareStatement(getConnection(), LIST_BY_MONITOR_SQL);
        clearParameters(listByMonitorStmt);

        ResultSet rs = null;

        try
        {
            listByMonitorStmt.setString(1, monitor.getId());
            listByMonitorStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByMonitorStmt.executeQuery();
            ret = new ArrayList<ContentReview>();
            while(rs.next())
            {
                ContentReview review = new ContentReview();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setReviewDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                review.setCode(rs.getString(5));
                review.setReason(rs.getString(6));
                review.setAttributes(new JSONObject(getClob(rs, 7)));
                review.setStatus(rs.getString(8));
                review.setMonitorId(rs.getString(9));
                review.setCreatedBy(rs.getString(10));
                review.setSessionId(rs.getInt(11));
                ret.add(review);
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
     * Returns the review items from the CONTENT_REVIEWS table by status.
     */
    public synchronized List<ContentReviewItem> listItems(ReviewStatus status) throws SQLException
    {
        List<ContentReviewItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsByStatusStmt == null)
            listItemsByStatusStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_STATUS_SQL);
        clearParameters(listItemsByStatusStmt);

        ResultSet rs = null;

        try
        {
            listItemsByStatusStmt.setString(1, status.name());
            listItemsByStatusStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsByStatusStmt.executeQuery();
            ret = new ArrayList<ContentReviewItem>();
            while(rs.next())
            {
                ContentReviewItem review = new ContentReviewItem();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setReviewDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                review.setCode(rs.getString(5));
                review.setReason(rs.getString(6));
                review.setStatus(rs.getString(7));
                review.setMonitorId(rs.getString(8));
                ret.add(review);
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
     * Returns the count of reviews from the table.
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
     * Removes the given review from the CONTENT_REVIEWS table.
     */
    public synchronized void delete(ContentReview review) throws SQLException
    {
        if(!hasConnection() || review == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, review.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted review '"+review.getId()+"' in CONTENT_REVIEWS");
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
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(listByMonitorStmt);
        listByMonitorStmt = null;
        closeStatement(listItemsByStatusStmt);
        listItemsByStatusStmt = null;
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
    private PreparedStatement listByCodeStmt;
    private PreparedStatement listByMonitorStmt;
    private PreparedStatement listItemsByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
