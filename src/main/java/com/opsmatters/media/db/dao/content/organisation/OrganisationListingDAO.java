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
package com.opsmatters.media.db.dao.content.organisation;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.organisation.OrganisationListing;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;

/**
 * DAO that provides operations on the ORGANISATION_LISTINGS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationListingDAO extends ContentDAO<OrganisationListing>
{
    private static final Logger logger = Logger.getLogger(OrganisationListingDAO.class.getName());

    /**
     * The query to use to select an organisation listing from the table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM ORGANISATION_LISTINGS WHERE SITE_ID=? AND ID=?";

    /**
     * The query to use to select an organisation listing from the table by code.
     */
    private static final String GET_BY_CODE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM ORGANISATION_LISTINGS WHERE SITE_ID=? AND CODE=?";

    /**
     * The query to use to select all the organisation listings from the table.
     */
    private static final String LIST_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM ORGANISATION_LISTINGS WHERE SITE_ID=? ORDER BY ID";

    /**
     * The query to use to select the matching organisation listings from the table.
     */
    private static final String LIST_LIKE_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, PUBLISHED, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM ORGANISATION_LISTINGS WHERE SITE_ID=? AND TITLE LIKE ? ORDER BY ID";

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
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, STATUS, ATTRIBUTES, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an organisation listing in the ORGANISATION_LISTINGS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATION_LISTINGS SET UUID=?, PUBLISHED_DATE=?, CODE=?, TITLE=?, STATUS=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND ID=?";

    /**
     * The query to use to get the last ID from the table.
     */
    private static final String GET_MAX_ID_SQL =  
      "SELECT MAX(ID) FROM ORGANISATION_LISTINGS WHERE SITE_ID=?";

    /**
     * The query to use to delete an organisation listing from the table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATION_LISTINGS WHERE SITE_ID=? AND ID=?";

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
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("TITLE", Types.VARCHAR, 60, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("LISTINGS_PK", new String[] {"UUID"});
        table.addIndex("LISTINGS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("LISTINGS_TITLE_IDX", new String[] {"SITE_ID","TITLE"});
        table.addIndex("LISTINGS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns an organisation listing from the table by id.
     */
    @Override
    public OrganisationListing getById(String siteId, String code, int id) throws SQLException
    {
        return getById(siteId, id);
    }

    /**
     * Returns an organisation listing from the table by id.
     */
    public synchronized OrganisationListing getById(String siteId, int id) throws SQLException
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
            getByIdStmt.setString(1, siteId);
            getByIdStmt.setInt(2, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                OrganisationListing content = new OrganisationListing();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret = content;
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
     * Returns an organisation listing from the table by code.
     */
    public synchronized OrganisationListing getByCode(String siteId, String code) throws SQLException
    {
        OrganisationListing ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByCodeStmt == null)
            getByCodeStmt = prepareStatement(getConnection(), GET_BY_CODE_SQL);
        clearParameters(getByCodeStmt);

        ResultSet rs = null;

        try
        {
            getByCodeStmt.setString(1, siteId);
            getByCodeStmt.setString(2, code);
            getByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByCodeStmt.executeQuery();
            while(rs.next())
            {
                OrganisationListing content = new OrganisationListing();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret = content;
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
    public List<OrganisationListing> list(Site site, String code) throws SQLException
    {
        return list(site);
    }

    /**
     * Returns the organisation listings from the table.
     */
    public synchronized List<OrganisationListing> list(Site site) throws SQLException
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
            listStmt.setString(1, site.getId());
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<OrganisationListing>();
            while(rs.next())
            {
                OrganisationListing content = new OrganisationListing();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
    public synchronized List<OrganisationListing> listByName(Site site, String name) throws SQLException
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
            listLikeStmt.setString(1, site.getId());
            listLikeStmt.setString(2, name+"%");
            listLikeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listLikeStmt.executeQuery();
            ret = new ArrayList<OrganisationListing>();
            while(rs.next())
            {
                OrganisationListing content = new OrganisationListing();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setPublished(rs.getBoolean(6));
                content.setAttributes(new JSONObject(getClob(rs, 7)));
                content.setStatus(rs.getString(8));
                content.setCreatedBy(rs.getString(9));
                ret.add(content);
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
    public int count(Site site, String code) throws SQLException
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
    public synchronized void add(OrganisationListing listing) throws SQLException
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
            insertStmt.setString(1, listing.getUuid());
            insertStmt.setString(2, listing.getSiteId());
            insertStmt.setString(3, listing.getCode());
            insertStmt.setInt(4, listing.getId());
            insertStmt.setTimestamp(5, new Timestamp(listing.getPublishedDateMillis()), UTC);
            insertStmt.setString(6, listing.getTitle());
            insertStmt.setString(7, listing.getStatus().name());
            String attributes = listing.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(8, reader, attributes.length());
            insertStmt.setString(9, listing.getCreatedBy());
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
    public synchronized void update(OrganisationListing listing) throws SQLException
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
            updateStmt.setString(1, listing.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(listing.getPublishedDateMillis()), UTC);
            updateStmt.setString(3, listing.getCode());
            updateStmt.setString(4, listing.getTitle());
            updateStmt.setString(5, listing.getStatus().name());
            String attributes = listing.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader, attributes.length());
            updateStmt.setString(7, listing.getSiteId());
            updateStmt.setInt(8, listing.getId());
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
    public synchronized void delete(OrganisationListing listing) throws SQLException
    {
        if(!hasConnection() || listing == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, listing.getSiteId());
        deleteStmt.setInt(2, listing.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted %s '%s' in %s (GUID=%s, code=%s)", 
            listing.getType().value(), listing.getTitle(), getTableName(), 
            listing.getGuid(), listing.getCode()));
    }

    /**
     * Returns the maximum ID from the table.
     */
    @Override
    protected int getMaxId(String siteId, String code) throws SQLException
    {
        return getMaxId(siteId);
    }

    /**
     * Returns the maximum ID from the table.
     */
    private synchronized int getMaxId(String siteId) throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(maxIdStmt == null)
            maxIdStmt = prepareStatement(getConnection(), GET_MAX_ID_SQL);
        clearParameters(maxIdStmt);

        maxIdStmt.setString(1, siteId);
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
            existing = getById(listing.getSiteId(), listing.getId());
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
                listing.setId(getMaxId(listing.getSiteId())+1);
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
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByCodeStmt);
        getByCodeStmt = null;
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

    private PreparedStatement getByIdStmt;
    private PreparedStatement getByCodeStmt;
    private PreparedStatement listStmt;
    private PreparedStatement listLikeStmt;
    private PreparedStatement countStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement maxIdStmt;
    private PreparedStatement deleteStmt;
}