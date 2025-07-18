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
package com.opsmatters.media.db.dao.content.tool;

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
import com.opsmatters.media.model.system.Site;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.tool.Tool;
import com.opsmatters.media.model.content.tool.ToolItem;
import com.opsmatters.media.util.SessionId;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;

/**
 * DAO that provides operations on the TOOLS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolDAO extends ContentDAO<Tool>
{
    private static final Logger logger = Logger.getLogger(ToolDAO.class.getName());

    /**
     * The query to use to insert a tool into the TOOLS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO TOOLS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, PRICING, PUBLISHED, PROMOTE, "
      +   "STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a tool in the TOOLS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE TOOLS SET UUID=?, PUBLISHED_DATE=?, TITLE=?, PRICING=?, PUBLISHED=?, PROMOTE=?, STATUS=?, CREATED_BY=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select the tool items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, PRICING, PUBLISHED, PROMOTE, STATUS "
      + "FROM TOOLS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * Constructor that takes a DAO factory.
     */
    public ToolDAO(ContentDAOFactory factory)
    {
        super(factory, "TOOLS");
    }

    /**
     * Defines the columns and indices for the TOOLS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("TITLE", Types.VARCHAR, 128, true);
        table.addColumn("PRICING", Types.VARCHAR, 15, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("TOOLS_PK", new String[] {"UUID"});
        table.addIndex("TOOLS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("TOOLS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Stores the given tool in the TOOLS table.
     */
    public synchronized void add(Tool content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("tool uuid null");

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, content.getUuid());
            insertStmt.setString(2, content.getSiteId());
            insertStmt.setString(3, content.getCode());
            insertStmt.setInt(4, content.getId());
            insertStmt.setTimestamp(5, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setString(6, content.getTitle());
            insertStmt.setString(7, content.getPricing());
            insertStmt.setBoolean(8, content.isPublished());
            insertStmt.setBoolean(9, content.isPromoted());
            insertStmt.setString(10, content.getStatus().name());
            insertStmt.setString(11, content.getCreatedBy());
            String attributes = content.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(12, reader, attributes.length());
            insertStmt.setInt(13, SessionId.get());
            insertStmt.executeUpdate();

            logger.info(String.format("Created %s '%s' in %s (GUID=%s)", 
                content.getType().value(), content.getTitle(), getTableName(), content.getGuid()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the tool already exists
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
     * Updates the given tool in the TOOLS table.
     */
    public synchronized void update(Tool content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("tool uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setString(1, content.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(3, content.getTitle());
            updateStmt.setString(4, content.getPricing());
            updateStmt.setBoolean(5, content.isPublished());
            updateStmt.setBoolean(6, content.isPromoted());
            updateStmt.setString(7, content.getStatus().name());
            updateStmt.setString(8, content.getCreatedBy());
            String attributes = content.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(9, reader, attributes.length());
            updateStmt.setString(10, content.getSiteId());
            updateStmt.setString(11, content.getCode());
            updateStmt.setInt(12, content.getId());
            updateStmt.executeUpdate();

            logger.info(String.format("Updated %s '%s' in %s (GUID=%s)", 
                content.getType().value(), content.getTitle(), getTableName(), content.getGuid()));
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Returns the tool items from the table by organisation code.
     */
    public synchronized List<ToolItem> listItems(Site site, String code) throws SQLException
    {
        List<ToolItem> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByCodeStmt == null)
            listByCodeStmt = prepareStatement(getConnection(), LIST_ITEMS_BY_CODE_SQL);
        clearParameters(listByCodeStmt);

        ResultSet rs = null;

        try
        {
            listByCodeStmt.setString(1, site.getId());
            listByCodeStmt.setString(2, code);
            listByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByCodeStmt.executeQuery();
            ret = new ArrayList<ToolItem>();
            while(rs.next())
            {
                ToolItem tool = new ToolItem();
                tool.setUuid(rs.getString(1));
                tool.setSiteId(rs.getString(2));
                tool.setCode(rs.getString(3));
                tool.setId(rs.getInt(4));
                tool.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                tool.setTitle(rs.getString(6));
                tool.setPricing(rs.getString(7));
                tool.setPublished(rs.getBoolean(8));
                tool.setPromoted(rs.getBoolean(9));
                tool.setStatus(rs.getString(10));
                ret.add(tool);
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
     * Close any tools associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
    }

    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByCodeStmt;
}
