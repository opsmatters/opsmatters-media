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
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.model.content.ContentTypeSummary;

/**
 * DAO that provides operations on the CONTENT_TYPE_SUMMARY table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ContentTypeSummaryDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ContentTypeSummaryDAO.class.getName());

    /**
     * The query to use to select a summary from the CONTENT_TYPE_SUMMARY table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_TYPE_SUMMARY WHERE ID=?";

    /**
     * The query to use to insert a summary into the CONTENT_TYPE_SUMMARY table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO CONTENT_TYPE_SUMMARY"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ITEM_COUNT, DEPLOYED )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a summary in the CONTENT_TYPE_SUMMARY table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE CONTENT_TYPE_SUMMARY SET UPDATED_DATE=?, ITEM_COUNT=?, DEPLOYED=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the summaries from the CONTENT_TYPE_SUMMARY table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_TYPE_SUMMARY ORDER BY CREATED_DATE";

    /**
     * The query to use to select the summaries from the CONTENT_TYPE_SUMMARY table by organisation code.
     */
    private static final String LIST_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, CONTENT_TYPE, ITEM_COUNT, DEPLOYED "
      + "FROM CONTENT_TYPE_SUMMARY WHERE CODE=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of summaries from the CONTENT_TYPE_SUMMARY table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM CONTENT_TYPE_SUMMARY";

    /**
     * The query to use to delete a summary from the CONTENT_TYPE_SUMMARY table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM CONTENT_TYPE_SUMMARY WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ContentTypeSummaryDAO(ContentDAOFactory factory)
    {
        super(factory, "CONTENT_TYPE_SUMMARY");
    }

    /**
     * Defines the columns and indices for the CONTENT_TYPE_SUMMARY table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, false);
        table.addColumn("ITEM_COUNT", Types.INTEGER, true);
        table.addColumn("DEPLOYED", Types.BOOLEAN, true);
        table.setPrimaryKey("CONTENT_TYPE_SUMMARY_PK", new String[] {"ID"});
        table.addIndex("CONTENT_TYPE_SUMMARY_CODE", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns a summary from the CONTENT_TYPE_SUMMARY table by id.
     */
    public ContentTypeSummary getById(String id) throws SQLException
    {
        ContentTypeSummary ret = null;

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
                ContentTypeSummary summary = new ContentTypeSummary();
                summary.setId(rs.getString(1));
                summary.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                summary.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                summary.setCode(rs.getString(4));
                summary.setType(rs.getString(5));
                summary.setItemCount(rs.getInt(6));
                summary.setDeployed(rs.getBoolean(7));
                ret = summary;
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
     * Stores the given summary in the CONTENT_TYPE_SUMMARY table.
     */
    public void add(ContentTypeSummary summary) throws SQLException
    {
        if(!hasConnection() || summary == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, summary.getId());
            insertStmt.setTimestamp(2, new Timestamp(summary.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(summary.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, summary.getCode());
            insertStmt.setString(5, summary.getType().name());
            insertStmt.setLong(6, summary.getItemCount());
            insertStmt.setBoolean(7, summary.isDeployed());
            insertStmt.executeUpdate();

            logger.info("Created summary '"+summary.getId()+"' in CONTENT_TYPE_SUMMARY");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the summary already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given summary in the CONTENT_TYPE_SUMMARY table.
     */
    public void update(ContentTypeSummary summary) throws SQLException
    {
        if(!hasConnection() || summary == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(summary.getUpdatedDateMillis()), UTC);
            updateStmt.setLong(2, summary.getItemCount());
            updateStmt.setBoolean(3, summary.isDeployed());
            updateStmt.setString(4, summary.getId());
            updateStmt.executeUpdate();

            logger.info("Updated summary '"+summary.getId()+"' in CONTENT_TYPE_SUMMARY");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the summaries from the CONTENT_TYPE_SUMMARY table.
     */
    public List<ContentTypeSummary> list() throws SQLException
    {
        List<ContentTypeSummary> ret = null;

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
            ret = new ArrayList<ContentTypeSummary>();
            while(rs.next())
            {
                ContentTypeSummary summary = new ContentTypeSummary();
                summary.setId(rs.getString(1));
                summary.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                summary.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                summary.setCode(rs.getString(4));
                summary.setType(rs.getString(5));
                summary.setItemCount(rs.getInt(6));
                summary.setDeployed(rs.getBoolean(7));
                ret.add(summary);
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
     * Returns the summaries from the CONTENT_TYPE_SUMMARY table by organisation code.
     */
    public List<ContentTypeSummary> list(String code) throws SQLException
    {
        List<ContentTypeSummary> ret = null;

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
            ret = new ArrayList<ContentTypeSummary>();
            while(rs.next())
            {
                ContentTypeSummary summary = new ContentTypeSummary();
                summary.setId(rs.getString(1));
                summary.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                summary.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                summary.setCode(rs.getString(4));
                summary.setType(rs.getString(5));
                summary.setItemCount(rs.getInt(6));
                summary.setDeployed(rs.getBoolean(7));
                ret.add(summary);
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
     * Returns the count of summaries from the table.
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
     * Removes the given summary from the CONTENT_TYPE_SUMMARY table.
     */
    public void delete(ContentTypeSummary summary) throws SQLException
    {
        if(!hasConnection() || summary == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, summary.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted summary '"+summary.getId()+"' in CONTENT_TYPE_SUMMARY");
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
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listByCodeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
