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
package com.opsmatters.media.db.dao.content.crawler;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.content.crawler.ErrorPage;
import com.opsmatters.media.db.dao.BaseDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;

/**
 * DAO that provides operations on the ERROR_PAGES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ErrorPageDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ErrorPageDAO.class.getName());

    /**
     * The query to use to select an error page from the ERROR_PAGES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, TITLE, NOTES, TYPE, STATUS, CREATED_BY "
      + "FROM ERROR_PAGES WHERE ID=?";

    /**
     * The query to use to insert an error page into the ERROR_PAGES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ERROR_PAGES"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, TITLE, NOTES, TYPE, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an error page in the ERROR_PAGES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ERROR_PAGES SET UPDATED_DATE=?, NAME=?, TITLE=?, NOTES=?, TYPE=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the error pages from the ERROR_PAGES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, TITLE, NOTES, TYPE, STATUS, CREATED_BY "
      + "FROM ERROR_PAGES ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of error pages from the ERROR_PAGES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ERROR_PAGES";

    /**
     * The query to use to delete an error page from the ERROR_PAGES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ERROR_PAGES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ErrorPageDAO(ContentDAOFactory factory)
    {
        super(factory, "ERROR_PAGES");
    }

    /**
     * Defines the columns and indices for the ERROR_PAGES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 25, true);
        table.addColumn("TITLE", Types.VARCHAR, 128, true);
        table.addColumn("NOTES", Types.LONGVARCHAR, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("ERROR_PAGES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an error page from the ERROR_PAGES table by id.
     */
    public synchronized ErrorPage getById(String id) throws SQLException
    {
        ErrorPage ret = null;

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
                ErrorPage page = new ErrorPage();
                page.setId(rs.getString(1));
                page.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                page.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                page.setName(rs.getString(4));
                page.setTitle(rs.getString(5));
                page.setNotes(rs.getString(6));
                page.setType(rs.getString(7));
                page.setStatus(rs.getString(8));
                page.setCreatedBy(rs.getString(9));
                ret = page;
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
     * Stores the given error page in the ERROR_PAGES table.
     */
    public synchronized void add(ErrorPage page) throws SQLException
    {
        if(!hasConnection() || page == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, page.getId());
            insertStmt.setTimestamp(2, new Timestamp(page.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(page.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, page.getName());
            insertStmt.setString(5, page.getTitle());
            insertStmt.setString(6, page.getNotes());
            insertStmt.setString(7, page.getType().name());
            insertStmt.setString(8, page.getStatus().name());
            insertStmt.setString(9, page.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created error page '"+page.getId()+"' in ERROR_PAGES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the page already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given error page in the ERROR_PAGES table.
     */
    public synchronized void update(ErrorPage page) throws SQLException
    {
        if(!hasConnection() || page == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(page.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, page.getName());
        updateStmt.setString(3, page.getTitle());
        updateStmt.setString(4, page.getNotes());
        updateStmt.setString(5, page.getType().name());
        updateStmt.setString(6, page.getStatus().name());
        updateStmt.setString(7, page.getId());
        updateStmt.executeUpdate();

        logger.info("Updated error page '"+page.getId()+"' in ERROR_PAGES");
    }

    /**
     * Returns the error pages from the ERROR_PAGES table.
     */
    public synchronized List<ErrorPage> list() throws SQLException
    {
        List<ErrorPage> ret = null;

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
            ret = new ArrayList<ErrorPage>();
            while(rs.next())
            {
                ErrorPage page = new ErrorPage();
                page.setId(rs.getString(1));
                page.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                page.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                page.setName(rs.getString(4));
                page.setTitle(rs.getString(5));
                page.setNotes(rs.getString(6));
                page.setType(rs.getString(7));
                page.setStatus(rs.getString(8));
                page.setCreatedBy(rs.getString(9));
                ret.add(page);
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
     * Returns the count of error pages from the ERROR_PAGES table.
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
     * Removes the given error page from the ERROR_PAGES table.
     */
    public synchronized void delete(ErrorPage page) throws SQLException
    {
        if(!hasConnection() || page == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, page.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted error page '"+page.getId()+"' in ERROR_PAGES");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByCodeStmt);
        getByCodeStmt = null;
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
    private PreparedStatement getByCodeStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
