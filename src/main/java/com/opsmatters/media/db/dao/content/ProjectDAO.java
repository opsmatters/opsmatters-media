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
import com.opsmatters.media.model.content.ProjectResource;

/**
 * DAO that provides operations on the PROJECTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectDAO extends ContentDAO<ProjectResource>
{
    private static final Logger logger = Logger.getLogger(ProjectDAO.class.getName());

    /**
     * The query to use to insert a project into the PROJECTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PROJECTS"
      + "( CODE, ID, PUBLISHED_DATE, UUID, TITLE, PROVIDER, PUBLISHED, CREATED_BY, ATTRIBUTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a project in the PROJECTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PROJECTS SET PUBLISHED_DATE=?, UUID=?, TITLE=?, PROVIDER=?, PUBLISHED=?, ATTRIBUTES=? "
      + "WHERE CODE=? AND ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public ProjectDAO(ContentDAOFactory factory)
    {
        super(factory, "PROJECTS");
    }

    /**
     * Defines the columns and indices for the PROJECTS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("TITLE", Types.VARCHAR, 128, true);
        table.addColumn("PROVIDER", Types.VARCHAR, 15, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("PROJECTS_PK", new String[] {"CODE","ID"});
        table.addIndex("PROJECTS_UUID_IDX", new String[] {"CODE","UUID"});
        table.setInitialised(true);
    }

    /**
     * Stores the given project in the PROJECTS table.
     */
    public void add(ProjectResource content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("project uuid null");

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
            insertStmt.setString(6, content.getProvider().code());
            insertStmt.setBoolean(7, content.isPublished());
            insertStmt.setString(8, content.getCreatedBy());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(9, reader, attributes.length());
            insertStmt.executeUpdate();

            logger.info("Created project '"+content.getTitle()+"' in PROJECTS"
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

            // Unique constraint violated means that the project already exists
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
     * Updates the given project in the PROJECTS table.
     */
    public void update(ProjectResource content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("project uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(2, content.getUuid());
            updateStmt.setString(3, content.getTitle());
            updateStmt.setString(4, content.getProvider().code());
            updateStmt.setBoolean(5, content.isPublished());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader, attributes.length());
            updateStmt.setString(7, content.getCode());
            updateStmt.setInt(8, content.getId());
            updateStmt.executeUpdate();

            logger.info("Updated project '"+content.getTitle()+"' in PROJECTS"
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
