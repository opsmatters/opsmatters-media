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
package com.opsmatters.media.db.dao.content.publication;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.content.ContentStatus;
import com.opsmatters.media.model.content.ContentLookup;
import com.opsmatters.media.model.content.publication.Publication;
import com.opsmatters.media.model.content.publication.PublicationItem;
import com.opsmatters.media.db.dao.content.ContentDAO;
import com.opsmatters.media.db.dao.content.ContentDAOFactory;
import com.opsmatters.media.util.SessionId;

/**
 * DAO that provides operations on the PUBLICATIONS table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class PublicationDAO extends ContentDAO<Publication>
{
    private static final Logger logger = Logger.getLogger(PublicationDAO.class.getName());

    /**
     * The query to use to select an publication from the PUBLICATIONS table by URL.
     */
    private static final String GET_BY_URL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM PUBLICATIONS WHERE SITE_ID=? AND CODE=? AND URL=? ";

    /**
     * The query to use to insert a publication into the PUBLICATIONS table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO PUBLICATIONS"
      + "( UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, PUBLICATION_TYPE, PUBLISHED, PROMOTE, "
      +   "STATUS, CREATED_BY, ATTRIBUTES, SESSION_ID )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a publication in the PUBLICATIONS table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE PUBLICATIONS SET UUID=?, PUBLISHED_DATE=?, TITLE=?, URL=?, PUBLICATION_TYPE=?, PUBLISHED=?, PROMOTE=?, STATUS=?, CREATED_BY=?, ATTRIBUTES=? "
      + "WHERE SITE_ID=? AND CODE=? AND ID=?";

    /**
     * The query to use to select a list of publications from the PUBLICATIONS table by URL.
     */
    private static final String LIST_BY_URL_SQL =  
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, ATTRIBUTES, STATUS, CREATED_BY "
      + "FROM PUBLICATIONS WHERE CODE=? AND URL=?";

    /**
     * The query to use to select the publication items from the table by organisation code.
     */
    private static final String LIST_ITEMS_BY_CODE_SQL =
      "SELECT UUID, SITE_ID, CODE, ID, PUBLISHED_DATE, TITLE, URL, PUBLICATION_TYPE, PUBLISHED, PROMOTE, STATUS "
      + "FROM PUBLICATIONS WHERE SITE_ID=? AND CODE=? ORDER BY ID";

    /**
     * Constructor that takes a DAO factory.
     */
    public PublicationDAO(ContentDAOFactory factory)
    {
        super(factory, "PUBLICATIONS");
    }

    /**
     * Defines the columns and indices for the PUBLICATIONS table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("UUID", Types.VARCHAR, 36, true);
        table.addColumn("SITE_ID", Types.VARCHAR, 5, true);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("ID", Types.INTEGER, true);
        table.addColumn("PUBLISHED_DATE", Types.TIMESTAMP, true);
        table.addColumn("TITLE", Types.VARCHAR, 256, true);
        table.addColumn("URL", Types.VARCHAR, 256, true);
        table.addColumn("PUBLICATION_TYPE", Types.VARCHAR, 30, true);
        table.addColumn("PUBLISHED", Types.BOOLEAN, true);
        table.addColumn("PROMOTE", Types.BOOLEAN, true);
        table.addColumn("STATUS", Types.VARCHAR, 15, true);
        table.addColumn("CREATED_BY", Types.VARCHAR, 15, true);
        table.addColumn("ATTRIBUTES", Types.LONGVARCHAR, true);
        table.addColumn("SESSION_ID", Types.INTEGER, true);
        table.setPrimaryKey("PUBLICATIONS_PK", new String[] {"UUID"});
        table.addIndex("PUBLICATIONS_ID_IDX", new String[] {"SITE_ID","CODE","ID"});
        table.addIndex("PUBLICATIONS_TITLE_IDX", new String[] {"SITE_ID","CODE","TITLE"});
        table.addIndex("PUBLICATIONS_URL_IDX", new String[] {"SITE_ID","CODE","URL"});
        table.addIndex("PUBLICATIONS_STATUS_IDX", new String[] {"STATUS"});
        table.addIndex("PUBLICATIONS_SESSION_IDX", new String[] {"SESSION_ID"});
        table.setInitialised(true);
    }

    /**
     * Returns an publication from the PUBLICATIONS table by URL.
     */
    public synchronized Publication getByUrl(String siteId, String code, String url) throws SQLException
    {
        Publication ret = null;

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
                Publication content = new Publication();
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
     * Returns a list of publications from the PUBLICATIONS table by URL.
     */
    public synchronized List<Publication> listByUrl(String code, String url) throws SQLException
    {
        List<Publication> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listByUrlStmt == null)
            listByUrlStmt = prepareStatement(getConnection(), LIST_BY_URL_SQL);
        clearParameters(listByUrlStmt);

        ResultSet rs = null;

        try
        {
            listByUrlStmt.setString(1, code);
            listByUrlStmt.setString(2, url);
            listByUrlStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listByUrlStmt.executeQuery();
            ret = new ArrayList<Publication>();
            while(rs.next())
            {
                Publication content = new Publication();
                content.setUuid(rs.getString(1));
                content.setSiteId(rs.getString(2));
                content.setCode(rs.getString(3));
                content.setId(rs.getInt(4));
                content.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                content.setAttributes(new JSONObject(getClob(rs, 6)));
                content.setStatus(rs.getString(7));
                content.setCreatedBy(rs.getString(8));
                ret.add(content);
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
     * Stores the given publication in the PUBLICATIONS table.
     */
    public synchronized void add(Publication content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("publication uuid null");

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
            insertStmt.setString(8, content.getPublicationType());
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

            // Unique constraint violated means that the publication already exists
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
     * Updates the given publication in the PUBLICATIONS table.
     */
    public synchronized void update(Publication content) throws SQLException
    {
        if(!hasConnection() || content == null)
            return;

        if(!content.hasUniqueId())
            throw new IllegalArgumentException("publication uuid null");

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
            updateStmt.setString(5, content.getPublicationType());
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
     * Returns a class to look up an organisation's content by title or id.
     */
    public ContentLookup<Publication> newLookup()
    {
        return new ContentLookup<Publication>()
        {
            @Override
            protected Publication getByTitle(String siteId, String code, String title)
                throws SQLException
            {
                return PublicationDAO.this.getByTitle(siteId, code, title);
            }

            @Override
            protected Publication getById(String siteId, String code, String id)
                throws SQLException
            {
                return PublicationDAO.this.getByUrl(siteId, code, id);
            }
        };
    }

    /**
     * Returns the publication items from the table by organisation code.
     */
    public synchronized List<PublicationItem> listItems(Site site, String code) throws SQLException
    {
        List<PublicationItem> ret = null;

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
            ret = new ArrayList<PublicationItem>();
            while(rs.next())
            {
                PublicationItem publication = new PublicationItem();
                publication.setUuid(rs.getString(1));
                publication.setSiteId(rs.getString(2));
                publication.setCode(rs.getString(3));
                publication.setId(rs.getInt(4));
                publication.setPublishedDateMillis(rs.getTimestamp(5, UTC).getTime());
                publication.setTitle(rs.getString(6));
                publication.setUrl(rs.getString(7));
                publication.setPublicationType(rs.getString(8));
                publication.setPublished(rs.getBoolean(9));
                publication.setPromoted(rs.getBoolean(10));
                publication.setStatus(rs.getString(11));
                ret.add(publication);
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
        closeStatement(listByUrlStmt);
        listByUrlStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listByCodeStmt);
        listByCodeStmt = null;
    }

    private PreparedStatement getByUrlStmt;
    private PreparedStatement listByUrlStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listByCodeStmt;
}
