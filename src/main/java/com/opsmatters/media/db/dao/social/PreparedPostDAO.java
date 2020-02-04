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
import com.opsmatters.media.model.social.PreparedPost;
import com.opsmatters.media.model.social.SocialChannels;

/**
 * DAO that provides operations on the PREPARED_POSTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PreparedPostDAO extends SocialDAO<PreparedPost>
{
    private static final Logger logger = Logger.getLogger(PreparedPostDAO.class.getName());

    /**
     * The query to use to select a post from the PREPARED_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, ORGANISATION, TITLE, MESSAGE, CHANNEL, STATUS, CREATED_BY "
      + "FROM PREPARED_POSTS WHERE ID=?";

    /**
     * The query to use to insert a post into the PREPARED_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PREPARED_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, TYPE, ORGANISATION, TITLE, MESSAGE, CHANNEL, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a post in the PREPARED_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PREPARED_POSTS SET UPDATED_DATE=?, ORGANISATION=?, TITLE=?, MESSAGE=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the posts from the PREPARED_POSTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, TYPE, ORGANISATION, TITLE, MESSAGE, CHANNEL, STATUS, CREATED_BY "
      + "FROM PREPARED_POSTS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of posts from the PREPARED_POSTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM PREPARED_POSTS";

    /**
     * The query to use to delete a post from the PREPARED_POSTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM PREPARED_POSTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public PreparedPostDAO(SocialDAOFactory factory)
    {
        super(factory, "PREPARED_POSTS");
    }

    /**
     * Defines the columns and indices for the PREPARED_POSTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("ORGANISATION", Types.VARCHAR, 5, false);
        table.addColumn("TITLE", Types.VARCHAR, 50, false);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("CHANNEL", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("PREPARED_POSTS_PK", new String[] {"ID"});
        table.addIndex("PREPARED_POSTS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a post from the PREPARED_POSTS table by id.
     */
    public PreparedPost getById(int id) throws SQLException
    {
        PreparedPost ret = null;

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
                PreparedPost post = new PreparedPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setOrganisation(rs.getString(5));
                post.setTitle(rs.getString(6));
                post.setMessage(rs.getString(7));
                post.setChannel(SocialChannels.get(rs.getString(8)));
                post.setStatus(rs.getString(9));
                post.setCreatedBy(rs.getString(10));
                ret = post;
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
     * Stores the given post in the PREPARED_POSTS table.
     */
    public void add(PreparedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, post.getId());
            insertStmt.setTimestamp(2, new Timestamp(post.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(post.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, post.getType().name());
            insertStmt.setString(5, post.getOrganisation());
            insertStmt.setString(6, post.getTitle());
            insertStmt.setString(7, post.getMessage());
            insertStmt.setString(8, post.getChannel().getName());
            insertStmt.setString(9, post.getStatus().name());
            insertStmt.setString(10, post.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created post '"+post.getId()+"' in PREPARED_POSTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the post already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given post in the PREPARED_POSTS table.
     */
    public void update(PreparedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(post.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, post.getOrganisation());
        updateStmt.setString(3, post.getTitle());
        updateStmt.setString(4, post.getMessage());
        updateStmt.setString(5, post.getStatus().name());
        updateStmt.setString(6, post.getId());
        updateStmt.executeUpdate();

        logger.info("Updated post '"+post.getId()+"' in PREPARED_POSTS");
    }

    /**
     * Returns the posts from the PREPARED_POSTS table.
     */
    public List<PreparedPost> list() throws SQLException
    {
        List<PreparedPost> ret = null;

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
            ret = new ArrayList<PreparedPost>();
            while(rs.next())
            {
                PreparedPost post = new PreparedPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setType(rs.getString(4));
                post.setOrganisation(rs.getString(5));
                post.setTitle(rs.getString(6));
                post.setMessage(rs.getString(7));
                post.setChannel(SocialChannels.get(rs.getString(8)));
                post.setStatus(rs.getString(9));
                post.setCreatedBy(rs.getString(10));
                ret.add(post);
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
     * Returns the count of posts from the table.
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
     * Removes the given post from the PREPARED_POSTS table.
     */
    public void delete(PreparedPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, post.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted post '"+post.getId()+"' in PREPARED_POSTS");
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
