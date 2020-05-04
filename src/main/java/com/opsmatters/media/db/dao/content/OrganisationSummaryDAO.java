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
import com.opsmatters.media.model.content.OrganisationSummary;

/**
 * DAO that provides operations on the ORGANISATION_SUMMARY table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationSummaryDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OrganisationSummaryDAO.class.getName());

    /**
     * The query to use to select a summary from the ORGANISATION_SUMMARY table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, STATUS, CREATED_BY "
      + "FROM ORGANISATION_SUMMARY WHERE ID=?";

    /**
     * The query to use to insert a summary into the ORGANISATION_SUMMARY table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORGANISATION_SUMMARY"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a summary in the ORGANISATION_SUMMARY table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATION_SUMMARY SET UPDATED_DATE=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the summaries from the ORGANISATION_SUMMARY table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, STATUS, CREATED_BY "
      + "FROM ORGANISATION_SUMMARY ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of summaries from the ORGANISATION_SUMMARY table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORGANISATION_SUMMARY";

    /**
     * The query to use to delete a summary from the ORGANISATION_SUMMARY table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATION_SUMMARY WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrganisationSummaryDAO(ContentDAOFactory factory)
    {
        super(factory, "ORGANISATION_SUMMARY");
    }

    /**
     * Defines the columns and indices for the ORGANISATION_SUMMARY table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("ORGANISATION_SUMMARY_PK", new String[] {"ID"});
        table.addIndex("ORGANISATION_SUMMARY_CODE", new String[] {"CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns a summary from the ORGANISATION_SUMMARY table by id.
     */
    public OrganisationSummary getById(String id) throws SQLException
    {
        OrganisationSummary ret = null;

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
                OrganisationSummary summary = new OrganisationSummary();
                summary.setId(rs.getString(1));
                summary.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                summary.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                summary.setCode(rs.getString(4));
                summary.setStatus(rs.getString(5));
                summary.setCreatedBy(rs.getString(6));
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
     * Stores the given summary in the ORGANISATION_SUMMARY table.
     */
    public void add(OrganisationSummary summary) throws SQLException
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
            insertStmt.setString(5, summary.getStatus().name());
            insertStmt.setString(6, summary.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created summary '"+summary.getId()+"' in ORGANISATION_SUMMARY");
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
     * Updates the given summary in the ORGANISATION_SUMMARY table.
     */
    public void update(OrganisationSummary summary) throws SQLException
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
            updateStmt.setString(2, summary.getStatus().name());
            updateStmt.setString(3, summary.getId());
            updateStmt.executeUpdate();

            logger.info("Updated summary '"+summary.getId()+"' in ORGANISATION_SUMMARY");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the summaries from the ORGANISATION_SUMMARY table.
     */
    public List<OrganisationSummary> list() throws SQLException
    {
        List<OrganisationSummary> ret = null;

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
            ret = new ArrayList<OrganisationSummary>();
            while(rs.next())
            {
                OrganisationSummary summary = new OrganisationSummary();
                summary.setId(rs.getString(1));
                summary.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                summary.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                summary.setCode(rs.getString(4));
                summary.setStatus(rs.getString(5));
                summary.setCreatedBy(rs.getString(6));
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
     * Removes the given summary from the ORGANISATION_SUMMARY table.
     */
    public void delete(OrganisationSummary summary) throws SQLException
    {
        if(!hasConnection() || summary == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, summary.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted summary '"+summary.getId()+"' in ORGANISATION_SUMMARY");
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
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
