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
package com.opsmatters.media.db.dao.social;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.social.SocialUpdate;
//GERALD
//import com.opsmatters.media.model.social.SocialChannels;

/**
 * DAO that provides operations on the SOCIAL_UPDATES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialUpdateDAO extends SocialDAO<SocialUpdate>
{
    private static final Logger logger = Logger.getLogger(SocialUpdateDAO.class.getName());

    /**
     * The query to use to select an item from the SOCIAL_UPDATES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORGANISATION, CONTENT_TYPE, STATUS, CREATED_BY "
      + "FROM SOCIAL_UPDATES WHERE ID=?";

    /**
     * The query to use to insert a social update into the SOCIAL_UPDATES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SOCIAL_UPDATES"
      + "( ID, CREATED_DATE, UPDATED_DATE, ORGANISATION, CONTENT_TYPE, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a social update in the SOCIAL_UPDATES table.
     */
/* GERALD
    private static final String UPDATE_SQL =  
      "UPDATE SOCIAL_UPDATES SET UPDATED_DATE=?, ORGANISATION=?, STATUS=? "
      + "WHERE ID=?";
*/
    /**
     * The query to use to select the social updates from the SOCIAL_UPDATES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORGANISATION, CONTENT_TYPE, STATUS, CREATED_BY "
      + "FROM SOCIAL_UPDATES ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of social updates from the SOCIAL_UPDATES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SOCIAL_UPDATES";

    /**
     * The query to use to delete a social update from the SOCIAL_UPDATES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SOCIAL_UPDATES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SocialUpdateDAO(SocialDAOFactory factory)
    {
        super(factory, "SOCIAL_UPDATES");
    }

    /**
     * Defines the columns and indices for the SOCIAL_UPDATES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("ORGANISATION", Types.VARCHAR, 5, false);
        table.addColumn("CONTENT_TYPE", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SOCIAL_UPDATES_PK", new String[] {"ID"});
        table.addIndex("SOCIAL_UPDATES_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a content item from the SOCIAL_UPDATES table by id.
     */
    public SocialUpdate getById(int id) throws SQLException
    {
        SocialUpdate ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), GET_BY_ID_SQL);
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setInt(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                SocialUpdate update = new SocialUpdate();
                update.setId(rs.getString(1));
                update.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                update.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                update.setOrganisation(rs.getString(4));
                update.setContentType(rs.getString(5));
                update.setStatus(rs.getString(6));
                update.setCreatedBy(rs.getString(7));
                ret = update;
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
     * Stores the given social update in the SOCIAL_UPDATES table.
     */
    public void add(SocialUpdate update) throws SQLException
    {
        if(!hasConnection() || update == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, update.getId());
            insertStmt.setTimestamp(2, new Timestamp(update.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(update.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, update.getOrganisation());
            insertStmt.setString(5, update.getContentType().name());
            insertStmt.setString(6, update.getStatus().name());
            insertStmt.setString(7, update.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created social update '"+update.getId()+"' in SOCIAL_UPDATES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the update already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given social post in the SOCIAL_POSTS table.
     */
/* GERALD: needed?
    public void update(SocialPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(post.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, post.getOrganisation());
        updateStmt.setString(3, post.getMessage());
        updateStmt.setString(4, post.getStatus().name());
        updateStmt.setString(5, post.getId());
        updateStmt.executeUpdate();

        logger.info("Updated social post '"+post.getId()+"' in SOCIAL_POSTS");
    }
*/
    /**
     * Returns the social updates from the SOCIAL_UPDATES table.
     */
    public List<SocialUpdate> list() throws SQLException
    {
        List<SocialUpdate> ret = null;

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
            ret = new ArrayList<SocialUpdate>();
            while(rs.next())
            {
                SocialUpdate update = new SocialUpdate();
                update.setId(rs.getString(1));
                update.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                update.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                update.setOrganisation(rs.getString(4));
                update.setContentType(rs.getString(5));
                update.setStatus(rs.getString(6));
                update.setCreatedBy(rs.getString(7));
                ret.add(update);
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
     * Returns the count of social updates from the table.
     */
    public int count(String code) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), COUNT_SQL);
        clearParameters(countStmt);

        countStmt.setString(1, code);
        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Removes the given social update from the SOCIAL_UPDATES table.
     */
    public void delete(SocialUpdate update) throws SQLException
    {
        if(!hasConnection() || update == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, update.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted social update '"+update.getId()+"' in SOCIAL_UPDATES");
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
//GERALD
//        closeStatement(updateStmt);
//        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement insertStmt;
//GERALD
//    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
