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
package com.opsmatters.media.db.dao.content.project;

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
import com.opsmatters.media.model.content.project.Project;
import com.opsmatters.media.model.content.project.ProjectItem;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the PROJECTS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ProjectDAO extends ContentDAO<Project>
{
    private static final Logger logger = Logger.getLogger(ProjectDAO.class.getName());

    /**
     * The query to use to select a project from the PROJECTS table by URL.
     */
    private static final String GET_BY_URL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM PROJECTS WHERE SITE_ID=? AND CODE=? AND URL=?";

    /**
     * The query to use to insert a project into the PROJECTS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PROJECTS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, LICENSE, PUBLISHED, PROMOTE, "
      +   "STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a project in the PROJECTS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PROJECTS SET UUID=?, PUBLISHED_DATE=?, TITLE=?, URL=?, LICENSE=?, PUBLISHED=?, PROMOTE=?, STATUS=?, CREATED_BY=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select the project items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, LICENSE, PUBLISHED, PROMOTE, STATUS "
      + "FROM PROJECTS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

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
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("TITLE", Types.VARCHAR, 128, true);
        table.addColumn("URL", Types.VARCHAR, 256, true);
        table.addColumn("LICENSE", Types.VARCHAR, 10, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("PROJECTS_PK", new String[] {"UUID"});
        table.addIndex("PROJECTS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("PROJECTS_TITLE_IDX", new String[] {"SITE_ID","CODE","TITLE"});
        table.addIndex("PROJECTS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a project from the PROJECTS table by URL.
     */
    public synchronized Project getByUrl(String siteId, String code, String url) throws SQLException
    {
        Project ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUrlStmt == null)
            getByUrlStmt = prepareStatement(getConnection(), GET_BY_URL_SQL);
        clearParameters(getByUrlStmt);

        ResultSet rs = null;

        try
        {
            getByUrlStmt.setString(1, siteId);
            getByUrlStmt.setString(2, code);
            getByUrlStmt.setString(3, url);
            getByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUrlStmt.executeQuery();
            while(rs.next())
            {
                Project content = new Project();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setAttributes(new JSONObject(getClob(rs, 6)));
                content.setStatus(rs.getString(7));
                content.setCreatedBy(rs.getString(8));
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
     * Stores the given project in the PROJECTS table.
     */
    public synchronized void add(Project content) throws SQLException
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
            insertStmt.setString(1, content.getUuid());
            insertStmt.setString(2, content.getSiteId());
            insertStmt.setString(3, content.getCode());
            insertStmt.setInt(4, content.getId());
            insertStmt.setTimestamp(5, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setString(6, content.getTitle());
            insertStmt.setString(7, content.getUrl());
            insertStmt.setString(8, content.getLicense());
            insertStmt.setBoolean(9, content.isPublished());
            insertStmt.setBoolean(10, content.isPromoted());
            insertStmt.setString(11, content.getStatus().name());
            insertStmt.setString(12, content.getCreatedBy());
            String attributes = content.getAttributes().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(13, reader, attributes.length());
            insertStmt.setInt(14, SessionId.get());
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
    public synchronized void update(Project content) throws SQLException
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
            updateStmt.setString(1, content.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(3, content.getTitle());
            updateStmt.setString(4, content.getUrl());
            updateStmt.setString(5, content.getLicense());
            updateStmt.setBoolean(6, content.isPublished());
            updateStmt.setBoolean(7, content.isPromoted());
            updateStmt.setString(8, content.getStatus().name());
            updateStmt.setString(9, content.getCreatedBy());
            String attributes = content.getAttributes().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(10, reader, attributes.length());
            updateStmt.setString(11, content.getSiteId());
            updateStmt.setString(12, content.getCode());
            updateStmt.setInt(13, content.getId());
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
     * Returns the project items from the table by organisation code.
     */
    public synchronized List<ProjectItem> listItems(Site site, String code) throws SQLException
    {
        List<ProjectItem> ret = null;

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
            ret = new ArrayList<ProjectItem>();
            while(rs.next())
            {
                ProjectItem project = new ProjectItem();
                project.setUuid(rs.getString(1));
                project.setSiteId(rs.getString(2));
                project.setCode(rs.getString(3));
                project.setId(rs.getInt(4));
                project.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                project.setTitle(rs.getString(6));
                project.setUrl(rs.getString(7));
                project.setLicense(rs.getString(8));
                project.setPublished(rs.getBoolean(9));
                project.setPromoted(rs.getBoolean(10));
                project.setStatus(rs.getString(11));
                ret.add(project);
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
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByUrlStmt);
        getByUrlStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
    }

    private PreparedStatement getByUrlStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByCodeStmt;
}
