/*
 * Copyright 2023 Gerald Curley
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

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.social.Hashtag;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the HASHTAGS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class HashtagDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(HashtagDAO.class.getName());

    /**
     * The query to use to select a hashtag from the HASHTAGS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, SITES, STATUS "
      + "FROM HASHTAGS WHERE ID=?";

    /**
     * The query to use to select a hashtag from the HASHTAGS table by name.
     */
    private static final String GET_BY_NAME_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, SITES, STATUS "
      + "FROM HASHTAGS WHERE NAME=?";

    /**
     * The query to use to insert a hashtag into the HASHTAGS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO HASHTAGS"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, SITES, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a hashtag in the HASHTAGS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE HASHTAGS SET UPDATED_DATE=?, NAME=?, SITES=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the hashtags from the HASHTAGS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, SITES, STATUS "
      + "FROM HASHTAGS ORDER BY NAME";

    /**
     * The query to use to get the count of hashtags from the HASHTAGS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM HASHTAGS";

    /**
     * The query to use to delete a hashtag from the HASHTAGS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM HASHTAGS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public HashtagDAO(SocialDAOFactory factory)
    {
        super(factory, "HASHTAGS");
    }

    /**
     * Defines the columns and indices for the HASHTAGS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 32, true);
        table.addColumn("SITES", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("HASHTAGS_PK", new String[] {"ID"});
        table.addIndex("HASHTAGS_NAME_IDX", new String[] {"NAME"});
        table.setInitialised(true);
    }

    /**
     * Returns a hashtag from the HASHTAGS table by id.
     */
    public synchronized Hashtag getById(String id) throws SQLException
    {
        Hashtag ret = null;

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
                Hashtag hashtag = new Hashtag();
                hashtag.setId(rs.getString(1));
                hashtag.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                hashtag.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                hashtag.setName(rs.getString(4));
                hashtag.setSites(rs.getString(5));
                hashtag.setStatus(rs.getString(6));
                ret = hashtag;
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
     * Returns a hashtag from the HASHTAGS table by name.
     */
    public synchronized Hashtag getByName(String name) throws SQLException
    {
        Hashtag ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByNameStmt == null)
            getByNameStmt = prepareStatement(getConnection(), GET_BY_NAME_SQL);
        clearParameters(getByNameStmt);

        ResultSet rs = null;

        try
        {
            getByNameStmt.setString(1, name);
            getByNameStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByNameStmt.executeQuery();
            while(rs.next())
            {
                Hashtag hashtag = new Hashtag();
                hashtag.setId(rs.getString(1));
                hashtag.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                hashtag.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                hashtag.setName(rs.getString(4));
                hashtag.setSites(rs.getString(5));
                hashtag.setStatus(rs.getString(6));
                ret = hashtag;
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
     * Stores the given hashtag in the HASHTAGS table.
     */
    public synchronized void add(Hashtag hashtag) throws SQLException
    {
        if(!hasConnection() || hashtag == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, hashtag.getId());
            insertStmt.setTimestamp(2, new Timestamp(hashtag.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(hashtag.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, hashtag.getName());
            insertStmt.setString(5, hashtag.getSites());
            insertStmt.setString(6, hashtag.getStatus().name());
            insertStmt.executeUpdate();

            logger.info("Created hashtag '"+hashtag.getId()+"' in HASHTAGS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the hashtag already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given hashtag in the HASHTAGS table.
     */
    public synchronized void update(Hashtag hashtag) throws SQLException
    {
        if(!hasConnection() || hashtag == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(hashtag.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, hashtag.getName());
        updateStmt.setString(3, hashtag.getSites());
        updateStmt.setString(4, hashtag.getStatus().name());
        updateStmt.setString(5, hashtag.getId());
        updateStmt.executeUpdate();

        logger.info("Updated hashtag '"+hashtag.getId()+"' in HASHTAGS");
    }

    /**
     * Adds or Updates the given feed in the HASHTAGS table.
     */
    public boolean upsert(Hashtag hashtag) throws SQLException
    {
        boolean ret = false;

        Hashtag existing = getById(hashtag.getId());
        if(existing != null)
        {
            update(hashtag);
        }
        else
        {
            add(hashtag);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the hashtags from the HASHTAGS table.
     */
    public synchronized List<Hashtag> list() throws SQLException
    {
        List<Hashtag> ret = null;

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
            ret = new ArrayList<Hashtag>();
            while(rs.next())
            {
                Hashtag hashtag = new Hashtag();
                hashtag.setId(rs.getString(1));
                hashtag.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                hashtag.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                hashtag.setName(rs.getString(4));
                hashtag.setSites(rs.getString(5));
                hashtag.setStatus(rs.getString(6));
                ret.add(hashtag);
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
     * Returns the count of hashtags from the table.
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
     * Removes the given hashtag from the HASHTAGS table.
     */
    public synchronized void delete(Hashtag hashtag) throws SQLException
    {
        if(!hasConnection() || hashtag == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, hashtag.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted hashtag '"+hashtag.getId()+"' in HASHTAGS");
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByNameStmt);
        getByNameStmt = null;
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
    private PreparedStatement getByNameStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
