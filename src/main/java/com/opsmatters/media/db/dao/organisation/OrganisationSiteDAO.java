/*
 * Copyright 2022 Gerald Curley
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
package com.opsmatters.media.db.dao.organisation;

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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.organisation.OrganisationSite;
import com.opsmatters.media.model.organisation.OrganisationSiteItem;
import com.opsmatters.media.model.organisation.ArchiveReason;

/**
 * DAO that provides operations on the ORGANISATION_SITES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationSiteDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(OrganisationSiteDAO.class.getName());

    /**
     * The query to use to select an organisation from the ORGANISATION_SITES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT SITE_ID, ID, CREATED_DATE, UPDATED_DATE, CODE, ATTRIBUTES, SPONSOR, LISTING, STATUS, REASON, CREATED_BY "
      + "FROM ORGANISATION_SITES WHERE ID=?";

    /**
     * The query to use to select an organisation from the ORGANISATION_SITES table by code.
     */
    private static final String GET_BY_CODE_SQL =  
      "SELECT SITE_ID, ID, CREATED_DATE, UPDATED_DATE, CODE, ATTRIBUTES, SPONSOR, LISTING, STATUS, REASON, CREATED_BY "
      + "FROM ORGANISATION_SITES WHERE SITE_ID=? AND CODE=?";

    /**
     * The query to use to insert an organisation into the ORGANISATION_SITES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORGANISATION_SITES"
      + "( SITE_ID, ID, CREATED_DATE, UPDATED_DATE, CODE, ATTRIBUTES, SPONSOR, LISTING, STATUS, REASON, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an organisation in the ORGANISATION_SITES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATION_SITES SET CODE=?, UPDATED_DATE=?, ATTRIBUTES=?, SPONSOR=?, LISTING=?, STATUS=?, REASON=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the organisations from the ORGANISATION_SITES table.
     */
    private static final String LIST_SQL =  
      "SELECT SITE_ID, ID, CREATED_DATE, UPDATED_DATE, CODE, ATTRIBUTES, SPONSOR, LISTING, STATUS, REASON, CREATED_BY "
      + "FROM ORGANISATION_SITES ORDER BY SITE_ID";

    /**
     * The query to use to select the organisations from the ORGANISATION_SITES table by site.
     */
    private static final String LIST_BY_SITE_SQL =  
      "SELECT SITE_ID, ID, CREATED_DATE, UPDATED_DATE, CODE, ATTRIBUTES, SPONSOR, LISTING, STATUS, REASON, CREATED_BY "
      + "FROM ORGANISATION_SITES WHERE SITE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to select the organisation items from the ORGANISATION_SITES table by site.
     */
    private static final String LIST_ITEMS_BY_SITE_SQL =  
      "SELECT SITE_ID, ID, CREATED_DATE, UPDATED_DATE, CODE, SPONSOR, LISTING, STATUS, REASON "
      + "FROM ORGANISATION_SITES WHERE SITE_ID=? ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of organisations from the ORGANISATION_SITES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORGANISATION_SITES";

    /**
     * The query to use to delete an organisation from the ORGANISATION_SITES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATION_SITES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrganisationSiteDAO(OrganisationDAOFactory factory)
    {
        super(factory, "ORGANISATION_SITES");
    }

    /**
     * Defines the columns and indices for the ORGANISATION_SITES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SPONSOR", Types.BOOLEAN, true);
        table.addColumn("LISTING", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("REASON", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("ORGANISATION_SITES_PK", new String[] {"ID"});
        table.addIndex("ORGANISATION_SITES_CODE_IDX", new String[] {"SITE_ID, CODE"});
        table.setInitialised(true);
    }

    /**
     * Returns an organisation from the ORGANISATION_SITES table by id.
     */
    public synchronized OrganisationSite getById(String id) throws SQLException
    {
        OrganisationSite ret = null;

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
                OrganisationSite organisation = new OrganisationSite();
                organisation.setSiteId(rs.getString(1));
                organisation.setId(rs.getString(2));
                organisation.setCreatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setSponsor(rs.getBoolean(7));
                organisation.setListing(rs.getBoolean(8));
                organisation.setStatus(rs.getString(9));
                organisation.setReason(rs.getString(10));
                organisation.setCreatedBy(rs.getString(11));
                ret = organisation;
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
     * Returns an organisation from the ORGANISATION_SITES table by code.
     */
    public synchronized OrganisationSite getByCode(String siteId, String code) throws SQLException
    {
        OrganisationSite ret = null;

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
                OrganisationSite organisation = new OrganisationSite();
                organisation.setSiteId(rs.getString(1));
                organisation.setId(rs.getString(2));
                organisation.setCreatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setSponsor(rs.getBoolean(7));
                organisation.setListing(rs.getBoolean(8));
                organisation.setStatus(rs.getString(9));
                organisation.setReason(rs.getString(10));
                organisation.setCreatedBy(rs.getString(11));
                ret = organisation;
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
     * Stores the given organisation in the ORGANISATION_SITES table.
     */
    public synchronized void add(OrganisationSite organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, organisation.getSiteId());
            insertStmt.setString(2, organisation.getId());
            insertStmt.setTimestamp(3, new Timestamp(organisation.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(4, new Timestamp(organisation.getUpdatedDateMillis()), UTC);
            insertStmt.setString(5, organisation.getCode());
            String attributes = organisation.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(6, reader, attributes.length());
            insertStmt.setBoolean(7, organisation.isSponsor());
            insertStmt.setBoolean(8, organisation.hasListing());
            insertStmt.setString(9, organisation.getStatus().name());
            insertStmt.setString(10, organisation.getReason().name());
            insertStmt.setString(11, organisation.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created organisation site '"+organisation.getId()+"' in ORGANISATION_SITES");
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
     * Updates the given organisation in the ORGANISATION_SITES table.
     */
    public synchronized void update(OrganisationSite organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setString(1, organisation.getCode());
            updateStmt.setTimestamp(2, new Timestamp(organisation.getUpdatedDateMillis()), UTC);
            String attributes = organisation.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(3, reader, attributes.length());
            updateStmt.setBoolean(4, organisation.isSponsor());
            updateStmt.setBoolean(5, organisation.hasListing());
            updateStmt.setString(6, organisation.getStatus().name());
            updateStmt.setString(7, organisation.getReason() != null ? organisation.getReason().name() : ArchiveReason.NONE.name());
            updateStmt.setString(8, organisation.getId());
            updateStmt.executeUpdate();

            logger.info("Updated organisation site '"+organisation.getId()+"' in ORGANISATION_SITES");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Adds or Updates the given monitor in the ORGANISATION_SITES table.
     */
    public boolean upsert(OrganisationSite organisation) throws SQLException
    {
        boolean ret = false;

        OrganisationSite existing = getById(organisation.getId());
        if(existing != null)
        {
            update(organisation);
        }
        else
        {
            add(organisation);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the organisations from the ORGANISATION_SITES table.
     */
    public synchronized List<OrganisationSite> list() throws SQLException
    {
        List<OrganisationSite> ret = null;

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
            ret = new ArrayList<OrganisationSite>();
            while(rs.next())
            {
                OrganisationSite organisation = new OrganisationSite();
                organisation.setSiteId(rs.getString(1));
                organisation.setId(rs.getString(2));
                organisation.setCreatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setSponsor(rs.getBoolean(7));
                organisation.setListing(rs.getBoolean(8));
                organisation.setStatus(rs.getString(9));
                organisation.setReason(rs.getString(10));
                organisation.setCreatedBy(rs.getString(11));
                ret.add(organisation);
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
     * Returns the organisations from the ORGANISATION_SITES table by site.
     */
    public synchronized List<OrganisationSite> list(Site site) throws SQLException
    {
        List<OrganisationSite> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listBySiteStmt == null)
            listBySiteStmt = prepareStatement(getConnection(), LIST_BY_SITE_SQL);
        clearParameters(listBySiteStmt);

        ResultSet rs = null;

        try
        {
            listBySiteStmt.setString(1, site.getId());
            listBySiteStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listBySiteStmt.executeQuery();
            ret = new ArrayList<OrganisationSite>();
            while(rs.next())
            {
                OrganisationSite organisation = new OrganisationSite();
                organisation.setSiteId(rs.getString(1));
                organisation.setId(rs.getString(2));
                organisation.setCreatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(5));
                organisation.setAttributes(new JSONObject(getClob(rs, 6)));
                organisation.setSponsor(rs.getBoolean(7));
                organisation.setListing(rs.getBoolean(8));
                organisation.setStatus(rs.getString(9));
                organisation.setReason(rs.getString(10));
                organisation.setCreatedBy(rs.getString(11));
                ret.add(organisation);
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
     * Returns the organisation items from the ORGANISATION_SITES table by site.
     */
    public synchronized List<OrganisationSiteItem> listItems(Site site) throws SQLException
    {
        List<OrganisationSiteItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listItemsBySiteStmt == null)
            listItemsBySiteStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_SITE_SQL);
        clearParameters(listItemsBySiteStmt);

        ResultSet rs = null;

        try
        {
            listItemsBySiteStmt.setString(1, site.getId());
            listItemsBySiteStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listItemsBySiteStmt.executeQuery();
            ret = new ArrayList<OrganisationSiteItem>();
            while(rs.next())
            {
                OrganisationSiteItem organisation = new OrganisationSiteItem();
                organisation.setSiteId(rs.getString(1));
                organisation.setId(rs.getString(2));
                organisation.setCreatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                organisation.setUpdatedDateMillis(rs.getTimestamp(4, UTC) != null ? rs.getTimestamp(4, UTC).getTime() : 0L);
                organisation.setCode(rs.getString(5));
                organisation.setSponsor(rs.getBoolean(6));
                organisation.setListing(rs.getBoolean(7));
                organisation.setStatus(rs.getString(8));
                organisation.setReason(rs.getString(9));
                ret.add(organisation);
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
     * Returns the count of organisations from the table.
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
     * Removes the given organisation from the ORGANISATION_SITES table.
     */
    public synchronized void delete(OrganisationSite organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, organisation.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted organisation site '"+organisation.getId()+"' in ORGANISATION_SITES");
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
        closeStatement(listBySiteStmt);
        listBySiteStmt = null;
        closeStatement(listItemsBySiteStmt);
        listItemsBySiteStmt = null;
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
    private PreparedStatement listBySiteStmt;
    private PreparedStatement listItemsBySiteStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
