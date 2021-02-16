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
package com.opsmatters.media.db.dao.content;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.content.OrganisationListing;
import com.opsmatters.media.model.content.ContentStatus;

/**
 * DAO that provides operations on the ORGANISATION_LISTINGS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationListingDAO extends ContentDAO<OrganisationListing>
{
    private static final Logger logger = Logger.getLogger(OrganisationListingDAO.class.getName());

    /**
     * The query to use to select an organisation listing from the ORGANISATION_LISTINGS table by UUID.
     */
    private static final String GET_BY_UUID_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATION_LISTINGS WHERE UUID=?";

    /**
     * The query to use to select an organisation listing from the table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATION_LISTINGS WHERE ID=?";

    /**
     * The query to use to select all the organisation listings from the table.
     */
    private static final String LIST_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATION_LISTINGS ORDER BY ID";

    /**
     * The query to use to select the matching organisation listings from the table.
     */
    private static final String LIST_LIKE_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATION_LISTINGS WHERE TITLE LIKE ? ORDER BY ID";

    /**
     * The query to use to get the count of organisation listings from the table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORGANISATION_LISTINGS";

    /**
     * The query to use to insert an organisation listing into the ORGANISATION_LISTINGS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORGANISATION_LISTINGS"
      + "( ID, PUBLISHED_DATE, UUID, CODE, TITLE, STATUS, ATTRIBUTES, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an organisation listing in the ORGANISATION_LISTINGS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATION_LISTINGS SET PUBLISHED_DATE=?, UUID=?, CODE=?, TITLE=?, STATUS=?, ATTRIBUTES=? "
      + "WHERE ID=?";

    /**
     * The query to use to get the last ID from the table.
     */
    private static final String GET_MAX_ID_SQL =  
      "SELECT MAX(ID) FROM ORGANISATION_LISTINGS";

    /**
     * The query to use to delete an organisation listing from the table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATION_LISTINGS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrganisationListingDAO(ContentDAOFactory factory)
    {
        super(factory, "ORGANISATION_LISTINGS");
    }

    /**
     * Defines the columns and indices for the ORGANISATION_LISTINGS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("TITLE", Types.VARCHAR, 60, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("ORGANISATION_LISTINGS_PK", new String[] {"ID"});
        table.addIndex("ORGANISATION_LISTINGS_UUID_IDX", new String[] {"UUID"});
        table.addIndex("ORGANISATION_LISTINGS_TITLE_IDX", new String[] {"TITLE"});
        table.addIndex("ORGANISATION_LISTINGS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns an organisation listing from the ORGANISATION_LISTINGS table by UUID.
     */
    @Override
    public OrganisationListing getByUuid(String code, String uuid) throws SQLException
    {
        return getByUuid(uuid);
    }

    /**
     * Returns an organisation listing from the ORGANISATION_LISTINGS table by UUID.
     */
    public OrganisationListing getByUuid(String uuid) throws SQLException
    {
        OrganisationListing ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUuidStmt == null)
            getByUuidStmt = prepareStatement(getConnection(), GET_BY_UUID_SQL);
        clearParameters(getByUuidStmt);

        ResultSet rs = null;

        try
        {
            getByUuidStmt.setString(1, uuid);
            getByUuidStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUuidStmt.executeQuery();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = new OrganisationListing(attributes);
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
     * Returns an organisation listing from the table by id.
     */
    @Override
    public OrganisationListing getById(String code, int id) throws SQLException
    {
        return getById(id);
    }

    /**
     * Returns an organisation listing from the table by id.
     */
    public OrganisationListing getById(int id) throws SQLException
    {
        OrganisationListing ret = null;

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
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret = new OrganisationListing(attributes);
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
     * Returns the organisation listings from the table.
     */
    @Override
    public List<OrganisationListing> list(String code) throws SQLException
    {
        return list();
    }

    /**
     * Returns the organisation listings from the table.
     */
    public List<OrganisationListing> list() throws SQLException
    {
        List<OrganisationListing> ret = null;

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
            ret = new ArrayList<OrganisationListing>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret.add(new OrganisationListing(attributes));
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
     * Returns the matching organisation listings from the table.
     */
    public List<OrganisationListing> listByName(String name) throws SQLException
    {
        List<OrganisationListing> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listLikeStmt == null)
            listLikeStmt = prepareStatement(getConnection(), LIST_LIKE_SQL);
        clearParameters(listLikeStmt);

        ResultSet rs = null;

        try
        {
            if(name == null)
                name = "";
            listLikeStmt.setString(1, name+"%");
            listLikeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listLikeStmt.executeQuery();
            ret = new ArrayList<OrganisationListing>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret.add(new OrganisationListing(attributes));
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
     * Returns the count of organisation listings from the table.
     */
    @Override
    public int count(String code) throws SQLException
    {
        return count();
    }

    /**
     * Returns the count of  listings from the table.
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
     * Stores the given organisation listing in the ORGANISATION_LISTINGS table.
     */
    @Override
    public void add(OrganisationListing listing) throws SQLException
    {
        if(!hasConnection() || listing == null)
            return;

        if(!listing.hasUniqueId())
            throw new IllegalArgumentException("organisation uuid null");

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setInt(1, listing.getId());
            insertStmt.setTimestamp(2, new Timestamp(listing.getPublishedDateMillis()), UTC);
            insertStmt.setString(3, listing.getUuid());
            insertStmt.setString(4, listing.getCode());
            insertStmt.setString(5, listing.getTitle());
            insertStmt.setString(6, listing.getStatus().name());
            String attributes = listing.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(7, reader, attributes.length());
            insertStmt.setString(8, listing.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info(String.format("Created %s '%s' in %s (GUID=%s, code=%s)", 
                listing.getType().value(), listing.getTitle(), getTableName(), 
                listing.getGuid(), listing.getCode()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the organisation already exists
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
     * Updates the given organisation listing in the ORGANISATION_LISTINGS table.
     */
    @Override
    public void update(OrganisationListing listing) throws SQLException
    {
        if(!hasConnection() || listing == null)
            return;

        if(!listing.hasUniqueId())
            throw new IllegalArgumentException("organisation uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(listing.getPublishedDateMillis()), UTC);
            updateStmt.setString(2, listing.getUuid());
            updateStmt.setString(3, listing.getCode());
            updateStmt.setString(4, listing.getTitle());
            updateStmt.setString(5, listing.getStatus().name());
            String attributes = listing.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader, attributes.length());
            updateStmt.setInt(7, listing.getId());
            updateStmt.executeUpdate();

            logger.info(String.format("Updated %s '%s' in %s (GUID=%s, code=%s)", 
                listing.getType().value(), listing.getTitle(), getTableName(), 
                listing.getGuid(), listing.getCode()));
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Removes the given organisation listing from the table.
     */
    @Override
    public void delete(OrganisationListing listing) throws SQLException
    {
        if(!hasConnection() || listing == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setInt(1, listing.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted %s '%s' in %s (GUID=%s, code=%s)", 
            listing.getType().value(), listing.getTitle(), getTableName(), 
            listing.getGuid(), listing.getCode()));
    }

    /**
     * Returns the maximum ID from the table.
     */
    @Override
    protected int getMaxId(String code) throws SQLException
    {
        return getMaxId();
    }

    /**
     * Returns the maximum ID from the table.
     */
    private int getMaxId() throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(maxIdStmt == null)
            maxIdStmt = prepareStatement(getConnection(), GET_MAX_ID_SQL);
        clearParameters(maxIdStmt);

        maxIdStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = maxIdStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Adds or Updates the given organisation listing in the table.
     */
    public boolean upsert(OrganisationListing listing) throws SQLException
    {
        return upsert(listing, false);
    }

    /**
     * Adds or Updates the given organisation listing in the table.
     */
    @Override
    public boolean upsert(OrganisationListing listing, boolean keepExternal) throws SQLException
    {
        boolean ret = false;

        OrganisationListing existing = getByUuid(listing.getUuid());
        if(existing != null)
        {
            update(listing);
        }
        else if(listing.getId() > 0) // Get by ID as the URL may have changed
        {
            existing = getById(listing.getId());
            if(existing != null)
            {
                update(listing);
            }
            else
            {
                add(listing);
                ret = true;
            }
        }
        else
        {
            synchronized(table)
            {
                listing.setId(getMaxId()+1);
                add(listing);
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByUuidStmt);
        getByUuidStmt = null;
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(listLikeStmt);
        listLikeStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(maxIdStmt);
        maxIdStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByUuidStmt;
    private PreparedStatement getByIdStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listLikeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement maxIdStmt;
    private PreparedStatement deleteStmt;
}