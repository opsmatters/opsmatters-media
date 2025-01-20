/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.db.dao.platform;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the SITES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class SiteDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(SiteDAO.class.getName());

    /**
     * The query to use to select a site from the SITES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, ICON, DOMAIN, SHORT_DOMAIN, NEWSLETTER_DAY, NEWSLETTER_HOUR, ENABLED, CREATED_BY "
      + "FROM SITES WHERE ID=?";

    /**
     * The query to use to insert a site into the SITES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SITES"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, ICON, DOMAIN, SHORT_DOMAIN, NEWSLETTER_DAY, NEWSLETTER_HOUR, ENABLED, CREATED_BY )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a site in the SITES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE SITES SET UPDATED_DATE=?, NAME=?, ICON=?, DOMAIN=?, SHORT_DOMAIN=?, NEWSLETTER_DAY=?, NEWSLETTER_HOUR=?, ENABLED=?, CREATED_BY=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the sites from the SITES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, ICON, DOMAIN, SHORT_DOMAIN, NEWSLETTER_DAY, NEWSLETTER_HOUR, ENABLED, CREATED_BY "
      + "FROM SITES ORDER BY ID";

    /**
     * The query to use to get the count of sites from the SITES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SITES";

    /**
     * The query to use to delete a site from the SITES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SITES WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public SiteDAO(PlatformDAOFactory factory)
    {
        super(factory, "SITES");
    }

    /**
     * Defines the columns and indices for the SITES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 3, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 20, true);
        table.addColumn("ICON", Types.VARCHAR, 15, true);
        table.addColumn("DOMAIN", Types.VARCHAR, 20, true);
        table.addColumn("SHORT_DOMAIN", Types.VARCHAR, 20, true);
        table.addColumn("NEWSLETTER_DAY", Types.INTEGER, true);
        table.addColumn("NEWSLETTER_HOUR", Types.INTEGER, true);
        table.addColumn("ENABLED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SITES_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a site from the SITES table by id.
     */
    public synchronized Site getById(String id) throws SQLException
    {
        Site ret = null;

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
                Site site = new Site();
                site.setId(rs.getString(1));
                site.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                site.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                site.setName(rs.getString(4));
                site.setIcon(rs.getString(5));
                site.setDomain(rs.getString(6));
                site.setShortDomain(rs.getString(7));
                site.setNewsletterDay(rs.getInt(8));
                site.setNewsletterHour(rs.getInt(9));
                site.setEnabled(rs.getBoolean(10));
                site.setCreatedBy(rs.getString(11));
                ret = site;
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
     * Stores the given site in the SITES table.
     */
    public synchronized void add(Site site) throws SQLException
    {
        if(!hasConnection() || site == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, site.getId());
            insertStmt.setTimestamp(2, new Timestamp(site.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(site.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, site.getName());
            insertStmt.setString(5, site.getIcon());
            insertStmt.setString(6, site.getDomain());
            insertStmt.setString(7, site.getShortDomain());
            insertStmt.setInt(8, site.getNewsletterDay());
            insertStmt.setInt(9, site.getNewsletterHour());
            insertStmt.setBoolean(10, site.isEnabled());
            insertStmt.setString(11, site.getCreatedBy());
            insertStmt.executeUpdate();

            logger.info("Created site '"+site.getId()+"' in SITES");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the site already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given site in the SITES table.
     */
    public synchronized void update(Site site) throws SQLException
    {
        if(!hasConnection() || site == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(site.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, site.getName());
        updateStmt.setString(3, site.getIcon());
        updateStmt.setString(4, site.getDomain());
        updateStmt.setString(5, site.getShortDomain());
        updateStmt.setInt(6, site.getNewsletterDay());
        updateStmt.setInt(7, site.getNewsletterHour());
        updateStmt.setBoolean(8, site.isEnabled());
        updateStmt.setString(9, site.getCreatedBy());
        updateStmt.setString(10, site.getId());
        updateStmt.executeUpdate();

        logger.info("Updated site '"+site.getId()+"' in SITES");
    }

    /**
     * Returns the sites from the SITES table.
     */
    public synchronized List<Site> list() throws SQLException
    {
        List<Site> ret = null;

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
            ret = new ArrayList<Site>();
            while(rs.next())
            {
                Site site = new Site();
                site.setId(rs.getString(1));
                site.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                site.setUpdatedDateMillis(rs.getTimestamp(3, UTC) != null ? rs.getTimestamp(3, UTC).getTime() : 0L);
                site.setName(rs.getString(4));
                site.setIcon(rs.getString(5));
                site.setDomain(rs.getString(6));
                site.setShortDomain(rs.getString(7));
                site.setNewsletterDay(rs.getInt(8));
                site.setNewsletterHour(rs.getInt(9));
                site.setEnabled(rs.getBoolean(10));
                site.setCreatedBy(rs.getString(11));
                ret.add(site);
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
     * Returns the count of sites from the SITES table.
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
     * Removes the given site from the SITES table.
     */
    public synchronized void delete(Site site) throws SQLException
    {
        if(!hasConnection() || site == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, site.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted site '"+site.getId()+"' in SITES");
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
