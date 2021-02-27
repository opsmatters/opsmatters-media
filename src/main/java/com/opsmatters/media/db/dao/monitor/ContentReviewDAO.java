/*
 * Copyright 2021 Gerald Curley
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

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.monitor.ContentReview;
import com.opsmatters.media.model.monitor.ReviewStatus;

/**
 * DAO that provides operations on the CONTENT_REVIEWS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentReviewDAO extends MonitorDAO<ContentReview>
{
    private static final Logger logger = Logger.getLogger(ContentReviewDAO.class.getName());

    /**
     * The query to use to select a review from the CONTENT_REVIEWS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, CREATED_BY "
      + "FROM CONTENT_REVIEWS WHERE ID=?";

    /**
     * The query to use to insert a review into the CONTENT_REVIEWS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_REVIEWS"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a review in the CONTENT_REVIEWS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_REVIEWS SET UPDATED_DATE=?, STATUS=?, NOTES=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the reviews from the CONTENT_REVIEWS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, CREATED_BY "
      + "FROM CONTENT_REVIEWS WHERE CREATED_DATE >= (NOW() + INTERVAL -30 DAY) ORDER BY CREATED_DATE";

    /**
     * The query to use to select the reviews from the CONTENT_REVIEWS table by status.
     */
    private static final String LIST_BY_STATUS_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, REASON, STATUS, MONITOR_ID, NOTES, CREATED_BY "
      + "FROM CONTENT_REVIEWS WHERE STATUS=? AND CREATED_DATE >= (NOW() + INTERVAL -30 DAY) ORDER BY CREATED_DATE";

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
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("REASON", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("MONITOR_ID", Types.VARCHAR, 36, true);
        table.addColumn("NOTES", Types.VARCHAR, 256, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("CONTENT_REVIEWS_PK", new String[] {"ID"});
        table.addIndex("CONTENT_REVIEWS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a review from the CONTENT_REVIEWS table by id.
     */
    public ContentReview getById(String id) throws SQLException
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
                review.setCode(rs.getString(4));
                review.setReason(rs.getString(5));
                review.setStatus(rs.getString(6));
                review.setMonitorId(rs.getString(7));
                review.setNotes(rs.getString(8));
                review.setCreatedBy(rs.getString(9));
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
    public void add(ContentReview review) throws SQLException
    {
        if(!hasConnection() || review == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, review.getId());
            insertStmt.setTimestamp(2, new Timestamp(review.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(review.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, review.getCode());
            insertStmt.setString(5, review.getReason().name());
            insertStmt.setString(6, review.getStatus().name());
            insertStmt.setString(7, review.getMonitorId());
            insertStmt.setString(8, review.getNotes());
            insertStmt.setString(9, review.getCreatedBy());
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
    }

    /**
     * Updates the given review in the CONTENT_REVIEWS table.
     */
    public void update(ContentReview review) throws SQLException
    {
        if(!hasConnection() || review == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(review.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, review.getStatus().name());
        updateStmt.setString(3, review.getNotes());
        updateStmt.setString(4, review.getCreatedBy());
        updateStmt.setString(5, review.getId());
        updateStmt.executeUpdate();

        logger.info("Updated review '"+review.getId()+"' in CONTENT_REVIEWS");
    }

    /**
     * Returns the reviews from the CONTENT_REVIEWS table.
     */
    public List<ContentReview> list() throws SQLException
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
                review.setCode(rs.getString(4));
                review.setReason(rs.getString(5));
                review.setStatus(rs.getString(6));
                review.setMonitorId(rs.getString(7));
                review.setNotes(rs.getString(8));
                review.setCreatedBy(rs.getString(9));
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
     * Returns the reviews from the CONTENT_REVIEWS table by status.
     */
    public List<ContentReview> list(ReviewStatus status) throws SQLException
    {
        List<ContentReview> ret = null;

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
            ret = new ArrayList<ContentReview>();
            while(rs.next())
            {
                ContentReview review = new ContentReview();
                review.setId(rs.getString(1));
                review.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                review.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                review.setCode(rs.getString(4));
                review.setReason(rs.getString(5));
                review.setStatus(rs.getString(6));
                review.setMonitorId(rs.getString(7));
                review.setNotes(rs.getString(8));
                review.setCreatedBy(rs.getString(9));
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
    public void delete(ContentReview review) throws SQLException
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
        closeStatement(listByStatusStmt);
        listByStatusStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByStatusStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
