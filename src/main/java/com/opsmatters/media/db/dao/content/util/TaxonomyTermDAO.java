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
package com.opsmatters.media.db.dao.content.util;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.util.TaxonomyTerm;
import com.opsmatters.media.model.content.util.TaxonomyType;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the TAXONOMY_TERMS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class TaxonomyTermDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(TaxonomyTermDAO.class.getName());

    /**
     * The query to use to select a term from the TAXONOMY_TERMS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, TYPE, NAME, STATUS "
      + "FROM TAXONOMY_TERMS WHERE ID=?";

    /**
     * The query to use to insert a term into the TAXONOMY_TERMS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO TAXONOMY_TERMS"
      + "( ID, CREATED_DATE, UPDATED_DATE, SITE_ID, TYPE, NAME, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a term in the TAXONOMY_TERMS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE TAXONOMY_TERMS SET UPDATED_DATE=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the terms from the TAXONOMY_TERMS table by site.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, TYPE, NAME, STATUS "
      + "FROM TAXONOMY_TERMS WHERE SITE_ID=?";

    /**
     * The query to use to select the terms from the TAXONOMY_TERMS table by site and type.
     */
    private static final String LIST_BY_TYPE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, SITE_ID, TYPE, NAME, STATUS "
      + "FROM TAXONOMY_TERMS WHERE SITE_ID=? AND TYPE=?";

    /**
     * The query to use to get the count of terms from the TAXONOMY_TERMS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM TAXONOMY_TERMS";

    /**
     * The query to use to delete terms from the TAXONOMY_TERMS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM TAXONOMY_TERMS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public TaxonomyTermDAO(ContentUtilDAOFactory factory)
    {
        super(factory, "TAXONOMY_TERMS");
    }

    /**
     * Defines the columns and indices for the TAXONOMY_TERMS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("TYPE", Types.VARCHAR, 20, true);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("TAXONOMY_TERMS_PK", new String[] {"ID"});
        table.addIndex("TAXONOMY_TERMS_NAME_IDX", new String[] {"SITE_ID", "NAME"});
        table.setInitialised(true);
    }

    /**
     * Returns a term from the TAXONOMY_TERMS table by id.
     */
    public synchronized TaxonomyTerm getById(String id) throws SQLException
    {
        TaxonomyTerm ret = null;

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
                TaxonomyTerm term = new TaxonomyTerm();
                term.setId(rs.getString(1));
                term.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                term.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                term.setSiteId(rs.getString(4));
                term.setType(rs.getString(5));
                term.setName(rs.getString(6));
                term.setStatus(rs.getString(7));
                ret = term;
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
     * Stores the given term in the TAXONOMY_TERMS table.
     */
    public synchronized void add(TaxonomyTerm term) throws SQLException
    {
        if(!hasConnection() || term == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, term.getId());
            insertStmt.setTimestamp(2, new Timestamp(term.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(term.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, term.getSiteId());
            insertStmt.setString(5, term.getType().name());
            insertStmt.setString(6, term.getName());
            insertStmt.setString(7, term.getStatus().name());
            insertStmt.executeUpdate();

            logger.info("Created term '"+term.getId()+"' in TAXONOMY_TERMS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the term already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given term in the TAXONOMY_TERMS table.
     */
    public synchronized void update(TaxonomyTerm term) throws SQLException
    {
        if(!hasConnection() || term == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(term.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, term.getStatus().name());
        updateStmt.setString(3, term.getId());
        updateStmt.executeUpdate();

        logger.info("Updated term '"+term.getId()+"' in TAXONOMY_TERMS");
    }

    /**
     * Adds or Updates the given feed in the TAXONOMY_TERMS table.
     */
    public boolean upsert(TaxonomyTerm term) throws SQLException
    {
        boolean ret = false;

        TaxonomyTerm existing = getById(term.getId());
        if(existing != null)
        {
            update(term);
        }
        else
        {
            add(term);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the terms from the TAXONOMY_TERMS table by site.
     */
    public synchronized List<TaxonomyTerm> list(Site site) throws SQLException
    {
        List<TaxonomyTerm> ret = null;

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
            ret = new ArrayList<TaxonomyTerm>();
            while(rs.next())
            {
                TaxonomyTerm term = new TaxonomyTerm();
                term.setId(rs.getString(1));
                term.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                term.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                term.setSiteId(rs.getString(4));
                term.setType(rs.getString(5));
                term.setName(rs.getString(6));
                term.setStatus(rs.getString(7));
                ret.add(term);
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
     * Returns the terms from the TAXONOMY_TERMS table by site and type.
     */
    public synchronized List<TaxonomyTerm> list(Site site, TaxonomyType type) throws SQLException
    {
        List<TaxonomyTerm> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByTypeStmt == null)
            listByTypeStmt = prepareStatement(getConnection(), LIST_BY_TYPE_SQL);
        clearParameters(listByTypeStmt);

        ResultSet rs = null;

        try
        {
            listByTypeStmt.setString(1, site.getId());
            listByTypeStmt.setString(2, type.name());
            listByTypeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByTypeStmt.executeQuery();
            ret = new ArrayList<TaxonomyTerm>();
            while(rs.next())
            {
                TaxonomyTerm term = new TaxonomyTerm();
                term.setId(rs.getString(1));
                term.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                term.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                term.setSiteId(rs.getString(4));
                term.setType(rs.getString(5));
                term.setName(rs.getString(6));
                term.setStatus(rs.getString(7));
                ret.add(term);
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
     * Returns the count of terms from the table.
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
     * Removes the given term from the TAXONOMY_TERMS table.
     */
    public synchronized void delete(TaxonomyTerm term) throws SQLException
    {
        if(!hasConnection() || term == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, term.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted term '"+term.getId()+"' from TAXONOMY_TERMS");
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
    private PreparedStatement listByTypeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}