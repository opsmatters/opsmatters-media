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
import com.opsmatters.media.model.social.SocialPost;
import com.opsmatters.media.model.social.SocialChannels;

/**
 * DAO that provides operations on the SOCIAL_POSTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SocialPostDAO extends SocialDAO<SocialPost>
{
    private static final Logger logger = Logger.getLogger(SocialPostDAO.class.getName());

    /**
     * The query to use to select an item from the SOCIAL_POSTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORGANISATION, MESSAGE, CHANNEL, STATUS, CREATED_BY "
      + "FROM SOCIAL_POSTS WHERE ID=?";

    /**
     * The query to use to insert a social post into the SOCIAL_POSTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SOCIAL_POSTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, ORGANISATION, MESSAGE, CHANNEL, STATUS, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a social post in the SOCIAL_POSTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE SOCIAL_POSTS SET UPDATED_DATE=?, ORGANISATION=?, MESSAGE=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the social posts from the SOCIAL_POSTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, ORGANISATION, MESSAGE, CHANNEL, STATUS, CREATED_BY "
      + "FROM SOCIAL_POSTS ORDER BY CREATED_DATE DESC";

    /**
     * The query to use to get the count of social posts from the SOCIAL_POSTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SOCIAL_POSTS";

    /**
     * The query to use to delete a social post from the SOCIAL_POSTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SOCIAL_POSTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SocialPostDAO(SocialDAOFactory factory)
    {
        super(factory, "SOCIAL_POSTS");
    }

    /**
     * Defines the columns and indices for the SOCIAL_POSTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("ORGANISATION", Types.VARCHAR, 5, false);
        table.addColumn("MESSAGE", Types.VARCHAR, 512, true);
        table.addColumn("CHANNEL", Types.VARCHAR, 15, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SOCIAL_POSTS_PK", new String[] {"ID"});
        table.addIndex("SOCIAL_POSTS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a content item from the SOCIAL_POSTS table by id.
     */
    public SocialPost getById(int id) throws SQLException
    {
        SocialPost ret = null;

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
                SocialPost post = new SocialPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setOrganisation(rs.getString(4));
                post.setMessage(rs.getString(5));
                post.setChannel(SocialChannels.get(rs.getString(6)));
                post.setStatus(rs.getString(7));
                post.setCreatedBy(rs.getString(8));
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
     * Stores the given social post in the SOCIAL_POSTS table.
     */
    public void add(SocialPost post) throws SQLException
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
            insertStmt.setString(4, post.getOrganisation());
            insertStmt.setString(5, post.getMessage());
            insertStmt.setString(6, post.getChannel().getName());
            insertStmt.setString(7, post.getStatus().name());
            insertStmt.setString(8, post.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created social post '"+post.getId()+"' in SOCIAL_POSTS");
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
     * Updates the given social post in the SOCIAL_POSTS table.
     */
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

    /**
     * Returns the social posts from the SOCIAL_POSTS table.
     */
    public List<SocialPost> list() throws SQLException
    {
        List<SocialPost> ret = null;

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
            ret = new ArrayList<SocialPost>();
            while(rs.next())
            {
                SocialPost post = new SocialPost();
                post.setId(rs.getString(1));
                post.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                post.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                post.setOrganisation(rs.getString(4));
                post.setMessage(rs.getString(5));
                post.setChannel(SocialChannels.get(rs.getString(6)));
                post.setStatus(rs.getString(7));
                post.setCreatedBy(rs.getString(8));
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
     * Returns the count of content items from the table.
     */
    public int count(String code) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), String.format(COUNT_SQL, getTableName()));
        clearParameters(countStmt);

        countStmt.setString(1, code);
        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Removes the given social post from the SOCIAL_POSTS table.
     */
    public void delete(SocialPost post) throws SQLException
    {
        if(!hasConnection() || post == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, post.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted social post '"+post.getId()+"' in SOCIAL_POSTS");
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
