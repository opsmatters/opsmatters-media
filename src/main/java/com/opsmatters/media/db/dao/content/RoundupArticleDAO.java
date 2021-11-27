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
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.RoundupArticle;
import com.opsmatters.media.model.content.ContentStatus;

/**
 * DAO that provides operations on the ROUNDUPS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RoundupArticleDAO extends ContentDAO<RoundupArticle>
{
    private static final Logger logger = Logger.getLogger(RoundupArticleDAO.class.getName());

    /**
     * The query to use to select a roundup from the ROUNDUPS table by URL.
     */
    private static final String GET_BY_URL_SQL =  
      "SELECT ATTRIBUTES, SITE_ID FROM ROUNDUPS WHERE CODE=? AND URL=? AND (?=0 OR ABS(TIMESTAMPDIFF(DAY, ?, PUBLISHED_DATE)) <= 7)";

    /**
     * The query to use to insert a roundup into the ROUNDUPS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO ROUNDUPS"
      + "( SITE_ID, CODE, ID, PUBLISHED_DATE, UUID, URL, PUBLISHED, STATUS, CREATED_BY, ATTRIBUTES )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a roundup in the ROUNDUPS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE ROUNDUPS SET PUBLISHED_DATE=?, UUID=?, URL=?, PUBLISHED=?, STATUS=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public RoundupArticleDAO(ContentDAOFactory factory)
    {
        super(factory, "ROUNDUPS");
    }

    /**
     * Defines the columns and indices for the ROUNDUPS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("URL", Types.VARCHAR, 256, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.setPrimaryKey("ROUNDUPS_PK", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("ROUNDUPS_UUID_IDX", new String[] {"SITE_ID","CODE","UUID"});
        table.addIndex("ROUNDUPS_URL_IDX", new String[] {"SITE_ID","CODE","URL"});
        table.addIndex("ROUNDUPS_STATUS_IDX", new String[] {"STATUS"});
        table.setInitialised(true);
    }

    /**
     * Returns a roundup from the ROUNDUPS table by URL.
     */
    public synchronized List<RoundupArticle> getByUrl(String code, String url, long publishedDate) throws SQLException
    {
        List<RoundupArticle> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByUrlStmt == null)
            getByUrlStmt = prepareStatement(getConnection(), GET_BY_URL_SQL);
        clearParameters(getByUrlStmt);

        ResultSet rs = null;

        try
        {
            getByUrlStmt.setString(1, code);
            getByUrlStmt.setString(2, url);
            getByUrlStmt.setLong(3, publishedDate);
            getByUrlStmt.setTimestamp(4, new Timestamp(publishedDate), UTC);
            getByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByUrlStmt.executeQuery();
            ret = new ArrayList<RoundupArticle>();
            while(rs.next())
            {
                JSONObject attributes = new JSONObject(getClob(rs, 1));
                RoundupArticle item = new RoundupArticle(attributes);
                item.setSiteId(rs.getString(2));
                ret.add(item);
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
     * Stores the given roundup in the ROUNDUPS table.
     */
    public synchronized void add(RoundupArticle content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("roundup uuid null");

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        StringReader reader = null;

        try
        {
            insertStmt.setString(1, content.getSiteId());
            insertStmt.setString(2, content.getCode());
            insertStmt.setInt(3, content.getId());
            insertStmt.setTimestamp(4, new Timestamp(content.getPublishedDateMillis()), UTC);
            insertStmt.setString(5, content.getUuid());
            insertStmt.setString(6, content.getUrl());
            insertStmt.setBoolean(7, content.isPublished());
            insertStmt.setString(8, content.getStatus().name());
            insertStmt.setString(9, content.getCreatedBy());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            insertStmt.setCharacterStream(10, reader, attributes.length());
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

            // Unique constraint violated means that the roundup already exists
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
     * Updates the given roundup in the ROUNDUPS table.
     */
    public synchronized void update(RoundupArticle content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("roundup uuid null");

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        StringReader reader = null;

        try
        {
            updateStmt.setTimestamp(1, new Timestamp(content.getPublishedDateMillis()), UTC);
            updateStmt.setString(2, content.getUuid());
            updateStmt.setString(3, content.getUrl());
            updateStmt.setBoolean(4, content.isPublished());
            updateStmt.setString(5, content.getStatus().name());
            String attributes = content.toJson().toString();
            reader = new StringReader(attributes);
            updateStmt.setCharacterStream(6, reader, attributes.length());
            updateStmt.setString(7, content.getSiteId());
            updateStmt.setString(8, content.getCode());
            updateStmt.setInt(9, content.getId());
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
    }

    private PreparedStatement getByUrlStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
}
