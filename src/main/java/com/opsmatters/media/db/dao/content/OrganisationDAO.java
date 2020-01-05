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
import com.opsmatters.media.model.content.Organisation;

/**
 * DAO that provides operations on the ORGANISATIONS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class OrganisationDAO extends ContentDAO<Organisation>
{
    private static final Logger logger = Logger.getLogger(OrganisationDAO.class.getName());

    /**
     * The query to use to select an organisation from the ORGANISATIONS table by UUID.
     */
    private static final String GET_BY_UUID_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATIONS WHERE UUID=?";

    /**
     * The query to use to select an organisation from the table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATIONS WHERE ID=?";

    /**
     * The query to use to select all the organisations from the table.
     */
    private static final String LIST_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATIONS ORDER BY ID";

    /**
     * The query to use to select the matching organisations from the table.
     */
    private static final String LIST_LIKE_SQL =  
      "SELECT ATTRIBUTES FROM ORGANISATIONS WHERE TITLE LIKE ? ORDER BY ID";

    /**
     * The query to use to get the count of organisations from the table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM ORGANISATIONS";

    /**
     * The query to use to insert an organisation into the ORGANISATIONS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ORGANISATIONS"
      + "( ID, PUBLISHED_DATE, UUID, CODE, TITLE, SPONSOR, TABS, "
      + "PUBLISHED, CREATED_BY, ATTRIBUTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update an organisation in the ORGANISATIONS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ORGANISATIONS SET PUBLISHED_DATE=?, UUID=?, CODE=?, TITLE=?, SPONSOR=?, TABS=?, "
      + "PUBLISHED=?, ATTRIBUTES=? "
      + "WHERE ID=?";

    /**
     * The query to use to get the last ID from the table.
     */
    private static final String GET_MAX_ID_SQL =  
      "SELECT MAX(ID) FROM ORGANISATIONS";

    /**
     * The query to use to delete an organisation from the table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM ORGANISATIONS WHERE ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public OrganisationDAO(ContentDAOFactory factory)
    {
        super(factory, "ORGANISATIONS");
    }

    /**
     * Defines the columns and indices for the ORGANISATIONS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("TITLE", Types.VARCHAR, 60, true);
        table.addColumn("SPONSOR", Types.BOOLEAN, true);
        table.addColumn("TABS", Types.VARCHAR, 10, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("ORGANISATIONS_PK", new String[] {"ID"});
        table.addIndex("ORGANISATIONS_UUID_IDX", new String[] {"UUID"});
        table.addIndex("ORGANISATIONS_TITLE_IDX", new String[] {"TITLE"});
        table.setInitialised(true);
    }

    /**
     * Returns an organisation from the ORGANISATIONS table by UUID.
     */
    @Override
    public Organisation getByUuid(String code, String uuid) throws SQLException
    {
        return getByUuid(uuid);
    }

    /**
     * Returns an organisation from the ORGANISATIONS table by UUID.
     */
    public Organisation getByUuid(String uuid) throws SQLException
    {
        Organisation ret = null;

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
                ret = new Organisation(attributes);
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
     * Returns an organisation from the table by id.
     */
    @Override
    public Organisation getById(String code, int id) throws SQLException
    {
        return getById(id);
    }

    /**
     * Returns an organisation from the table by id.
     */
    public Organisation getById(int id) throws SQLException
    {
        Organisation ret = null;

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
                ret = new Organisation(attributes);
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
     * Returns the organisations from the table.
     */
    @Override
    public List<Organisation> list(String code) throws SQLException
    {
        return list();
    }

    /**
     * Returns the organisations from the table.
     */
    public List<Organisation> list() throws SQLException
    {
        List<Organisation> ret = null;

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
            ret = new ArrayList<Organisation>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret.add(new Organisation(attributes));
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
     * Returns the matching organisations from the table.
     */
    public List<Organisation> listByName(String name) throws SQLException
    {
        List<Organisation> ret = null;

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
            ret = new ArrayList<Organisation>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                ret.add(new Organisation(attributes));
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
    @Override
    public int count(String code) throws SQLException
    {
        return count();
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
     * Stores the given organisation in the ORGANISATIONS table.
     */
    @Override
    public void add(Organisation organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(!organisation.hasUniqueId())
            throw new IllegalArgumentException("organisation uuid null");

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setInt(1, organisation.getId());
            insertStmt.setTimestamp(2, new Timestamp(organisation.getPublishedDateMillis()), UTC);
            insertStmt.setString(3, organisation.getUuid());
            insertStmt.setString(4, organisation.getCode());
            insertStmt.setString(5, organisation.getTitle());
            insertStmt.setBoolean(6, organisation.isSponsor());
            insertStmt.setString(7, organisation.getTabs().name());
            insertStmt.setBoolean(8, organisation.isPublished());
            insertStmt.setString(9, organisation.getCreatedBy());
            String attributes = organisation.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(10, reader, attributes.length());
            insertStmt.executeUpdate();

            logger.info("Created organisation '"+organisation.getTitle()+"' in ORGANISATIONS"
                +" (id="+organisation.getId()+" uuid="+organisation.getUuid()+" code="+organisation.getCode()+")");
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
     * Updates the given organisation in the ORGANISATIONS table.
     */
    @Override
    public void update(Organisation organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(!organisation.hasUniqueId())
            throw new IllegalArgumentException("organisation uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(organisation.getPublishedDateMillis()), UTC);
            updateStmt.setString(2, organisation.getUuid());
            updateStmt.setString(3, organisation.getCode());
            updateStmt.setString(4, organisation.getTitle());
            updateStmt.setBoolean(5, organisation.isSponsor());
            updateStmt.setString(6, organisation.getTabs().name());
            updateStmt.setBoolean(7, organisation.isPublished());
            String attributes = organisation.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(8, reader, attributes.length());
            updateStmt.setInt(9, organisation.getId());
            updateStmt.executeUpdate();

            logger.info("Updated organisation '"+organisation.getTitle()+"' in ORGANISATIONS"
                +" (id="+organisation.getId()+" uuid="+organisation.getUuid()+" code="+organisation.getCode()+")");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Removes the given organisation from the table.
     */
    @Override
    public void delete(Organisation organisation) throws SQLException
    {
        if(!hasConnection() || organisation == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setInt(1, organisation.getId());
        deleteStmt.executeUpdate();

        logger.info("Deleted organisation '"+organisation.getTitle()+"' in ORGANISATIONS"
            +" (id="+organisation.getId()+" uuid="+organisation.getUuid()+" code="+organisation.getCode()+")");
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
     * Adds or Updates the given organisation in the table.
     */
    public boolean upsert(Organisation organisation) throws SQLException
    {
        return upsert(organisation, false);
    }

    /**
     * Adds or Updates the given organisation in the table.
     */
    @Override
    public boolean upsert(Organisation organisation, boolean keepExternal) throws SQLException
    {
        boolean ret = false;

        Organisation existing = getByUuid(organisation.getUuid());
        if(existing != null)
        {
            update(organisation);
        }
        else if(organisation.getId() > 0) // Get by ID as the URL may have changed
        {
            existing = getById(organisation.getId());
            if(existing != null)
            {
                update(organisation);
            }
            else
            {
                add(organisation);
                ret = true;
            }
        }
        else
        {
            synchronized(table)
            {
                organisation.setId(getMaxId()+1);
                add(organisation);
                ret = true;
            }
        }

        return ret;
    }

    /**
     * Returns <CODE>true</CODE> if all the organisations have been deployed.
     */
    @Override
    public boolean isDeployed(String code) throws SQLException
    {
        return isDeployed();
    }

    /**
     * Returns <CODE>true</CODE> if all the organisations have been deployed.
     */
    public boolean isDeployed() throws SQLException
    {
        boolean ret = true;

        List<Organisation> organisations = list();
        if(organisations.size() > 0)
        {
            for(Organisation organisation : organisations)
            {
                if(!organisation.isDeployed())
                {
                    ret = false;
                    break;
                }
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