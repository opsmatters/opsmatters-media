/*
 * Copyright 2020 Gerald Curley
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
package com.opsmatters.media.db.dao.content.job;

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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.job.Job;
import com.opsmatters.media.model.content.job.JobItem;
import com.opsmatters.media.util.SessionId;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;

/**
 * DAO that provides operations on the JOBS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class JobDAO extends ContentDAO<Job>
{
    private static final Logger logger = Logger.getLogger(JobDAO.class.getName());

    /**
     * The query to use to insert a job into the JOBS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO JOBS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, PACKAGE, LOCATION, PUBLISHED, PROMOTE, "
      +   "STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a job in the JOBS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE JOBS SET UUID=?, PUBLISHED_DATE=?, TITLE=?, PACKAGE=?, LOCATION=?, PUBLISHED=?, PROMOTE=?, STATUS=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select the job items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, PACKAGE, LOCATION, PUBLISHED, PROMOTE, STATUS "
      + "FROM JOBS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * Constructor that takes a DAO factory.
     */
    public JobDAO(ContentDAOFactory factory)
    {
        super(factory, "JOBS");
    }

    /**
     * Defines the columns and indices for the JOBS table.
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
        table.addColumn("PACKAGE", Types.VARCHAR, 30, true);
        table.addColumn("LOCATION", Types.VARCHAR, 30, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("JOBS_PK", new String[] {"UUID"});
        table.addIndex("JOBS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("JOBS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Stores the given job in the JOBS table.
     */
    public synchronized void add(Job content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("job uuid null");

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
            insertStmt.setString(7, content.getPackage());
            insertStmt.setString(8, content.getLocation());
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

            // Unique constraint violated means that the job already exists
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
     * Updates the given job in the JOBS table.
     */
    public synchronized void update(Job content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("job uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setString(1, content.getUuid());
            updateStmt.setTimestamp(2, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(3, content.getTitle());
            updateStmt.setString(4, content.getPackage());
            updateStmt.setString(5, content.getLocation());
            updateStmt.setBoolean(6, content.isPublished());
            updateStmt.setBoolean(7, content.isPromoted());
            updateStmt.setString(8, content.getStatus().name());
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
     * Returns the job items from the table by organisation code.
     */
    public synchronized List<JobItem> listItems(Site site, String code) throws SQLException
    {
        List<JobItem> ret = null;

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
            ret = new ArrayList<JobItem>();
            while(rs.next())
            {
                JobItem job = new JobItem();
                job.setUuid(rs.getString(1));
                job.setSiteId(rs.getString(2));
                job.setCode(rs.getString(3));
                job.setId(rs.getInt(4));
                job.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                job.setTitle(rs.getString(6));
                job.setPackage(rs.getString(7));
                job.setLocation(rs.getString(8));
                job.setPublished(rs.getBoolean(9));
                job.setPromoted(rs.getBoolean(10));
                job.setStatus(rs.getString(11));
                ret.add(job);
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
