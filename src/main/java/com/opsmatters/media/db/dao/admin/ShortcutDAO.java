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
package com.opsmatters.media.db.dao.admin;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.Shortcut;
import com.opsmatters.media.db.dao.BaseDAO;

/**
 * DAO that provides operations on the SHORTCUTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ShortcutDAO extends BaseDAO
{
    private static final Logger logger = Logger.getLogger(ShortcutDAO.class.getName());

    /**
     * The query to use to select a shortcut from the SHORTCUTS table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, TYPE, MENU, SITE_ID, SELECTION, URL, ICON, POSITION, STATUS "
      + "FROM SHORTCUTS WHERE ID=?";

    /**
     * The query to use to insert a shortcut into the SHORTCUTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO SHORTCUTS"
      + "( ID, CREATED_DATE, UPDATED_DATE, NAME, TYPE, MENU, SITE_ID, SELECTION, URL, ICON, POSITION, STATUS )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a shortcut in the SHORTCUTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE SHORTCUTS SET UPDATED_DATE=?, NAME=?, TYPE=?, MENU=?, SITE_ID=?, SELECTION=?, URL=?, ICON=?, POSITION=?, STATUS=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the shortcuts from the SHORTCUTS table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, NAME, TYPE, MENU, SITE_ID, SELECTION, URL, ICON, POSITION, STATUS "
      + "FROM SHORTCUTS ORDER BY CREATED_DATE";

    /**
     * The query to use to get the count of shortcuts from the SHORTCUTS table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM SHORTCUTS";

    /**
     * The query to use to delete a shortcut from the SHORTCUTS table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM SHORTCUTS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ShortcutDAO(AdminDAOFactory factory)
    {
        super(factory, "SHORTCUTS");
    }

    /**
     * Defines the columns and indices for the SHORTCUTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("NAME", Types.VARCHAR, 30, true);
        table.addColumn("TYPE", Types.VARCHAR, 15, true);
        table.addColumn("MENU", Types.VARCHAR, 15, false);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, false);
        table.addColumn("SELECTION", Types.VARCHAR, 30, false);
        table.addColumn("URL", Types.VARCHAR, 128, false);
        table.addColumn("ICON", Types.VARCHAR, 30, true);
        table.addColumn("POSITION", Types.INTEGER, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.setPrimaryKey("SHORTCUTS_PK", new String[] {"ID"});
        table.setInitialised(true);
    }

    /**
     * Returns a shortcut from the SHORTCUTS table by id.
     */
    public synchronized Shortcut getById(String id) throws SQLException
    {
        Shortcut ret = null;

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
                Shortcut shortcut = new Shortcut();
                shortcut.setId(rs.getString(1));
                shortcut.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                shortcut.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                shortcut.setName(rs.getString(4));
                shortcut.setType(rs.getString(5));
                shortcut.setMenu(rs.getString(6));
                shortcut.setSiteId(rs.getString(7));
                shortcut.setSelection(rs.getString(8));
                shortcut.setUrl(rs.getString(9));
                shortcut.setIcon(rs.getString(10));
                shortcut.setPosition(rs.getInt(11));
                shortcut.setStatus(rs.getString(12));
                ret = shortcut;
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
     * Stores the given shortcut in the SHORTCUTS table.
     */
    public synchronized void add(Shortcut shortcut) throws SQLException
    {
        if(!hasConnection() || shortcut == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, shortcut.getId());
            insertStmt.setTimestamp(2, new Timestamp(shortcut.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(shortcut.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, shortcut.getName());
            insertStmt.setString(5, shortcut.getType().name());
            insertStmt.setString(6, shortcut.getMenu());
            insertStmt.setString(7, shortcut.getSiteId());
            insertStmt.setString(8, shortcut.getSelection());
            insertStmt.setString(9, shortcut.getUrl());
            insertStmt.setString(10, shortcut.getIcon());
            insertStmt.setInt(11, shortcut.getPosition());
            insertStmt.setString(12, shortcut.getStatus().name());
            insertStmt.executeUpdate();

            logger.info("Created shortcut '"+shortcut.getId()+"' in SHORTCUTS");
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the shortcut already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given shortcut in the SHORTCUTS table.
     */
    public synchronized void update(Shortcut shortcut) throws SQLException
    {
        if(!hasConnection() || shortcut == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(shortcut.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, shortcut.getName());
        updateStmt.setString(3, shortcut.getType().name());
        updateStmt.setString(4, shortcut.getMenu());
        updateStmt.setString(5, shortcut.getSiteId());
        updateStmt.setString(6, shortcut.getSelection());
        updateStmt.setString(7, shortcut.getUrl());
        updateStmt.setString(8, shortcut.getIcon());
        updateStmt.setInt(9, shortcut.getPosition());
        updateStmt.setString(10, shortcut.getStatus().name());
        updateStmt.setString(11, shortcut.getId());
        updateStmt.executeUpdate();

        logger.info("Updated shortcut '"+shortcut.getId()+"' in SHORTCUTS");
    }

    /**
     * Adds or Updates the given shortcut in the SHORTCUTS table.
     */
    public boolean upsert(Shortcut shortcut) throws SQLException
    {
        boolean ret = false;

        Shortcut existing = getById(shortcut.getId());
        if(existing != null)
        {
            update(shortcut);
        }
        else
        {
            add(shortcut);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the shortcuts from the SHORTCUTS table.
     */
    public synchronized List<Shortcut> list() throws SQLException
    {
        List<Shortcut> ret = null;

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
            ret = new ArrayList<Shortcut>();
            while(rs.next())
            {
                Shortcut shortcut = new Shortcut();
                shortcut.setId(rs.getString(1));
                shortcut.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                shortcut.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                shortcut.setName(rs.getString(4));
                shortcut.setType(rs.getString(5));
                shortcut.setMenu(rs.getString(6));
                shortcut.setSiteId(rs.getString(7));
                shortcut.setSelection(rs.getString(8));
                shortcut.setUrl(rs.getString(9));
                shortcut.setIcon(rs.getString(10));
                shortcut.setPosition(rs.getInt(11));
                shortcut.setStatus(rs.getString(12));
                ret.add(shortcut);
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
     * Returns the count of shortcuts from the table.
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
     * Removes the given shortcut from the SHORTCUTS table.
     */
    public synchronized void delete(Shortcut shortcut) throws SQLException
    {
        if(!hasConnection() || shortcut == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, shortcut.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted shortcut '"+shortcut.getId()+"' in SHORTCUTS");
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
