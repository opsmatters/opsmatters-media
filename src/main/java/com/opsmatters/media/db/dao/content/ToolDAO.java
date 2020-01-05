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

import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.content.ToolResource;

/**
 * DAO that provides operations on the TOOLS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ToolDAO extends ContentDAO<ToolResource>
{
    private static final Logger logger = Logger.getLogger(ToolDAO.class.getName());

    /**
     * The query to use to insert a tool into the TOOLS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO TOOLS"
      + "( CODE, ID, PUBLISHED_DATE, UUID, TITLE, "
      + "PUBLISHED, CREATED_BY, ATTRIBUTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a tool in the TOOLS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE TOOLS SET PUBLISHED_DATE=?, UUID=?, TITLE=?, "
      + "PUBLISHED=?, ATTRIBUTES=? "
      + "WHERE CODE=? AND ID=?";

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
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("TITLE", Types.VARCHAR, 128, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("TOOLS_PK", new String[] {"CODE","ID"});
        table.addIndex("TOOLS_UUID_IDX", new String[] {"CODE","UUID"});
        table.setInitialised(true);
    }

    /**
     * Stores the given tool in the TOOLS table.
     */
    public void add(ToolResource content) throws SQLException
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
            insertStmt.setString(1, content.getCode());
            insertStmt.setInt(2, content.getId());
            insertStmt.setTimestamp(3, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setString(4, content.getUuid());
            insertStmt.setString(5, content.getTitle());
            insertStmt.setBoolean(6, content.isPublished());
            insertStmt.setString(7, content.getCreatedBy());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(8, reader, attributes.length());
            insertStmt.executeUpdate();

            logger.info("Created tool '"+content.getTitle()+"' in TOOLS"
                +" (id="+content.getId()+" uuid="+content.getUuid()+")");
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
    public void update(ToolResource content) throws SQLException
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
            updateStmt.setTimestamp(1, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(2, content.getUuid());
            updateStmt.setString(3, content.getTitle());
            updateStmt.setBoolean(4, content.isPublished());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(5, reader, attributes.length());
            updateStmt.setString(6, content.getCode());
            updateStmt.setInt(7, content.getId());
            updateStmt.executeUpdate();

            logger.info("Updated tool '"+content.getTitle()+"' in TOOLS"
                +" (id="+content.getId()+" uuid="+content.getUuid()+")");
        }
        finally
        {
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
    }

    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
}
